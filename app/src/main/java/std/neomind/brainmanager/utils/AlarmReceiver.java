package std.neomind.brainmanager.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import std.neomind.brainmanager.R;

public class AlarmReceiver extends BroadcastReceiver{
    static public final String TAG = "AlarmReceiver";
    static public final String EXTRAS_KEY_REVIEW_DATE = "keywordReviewDate";

    @Override
    public void onReceive(Context context, Intent intent) {
        final AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        /**
         * 폰 재시작 할때 브로드캐스트 등록
         */
        if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())){
            Log.i("RestartService" , "ACTION_BOOT_COMPLETED" );

            ArrayList<Integer> idList = new ArrayList<>();
            ArrayList<Long> DateList = new ArrayList<>();
            try {
                BrainSerialDataIO.getNextReviewTimeInfo(context, idList, DateList);
            } catch (BrainSerialDataIO.LoadFailException e) {
                e.printStackTrace();
                Log.d(TAG, "시리얼 파일 불러오기 실패");
                return;
            } catch (BrainSerialDataIO.ListNotEqualSizeException e) {
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
            try {
                date = intent.getExtras().getLong(EXTRAS_KEY_REVIEW_DATE);
            }catch (NullPointerException ex){
                Toast.makeText(context.getApplicationContext(), context.getString(R.string.AlarmReceiver_Error), Toast.LENGTH_LONG).show();
                Log.d(TAG, "getExtras 에서 오류 발생");
                return;
            }
            if(date != Long.MIN_VALUE) {
                Intent localIntent = new Intent(context, NotificationReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, localIntent, 0);
                am.set(AlarmManager.RTC_WAKEUP, date, pendingIntent);
            }
        }
    }
}
