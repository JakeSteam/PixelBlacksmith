package uk.co.jakelee.blacksmith.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.Toast;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.main.MarketActivity;
import uk.co.jakelee.blacksmith.main.TraderActivity;
import uk.co.jakelee.blacksmith.main.VisitorActivity;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Trader;
import uk.co.jakelee.blacksmith.model.Trader_Stock;
import uk.co.jakelee.blacksmith.model.Visitor;

public class AlertDialogHelper {
    public static void enterSupportCode(final Context context, Activity activity) {
        final EditText supportCodeBox = new EditText(context);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, android.R.style.Theme_Dialog);
        alertDialog.setMessage(context.getString(R.string.supportCodeQuestion));
        alertDialog.setView(supportCodeBox);

        alertDialog.setPositiveButton(context.getString(R.string.supportCodeConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //String supportCode = SupportCodeHelper.encode("UPDATE inventory SET quantity = 123456 WHERE item = 52");
                String supportCode = supportCodeBox.getText().toString();
                if (SupportCodeHelper.applyCode(supportCode)) {
                    ToastHelper.showToast(context, Toast.LENGTH_LONG, R.string.supportCodeComplete, true);
                } else {
                    ToastHelper.showToast(context, Toast.LENGTH_LONG, R.string.supportCodeFailed, true);
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

    public static void confirmPrestige(final Context context, Activity activity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog);
        alertDialog.setMessage(context.getString(R.string.prestigeQuestion));
        alertDialog.setIcon(R.drawable.levels);

        alertDialog.setPositiveButton(context.getString(R.string.prestigeConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                PrestigeHelper.prestigeAccount();
                ToastHelper.showToast(context, Toast.LENGTH_LONG, String.format(context.getString(R.string.prestigeComplete), Player_Info.getPrestige()), false);
            }
        });

        alertDialog.setNegativeButton(context.getString(R.string.prestigeCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public static void confirmVisitorAdd(final Context context, Activity activity) {
        final int visitorCost = VisitorHelper.getVisitorAddCost();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setMessage(String.format(context.getString(R.string.bribeQuestion), visitorCost));
        alertDialog.setIcon(R.drawable.item52);

        alertDialog.setPositiveButton(context.getString(R.string.bribeConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Inventory coinStock = Inventory.getInventory(Constants.ITEM_COINS, Constants.STATE_NORMAL);
                if (coinStock.getQuantity() >= visitorCost) {
                    coinStock.setQuantity(coinStock.getQuantity() - visitorCost);
                    coinStock.save();
                    if (VisitorHelper.tryCreateVisitor()) {
                        ToastHelper.showToast(context, Toast.LENGTH_SHORT, String.format(context.getString(R.string.bribeComplete), visitorCost), true);
                    }
                } else {
                    ToastHelper.showToast(context, Toast.LENGTH_SHORT, context.getString(R.string.bribeFailure), true);
                }
            }
        });

        alertDialog.setNegativeButton(context.getString(R.string.bribeCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public static void confirmVisitorDismiss(final Context context, final Visitor visitor, final VisitorActivity activity) {
        final int visitorCost = VisitorHelper.getVisitorDismissCost(visitor.getId());

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog);
        alertDialog.setMessage(String.format(context.getString(R.string.dismissQuestion), visitorCost));
        alertDialog.setIcon(R.drawable.item52);

        alertDialog.setPositiveButton(context.getString(R.string.dismissConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Inventory coinStock = Inventory.getInventory(Constants.ITEM_COINS, Constants.STATE_NORMAL);
                if (coinStock.getQuantity() >= visitorCost) {
                    coinStock.setQuantity(coinStock.getQuantity() - visitorCost);
                    coinStock.save();

                    VisitorHelper.removeVisitor(visitor);
                    SoundHelper.playSound(context, SoundHelper.walkingSounds);
                    ToastHelper.showToast(context, Toast.LENGTH_SHORT, context.getString(R.string.dismissComplete), true);
                    activity.finish();
                } else {
                    ToastHelper.showToast(context, Toast.LENGTH_SHORT, context.getString(R.string.dismissFailure), true);
                }
            }
        });

        alertDialog.setNegativeButton(context.getString(R.string.dismissCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public static void confirmTraderRestockAll(final Context context, final MarketActivity activity, final int restockCost) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog);
        alertDialog.setMessage(String.format(context.getString(R.string.traderRestockAllQuestion), restockCost));
        alertDialog.setIcon(R.drawable.item52);

        alertDialog.setPositiveButton(context.getString(R.string.traderRestockAllConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int traderResponse = Trader.restockAll(restockCost);
                if (traderResponse == Constants.SUCCESS) {
                    ToastHelper.showToast(context, Toast.LENGTH_SHORT, String.format(context.getString(R.string.traderRestockAllComplete), restockCost), true);
                } else {
                    ToastHelper.showToast(context, Toast.LENGTH_SHORT, ErrorHelper.errors.get(traderResponse), true);
                }
                activity.alertDialogCallback();
            }
        });

        alertDialog.setNegativeButton(context.getString(R.string.traderRestockAllCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

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
                    ToastHelper.showToast(context, Toast.LENGTH_SHORT, String.format(context.getString(R.string.traderRestockComplete), restockCost), true);
                } else {
                    ToastHelper.showToast(context, Toast.LENGTH_SHORT, ErrorHelper.errors.get(traderResponse), true);
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
                    ToastHelper.showToast(context, Toast.LENGTH_SHORT, ErrorHelper.errors.get(buyResponse), false);
                }
                activity.alertDialogCallback();
            }
        });

        alertDialog.setNegativeButton(context.getString(R.string.itemBuyAllConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int itemsBought = 0;
                int buyResponse = Constants.ERROR_NO_ITEMS;
                boolean successful = true;

                while (successful) {
                    buyResponse = Inventory.buyItem(itemStock);
                    if (buyResponse == Constants.SUCCESS) {
                        itemsBought++;
                    } else {
                        successful = false;
                    }
                }

                if (itemsBought > 0) {
                    ToastHelper.showToast(context, Toast.LENGTH_SHORT, String.format(context.getString(R.string.itemBuyComplete), itemsBought, itemName, itemValue * itemsBought), false);
                    Player_Info.increaseByX(Player_Info.Statistic.ItemsBought, itemsBought);
                    trader.setPurchases(trader.getPurchases() + itemsBought);
                    trader.save();
                } else {
                    ToastHelper.showToast(context, Toast.LENGTH_SHORT, ErrorHelper.errors.get(buyResponse), false);
                }
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
}

