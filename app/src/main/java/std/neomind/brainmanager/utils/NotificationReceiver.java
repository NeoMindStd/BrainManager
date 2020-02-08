package std.neomind.brainmanager.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Calendar;

import std.neomind.brainmanager.R;
import std.neomind.brainmanager.ReviewActivity;

public final class NotificationReceiver extends BroadcastReceiver {
    //chan
    private static final String TAG = "NotificationReceiver";
    private static final String CHANNEL = "BrainNotificationChannel";
    private static final String DEFAULT_CHANNEL = "기본 채널";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {

        boolean soundFlag = true;   //디폴트는 사운드 on(true)
        SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.
                SharedPreferencesName), Context.MODE_PRIVATE);
        if (pref.getBoolean(context.getString(R.string.SharedPreferences_nightMode), false)) {
            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.DAY_OF_MONTH);
            if (hour > 23 || hour < 8) {
                soundFlag = false;
            }
        }

        ArrayList<Integer> idList = new ArrayList<>();
        ArrayList<Long> dateList = new ArrayList<>();

        try {
            BrainSerialDataIO.getNextReviewTimeInfo(context, idList, dateList);
        } catch (BrainSerialDataIO.LoadFailException e) {
            e.printStackTrace();
            Log.d(TAG, "시리얼 저장된 객체 불러오기 실패.");
            return;
        }
        if (idList.size() == 0) {
            Log.d(TAG, "알람 개수가 0개임.");
            return;
        }
        int showNum = 0;
        long now = System.currentTimeMillis();
        for (long date : dateList) {
            if (date < now) {
                showNum++;
            }
        }
        if (showNum == 0) {
            return;
        }

        //배너 눌렀을 때 전해줄 intent
        Intent newIntent = new Intent(context, ReviewActivity.class);
        newIntent.putExtra(ReviewActivity.EXTRAS_MODE, ReviewActivity.EXPIRED_MODE);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, newIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        //만약 알림을 취소하면 넘길 펜딩인텐트
        Intent intentCancel = new Intent(context, AlarmReceiver.class);
        intentCancel.putExtra(AlarmReceiver.EXTRAS_MODE, AlarmReceiver.MODE_CANCEL_BANNER);
        PendingIntent pendingCancelIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, intentCancel, 0);

        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        //노티피케이션 빌더 초기화
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL);
            NotificationChannel mChannel;
            if (soundFlag) {
                mChannel = new NotificationChannel(CHANNEL, DEFAULT_CHANNEL, NotificationManager.IMPORTANCE_DEFAULT);
            } else {
                mChannel = new NotificationChannel(CHANNEL, DEFAULT_CHANNEL, NotificationManager.IMPORTANCE_LOW);
            }

            notificationmanager.createNotificationChannel(mChannel);

            builder.setSmallIcon(R.drawable.ic_main_study).setTicker("Notification.Builder")
                    .setWhen(System.currentTimeMillis())
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_round))
                    .setNumber(showNum)
                    .setContentTitle(context.getString(R.string.Notification_title))
                    .setContentText(
                            showNum + context.getString(R.string.Notification_keyword)
                                    + context.getString(R.string.Global_plural)
                                    + context.getString(R.string.Notification_text))
                    .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    .setContentIntent(pendingIntent).setAutoCancel(true)
                    .setDeleteIntent(pendingCancelIntent)
                    .setChannelId(CHANNEL);
            if (!soundFlag) {
                builder.setDefaults(0);
            }

            Notification note = builder.build();
            note.flags |= Notification.FLAG_AUTO_CANCEL;

            notificationmanager.notify(1, note);
        } else {
            Notification.Builder builder = new Notification.Builder(context);
            builder.setSmallIcon(R.drawable.ic_main_study).setTicker("Notification.Builder")
                    .setWhen(System.currentTimeMillis())
                    .setNumber(showNum).setContentTitle(context.getString(R.string.Notification_title))
                    .setContentText(
                            showNum + context.getString(R.string.Notification_keyword)
                                    + context.getString(R.string.Global_plural)
                                    + context.getString(R.string.Notification_text))
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_round))
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE)
                    .setDeleteIntent(pendingCancelIntent)
                    .setContentIntent(pendingIntent).setAutoCancel(true);
            if (!soundFlag) {
                builder.setDefaults(0);
            }

            notificationmanager.notify(1, builder.build());
        }
    }
}
