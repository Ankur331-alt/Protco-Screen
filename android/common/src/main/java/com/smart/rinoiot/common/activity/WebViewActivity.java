package com.smart.rinoiot.common.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.smart.rinoiot.common.Constant;
import com.smart.rinoiot.common.base.BaseActivity;
import com.smart.rinoiot.common.base.BaseViewModel;
import com.smart.rinoiot.common.databinding.ActivityWebviewBinding;
import com.smart.rinoiot.common.utils.DpUtils;

/**
 * 副文本、html显示
 */
public class WebViewActivity extends BaseActivity<ActivityWebviewBinding, BaseViewModel> {
    @Override
    public String getToolBarTitle() {
        return getIntent().getStringExtra(Constant.ACTIVITY_TITLE);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void init() {
        changeAppLanguage();
        binding.webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        WebSettings webSettings = binding.webview.getSettings();
        //支持缩放，默认为true。
        webSettings.setSupportZoom(false);
        //调整图片至适合webview的大小
        webSettings.setUseWideViewPort(true);
        // 缩放至屏幕的大小
        webSettings.setLoadWithOverviewMode(false);
        //设置默认编码
        webSettings.setDefaultTextEncodingName("utf-8");
        ////设置自动加载图片
        webSettings.setLoadsImagesAutomatically(true);
        //多窗口
        webSettings.setSupportMultipleWindows(false);
        //获取触摸焦点s
        binding.webview.requestFocusFromTouch();
        //允许访问文件
        webSettings.setAllowFileAccess(true);
        //开启javascript
        webSettings.setJavaScriptEnabled(true);
//        webSettings.setAppCacheEnabled(true);
        webSettings.setDomStorageEnabled(true);
        //支持通过JS打开新窗口
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //提高渲染的优先级
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        //支持内容重新布局
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        //关闭webview中缓存
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        setIntentUrl(1, "");
        binding.webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (!TextUtils.isEmpty(url) && url.contains("https://iot.rinoiot.com/auth?code=")) {
                    Intent intent = new Intent();
                    intent.putExtra(Constant.ALEXA_VERIFY_CALL_BACK, url);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        binding.webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {
                    binding.progressBar.setVisibility(View.GONE);
                } else {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    binding.progressBar.setProgress(newProgress);
                }
            }
        });

    }

    /**
     * @param type 1:传了副文本内容 其他没传
     */
    private void setIntentUrl(int type, String detailUrl) {
        String url;
        if (type == 1) {
            url = getIntent().getStringExtra(Constant.WEB_URL);
        } else {
            url = detailUrl;
        }
        if (url != null && (url.contains("http:") || url.contains("https:"))) {
            binding.webview.loadUrl(url);
        } else {
            binding.webview.setBackgroundColor(0);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = DpUtils.dip2px(18);
            params.rightMargin = DpUtils.dip2px(18);
            params.weight = 1;
            binding.webview.setLayoutParams(params);
            url = getHtmlData(url);
            binding.webview.loadDataWithBaseURL(null, url, "text/html", "utf-8", null);
        }
    }

    /**
     * 富文本适配
     */
    private String getHtmlData(String bodyHTML) {
        String head = "<head>"
                + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, user-scalable=no\"> "
                + "<style>img{max-width: 100%; width:auto; height:auto;}</style>"
                + "</head>";
        return "<html>" + head + "<body>" + bodyHTML + "</body></html>";
    }

    @Override
    public ActivityWebviewBinding getBinding(LayoutInflater inflater) {
        return ActivityWebviewBinding.inflate(inflater);
    }
}
