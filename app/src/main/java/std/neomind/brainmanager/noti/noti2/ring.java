package std.neomind.brainmanager.noti.noti2;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

import std.neomind.brainmanager.R;


public class ring extends Service {

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

        String getState = intent.getExtras().getString("state");

        mediaPlayer = MediaPlayer.create(this, R.raw.ouu);
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
        else
        {
            mediaPlayer.start();
        }
        return START_NOT_STICKY;
    }
}