//package std.neomind.brainmanager.noti.noti2;
//
//import android.app.Activity;
//import android.app.AlarmManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Build;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//
//import androidx.annotation.RequiresApi;
//
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//
//import std.neomind.brainmanager.R;
//
//public class main22 extends Activity {
//
//    private static int ONE_MINUTE = 5626;
//    long now = System.currentTimeMillis();
//    Date date = new Date(now);
//    SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm");
//    String formatDate = sdfNow.format(date);
//    TextView dateNow;
//
//
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.notiscreen);
//        new main22.AlarmHATT(getApplicationContext()).Alarm();
//
//        dateNow = findViewById(R.id.dateNow);
//        dateNow.setText(formatDate);
//
//    }
//
//    public class AlarmHATT {
//        Context context;
//        PendingIntent pendingIntent;
//        public AlarmHATT(Context context) {
//            this.context = context;
//        }
//
//        public void Alarm() {
//            final AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//            final Intent intent = new Intent(this.context, std.neomind.brainmanager.noti.noti2.BroadcastD2.class);
//
//            //펜딩인텐트에 브로드캐스트 인텐트를 대입한다.
//            pendingIntent = PendingIntent.getBroadcast(main22.this, 0, intent, 0);
//
//            Calendar calendar = Calendar.getInstance();
////
//            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 22, 6, 0);
//            intent.putExtra("state","alarm on");
//            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//
//            Button alarm_off = findViewById(R.id.off_bt);
//            alarm_off.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    am.cancel(pendingIntent);
//
//                    intent.putExtra("state","alarm off");
//                    sendBroadcast(intent);
//                    finish();
//                }
//            });
//        }
//    }
//}