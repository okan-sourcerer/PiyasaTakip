package com.example.piyasatakip

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView

// recyclerview içindeki nesnelerin durumlarına buradan erişiliyor. Adapter sınıfı parametre olarak recyclerview içinde kullanılacak listeyi alıyor.
class ItemAdapter(var items: MutableList<PiyasaBilgisi>) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    private var backupItems = mutableListOf<PiyasaBilgisi>()

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

    // tablar arasında geçiş yaptığımızda listeyi de değiştiriyor.
    fun notifyListChange(list: MutableList<PiyasaBilgisi>){
        items = list
        notifyDataSetChanged()
        updateBackup(list)
    }

    fun filterSearch(query: String){
        Log.d("filterSearch", "query: $query")

        items.removeAll { true }
        Log.d("filterSearch", "List items  ${items.size} ${backupItems.size}")
        backupItems.forEach{
            items.add(it)
        }
        Log.d("filterSearch", "List items  ${items.size} ${backupItems.size}")

        if (query.isNotEmpty())
            items = items.filter {
                it.fullName.contains(query, true) || it.shortName.contains(query, true)
            } as MutableList<PiyasaBilgisi>
        Log.d("filterSearch", "List elements ${items.size}")
        Log.d("filterSearch", "List backup elements ${backupItems.size}")

        notifyDataSetChanged()
    }

    fun backupItems(){
        Log.d("backupItems", "backing up items.   ${items.size} ${backupItems.size}")
        backupItems.removeAll { true }
        Log.d("backupItems", "backing up items 2.0   ${items.size} ${backupItems.size}")
        items.forEach { piyasaBilgisi ->
            backupItems.add(piyasaBilgisi)
        }
        Log.d("backupItems", "backing up items 3.0   ${items.size} ${backupItems.size}")
    }

    fun restoreItems(){
        Log.d("restoreItems", "Restoring items. ${items.size} ${backupItems.size}")
        items.removeAll{true}
        backupItems.forEach{
            items.add(it)
        }
        notifyDataSetChanged()
    }

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
            price.text = info.price[info.price.size - 1].toString()
            increase.text = info.difference

            handleFavIcon(info.isFav)

            // favori ikonuna listener atanıyor. Bu sayede tıkladğıımızda ikon değişiyor
            favIcon.setOnClickListener {
                info.isFav = !info.isFav
                handleFavIcon(info.isFav)
            }

            // oluşturulan model chartviewa ekleniyor
            val chartModel = ChartHandler.setData(info)
            //chart.setImageBitmap(ChartHandler.getBitmapOfChart(info, itemView.context))
            chart.aa_drawChartWithChartModel(chartModel)
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
