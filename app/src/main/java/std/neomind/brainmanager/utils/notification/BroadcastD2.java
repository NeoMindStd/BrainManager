package std.neomind.brainmanager.utils.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BroadcastD2 extends BroadcastReceiver{
    static public final String EXTRAS_KEYS_ID = "keywordId";
    static public final String EXTRAS_KEY_REVIEW_DATE = "keywordReviewDate";

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        /**
         * 폰 재시작 할때 서비스 등록
         */
        if(intent.getAction() != null){
            Log.i("RestartService" , "ACTION_BOOT_COMPLETED" );
            Intent i = new Intent(context, ring.class);
            i.putExtra(ring.EXTRAS_WAKE_UP, ring.EXTRAS_WAKE_UP);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
                this.context.startForegroundService(i);
            }else{
                this.context.startService(i);
            }
        }
        else {  /** 폰 재시작할 경우가 아닌경우 **/
            int id;
            long date;
            try {
                id = intent.getExtras().getInt(EXTRAS_KEYS_ID);
            }catch (NullPointerException ex){
                id = -1;
            }
            try{
                date = intent.getExtras().getLong(EXTRAS_KEY_REVIEW_DATE);
            }catch (NullPointerException ex){
                date = Long.MIN_VALUE;
            }
            if(id != -1 && date != Long.MIN_VALUE) {
                Intent service_intent = new Intent(context, ring.class);
                service_intent.putExtra(ring.EXTRAS_KEYS_ID, id);
                service_intent.putExtra(ring.EXTRAS_KEY_REVIEW_DATE, date);
                //service_intent.putExtra(ring.EXTRAS_KEY_REVIEW_DATE, System.currentTimeMillis()+60000); //테스트용.

//        service_intent.putExtra("state", get_yout_string);

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    this.context.startForegroundService(service_intent);
                } else {
                    this.context.startService(service_intent);
                }
            }
        }

    }
}
