package uk.co.jakelee.blacksmith.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import uk.co.jakelee.blacksmith.BuildConfig;
import uk.co.jakelee.blacksmith.R;
import uk.co.jakelee.blacksmith.main.InventoryActivity;
import uk.co.jakelee.blacksmith.main.MainActivity;
import uk.co.jakelee.blacksmith.main.MarketActivity;
import uk.co.jakelee.blacksmith.main.AssistantActivity;
import uk.co.jakelee.blacksmith.main.TraderActivity;
import uk.co.jakelee.blacksmith.main.UpgradeActivity;
import uk.co.jakelee.blacksmith.main.VisitorActivity;
import uk.co.jakelee.blacksmith.main.WorkerActivity;
import uk.co.jakelee.blacksmith.model.Assistant;
import uk.co.jakelee.blacksmith.model.Hero;
import uk.co.jakelee.blacksmith.model.Hero_Adventure;
import uk.co.jakelee.blacksmith.model.Inventory;
import uk.co.jakelee.blacksmith.model.Item;
import uk.co.jakelee.blacksmith.model.Location;
import uk.co.jakelee.blacksmith.model.Pending_Inventory;
import uk.co.jakelee.blacksmith.model.Player_Info;
import uk.co.jakelee.blacksmith.model.Slot;
import uk.co.jakelee.blacksmith.model.Super_Upgrade;
import uk.co.jakelee.blacksmith.model.Trader;
import uk.co.jakelee.blacksmith.model.Trader_Stock;
import uk.co.jakelee.blacksmith.model.Upgrade;
import uk.co.jakelee.blacksmith.model.Visitor;
import uk.co.jakelee.blacksmith.model.Visitor_Stats;
import uk.co.jakelee.blacksmith.model.Visitor_Type;
import uk.co.jakelee.blacksmith.model.Worker;

public class AlertDialogHelper {
    public static void enterSupportCode(final Context context, final Activity activity) {
        final EditText supportCodeBox = new EditText(context);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AppTheme_Dialog);
        alertDialog.setMessage(context.getString(R.string.supportCodeQuestion));
        alertDialog.setView(supportCodeBox);

        alertDialog.setPositiveButton(context.getString(R.string.supportCodeConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                //String supportCode = SupportCodeHelper.encode((System.currentTimeMillis() + 259200000) + "|UPDATE playerinfo set int_value = 504100 WHERE name = 'XP';UPDATE playerinfo set int_value = 1 WHERE name = 'Premium';UPDATE slot SET premium = 0 WHERE level = 9999; UPDATE upgrade SET current = current + 20, maximum = maximum + 50 WHERE name IN ('Coins Bonus', 'XP Bonus')");
                String supportCode = supportCodeBox.getText().toString().trim();

                Log.d("Code", supportCode);
                if (SupportCodeHelper.applyCode(supportCode)) {
                    ToastHelper.showPositiveToast(activity.findViewById(R.id.settings), ToastHelper.LONG, activity.getString(R.string.supportCodeComplete), true);
                } else {
                    ToastHelper.showErrorToast(activity.findViewById(R.id.settings), ToastHelper.LONG, activity.getString(R.string.supportCodeFailed), true);
                }
            }
        });

        alertDialog.setNegativeButton(context.getString(R.string.supportCodeCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final Dialog dialog = alertDialog.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.show();
        dialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public static void displayEventInfo(final Activity activity) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AppTheme_Dialog);
        alertDialog.setMessage(R.string.eventText);

        alertDialog.setPositiveButton(activity.getString(R.string.updateClose), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final Dialog dialog = alertDialog.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.show();
        dialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public static void enterAssistantName(final AssistantActivity activity, final Assistant assistant) {
        final EditText nameBox = new EditText(activity);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AppTheme_Dialog);
        alertDialog.setMessage(String.format(Locale.ENGLISH, activity.getString(R.string.assistantEditNamePrompt),
                assistant.getName().equals("") ? assistant.getTypeName(activity) : assistant.getName()));
        alertDialog.setView(nameBox);

        alertDialog.setPositiveButton(activity.getString(R.string.supportCodeConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String name = nameBox.getText().toString().trim();
                if (name.length() > 30) {
                    name = name.substring(0, 30) + "...";
                }
                assistant.setName(name);
                assistant.save();
            }
        });

        alertDialog.setNegativeButton(activity.getString(R.string.supportCodeCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final Dialog dialog = alertDialog.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.show();
        dialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public static void confirmUpgrade(final Context context, final UpgradeActivity activity, final Upgrade upgrade) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AppTheme_Dialog);
        alertDialog.setMessage(String.format(context.getString(R.string.upgradeQuestion),
                upgrade.getName(context),
                (upgrade.increases() ? upgrade.getCurrent() + upgrade.getIncrement() : upgrade.getCurrent() - upgrade.getIncrement()),
                upgrade.getMaximum(),
                upgrade.getUpgradeCost()));
        alertDialog.setIcon(R.drawable.item52);

        alertDialog.setPositiveButton(context.getString(R.string.upgradeConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int upgradeResponse = upgrade.tryUpgrade();
                if (upgradeResponse == Constants.SUCCESS) {
                    ToastHelper.showPositiveToast(activity.findViewById(R.id.upgradeTitle), ToastHelper.SHORT, String.format(context.getString(R.string.upgradeSuccess), upgrade.getName(context)), true);
                    Player_Info.increaseByOne(Player_Info.Statistic.UpgradesBought);
                    activity.alertDialogCallback();
                } else {
                    ToastHelper.showErrorToast(activity.findViewById(R.id.upgradeTitle), ToastHelper.SHORT, activity.getString(ErrorHelper.errors.get(upgradeResponse)), true);
                }
            }
        });

        alertDialog.setNegativeButton(context.getString(R.string.upgradeCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final Dialog dialog = alertDialog.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.show();
        dialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public static void openSocialMedia(final Context context, final Activity activity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AppTheme_Dialog);
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

        final Dialog dialog = alertDialog.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.show();
        dialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public static void confirmBuyHero(final Context context, final WorkerActivity activity, final Hero hero) {
        final int buyCost = WorkerHelper.getBuyCost(hero);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AppTheme_Dialog);
        alertDialog.setMessage(String.format(context.getString(R.string.buyHeroQuestion), buyCost));

        alertDialog.setPositiveButton(context.getString(R.string.buyHeroConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Inventory coinStock = Inventory.getInventory(Constants.ITEM_COINS, Constants.STATE_NORMAL);
                if (coinStock.getQuantity() >= buyCost) {
                    coinStock.setQuantity(coinStock.getQuantity() - buyCost);
                    coinStock.save();

                    hero.setPurchased(true);
                    hero.save();
                    ToastHelper.showPositiveToast(activity.findViewById(R.id.workerTitle), ToastHelper.LONG, context.getString(R.string.buyHeroComplete), true);
                    activity.scheduledTask();
                } else {
                    ToastHelper.showErrorToast(activity.findViewById(R.id.workerTitle), ToastHelper.SHORT, activity.getString(R.string.error_not_enough_coins), false);
                }
            }
        });

        alertDialog.setNegativeButton(context.getString(R.string.buyWorkerCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final Dialog dialog = alertDialog.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.show();
        dialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public static void confirmBuyWorker(final Context context, final WorkerActivity activity, final Worker worker) {
        final int buyCost = WorkerHelper.getBuyCost(worker);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AppTheme_Dialog);
        alertDialog.setMessage(String.format(context.getString(R.string.buyWorkerQuestion), buyCost));

        alertDialog.setPositiveButton(context.getString(R.string.buyWorkerConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Inventory coinStock = Inventory.getInventory(Constants.ITEM_COINS, Constants.STATE_NORMAL);
                if (coinStock.getQuantity() >= buyCost) {
                    coinStock.setQuantity(coinStock.getQuantity() - buyCost);
                    coinStock.save();

                    worker.setPurchased(true);
                    worker.save();
                    ToastHelper.showPositiveToast(activity.findViewById(R.id.workerTitle), ToastHelper.LONG, context.getString(R.string.buyWorkerComplete), true);
                    activity.scheduledTask();
                } else {
                    ToastHelper.showErrorToast(activity.findViewById(R.id.workerTitle), ToastHelper.SHORT, activity.getString(R.string.error_not_enough_coins), false);
                }
            }
        });

        alertDialog.setNegativeButton(context.getString(R.string.buyWorkerCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final Dialog dialog = alertDialog.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.show();
        dialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public static void confirmPrestige(final Context context, final Activity activity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AppTheme_Dialog);
        alertDialog.setMessage(context.getString(R.string.prestigeQuestion));
        alertDialog.setIcon(R.drawable.levels);

        alertDialog.setPositiveButton(context.getString(R.string.prestigeConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                PrestigeHelper.prestigeAccount();
                ToastHelper.showPositiveToast(activity.findViewById(R.id.settings), ToastHelper.LONG, String.format(context.getString(R.string.prestigeComplete),
                        Player_Info.getPrestige() * 50,
                        (int) (100 * (1 - Math.pow(0.75, Player_Info.getPrestige())))), false);
            }
        });

        alertDialog.setNegativeButton(context.getString(R.string.prestigeCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final Dialog dialog = alertDialog.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.show();
        dialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public static void confirmVisitorAdd(final Context context, final MainActivity activity) {
        final int visitorCost = VisitorHelper.getVisitorAddCost();
        int questionString = Player_Info.displayAds() ? R.string.bribeQuestionAdvert : R.string.bribeQuestion;

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AppTheme_Dialog);
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
                        ToastHelper.showPositiveToast(null, ToastHelper.SHORT, String.format(context.getString(R.string.bribeComplete), visitorCost), true);
                    }
                } else {
                    ToastHelper.showErrorToast(null, ToastHelper.SHORT, context.getString(R.string.bribeFailure), true);
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

        final Dialog dialog = alertDialog.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.show();
        dialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public static void confirmVisitorDismiss(final Context context, final Visitor visitor, final VisitorActivity activity) {
        final int visitorCost = VisitorHelper.getVisitorDismissCost(visitor.getId());
        int questionID = Player_Info.displayAds() ? R.string.dismissQuestionAdvert : R.string.dismissQuestion;

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AppTheme_Dialog);
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
                    ToastHelper.showPositiveToast(activity.findViewById(R.id.visitor), ToastHelper.SHORT, activity.getString(R.string.dismissComplete), true);
                    activity.finish();
                } else {
                    ToastHelper.showErrorToast(activity.findViewById(R.id.visitor), ToastHelper.SHORT, context.getString(R.string.dismissFailure), true);
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

        final Dialog dialog = alertDialog.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.show();
        dialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public static void confirmTraderRestockAll(final Context context, final MarketActivity activity, final int restockCost) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AppTheme_Dialog);
        String question = String.format(Player_Info.displayAds() ?
                context.getString(R.string.traderRestockAllQuestionAdvert) :
                context.getString(R.string.traderRestockAllQuestion), restockCost);
        alertDialog.setMessage(question);
        alertDialog.setIcon(R.drawable.item52);

        alertDialog.setPositiveButton(context.getString(R.string.traderRestockAllConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int traderResponse = Trader.restockAll(restockCost);
                if (traderResponse == Constants.SUCCESS) {
                    ToastHelper.showPositiveToast(activity.findViewById(R.id.marketTitle), ToastHelper.SHORT, String.format(context.getString(R.string.traderRestockAllComplete), restockCost), true);
                } else {
                    ToastHelper.showErrorToast(activity.findViewById(R.id.marketTitle), ToastHelper.SHORT, activity.getString(ErrorHelper.errors.get(traderResponse)), true);
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

        final Dialog dialog = alertDialog.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.show();
        dialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public static void confirmTraderLock(final TraderActivity activity, Trader trader) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AppTheme_Dialog);
        alertDialog.setMessage(String.format(activity.getString(trader.isFixed() ? R.string.unlockConfirm : R.string.lockConfirm),
                trader.getName(activity),
                Constants.TRADER_LOCK_COST));

        alertDialog.setPositiveButton(activity.getString(trader.isFixed() ? R.string.unlock : R.string.lock), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                activity.toggleTraderLock();
            }
        });

        alertDialog.setNegativeButton(activity.getString(R.string.lockCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final Dialog dialog = alertDialog.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.show();
        dialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public static void confirmTraderRestock(final Context context, final TraderActivity activity, final Trader trader, final int restockCost) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AppTheme_Dialog);
        alertDialog.setMessage(Player_Info.displayAds() ?
                        String.format(context.getString(R.string.traderRestockQuestionAdvert), trader.getName(context), restockCost) :
                        String.format(context.getString(R.string.traderRestockQuestion), trader.getName(context), restockCost));
        alertDialog.setIcon(R.drawable.item52);

        alertDialog.setPositiveButton(context.getString(R.string.traderRestockConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int traderResponse = trader.restock(restockCost);
                if (traderResponse == Constants.SUCCESS) {
                    ToastHelper.showPositiveToast(activity.findViewById(R.id.trader), ToastHelper.SHORT, String.format(context.getString(R.string.traderRestockComplete), restockCost), true);
                } else {
                    ToastHelper.showErrorToast(activity.findViewById(R.id.trader), ToastHelper.SHORT, activity.getString(ErrorHelper.errors.get(traderResponse)), true);
                }
                activity.alertDialogCallback();
            }
        });

        alertDialog.setNeutralButton(context.getString(R.string.traderRestockCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        if (Player_Info.displayAds()) {
            alertDialog.setNegativeButton(context.getString(R.string.traderRestockConfirmAdvert), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    AdvertHelper.getInstance(context).showAdvert(activity, AdvertHelper.advertPurpose.ConvTraderRestock);
                }
            });
        }

        final Dialog dialog = alertDialog.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.show();
        dialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public static void confirmBuyAssistant(final Context context, final AssistantActivity activity, final Assistant assistant) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AppTheme_Dialog);
        alertDialog.setMessage(String.format(Locale.ENGLISH, activity.getString(R.string.assistantBuyQuestion), assistant.getTypeName(activity), assistant.getCoinsRequired()));

        alertDialog.setPositiveButton(context.getString(R.string.buy), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (assistant.getCoinsRequired() <= Inventory.getCoins()) {
                    Inventory coinStock = Inventory.getInventory(Constants.ITEM_COINS, Constants.STATE_NORMAL);
                    coinStock.setQuantity(coinStock.getQuantity() - assistant.getCoinsRequired());
                    coinStock.save();
                    assistant.setCurrentXp(Assistant.getXpForLevel(assistant.getLevelModifier(), 1));
                    assistant.setObtained(System.currentTimeMillis());
                    assistant.save();

                    activity.unlockAssistant();
                }
            }
        });

        alertDialog.setNegativeButton(context.getString(R.string.itemBuyCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final Dialog dialog = alertDialog.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.show();
        dialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public static void confirmBonusAdvert(final Context context, final MainActivity activity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AppTheme_Dialog);
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

        final Dialog dialog = alertDialog.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.show();
        dialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public static void displayCompletion(final Context context, final Activity activity) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AppTheme_Dialog);
        alertDialog.setMessage(String.format(activity.getString(R.string.completion_breakdown),
                Player_Info.getPlayerLevel(), Constants.PRESTIGE_LEVEL_REQUIRED,
                Select.from(Player_Info.class).where(Condition.prop("name").eq("UpgradesBought")).first().getIntValue(), Upgrade.getMaximumUpgrades(),
                Select.from(Trader.class).where(Condition.prop("level").lt(Player_Info.getPlayerLevel() + 1)).count(), Trader.count(Trader.class),
                Slot.getUnlockedCount(), Slot.count(Slot.class) - (int) Location.count(Location.class),
                Trader_Stock.getUnlockedCount(), Trader_Stock.count(Trader_Stock.class),
                Inventory.findWithQuery(Inventory.class, "SELECT * FROM inventory GROUP BY item").size(), Item.count(Item.class),
                Visitor_Type.getTotalPreferencesDiscovered(), Visitor_Type.count(Visitor_Type.class) * 3,
                Select.from(Visitor_Stats.class).where(Condition.prop("trophy_achieved").gt(0)).count(), Visitor_Stats.count(Visitor_Stats.class),
                Select.from(Worker.class).where(Condition.prop("purchased").eq(1)).count(), Worker.count(Worker.class),
                Visitor_Type.getAdventureAttempts().second, Hero_Adventure.count(Hero_Adventure.class),
                Select.from(Assistant.class).where(Condition.prop("obtained").gt(0)).count(), Assistant.count(Assistant.class),
                Player_Info.getPrestige(), Player_Info.getPrestige() * 100));

        alertDialog.setPositiveButton(context.getString(R.string.updateClose), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final Dialog dialog = alertDialog.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.show();
        ((TextView)dialog.findViewById(android.R.id.message)).setGravity(Gravity.RIGHT);
        dialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public static void confirmPageExchange(final Context context, final InventoryActivity activity, final View view,  final Inventory inventory, final Item item) {
        final int maxNewPages = inventory.getQuantity() / Constants.PAGE_EXCHANGE_QTY;

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AppTheme_Dialog);
        alertDialog.setMessage(String.format(activity.getString(R.string.exchangePagesQuestion),
                Constants.PAGE_EXCHANGE_QTY,
                item.getName(context),
                maxNewPages * Constants.PAGE_EXCHANGE_QTY,
                maxNewPages));

        alertDialog.setPositiveButton(context.getString(R.string.exchangePagesOne), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String name = Inventory.exchangePages(activity, inventory, 1);
                ToastHelper.showPositiveToast(view, ToastHelper.SHORT, String.format(context.getString(R.string.exchangePagesSuccess),
                        Constants.PAGE_EXCHANGE_QTY,
                        item.getName(context),
                        1,
                        name), true);
                activity.callback();
            }
        });

        alertDialog.setNegativeButton(context.getString(R.string.exchangePagesAll), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String name = Inventory.exchangePages(activity, inventory, maxNewPages);
                ToastHelper.showPositiveToast(view, ToastHelper.SHORT, String.format(context.getString(R.string.exchangePagesSuccess),
                        maxNewPages * Constants.PAGE_EXCHANGE_QTY,
                        item.getName(context),
                        maxNewPages,
                        name), true);
                activity.callback();
            }
        });

        alertDialog.setNeutralButton(context.getString(R.string.exchangePagesCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final Dialog dialog = alertDialog.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.show();
        dialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public static void confirmWorseLocalLoad(final Activity activity, int localPrestige, int localXP, int filePrestige, int fileXP) {
        int localLevel = Player_Info.convertXpToLevel(localXP);
        int fileLevel = Player_Info.convertXpToLevel(fileXP);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AppTheme_Dialog);
        alertDialog.setMessage(String.format(activity.getString(R.string.worseLocalSaveMessage),
                localPrestige,
                localLevel,
                localXP,
                filePrestige,
                fileLevel,
                fileXP));

        alertDialog.setPositiveButton(activity.getString(R.string.worseLocalSaveLoad), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String readResult = StorageHelper.loadLocalSave(activity, false);
                if (readResult.startsWith("PixelBlacksmith")) {
                    ToastHelper.showPositiveToast(null, ToastHelper.LONG, activity.getString(R.string.saveImportLoadSuccess), true);
                } else {
                    ToastHelper.showErrorToast(null, ToastHelper.LONG, String.format(activity.getString(R.string.saveImportFailure),
                            readResult), true);
                }
            }
        });

        alertDialog.setNegativeButton(activity.getString(R.string.worseLocalSaveCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Dialog dialog = alertDialog.create();
                dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                dialog.show();
                dialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            }
        });
    }

    public static void confirmWorseCloudLoad(final Context context, final Activity activity, int localPrestige, int localXP, int cloudPrestige, int cloudXP) {
        int localLevel = Player_Info.convertXpToLevel(localXP);
        int cloudLevel = Player_Info.convertXpToLevel(cloudXP);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AppTheme_Dialog);
        alertDialog.setMessage(String.format(context.getString(R.string.worseSaveMessage),
                localPrestige,
                localLevel,
                localXP,
                cloudPrestige,
                cloudLevel,
                cloudXP));

        alertDialog.setPositiveButton(context.getString(R.string.worseSaveLoad), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                GooglePlayHelper.forceLoadFromCloud();
            }
        });

        alertDialog.setNegativeButton(context.getString(R.string.worseSaveCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Dialog dialog = alertDialog.create();
                dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                dialog.show();
                dialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            }
        });
    }

    public static void confirmCloudSave(final Context context, final Activity activity, String desc, long saveTime, String deviceName) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AppTheme_Dialog);
        alertDialog.setMessage(String.format(context.getString(R.string.cloudSaveWarning),
                desc,
                DateHelper.displayTime(saveTime, DateHelper.datetime),
                deviceName));

        alertDialog.setPositiveButton(context.getString(R.string.cloudSaveSave), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                GooglePlayHelper.forceSaveToCloud();
            }
        });

        alertDialog.setNegativeButton(context.getString(R.string.cloudSaveCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final Dialog dialog = alertDialog.create();
                dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
                dialog.show();
                dialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            }
        });
    }

    public static void displayUpdateMessage(final Context context, final MainActivity activity) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AppTheme_Dialog);
        alertDialog.setMessage(String.format(context.getString(R.string.updateMessage), BuildConfig.VERSION_NAME));

        alertDialog.setPositiveButton(context.getString(R.string.updateReddit), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.reddit.com/r/PixelBlacksmith/"));
                context.startActivity(browserIntent);
            }
        });

        alertDialog.setNegativeButton(context.getString(R.string.updateClose), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final Dialog dialog = alertDialog.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.show();
        dialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public static void confirmItemBuy(final Context context, final TraderActivity activity, final Trader_Stock itemStock, boolean isFixed) {
        final Item item = Item.findById(Item.class, itemStock.getItemID());
        int itemValue = item.getValue() / (item.getValue() > 1 && Super_Upgrade.isEnabled(Constants.SU_HALF_MARKET_COST) ? 2 : 1);
        final int finalItemValue = isFixed ? (int)Math.ceil((double)itemValue * Constants.TRADER_LOCK_PRICE_MODIFIER) : itemValue;
        final String itemName = item.getFullName(context, Constants.STATE_NORMAL);
        final Trader trader = Trader.findById(Trader.class, itemStock.getTraderType());
        final int itemsToSell = (int)Select.from(Trader_Stock.class).where(
                Condition.prop("trader_type").eq(trader.getId()),
                Condition.prop("required_purchases").lt(trader.getPurchases() + 1),
                Condition.prop("stock").gt(0)).count();

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AppTheme_Dialog);
        alertDialog.setMessage(String.format(context.getString(R.string.itemBuyQuestion), itemName, finalItemValue, itemStock.getStock(), finalItemValue));
        alertDialog.setIcon(R.drawable.item52);

        alertDialog.setPositiveButton(context.getString(R.string.itemBuy1Confirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int quantity = Super_Upgrade.isEnabled(Constants.SU_TRADER_STOCK) ? 2 : 1;

                int buyResponse = Inventory.buyItem(itemStock);
                if (buyResponse == Constants.SUCCESS) {
                    ToastHelper.showToast(activity.findViewById(R.id.trader), ToastHelper.SHORT, String.format(context.getString(R.string.itemBuyComplete), quantity, itemName, finalItemValue), false);
                    Player_Info.increaseByOne(Player_Info.Statistic.ItemsBought);
                    GooglePlayHelper.UpdateEvent(Constants.EVENT_BOUGHT_ITEM, 1);
                    if (itemStock.getStock() == 1 && itemsToSell == 1) {
                        GooglePlayHelper.UpdateEvent(Constants.EVENT_BUY_ALL_ITEM, 1);
                    }
                    trader.setPurchases(trader.getPurchases() + quantity);
                    trader.save();
                } else {
                    ToastHelper.showErrorToast(activity.findViewById(R.id.trader), ToastHelper.SHORT, activity.getString(ErrorHelper.errors.get(buyResponse)), false);
                }
                activity.alertDialogCallback();
            }
        });

        alertDialog.setNegativeButton(context.getString(R.string.itemBuyAllConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int itemsBought = 0;
                int buyResponse = Constants.ERROR_NOT_ENOUGH_COINS;
                List<Pair<Long, Integer>> items = new ArrayList<>();

                Inventory coinStock = Inventory.getInventory(Constants.ITEM_COINS, Constants.STATE_NORMAL);
                int totalCost = itemStock.getStock() * finalItemValue;

                if (totalCost <= coinStock.getQuantity()) {
                    int itemsToBuy = (Super_Upgrade.isEnabled(Constants.SU_TRADER_STOCK) ? 2 : 1 ) * itemStock.getStock();
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
                    ToastHelper.showToast(activity.findViewById(R.id.trader), ToastHelper.SHORT, String.format(context.getString(R.string.itemBuyComplete), itemsBought, itemName, totalCost), false);
                    Player_Info.increaseByX(Player_Info.Statistic.ItemsBought, itemsBought);
                    GooglePlayHelper.UpdateEvent(Constants.EVENT_BOUGHT_ITEM, itemsBought);
                    if (itemsToSell == 1) {
                        // If this is the only item currently being sold
                        GooglePlayHelper.UpdateEvent(Constants.EVENT_BUY_ALL_ITEM, 1);
                    }
                    trader.setPurchases(trader.getPurchases() + itemsBought);
                    trader.save();
                } else {
                    ToastHelper.showErrorToast(activity.findViewById(R.id.trader), ToastHelper.SHORT, activity.getString(ErrorHelper.errors.get(buyResponse)), false);
                }

                Pending_Inventory.addScheduledItems(Constants.LOCATION_MARKET, items);
                activity.alertDialogCallback();
            }
        });

        alertDialog.setNeutralButton(context.getString(R.string.itemBuyCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final Dialog dialog = alertDialog.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.show();
        dialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public static void confirmItemBuyAll(final TraderActivity activity, final Trader trader) {
        final List<Trader_Stock> itemStocks = Select.from(Trader_Stock.class).where(
                Condition.prop("trader_type").eq(trader.getId()),
                Condition.prop("stock").gt(0),
                Condition.prop("required_purchases").lt(trader.getPurchases() + 1)).list();

        if (itemStocks.size() == 0) {
            ToastHelper.showToast(activity.findViewById(R.id.trader), ToastHelper.SHORT, activity.getString(R.string.itemBuyAllNoItems), false);
            return;
        }

        int totalValue = 0;
        int itemCount = 0;
        for (Trader_Stock itemStock : itemStocks) {
            Item item = Item.findById(Item.class, itemStock.getItemID());
            totalValue += (item.getModifiedValue(itemStock.getState()) * itemStock.getStock());
            itemCount += itemStock.getStock();
        }
        totalValue = totalValue / (Super_Upgrade.isEnabled(Constants.SU_HALF_MARKET_COST) ? 2 : 1);
        final int finalTotalValue = trader.isFixed() ? (int)Math.ceil((double)totalValue * Constants.TRADER_LOCK_PRICE_MODIFIER) : totalValue;

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AppTheme_Dialog);
        alertDialog.setMessage(String.format(activity.getString(R.string.itemBuyAllQuestion), itemCount, finalTotalValue));
        alertDialog.setIcon(R.drawable.item52);

        alertDialog.setPositiveButton(activity.getString(R.string.itemBuyAllConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int itemsBought = 0;
                int buyResponse = Constants.ERROR_NOT_ENOUGH_COINS;
                boolean successful = true;
                List<Pair<Long, Integer>> items = new ArrayList<>();

                for (Trader_Stock itemStock : itemStocks) {
                    Inventory coinStock = Inventory.getInventory(Constants.ITEM_COINS, Constants.STATE_NORMAL);
                    Item item = Item.findById(Item.class, itemStock.getItemID());
                    int totalCost = (item.getModifiedValue(itemStock.getState()) * itemStock.getStock());
                    totalCost = trader.isFixed() ? (int)Math.ceil((double)totalCost * Constants.TRADER_LOCK_PRICE_MODIFIER) : totalCost;
                    totalCost = totalCost / ((totalCost > 1 && Super_Upgrade.isEnabled(Constants.SU_HALF_MARKET_COST)) ? 2 : 1);

                    if (totalCost <= coinStock.getQuantity() && successful) {
                        int itemsToBuy = (Super_Upgrade.isEnabled(Constants.SU_TRADER_STOCK) ? 2 : 1 ) * itemStock.getStock();
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
                    ToastHelper.showPositiveToast(activity.findViewById(R.id.trader), ToastHelper.SHORT, String.format(activity.getString(R.string.itemBuyAllComplete), itemsBought), false);
                    Player_Info.increaseByX(Player_Info.Statistic.ItemsBought, itemsBought);
                    GooglePlayHelper.UpdateEvent(Constants.EVENT_BUY_ALL_ITEM, 1);
                    GooglePlayHelper.UpdateEvent(Constants.EVENT_BOUGHT_ITEM, itemsBought);
                    trader.setPurchases(trader.getPurchases() + itemsBought);
                    trader.save();
                } else {
                    ToastHelper.showErrorToast(activity.findViewById(R.id.trader), ToastHelper.SHORT, activity.getString(ErrorHelper.errors.get(buyResponse)), false);
                }

                Pending_Inventory.addScheduledItems(Constants.LOCATION_MARKET, items);
                activity.alertDialogCallback();
            }
        });

        alertDialog.setNegativeButton(activity.getString(R.string.itemBuyCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final Dialog dialog = alertDialog.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.show();
        dialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }

    public static void confirmMarketBuyAll(final MarketActivity activity) {
        final List<Trader_Stock> itemStocks = Trader_Stock.findWithQuery(Trader_Stock.class, "SELECT ts.id, ts.default_stock, ts.item_id, ts.required_purchases, ts.state, ts.stock, ts.trader_type "
                + "FROM traderstock AS ts "
                + "INNER JOIN trader AS t ON ts.trader_type = t.id "
                + "WHERE t.status = 1 "
                + "AND ts.required_purchases <= t.purchases "
                + "AND ts.stock > 0");

        if (itemStocks.size() == 0) {
            ToastHelper.showToast(activity.findViewById(R.id.trader), ToastHelper.SHORT, activity.getString(R.string.itemBuyAllNoItems), false);
            return;
        }

        int totalValue = 0;
        int itemCount = 0;
        for (Trader_Stock itemStock : itemStocks) {
            Item item = Item.findById(Item.class, itemStock.getItemID());
            totalValue += (item.getModifiedValue(itemStock.getState()) * itemStock.getStock());
            itemCount += itemStock.getStock();
        }
        totalValue = totalValue / (Super_Upgrade.isEnabled(Constants.SU_HALF_MARKET_COST) ? 2 : 1);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity, R.style.AppTheme_Dialog);
        alertDialog.setMessage(String.format(activity.getString(R.string.itemBuyAllQuestion), itemCount, totalValue));
        alertDialog.setIcon(R.drawable.item52);

        alertDialog.setPositiveButton(activity.getString(R.string.itemBuyAllConfirm), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int itemsBought = 0;
                int buyResponse = Constants.ERROR_NOT_ENOUGH_COINS;
                boolean successful = true;
                List<Pair<Long, Integer>> items = new ArrayList<>();

                for (Trader_Stock itemStock : itemStocks) {
                    Inventory coinStock = Inventory.getInventory(Constants.ITEM_COINS, Constants.STATE_NORMAL);
                    Item item = Item.findById(Item.class, itemStock.getItemID());
                    int totalCost = (item.getModifiedValue(itemStock.getState()) * itemStock.getStock());
                    totalCost = totalCost / ((totalCost > 1 && Super_Upgrade.isEnabled(Constants.SU_HALF_MARKET_COST)) ? 2 : 1);

                    if (totalCost <= coinStock.getQuantity() && successful) {
                        int itemsToBuy = (Super_Upgrade.isEnabled(Constants.SU_TRADER_STOCK) ? 2 : 1 ) * itemStock.getStock();
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
                    ToastHelper.showPositiveToast(null, ToastHelper.SHORT, String.format(activity.getString(R.string.itemBuyAllComplete), itemsBought), false);
                    Player_Info.increaseByX(Player_Info.Statistic.ItemsBought, itemsBought);
                    GooglePlayHelper.UpdateEvent(Constants.EVENT_BUY_ALL_ITEM, 1);
                    GooglePlayHelper.UpdateEvent(Constants.EVENT_BOUGHT_ITEM, itemsBought);
                } else {
                    ToastHelper.showErrorToast(null, ToastHelper.SHORT, activity.getString(ErrorHelper.errors.get(buyResponse)), false);
                }

                Pending_Inventory.addScheduledItems(Constants.LOCATION_MARKET, items);
                activity.alertDialogCallback();
            }
        });

        alertDialog.setNegativeButton(activity.getString(R.string.itemBuyCancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final Dialog dialog = alertDialog.create();
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dialog.show();
        dialog.getWindow().getDecorView().setSystemUiVisibility(activity.getWindow().getDecorView().getSystemUiVisibility());
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }
}

