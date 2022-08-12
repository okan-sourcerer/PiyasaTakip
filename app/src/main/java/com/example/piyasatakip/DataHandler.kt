package com.example.piyasatakip

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import java.io.*


@SuppressLint("NotifyDataSetChanged")
object DataHandler {

    // hisse bilgilerinin tutulduğu list
    val hisseList: MutableList<PiyasaBilgisi> = mutableListOf()
    // döviz birimlerinin bilgilerinin tutulduğu liste
    val dovizList: MutableList<PiyasaBilgisi> = mutableListOf()

    var adapter: ItemAdapter? = null
    var recycler: RecyclerView? = null

    // statik veriler bu objeye erişildiğinde oluşturuluyor.
    init {
        Log.d("DataHandler", "init: ")

        loadData()

        FirebaseFirestore.getInstance().collection("1").addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.w("DataHandler", "addSnapshotListener listen: error")
                return@addSnapshotListener
            }

            for (doc in snapshot?.documentChanges!!){
                val info = doc.document.toObject<PiyasaBilgisi>()
                Log.d("DataHandler", "addSnapshotListener: adapter is ${adapter==null}")
                when(doc.type){
                    DocumentChange.Type.ADDED -> {
                        Log.d("DataHandler", "addSnapshotListener New info: ${doc.document.data}")
                        if (info.type == "Hisse"){
                            if (hisseList.find { it.shortName == info.shortName } == null)
                                hisseList.add(info)
                        }
                        else if (dovizList.find { it.shortName == info.shortName } == null){
                            dovizList.add(info)
                        }
                        adapter?.notifyDataSetChanged()
                    }
                    DocumentChange.Type.MODIFIED -> {
                        Log.d("DataHandler", "addSnapshotListener Updated info: ${doc.document.data}")
                        var pos: Int
                        var element: PiyasaBilgisi?
                        if (info.type == "Hisse"){
                            element = hisseList.find { info.shortName == it.shortName }
                            pos = hisseList.indexOf(element)
                        } else {
                            element = dovizList.find { info.shortName == it.shortName }
                            pos = dovizList.indexOf(element)
                        }
                        element?.priceHistory = info.priceHistory
                        element?.current = info.current
                        val view = recycler?.findViewHolderForLayoutPosition(pos) as ItemAdapter.ViewHolder
                        if (element != null) {
                            view.refreshChart(element)
                        }
                    }
                    DocumentChange.Type.REMOVED -> {
                        Log.d("DataHandler", "addSnapshotListener Removed info: ${doc.document.data}")
                        if (info.type == "Hisse"){
                            hisseList.removeIf { info.shortName == it.shortName }
                        } else {
                            dovizList.removeIf { info.shortName == it.shortName }
                        }
                        adapter?.notifyDataSetChanged()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun loadData(){
        Log.d("DataHandler", "loadData: from constructor")

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

    fun loadData(context: Context){
        Log.d("DataHandler", "loadData: with context")

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
            }
            .addOnFailureListener { exception ->
                Log.w("DataHandler", "Error getting documents: ", exception)
                Toast.makeText(context, "Internet bağlantısı bulunamadı", Toast.LENGTH_SHORT).show()
            }
    }

    fun loadData(adapter: ItemAdapter, context: Context){
        Log.d("DataHandler", "loadData: with context and adapter")

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
                this.adapter = adapter
            }
            .addOnFailureListener { exception ->
                Log.w("DataHandler", "Error getting documents: ", exception)
                Toast.makeText(context, "Internet bağlantısı bulunamadı", Toast.LENGTH_SHORT).show()
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
}