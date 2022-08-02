package com.example.piyasatakip

object DataHandler {

    // hisse bilgilerinin tutulduğu list
    val hisseList: MutableList<PiyasaBilgisi> = mutableListOf()
    // döviz birimlerinin bilgilerinin tutulduğu liste
    val dovizList: MutableList<PiyasaBilgisi> = mutableListOf()

    // statik veriler bu objeye erişildiğinde oluşturuluyor.
    init
    {
        initializeHisse()
        initializeDoviz()
    }

    // Statik hisse verileri oluşturarak listeye ekleyecek. API üzerinden veri çekeceksek statik verilerin EKLENDİĞİ yerler silinmeli.
    private fun initializeHisse(){
        // Fiyat geçmişinin tutulduğu liste.
        var priceList = mutableListOf<Double>()
        priceList.add(50.25)
        priceList.add(48.7)
        priceList.add(51.98)
        priceList.add(54.12)
        priceList.add(53.38)
        priceList.add(45.47)

        // yeni bir nesne oluşturarak listemize ekliyoruz. Şimdilik statik veriler kullanılıyor.
        hisseList.add(PiyasaBilgisi("YK", "Yapı Kredi", priceList, "+1.15", true, "Hisse"))
    }

    // Statik verilerle döviz verileri oluşturuluyor. Yukarıdaki bilgiler bunun için de geçerli.
    private fun initializeDoviz(){
        var dovizPriceList = mutableListOf<Double>()
        dovizPriceList.add(8.0)
        dovizPriceList.add(9.0)
        dovizPriceList.add(10.0)
        dovizPriceList.add(9.0)
        dovizPriceList.add(11.20)
        dovizPriceList.add(10.15)
        dovizList.add(PiyasaBilgisi("USD", "Dolar", dovizPriceList, "+1", true, "Döviz"))
    }
}