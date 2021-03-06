package com.example.asiantech.musicdemo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.asiantech.musicdemo.OnItemListener;
import com.example.asiantech.musicdemo.R;
import com.example.asiantech.musicdemo.model.Song;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private Context mContext;
    private ArrayList<Song> mListSong;
    private OnItemListener mListener;

    public SongAdapter(Context context, ArrayList<Song> listSong, OnItemListener listener) {
        this.mContext = context;
        this.mListSong = listSong;
        this.mListener = listener;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_song, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        Song song = mListSong.get(position);
        holder.mTvTitle.setText(song.getTitle());
        holder.mTvArtist.setText(song.getArtist());
        if (song.isPlaying()) {
            holder.mTvTitle.setTextColor(Color.parseColor("#FF4081"));
        } else {
            holder.mTvTitle.setTextColor(Color.parseColor("#ffffff"));
        }
    }

    @Override
    public int getItemCount() {
        return mListSong.size();
    }

    class SongViewHolder extends RecyclerView.ViewHolder {
        private TextView mTvTitle;
        private TextView mTvArtist;

        public SongViewHolder(View itemView) {
            super(itemView);
            mTvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            mTvArtist = (TextView) itemView.findViewById(R.id.tvArtist);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onItemClick(getLayoutPosition());
                    }
                }
            });
        }
    }
}
