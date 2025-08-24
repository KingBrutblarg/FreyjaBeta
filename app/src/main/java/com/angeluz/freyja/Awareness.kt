package com.angeluz.freyja
import com.angeluz.freyja.Prefs

import android.content.Context
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Awareness {
    fun isUnlockedBlocking(context: Context): Boolean = runBlocking {
        Prefs.run { context.Prefs.Prefs.isUnlockedFlow.first() }
    }

    fun currentSpeakModeBlocking(context: Context): SpeakMode = runBlocking {
        Prefs.run { context.Prefs.Prefs.speakModeFlow.first() }
    }
}
