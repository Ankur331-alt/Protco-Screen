package com.smart.rinoiot.upush.mfr;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.umeng.message.UmengNotifyClickActivity;

/**
 * 厂商通道配置启动的Activity
 * 点击小米、vivo等厂商渠道的推送通知消息后跳转的activity
 * v6.5.2之后新增支持跳转到任意Activity，建议参考MfrMessageActivity2
 * <p>
 * 必须在AndroidManifest.xml中MfrMessageActivity标签下配置：
 * 1. 配置 android:exported="true"
 * 2. 新增 intent-filter
 * <intent-filter>
 * <action android:name="android.intent.action.VIEW" />
 * <category android:name="android.intent.category.DEFAULT" />
 * <category android:name="android.intent.category.BROWSABLE" />
 * <data
 * android:host="${applicationId}"
 * android:path="/thirdpush"
 * android:scheme="agoo" />
 * </intent-filter>
 */
@Deprecated
public class MfrMessageActivity extends UmengNotifyClickActivity {
    private static final String TAG = MfrMessageActivity.class.getSimpleName();
    private TextView miPushTextView;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
//        setContentView(R.layout.mfr_message_layout);
//        miPushTextView = (TextView) findViewById(R.id.tv);
        //跳到首页
//        NavigationUtil.navigationFlash();
        finish();
    }

    @Override
    public void onMessage(Intent intent) {
        super.onMessage(intent);
//        final String body = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
//        Log.d(TAG, body);
//        if (!TextUtils.isEmpty(body)) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    miPushTextView.setText(body);
//                }
//            });
//        }
    }
}
