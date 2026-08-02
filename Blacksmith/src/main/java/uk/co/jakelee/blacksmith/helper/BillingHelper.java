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

public class BillingHelper {
    private static final String TAG = "BillingHelper";
    public static final String SKU_PREMIUM = "premium";
    public static final String SKU_CONTRIBUTE = "contribute";
    public static final String SKU_COIN_1 = "coin_pack_1";
    public static final String SKU_COIN_2 = "coin_pack_2";
    public static final String SKU_COIN_3 = "coin_pack_3";

    // App-wide, not per-screen: queryPurchasesAsync returns everything the player owns, so
    // classifying against one screen's product list would settle another screen's purchase wrongly.
    private static final Set<String> CONSUMABLES = new HashSet<>(Arrays.asList(
            SKU_CONTRIBUTE, SKU_COIN_1, SKU_COIN_2, SKU_COIN_3));

    private final Activity activity;
    private final Listener listener;
    private final List<String> allProducts = new ArrayList<>();
    // Concurrent: Play writes this from a background thread and the UI reads it from the main one.
    // A plain HashMap is published unsafely, and the reader then sees only some of the entries.
    private final Map<String, ProductDetails> productDetails = new ConcurrentHashMap<>();
    private BillingClient client;

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

    /** Null until Play answers, so callers need a fallback for the first render. */
    public String getFormattedPrice(String productId) {
        ProductDetails details = productDetails.get(productId);
        if (details == null) {
            return null;
        }

        // Billing 8 added purchase options, for which the singular accessor returns null and the
        // price lives in the offer list instead. Both shapes occur.
        if (details.getOneTimePurchaseOfferDetails() != null) {
            return details.getOneTimePurchaseOfferDetails().getFormattedPrice();
        }

        List<ProductDetails.OneTimePurchaseOfferDetails> offers = details.getOneTimePurchaseOfferDetailsList();
        if (offers != null && !offers.isEmpty()) {
            return offers.get(0).getFormattedPrice();
        }

        return null;
    }

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

    // An owned consumable means a previous grant was interrupted before it could be consumed, so
    // it is granted and consumed now; non-consumables are reported so entitlements can be restored.
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
     * Grants before settling, so a grant interrupted by the app dying is replayed on next launch.
     *
     * @param notifyForNonConsumables false when replaying at startup, since granting a
     *                                non-consumable twice would stack its bonuses.
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

    // Play calls back on a background thread and every Listener method touches views, so they are
    // bounced onto the main thread. runOnUiThread runs inline when already there.
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
        /** ownedProducts holds the non-consumables the player already owns. */
        void onBillingReady(Set<String> ownedProducts);

        void onProductPurchased(String productId);

        void onBillingError();
    }
}
