package com.example.asiantech.musicdemo.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.asiantech.musicdemo.MainActivity;
import com.example.asiantech.musicdemo.MainActivity_;
import com.example.asiantech.musicdemo.MusicReceiver;
import com.example.asiantech.musicdemo.R;
import com.example.asiantech.musicdemo.model.Song;

import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private MediaPlayer mMediaPlayer;
    private ArrayList<Song> mListSong;
    private int mSongPosition;
    private IBinder mMusicBinder = new MusicBinder();
    private static final int NOTIFICATION_ID = 111;
    private String mMessage = "";
    private RemoteViews mRemoteViews;
    private Notification mNotification;
    private boolean mPause = false;

    public MusicService() {
    }

    @Override
    public void onCreate() {
        Log.d("TAG SERVICE", "onCreate");
        super.onCreate();
        mMediaPlayer = new MediaPlayer();
        initPlayer();
        createNotification();
    }

    private void initPlayer() {
        Log.d("TAG SERVICE", "initPlayer");
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("TAG SERVICE", "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMusicBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("TAG SERVICE", "onUnbind");
        return true;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d("TAG SERVICE", "onRebind");
        super.onRebind(intent);
        mMessage = "onRebind";
        sendBroadcast();
    }

    @Override
    public void onDestroy() {
        Log.d("TAG SERVICE", "onDestroy");
        if (isPlayMusic()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.release();
        super.onDestroy();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d("TAG SERVICE", "onCompletion");
        playNext();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d("TAG SERVICE", "onError");
        return false;
    }

    public void playSong() {
        Log.d("TAG SERVICE", "playSong");
        mMediaPlayer.reset();
        Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                mListSong.get(mSongPosition).getId());
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), uri);
        } catch (IOException e) {
            Log.e("MUSIC SERVICE", "Error set data source");
        }
        mMediaPlayer.prepareAsync();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d("TAG SERVICE", "onPrepared");
        mp.start();
        updateNotification();
        mMessage = "play";
        sendBroadcast();
    }

    public void setList(ArrayList<Song> listSong) {
        Log.d("TAG SERVICE", "setList");
        mListSong = listSong;
    }

    public void setSong(int position) {
        Log.d("TAG SERVICE", "setSong");
        this.mSongPosition = position;
    }

    private void createNotification() {
        Log.d("TAG SERVICE", "createNotification");
        mRemoteViews = new RemoteViews(getPackageName(), R.layout.notification_play_music);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.icon_notification)
                .setContentTitle("play music")
                .setContent(mRemoteViews);
        Intent intent = new Intent(this, MainActivity_.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        setListenerNotification();
        mNotification = builder.build();
    }

    private void setListenerNotification() {
        Intent intent = new Intent(this, MusicReceiver.class);
        intent.putExtra("action", "close");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.imgClose, pendingIntent);
        intent.putExtra("action", "play");
        pendingIntent = PendingIntent.getBroadcast(this, 2, intent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.imgPlay, pendingIntent);
        intent.putExtra("action", "back");
        pendingIntent = PendingIntent.getBroadcast(this, 3, intent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.imgBack, pendingIntent);
        intent.putExtra("action", "next");
        pendingIntent = PendingIntent.getBroadcast(this, 4, intent, 0);
        mRemoteViews.setOnClickPendingIntent(R.id.imgNext, pendingIntent);
    }

    private void updateNotification() {
        if (isPlaying()) {
            mRemoteViews.setImageViewResource(R.id.imgPlay, R.drawable.ic_pause);
        } else {
            mRemoteViews.setImageViewResource(R.id.imgPlay, R.drawable.ic_play);
        }
        mRemoteViews.setTextViewText(R.id.tvSongTitle, getSongTitle());
        startForeground(NOTIFICATION_ID, mNotification);
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            Log.d("TAG SERVICE", "getService");
            return MusicService.this;
        }
    }

    public void go() {
        Log.d("TAG SERVICE", "go");
        mMediaPlayer.start();
        mMessage = "play";
        sendBroadcast();
        updateNotification();
    }

    public void pausePlayer() {
        Log.d("TAG SERVICE", "pausePlayer");
        mMediaPlayer.pause();
        mPause = true;
        mMessage = "pause";
        sendBroadcast();
        updateNotification();
    }

    public void playNext() {
        Log.d("TAG SERVICE", "playNext");
        mSongPosition++;
        if (mSongPosition >= mListSong.size() - 1) {
            mSongPosition = 0;
        }
        playSong();
    }

    public void playPrev() {
        Log.d("TAG SERVICE", "playPrev");
        mSongPosition--;
        if (mSongPosition < 0) {
            mSongPosition = mListSong.size() - 1;
        }
        playSong();
    }

    public String getSongTitle() {
        return mListSong.get(mSongPosition).getTitle();
    }

    public int getCurPos() {
        return mMediaPlayer.getCurrentPosition();
    }

    public int getDur() {
        return mMediaPlayer.getDuration();
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void seekTo(int position) {
        mMediaPlayer.seekTo(position);
    }

    public void clickClose() {
        stopSelf();
        mMessage = "close";
        sendBroadcast();
    }

    public boolean isPlayMusic() {
        return isPlaying() || mPause;
    }

    private void sendBroadcast() {
        Log.d("TAG SERVICE", "sendBroadcast");
        Intent intent = new Intent();
        intent.setAction(MainActivity.ACTION_STRING_ACTIVITY);
        intent.putExtra("message", mMessage);
        sendBroadcast(intent);
    }
}
