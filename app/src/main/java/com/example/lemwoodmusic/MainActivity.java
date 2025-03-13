package com.example.lemwoodmusic;

import android.Manifest;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    
    private ListView songListView;
    private Button btnPlay, btnNext, btnPrev;
    private TextView currentSongTitle, currentSongArtist;
    
    private Button btnRefresh;
    private Button btnPlaylist;
    
    private List<Song> songList;
    private SongAdapter adapter;
    
    private MusicService musicService;
    private boolean isBound = false;
    
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
            musicService = binder.getService();
            musicService.setSongList(songList);
            isBound = true;
            updateUI();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // 初始化视图
        songListView = findViewById(R.id.song_list);
        btnPlay = findViewById(R.id.btn_play);
        btnNext = findViewById(R.id.btn_next);
        btnPrev = findViewById(R.id.btn_prev);
        btnRefresh = findViewById(R.id.btn_refresh);
        btnPlaylist = findViewById(R.id.btn_playlist);
        currentSongTitle = findViewById(R.id.current_song_title);
        currentSongArtist = findViewById(R.id.current_song_artist);
        
        // 检查权限
        checkPermission();
        
        // 设置刷新按钮监听器
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadSongs();
                Toast.makeText(MainActivity.this, "音乐库已刷新", Toast.LENGTH_SHORT).show();
            }
        });
        
        // 设置歌单按钮监听器
        btnPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlaylistActivity.class);
                startActivity(intent);
            }
        });
        // 设置监听器
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicService != null) {
                    if (musicService.isPlaying()) {
                        musicService.pause();
                        btnPlay.setText("播放");
                    } else {
                        if (musicService.getCurrentSong() != null) {
                            musicService.resume();
                            btnPlay.setText("暂停");
                        } else if (!songList.isEmpty()) {
                            musicService.playSong(0);
                            btnPlay.setText("暂停");
                            updateUI();
                        }
                    }
                }
            }
        });
        
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicService != null) {
                    musicService.playNext();
                    btnPlay.setText("暂停");
                    updateUI();
                }
            }
        });
        
        btnPrev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicService != null) {
                    musicService.playPrevious();
                    btnPlay.setText("暂停");
                    updateUI();
                }
            }
        });
        
        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (musicService != null) {
                    musicService.playSong(position);
                    btnPlay.setText("暂停");
                    updateUI();
                }
            }
        });
    }
    
    private void updateUI() {
        if (musicService != null && musicService.getCurrentSong() != null) {
            Song currentSong = musicService.getCurrentSong();
            currentSongTitle.setText(currentSong.getTitle());
            currentSongArtist.setText(currentSong.getArtist());
        }
    }
    
    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        } else {
            loadSongs();
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadSongs();
            } else {
                Toast.makeText(this, "需要存储权限才能播放本地音乐", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void loadSongs() {
        songList = new ArrayList<>();
        
        ContentResolver contentResolver = getContentResolver();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0";
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        
        Cursor cursor = contentResolver.query(uri, null, selection, null, sortOrder);
        
        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                int idColumn = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
                int titleColumn = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
                int artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
                int pathColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                int durationColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);

                if (idColumn >= 0 && titleColumn >= 0 && artistColumn >= 0 && 
                    pathColumn >= 0 && durationColumn >= 0) {
                    long id = cursor.getLong(idColumn);
                    String title = cursor.getString(titleColumn);
                    String artist = cursor.getString(artistColumn);
                    String path = cursor.getString(pathColumn);
                    long duration = cursor.getLong(durationColumn);
                    
                    Song song = new Song(id, title, artist, path, duration);
                    songList.add(song);
                }
            }
        }
        
        if (cursor != null) {
            cursor.close();
        }
        
        adapter = new SongAdapter(this, songList);
        songListView.setAdapter(adapter);
        
        // 启动并绑定服务
        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    
    @Override
    protected void onDestroy() {
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
        super.onDestroy();
    }
}