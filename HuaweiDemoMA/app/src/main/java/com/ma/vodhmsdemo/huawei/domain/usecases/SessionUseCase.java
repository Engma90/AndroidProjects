package com.ma.vodhmsdemo.huawei.domain.usecases;
import android.app.Activity;
import com.huawei.hms.auth.api.signin.HuaweiIdSignIn;
import com.huawei.hms.auth.api.signin.HuaweiIdSignInClient;
import com.huawei.hms.support.api.hwid.HuaweiIdSignInOptions;
import com.ma.vodhmsdemo.huawei.common.Constant;

public class SessionUseCase {
    public void startLoginProcess(Activity activity){
        HuaweiIdSignInOptions mSignInOptions = new HuaweiIdSignInOptions.Builder(HuaweiIdSignInOptions.DEFAULT_SIGN_IN).requestIdToken("").build();
        HuaweiIdSignInClient mSignInClient = HuaweiIdSignIn.getClient(activity, mSignInOptions);
        activity.startActivityForResult(mSignInClient.getSignInIntent(), Constant.REQUEST_SIGN_IN_LOGIN);
    }
}
