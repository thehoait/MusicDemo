package com.example.asiantech.musicdemo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.asiantech.musicdemo.adapter.SongAdapter;
import com.example.asiantech.musicdemo.model.Song;
import com.example.asiantech.musicdemo.service.MusicService;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.Locale;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity implements OnItemListener {

    @ViewById(R.id.recycleViewListSong)
    RecyclerView mRecycleListSong;
    @ViewById(R.id.imgPlay)
    ImageView mImgPlay;
    @ViewById(R.id.seekBar)
    SeekBar mSeekBar;
    @ViewById(R.id.tvCurrentTime)
    TextView mTvCurrentTime;
    @ViewById(R.id.tvSongTime)
    TextView mTvSongTime;
    @ViewById(R.id.llController)
    LinearLayout mController;
    @ViewById(R.id.tvSongTitle)
    TextView mTvSongTitle;
    @ViewById(R.id.imgRepeat)
    ImageView mImgRepeat;
    private ArrayList<Song> mListSong;
    private boolean mBound;
    private MusicService mMusicService;
    private Intent mPlayIntent;
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    private Handler mHandler = new Handler();
    public static final String ACTION_STRING_ACTIVITY = "ToActivity";

    @AfterViews
    void afterView() {
        Log.d("TAG ACTIVITY", "afterView");
        mListSong = new ArrayList<>();
        getListSong();
        Collections.sort(mListSong, new Comparator<Song>() {
            public int compare(Song a, Song b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });
        SongAdapter adapter = new SongAdapter(this, mListSong, this);
        mRecycleListSong.setLayoutManager(new LinearLayoutManager(this));
        mRecycleListSong.setAdapter(adapter);
        mController.setVisibility(View.GONE);
        mTvSongTitle.setSelected(true);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        mSeekBar.setMax(1000);
        mSeekBar.setOnSeekBarChangeListener(mListener);
        if (mReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(ACTION_STRING_ACTIVITY);
            registerReceiver(mReceiver, intentFilter);
        }
    }

    private ServiceConnection mMusicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("TAG ACTIVITY", "onServiceConnected");
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            mMusicService = binder.getService();
            mMusicService.setList(mListSong);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("TAG ACTIVITY", "onServiceDisconnected");
            mBound = false;
        }
    };

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("TAG ACTIVITY", "onReceive");
            if (intent != null) {
                String message = intent.getExtras().getString("message");
                if (message != null) {
                    switch (message) {
                        case "play":
                            showController();
                            break;
                        case "pause":
                            updatePlayPause();
                            break;
                        case "onRebind":
                            showController();
                            break;
                        case "close":
                            mMusicService = null;
                            finish();
                    }
                }
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener mListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser) {
                return;
            }
            mMusicService.seekTo(progress * mMusicService.getDur() / 1000);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    private void getListSong() {
        Log.d("TAG ACTIVITY", "getListSong");
        ContentResolver musicResolver = getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        Cursor musicCursor = musicResolver.query(musicUri, null, MediaStore.Audio.Media.IS_MUSIC +
                " !=0", null, null);
        if (musicCursor != null && musicCursor.moveToFirst()) {

            int titleColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.DISPLAY_NAME);
            int idColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (MediaStore.Audio.Media.ARTIST);
            do {
                long thisId = musicCursor.getLong(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                mListSong.add(new Song(thisId, thisTitle, thisArtist));
            }
            while (musicCursor.moveToNext());
        }
        if (musicCursor != null) {
            musicCursor.close();
        }
    }

    @Click(R.id.imgPlay)
    void onClickPlay() {
        Log.d("TAG ACTIVITY", "onClickPlay");
        if (mMusicService != null && mBound) {
            if (isPlaying()) {
                pause();
            } else {
                start();
            }
        }
    }

    @Click(R.id.imgNext)
    void onClickNext() {
        Log.d("TAG ACTIVITY", "onClickNext");
        mMusicService.playNext();
    }

    @Click(R.id.imgBack)
    void onClickPrev() {
        Log.d("TAG ACTIVITY", "onClickPrev");
        mMusicService.playPrev();
    }

    @Click(R.id.imgRepeat)
    void onClickRepeat() {
        Log.d("TAG ACTIVITY", "onClickRepeat");
        mMusicService.setRepeat(!isRepeat());
        updateRepeat();
    }

    private void updatePlayPause() {
        Log.d("TAG ACTIVITY", "updatePlayPause");
        if (isPlaying()) {
            mImgPlay.setImageResource(R.drawable.ic_pause);
        } else {
            mImgPlay.setImageResource(R.drawable.ic_play);
        }
    }

    private void updateRepeat() {
        Log.d("TAG ACTIVITY", "updateRepeat");
        if (isRepeat()) {
            mImgRepeat.setImageResource(R.drawable.ic_repeat);
        } else {
            mImgRepeat.setImageResource(R.drawable.ic_none_repeat);
        }
    }

    private void updateProgress() {
        int duration = getDuration();
        int currentPosition = getCurrentPosition();
        if (duration > 0) {
            long position = 1000L * currentPosition / duration;
            mSeekBar.setProgress((int) position);
        }
        mTvCurrentTime.setText(stringForTime(currentPosition));
        mHandler.postDelayed(mRunnable, 1000);

    }

    private void setSongTime() {
        Log.d("TAG ACTIVITY", "setSongTime");
        int duration = getDuration();
        mTvSongTime.setText(stringForTime(duration));
    }

    private String getSongTitle() {
        if (mMusicService != null && mBound) {
            return mMusicService.getSongTitle();
        }
        return null;
    }

    private void start() {
        Log.d("TAG ACTIVITY", "start");
        mMusicService.go();
    }

    private void pause() {
        Log.d("TAG ACTIVITY", "pause");
        mMusicService.pausePlayer();
    }

    private int getCurrentPosition() {
        if (mMusicService != null && mBound) {
            return mMusicService.getCurPos();
        }
        return 0;

    }

    private int getDuration() {
        if (mMusicService != null && mBound) {
            return mMusicService.getDur();
        }
        return 0;
    }

    private boolean isPlaying() {
        return mMusicService != null && mBound && mMusicService.isPlaying();

    }

    private boolean isRepeat() {
        return mMusicService != null && mBound && mMusicService.isRepeat();

    }

    @Override
    protected void onStart() {
        Log.d("TAG ACTIVITY", "onStart");
        super.onStart();
        connectService();
    }

    private void connectService() {
        Log.d("TAG ACTIVITY", "connectService");
        if (mPlayIntent == null) {
            mPlayIntent = new Intent(this, MusicService.class);
        }
        if (!isServiceRunning()) {
            startService(mPlayIntent);
        }
        bindService(mPlayIntent, mMusicConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onRestart() {
        Log.d("TAG ACTIVITY", "onRestart");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        Log.d("TAG ACTIVITY", "onResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d("TAG ACTIVITY", "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("TAG ACTIVITY", "onStop");
        unbindService(mMusicConnection);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("TAG ACTIVITY", "onDestroy");
        if (mMusicService != null && mBound && !mMusicService.isPlayMusic()) {
            stopService(mPlayIntent);
        }
        mPlayIntent = null;
        mMusicService = null;
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    private boolean isServiceRunning() {
        Log.d("TAG ACTIVITY", "isServiceRunning");
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(
                Integer.MAX_VALUE)) {
            if (MusicService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    @Override
    public void onItemClick(int position) {
        Log.d("TAG ACTIVITY", "onItemClick");
        mMusicService.setSong(position);
        mMusicService.playSong();
    }

    private void showController() {
        mController.setVisibility(View.VISIBLE);
        setSongTime();
        mTvSongTitle.setText(getSongTitle());
        updatePlayPause();
        updateRepeat();
        updateProgress();
    }
}
