package com.example.piyasatakip

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.piyasatakip.DataHandler.dovizList
import com.example.piyasatakip.DataHandler.hisseList
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView

    private lateinit var tabLayout: TabLayout

    private lateinit var adapter: ItemAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // cihazda kayıtlı olan temanın yüklenmesi sağlanıyor.
        loadThemePreference()

        // recyclerview ve tab layout için gerekli işlemleri gerçekleştiriyor.
        handleViews()

        // Toolbar yazı rengi uygulamanın genel hatlarına uygun olması için mavi renk yapılıyor.
        supportActionBar?.title = HtmlCompat.fromHtml("<font color=#ffffff>" + getString(R.string.app_name) + "</font>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        supportActionBar?.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.main_header_selector))
    }
    // Uygulamada arama tuşu ve tema değiştirme tuşu olmasını salğıyor.
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mainmenu, menu)

        val item = menu?.findItem(R.id.search_button)
        val searchView = item?.actionView as androidx.appcompat.widget.SearchView

        // search queryTextChange Listener
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                Log.d("onQueryTextChange", "query: $query")
                query?.let { adapter.filterSearch(it) }
                return true
            }
        })
        //Expand Collapse listener
        item.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionCollapse(p0: MenuItem?): Boolean {
                Log.d("MainActivity ", "onMenuItemActionCollapse: ")
                adapter.restoreItems()
                return true
            }

            override fun onMenuItemActionExpand(p0: MenuItem?): Boolean {
                Log.d("MainActivity ", "onMenuItemActionExpand: ")
                adapter.backupItems()
                return true
            }
        })
        return true
    }

    // seçilen menu tuşlarına göre bir işlem gerçekleştirilmesini sağlıyor.
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){

            // Temalar arasında geçiş yapabilmek için ikona basılabilir. Aynı zamanda tema değiştirildiğinde bunu kaydediyor.
            R.id.themeSwitcher -> {
                val currMode = AppCompatDelegate.getDefaultNightMode()

                // temalar arasında toggle
                if (currMode == AppCompatDelegate.MODE_NIGHT_YES) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    delegate.applyDayNight()
                    SavedPreference.setChecktheme(applicationContext, SavedPreference.LIGHT_MODE)
                    ChartHandler.toggleChartTheme()
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    delegate.applyDayNight()
                    SavedPreference.setChecktheme(applicationContext, SavedPreference.DARK_MODE)
                    ChartHandler.toggleChartTheme()
                }
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
        adapter = ItemAdapter(dovizList) // default olarak döviz sayfası açılacağından döviz listesi yükleniyor.
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
                else{
                    adapter.notifyListChange(dovizList)
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {} })
    }
}
