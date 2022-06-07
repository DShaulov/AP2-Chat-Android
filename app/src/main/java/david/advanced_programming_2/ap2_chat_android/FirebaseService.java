package david.advanced_programming_2.ap2_chat_android;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class FirebaseService extends FirebaseMessagingService {
    private LocalBroadcastManager broadcaster;
    private SharedPreferences prefs;
    public FirebaseService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
        prefs = getApplicationContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Map<String, String> messageData = remoteMessage.getData();
        if (messageData != null) {
            String from = messageData.get("fromUser");
            // Only create notification if the current user is not the one who sent the message
            if (!from.equals(prefs.getString("currentUser", ""))) {
                createNotificationChannel();
                String messageTitle = messageData.get("title");
                String messageBody = messageData.get("body");
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
                notificationManagerCompat.notify(1, builder.build());

                Intent intent = new Intent("MessageData");
                intent.putExtra("from", remoteMessage.getData().get("fromUser"));
                intent.putExtra("to", remoteMessage.getData().get("toUser"));
                intent.putExtra("body", messageBody);
                intent.putExtra("time", remoteMessage.getData().get("timeSent"));
                broadcaster.sendBroadcast(intent);
            }
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", "My Channel", importance);
            channel.setDescription("my channel");

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}