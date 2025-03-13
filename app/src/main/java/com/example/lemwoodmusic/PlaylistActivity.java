package com.example.lemwoodmusic;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class PlaylistActivity extends AppCompatActivity {
    private EditText playlistNameInput;
    private Button createPlaylistBtn;
    private ListView playlistListView;
    private List<Playlist> playlists;
    private PlaylistAdapter adapter;
    private long playlistIdCounter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        try {
            initializeViews();
            setupPlaylistAdapter();
            setupClickListeners();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "初始化歌单列表失败", Toast.LENGTH_SHORT).show();
            finish(); // 如果初始化失败，关闭Activity
        }
    }

    private void initializeViews() {
        playlistNameInput = findViewById(R.id.playlist_name_input);
        createPlaylistBtn = findViewById(R.id.create_playlist_btn);
        playlistListView = findViewById(R.id.playlist_list);
    }

    private void setupPlaylistAdapter() {
        if (playlists == null) {
            playlists = new ArrayList<>();
        }
        adapter = new PlaylistAdapter(this, playlists);
        playlistListView.setAdapter(adapter);
    }

    private void setupClickListeners() {
        playlistListView.setOnItemClickListener((parent, view, position, id) -> {
            try {
                if (playlists != null && position >= 0 && position < playlists.size()) {
                    Playlist selectedPlaylist = playlists.get(position);
                    if (selectedPlaylist != null) {
                        handlePlaylistSelection(selectedPlaylist);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "打开歌单失败", Toast.LENGTH_SHORT).show();
            }
        });

        createPlaylistBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String name = playlistNameInput.getText().toString().trim();
                    if (!name.isEmpty()) {
                        Playlist playlist = new Playlist(++playlistIdCounter, name);
                        playlists.add(playlist);
                        adapter.notifyDataSetChanged();
                        playlistNameInput.setText("");
                        Toast.makeText(PlaylistActivity.this, "歌单创建成功", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PlaylistActivity.this, "请输入歌单名称", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(PlaylistActivity.this, "创建歌单失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void handlePlaylistSelection(Playlist playlist) {
        try {
            if (playlist != null) {
                Toast.makeText(this, "已选择歌单: " + playlist.getName(), Toast.LENGTH_SHORT).show();
                // TODO: 实现歌单选择后的具体逻辑
                // 这里可以添加跳转到歌单详情页面的代码
                // Intent intent = new Intent(this, PlaylistDetailActivity.class);
                // intent.putExtra("playlist_id", playlist.getId());
                // startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "处理歌单选择失败", Toast.LENGTH_SHORT).show();
        }
    }
}