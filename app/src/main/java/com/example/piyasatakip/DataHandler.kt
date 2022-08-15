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
    var hisseList: MutableList<PiyasaBilgisi> = mutableListOf()
    // döviz birimlerinin bilgilerinin tutulduğu liste
    var dovizList: MutableList<PiyasaBilgisi> = mutableListOf()

    // verilerde güncelleme yapıldığında uygulama içerisinde güncellenmesi için fieldlar tutuluyor.
    var adapter: ItemAdapter? = null
    var recycler: RecyclerView? = null

    // uygulama veya widget açıldığında bir instance oluşturulacak. Veritabanından veriler çekilecek.
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
                            view.refreshValues(info)
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

    /**
     * Loads data from firebase firestore. This method is accessed from the constructor of the object
     * as a fallback method. For whatever reason other loadData methods are not accessed properly.
     * This will populate the lists but DOES NOT updates the recyclerview adapter.
     */
    private fun loadData(){
        Log.d("DataHandler", "loadData: from constructor")

        // access firestore instance collection with the specified name.
        // get data as sorted. Clear the list from the previous entries to avoid showing multiple instances of
        // the same object.
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
        Log.d("DataHandler", "loadData: After firebase listener")

        // sort lists to show favourited infos at the top.
        hisseList.sortBy { !it.isFav }
        dovizList.sortBy { !it.isFav }
    }

    /**
     * Loads data from firebase firestore database. This method is accessed from the widget at the
     * onUpdate callback. This method loads data from the database to the lists and grabs images from
     * firebase storage instance. After grabbing the images, images will be saved to the device storage.
     */
    fun loadData(context: Context){
        Log.d("DataHandler", "loadData: with context")

        // access firestore instance collection with the specified name.
        // get data as sorted. Clear the list from the previous entries to avoid showing multiple instances of
        // the same object.
        FirebaseFirestore.getInstance().collection("1")
            .orderBy("shortName")
            .get()
            .addOnSuccessListener { result ->
                hisseList.removeAll { true } // remove all items from the lists to avoid showing multiple instances.
                dovizList.removeAll { true }
                for (document in result) { // for every document in the database convert the data to object and add to the list.
                    val info = document.toObject(PiyasaBilgisi::class.java)
                    if (info.type == "Hisse"){
                        hisseList.add(info)
                    }else{
                        dovizList.add(info)
                    }
                    // download the images from the firebase storage and save
                    FirebaseStorage.getInstance().reference.child(info.imagePath).getBytes(1024*1024)
                        .addOnSuccessListener {
                            val bm = BitmapFactory.decodeByteArray(it, 0, it.size)
                            val path = saveToInternalStorage(info.shortName, bm, context)
                            if (path != null) {
                                SavedPreference.saveImagePath(context, info.shortName, path)
                            }
                            Log.d("WidgetService", "getViewAt: image size ${it.size}")
                        }
                    // get isFav info from the SavedPreference
                    info.isFav = SavedPreference.getFav(context, info.shortName)
                    Log.d("DataHandler", "addOnSuccessListener $info ${hisseList.size} ${dovizList.size}")
                }
                // sort the list to show favourited items at the top
                hisseList.sortBy { !it.isFav }
                dovizList.sortBy { !it.isFav }
            }
            .addOnFailureListener { exception ->
                Log.w("DataHandler", "Error getting documents: ", exception)
                Toast.makeText(context, "Internet bağlantısı bulunamadı", Toast.LENGTH_SHORT).show()
            }
        Log.d("DataHandler", "loadData: After firebase listener")
    }

    /**
     * This method is accessed from the MainActivity right after initializing views. Adapter is used to
     * refresh the list so when our listener is activated and values are updated when firebase has an update.
     */
    fun loadData(adapter: ItemAdapter, context: Context){
        Log.d("DataHandler", "loadData: with context and adapter")

        // access firestore instance collection with the specified name.
        // get data as sorted. Clear the list from the previous entries to avoid showing multiple instances of
        // the same object.
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
                    // download the chart to the device.
//                    FirebaseStorage.getInstance().reference.child(info.imagePath).getBytes(1024*1024)
//                        .addOnSuccessListener {
//                            val bm = BitmapFactory.decodeByteArray(it, 0, it.size)
//                            val path = saveToInternalStorage(info.shortName, bm, context)
//                            if (path != null) {
//                                SavedPreference.saveImagePath(context, info.shortName, path)
//                            }
//                            Log.d("WidgetService", "getViewAt: image size ${it.size}")
//                        }
                    // get if this element is in user's favourite
                    info.isFav = SavedPreference.getFav(context, info.shortName)
                    Log.d("DataHandler", "addOnSuccessListener $info ${hisseList.size} ${dovizList.size}")
                }
                // update the recyclerview using adapter.
                adapter.notifyDataSetChanged()
                this.adapter = adapter
                // sort the list so favourites show at the top
                hisseList.sortBy { !it.isFav }
                dovizList.sortBy { !it.isFav }
            }
            .addOnFailureListener { exception ->
                Log.w("DataHandler", "Error getting documents: ", exception)
                Toast.makeText(context, "Internet bağlantısı bulunamadı", Toast.LENGTH_SHORT).show()
            }

        Log.d("DataHandler", "loadData: After firebase listener")
    }

    /**
     * This is accessed when user favourited an element. Lists needs to be re-arranged using adapter.
     */
    fun sortLists(){
        Log.d("DataHandler", "sortLists: Sorting lists if adapter exists. ${adapter!=null}")
        if (adapter != null){
            Log.d("DataHandler", "sortLists: sorting hisseList")
            hisseList.sortBy { it.shortName } // sort alphabetically
            hisseList.sortBy { !it.isFav } // show the favourites at the top
            dovizList.sortBy { it.shortName }
            dovizList.sortBy { !it.isFav }

            adapter!!.notifyDataSetChanged()
        }
    }

    /**
     * Saves image, with the specified name and with the image downloaded from the firebase storage as bitmap to the device
     * @param imageName Info's shortname as the image name
     * @param bitmapImage downloaded bitmap
     * @param context application context to access device storage.
     * @return returns the path of the saved image
     */
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

    /**
     * This method is accessed from the widget. Loads the image from the device storage.
     * Combines path and name of the image to get the file.
     * @param path local path of the image
     * @param name Name of the info.
     * @return returns bitmap of the image to be shown in the widget.
     */
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