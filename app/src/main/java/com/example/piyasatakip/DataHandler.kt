package com.example.piyasatakip

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.*


object DataHandler {

    // hisse bilgilerinin tutulduğu list
    val hisseList: MutableList<PiyasaBilgisi> = mutableListOf()
    // döviz birimlerinin bilgilerinin tutulduğu liste
    val dovizList: MutableList<PiyasaBilgisi> = mutableListOf()

    // statik veriler bu objeye erişildiğinde oluşturuluyor.
    init
    {
        Log.d("DataHandler", "init: ")

        loadData()

//        initializeHisse()
//        initializeDoviz()
    }

    fun loadData(){
        Log.d("DataHandler", "loadData: ")

        FirebaseFirestore.getInstance().collection("1")
            .orderBy("shortName")
            .get()
            .addOnSuccessListener { result ->
                hisseList.removeAll { true }
                dovizList.removeAll { true }
                for (document in result) {
                    val info = document.toObject(PiyasaBilgisi::class.java)
                    if (info.type == "Hisse"){
                        hisseList.add(info)
                    }else{
                        dovizList.add(info)
                    }

                    Log.d("DataHandler", "addOnSuccessListener $info ${hisseList.size} ${dovizList.size}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("DataHandler", "Error getting documents: ", exception)
            }

    }

    fun loadData(adapter: ItemAdapter, context: Context){
        Log.d("DataHandler", "loadData: ")

        FirebaseFirestore.getInstance().collection("1")
            .orderBy("shortName")
            .get()
            .addOnSuccessListener { result ->
                hisseList.removeAll { true }
                dovizList.removeAll { true }
                for (document in result) {
                    val info = document.toObject(PiyasaBilgisi::class.java)
                    if (info.type == "Hisse"){
                        hisseList.add(info)
                    }else{
                        dovizList.add(info)
                    }
                    FirebaseStorage.getInstance().reference.child(info.imagePath).getBytes(1024*1024)
                        .addOnSuccessListener {
                            val bm = BitmapFactory.decodeByteArray(it, 0, it.size)
                            val path = saveToInternalStorage(info.shortName, bm, context)
                            if (path != null) {
                                SavedPreference.saveImagePath(context, info.shortName, path)
                            }
                            Log.d("WidgetService", "getViewAt: image size ${it.size}")
                        }
                    info.isFav = SavedPreference.getFav(context, info.shortName)
                    Log.d("DataHandler", "addOnSuccessListener $info ${hisseList.size} ${dovizList.size}")
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("DataHandler", "Error getting documents: ", exception)
                Toast.makeText(context, "Internet bağlantısı bulunamadı", Toast.LENGTH_SHORT)
            }
    }

    private fun saveToInternalStorage(imageName: String, bitmapImage: Bitmap, context: Context): String? {
        val cw = ContextWrapper(context)
        // path to /data/data/yourapp/app_data/imageDir
        val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
        // Create imageDir
        val mypath = File(directory, imageName)
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(mypath)
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                fos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return directory.absolutePath
    }

    fun loadImageFromStorage(path: String, name: String): Bitmap? {
        var b: Bitmap? = null
        try {
            Log.d("DataHandler", "loadImageFromStorage: image path $path $name")
            val f = File(path, name)
            Log.d("DataHandler", "loadImageFromStorage: ${f.absolutePath}")
            b = BitmapFactory.decodeStream(FileInputStream(f))
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        return b
    }

    // Statik hisse verileri oluşturarak listeye ekleyecek. API üzerinden veri çekeceksek statik verilerin EKLENDİĞİ yerler silinmeli.
//    private fun initializeHisse(){
//        // Fiyat geçmişinin tutulduğu liste.
//        var priceList = mutableListOf<Double>()
//        priceList.add(4.85)
//        priceList.add(4.81)
//        priceList.add(4.92)
//        priceList.add(5.06)
//        priceList.add(5.24)
//
//        // yeni bir nesne oluşturarak listemize ekliyoruz. Şimdilik statik veriler kullanılıyor.
//        hisseList.add(PiyasaBilgisi("YKBNK", "Yapı ve Kredi Bankası", priceList, true, "Hisse", 5.24))
//        priceList = mutableListOf()
//        priceList.add(303.1)
//        priceList.add(309.6)
//        priceList.add(307.3)
//        priceList.add(312.10)
//        priceList.add(314.30)
//        priceList.add(319.1)
//
//        // yeni bir nesne oluşturarak listemize ekliyoruz. Şimdilik statik veriler kullanılıyor.
//        hisseList.add(PiyasaBilgisi("FROTO", "Ford Otosan", priceList, false, "Hisse", 319.1))
//        priceList = mutableListOf()
//        priceList.add(82.85)
//        priceList.add(82.24)
//        priceList.add(81.79)
//        priceList.add(82.53)
//        priceList.add(83.15)
//        priceList.add(82.7)
//
//        // yeni bir nesne oluşturarak listemize ekliyoruz. Şimdilik statik veriler kullanılıyor.
//        hisseList.add(PiyasaBilgisi("TOASO", "Tofaş Türk Otomobil Fabrikası A.Ş." ,  priceList, false, "Hisse", 82.70))
//
//        priceList = mutableListOf()
//        priceList.add(23.64)
//        priceList.add(24.01)
//        priceList.add(23.58)
//        priceList.add(24.0)
//        priceList.add(23.55)
//        priceList.add(24.02)
//        hisseList.add(PiyasaBilgisi("ASELS", "Aselsan", priceList, false, "Hisse", 24.02))
//
//        priceList = mutableListOf()
//        priceList.add(478.50)
//        priceList.add(468.50)
//        priceList.add(474.40)
//        priceList.add(474.90)
//        priceList.add(471.20)
//        priceList.add(468.60)
//        priceList.add(472.50)
//        priceList.add(472.80)
//        hisseList.add(PiyasaBilgisi("OTKAR", "Otokar Otomotiv ve Savunma Sanayi AS", priceList, false, "Hisse", 472.80))
//
//        hisseList.sortBy { it.shortName }
//    }

    // Statik verilerle döviz verileri oluşturuluyor. Yukarıdaki bilgiler bunun için de geçerli.
//    private fun initializeDoviz(){
//        var dovizPriceList = mutableListOf<Double>()
//        dovizPriceList.add(17.89)
//        dovizPriceList.add(17.93)
//        dovizPriceList.add(17.92)
//        dovizPriceList.add(17.91)
//        dovizPriceList.add(17.91)
//        dovizPriceList.add(17.96)
//        dovizList.add(PiyasaBilgisi("USD", "Dolar", dovizPriceList, true, "Döviz", 17.96))
//
//        dovizPriceList = mutableListOf<Double>()
//        dovizPriceList.add(18.36)
//        dovizPriceList.add(18.22)
//        dovizPriceList.add(18.22)
//        dovizPriceList.add(18.35)
//        dovizPriceList.add(18.23)
//        dovizPriceList.add(18.23)
//        dovizPriceList.add(18.31)
//        dovizPriceList.add(18.32)
//        dovizList.add(PiyasaBilgisi("EUR", "Euro", dovizPriceList, false, "Döviz", 18.31))
//
//        dovizPriceList = mutableListOf<Double>()
//        dovizPriceList.add(21.57)
//        dovizPriceList.add(21.62)
//        dovizPriceList.add(21.68)
//        dovizPriceList.add(21.72)
//        dovizPriceList.add(21.75)
//        dovizPriceList.add(21.78)
//        dovizPriceList.add(21.75)
//        dovizList.add(PiyasaBilgisi("GBP", "Sterlin", dovizPriceList, false, "Döviz", 21.75))
//
//        dovizPriceList = mutableListOf<Double>()
//        dovizPriceList.add(18.84)
//        dovizPriceList.add(18.74)
//        dovizPriceList.add(18.68)
//        dovizPriceList.add(18.75)
//        dovizPriceList.add(18.63)
//        dovizPriceList.add(18.63)
//        dovizPriceList.add(18.77)
//        dovizList.add(PiyasaBilgisi("CHF", "İsviçre Frangı", dovizPriceList, false, "Döviz", 18.77))
//
//
//    }
}