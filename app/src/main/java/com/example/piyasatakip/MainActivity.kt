package com.example.piyasatakip

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private var itemList = mutableListOf<PiyasaBilgisi>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Fiyat geçmişinin tutulduğu liste.
        var priceList = mutableListOf<Double>()
        priceList.add(50.25)
        priceList.add(48.7)
        priceList.add(51.98)
        priceList.add(54.12)
        priceList.add(53.38)
        priceList.add(45.47)

        // yeni bir nesne oluşturarak listemize ekliyoruz. Şimdilik statik veriler kullanılıyor.
        itemList.add(PiyasaBilgisi("YK", "Yapı Kredi Teknoloji", priceList, "+1.15", true))

        // activity_main.xml dpsyası içinde kullanılan recyclerview erişiliyor
        recyclerView = findViewById(R.id.recycler_main)

        // recyclerview adaptörü oluşturularak atama işlemi yapılıyor.
        recyclerView.adapter = ItemAdapter(itemList)
        // lineer layout manager kullanılıyor.
        recyclerView.layoutManager = LinearLayoutManager(this)
    }
}