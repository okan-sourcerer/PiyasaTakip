package com.example.piyasatakip

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object SavedPreference {
    // preference içinde kaydedileceği isim
    const val CHECKTHEME = "checkTheme"
    // light mode
    const val LIGHT_MODE = 1
    // dark mode
    const val DARK_MODE = 2

    // manager ile preferenceler alınıyor.
    fun getSharedPreferences(ctx: Context?): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(ctx)
    }

    // preference içine değer kaydedileceği zaman çağırılıyor
    fun setChecktheme(context: Context?, check: Int) {
        val editor = getSharedPreferences(context).edit()
        editor.putInt(CHECKTHEME, check)
        editor.apply()
    }

    // preference içinden değer okunacağı zaman çağırılıyor.
    fun getChecktheme(context: Context?): Int {
        return getSharedPreferences(context)
            .getInt(CHECKTHEME, LIGHT_MODE) // Eğer kayıtlı bir değer yoksa default olarak light mode döndürecek.
    }
}