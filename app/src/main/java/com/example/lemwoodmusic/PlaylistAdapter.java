package com.example.lemwoodmusic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class PlaylistAdapter extends ArrayAdapter<Playlist> {
    
    public PlaylistAdapter(Context context, List<Playlist> playlists) {
        super(context, 0, playlists);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Playlist playlist = getItem(position);
        
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.playlist_item, parent, false);
        }
        
        TextView nameTextView = convertView.findViewById(R.id.playlist_name);
        TextView countTextView = convertView.findViewById(R.id.song_count);
        
        nameTextView.setText(playlist.getName());
        countTextView.setText(String.format("%d首歌曲", playlist.getSongCount()));
        
        return convertView;
    }
}