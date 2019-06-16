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
    public static final String MODE_CANCEL_REVIEW = "cancelreview";
    public static final String MODE_CANCEL_BANNER = "cancelbanner";

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
                for(long temp_date : DateList){
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int)(temp_date/1000/60/10), localIntent, 0);
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
            String cancelShow ="";
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
                //두개의 모드가 있지만 현재는 동작이 동일하기 때문에 합쳐서 함.
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                cal.add(Calendar.MINUTE, 20);
                date = cal.getTimeInMillis();
                cancelShow = context.getString(R.string.AlarmReceiver_cancel);
            }
            long diffDate = date - System.currentTimeMillis()+2000;
            long diffMinute = diffDate % (1000*60*60*24) / (1000*60) % 60;
            long diffHour = diffDate % (1000*60*60*24) / (1000*60*60) % 24;
            long diffDay = diffDate / (1000*60*60*24);

            String tempDateText = "";
            if(diffDay > 0) {
                if(diffDay > 1)
                    tempDateText += diffDay + context.getString(R.string.Global_Day) + context.getString(R.string.Global_plural) + " ";
                else
                    tempDateText += diffDay + context.getString(R.string.Global_Day) + " ";
            }
            if(diffHour > 0) {
                if(diffHour > 1)
                    tempDateText += diffHour + context.getString(R.string.Global_Hour) + context.getString(R.string.Global_plural) + " ";
                else
                    tempDateText += diffHour + context.getString(R.string.Global_Hour) + " ";
            }
            if(diffMinute > 0) {
                if(diffMinute > 1)
                    tempDateText += diffMinute + context.getString(R.string.Global_Minute) + context.getString(R.string.Global_plural) + " ";
                else
                    tempDateText += diffMinute + context.getString(R.string.Global_Minute) + " ";
            }


            Toast.makeText(context.getApplicationContext(), cancelShow+String.format(context.getString(R.string.AlarmReceiver_date_toast), tempDateText), Toast.LENGTH_LONG).show();

            if(date != Long.MIN_VALUE) {
                Intent localIntent = new Intent(context, NotificationReceiver.class);
                //10분단위로 id를 묶어서 중복을 방지한다.
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                        (int)(date/1000/60/10), localIntent, 0);
                am.set(AlarmManager.RTC_WAKEUP, date, pendingIntent);
            }
        }
    }
}
