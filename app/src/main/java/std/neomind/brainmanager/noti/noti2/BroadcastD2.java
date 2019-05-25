package std.neomind.brainmanager.noti.noti2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BroadcastD2 extends BroadcastReceiver{

    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        String get_yout_string = intent.getExtras().getString("state");

        Intent service_intent = new Intent(context, ring.class);
        service_intent.putExtra("state", get_yout_string);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            this.context.startForegroundService(service_intent);
        }else{
            this.context.startService(service_intent);
        }
    }
}
