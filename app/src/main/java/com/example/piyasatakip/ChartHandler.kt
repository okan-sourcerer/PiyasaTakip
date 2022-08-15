package com.example.piyasatakip

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
            chartModel.backgroundColor = "#424242"
        }
        else{
            chartModel.backgroundColor = "#ffffff"
        }
        chartModel.xAxisLabelsEnabled = false
        chartModel.yAxisLabelsEnabled = false
        chartModel.legendEnabled = false
        chartModel.dataLabelsEnabled = false
        chartModel.markerRadius = 1f

        chartModel.yAxisTitle = ""
        chartModel.xAxisVisible = false
        chartModel.yAxisGridLineWidth = 0
        chartModel.colorsTheme = arrayOf("#787878")

        return chartModel
    }

    /**
     * Add data to the chart
     */
    fun setData(data: PiyasaBilgisi): AAChartModel{
        return chartModel.series(arrayOf(AASeriesElement().data(data.priceHistory.toTypedArray())))
    }

    /**
     * Toggle chart theme based on application theme.
     */
    fun toggleChartTheme(){
        // uygulamanın temasına göre chart arka plan rengi de değiştiriliyor.
        if (AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            chartModel.backgroundColor = "#424242"
        }
        else{
            chartModel.backgroundColor = "#ffffff"
        }
    }
}