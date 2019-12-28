package com.ma.vodhmsdemo.huawei.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.support.api.client.Status;
import com.huawei.hms.support.api.entity.iap.ConsumePurchaseReq;
import com.huawei.hms.support.api.entity.iap.GetBuyIntentReq;
import com.huawei.hms.support.api.entity.iap.GetPurchaseReq;
import com.huawei.hms.support.api.entity.iap.OrderStatusCode;
import com.huawei.hms.support.api.entity.iap.SkuDetailReq;
import com.huawei.hms.support.api.iap.BuyResultInfo;
import com.huawei.hms.support.api.iap.ConsumePurchaseResult;
import com.huawei.hms.support.api.iap.GetBuyIntentResult;
import com.huawei.hms.support.api.iap.GetPurchasesResult;
import com.huawei.hms.support.api.iap.SkuDetailResult;
import com.huawei.hms.support.api.iap.json.Iap;
import com.huawei.hms.support.api.iap.json.IapApiException;
import com.huawei.hms.support.api.iap.json.IapClient;
import com.ma.vodhmsdemo.huawei.models.ProductModel;
import com.ma.vodhmsdemo.huawei.utils.CipherUtil;
import com.ma.vodhmsdemo.huawei.utils.Key;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import static com.ma.vodhmsdemo.huawei.utils.Constant.PRODUCT_TYPE_CONSUMABLE;
import static com.ma.vodhmsdemo.huawei.utils.Constant.REQ_CODE_BUY;

public class ProductViewModel extends ViewModel {
    private static final String TAG = "ProductViewModel";

    private static MutableLiveData<ProductModel> liveData;
    LiveData<ProductModel> getLiveData(){
        if(liveData == null) {
            liveData = new MutableLiveData<>();
        }
        return liveData;
    }

    private void loadLiveData(ProductModel productModel){
            liveData.setValue(productModel);
    }




    void initProduct(Activity activity) {
        // obtain in-app productModel details configured in AppGallery Connect, and then show the products
        IapClient iapClient = Iap.getIapClient(activity);
        Task<SkuDetailResult> task = iapClient.getSkuDetail(createSkuDetailReq());
        task.addOnSuccessListener(result -> {
            if (result != null && !result.getSkuList().isEmpty()) {
                ProductModel productModel = new ProductModel();
                productModel.setId(result.getSkuList().get(0).productId);
                productModel.setName(result.getSkuList().get(0).productName);
                productModel.setPrice(result.getSkuList().get(0).price);
                productModel.setPaidForIt(false);
                loadLiveData(productModel);
            }
        }).addOnFailureListener(e -> Toast.makeText(activity, "error", Toast.LENGTH_SHORT).show());
    }

    private SkuDetailReq createSkuDetailReq() {
        SkuDetailReq skuDetailRequest = new SkuDetailReq();
        // In-app productModel type contains:
        // 0: consumable
        // 1: non-consumable
        // 2: auto-renewable subscription
        skuDetailRequest.priceType = PRODUCT_TYPE_CONSUMABLE;
        ArrayList<String> skuList = new ArrayList<>();
        // Pass in the productId list of products to be queried.
        // The productModel ID is the same as that set by a developer when configuring productModel information in AppGallery Connect.
        skuList.add("Consumable.Product01");
        skuDetailRequest.skuIds = skuList;
        return skuDetailRequest;
    }

    /**
     * create orders for in-app products in the PMS
     * @param activity indicates the activity object that initiates a request.
     * @param skuId ID list of products to be queried. Each productModel ID must exist and be unique in the current app.
     * @param type  In-app productModel type.
     */
    void getBuyIntent(final Activity activity, String skuId, int type) {
        Log.d(TAG, "call getBuyIntent");
        Log.d(TAG, "type " + type);
        Log.d(TAG, "skuId " + skuId);
        IapClient mClient = Iap.getIapClient(activity);
        Task<GetBuyIntentResult> task = mClient.getBuyIntent(createGetBuyIntentReq(type, skuId));

//        task.addOnCanceledListener(() -> {
//            Log.d(TAG, "getBuyIntent, Canceled ");
//        });
        task.addOnSuccessListener(result -> {
            Log.d(TAG, "getBuyIntent, onSuccess");
            // you should pull up the page to complete the payment process
            Status status = result.getStatus();
                    if (status.hasResolution()) {
                        try {
                            status.startResolutionForResult(activity, REQ_CODE_BUY);
                        } catch (IntentSender.SendIntentException exp) {
                            Log.e(TAG, "startResolutionForResult, " + exp.getMessage());
                        }
                    }
        });
        task.addOnFailureListener(e -> {
            if (e instanceof IapApiException) {
                IapApiException apiException = (IapApiException)e;
                int returnCode = apiException.getStatusCode();
                Log.d(TAG, "getBuyIntent, returnCode: " + returnCode);
                // handle error scenarios
            } else {
                // Other external errors
                Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            }

        });
    }

    /**
     * Create a GetBuyIntentReq request
     * @param type In-app productModel type.
     * @param skuId ID of the in-app productModel to be paid.
     *              The in-app productModel ID is the productModel ID you set during in-app productModel configuration in AppGallery Connect.
     * @return GetBuyIntentReq
     */
    private static GetBuyIntentReq createGetBuyIntentReq(int type, String skuId) {
        GetBuyIntentReq request = new GetBuyIntentReq();
        request.productId = skuId;
        request.priceType = type;
        request.developerPayload = "test";
        return request;
    }



    /**
     * consume the unconsumed purchase with type 0
     * @param inAppPurchaseData JSON string that contains purchase order details.
     */
    @SuppressLint("ClickableViewAccessibility")
    private void consumePurchase(final Context context, String inAppPurchaseData) {
        Log.i(TAG, "call consumePurchase");
        IapClient mClient = Iap.getIapClient(context);
        Task<ConsumePurchaseResult> task = mClient.consumePurchase(createConsumePurchaseReq(inAppPurchaseData));
        task.addOnSuccessListener(result -> {
            // Consume success
            Log.i(TAG, "consumePurchase success");
            Toast.makeText(context, "Pay success, and the productModel has been delivered", Toast.LENGTH_SHORT).show();

            ProductModel productModel = liveData.getValue();
            if(productModel != null){
                productModel.setPaidForIt(true);
                loadLiveData(productModel);
            }



        }).addOnFailureListener(e -> {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            if (e instanceof IapApiException) {
                IapApiException apiException = (IapApiException)e;
                int returnCode = apiException.getStatusCode();
                Log.i(TAG, "consumePurchase fail,returnCode: " + returnCode);
            } else {
                // Other external errors
                Toast.makeText(context, "external error", Toast.LENGTH_SHORT).show();
                Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            }

        });
    }


    /**
     * Create a ConsumePurchaseReq request
     * @param purchaseData which is generated by the Huawei payment server during productModel payment and returned to the app through InAppPurchaseData.
     *                     The app transfers this parameter for the Huawei payment server to update the order status and then deliver the in-app productModel.
     * @return ConsumePurchaseReq
     */
    private ConsumePurchaseReq createConsumePurchaseReq(String purchaseData) {
        ConsumePurchaseReq consumePurchaseRequest = new ConsumePurchaseReq();
        String purchaseToken = "";
        try {
            JSONObject jsonObject = new JSONObject(purchaseData);
            purchaseToken = jsonObject.optString("purchaseToken");
        } catch (JSONException e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
        consumePurchaseRequest.purchaseToken = purchaseToken;
        return consumePurchaseRequest;
    }

    void processBuyResult(Intent data, Context context) {
        if (data != null) {
            IapClient mClient = Iap.getIapClient(context);
            BuyResultInfo buyResultInfo = mClient.getBuyResultInfoFromIntent(data);
            if (buyResultInfo.getReturnCode() == OrderStatusCode.ORDER_STATE_SUCCESS ) {
                Log.i(TAG,"ORDER_STATE_SUCCESS");
                // verify signature of payment results
                boolean success = CipherUtil.doCheck(buyResultInfo.getInAppPurchaseData(),
                        buyResultInfo.getInAppDataSignature(), Key.getPublicKey());
                if (success) {
                    // call the consumption interface to consume it after successfully delivering the productModel to your user
                    consumePurchase(context, buyResultInfo.getInAppPurchaseData());
                } else {
                    Log.e(TAG,"CipherUtil.doCheck no SUCCESS");
                }
            }else if(buyResultInfo.getReturnCode() == OrderStatusCode.ORDER_ITEM_ALREADY_OWNED) {
                Log.i(TAG,"ORDER_ITEM_ALREADY_OWNED");

                Task<GetPurchasesResult> task = mClient.getPurchases(new GetPurchaseReq());
                task.addOnSuccessListener(result -> consumePurchase(
                        context,
                        result.getInAppPurchaseDataList().get(0)));

            }else if(buyResultInfo.getReturnCode() == OrderStatusCode.ORDER_STATE_CANCEL) {
                Log.e(TAG,"ORDER_STATE_CANCEL");
            }else {
                Log.e(TAG,"Pay failed");
            }
        } else {
            Log.e(TAG, "data is null");
        }
    }
}
