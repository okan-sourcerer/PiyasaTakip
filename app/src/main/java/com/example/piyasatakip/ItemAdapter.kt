package com.example.piyasatakip

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView

// recyclerview içindeki nesnelerin durumlarına buradan erişiliyor. Adapter sınıfı parametre olarak recyclerview içinde kullanılacak listeyi alıyor.
class ItemAdapter(var items: MutableList<PiyasaBilgisi>) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    private var backupItems = mutableListOf<PiyasaBilgisi>()
    private var lastQuery: String = ""

    // layout ilk oluşturulduğunda buraya gelecek. Görüldüğü gibi recyclerview_item.xml dosyasında belirtilen layout oluşturularak recyclerviewda gösterilmek üzere ekleniyor.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false))
    }

    // recyclerview üzerinde gösterilecek nesneler, pozisyon parametresiyle burada değer ataması yapılarak ekleniyor.
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    // listedeki toplam nesne sayısı
    override fun getItemCount() = items.size

    /**
     * Used to change list of the adapter and notify the adapter for the current changes.
     * Updates backup list.
     * Called from tab changes.
     * @param list new list for the adapter.
     * @param applyQuery if searchview is expanded, on tab change we need to change the list and apply the
     * query to that list as well
     */
    fun notifyListChange(list: MutableList<PiyasaBilgisi>, applyQuery: Boolean){
        items = list
        updateBackup(list)
        if (applyQuery)
            filterSearch(lastQuery)
        notifyDataSetChanged()
    }

    /**
     * To filter the list for the query entered from the searchView in @MainActivity.kt
     * Modifies the list for every input.
     */
    fun filterSearch(query: String){
        Log.d("filterSearch", "query: $query")

        items.removeAll { true }
        Log.d("filterSearch", "List items  ${items.size} ${backupItems.size}")
        backupItems.forEach{
            items.add(it)
        }
        Log.d("filterSearch", "List items  ${items.size} ${backupItems.size}")

        // filter by shortname of full name
        if (query.isNotEmpty())
            items = items.filter {
                it.fullName.contains(query, true) || it.shortName.contains(query, true)
            } as MutableList<PiyasaBilgisi>
        Log.d("filterSearch", "List elements ${items.size}")
        Log.d("filterSearch", "List backup elements ${backupItems.size}")

        notifyDataSetChanged()
        lastQuery = query
    }

    /**
     * Called when search button is pressed in the main activity to expand. Expanding the search button will
     * trigger this method to backup the list in case of search query being entered. Filtering will remove items from the
     * list. This list will be used to restore the removed items.
     */
    fun backupItems(){
        Log.d("backupItems", "backing up items.   ${items.size} ${backupItems.size}")
        backupItems.removeAll { true }
        Log.d("backupItems", "backing up items 2.0   ${items.size} ${backupItems.size}")
        items.forEach { piyasaBilgisi ->
            backupItems.add(piyasaBilgisi)
        }
        Log.d("backupItems", "backing up items 3.0   ${items.size} ${backupItems.size}")
    }

    /**
     * Called when search button is collapsed in main activity. When search bar is closed, we restore
     * the list from the backup and refresh the adapter to show all elements.
     */
    fun restoreItems(){
        Log.d("restoreItems", "Restoring items. ${items.size} ${backupItems.size}")
        items.removeAll{true}
        backupItems.forEach{
            items.add(it)
        }
        notifyDataSetChanged()
    }

    /**
     * Update backup when tab is switched.
     */
    private fun updateBackup(items: MutableList<PiyasaBilgisi>){
        backupItems.removeAll{true}
        items.forEach{
            backupItems.add(it)
        }
    }


    // adapter sınıfı her satırdaki değerleri ayrı viewholderlara ayırdığından her bir viewholder nesnesinin değerlerini bu sınıf içinde atıyoruz.
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        // recyclerview_item.xml dosyasında tanımlanan viewlara burada erişiliyor.
        private var shortName = itemView.findViewById<TextView>(R.id.text_item_short)
        private var fullName = itemView.findViewById<TextView>(R.id.text_item_full)
        private var price = itemView.findViewById<TextView>(R.id.text_item_price)
        private var increase = itemView.findViewById<TextView>(R.id.text_item_difference)
        private var favIcon = itemView.findViewById<ImageView>(R.id.image_item_star)
        private var chart = itemView.findViewById<AAChartView>(R.id.item_chart)

        // burada erişilen viewlara değişken ataması yapılıyor. Ayrıca yıldız ikonlarına tıklama durumu da kontrol ediliyor.
        fun bind(info: PiyasaBilgisi){
            // döviz/hisse senedi isimleriyle ilgili değişkenler atanıyor
            shortName.text = info.shortName
            fullName.text = info.fullName

            refreshValues(info)
            handleFavIcon(info.isFav)

            // favori ikonuna listener atanıyor. Bu sayede tıkladğıımızda ikon değişiyor
            favIcon.setOnClickListener {
                info.isFav = !info.isFav
                handleFavIcon(info.isFav)
                SavedPreference.saveFav(itemView.context, info.shortName, info.isFav)
                DataHandler.sortLists()
            }

            drawChart(info)
        }

        /**
         * Draw chart using chartmodel created in ChartHandler class and info. Change graph color based on
         * first and the last values in the graph.
         */
        private fun drawChart(info: PiyasaBilgisi){
            // oluşturulan model chartviewa ekleniyor
            val chartModel = ChartHandler.setData(info)
            if (info.priceHistory.size > 1){
                val overallDiff = info.priceHistory[info.priceHistory.size - 1] - info.priceHistory[0]
                if (overallDiff < 0){
                    chartModel.colorsTheme = arrayOf("#ff0000")
                } else if (overallDiff > 0){
                    chartModel.colorsTheme = arrayOf("#33EA36")
                }
            }

            //chart.setImageBitmap(ChartHandler.getBitmapOfChart(info, itemView.context))
            chart.aa_drawChartWithChartModel(chartModel)
        }

        /**
         * Refreshes the view based on the updated values.
         */
        fun refreshValues(info: PiyasaBilgisi){
            price.text = info.current.toString()
            // if we have enough history data, we compare closing values etc to add colors to the difference text
            if (info.priceHistory.size > 2){
                val lastClose = info.priceHistory[info.priceHistory.size - 2]
                val current = info.current
                val diff = current - lastClose
                increase.text = String.format("%.2f", diff)
                if (diff < 0){
                    increase.setTextColor(ContextCompat.getColor(itemView.context, R.color.brigthRed))
                }
                else if (diff > 0){
                    increase.setTextColor(ContextCompat.getColor(itemView.context, R.color.green))
                }
            }
            drawChart(info)
        }

        // döviz/ hisse senedinin favoriye eklenmesi durumunda ikondaki değişiklik sağlanıyor.
        private fun handleFavIcon(isFav: Boolean){
            val ids = AppWidgetManager.getInstance(itemView.context).getAppWidgetIds(ComponentName(itemView.context, Widget::class.java))

            if(ids.isNotEmpty()){
                AppWidgetManager.getInstance(itemView.context).notifyAppWidgetViewDataChanged(ids, R.id.list_view)
            }

            if (isFav){
                favIcon.setImageResource(R.drawable.ic_baseline_star_rate_24)
            }
            else{
                favIcon.setImageResource(R.drawable.ic_baseline_star_outline_24)
            }
        }
    }
}
