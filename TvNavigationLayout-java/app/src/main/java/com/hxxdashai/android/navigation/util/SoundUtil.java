package com.hxxdashai.android.navigation.util;

import android.content.Context;
import android.media.AudioManager;
import android.view.View;

/**
 * Created by Mr.T on 2018/4/25.
 */

public class SoundUtil {

    public static void playClickSound(View view) {
        if (view.isSoundEffectsEnabled()) {
            AudioManager manager = (AudioManager) view.getContext().getSystemService(Context.AUDIO_SERVICE);
            manager.playSoundEffect(AudioManager.FX_KEY_CLICK);
        }
    }
}