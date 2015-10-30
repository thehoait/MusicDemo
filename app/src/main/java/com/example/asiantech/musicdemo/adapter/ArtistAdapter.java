package com.example.asiantech.musicdemo.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.asiantech.musicdemo.R;
import com.example.asiantech.musicdemo.model.Artist;

import java.util.ArrayList;

public class ArtistAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Artist> mListArtist;

    public ArtistAdapter(Context context, ArrayList<Artist> listArtist) {
        this.mContext = context;
        this.mListArtist = listArtist;
    }

    @Override
    public int getCount() {
        return mListArtist.size();
    }

    @Override
    public Object getItem(int position) {
        return mListArtist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_list_album, parent,
                    false);
        }
        TextView tvNameAlbum = (TextView) convertView.findViewById(R.id.tvNameAlbum);
        tvNameAlbum.setText(mListArtist.get(position).getArtist());
        return convertView;
    }
}
