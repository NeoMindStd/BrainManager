package std.neomind.brainmanager.utils.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import std.neomind.brainmanager.R;
import std.neomind.brainmanager.ReviewActivity;

public class Broadcast22 extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //han
        Intent newintent = new Intent(context, ReviewActivity.class);
        newintent.putExtra(ReviewActivity.EXTRAS_MODE, "bannermode");

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, newintent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher).setTicker("HETT").setWhen(System.currentTimeMillis())
                .setNumber(1).setContentTitle(context.getString(R.string.Notification_title)).setContentText(context.getString(R.string.Notification_text))
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingIntent).setAutoCancel(true);

        notificationmanager.notify(1, builder.build());
    }
}
