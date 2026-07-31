package uk.co.jakelee.blacksmith.helper;

import android.app.Activity;
import android.util.Log;

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
import com.android.billingclient.api.UnfetchedProduct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thin wrapper over Google Play Billing, doing the job the anjlab library used to.
 * The game only sells one-time products, so subscriptions are deliberately unsupported.
 */
public class BillingHelper {
    private static final String TAG = "BillingHelper";
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
    /**
     * Concurrent because Play fills this in on its own background thread while the UI reads it on
     * the main one. A plain HashMap is published unsafely across that boundary, and the reader can
     * then see only some of the entries - which showed up as one real price and two fallbacks, and
     * would also make isReady()/purchase() refuse a product the player actually can buy.
     */
    private final Map<String, ProductDetails> productDetails = new ConcurrentHashMap<>();
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
                        notifyError();
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
                } else {
                    // Total billing failure: no prices, no restore, nothing buyable. Worth a log,
                    // since otherwise it looks identical to Play simply being slow.
                    Log.w(TAG, "Billing setup failed: " + billingResult.getResponseCode()
                            + " " + billingResult.getDebugMessage());
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
        if (details == null) {
            return null;
        }

        // Billing 8 added purchase options for one-time products, for which the singular accessor
        // returns null and the price lives in the offer list instead. Handle both shapes.
        if (details.getOneTimePurchaseOfferDetails() != null) {
            return details.getOneTimePurchaseOfferDetails().getFormattedPrice();
        }

        List<ProductDetails.OneTimePurchaseOfferDetails> offers = details.getOneTimePurchaseOfferDetailsList();
        if (offers != null && !offers.isEmpty()) {
            return offers.get(0).getFormattedPrice();
        }

        return null;
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
                        reportUnfetched(result.getUnfetchedProductList());
                    } else {
                        Log.w(TAG, "Product query failed: " + billingResult.getResponseCode()
                                + " " + billingResult.getDebugMessage());
                    }
                    queryExistingPurchases();
                });
    }

    /**
     * Play can return a partial result, and silently missing products mean a wrong price or an
     * unbuyable button. This is the only diagnostic the game has for that, so it stays in.
     */
    private void reportUnfetched(List<UnfetchedProduct> unfetched) {
        if (unfetched == null) {
            return;
        }

        for (UnfetchedProduct product : unfetched) {
            Log.w(TAG, "Play returned no details for " + product.getProductId()
                    + " (status " + product.getStatusCode() + ", "
                    + describeStatus(product.getStatusCode()) + ")");
        }
    }

    private static String describeStatus(int statusCode) {
        if (statusCode == UnfetchedProduct.StatusCode.PRODUCT_NOT_FOUND) {
            return "product not found";
        } else if (statusCode == UnfetchedProduct.StatusCode.NO_ELIGIBLE_OFFER) {
            return "no offer this account is eligible for";
        } else if (statusCode == UnfetchedProduct.StatusCode.INVALID_PRODUCT_ID_FORMAT) {
            return "invalid product id";
        }
        return "unknown";
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
                    notifyReady(Collections.unmodifiableSet(owned));
                });
    }

    public void purchase(String productId) {
        ProductDetails details = productDetails.get(productId);
        if (client == null || !client.isReady() || details == null) {
            notifyError();
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
                notifyPurchased(productId);
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

    /**
     * Play runs its callbacks on a background thread, but every Listener method here ends up
     * touching views (relabelling buttons, toasts), so they get bounced onto the main thread first.
     * runOnUiThread runs inline when already on it, so the click-driven paths are unaffected.
     */
    private void notifyReady(final Set<String> owned) {
        activity.runOnUiThread(() -> listener.onBillingReady(owned));
    }

    private void notifyPurchased(final String productId) {
        activity.runOnUiThread(() -> listener.onProductPurchased(productId));
    }

    private void notifyError() {
        activity.runOnUiThread(() -> listener.onBillingError());
    }

    public interface Listener {
        /** Billing is connected. ownedProducts holds the non-consumables the player already owns. */
        void onBillingReady(Set<String> ownedProducts);

        /** Grant the product to the player. */
        void onProductPurchased(String productId);

        void onBillingError();
    }
}
