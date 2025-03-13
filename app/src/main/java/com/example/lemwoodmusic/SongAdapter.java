package com.example.lemwoodmusic;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class SongAdapter extends ArrayAdapter<Song> {
    
    public SongAdapter(Context context, List<Song> songs) {
        super(context, 0, songs);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 获取指定位置的歌曲
        Song song = getItem(position);
        
        // 检查是否有重用的视图，否则膨胀一个新视图
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.song_item, parent, false);
        }
        
        // 查找并填充视图
        TextView titleTextView = convertView.findViewById(R.id.song_title);
        TextView artistTextView = convertView.findViewById(R.id.song_artist);
        
        // 设置歌曲数据
        titleTextView.setText(song.getTitle());
        artistTextView.setText(song.getArtist());
        
        return convertView;
    }
} 