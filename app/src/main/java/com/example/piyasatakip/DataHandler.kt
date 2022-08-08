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
        priceList.add(4.85)
        priceList.add(4.81)
        priceList.add(4.92)
        priceList.add(5.06)
        priceList.add(5.24)

        // yeni bir nesne oluşturarak listemize ekliyoruz. Şimdilik statik veriler kullanılıyor.
        hisseList.add(PiyasaBilgisi("YKBNK", "Yapı ve Kredi Bankası", priceList, "+1.15", true, "Hisse", 5.24))
        priceList = mutableListOf()
        priceList.add(303.1)
        priceList.add(309.6)
        priceList.add(307.3)
        priceList.add(312.10)
        priceList.add(314.30)
        priceList.add(319.1)

        // yeni bir nesne oluşturarak listemize ekliyoruz. Şimdilik statik veriler kullanılıyor.
        hisseList.add(PiyasaBilgisi("FROTO", "Ford Otosan", priceList, "+1.15", false, "Hisse", 319.1))
        priceList = mutableListOf()
        priceList.add(82.85)
        priceList.add(82.24)
        priceList.add(81.79)
        priceList.add(82.53)
        priceList.add(83.15)
        priceList.add(82.7)

        // yeni bir nesne oluşturarak listemize ekliyoruz. Şimdilik statik veriler kullanılıyor.
        hisseList.add(PiyasaBilgisi("TOASO", "Tofaş Türk Otomobil Fabrikası A.Ş." ,  priceList, "%1.53",false, "Hisse", 82.70))

        priceList = mutableListOf()
        priceList.add(23.64)
        priceList.add(24.01)
        priceList.add(23.58)
        priceList.add(24.0)
        priceList.add(23.55)
        priceList.add(24.02)
        hisseList.add(PiyasaBilgisi("ASELS", "Aselsan", priceList, "+0.44", false, "Hisse", 24.02))

        priceList = mutableListOf()
        priceList.add(478.50)
        priceList.add(468.50)
        priceList.add(474.40)
        priceList.add(474.90)
        priceList.add(471.20)
        priceList.add(468.60)
        priceList.add(472.50)
        priceList.add(472.80)
        hisseList.add(PiyasaBilgisi("OTKAR", "Otokar Otomotiv ve Savunma Sanayi AS", priceList, "", false, "Hisse", 472.80))
    }

    // Statik verilerle döviz verileri oluşturuluyor. Yukarıdaki bilgiler bunun için de geçerli.
    private fun initializeDoviz(){
        var dovizPriceList = mutableListOf<Double>()
        dovizPriceList.add(17.89)
        dovizPriceList.add(17.93)
        dovizPriceList.add(17.92)
        dovizPriceList.add(17.91)
        dovizPriceList.add(17.91)
        dovizPriceList.add(17.96)
        dovizList.add(PiyasaBilgisi("USD", "Dolar", dovizPriceList, "+1", true, "Döviz", 17.96))
        dovizPriceList = mutableListOf<Double>()
        dovizPriceList.add(18.84)
        dovizPriceList.add(18.74)
        dovizPriceList.add(18.68)
        dovizPriceList.add(18.75)
        dovizPriceList.add(18.63)
        dovizPriceList.add(18.63)
        dovizPriceList.add(18.77)
        dovizList.add(PiyasaBilgisi("CHF", "İsviçre Frangı", dovizPriceList, "+1", false, "Döviz", 18.77))

        dovizPriceList = mutableListOf<Double>()
        dovizPriceList.add(18.36)
        dovizPriceList.add(18.22)
        dovizPriceList.add(18.22)
        dovizPriceList.add(18.35)
        dovizPriceList.add(18.23)
        dovizPriceList.add(18.23)
        dovizPriceList.add(18.31)
        dovizPriceList.add(18.32)
        dovizList.add(PiyasaBilgisi("EUR", "Euro", dovizPriceList, "", false, "Döviz", 18.31))

        dovizPriceList = mutableListOf<Double>()
        dovizPriceList.add(21.57)
        dovizPriceList.add(21.62)
        dovizPriceList.add(21.68)
        dovizPriceList.add(21.72)
        dovizPriceList.add(21.75)
        dovizPriceList.add(21.78)
        dovizPriceList.add(21.75)
        dovizList.add(PiyasaBilgisi("GBP", "Sterlin", dovizPriceList, "", false, "Döviz", 21.75))



    }
}