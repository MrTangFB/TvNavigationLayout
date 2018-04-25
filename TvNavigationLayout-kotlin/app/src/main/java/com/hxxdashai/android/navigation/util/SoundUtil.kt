package com.hxxdashai.android.navigation.util

import android.content.Context
import android.media.AudioManager
import android.view.View

/**
 * Created by Mr.T on 2018/3/30.
 */
object SoundUtil {

    fun playClickSound(view: View) {
        if (view.isSoundEffectsEnabled) {
            val manager = view.context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            manager.playSoundEffect(AudioManager.FX_KEY_CLICK)
        }
    }
}