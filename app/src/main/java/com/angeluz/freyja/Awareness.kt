package com.angeluz.freyja
import com.angeluz.freyja.Prefs

import android.content.Context
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Awareness {
    fun isUnlockedBlocking(context: Context): Boolean = runBlocking {
        com.angeluz.freyja.Prefs.run { com.angeluz.freyja.Prefs.com.angeluz.freyja.Prefs.isUnlockedFlow.first() }
    }

    fun currentSpeakModeBlocking(context: Context): SpeakMode = runBlocking {
        com.angeluz.freyja.Prefs.run { com.angeluz.freyja.Prefs.com.angeluz.freyja.Prefs.speakModeFlow.first() }
    }
}
