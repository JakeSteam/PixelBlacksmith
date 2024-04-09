# Pixel Blacksmith

This is the source code for [Pixel Blacksmith](https://play.google.com/store/apps/details?id=uk.co.jakelee.blacksmith), feel free to do whatever you want with it!

## Screenshots

| Home | Inventory | Workers | Market | Visitor | Crafting Table | Trophies |
| -- | -- | -- | -- | -- | -- | -- |
| ![image](https://user-images.githubusercontent.com/12380876/157330400-723aaf96-9abf-4210-958f-bc29b8548f96.png) | ![image](https://user-images.githubusercontent.com/12380876/157330425-a7b793ed-afb9-483d-81cb-bac14e8f668e.png) | ![image](https://user-images.githubusercontent.com/12380876/157330456-70c53943-60cb-4fde-9011-ced2692a8b69.png) | ![image](https://user-images.githubusercontent.com/12380876/157330517-8ff445d8-5162-42de-b18a-eda7ac2a2459.png) | ![image](https://user-images.githubusercontent.com/12380876/157330531-65be1ca3-4e8a-410e-a12a-4808095ef9f7.png) | ![image](https://user-images.githubusercontent.com/12380876/157330555-1a6814d9-b3dc-4cd0-9fe6-e17adcbbe2d2.png) | ![image](https://user-images.githubusercontent.com/12380876/157330570-165eadf6-04f5-432d-a4ca-9a98f60cfd82.png) |

## Play Store description

Pixel Blacksmith is a game about crafting items for customers! As all kinds of visitors come into your humble blacksmithing shop (there's even robots!), they'll want all sorts of items before they'll leave. Can you help them?

Features:

* No premium currency, no pay to win, no "Invite your friends", no forced adverts, no nonsense!
* 250+ unique items, each with recipes!
* Advanced multi-stage crafting system!
* 50+ traders on the market, each with unlockable tiers!
* 55+ visitors, each with bonuses for their preferred items!
* Advanced visitor demand system, so you'll never get the same request twice!
* 30 upgrades (with tiers) + 24 settings!
* Hire helpers + heroes to gather resources!
* Seasonal events with unique rewards!

Also:

* Comprehensive tutorial, and detailed help articles for every area!
* Regular additions of player-suggested features & content!
* No internet required!
* Active Reddit community (/r/PixelBlacksmith)!

Google Play:

* ~~35 Repeatable Quests~~ Deprecated by Google :(
* 31 Achievements
* 7 Leaderboards
* ~~Cloud Saves~~ Requires rebuild of Google Play implementation :(

Supported Devices:

* All Android versions from ~~Ice Cream Sandwich~~ API 19+
* All phone & tablet sizes, from a tiny 3.7" Nexus One to a chubby 5.7" Nexus 6P, and beyond to the 10.1" Nexus 10!
* Google Play Services are optional.

Permissions:

* Billing: Used for Premium IAP, and contributions.
* Internet, External Storage, Network State: Used to download and cache the appropriate quality adverts.
* External Files / Photos: Used to import + export game save files.

About Developer:

Pixel Blacksmith is created and maintained by Jake Lee, a software engineer from England. If you've encountered a bug, or have an idea for a new feature, please mention it in a review or on https://reddit.com/r/PixelBlacksmith and I'll reply ASAP. I don't bite!

Happy crafting, blacksmiths!

## Codebase notes
* The app uses the long-dead SugarORM in `DatabaseHelper.java`.
* The app contains most code in each screen's activity, with additional functionality added via "Helper" classes.
* `DisplayHelper.java`, `VisitorHelper.java`, and `WorkerHelper.java` are horrendously complex classes that contain most of the logic!

## Licensing
* Entire repository is under the MIT license, essentially do whatever you want but don't blame me if it breaks!
* Redacted images are licensed from long forgotten sources, unfortunately!
* All unredacted images are modified versions of [Kenney](https://www.kenney.nl/assets) assets.

