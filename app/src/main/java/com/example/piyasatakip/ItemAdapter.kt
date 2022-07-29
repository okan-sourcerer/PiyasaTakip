package com.example.piyasatakip

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.recyclerview.widget.RecyclerView
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement

// recyclerview içindeki nesnelerin durumlarına buradan erişiliyor. Adapter sınıfı parametre olarak recyclerview içinde kullanılacak listeyi alıyor.
class ItemAdapter(private var items: MutableList<PiyasaBilgisi>) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

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
            chart.aa_drawChartWithChartModel(createChartModel(info))
        }

        private fun createChartModel(info: PiyasaBilgisi): AAChartModel{
            // info nesnesinin içinde tutulan price geçmişi listesinden yararlanarak bir chart oluşturuluyor.
            // kısıtlı alana sahip olduğumuz için labellar kapatıldı. Info nesnesinin fiyat geçmişi listesi arraya dönüştürüldü.
            val chartModel = AAChartModel().chartType(AAChartType.Line)
                .series(arrayOf(AASeriesElement().data(info.price.toTypedArray())))

            // uygulamanın temasına göre chart arka plan rengi de değiştiriliyor.
            if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
                chartModel.backgroundColor = "#787878"
            }
            else{
                chartModel.backgroundColor = "#ffffff"
            }
            chartModel.xAxisLabelsEnabled = false
            chartModel.yAxisLabelsEnabled = false
            chartModel.legendEnabled = false
            chartModel.dataLabelsEnabled = false
            chartModel.markerRadius = 1f
            chartModel.tooltipEnabled = false
            
            return chartModel
        }

        // döviz/ hisse senedinin favoriye eklenmesi durumunda ikondaki değişiklik sağlanıyor.
        private fun handleFavIcon(isFav: Boolean){
            if (isFav){
                favIcon.setImageResource(R.drawable.ic_baseline_star_rate_24)
            }
            else{
                favIcon.setImageResource(R.drawable.ic_baseline_star_outline_24)
            }
        }
    }
}
