package com.ma.vodhmsdemo.huawei.domain.usecases;

import android.app.Activity;
import android.content.IntentSender;
import android.util.Log;

import com.huawei.hmf.tasks.Task;
import com.huawei.hms.support.api.client.Status;
import com.huawei.hms.support.api.entity.iap.GetBuyIntentReq;
import com.huawei.hms.support.api.iap.GetBuyIntentResult;
import com.huawei.hms.support.api.iap.json.Iap;
import com.huawei.hms.support.api.iap.json.IapApiException;
import com.huawei.hms.support.api.iap.json.IapClient;

import java.util.Objects;

import static com.ma.vodhmsdemo.huawei.common.Constant.REQ_CODE_BUY;

public class PaymentUseCase {
    private static final String TAG = "PaymentUseCase";
    /**
     * create orders for in-app products in the PMS
     * @param activity indicates the activity object that initiates a request.
     * @param skuId ID list of products to be queried. Each productModel ID must exist and be unique in the current app.
     * @param type  In-app productModel type.
     */
    public void getBuyIntent(final Activity activity, String skuId, int type) {
        Log.d(TAG, "call getBuyIntent");
        Log.d(TAG, "type " + type);
        Log.d(TAG, "skuId " + skuId);
        IapClient mClient = Iap.getIapClient(activity);
        Task<GetBuyIntentResult> task = mClient.getBuyIntent(createGetBuyIntentReq(type, skuId));
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
}
