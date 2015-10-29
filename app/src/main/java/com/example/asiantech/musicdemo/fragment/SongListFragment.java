package com.example.asiantech.musicdemo.fragment;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.asiantech.musicdemo.MainActivity_;
import com.example.asiantech.musicdemo.OnItemListener;
import com.example.asiantech.musicdemo.R;
import com.example.asiantech.musicdemo.adapter.SongAdapter;
import com.example.asiantech.musicdemo.model.Song;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

@EFragment(R.layout.song_list_fragment)
public class SongListFragment extends Fragment {
    @ViewById(R.id.recycleViewListSong)
    RecyclerView mRecycleListSong;
    private SongAdapter mAdapter;
    private ArrayList<Song> mSongList;
    private OnItemListener mOnItemListener;

    @AfterViews
    void afterView() {
        Log.d("TAG SONG LIST FRAGMENT","afterView");
        mSongList = new ArrayList<>();
        if (getActivity() instanceof MainActivity_) {
            mSongList = ((MainActivity_) getActivity()).getMListSong();
            mOnItemListener = ((MainActivity_) getActivity()).getMOnItemListener();
        }
        mAdapter = new SongAdapter(getContext(), mSongList, mOnItemListener);
        mRecycleListSong.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycleListSong.setAdapter(mAdapter);
    }

    public void notifySongListAdapter() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        } else {
            Log.d("sss", "adapter is null");
        }

    }
}
