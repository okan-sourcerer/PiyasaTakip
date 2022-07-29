package com.example.piyasatakip

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.piyasatakip.DataHandler.dovizList
import com.example.piyasatakip.DataHandler.hisseList
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // cihazda kayıtlı olan temanın yüklenmesi sağlanıyor.
        loadThemePreference()

        // recyclerview ve tab layout için gerekli işlemleri gerçekleştiriyor.
        handleViews()

        // Toolbar yazı rengi uygulamanın genel hatlarına uygun olması için mavi renk yapılıyor.
        supportActionBar?.title = HtmlCompat.fromHtml("<font color=#2f68b6>" + getString(R.string.app_name) + "</font>", HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
    // Uygulamada arama tuşu ve tema değiştirme tuşu olmasını salğıyor.
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mainmenu, menu)
        return true
    }

    // seçilen menu tuşlarına göre bir işlem gerçekleştirilmesini sağlıyor.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            // Arama tuşunun bir işlevi yok ancak ileride eklenecek.
            R.id.search_button -> TODO("Search view eklenecek. Her karakter yazıldığında tekrar query yapılacak.")

            // Temalar arasında geçiş yapabilmek için ikona basılabilir. Aynı zamanda tema değiştirildiğinde bunu kaydediyor.
            R.id.themeSwitcher ->
                item.setOnMenuItemClickListener {
                    val currMode = AppCompatDelegate.getDefaultNightMode()

                    // temalar arasında toggle
                    if (currMode == AppCompatDelegate.MODE_NIGHT_YES){
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        delegate.applyDayNight()
                        SavedPreference.setChecktheme(applicationContext, SavedPreference.LIGHT_MODE)
                    }
                    else{
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        delegate.applyDayNight()
                        SavedPreference.setChecktheme(applicationContext, SavedPreference.DARK_MODE)
                    }
                    true
                }
        }
        return true
    }

    /**
     * Cihaza tema seçme aracılığıyla kaydedilen temayı yüklememizi sağlıyor.
     */
    private fun loadThemePreference(){
        if (SavedPreference.getChecktheme(applicationContext) == SavedPreference.LIGHT_MODE){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            delegate.applyDayNight()
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            delegate.applyDayNight()
        }
    }

    /**
     * Uygulamadaki viewlara erişiliyor ve o viewlara özgü işlevler gerçekleştiriliyor.
     */
    private fun handleViews(){
        //------------------------RecyclerView------------------------------------------
        // activity_main.xml dpsyası içinde kullanılan recyclerview erişiliyor
        recyclerView = findViewById(R.id.recycler_main)

        // recyclerview adaptörü oluşturularak atama işlemi yapılıyor.
        val adapter = ItemAdapter(dovizList) // default olarak döviz sayfası açılacağından döviz listesi yükleniyor.
        recyclerView.adapter = adapter
        // lineer layout manager kullanılıyor.
        recyclerView.layoutManager = LinearLayoutManager(this)

        //-----------------------Tab Layout---------------------------------------------
        // tab layout eklendi. Hisse ve döviz adında 2 tab eklendi. Default olarak döviz açılacak.
        tabLayout = findViewById(R.id.tab_layout)
        val hisseTab = tabLayout.newTab()
        hisseTab.text = "Hisse"

        val dovizTab = tabLayout.newTab()
        dovizTab.text = "Doviz"

        tabLayout.selectTab(tabLayout.getTabAt(0))
        // Tab değiştirildiğinde her sayfaya özel veriler yükleniyor.
        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tabLayout.selectedTabPosition == 1){
                    adapter.notifyListChange(hisseList)
                }
                else
                    adapter.notifyListChange(dovizList)
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {} })
    }
}
