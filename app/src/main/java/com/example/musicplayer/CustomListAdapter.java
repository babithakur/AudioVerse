package com.example.musicplayer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.ObjectKey;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class CustomListAdapter extends ArrayAdapter<Song> {
    private int selectedPosition = -1;
    public CustomListAdapter(Context context, ArrayList<Song> songs) {
        super(context, 0, songs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Song song = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.listview_items, parent, false);
        }
        TextView songName = convertView.findViewById(R.id.song_name);
        TextView artistName = convertView.findViewById(R.id.artist_name);
        songName.setText(song.getTitle());
        artistName.setText(song.getArtist());
        if (position == selectedPosition) {
            convertView.setBackgroundColor(Color.parseColor("#3da2f5")); 
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT); 
        }

        return convertView;
    }
    public void setSelectedPosition(int position) {
        selectedPosition = position;
        notifyDataSetChanged(); 
    }
}

