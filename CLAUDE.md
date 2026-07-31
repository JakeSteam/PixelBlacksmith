# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Pixel Blacksmith — a released Android crafting game (`uk.co.jakelee.blacksmith`), Java, no Kotlin. Two Gradle modules: `:Blacksmith` (the app) and `:BaseGameUtils` (Google's vendored Play Games sample helper).

Codebase notes from the README, which still hold: most logic lives in each screen's Activity, with shared behaviour bolted on via `helper/*Helper` static classes. `DatabaseHelper`, `DisplayHelper`, `VisitorHelper`, and `WorkerHelper` are the large, tangled ones.

## Commands

```sh
./gradlew :Blacksmith:assembleDebug        # build debug APK
./gradlew :Blacksmith:assembleRelease      # build release APK
./gradlew :Blacksmith:installDebug         # build + install on connected device
./gradlew :Blacksmith:lintDebug            # Android lint
./gradlew clean
```

There are no tests — no `src/test`, no `src/androidTest`, no test dependencies. Don't invent a test command; verify changes by building and running the app.

Toolchain: AGP 8.11.2 / Gradle 8.13 / JDK 17+, `compileSdk` 36, `targetSdk` 36 (via `apiVersion` in the root `build.gradle`), `minSdk` 21. API 36 needs AGP 8.11 or newer — AGP 8.9 and below cap out at 35.

### Verifying on an emulator

Two things will waste time otherwise:

- **`uiautomator dump` never works on `MainActivity`.** Its every-second refresh handler means the UI never reaches idle, so the dump fails with "could not get idle state". It works fine on the static popup screens. For `MainActivity`, tap known coordinates derived from a screenshot instead.
- **Non-exported activities can't be launched with `am start`** on a `user` build (`SecurityException`). Navigate through the UI, or read state out of the database.

The DB and SharedPreferences are readable on a debug build via `run-as`, which is by far the most reliable way to assert on game state:

```sh
adb shell run-as uk.co.jakelee.blacksmith cat databases/blacksmith.db > /tmp/x.db
sqlite3 /tmp/x.db "SELECT id, message FROM MESSAGE ORDER BY ID DESC LIMIT 5;"
```

Table names are SugarORM-mangled (`PLAYERINFO`, not `PLAYER_INFO`) and booleans live in `BOOL_VALUE`, ints in `INT_VALUE`. The `MESSAGE` table is especially useful: any toast raised with `saveToLog = true` lands there, so you can assert which user-facing message fired without racing a Snackbar's fade.

Note the Play Games sign-in dialog hijacks the foreground on launch when no Google account is present; dismiss it with a single `KEYCODE_BACK` (more than that exits the app).

## Architecture

### Persistence: SugarORM, seeded and migrated in code

All game data — items, recipes, visitors, traders, upgrades, *and* the player's save — lives in one SQLite DB (`blacksmith.db`) via the long-dead SugarORM. Configuration is in `AndroidManifest.xml` `<meta-data>` (`DATABASE`, `VERSION`, `DOMAIN_PACKAGE_NAME`), and the application class is `com.orm.SugarApp`.

Every class in `model/` extends `SugarRecord`. Field names map to snake_case columns; `Select.from(X.class).where(Condition.prop("some_field").eq(v))` is the query idiom, with raw `X.executeQuery("UPDATE ...")` used freely in migrations.

**`helper/DatabaseHelper.java` (3.3k lines) is the schema, the seed data, and the migration chain.** It's an `AsyncTask` run by `SplashScreenActivity`. It reads a `databaseVersion` int from SharedPreferences (key `uk.co.jakelee.blacksmith`) and walks a chain of `if (version <= DB_V…) { patchAtoB(); bump version; }` blocks up to `DB_LATEST`. `createX()` methods seed a fresh install; `patchAtoB()` methods migrate existing saves.

Consequences to respect when changing game content:

- Adding or changing an item/recipe/visitor/trader means editing **both** the relevant `createX()` (fresh installs) **and** a new `patchAtoB()` (existing players), then adding the `DB_V…` constant and moving `DB_LATEST`.
- Model IDs are hardcoded numbers with real meaning — item 52 is coins, item 201 is The Collection. `helper/Constants.java` is the central registry of these magic IDs (`LOCATION_*`, `STATE_*`, `ITEM_*`, `SETTING_*`, `ERROR_*`, Play Games leaderboard/event IDs). Look there before hardcoding a number.
- Patch methods are written defensively (null-check the row, then mutate) because they run against arbitrary old saves.
- `Upgrade` rows are looked up by their English `name` string (`Upgrade.getValue("Minimum Visitor Rewards")`), not by ID — renaming an upgrade silently breaks every caller.

### Localisation: strings resolved by name at runtime

Model display text is *not* the `name`/`description` column — those are vestigial. `Item.getName(context)` calls `TextHelper.getInstance(ctx).getText("item_name_" + id)`, which does `resources.getIdentifier(identifier, "string", packageName)`. Same for `item_desc_<id>`, `state_<id>`, `language_<id>`, and friends.

Drawables work the same way: `DisplayHelper.getItemDrawableID()` resolves `"item" + id`, and there are `visitor<n>`, `character<n>`, `adventure<n>`, `assistant<n>_<tier>` families across ~460 drawables.

So adding content means adding matching `strings.xml` entries and a correspondingly-named drawable, or it silently renders the identifier string / a missing image. Translations live in `values-{de,es,fr,ja,ko,nl,pt,ru,zh}`; `LanguageHelper` swaps locale in-process via `Resources.updateConfiguration`.

### UI: programmatic view construction

Activities are plain `Activity` (only `MainActivity` and `SplashScreenActivity` are `AppCompatActivity`); there is no shared base activity, so lifecycle boilerplate is copy-pasted. Only 40 layout XMLs exist for 31 screens because `DisplayHelper` (869 lines) builds most views in Java — `createItemImage`, `createTextView`, `createAllSlots`, `populateVisitorsContainer`, etc. Custom controls in `controls/` (notably `TextViewPixel`, which applies `assets/fonts/BetterPixels.ttf`).

`MainActivity` drives the game loop with `Handler`-posted every-second / every-ten-second / every-minute runnables, and coordinates cross-activity redraws through static flags (`MainActivity.needToRedrawVisitors`, `needToRedrawSlots`) plus `MainActivity.vh`, a `VariableHelper` holding transient "is the furnace busy" state.

### Crafting flow

`Location` (anvil, furnace, table, market, selling, enchanting) → `Slot` (unlocked by player level and premium status) → `Pending_Inventory` (an item in progress, with `timeCreated` + `craftTime`) → `Inventory` on completion. `Slot.hasAvailableSlot()` gates starting a craft; the polling runnables in `MainActivity` sweep completed `Pending_Inventory` rows into `Inventory`.

### Billing

`helper/BillingHelper.java` wraps Google Play Billing 8.0.0 (one-time products only — the game sells no subscriptions). `PremiumActivity` and `BuyCoinsActivity` implement `BillingHelper.Listener`.

`premium` is a non-consumable and gets **acknowledged**; `contribute` and `coin_pack_1/2/3` are consumables and get **consumed**. Purchases are granted before being settled, so an interrupted grant is recovered on the next launch — consumables replay through `onProductPurchased`, non-consumables deliberately don't, because `addPremiumFeatures()` stacks its bonuses and isn't idempotent.

Billing can't be tested locally: `launchBillingFlow` needs an upload-key-signed build on a Play track. On a debug build the product query returns nothing, `isReady()` is false, and the buy buttons fall back to the `cannotBuyIAP` message — that's the expected local behaviour, not a bug.

### Error handling convention

Game-action methods return an `int` status: a positive count on success, or a negative `Constants.ERROR_*` code. Callers map it through `ErrorHelper.errors` (a `HashMap<Integer, Integer>` of error code → string resource) and surface it with `ToastHelper.showErrorToast(view, length, text, saveToLog)`. "Toasts" are actually Snackbars, optionally persisted to the in-game message log (`Message.add`). Prefer this over throwing.

## Gotchas

- **Don't bump `supportLibraryVersion` (23.0.0) in the root `build.gradle`.** The existing comment there is accurate: it breaks alert sizes, tutorial skipping, and more. AndroidX + Jetifier are enabled and rewrite it at build time.
- **`android.nonFinalResIds=false` in `gradle.properties` is load-bearing** — `SettingsActivity` switches on view IDs.
- **TourGuide comes from JitPack**, as `com.github.worker8:TourGuide:v1.0.17-SNAPSHOT@aar`. The coordinate is case-sensitive and the version is tag-shaped; `v1.0.14` is *not* a substitute, as it predates the `ChainTourGuide` class `TutorialHelper` uses. Only jcenter ever served the old `tourguide` spelling.
- **`ToastHelper` silently discards anything passed a null view** — it returns before `Message.add`, so there's no toast *and* no log entry. Roughly 25 call sites still do this, so some player-facing messages never appear. Pass a real view (`findViewById(android.R.id.content)` works) for anything new.
- Release builds have `minifyEnabled false`, but `Blacksmith/proguard.pro` matters anyway — it keeps `com.orm.**` and `uk.co.jakelee.blacksmith.model.**`, which SugarORM reflects over. Don't rename or obfuscate model classes/fields without also updating migration code.
- **Play Games Services v1 is on a shutdown clock.** `GooglePlayHelper` and `BaseGameUtils` use the deprecated `GoogleApiClient` + `Games.*` static API. Play blocks *new* titles on v1 as of Sept 2025, the v1 APIs were removed from the SDK in `play-services-games:25.0.0` (June 2026 — so **do not bump `playLibraryVersion` off 17.0.0**), and v1 calls stop working at runtime in 2027. Migrating to `play-services-games-v2` is the only route to restoring cloud saves, and drops the `play-services-drive` dependency, which is the only reason it's still here.
- Cloud saves are disabled: `Drive.API` is commented out in `MainActivity`'s client builder because the Drive Android API shut down in 2019. The serialisation (`GooglePlayHelper.createBackup`/`applyBackup`) is intact and shared with local export/import, so only the transport needs rebuilding. Play Services are optional at runtime — guard against a null/disconnected client.
- **Adverts are gone.** The AppLovin account is disabled and the SDK was removed. `AdvertHelper` remains and still grants the same rewards, but goes straight to `InterstitialActivity` — a homegrown tips-and-countdown screen, not an ad SDK. Strings still say "advert" / "WATCH AD", which is now inaccurate.
- Bump `versionCode` and `versionName` in `Blacksmith/build.gradle` for releases. `MainActivity` compares `BuildConfig.VERSION_CODE` against the `SavedVersion` `Player_Info` row and shows an update message only when `VERSION_NAME` ends in `.0`.
