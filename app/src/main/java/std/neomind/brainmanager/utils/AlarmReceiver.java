package std.neomind.brainmanager.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

import std.neomind.brainmanager.R;

public final class AlarmReceiver extends BroadcastReceiver{
    private static final String TAG = "AlarmReceiver";

    public static final String EXTRAS_KEY_REVIEW_DATE = "keywordReviewDate";
    public static final String EXTRAS_MODE = "mode";

    @Override
    public void onReceive(Context context, Intent intent) {
        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        /**
         * 폰 재시작 할때 브로드캐스트 등록
         */
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            Log.i(TAG , "ACTION_BOOT_COMPLETED");

            ArrayList<Integer> idList = new ArrayList<>();
            ArrayList<Long> DateList = new ArrayList<>();
            try {
                BrainSerialDataIO.getNextReviewTimeInfo(context, idList, DateList);
            } catch (BrainSerialDataIO.LoadFailException e) {
                e.printStackTrace();
                Log.d(TAG, "시리얼 파일 불러오기 실패");
                return;
            }
            if(idList.isEmpty() && DateList.isEmpty()){
                Intent localIntent = new Intent(context, NotificationReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, localIntent, 0);
                for(long temp_date : DateList){
                    am.set(AlarmManager.RTC_WAKEUP, temp_date, pendingIntent);
                }
            }

//            Intent i = new Intent(context, ring.class);
//            i.putExtra(ring.EXTRAS_WAKE_UP, ring.EXTRAS_WAKE_UP);
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
//                context.startForegroundService(i);
//            }else{
//                context.startService(i);
//            }
        }
        else {  /** 폰 재시작할 경우가 아닌경우 **/
            long date;
            Log.i(TAG , "ACTION_ALARM_RECEIVER_COMPLETED");
            if(intent.getStringExtra(EXTRAS_MODE) == null){
                try {
                    date = intent.getExtras().getLong(EXTRAS_KEY_REVIEW_DATE);
                } catch (NullPointerException ex) {
                    Toast.makeText(context.getApplicationContext(), context.getString(R.string.AlarmReceiver_error), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "getExtras 에서 오류 발생");
                    return;
                }
            } else{
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MINUTE, 30);
                date = cal.getTimeInMillis();
            }
            long diffDate = date - System.currentTimeMillis()+1000;
            long diffMinute = diffDate % (1000*60*60*24) / (1000*60) % 60;
            long diffHour = diffDate % (1000*60*60*24) / (1000*60*60) % 24;
            long diffDay = diffDate / (1000*60*60*24);

            String tempDateText = "";
            if(diffDay > 0)
                tempDateText += diffDay + context.getString(R.string.Global_Day) + context.getString(R.string.Global_plural) + " ";
            if(diffHour > 0)
                tempDateText += diffHour + context.getString(R.string.Global_Hour) + context.getString(R.string.Global_plural) + " ";
            if(diffMinute > 0)
                tempDateText += diffMinute + context.getString(R.string.Global_Minute) + context.getString(R.string.Global_plural) + " ";

            Toast.makeText(context.getApplicationContext(), String.format(context.getString(R.string.AlarmReceiver_date_toast), tempDateText), Toast.LENGTH_LONG).show();

            if(date != Long.MIN_VALUE) {
                Intent localIntent = new Intent(context, NotificationReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, localIntent, 0);
                am.set(AlarmManager.RTC_WAKEUP, date, pendingIntent);
            }
        }
    }
}
