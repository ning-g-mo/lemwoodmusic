package com.example.lemwoodmusic;

import android.webkit.MimeTypeMap;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AudioFormatUtils {
    private static final Set<String> SUPPORTED_AUDIO_FORMATS = new HashSet<>(Arrays.asList(
            "mp3", "wav", "ogg", "m4a", "aac"
    ));

    private static final Set<String> EXCLUDED_FORMATS = new HashSet<>(Arrays.asList(
            "kgg"
    ));

    public static boolean isSupportedAudioFormat(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return false;
        }

        String extension = MimeTypeMap.getFileExtensionFromUrl(filePath.toLowerCase());
        if (extension == null || extension.isEmpty()) {
            int lastDot = filePath.lastIndexOf('.');
            if (lastDot != -1 && lastDot < filePath.length() - 1) {
                extension = filePath.substring(lastDot + 1).toLowerCase();
            } else {
                return false; // 没有有效的扩展名
            }
        }

        return SUPPORTED_AUDIO_FORMATS.contains(extension) && !EXCLUDED_FORMATS.contains(extension);
    }

    public static Set<String> getSupportedAudioFormats() {
        return new HashSet<>(SUPPORTED_AUDIO_FORMATS);
    }
}