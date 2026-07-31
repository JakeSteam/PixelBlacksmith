# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Pixel Blacksmith — a released Android crafting game (`uk.co.jakelee.blacksmith`), Java, no Kotlin. Two Gradle modules: `:Blacksmith` (the app) and `:BaseGameUtils` (Google's vendored Play Games sample helper).

Codebase notes from the README, which still hold: most logic lives in each screen's Activity, with shared behaviour bolted on via `helper/*Helper` static classes. `DatabaseHelper`, `DisplayHelper`, `VisitorHelper`, and `WorkerHelper` are the large, tangled ones.

## Commands

`gradlew` is not executable in the repo — invoke it via `sh`:

```sh
sh gradlew :Blacksmith:assembleDebug        # build debug APK
sh gradlew :Blacksmith:assembleRelease      # build release APK
sh gradlew :Blacksmith:installDebug         # build + install on connected device
sh gradlew :Blacksmith:lintDebug            # Android lint
sh gradlew clean
```

There are no tests — no `src/test`, no `src/androidTest`, no test dependencies. Don't invent a test command; verify changes by building and running the app.

### The build is currently broken

`:Blacksmith:assembleDebug` fails at dependency resolution:

```
Could not find com.github.worker8:tourguide:1.0.17-SNAPSHOT
```

jcenter is dead and that snapshot is no longer on Sonatype OSS, and it isn't cached locally or vendored in `Blacksmith/libs/`. Everything else resolves. TourGuide is used only by `helper/TutorialHelper.java` (the onboarding tour overlays). Any work requiring a successful build needs this resolved first — vendor the AAR into `Blacksmith/libs/`, pin a released version, or replace TutorialHelper's implementation.

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

### Error handling convention

Game-action methods return an `int` status: a positive count on success, or a negative `Constants.ERROR_*` code. Callers map it through `ErrorHelper.errors` (a `HashMap<Integer, Integer>` of error code → string resource) and surface it with `ToastHelper.showErrorToast(view, length, text, saveToLog)`. "Toasts" are actually Snackbars, optionally persisted to the in-game message log (`Message.add`). Prefer this over throwing.

## Gotchas

- **Don't bump `supportLibraryVersion` (23.0.0) in the root `build.gradle`.** The existing comment there is accurate: it breaks alert sizes, tutorial skipping, and more. AndroidX + Jetifier are enabled and rewrite it at build time.
- **`android.nonFinalResIds=false` in `gradle.properties` is load-bearing** — `SettingsActivity` switches on view IDs.
- `compileSdkVersion` is pinned to 33 in `Blacksmith/build.gradle` while `targetSdkVersion` follows `apiVersion` (35) from the root `build.gradle`.
- The root `build.gradle` still lists `jcenter()`, which no longer serves artifacts. Add new dependencies expecting only `google()` / `mavenCentral()` to work.
- Release builds have `minifyEnabled false`, but `Blacksmith/proguard.pro` matters anyway — it keeps `com.orm.**` and `uk.co.jakelee.blacksmith.model.**`, which SugarORM reflects over. Don't rename or obfuscate model classes/fields without also updating migration code.
- Google Play Games (achievements, leaderboards) uses the deprecated `GoogleApiClient` in `GooglePlayHelper`; cloud saves and quests are already disabled/commented out. Play Services are optional at runtime — guard against a null/disconnected client.
- Bump `versionCode` and `versionName` in `Blacksmith/build.gradle` for releases. `MainActivity` compares `BuildConfig.VERSION_CODE` against the `SavedVersion` `Player_Info` row and shows an update message only when `VERSION_NAME` ends in `.0`.
