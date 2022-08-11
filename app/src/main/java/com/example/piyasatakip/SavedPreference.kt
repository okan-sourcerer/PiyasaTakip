package com.example.piyasatakip

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

object SavedPreference {
    // preference içinde kaydedileceği isim
    private const val CHECKTHEME = "checkTheme"
    // light mode
    const val LIGHT_MODE = 1
    // dark mode
    const val DARK_MODE = 2

    // manager ile preferenceler alınıyor.
    private fun getSharedPreferences(ctx: Context): SharedPreferences {
        return PreferenceManager.getDefaultSharedPreferences(ctx)
    }

    // preference içine değer kaydedileceği zaman çağırılıyor
    fun setChecktheme(context: Context, check: Int) {
        val editor = getSharedPreferences(context).edit()
        editor.putInt(CHECKTHEME, check)
        editor.apply()
    }

    // preference içinden değer okunacağı zaman çağırılıyor.
    fun getChecktheme(context: Context): Int {
        return getSharedPreferences(context)
            .getInt(CHECKTHEME, LIGHT_MODE) // Eğer kayıtlı bir değer yoksa default olarak light mode döndürecek.
    }

    fun saveImagePath(context: Context, name: String, path: String){
        val editor = getSharedPreferences(context).edit()
        val imgPath = "${name}path"
        editor.putString(imgPath, path)
        editor.apply()
    }

    fun getImagePath(context: Context, name: String): String? {
        val imgPath = "${name}path"
        return getSharedPreferences(context).getString(imgPath, null)
    }

    fun saveFav(context: Context, name: String, isFav: Boolean){
        val editor = getSharedPreferences(context).edit()
        val fav = "${name}fav"
        editor.putBoolean(fav, isFav)
        editor.apply()
    }

    fun getFav(context: Context, name: String): Boolean {
        val fav = "${name}fav"
        return getSharedPreferences(context).getBoolean(fav, false)
    }
}