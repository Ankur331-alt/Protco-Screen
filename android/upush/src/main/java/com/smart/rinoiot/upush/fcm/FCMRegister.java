package com.smart.rinoiot.upush.fcm;//package com.umeng.message.demo.fcm;
//
//import android.content.Context;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.messaging.FirebaseMessaging;
//
//import org.android.agoo.control.NotifManager;
//
//public class FCMRegister {
//
//    public static void register(final Context context) {
//        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
//            @Override
//            public void onComplete(@NonNull Task<String> task) {
//                if (!task.isSuccessful()) {
//                    Log.w("fcm", "Fetching FCM registration token failed", task.getException());
//                    return;
//                }
//                // Get new FCM registration token
//                String token = task.getResult();
//                Log.d("fcm", "token:" + token);
//                try {
//                    NotifManager notifyManager = new NotifManager();
//                    notifyManager.init(context.getApplicationContext());
//                    notifyManager.reportThirdPushToken(token, "gcm");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//}
