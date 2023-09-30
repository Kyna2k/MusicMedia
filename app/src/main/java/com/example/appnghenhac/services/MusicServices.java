package com.example.appnghenhac.services;

import static com.example.appnghenhac.MainActivity.index;
import static com.example.appnghenhac.MainActivity.isPlay;
import static com.example.appnghenhac.MainActivity.mediaPlayer;
import static com.example.appnghenhac.MainActivity.musicArrayList;
import static com.example.appnghenhac.MyApplication.CHANNEL_ID;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.NotificationTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.example.appnghenhac.MainActivity;
import com.example.appnghenhac.R;
import com.example.appnghenhac.Reciver;
import com.example.appnghenhac.model.Music;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutionException;

@RequiresApi(api = Build.VERSION_CODES.S)
public class MusicServices extends Service {


    public static final int ACTION_PAUSE = 1;
    public static final int ACTION_RESUME = 2;
    public static final int ACTION_NEXT = 4;
    public static final int ACTION_BACK = 3;
    Music _music = null;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Music music = (Music) bundle.getSerializable("music");
            if(music != null)
            {
                _music = music;
                sendNoti(music);
                startMusic(music);
            }


        }
        int _action = intent.getIntExtra("action",-1);
        action(_action);
        return START_NOT_STICKY;
    }

    private void startMusic(Music music) {
        if(mediaPlayer != null) return;
        mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(music.getFile()));
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setLooping(false);

        mediaPlayer.start();
    }


    @SuppressLint("CheckResult")
    private void sendNoti(Music music) {

//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//
//
//        RemoteViews remoteViews = new RemoteViews(getPackageName(),R.layout.notifcation);
//        remoteViews.setTextViewText(R.id.name_song, music.getName());
//        remoteViews.setTextViewText(R.id.singer_song, music.getSinger());
//        remoteViews.setImageViewResource(R.id.back,R.drawable.baseline_skip_previous_24);
//        remoteViews.setImageViewResource(R.id.next,R.drawable.baseline_skip_next_24);
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        if(isPlay)
//        {
//            remoteViews.setOnClickPendingIntent(R.id.play2,pendingIntent(this, ACTION_PAUSE));
//            remoteViews.setImageViewResource(R.id.play2,R.drawable.baseline_pause_circle_24);
//
//        }else{
//            remoteViews.setOnClickPendingIntent(R.id.play2,pendingIntent(this, ACTION_RESUME));
//            remoteViews.setImageViewResource(R.id.play2,R.drawable.baseline_play_circle_24);
//        }
//        NotificationTarget notificationTarget = new NotificationTarget(
//                this,
//                R.id.img_song,
//                remoteViews,
//                notification,
//                1);
//

        MediaSessionCompat _mediaSession = new MediaSessionCompat(this, "tag");
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.hihi);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_play_circle_24)
                .setContentTitle(music.getName())
                .setContentText(music.getSinger())
                .setSound(null)
                .setLargeIcon(bitmap)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .addAction(R.drawable.baseline_skip_previous_24,"Pe",pendingIntent(this, ACTION_BACK))
                .addAction(isPlay ? R.drawable.baseline_pause_circle_24 :R.drawable.baseline_play_circle_24 ,"PLAY", isPlay ? pendingIntent(this, ACTION_PAUSE) :pendingIntent(this, ACTION_RESUME))
                .addAction(R.drawable.baseline_skip_next_24,"NEXT",pendingIntent(this, ACTION_NEXT))
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(0,1,2).setMediaSession(_mediaSession.getSessionToken()));

        Notification notification = builder.build();
        startForeground(1,notification);
    }

    private PendingIntent pendingIntent(@NotNull Context context, int action)
    {
        Intent intent = new Intent(this, Reciver.class);
        intent.putExtra("SVACTION",action);
        intent.setAction("PLAY");
        return PendingIntent.getBroadcast(context.getApplicationContext(), action, intent,  PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    private void action(int action)
    {
        switch (action)
        {
            case ACTION_PAUSE:
                pause();
                break;
            case ACTION_RESUME:
                resume();
                break;
            case ACTION_NEXT:
                next();
                break;
            case ACTION_BACK:
                back();
                break;
            default: break;
        }
    }
    private void back() {
        mediaPlayer.release();
        mediaPlayer = null;
        index = index - 1 < 0 ? musicArrayList.size() -1 : index - 1 ;
        _music = musicArrayList.get(index);
        sendNoti(musicArrayList.get(index));
        startMusic(musicArrayList.get(index));
    }

    private void next() {
        mediaPlayer.release();
        mediaPlayer = null;
        index = index + 1 > musicArrayList.size() - 1 ? 0 : index + 1 ;
        _music = musicArrayList.get(index);
        sendNoti(musicArrayList.get(index));
        startMusic(musicArrayList.get(index));
    }

    private void resume() {
        if(mediaPlayer != null && !isPlay)
        {
            mediaPlayer.start();
            isPlay = true;
            sendNoti(_music);
        }
    }

    private void pause() {
        if(mediaPlayer != null && isPlay)
        {
            mediaPlayer.pause();
            isPlay = false;
            sendNoti(_music);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}
