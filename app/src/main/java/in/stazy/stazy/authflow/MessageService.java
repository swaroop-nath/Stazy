package in.stazy.stazy.authflow;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import in.stazy.stazy.R;
import in.stazy.stazy.datamanagercrossend.Manager;
import in.stazy.stazy.hotelend.Performer;
import in.stazy.stazy.performerend.Hotel;

public class MessageService extends FirebaseMessagingService {


    public static final String SHOW_EXTRA_CONTENT_PERFORMER_END = "show_performer";
    public static final String SHOW_EXTRA_CONTENT_HOTEL_END = "show_hotel";
    public static final String PERFORMANCE_DETAILS_PERFORMER_END = "performance_details";
    public static final String HIRING_HOTEL_UID = "hiring_hotel";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        final String CHANNEL_ID =  getResources().getString(R.string.default_notification_channel_id);

        String notificationTitle = remoteMessage.getNotification().getTitle();
        String notificationBody = remoteMessage.getNotification().getBody();

        String revertUID = remoteMessage.getData().get("sender");
        String dataIntent = remoteMessage.getData().get("intent");

        PendingIntent pendingIntent = null;
        if (dataIntent.equals("SHORTLIST")) {
            //Notification is on the performer end
            Intent intent = new Intent(this, Hotel.class);
            intent.putExtra(SHOW_EXTRA_CONTENT_PERFORMER_END, true);
            intent.putExtra(PERFORMANCE_DETAILS_PERFORMER_END, notificationBody);
            intent.putExtra(HIRING_HOTEL_UID, revertUID);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        } else if (dataIntent.equals("RESPONSE")) {
            //Notification is on the hotel end
            Intent intent = new Intent(this, Performer.class);
            intent.putExtra(SHOW_EXTRA_CONTENT_HOTEL_END, true);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle(notificationTitle)
                .setContentText("")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(notificationBody))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (pendingIntent != null) {
            builder = builder.setContentIntent(pendingIntent);
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        int notificationID = 001;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "hire_notification";
            String description = "intent_hire_performer";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            notificationManager.createNotificationChannel(channel);
        }

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);

        notificationManagerCompat.notify(notificationID, builder.build());

    }

    @Override
    public void onNewToken(String s) {
        Log.e("TOKEN", "New Token Made");
        Manager.FCM_TOKEN = s;
        Manager.NEW_TOKEN_RECEIVED = 1;
    }
}
