package com.smart.rinoiot.upush.fcm;//package com.umeng.message.demo.fcm;
//
//import androidx.annotation.NonNull;
//
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//
//import org.android.agoo.control.AgooFactory;
//import org.android.agoo.control.NotifManager;
//
//public class AgooFirebaseMessagingService extends FirebaseMessagingService {
//    private AgooFactory agooFactory;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        agooFactory = new AgooFactory();
//        agooFactory.init(getApplicationContext(), null, null);
//    }
//
//    @Override
//    public void onNewToken(@NonNull String token) {
//        try {
//            NotifManager notifyManager = new NotifManager();
//            notifyManager.init(getApplicationContext());
//            notifyManager.reportThirdPushToken(token, "gcm");
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onMessageReceived(@NonNull RemoteMessage message) {
//        try {
//            String msg = message.getData().get("payload");
//            if (msg != null && msg.length() > 0) {
//                agooFactory.msgRecevie(msg.getBytes(), "gcm");
//            }
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }
//    }
//
//}
