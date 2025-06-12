package com.example.musicplayer;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;

public class PlaySong {
    MediaPlayer mediaPlayer;
    ImageButton prevButton, nextButton, pauseButton;
    SeekBar seekBar;
    TextView songPlayed, artistPlayed, elapsedTime, totalDuration;
    ArrayList<String> songTitles = new ArrayList<>();
    ArrayList<String> songPaths = new ArrayList<>();
    ArrayList<String> songArtists = new ArrayList<>();
    ArrayList<Bitmap> albumArts = new ArrayList<>();
    CustomListAdapter adapter;
    Context context;
    public PlaySong(Context context, CustomListAdapter adapter, ArrayList<String> songTitles, ArrayList<String> songPaths, ArrayList<String> songArtists){
        this.context = context;
        this.adapter = adapter;
        this.songTitles = songTitles;
        this.songArtists = songArtists;
        this.songPaths = songPaths;
    }
    public void playSong(int songIndex, String songTitle, String songArtist, String selectedPath) {
        adapter.setSelectedPosition(songIndex);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        pauseButton.setImageResource(R.drawable.circlepausesolid);
        mediaPlayer = new MediaPlayer();
        try {
            songPlayed.setText(songTitle);
            artistPlayed.setText(songArtist);
            mediaPlayer.setDataSource(selectedPath); 
            mediaPlayer.prepare(); 
            mediaPlayer.start(); 

            mediaPlayer.setOnPreparedListener(mp -> {
                seekBar.setMax(mp.getDuration()); 
                int duration = mediaPlayer.getDuration(); 
                totalDuration.setText(formatTime(duration));

                Handler handler = new Handler();
                Runnable updateSeekBar = new Runnable() {
                    @Override
                    public void run() {
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            int currentPosition = mediaPlayer.getCurrentPosition(); 
                            elapsedTime.setText(formatTime(currentPosition));
                            seekBar.setProgress(mediaPlayer.getCurrentPosition()); 
                            handler.postDelayed(this, 1000); 
                        }
                    }
                };
                handler.removeCallbacks(updateSeekBar);
                handler.post(updateSeekBar); 
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser && mediaPlayer != null) {
                            mediaPlayer.seekTo(progress); 
                            elapsedTime.setText(formatTime(progress));
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                        handler.removeCallbacks(updateSeekBar); 
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        handler.post(updateSeekBar); 
                });
                
                prevButton.setOnClickListener(v -> {
                    int prevSongIndex = songIndex - 1; 
                    if (prevSongIndex < songPaths.size() && prevSongIndex >= 0) {
                        String nextSelectedPath = songPaths.get(prevSongIndex); 
                        String nextSongTitle = songTitles.get(prevSongIndex);
                        String nextSongArtist = songArtists.get(prevSongIndex);
                        playSong(prevSongIndex, nextSongTitle, nextSongArtist, nextSelectedPath); 
                    }
                });

                pauseButton.setOnClickListener(v -> {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.pause(); 
                        handler.removeCallbacks(updateSeekBar); 
                        pauseButton.setImageResource(R.drawable.circleplaysolid);
                    }else{
                        pauseButton.setImageResource(R.drawable.circlepausesolid);
                        mediaPlayer.start();
                        handler.post(updateSeekBar);
                    }
                });
                nextButton.setOnClickListener(v ->{
                    int nextSongIndex = songIndex + 1; 
                    if (nextSongIndex < songPaths.size()) {
                        String nextSelectedPath = songPaths.get(nextSongIndex); 
                        String nextSongTitle = songTitles.get(nextSongIndex);
                        String nextSongArtist = songArtists.get(nextSongIndex);
                        playSong(nextSongIndex, nextSongTitle, nextSongArtist, nextSelectedPath); 
                        String firstSelectedPath = songPaths.get(0); 
                        String firstSongTitle = songTitles.get(0);
                        String firstSongArtist = songArtists.get(0);
                        playSong(0, firstSongTitle, firstSongArtist, firstSelectedPath);
                    }
                });
            });
            mediaPlayer.setOnCompletionListener(mp -> {
                int nextSongIndex = songIndex + 1; 
                if (nextSongIndex < songPaths.size()) {
                    String nextSelectedPath = songPaths.get(nextSongIndex); 
                    String nextSongTitle = songTitles.get(nextSongIndex);
                    String nextSongArtist = songArtists.get(nextSongIndex);
                    playSong(nextSongIndex, nextSongTitle, nextSongArtist, nextSelectedPath); 
                } else {
                    String firstSelectedPath = songPaths.get(0); 
                    String firstSongTitle = songTitles.get(0);
                    String firstSongArtist = songArtists.get(0);
                    playSong(0, firstSongTitle, firstSongArtist, firstSelectedPath);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String formatTime(int timeInMillis) {
        int minutes = (timeInMillis / 1000) / 60;
        int seconds = (timeInMillis / 1000) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    public Bitmap getAlbumArt(Uri albumArtUri) {
        Bitmap albumArt = null;
        try {
            Cursor cursor = context.getContentResolver().query(albumArtUri, new String[]{MediaStore.Audio.Albums.ALBUM_ART}, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                String artPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                if (artPath != null) {
                    albumArt = BitmapFactory.decodeFile(artPath);
                }
                cursor.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return albumArt;
    }

}

