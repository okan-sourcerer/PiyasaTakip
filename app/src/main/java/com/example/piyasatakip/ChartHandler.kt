package com.example.piyasatakip

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement


object ChartHandler {

    var chartModel = createEmptyChartModel()


    // boş bir chart oluşturuluyor. Burada oluşturulan modele verilerin eklenmesi gerekiyor.
    private fun createEmptyChartModel(): AAChartModel {
        // info nesnesinin içinde tutulan price geçmişi listesinden yararlanarak bir chart oluşturuluyor.
        // kısıtlı alana sahip olduğumuz için labellar kapatıldı. Info nesnesinin fiyat geçmişi listesi arraya dönüştürüldü.
        val chartModel = AAChartModel().chartType(AAChartType.Line)

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

        return chartModel
    }

    fun setData(data: PiyasaBilgisi): AAChartModel{
        return chartModel.series(arrayOf(AASeriesElement().data(data.price.toTypedArray())))
    }

    fun loadBitmapFromView(v: View): Bitmap? {
        val b = Bitmap.createBitmap(
            v.width,
            v.height,
            Bitmap.Config.ARGB_8888
        )
        val c = Canvas(b)
        v.layout(v.left, v.top, v.right, v.bottom)
        v.draw(c)
        return b
    }

    fun toggleChartTheme(){
        // uygulamanın temasına göre chart arka plan rengi de değiştiriliyor.
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            chartModel.backgroundColor = "#787878"
        }
        else{
            chartModel.backgroundColor = "#ffffff"
        }
    }
}