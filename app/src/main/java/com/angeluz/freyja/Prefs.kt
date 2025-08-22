package com.angeluz.freyja

import android.content.Context

object Prefs {
    private const val NAME = "freyja_prefs"
    private const val K_UNLOCKED = "guardia_1226_unlocked"

    fun isUnlocked(ctx: Context): Boolean =
        ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .getBoolean(K_UNLOCKED, false)

    fun setUnlocked(ctx: Context, value: Boolean) {
        ctx.getSharedPreferences(NAME, Context.MODE_PRIVATE)
            .edit().putBoolean(K_UNLOCKED, value).apply()
    }
}