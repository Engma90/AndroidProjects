package com.ma.vodhmsdemo.huawei.ui.video;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.ma.vodhmsdemo.huawei.R;
import com.ma.vodhmsdemo.huawei.data.model.ProductModel;
import com.ma.vodhmsdemo.huawei.domain.usecases.PaymentUseCase;
import com.ma.vodhmsdemo.huawei.ui.UserViewModel;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.ma.vodhmsdemo.huawei.common.Constant.PRODUCT_TYPE_CONSUMABLE;
import static com.ma.vodhmsdemo.huawei.common.Constant.REQ_CODE_BUY;

public class VideoActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "VideoActivity";
    Button btnBuyNow;
    CircleImageView imgAvatar;
    WebView videoView;
    TextView txtNickName;
    ProductViewModel productViewModel;
    UserViewModel userViewModel;
    ProgressBar webViewProgressBar, avatarProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        txtNickName = findViewById(R.id.txtNickName);
        btnBuyNow = findViewById(R.id.btnBuyNow);
        imgAvatar = findViewById(R.id.imgAvatar);
        webViewProgressBar = findViewById(R.id.webViewProgressBar);
        avatarProgressBar = findViewById(R.id.avatarProgressBar);

        btnBuyNow.setOnClickListener(this);

        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        userViewModel.getLiveData().observe(this, userModel -> {
            txtNickName.setText(userModel.getName());
            if(userModel.getPhoto() != null) {
                imgAvatar.setImageBitmap(userModel.getPhoto());
                avatarProgressBar.setVisibility(View.GONE);
            }
        });


        productViewModel = ViewModelProviders.of(this).get(ProductViewModel.class);
        productViewModel.getLiveData().observe(this, productModel -> {
            btnBuyNow.setText(String.format("%s for %s",
                    productModel.getName(),
                    productModel.getPrice()));
            initWebView(productModel);
        });

        if(productViewModel.getLiveData().getValue() == null)
            productViewModel.initProduct(VideoActivity.this);

    }

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    private void initWebView(ProductModel productModel){
        Log.i(TAG, "initializing webView");
        String frameVideo =
                "<html>" +
                "<body style=\"margin: 0; padding: 0\">" +
                "<center>" +
                "<iframe " +
                        "width=\"100%\" height=\"100%\" " +
                        "src=\"https://www.youtube.com/embed/WGpgHbKQmaI\" " +
                        "frameborder=\"0\" allowfullscreen>" +
                "</iframe>" +
                "</center>" +
                "</body>" +
                "</html>";

        videoView = findViewById(R.id.videoView);
        videoView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request){
                return true;
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url){
                webViewProgressBar.setVisibility(View.GONE);
            }
        });
        WebSettings webSettings = videoView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        videoView.loadData(frameVideo, "text/html", "utf-8");
        if(productModel.isPaidForIt()) {
            btnBuyNow.setVisibility(View.GONE);
            videoView.setOnTouchListener((v, event) -> false);
        }
        else {

            videoView.setOnTouchListener((v, event) ->{
                btnBuyNow.setVisibility(View.VISIBLE);
            return true;});
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_BUY) {
            productViewModel.processBuyResult(data, VideoActivity.this);
        }
    }

    @Override
    public void onClick(View view) {
        if(view == btnBuyNow){
            if(productViewModel.getLiveData().getValue() != null) {
                new PaymentUseCase().getBuyIntent(VideoActivity.this,
                        productViewModel.getLiveData().getValue().getId(),
                        PRODUCT_TYPE_CONSUMABLE);
            }
        }
    }
}
