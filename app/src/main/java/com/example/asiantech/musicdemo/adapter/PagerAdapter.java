package com.example.asiantech.musicdemo.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.asiantech.musicdemo.fragment.AlbumsFragment_;
import com.example.asiantech.musicdemo.fragment.SongListFragment_;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private String[] mTitles = {"Songs", "Albums", "Artist"};
    private Fragment songListFragment = new SongListFragment_();

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return songListFragment;
            case 1:
                return new AlbumsFragment_();
            case 2:
                return new AlbumsFragment_();
        }
        return null;
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}
