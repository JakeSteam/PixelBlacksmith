# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Pixel Blacksmith — a released Android crafting game (`uk.co.jakelee.blacksmith`), Java, no Kotlin. One Gradle module, `:Blacksmith`.

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

Toolchain: AGP 9.3.1 / Gradle 9.5.0 / JDK 17+, `compileSdk` 37, `targetSdk` 37 (via `apiVersion` in the root `build.gradle`), `minSdk` 23. API 37 needs AGP 9.3 or newer, which in turn requires Gradle 9.5.0.

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

Emulator gotchas that produce convincing false alarms:

- **A device with no Google account** makes GMS throw up an add-account flow over the game on launch. Nothing to do with the app.
- **A debug-signed build fails Play Games with `DEVELOPER_ERROR`** ("package name and certificate fingerprint do not match"), which manifests as a sign-in screen on every launch and `isConnected()` never becoming true. See the signing note below — builds here are signed with the shipping certificate precisely so this doesn't happen.
- The shell is **zsh**, which does *not* word-split unquoted variables, so `D="-s emulator-5556"; adb $D shell` fails with "-s requires an argument". Write the flags out.

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

### Signing

Both build types are signed with the shipping certificate, from `PB_STORE_FILE`, `PB_STORE_PASSWORD`, `PB_KEY_ALIAS` and `PB_KEY_PASSWORD` in **`~/.gradle/gradle.properties`** — outside the repo, since this one is public. If those properties are missing the build still works (debug falls back to the local debug keystore, release comes out unsigned), so a clone builds fine.

Debug is signed with the shipping certificate deliberately: Play Games and Play Billing both authenticate on package name *plus* certificate, so a debug-signed build can't exercise either. The trade-off is that a local debug build will install over a production one.

There is no Play App Signing on this app, so that keystore is the app's only identity — lose it and the app can never be updated again, and since the save is a local database, players couldn't be migrated.

### Billing

`helper/BillingHelper.java` wraps Google Play Billing 9.1.0 (one-time products only — the game sells no subscriptions). `PremiumActivity` and `BuyCoinsActivity` implement `BillingHelper.Listener`.

`premium` is a non-consumable and gets **acknowledged**; `contribute` and `coin_pack_1/2/3` are consumables and get **consumed**. Purchases are granted before being settled, so an interrupted grant is recovered on the next launch — consumables replay through `onProductPurchased`, non-consumables deliberately don't, because `addPremiumFeatures()` stacks its bonuses and isn't idempotent.

With a shipping-signed build on a device with a Play account, products resolve and `launchBillingFlow` does hand off to Play's purchase sheet. Completing a purchase still needs the app on a Play track with a licence tester, so that last step can't be verified locally.

Billing 9's transitive `lifecycle 2.6.2` drags in `kotlin-stdlib-jdk8:1.6.21`, whose classes moved into `kotlin-stdlib` in Kotlin 1.8 — hence the `constraints` block aligning them, without which `checkDuplicateClasses` fails.

**Play's callbacks arrive on a background thread**, which caught this code out twice. `productDetails` is a `ConcurrentHashMap` for that reason — as a plain `HashMap` it was published unsafely, and the main thread saw an arbitrary subset of the entries, which showed up as one real coin-pack price next to two hardcoded fallbacks. The `Listener` methods all touch views, so `BillingHelper` bounces them onto the main thread itself; don't call `listener.*` directly. The two `Log.w` diagnostics (setup failure, unfetched products) are the only signal that billing is broken, since both failure modes otherwise look identical to Play merely being slow.

### Play Games

Services **v2** (`play-services-games-v2`), via `helper/GooglePlayHelper.java`. There is no `GoogleApiClient`, no connect/disconnect, and **no sign-out** — authentication is automatic and `GooglePlayHelper` just records whether it succeeded.

Every v2 client needs an `Activity`. Fire-and-forget writes (`UpdateEvent`, `UpdateLeaderboards`, `UnlockAchievement`, `UpdateAchievements`) are called from places holding only a `Context`, so `MainActivity` registers itself via `initialise()` and those borrow it. UI getters are asynchronous — `Task<Intent>`, launched from a success listener.

Cloud saves work through `SnapshotsClient`, sharing `createBackup`/`applyBackup` with local export/import. **`applyBackup` deletes and repopulates fourteen tables**, so `isPlausibleSave` gates it: the save *is* the database, and a truncated one previously destroyed a game partway through. Keep that guard.

GMS logs "You are using the deprecated Play Games Services v1 SDK" from `SnapshotsClient`. That's noise on Google's side; the dependency graph contains only games-v2.

### Error handling convention

Game-action methods return an `int` status: a positive count on success, or a negative `Constants.ERROR_*` code. Callers map it through `ErrorHelper.errors` (a `HashMap<Integer, Integer>` of error code → string resource) and surface it with `ToastHelper.showErrorToast(view, length, text, saveToLog)`. "Toasts" are actually Snackbars, optionally persisted to the in-game message log (`Message.add`). Prefer this over throwing.

## Gotchas

- **Don't bump `supportLibraryVersion` (23.0.0) in the root `build.gradle`.** The existing comment there is accurate: it breaks alert sizes, tutorial skipping, and more. AndroidX + Jetifier are enabled and rewrite it at build time.
- **Jetifier is the next thing to break, in AGP 10.** `android.enableJetifier=true` is what maps the support libraries onto androidx, and AGP 9 already warns it's going. When it goes, the three `com.android.support:23.0.0` dependencies must be replaced with real androidx artifacts — which is exactly the change the comment above warns about. The androidx surface actually used is small (7 packages, 19 imports, zero `android.support.*` imports), so it's tractable, but expect dialog and tutorial theming to need checking.
- **A `constraints` block pins `androidx.vectordrawable`(`-animated`) to 1.1.0.** Jetifier maps to androidx 1.0.0, where both artifacts declare the namespace `androidx.vectordrawable`; AGP 9 rejects duplicate namespaces outright. 1.1.0 is the first release that separates them.
- **Resource IDs are non-final**, so they can't be `switch` case labels — `SettingsActivity.toggleSetting` is an if/else chain for this reason. `android.nonFinalResIds=false` used to paper over it and has been removed.
- **TourGuide comes from JitPack**, as `com.github.worker8:TourGuide:v1.0.17-SNAPSHOT@aar`. The coordinate is case-sensitive and the version is tag-shaped; `v1.0.14` is *not* a substitute, as it predates the `ChainTourGuide` class `TutorialHelper` uses. Only jcenter ever served the old `tourguide` spelling.
- **`ToastHelper` needs a view to show anything.** It logs to `Message` first now, so nothing is silently lost, but with a null view there's no visible toast. Pass `findViewById(android.R.id.content)`. Three call sites in `DisplayHelper` deliberately still pass null — they're out-of-memory and image-load failures reached from `populateSlots` on a background thread, where showing a Snackbar would be unsafe.
- Release builds have `minifyEnabled false`, but `Blacksmith/proguard.pro` matters anyway — it keeps `com.orm.**` and `uk.co.jakelee.blacksmith.model.**`, which SugarORM reflects over. Don't rename or obfuscate model classes/fields without also updating migration code.
- **`gamesLibraryVersion` stays at 21.0.0** — `play-services-games-v2` 22.x requires `minSdk 24`, and this app is on 23 because of Play Billing 9.
- **On screens ≥600dp, `targetSdk 37` means orientation requests are not honoured.** The `appCategory="game"` exemption and the compat property were both withdrawn at API 37. With the orientation setting on Auto (the default) the game fills a large screen and looks better than on a phone; if a player has forced portrait or landscape, the system letterboxes it into a window with black bars instead. Playable either way — it's cosmetic, and unavoidable at this target.
- **Adverts are gone.** The AppLovin account is disabled and the SDK was removed. `AdvertHelper` remains and still grants the same rewards, but goes straight to `InterstitialActivity` — a homegrown tips-and-countdown screen, not an ad SDK. Strings still say "advert" / "WATCH AD", which is now inaccurate.
- Bump `versionCode` and `versionName` in `Blacksmith/build.gradle` for releases. `MainActivity` compares `BuildConfig.VERSION_CODE` against the `SavedVersion` `Player_Info` row and shows the update dialog only when `VERSION_NAME` ends in `.0` — so a `.0` version is opt-in to announcing the release, and anything else stays silent. The dialog text is two strings: `updateMessage` is the greeting and is translated into all nine languages, while `updateMessageBody` holds the per-release notes in English only. **Rewrite `updateMessageBody` whenever you ship a `.0`**, or players get the previous release's notes.

## Deadlines

The point of the 3.0.4 work was to clear every known clock at once. Next forced dates:

| When | What |
|---|---|
| Aug 2027 | Nothing — cleared by targeting 37 and Billing 9 |
| Aug 2028 | Play Billing 9 stops being accepted; targetSdk 38 required |
| AGP 10 | Jetifier removed, forcing the androidx migration above |

Google adds requirements mid-cycle too — the 16KB page-size rule appeared that way, and Play rejected the first 3.1.0 upload over it. The only native code is `libpl_droidsonroids_gif`, from `android-gif-drawable`; 1.1.16 shipped 4KB-aligned `x86_64` and `armeabi-v7a` libs, and 1.2.29 is 16KB-aligned on all four ABIs. Checking only `arm64-v8a` is what hid it — verify **every** ABI's `PT_LOAD` alignment, plus the APK's own zip alignment:

```sh
unzip -o app.apk 'lib/*' -d /tmp/so                        # then per .so:
llvm-readelf -l /tmp/so/lib/<abi>/x.so | grep LOAD         # p_align must be >= 16384
zipalign -c -P 16 -v 4 app.apk                             # uncompressed .so page-aligned in the zip
```
