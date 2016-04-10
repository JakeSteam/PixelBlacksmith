package uk.co.jakelee.blacksmith.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.main.TraderActivity;
import uk.co.jakelee.blacksmith.main.VisitorActivity;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Trader;
import uk.co.jakelee.blacksmith.model.Trader_Stock;
import uk.co.jakelee.blacksmith.model.Visitor;

public class AlertDialogHelper {
    public static void confirmPrestige(final Context context, Activity activity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog);
        alertDialog.setMessage(context.getString(R.string.prestigeQuestion));
        alertDialog.setIcon(R.drawable.levels);

        alertDialog.setPositiveButton(context.getString(R.string.prestigeConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                PrestigeHelper.prestigeAccount();
                ToastHelper.showToast(context, Toast.LENGTH_LONG, String.format(context.getString(R.string.prestigeComplete), Player_Info.getPrestige()));
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
                        ToastHelper.showToast(context, Toast.LENGTH_SHORT, String.format(context.getString(R.string.bribeComplete), visitorCost));
                    }
                } else {
                    ToastHelper.showToast(context, Toast.LENGTH_SHORT, context.getString(R.string.bribeFailure));
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
                    ToastHelper.showToast(context, Toast.LENGTH_SHORT, context.getString(R.string.dismissComplete));
                    activity.finish();
                } else {
                    ToastHelper.showToast(context, Toast.LENGTH_SHORT, context.getString(R.string.dismissFailure));
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
                    ToastHelper.showToast(context, Toast.LENGTH_SHORT, String.format(context.getString(R.string.itemBuyComplete), quantity, itemName, itemValue));
                    Player_Info.increaseByOne(Player_Info.Statistic.ItemsBought);
                    trader.setPurchases(trader.getPurchases() + quantity);
                    trader.save();
                } else {
                    ToastHelper.showToast(context, Toast.LENGTH_SHORT, ErrorHelper.errors.get(buyResponse));
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
                    ToastHelper.showToast(context, Toast.LENGTH_SHORT, String.format(context.getString(R.string.itemBuyComplete), itemsBought, itemName, itemValue * itemsBought));
                    Player_Info.increaseByX(Player_Info.Statistic.ItemsBought, itemsBought);
                    trader.setPurchases(trader.getPurchases() + itemsBought);
                    trader.save();
                } else {
                    ToastHelper.showToast(context, Toast.LENGTH_SHORT, ErrorHelper.errors.get(buyResponse));
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

