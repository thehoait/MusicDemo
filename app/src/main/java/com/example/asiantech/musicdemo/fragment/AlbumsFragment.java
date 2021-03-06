package com.example.asiantech.musicdemo.fragment;

import android.content.ContentUris;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.asiantech.musicdemo.MainActivity_;
import com.example.asiantech.musicdemo.R;
import com.example.asiantech.musicdemo.adapter.AlbumAdapter;
import com.example.asiantech.musicdemo.model.Album;
import com.example.asiantech.musicdemo.model.Song;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.util.ArrayList;

@EFragment(R.layout.albums_fragment)
public class AlbumsFragment extends Fragment {
    private ArrayList<Song> mListSong;
    private ArrayList<Album> mListAlbum;
    @ViewById(R.id.gvAlbums)
    GridView mGvAlbums;

    @AfterViews
    void afterView() {
        Log.d("TAG ALBUMS_FRAGMENT", "afterView");
        if (getActivity() instanceof MainActivity_) {
            mListSong = ((MainActivity_) getActivity()).getMListSong();
        }
        mListAlbum = new ArrayList<>();
        for (int i = 0; i < mListSong.size(); i++) {
            boolean exists = false;
            for (int j = 0; j < mListAlbum.size(); j++) {
                if (mListSong.get(i).getAlbumId() == mListAlbum.get(j).getAlbumId()) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                mListAlbum.add(new Album(mListSong.get(i).getAlbumId(), mListSong.get(i).getAlbum(),
                        getImageBitmap(mListSong.get(i).getAlbumId())));
            }
        }
        AlbumAdapter adapter = new AlbumAdapter(getContext(), mListAlbum);
        mGvAlbums.setAdapter(adapter);

        mGvAlbums.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlbumDetailFragment_ fragment = new AlbumDetailFragment_();
                Bundle bundle = new Bundle();
                bundle.putLong("albumId", mListAlbum.get(position).getAlbumId());
                fragment.setArguments(bundle);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager()
                        .beginTransaction();
                transaction.add(R.id.rlContainer, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }

    private Bitmap getImageBitmap(long albumId) {
        Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
        Uri albumArtUri = ContentUris.withAppendedId(sArtworkUri, albumId);
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),
                    albumArtUri);
            bitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
