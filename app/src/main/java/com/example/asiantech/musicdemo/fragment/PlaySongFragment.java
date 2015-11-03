package com.example.asiantech.musicdemo.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.asiantech.musicdemo.MainActivity;
import com.example.asiantech.musicdemo.R;
import com.example.asiantech.musicdemo.service.MusicService;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.Formatter;
import java.util.Locale;

@EFragment(R.layout.play_song_fragment)
public class PlaySongFragment extends Fragment {
    @ViewById(R.id.tvSongTitle)
    TextView mTvSongTitle;
    @ViewById(R.id.seekBar)
    SeekBar mSeekBar;
    @ViewById(R.id.tvCurrentTime)
    TextView mTvCurrentTime;
    @ViewById(R.id.tvSongTime)
    TextView mTvSongTime;
    @ViewById(R.id.imgPlay)
    ImageView mImgPlay;
    @ViewById(R.id.imgShuffle)
    ImageView mImgShuffle;
    @ViewById(R.id.imgRepeat)
    ImageView mImgRepeat;
    private MusicService mMusicService;
    private StringBuilder mFormatBuilder;
    private Formatter mFormatter;
    private Handler mHandler = new Handler();

    @AfterViews
    void afterView() {
        if (getActivity() instanceof MainActivity) {
            mMusicService = ((MainActivity) getActivity()).getMMusicService();
        }
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        mSeekBar.setMax(1000);
        mTvSongTitle.setSelected(true);
        setSongTitle();
        updatePlayPause();
        setSongTime();
        updateProgress();
        if (mReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(MainActivity.ACTION_STRING_ACTIVITY);
            getActivity().registerReceiver(mReceiver, intentFilter);
        }
        mSeekBar.setOnSeekBarChangeListener(mListener);
    }

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

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            updateProgress();
        }
    };

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String message = intent.getExtras().getString("message");
                if (message != null) {
                    switch (message) {
                        case "play":
                            updateProgress();
                            setSongTime();
                            setSongTitle();
                            break;
                        case "pause":
                            updatePlayPause();
                            break;
                    }
                }
            }
        }
    };

    @Click(R.id.imgPlay)
    void onClickPlay() {
        if (mMusicService.isPlaying()) {
            mMusicService.pausePlayer();
        } else {
            mMusicService.go();
        }
        updatePlayPause();
    }

    @Click(R.id.imgNext)
    void onClickNext() {
        resetController();
        mMusicService.playNext();
    }

    @Click(R.id.imgBack)
    void onClickBack() {
        resetController();
        mMusicService.playPrev();
    }

    @Click(R.id.imgShuffle)
    void onClickShuffle() {
        mMusicService.setShuffle(!mMusicService.isShuffle());
        updateShuffle();
    }

    @Click(R.id.imgRepeat)
    void onClickRepeat() {
        mMusicService.setRepeat(!mMusicService.isRepeat());
        updateRepeat();
    }

    private void updateRepeat() {
        mImgRepeat.setSelected(mMusicService.isRepeat());
    }

    private void updateShuffle() {
        mImgShuffle.setSelected(mMusicService.isShuffle());
    }

    private void setSongTime() {
        Log.d("TAG ACTIVITY", "setSongTime");
        int duration = mMusicService.getDur();
        mTvSongTime.setText(stringForTime(duration));
    }

    private void setSongTitle() {
        Log.d("TAG ACTIVITY", "setSongTime");
        mTvSongTitle.setText(mMusicService.getSongTitle());
    }

    private void updateProgress() {
        int duration = mMusicService.getDur();
        int currentPosition = mMusicService.getCurPos();
        if (duration > 0) {
            long position = 1000L * currentPosition / duration;
            mSeekBar.setProgress((int) position);
        }
        mTvCurrentTime.setText(stringForTime(currentPosition));
        mHandler.postDelayed(mRunnable, 1000);
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

    private void updatePlayPause() {
        Log.d("TAG ACTIVITY", "updatePlayPause");
        if (mMusicService.isPlaying()) {
            mImgPlay.setImageResource(R.drawable.button_pause);
        } else {
            mImgPlay.setImageResource(R.drawable.button_play);
        }
    }

    public void resetController() {
        Log.d("TAG SONG_FRAGMENT", "resetController");
        if (mHandler != null && mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    @Override
    public void onDestroy() {
        Log.d("TAG SONG_FRAGMENT", "onDestroy");
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
        getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();
    }
}
