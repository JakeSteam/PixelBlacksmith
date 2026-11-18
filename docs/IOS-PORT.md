# iOS port: Kotlin Multiplatform + Compose Multiplatform

Plan for moving Pixel Blacksmith off the 2015-era Java/Android codebase onto one Kotlin Multiplatform (KMP) + Compose Multiplatform (CMP) codebase that ships on iOS first, then replaces the Android app.

## Why KMP/CMP

- **One codebase for both platforms.** The UI is entirely custom pixel art, so building it natively in SwiftUI would gain almost nothing.
- **The Android app needs this work anyway.** AGP 10 removes Jetifier, which forces the androidx migration. `targetSdk 38` is due by Aug 2028. Moving Android onto the new codebase deals with both.
- **The tools are mature.**
  - CMP for iOS has been stable since 1.8 (May 2025); the current version is 1.12.x.
  - SQLDelight is mature on both platforms.
- **Rejected alternatives:**
  - Swift only: two codebases forever.
  - Flutter: a full rewrite in Dart, with no advantage over KMP.
  - Godot or Unity: a poor fit for a game that is mostly menus.
  - J2ObjC or MobiVM (Java on iOS): the logic is too tangled up with SugarORM and Android to reuse.

## What the existing code looks like

| | |
|---|---|
| Code | 20.7k lines of Java in 97 files. About 7.3k is in Activities, 9.3k in helpers (including `DatabaseHelper` at 3.3k) and 4k in models |
| How tangled it is | 416 SugarORM call sites spread across screens and helpers, and game rules (e.g. `craft()`) sit inside Activities. No logic can be lifted out unchanged, so this is a port, done screen by screen |
| Screens | 30 Activities. 28 are "popups": full-window activities drawing a framed panel over `MainActivity`. 28 `AlertDialog`s, of which only 2 take text input |
| Art | 461 drawables looked up by name (`item52`, `visitor3`, …). 379 of the 394 sprites are exact 10× upscales of 16×16 art. 21 GIFs, all small. 10 files have an uppercase `.PNG` extension |
| Strings | 1,842 English strings and 1,581 in each of 9 other languages, looked up by name (`item_name_` + id, 23 prefixes). Android-only format specifiers: `%,d`, `%.0f`, up to `%24$d` |
| Audio | 10 sound effects and 1 looping music track. `sell1.ogg` can't play on iOS |
| Platform features | Billing 9 (5 products), Play Games v2 (30 achievements, 7 leaderboards, events, cloud saves), 7 kinds of scheduled notification, AES-encrypted save export |
| Database | Schema last changed Feb 2018 (`DB_V2_3_6`). Static game content and player progress are mixed together in the same tables |

## Architecture

```
PixelBlacksmith/
├─ Blacksmith/   existing Java app, untouched until the Android cutover (M8), then deleted
├─ shared/       KMP library: com.android.kotlin.multiplatform.library + CMP + SQLDelight
│   ├─ commonMain   content, persistence, game rules, UI, strings, assets
│   ├─ commonTest   the project's first tests
│   ├─ jvmMain/Test JVM target used only for fast tests
│   ├─ androidMain  Billing 9, Play Games v2, notifications, audio
│   └─ iosMain      notifications, audio (Kotlin/Native calling Apple APIs)
├─ androidApp/   thin com.android.application, applicationId uk.co.jakelee.blacksmith.next until cutover
└─ iosApp/       Xcode project: SwiftUI host, StoreKit 2 and GameKit in Swift
```

- AGP 9 no longer allows the KMP plugin and `com.android.application` in the same module, which is why `shared` and `androidApp` are separate.
- **Versions:** Kotlin 2.4.x, CMP 1.12.x, SQLDelight 2.4.x, on the existing AGP 9.3.1 and Gradle 9.5.0.
- **Minimum OS:** iOS 16 (StoreKit 2 needs at least 15). Android stays at `minSdk 23` because of Billing 9.
- **Universal app:** one iOS build for iPhone and iPad.

### Data: a new schema, plus a one-off migrator

The old layout isn't carried forward. It mixes static content with player progress, looks upgrades up by their English name, and needed the "hotfix" repair tools to fix up SugarORM.

- **Static content becomes read-only bundled data.** This covers items, recipes, types, tiers, states, categories, locations, characters, criteria, trader definitions and default stock, visitor types and demands, upgrade and super-upgrade definitions, achievements, hero adventures and categories, worker resources, contribution goals and assistant definitions.
  - It's generated once, by a script, from a fresh install of the Java app.
  - Content IDs stay exactly the same, because drawable and string names are built from them (`item52`, `item_name_52`).
  - A content change becomes a data edit, not a migration.
- **Player progress gets a new, normalised SQLDelight schema.** Only real state is stored:
  - inventory and pending crafts;
  - statistics and settings;
  - upgrade levels, keyed by a stable ID instead of the English name;
  - trader stock, level and lock;
  - current visitors, plus visitor statistics and trophies;
  - workers, heroes, assistants and the message log;
  - what each achievement last reported.
  - Anything that can be derived is calculated rather than stored, e.g. which slots are unlocked from level and Premium.
- **One migrator, two inputs.**
  - A `LegacySnapshot` can be read either from an old SugarORM `blacksmith.db` or from the legacy save string: the Gson output of 14 tables, used by local exports *and* Play Games cloud saves.
  - The migrator converts either into the new schema.
  - The legacy save-string reader stays in permanently, for old exports and existing cloud saves.
- **All players are assumed to be on the latest version, `DB_LATEST` (25).** The migration patches are **not** ported.
  - If a database reports an older version, migration refuses rather than guessing.
  - The old database file is never deleted. It's renamed and kept as a backup, so a failed migration can be retried in a later release.
- **New save format:** versioned JSON from kotlinx.serialization, with no AES. Obfuscating a single-player save buys nothing, and dropping encryption avoids Apple's export-compliance questions. AES remains only inside the legacy reader.
- **Preferences:**
  - Settings move into the database.
  - On-screen UI positions go in multiplatform-settings.
  - On Android, the migrator reads the legacy `uk.co.jakelee.blacksmith` prefs once, for `tutorialStage` and the saved language (`locale`).

### Game rules

- **Models and helpers:** ported into one Kotlin data-access object per table. Methods keep their Java names where that makes side-by-side review easier.
- **Global state:** the hidden statics (`MainActivity.vh`, `needToRedrawVisitors/Slots`, the `DisplayHelper` singleton holding live views) become observable state that the UI watches.
- **`GameClock`:** a coroutine-based clock replacing the 1s/10s/60s `Handler` loops. The clock and the random-number source are passed in, so rules can be tested.
- **No background running needed:** progress is already based on timestamps, so iOS never has to run the game in the background.

### Strings

- **Conversion:** a script converts the Android `values*/strings.xml` files into one JSON file per language.
- **Our own lookup:** supports names built at runtime, falls back to English, and switches language live without a restart.
- **Our own formatter:** CMP's string formatter handles only `%1$s` and `%1$d`. Ours covers the specifiers actually used:
  - `%s`, `%d`, `%n$s` and `%n$d`;
  - `%,d` and `%n$,d` (thousands separators);
  - `%.Nf` (fixed decimals);
  - `%%` (a literal percent sign).
  - It has unit tests.
- **iOS wording:** strings that mention Google Play, the Play Store or Android get iOS-specific variants, as Apple's guideline 2.3.10 requires.

### Images and animation

- **Drawables:** copied into Compose resources and looked up with `Res.allDrawableResources["item$id"]`.
- **Crisp pixels:** images are drawn with `FilterQuality.None`, so scaling doesn't blur them.
- **State tints:** done with multiply colour filters, replacing `PorterDuff.MULTIPLY`. Silhouettes for unseen items work the same way.
- **GIFs:** converted to PNG frame strips with frame timings by a script, and played by a small `AnimatedSprite` composable. Compose can't play GIFs on iOS.
- **Fixes along the way:** the uppercase `.PNG` files are renamed. `loading_screen.gif`, which nothing uses, is dropped.

### UI structure

- **Navigation:** the main scene (a horizontal scroll over 3 wallpapers, with objects placed by percentage) plus a stack of popups drawn over it.
- **Shared components:**
  - `PixelText` (BetterPixels font) and `PixelButton`;
  - `Panel` for the 2-part and 3-part backgrounds;
  - `ItemPager` (replaces ViewFlipper, HorizontalDots and the swipe code);
  - `Tabs` and `ItemTable`;
  - `PixelDialog`, one component for all the dialogs, with an optional text field;
  - `Toast` in 4 colours, plus the message log;
  - `TutorialOverlay`, a spotlight using target positions captured at layout time;
  - `Confetti`, shown in December only.
- **Platform features**, as Kotlin interfaces implemented per platform:
  - `Store`
  - `GameServices`
  - `Notifier`
  - `Audio`
  - `SaveFiles`
  - `Links`
  - `Orientation`
  - `RewardedAds`
  - The iOS `Store` and `GameServices` are written in Swift, because StoreKit 2 can only be called from Swift. They're passed into Kotlin at startup.

### Rewarded adverts

The advert step is a balance mechanic. Watching an ad is the alternative to paying coins for:

- a visitor arriving early;
- dismissing a visitor;
- restocking one trader, or all of them;
- the bonus chest (Premium players skip the ad).

The advert itself only came out of 3.x to avoid technical problems. So the mechanic stays, behind a `RewardedAds` interface:

- **Until real ads are added,** the interface shows the existing 15-second tips countdown (`InterstitialActivity` today). Its buttons say "Wait" rather than "Watch ad", so App Review doesn't see an advert button with no advert behind it.
- **Real ads are expected before release.** Adding them means:
  - choosing an SDK that works on both platforms, with a thin native implementation each;
  - a consent prompt for the UK and EU;
  - Apple's App Tracking Transparency prompt, if the SDK tracks;
  - updating the App Store privacy label and the Play Data safety form to match the SDK.
- **Unchanged:** the reward rules, Premium skipping the bonus-chest ad, and the "Disable adverts" setting for Premium players.

### Features being removed

| Feature | Why | Effect |
|---|---|---|
| Support codes | They run SQL to grant items, which risks Apple's rule 3.1.1 (no unlocking outside in-app purchase). Barely used | Removed on both platforms |
| Hotfix menu | It repaired SugarORM-specific problems that can't happen in the new schema | Removed |
| Blacksmith Slots promo | Links to an Android-only sister app | Removed on iOS |
| Play Games events | Not supported by Game Center; they were only analytics | Kept on Android, does nothing on iOS |
| Premium carried across platforms | Not worth the complexity | A save moved between platforms doesn't carry Premium. On iOS, Premium comes from a StoreKit purchase or Restore |

## Milestones

Each milestone ends with a stop to verify and commit.

### M0: Apple account setup (manual, can run alongside M1)

- **Enrol** as the organisation, using the company's existing D-U-N-S number. Sign the Paid Apps agreement, add tax (W-8BEN-E) and bank details, join the Small Business Program (15% commission), and declare EU trader status.
- **Reserve the name** "Pixel Blacksmith" in App Store Connect.
- **Create the bundle ID** with the Game Center, In-App Purchase and iCloud capabilities.
- **Create the in-app products:** `premium` as a non-consumable; `contribute`, `coin_pack_1`, `coin_pack_2` and `coin_pack_3` as consumables.
- **Create the 30 achievements and 7 leaderboards.**
  - The existing Play IDs (`CgkI…`) can be reused as Game Center IDs, which avoids a mapping table.
  - Game Center requires an image of at least 512px for each achievement. Check the Play Console for the originals.

### M1: Prove the approach (go/no-go)

1. Gradle modules plus the Xcode project. Both apps launch.
2. The main scene scrolls horizontally, with the wallpapers and one animated sprite converted from a GIF.
3. The strings converter and formatter work, including `%,d`. The BetterPixels font renders **Japanese, Chinese, Korean and Russian** text on iOS. Fallback for characters the font lacks is the biggest unknown.
4. The Furnace pager works with images looked up by name, and one smelt completes and is saved in a first draft of the new schema.
5. Record iOS app size and startup time.

**Stop if 3 or 4 doesn't come out well.**

### M2: Content and persistence

1. **Generate the static content.** Fresh-install the Java app on an emulator, dump the database, and generate the bundled content from it. A test checks every content ID used by drawables and strings exists, and the reverse.
2. **Check which columns change.** Diff a fresh database against a well-played one, column by column, to confirm which legacy columns hold player state. That decides the new schema.
3. **Write the new player schema and its data-access objects.**
4. **Build the migrator** for both input types, with tests using real database files and real exported saves at the latest version. Collect a few, from your own devices and any players willing to share them.
5. **Add the new JSON save format** and tests that round-trip it.

### M3: Game rules

Port the models and helpers, with tests for:

- crafting checks and ingredient removal;
- visitor generation and demands;
- trader restocking and the restock tax;
- workers and heroes finishing;
- prestige;
- the bonus chest timer, and claiming it through the advert step;
- achievement progress;
- when each of the 7 notification types should fire.

### M4: Shared components and the core game loop

- The shared components listed above.
- **Screens:** Splash, Main, Furnace, Anvil, Table, Enchanting, Inventory, Market, Trader, Visitor, Trade, ItemSelect, Messages, Help.
- **Exit check:** play from a new game to level 5 on both platforms.

### M5: Remaining screens

- **Screens:** Upgrade, Worker, Food, Tool, Adventure, Equipment, EquipmentSelect, VisitorSelect, Assistant (peeking carousel), Trophy, Statistics, Settings, Credits, Premium, BuyCoins, and the countdown screen used by `RewardedAds`.
- **Tutorial:** 15 stages, about 41 steps. Each step's text is also written to the message log, as now.
- **Audio:** sound effects and looping music. This fixes the `MediaPlayer` leak and the volume bug in `MusicService`. Convert `sell1.ogg` to a format iOS can play.
- **Seasonal effects:** December wallpapers and confetti.

### M6: Platform services

- **`Store`.** Android ports `BillingHelper`. iOS uses StoreKit 2:
  - a transaction listener started at launch;
  - each transaction finished only after the reward is granted;
  - a **Restore Purchases** button.
  - Applying Premium must do nothing if it's already active. `addPremiumFeatures` currently stacks its bonuses each time it runs.
- **`GameServices`.** Android ports Play Games v2. iOS uses GameKit:
  - sign-in;
  - achievement progress reported as percentages;
  - leaderboard scores;
  - the Game Center screens.
- **Cloud saves.** Android reads legacy Play Games saves through the legacy reader, and writes the new format. iOS uses Game Center saved games. Both keep the "is this save better?" check.
- **`Notifier`.** Android keeps its alarms. iOS uses `UNUserNotificationCenter`, scheduled when the app goes to the background.
- **`SaveFiles`.** A file picker and share sheet for export and import, on both platforms.
- **`RewardedAds`.** The real ad SDK on both platforms, with consent handling (see "Rewarded adverts"). Replaces the countdown stand-in.
- **Other:** the system review prompt (replacing android-rate), external links, and the orientation setting.

### M7: iOS release

- **App files:**
  - a 1024px icon, flattened onto the `#F5B784` background;
  - the launch screen;
  - `PrivacyInfo.xcprivacy`, the privacy manifest Apple now requires.
- **App Store Connect forms:**
  - the privacy label: "Data Not Collected" without ads, or whatever the chosen ad SDK requires;
  - the age-rating questionnaire;
  - iPhone and iPad screenshots.
- **TestFlight beta**, opened to r/PixelBlacksmith, then App Store review and release.

### M8: Android cutover

- **Release settings:** `androidApp` takes over `uk.co.jakelee.blacksmith`, the shipping signing config, versionCode 100 or higher and version `4.0.0`. Rewrite `updateMessageBody` with the release notes.
- **First launch:** migrates the legacy database and prefs, and keeps the old database file as a backup.
- **Testing:**
  - Install over a real 3.1.0 game on a **test device**. Debug builds are signed with the shipping certificate and will overwrite a production install.
  - Run a parity checklist against the Java app, with side-by-side screenshots.
- **Rollout:** internal testing, then closed testing, then a staged production rollout you can halt.
- **Cleanup:** delete `Blacksmith/` and rewrite CLAUDE.md for the new structure.
- **Separately, on Play, now:** the listing still says "Contains ads", the Data safety form still mentions third-party sharing, and the description still mentions adverts. All of that dates from the removed AppLovin SDK.

## Biggest risks

| Risk | Mitigation |
|---|---|
| The pixel font's handling of Japanese, Chinese, Korean and Russian on iOS | Tested first, in M1 |
| The migrator gets an unusual real save wrong | Test with real sample files. Never delete the legacy database. Refuse rather than guess |
| Performance of long text-heavy screens (Help, Statistics) on iOS | Use lazy lists. Measure during M4 and M5 |
| Rejection at App Store review | Already addressed above: 2.3.10 (other platforms' names), 3.1.1 (support codes), "Wait" wording until real ads exist, and Restore Purchases |
| Ad SDK support across both platforms, plus consent | Pick the SDK early in M6. Keep it behind `RewardedAds` so the game logic never depends on it |
| How much work the UI is (30 screens) | Shared components first. Grouping screens into the M4 and M5 batches gives regular points where something is playable |
