package uk.co.jakelee.blacksmith.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.main.MainActivity;
import uk.co.jakelee.blacksmith.main.TraderActivity;
import uk.co.jakelee.blacksmith.main.VisitorActivity;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Trader;
import uk.co.jakelee.blacksmith.model.Visitor;

public class AlertDialogHelper {
    public static void confirmVisitorAdd(final Context context, MainActivity activity) {
        final int visitorCost = VisitorHelper.getVisitorAddCost();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setMessage(String.format("Would you like to bribe a visitor %d coins to come in immediately?", visitorCost));
        alertDialog.setIcon(R.drawable.item52);

        alertDialog.setPositiveButton("Bribe", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Inventory coinStock = Inventory.getInventory(Constants.ITEM_COINS, Constants.STATE_NORMAL);
                if (coinStock.getQuantity() >= visitorCost) {
                    coinStock.setQuantity(coinStock.getQuantity() - visitorCost);
                    coinStock.save();
                    if (VisitorHelper.tryCreateVisitor()) {
                        ToastHelper.showToast(context, Toast.LENGTH_SHORT, String.format("A visitor walks in, with %d coins. What a coincidence!", visitorCost));
                    }
                } else {
                    ToastHelper.showToast(context, Toast.LENGTH_SHORT, "Not enough money to bribe a visitor.");
                }
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public static void confirmVisitorDismiss(final Context context, final Visitor visitor, final VisitorActivity activity) {
        final int visitorCost = VisitorHelper.getVisitorDismissCost(visitor.getId());

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog);
        alertDialog.setMessage(String.format("Would you like to pay a visitor %d coins to leave immediately?", visitorCost));
        alertDialog.setIcon(R.drawable.item52);

        alertDialog.setPositiveButton("Bribe", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Inventory coinStock = Inventory.getInventory(Constants.ITEM_COINS, Constants.STATE_NORMAL);
                if (coinStock.getQuantity() >= visitorCost) {
                    coinStock.setQuantity(coinStock.getQuantity() - visitorCost);
                    coinStock.save();

                    VisitorHelper.removeVisitor(visitor);
                    SoundHelper.playSound(context, SoundHelper.walkingSounds);
                    ToastHelper.showToast(context, Toast.LENGTH_SHORT, "The visitor leaves, a little bit grumpily.");
                    activity.finish();
                } else {
                    ToastHelper.showToast(context, Toast.LENGTH_SHORT, "Not enough money to bribe a visitor.");
                }
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public static void confirmItemBuy(final Context context, final TraderActivity activity, final Trader trader, long itemID, final int itemStock) {
        final Item item = Item.findById(Item.class, itemID);
        final int itemValue = item.getValue();
        final String itemName = item.getFullName(Constants.STATE_NORMAL);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, android.R.style.Theme_DeviceDefault_Light_Dialog);
        alertDialog.setMessage(String.format("Would you like to buy 1x %1s for %2d coin(s), or up to %3d for %4d coin(s) each?", itemName, itemValue, itemStock, itemValue));
        alertDialog.setIcon(R.drawable.item52);

        alertDialog.setPositiveButton("Buy 1", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int quantity = 1;

                int buyResponse = Inventory.buyItem(item.getId(), Constants.STATE_NORMAL, trader.getId(), item.getValue());
                if (buyResponse == Constants.SUCCESS) {
                    ToastHelper.showToast(context, Toast.LENGTH_SHORT, String.format("Added %1sx %2s to pending buying for %3s coin(s)", quantity, itemName, itemValue));
                    Player_Info.increaseByOne(Player_Info.Statistic.ItemsBought);
                    trader.setPurchases(trader.getPurchases() + quantity);
                    trader.save();
                } else {
                    ToastHelper.showToast(context, Toast.LENGTH_SHORT, ErrorHelper.errors.get(buyResponse));
                }
            }
        });

        alertDialog.setNegativeButton("Buy Max", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int itemsBought = 0;
                int buyResponse = Constants.ERROR_NO_ITEMS;
                boolean successful = true;

                while (successful) {
                    buyResponse = Inventory.buyItem(item.getId(), Constants.STATE_NORMAL, trader.getId(), item.getValue());
                    if (buyResponse == Constants.SUCCESS) {
                        itemsBought++;
                    } else {
                        successful = false;
                    }
                }

                if (itemsBought > 0) {
                    ToastHelper.showToast(context, Toast.LENGTH_SHORT, String.format("Added %1sx %2s to pending buying for %3s coin(s)", itemsBought, itemName, itemValue * itemsBought));
                    Player_Info.increaseByX(Player_Info.Statistic.ItemsBought, itemsBought);
                    trader.setPurchases(trader.getPurchases() + itemsBought);
                    trader.save();
                } else {
                    ToastHelper.showToast(context, Toast.LENGTH_SHORT, ErrorHelper.errors.get(buyResponse));
                }
            }
        });

        alertDialog.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }
}

