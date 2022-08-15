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

    const val TAB_POS = "TabPosition"
    const val SEARCH_QUERY = "Query"

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

    /**
     * Saves path and name of the file to the shared preferences.
     * @param context to access preferences
     * @param name name of the file
     * @param path directory path of the file
     */
    fun saveImagePath(context: Context, name: String, path: String){
        val editor = getSharedPreferences(context).edit()
        val imgPath = "${name}path"
        editor.putString(imgPath, path)
        editor.apply()
    }

    /**
     * Gets the path of the file using file name
     * @param context needed to get the shared preferences
     * @param name name of the file
     * @return using name of the file, get the path of the image from preference.
     */
    fun getImagePath(context: Context, name: String): String? {
        val imgPath = "${name}path"
        return getSharedPreferences(context).getString(imgPath, null)
    }

    /**
     * Saves the favourite status of the info
     * @param name name of the info
     * @param isFav favourite status
     */
    fun saveFav(context: Context, name: String, isFav: Boolean){
        val editor = getSharedPreferences(context).edit()
        val fav = "${name}fav"
        editor.putBoolean(fav, isFav)
        editor.apply()
    }

    /**
     * Gets the fav status of the info from the preferences.
     * @param name name of the info
     * @return returns info fav status. Boolean, true or false
     */
    fun getFav(context: Context, name: String): Boolean {
        val fav = "${name}fav"
        return getSharedPreferences(context).getBoolean(fav, false)
    }

    /**
     * Save current tab position in case of a theme change
     */
    fun saveTabPosition(context: Context, pos: Int){
        val editor = getSharedPreferences(context).edit()
        editor.putInt(TAB_POS, pos)
        editor.apply()
    }

    /**
     * Restore the current tab position when theme is changed
     */
    fun getTabPosition(context: Context): Int{
        return getSharedPreferences(context).getInt(TAB_POS, 0)
    }
}