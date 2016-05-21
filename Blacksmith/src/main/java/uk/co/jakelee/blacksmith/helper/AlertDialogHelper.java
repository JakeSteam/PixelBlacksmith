package uk.co.jakelee.blacksmith.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Pair;
import android.widget.EditText;
import android.widget.Toast;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.main.MainActivity;
import uk.co.jakelee.blacksmith.main.MarketActivity;
import uk.co.jakelee.blacksmith.main.TraderActivity;
import uk.co.jakelee.blacksmith.main.UpgradeActivity;
import uk.co.jakelee.blacksmith.main.VisitorActivity;
import uk.co.jakelee.blacksmith.main.WorkerActivity;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Pending_Inventory;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Trader;
import uk.co.jakelee.blacksmith.model.Trader_Stock;
import uk.co.jakelee.blacksmith.model.Upgrade;
import uk.co.jakelee.blacksmith.model.Visitor;
import uk.co.jakelee.blacksmith.model.Worker;

public class AlertDialogHelper {
    public static void enterSupportCode(final Context context, Activity activity) {
        final EditText supportCodeBox = new EditText(context);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, android.R.style.Theme_Dialog);
        alertDialog.setMessage(context.getString(R.string.supportCodeQuestion));
        alertDialog.setView(supportCodeBox);

        alertDialog.setPositiveButton(context.getString(R.string.supportCodeConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //String supportCode = SupportCodeHelper.encode("1462827600000|UPDATE upgrade SET current = 20, maximum = 100 WHERE name IN ('Gold Bonus', 'XP Bonus')");
                String supportCode = supportCodeBox.getText().toString();
                if (SupportCodeHelper.applyCode(supportCode)) {
                    ToastHelper.showPositiveToast(context, Toast.LENGTH_LONG, R.string.supportCodeComplete, true);
                } else {
                    ToastHelper.showErrorToast(context, Toast.LENGTH_LONG, R.string.supportCodeFailed, true);
                }
            }
        });

        alertDialog.setNegativeButton(context.getString(R.string.supportCodeCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public static void confirmUpgrade(final Context context, final UpgradeActivity activity, final Upgrade upgrade) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog);
        alertDialog.setMessage(String.format(context.getString(R.string.upgradeQuestion),
                upgrade.getName(),
                (upgrade.increases() ? upgrade.getCurrent() + upgrade.getIncrement() : upgrade.getCurrent() - upgrade.getIncrement()),
                upgrade.getMaximum(),
                upgrade.getUpgradeCost()));
        alertDialog.setIcon(R.drawable.item52);

        alertDialog.setPositiveButton(context.getString(R.string.upgradeConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int upgradeResponse = upgrade.tryUpgrade();
                if (upgradeResponse == Constants.SUCCESS) {
                    ToastHelper.showPositiveToast(context, Toast.LENGTH_SHORT, String.format(context.getString(R.string.upgradeSuccess), upgrade.getName()), true);
                    Player_Info.increaseByOne(Player_Info.Statistic.UpgradesBought);
                    activity.alertDialogCallback();
                } else {
                    ToastHelper.showErrorToast(context, Toast.LENGTH_SHORT, ErrorHelper.errors.get(upgradeResponse), true);
                }
            }
        });

        alertDialog.setNegativeButton(context.getString(R.string.upgradeCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public static void openSocialMedia(final Context context, final Activity activity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog);
        alertDialog.setMessage(context.getString(R.string.socialMediaQuestion));

        alertDialog.setNeutralButton(context.getString(R.string.socialMediaReddit), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.reddit.com/r/PixelBlacksmith"));
                activity.startActivity(browserIntent);
            }
        });

        alertDialog.setNegativeButton(context.getString(R.string.socialMediaTwitter), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.twitter.com/PixelBlacksmith"));
                activity.startActivity(browserIntent);
            }
        });

        alertDialog.setPositiveButton(context.getString(R.string.socialMediaFacebook), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/PixelBlacksmith"));
                activity.startActivity(browserIntent);
            }
        });

        alertDialog.show();
    }

    public static void confirmBuyWorker(final Context context, final WorkerActivity activity, final Worker worker) {
        final int buyCost = WorkerHelper.getBuyCost(worker);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog);
        alertDialog.setMessage(String.format(context.getString(R.string.buyWorkerQuestion), buyCost));

        alertDialog.setPositiveButton(context.getString(R.string.buyWorkerConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Inventory coinStock = Inventory.getInventory(Constants.ITEM_COINS, Constants.STATE_NORMAL);
                if (coinStock.getQuantity() >= buyCost) {
                    coinStock.setQuantity(coinStock.getQuantity() - buyCost);
                    coinStock.save();

                    worker.setPurchased(true);
                    worker.save();
                    ToastHelper.showPositiveToast(context, Toast.LENGTH_LONG, context.getString(R.string.buyWorkerComplete), true);
                    activity.scheduledTask();
                } else {
                    ToastHelper.showErrorToast(context, Toast.LENGTH_SHORT, ErrorHelper.errors.get(Constants.ERROR_NOT_ENOUGH_COINS), false);
                }
            }
        });

        alertDialog.setNegativeButton(context.getString(R.string.buyWorkerCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public static void confirmPrestige(final Context context, Activity activity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog);
        alertDialog.setMessage(context.getString(R.string.prestigeQuestion));
        alertDialog.setIcon(R.drawable.levels);

        alertDialog.setPositiveButton(context.getString(R.string.prestigeConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                PrestigeHelper.prestigeAccount();
                ToastHelper.showPositiveToast(context, Toast.LENGTH_LONG, String.format(context.getString(R.string.prestigeComplete), Player_Info.getPrestige() + 1), false);
            }
        });

        alertDialog.setNegativeButton(context.getString(R.string.prestigeCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public static void confirmVisitorAdd(final Context context, final MainActivity activity) {
        final int visitorCost = VisitorHelper.getVisitorAddCost();
        int questionString = Player_Info.displayAds() ? R.string.bribeQuestionAdvert : R.string.bribeQuestion;

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setMessage(String.format(context.getString(questionString),
                visitorCost,
                DateHelper.getMinsSecsRemaining(VisitorHelper.getTimeUntilSpawn())));
        alertDialog.setIcon(R.drawable.item52);

        alertDialog.setPositiveButton(context.getString(R.string.bribeConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Inventory coinStock = Inventory.getInventory(Constants.ITEM_COINS, Constants.STATE_NORMAL);
                if (coinStock.getQuantity() >= visitorCost) {
                    coinStock.setQuantity(coinStock.getQuantity() - visitorCost);
                    coinStock.save();
                    if (VisitorHelper.tryCreateVisitor()) {
                        ToastHelper.showPositiveToast(context, Toast.LENGTH_SHORT, String.format(context.getString(R.string.bribeComplete), visitorCost), true);
                    }
                } else {
                    ToastHelper.showErrorToast(context, Toast.LENGTH_SHORT, context.getString(R.string.bribeFailure), true);
                }
            }
        });

        alertDialog.setNeutralButton(context.getString(R.string.bribeCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        if (Player_Info.displayAds()) {
            alertDialog.setNegativeButton(context.getString(R.string.bribeAdvert), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    AdvertHelper.getInstance(context).showAdvert(activity, AdvertHelper.advertPurpose.ConvVisitorSpawn);
                }
            });
        }
        alertDialog.show();
    }

    public static void confirmVisitorDismiss(final Context context, final Visitor visitor, final VisitorActivity activity) {
        final int visitorCost = VisitorHelper.getVisitorDismissCost(visitor.getId());
        int questionID = Player_Info.displayAds() ? R.string.dismissQuestionAdvert : R.string.dismissQuestion;

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog);
        alertDialog.setMessage(String.format(context.getString(questionID), visitorCost));
        alertDialog.setIcon(R.drawable.item52);

        alertDialog.setPositiveButton(R.string.dismissConfirm, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Inventory coinStock = Inventory.getInventory(Constants.ITEM_COINS, Constants.STATE_NORMAL);
                if (coinStock.getQuantity() >= visitorCost) {
                    coinStock.setQuantity(coinStock.getQuantity() - visitorCost);
                    coinStock.save();

                    VisitorHelper.removeVisitor(visitor);
                    SoundHelper.playSound(context, SoundHelper.walkingSounds);
                    ToastHelper.showPositiveToast(context, Toast.LENGTH_SHORT, R.string.dismissComplete, true);
                    activity.finish();
                } else {
                    ToastHelper.showErrorToast(context, Toast.LENGTH_SHORT, context.getString(R.string.dismissFailure), true);
                }
            }
        });

        alertDialog.setNeutralButton(context.getString(R.string.dismissCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        if (Player_Info.displayAds()) {
            alertDialog.setNegativeButton(R.string.dismissConfirmAdvert, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    AdvertHelper.getInstance(context).showAdvert(activity, AdvertHelper.advertPurpose.ConvVisitorDismiss);
                }
            });
        }

        alertDialog.show();
    }

    public static void confirmTraderRestockAll(final Context context, final MarketActivity activity, final int restockCost) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog);
        String question = Player_Info.displayAds() ?
                context.getString(R.string.traderRestockAllQuestionAdvert) :
                String.format(context.getString(R.string.traderRestockAllQuestion), restockCost);
        alertDialog.setMessage(question);
        alertDialog.setIcon(R.drawable.item52);

        alertDialog.setPositiveButton(context.getString(R.string.traderRestockAllConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int traderResponse = Trader.restockAll(restockCost);
                if (traderResponse == Constants.SUCCESS) {
                    ToastHelper.showPositiveToast(context, Toast.LENGTH_SHORT, String.format(context.getString(R.string.traderRestockAllComplete), restockCost), true);
                } else {
                    ToastHelper.showErrorToast(context, Toast.LENGTH_SHORT, ErrorHelper.errors.get(traderResponse), true);
                }
                activity.alertDialogCallback();
            }
        });

        alertDialog.setNeutralButton(context.getString(R.string.traderRestockAllCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        if (Player_Info.displayAds()) {
            alertDialog.setNegativeButton(context.getString(R.string.traderRestockAllConfirmAdvert), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    AdvertHelper.getInstance(context).showAdvert(activity, AdvertHelper.advertPurpose.ConvMarketRestock);
                }
            });
        }

        alertDialog.show();
    }

    public static void confirmTraderRestock(final Context context, final TraderActivity activity, final Trader trader, final int restockCost) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog);
        alertDialog.setMessage(String.format(context.getString(R.string.traderRestockQuestion), trader.getName(), restockCost));
        alertDialog.setIcon(R.drawable.item52);

        alertDialog.setPositiveButton(context.getString(R.string.traderRestockConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int traderResponse = trader.restock(restockCost);
                if (traderResponse == Constants.SUCCESS) {
                    ToastHelper.showPositiveToast(context, Toast.LENGTH_SHORT, String.format(context.getString(R.string.traderRestockComplete), restockCost), true);
                } else {
                    ToastHelper.showErrorToast(context, Toast.LENGTH_SHORT, ErrorHelper.errors.get(traderResponse), true);
                }
                activity.alertDialogCallback();
            }
        });

        alertDialog.setNegativeButton(context.getString(R.string.traderRestockCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public static void confirmBonusAdvert(final Context context, final MainActivity activity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog);
        alertDialog.setMessage(R.string.bonusQuestion);

        alertDialog.setPositiveButton(context.getString(R.string.bonusWatch), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                AdvertHelper.getInstance(context).showAdvert(activity, AdvertHelper.advertPurpose.BonusBox);
            }
        });

        alertDialog.setNegativeButton(context.getString(R.string.bonusCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public static void confirmItemBuy(final Context context, final TraderActivity activity, final Trader_Stock itemStock) {
        final Item item = Item.findById(Item.class, itemStock.getItemID());
        final int itemValue = item.getValue();
        final String itemName = item.getFullName(Constants.STATE_NORMAL);
        final Trader trader = Trader.findById(Trader.class, itemStock.getTraderType());

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog);
        alertDialog.setMessage(String.format(context.getString(R.string.itemBuyQuestion), itemName, itemValue, itemStock.getStock(), itemValue));
        alertDialog.setIcon(R.drawable.item52);

        alertDialog.setPositiveButton(context.getString(R.string.itemBuy1Confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int quantity = 1;

                int buyResponse = Inventory.buyItem(itemStock);
                if (buyResponse == Constants.SUCCESS) {
                    ToastHelper.showToast(context, Toast.LENGTH_SHORT, String.format(context.getString(R.string.itemBuyComplete), quantity, itemName, itemValue), false);
                    Player_Info.increaseByOne(Player_Info.Statistic.ItemsBought);
                    trader.setPurchases(trader.getPurchases() + quantity);
                    trader.save();
                } else {
                    ToastHelper.showErrorToast(context, Toast.LENGTH_SHORT, ErrorHelper.errors.get(buyResponse), false);
                }
                activity.alertDialogCallback();
            }
        });

        alertDialog.setNegativeButton(context.getString(R.string.itemBuyAllConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.vh.traderBusy = true;
                int itemsBought = 0;
                int buyResponse = Constants.ERROR_NOT_ENOUGH_COINS;
                List<Pair<Long, Integer>> items = new ArrayList<>();

                Inventory coinStock = Inventory.getInventory(Constants.ITEM_COINS, Constants.STATE_NORMAL);
                Item item = Item.findById(Item.class, itemStock.getItemID());
                int totalCost = (item.getModifiedValue(itemStock.getState()) * itemStock.getStock());
                if (totalCost <= coinStock.getQuantity()) {
                    int itemsToBuy = itemStock.getStock();
                    itemsBought += itemsToBuy;
                    itemStock.setStock(0);
                    itemStock.save();

                    coinStock.setQuantity(coinStock.getQuantity() - totalCost);
                    coinStock.save();

                    for (int i = 1; i <= itemsToBuy; i++) {
                        items.add(new Pair<>(itemStock.getItemID(), itemStock.getState()));
                    }
                }

                if (itemsBought > 0) {
                    ToastHelper.showToast(context, Toast.LENGTH_SHORT, String.format(context.getString(R.string.itemBuyComplete), itemsBought, itemName, itemValue * itemsBought), false);
                    Player_Info.increaseByX(Player_Info.Statistic.ItemsBought, itemsBought);
                    trader.setPurchases(trader.getPurchases() + itemsBought);
                    trader.save();
                } else {
                    ToastHelper.showErrorToast(context, Toast.LENGTH_SHORT, ErrorHelper.errors.get(buyResponse), false);
                }

                Pending_Inventory.addScheduledItems(Constants.LOCATION_MARKET, items);

                MainActivity.vh.traderBusy = false;
                activity.alertDialogCallback();
            }
        });

        alertDialog.setNeutralButton(context.getString(R.string.itemBuyCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public static void confirmItemBuyAll(final Context context, final TraderActivity activity, final Trader trader) {
        final List<Trader_Stock> itemStocks = Select.from(Trader_Stock.class).where(
                Condition.prop("trader_type").eq(trader.getId()),
                Condition.prop("stock").gt(0),
                Condition.prop("required_purchases").lt(trader.getPurchases() + 1)).list();

        if (itemStocks.size() == 0) {
            ToastHelper.showToast(context, Toast.LENGTH_SHORT, R.string.itemBuyAllNoItems, false);
            return;
        }

        int totalValue = 0;
        int itemCount = 0;
        for (Trader_Stock itemStock : itemStocks) {
            Item item = Item.findById(Item.class, itemStock.getItemID());
            totalValue += (item.getModifiedValue(itemStock.getState()) * itemStock.getStock());
            itemCount += itemStock.getStock();
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog);
        alertDialog.setMessage(String.format(context.getString(R.string.itemBuyAllQuestion), itemCount, totalValue));
        alertDialog.setIcon(R.drawable.item52);

        alertDialog.setPositiveButton(context.getString(R.string.itemBuyAllConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                MainActivity.vh.traderBusy = true;
                int itemsBought = 0;
                int buyResponse = Constants.ERROR_NOT_ENOUGH_COINS;
                boolean successful = true;
                List<Pair<Long, Integer>> items = new ArrayList<>();

                for (Trader_Stock itemStock : itemStocks) {
                    Inventory coinStock = Inventory.getInventory(Constants.ITEM_COINS, Constants.STATE_NORMAL);
                    Item item = Item.findById(Item.class, itemStock.getItemID());
                    int totalCost = (item.getModifiedValue(itemStock.getState()) * itemStock.getStock());
                    if (totalCost <= coinStock.getQuantity() && successful) {
                        int itemsToBuy = itemStock.getStock();
                        itemsBought += itemsToBuy;
                        itemStock.setStock(0);
                        itemStock.save();

                        coinStock.setQuantity(coinStock.getQuantity() - totalCost);
                        coinStock.save();

                        for (int i = 1; i <= itemsToBuy; i++) {
                            items.add(new Pair<>(itemStock.getItemID(), itemStock.getState()));
                        }
                    } else {
                        successful = false;
                    }
                }

                if (itemsBought > 0) {
                    ToastHelper.showPositiveToast(context, Toast.LENGTH_SHORT, String.format(context.getString(R.string.itemBuyAllComplete), itemsBought), false);
                    Player_Info.increaseByX(Player_Info.Statistic.ItemsBought, itemsBought);
                    trader.setPurchases(trader.getPurchases() + itemsBought);
                    trader.save();
                } else {
                    ToastHelper.showErrorToast(context, Toast.LENGTH_SHORT, ErrorHelper.errors.get(buyResponse), false);
                }

                Pending_Inventory.addScheduledItems(Constants.LOCATION_MARKET, items);

                MainActivity.vh.traderBusy = false;
                activity.alertDialogCallback();
            }
        });

        alertDialog.setNegativeButton(context.getString(R.string.itemBuyCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }
}

