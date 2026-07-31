package uk.co.jakelee.blacksmith.helper;

import android.app.Activity;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponseCode;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Thin wrapper over Google Play Billing, doing the job the anjlab library used to.
 * The game only sells one-time products, so subscriptions are deliberately unsupported.
 */
public class BillingHelper {
    public static final String SKU_PREMIUM = "premium";
    public static final String SKU_CONTRIBUTE = "contribute";
    public static final String SKU_COIN_1 = "coin_pack_1";
    public static final String SKU_COIN_2 = "coin_pack_2";
    public static final String SKU_COIN_3 = "coin_pack_3";

    /**
     * Which products are consumed rather than acknowledged. Deliberately app-wide rather than
     * per-screen: queryPurchasesAsync returns everything the player owns, not just the products the
     * current screen sells, so classifying against one screen's list would settle another screen's
     * purchase the wrong way round and leave it stuck as "already owned".
     */
    private static final Set<String> CONSUMABLES = new HashSet<>(Arrays.asList(
            SKU_CONTRIBUTE, SKU_COIN_1, SKU_COIN_2, SKU_COIN_3));

    private final Activity activity;
    private final Listener listener;
    private final List<String> allProducts = new ArrayList<>();
    private final Map<String, ProductDetails> productDetails = new HashMap<>();
    private BillingClient client;

    /** products are the ones this screen sells, used to fetch prices; settling uses CONSUMABLES. */
    public BillingHelper(Activity activity, Listener listener, String... products) {
        this.activity = activity;
        this.listener = listener;
        this.allProducts.addAll(Arrays.asList(products));

        client = BillingClient.newBuilder(activity)
                .setListener((billingResult, purchases) -> {
                    if (billingResult.getResponseCode() == BillingResponseCode.OK && purchases != null) {
                        for (Purchase purchase : purchases) {
                            handlePurchase(purchase, true);
                        }
                    } else if (billingResult.getResponseCode() != BillingResponseCode.USER_CANCELED) {
                        listener.onBillingError();
                    }
                })
                .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
                .enableAutoServiceReconnection()
                .build();

        client.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingResponseCode.OK) {
                    fetchProductDetails();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // enableAutoServiceReconnection() handles reconnecting for us.
            }
        });
    }

    public boolean isReady() {
        return client != null && client.isReady() && !productDetails.isEmpty();
    }

    public void release() {
        if (client != null) {
            client.endConnection();
            client = null;
        }
    }

    /**
     * Play's own price for the product, already formatted in the player's currency. Null until the
     * product details have come back, so callers need a fallback for the first render.
     */
    public String getFormattedPrice(String productId) {
        ProductDetails details = productDetails.get(productId);
        if (details == null || details.getOneTimePurchaseOfferDetails() == null) {
            return null;
        }

        return details.getOneTimePurchaseOfferDetails().getFormattedPrice();
    }

    /** Play requires a ProductDetails to launch a purchase flow, so these are fetched up front. */
    private void fetchProductDetails() {
        List<QueryProductDetailsParams.Product> products = new ArrayList<>();
        for (String productId : allProducts) {
            products.add(QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build());
        }

        if (client == null) {
            return;
        }

        client.queryProductDetailsAsync(
                QueryProductDetailsParams.newBuilder().setProductList(products).build(),
                (billingResult, result) -> {
                    if (billingResult.getResponseCode() == BillingResponseCode.OK) {
                        for (ProductDetails details : result.getProductDetailsList()) {
                            productDetails.put(details.getProductId(), details);
                        }
                    }
                    queryExistingPurchases();
                });
    }

    /**
     * Picks up anything the player already owns. Two distinct cases:
     * non-consumables get reported to the listener so it can restore entitlements, whereas an
     * owned consumable means a previous grant was interrupted before it could be consumed, so it
     * gets granted and consumed now.
     */
    private void queryExistingPurchases() {
        if (client == null) {
            return;
        }

        client.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder().setProductType(BillingClient.ProductType.INAPP).build(),
                (billingResult, purchases) -> {
                    Set<String> owned = new HashSet<>();
                    if (billingResult.getResponseCode() == BillingResponseCode.OK) {
                        for (Purchase purchase : purchases) {
                            if (purchase.getPurchaseState() != Purchase.PurchaseState.PURCHASED) {
                                continue;
                            }
                            for (String productId : purchase.getProducts()) {
                                if (!CONSUMABLES.contains(productId)) {
                                    owned.add(productId);
                                }
                            }
                            handlePurchase(purchase, false);
                        }
                    }
                    listener.onBillingReady(Collections.unmodifiableSet(owned));
                });
    }

    public void purchase(String productId) {
        ProductDetails details = productDetails.get(productId);
        if (client == null || !client.isReady() || details == null) {
            listener.onBillingError();
            return;
        }

        BillingFlowParams.ProductDetailsParams productParams = BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(details)
                .build();

        client.launchBillingFlow(activity, BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(Collections.singletonList(productParams))
                .build());
    }

    /**
     * Grant first, then consume or acknowledge. If the app dies in between, the purchase stays
     * owned and is picked up again on the next launch, so the player can't pay for nothing.
     *
     * @param notifyForNonConsumables false when replaying purchases found at startup, since
     *                                granting a non-consumable twice would stack its bonuses.
     */
    private void handlePurchase(Purchase purchase, boolean notifyForNonConsumables) {
        if (purchase.getPurchaseState() != Purchase.PurchaseState.PURCHASED) {
            return;
        }

        if (client == null) {
            return;
        }

        boolean anyConsumable = false;
        for (String productId : purchase.getProducts()) {
            boolean isConsumable = CONSUMABLES.contains(productId);
            anyConsumable |= isConsumable;

            if (isConsumable || notifyForNonConsumables) {
                listener.onProductPurchased(productId);
            }
        }

        if (anyConsumable) {
            client.consumeAsync(
                    ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build(),
                    (billingResult, purchaseToken) -> {
                    });
        } else if (!purchase.isAcknowledged()) {
            // Unacknowledged purchases get refunded automatically after three days.
            client.acknowledgePurchase(
                    AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build(),
                    billingResult -> {
                    });
        }
    }

    public interface Listener {
        /** Billing is connected. ownedProducts holds the non-consumables the player already owns. */
        void onBillingReady(Set<String> ownedProducts);

        /** Grant the product to the player. */
        void onProductPurchased(String productId);

        void onBillingError();
    }
}
