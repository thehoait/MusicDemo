package com.example.asiantech.musicdemo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.asiantech.musicdemo.MainActivity_;
import com.example.asiantech.musicdemo.R;
import com.example.asiantech.musicdemo.adapter.ArtistAdapter;
import com.example.asiantech.musicdemo.model.Artist;
import com.example.asiantech.musicdemo.model.Song;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;

@EFragment(R.layout.artist_fragment)
public class ArtistFragment extends Fragment {
    private ArrayList<Song> mListSong;
    @ViewById(R.id.gvArtist)
    GridView mGvArtists;

    @AfterViews
    void afterView() {
        Log.d("TAG ARTIST_FRAGMENT", "afterView");
        if (getActivity() instanceof MainActivity_) {
            mListSong = ((MainActivity_) getActivity()).getMListSong();
        }
        final ArrayList<Artist> listArtist = new ArrayList<>();
        for (int i = 0; i < mListSong.size(); i++) {
            boolean exists = false;
            for (int j = 0; j < listArtist.size(); j++) {
                if (mListSong.get(i).getArtistId() == listArtist.get(j).getArtistId()) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                listArtist.add(new Artist(mListSong.get(i).getArtistId(),
                        mListSong.get(i).getArtist()));
            }
        }
        ArtistAdapter adapter = new ArtistAdapter(getContext(), listArtist);
        mGvArtists.setAdapter(adapter);
        mGvArtists.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArtistDetailFragment_ fragment = new ArtistDetailFragment_();
                Bundle bundle = new Bundle();
                bundle.putLong("artistId", listArtist.get(position).getArtistId());
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                        .beginTransaction();
                transaction.add(R.id.rlContainer, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }
}
