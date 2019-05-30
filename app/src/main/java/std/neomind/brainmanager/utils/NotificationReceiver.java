package std.neomind.brainmanager.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

import std.neomind.brainmanager.R;
import std.neomind.brainmanager.ReviewActivity;
import std.neomind.brainmanager.utils.BrainSerialDataIO;

public class NotificationReceiver extends BroadcastReceiver {
    static private final String TAG = "NotificationReceiver";
    static private final String channel= "BrainNotificationChannel";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //han

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationmanager.createNotificationChannel(new NotificationChannel(channel, "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

        //배너 눌렀을 때 전해줄 intent
        Intent newintent = new Intent(context, ReviewActivity.class);
        newintent.putExtra(ReviewActivity.EXTRAS_MODE, "bannermode");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, newintent, PendingIntent.FLAG_CANCEL_CURRENT);

        ArrayList<Integer> idList = new ArrayList<>();
        ArrayList<Long> dateList = new ArrayList<>();

        try {
            BrainSerialDataIO.getNextReviewTimeInfo(context, idList, dateList);
        } catch (BrainSerialDataIO.LoadFailException e) {
            e.printStackTrace();
            Log.d(TAG, "시리얼 저장된 객체 불러오기 실패.");
            return;
        } catch (BrainSerialDataIO.ListNotEqualSizeException e) {
            e.printStackTrace();
            Log.d(TAG, "시리얼 저장된 객체 불러오기 실패.");
            return;
        }
        if(idList.size() == 0){
            Log.d(TAG, "알람 개수가 0개임.");
            return;
        }
        int showNum = 0;
        long now = System.currentTimeMillis();
        for(long date : dateList){
            if(date < now){
                showNum++;
            }
        }

        //노티피케이션 빌더 초기화
        Notification.Builder builder = new Notification.Builder(context);
//        builder.setSmallIcon(R.mipmap.ic_launcher).setTicker("Notification.Builder").setWhen(System.currentTimeMillis())
//                .setNumber(1).setContentTitle(context.getString(R.string.Notification_title)).setContentText(context.getString(R.string.Notification_text))
//                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingIntent).setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setSmallIcon(R.mipmap.ic_launcher).setTicker("Notification.Builder").setWhen(System.currentTimeMillis())
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_round))
                    .setNumber(showNum).setContentTitle(context.getString(R.string.Notification_title)).setContentText(context.getString(R.string.Notification_text))
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingIntent).setAutoCancel(true)
                    .setChannelId(channel);
        }else{
            builder.setSmallIcon(R.mipmap.ic_launcher).setTicker("Notification.Builder").setWhen(System.currentTimeMillis())
                    .setNumber(showNum).setContentTitle(context.getString(R.string.Notification_title)).setContentText(context.getString(R.string.Notification_text))
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_round))
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingIntent).setAutoCancel(true);
        }

        notificationmanager.notify(1, builder.build());
    }
}
