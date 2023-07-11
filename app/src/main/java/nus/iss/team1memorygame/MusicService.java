package nus.iss.team1memorygame;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;

public class MusicService extends Service {
    private static final int FOREGD_NOTIFY_ID = 1;
    private MediaPlayer player = null;

    private static final String CHANNEL_ID = "MyMusicService_Channel";

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public void onCreate() {
        super.onCreate();
        startInForeground();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();

        if (action != null) {
            if (action.equalsIgnoreCase("play")) {
                playSong(0);
            }
            else if (action.equalsIgnoreCase("downloaded")){
                playSong(1);
            }
            else if (action.equalsIgnoreCase("stop")) {
                stopSong();
            }

        }

        return super.onStartCommand(intent, flags, startId);
    }
    protected void playSong(int i) {
        if (player != null) {
            stopSong();
        }
        if(i==0){
            player = MediaPlayer.create(this, R.raw.bgm2);
        }else {
            player = MediaPlayer.create(this, R.raw.sunny);
        }

        player.start();

        onPlay();
    }
    protected void stopSong() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }

        onStop();
    }
    protected void onPlay()
    {

        NotificationManager notifMgr = getSystemService(NotificationManager.class);
        Notification notification = createNotification("BGM",
                "Playing \"" + "bgm" + ".mp3\"");
        notifMgr.notify(FOREGD_NOTIFY_ID, notification);
    }

    protected void onStop() {
        NotificationManager notifMgr = getSystemService(NotificationManager.class);
        Notification notification = createNotification("bgm", "");
        notifMgr.notify(FOREGD_NOTIFY_ID, notification);
    }

    protected void startInForeground() {
        createNotificationChannel();

        Notification notification = createNotification("BGM", "");
        startForeground(FOREGD_NOTIFY_ID, notification);
    }
    protected void createNotificationChannel() {
        NotificationManager notifMgr = getSystemService(NotificationManager.class);
        NotificationChannel serviceChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID,
                NotificationManager.IMPORTANCE_DEFAULT);
        notifMgr.createNotificationChannel(serviceChannel);
    }
    protected Notification createNotification(String title, String text) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, intent, PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.music)
                .setContentIntent(pendingIntent)
                .build();
    }
}