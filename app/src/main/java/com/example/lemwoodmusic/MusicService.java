package com.example.lemwoodmusic;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.List;

public class MusicService extends Service {
    private MediaPlayer mediaPlayer;
    private List<Song> songList;
    private int currentPosition = -1;
    private final IBinder binder = new MusicBinder();
    
    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // 歌曲播放完成后自动播放下一首
                playNext();
            }
        });
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
    
    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }
    
    public void playSong(int position) {
        try {
            if (songList == null || songList.isEmpty() || position < 0 || position >= songList.size()) {
                Log.e("MusicService", "无效的播放位置或歌曲列表为空");
                return;
            }
            
            Song song = songList.get(position);
            if (song == null || song.getPath() == null) {
                Log.e("MusicService", "歌曲或路径为空");
                return;
            }
            
            if (!AudioFormatUtils.isSupportedAudioFormat(song.getPath())) {
                Log.e("MusicService", "不支持的音频格式: " + song.getPath());
                playNext();
                return;
            }
            
            // 释放之前的MediaPlayer资源
            releaseMediaPlayer();
            
            // 创建新的MediaPlayer实例
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnCompletionListener(mp -> playNext());
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e("MusicService", "播放错误: what=" + what + ", extra=" + extra);
                releaseMediaPlayer();
                playNext();
                return true;
            });
            
            currentPosition = position;
            
            // 设置数据源并异步准备播放
            mediaPlayer.setDataSource(song.getPath());
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(mp -> {
                try {
                    if (mediaPlayer != null) {
                        mediaPlayer.start();
                    }
                } catch (Exception e) {
                    Log.e("MusicService", "开始播放失败: " + e.getMessage(), e);
                    releaseMediaPlayer();
                    playNext();
                }
            });
        } catch (IOException e) {
            Log.e("MusicService", "播放错误: " + e.getMessage(), e);
            playNext();
        } catch (Exception e) {
            Log.e("MusicService", "未知错误: " + e.getMessage(), e);
        }
    }
    
    public void pause() {
        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        } catch (Exception e) {
            Log.e("MusicService", "暂停播放失败: " + e.getMessage(), e);
        }
    }
    
    public void resume() {
        try {
            if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        } catch (Exception e) {
            Log.e("MusicService", "恢复播放失败: " + e.getMessage(), e);
        }
    }
    
    public void playNext() {
        if (songList == null || songList.isEmpty()) {
            return;
        }
        
        int nextPosition = (currentPosition + 1) % songList.size();
        playSong(nextPosition);
    }
    
    public void playPrevious() {
        if (songList == null || songList.isEmpty()) {
            return;
        }
        
        int prevPosition = (currentPosition - 1 + songList.size()) % songList.size();
        playSong(prevPosition);
    }
    
    public boolean isPlaying() {
        try {
            return mediaPlayer != null && mediaPlayer.isPlaying();
        } catch (Exception e) {
            Log.e("MusicService", "检查播放状态失败: " + e.getMessage(), e);
            return false;
        }
    }
    
    public Song getCurrentSong() {
        if (currentPosition != -1 && songList != null && !songList.isEmpty() && currentPosition < songList.size()) {
            return songList.get(currentPosition);
        }
        return null;
    }
    
    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.reset();
                mediaPlayer.release();
            } catch (Exception e) {
                Log.e("MusicService", "释放MediaPlayer资源失败: " + e.getMessage(), e);
            } finally {
                mediaPlayer = null;
            }
        }
    }

    @Override
    public void onDestroy() {
        releaseMediaPlayer();
        super.onDestroy();
    }
}