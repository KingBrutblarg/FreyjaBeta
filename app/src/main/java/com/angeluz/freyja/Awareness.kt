package com.angeluz.freyja

import android.content.Context
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Awareness {
    fun isUnlockedBlocking(context: Context): Boolean = runBlocking {
        Prefs.run { context.isUnlockedFlow.first() }
    }

    fun currentSpeakModeBlocking(context: Context): SpeakMode = runBlocking {
        Prefs.run { context.speakModeFlow.first() }
    }
}
