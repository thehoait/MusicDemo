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
public class AlbumDetailFragment extends Fragment {
    @ViewById(R.id.recycleViewListSong)
    RecyclerView mRecycleListSong;
    private OnItemListener mOnItemListener;
    private long mAlbumId;

    @AfterViews
    void afterView() {
        Log.d("TAG DETAIL FRAGMENT", "afterView");
        ArrayList<Song> listSong = new ArrayList<>();
        if (getArguments() != null) {
            mAlbumId = getArguments().getLong("albumId");
        }
        if (getActivity() instanceof MainActivity_) {
            listSong = ((MainActivity_) getActivity()).getMListSong();
            mOnItemListener = ((MainActivity_) getActivity()).getMOnItemListener();
        }
        ArrayList<Song> listSongAlbum = new ArrayList<>();
        for (int i = listSong.size() - 1; i >= 0; i--) {
            if (listSong.get(i).getAlbumId() == mAlbumId) {
                listSongAlbum.add(listSong.get(i));
            }
        }
        SongAdapter adapter = new SongAdapter(getContext(), listSongAlbum, mOnItemListener);
        mRecycleListSong.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecycleListSong.setAdapter(adapter);
    }
}
