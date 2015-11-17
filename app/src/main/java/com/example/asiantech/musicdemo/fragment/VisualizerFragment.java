package com.example.asiantech.musicdemo.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.example.asiantech.musicdemo.MainActivity;
import com.example.asiantech.musicdemo.R;
import com.example.asiantech.musicdemo.service.MusicService;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.visualizor_fragment)
public class VisualizerFragment extends Fragment {
    @ViewById(R.id.imgCircleMusic)
    ImageView mImgCircleMusic;
    Animation mAnimation;
    MusicService mMusicService;

    @AfterViews
    void afterView() {
        Log.d("TAG VISUALIZER", "afterView");
        mAnimation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate_around_center_point);
        if (getActivity() instanceof MainActivity) {
            mMusicService = ((MainActivity) getActivity()).getMMusicService();
        }
        updateAnimation();
        if (mReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(MainActivity.ACTION_STRING_ACTIVITY);
            getActivity().registerReceiver(mReceiver, intentFilter);
        }
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("TAG VISUALIZER", "onReceive");
            if (intent != null) {
                String message = intent.getExtras().getString("message");
                if (message != null) {
                    updateAnimation();
                }
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Log.d("TAG VISUALIZER", "onResume");
    }

    public void updateAnimation() {
        Log.d("TAG VISUALIZER", "updateAnimation");
        if (mMusicService.isPlaying()) {
            mImgCircleMusic.startAnimation(mAnimation);
        } else {
            mImgCircleMusic.clearAnimation();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(mReceiver);
        mAnimation.cancel();
    }
}
