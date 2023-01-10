package com.or2go.or2gopartner;

import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {
    final long Interval = 60000;
    Handler handler = new Handler();
    Timer timer = null;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("unsupport");
    }

    @Override
    public void onCreate() {
        if (timer != null){
            timer.cancel();
        }else{
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimeDisplay(), 0, Interval);
        }
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "destroy", Toast.LENGTH_SHORT).show();
        timer.cancel();
    }

    private class TimeDisplay extends TimerTask{
        @Override
        public void run() {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Uri notify = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), notify);
                    ringtone.play();
                    Toast.makeText(MyService.this, "notice", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
