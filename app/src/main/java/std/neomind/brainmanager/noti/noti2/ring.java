package std.neomind.brainmanager.noti.noti2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import std.neomind.brainmanager.R;
import std.neomind.brainmanager.noti.Broadcast22;


public class ring extends Service {

    static public final String EXTRAS_ALARM_MODE = "newAlarm";
    static public final String EXTRAS_KEYS_ID = "keywordId";
    static public final String EXTRAS_KEY_REVIEW_DATE = "keywordReviewDate";
    static public final String EXTRAS_WAKE_UP = "wakeUp";

    ArrayList<Integer> reviewList;      //이 두개가 empty값이면 재부팅했을 때 아무것도 없다는 뜻이므로 알람을 생성하면 안됨.
    ArrayList<Long> reviewDateList;

    MediaPlayer mediaPlayer;
    int startId;
    boolean isRunning;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int id;     //ArrayList에 들어가 객체로 저장될 id변수
        long date;  //ArrayList에 들어가 객체로 저장될 date변수

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

        //서비스가 실행될 때 직렬화되어 저장된 객체를 불러옴.
        try {
            FileInputStream fileStream = new FileInputStream(new File(getFilesDir(),"BrainAlarm.data"));
            try {
                ObjectInputStream os = new ObjectInputStream(fileStream);

                reviewList = (ArrayList<Integer>)os.readObject();
                reviewDateList = (ArrayList<Long>) os.readObject();
        //y
                os.close();
            }
            catch (Exception ioe){
                ioe.printStackTrace();
            }
            finally {
                try {
                    fileStream.close();
                }
                catch (IOException ioee){
                    ioee.printStackTrace();
                }
            }
        }
        catch (FileNotFoundException e){    //새로 만드는 부분 => 없앰
        //e
            e.printStackTrace();
        }

        String wakeup = intent.getExtras().getString(EXTRAS_WAKE_UP);

        //watkeup이 지정되면 모든 ArrayList<Long>을 알람으로 시작해야됨.
        //물론 List들이 null이나 size가 0이 아니어야 한다.
        if(wakeup != null){
            if(reviewList.isEmpty() && reviewDateList.isEmpty()){
                Intent localIntent = new Intent(this, Broadcast22.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, localIntent, 0);
                for(long temp_date : reviewDateList){
                    am.set(AlarmManager.RTC_WAKEUP, temp_date, pendingIntent);
                }
            }
        }
        //c
        else{   //만약 wakeup이 지정이 안되어있을 경우
            Intent localIntent = new Intent(this, Broadcast22.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, localIntent, 0);
            am.set(AlarmManager.RTC_WAKEUP, date, pendingIntent);
        }

        String getState = intent.getExtras().getString("state");

        mediaPlayer = MediaPlayer.create(this, R.raw.birds);
        if (getState != null) {
            switch (getState) {
                case "alarm on":
                    mediaPlayer.start();
                    break;
                case "alarm off":
                    mediaPlayer.stop();
                    break;
                default:
                    mediaPlayer.start();
                    break;
            }
        }
        stopSelf();
        return START_NOT_STICKY ;
    }
}