package com.angeluz.freyja

import android.content.Context
import android.content.SharedPreferences

object Prefs {
    private const val NAME = "freyja_prefs"
    private const val KEY_UNLOCKED = "unlocked"
    private const val KEY_SPEAK_MODE = "speak_mode"

    private fun prefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    fun isUnlocked(ctx: Context): Boolean =
        prefs(ctx).getBoolean(KEY_UNLOCKED, false)

    fun setUnlocked(ctx: Context, value: Boolean) {
        prefs(ctx).edit().putBoolean(KEY_UNLOCKED, value).apply()
    }

    fun getSpeakMode(ctx: Context): SpeakMode {
        val raw = prefs(ctx).getString(KEY_SPEAK_MODE, SpeakMode.NOTIFY.name)
        return runCatching { SpeakMode.valueOf(raw!!) }.getOrElse { SpeakMode.NOTIFY }
    }

    fun setSpeakMode(ctx: Context, mode: SpeakMode) {
        prefs(ctx).edit().putString(KEY_SPEAK_MODE, mode.name).apply()
    }
}
