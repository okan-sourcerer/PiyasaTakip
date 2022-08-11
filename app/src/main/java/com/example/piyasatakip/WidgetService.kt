package com.example.piyasatakip

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import com.bumptech.glide.request.target.AppWidgetTarget
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.ExecutionException


class WidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return ListRemoteViewsFactory(applicationContext, intent)
    }

    internal inner class ListRemoteViewsFactory(private val mContext: Context, intent: Intent) :
        RemoteViewsFactory {
        private var mWidgetItems: MutableList<PiyasaBilgisi> = ArrayList()
        private val mAppWidgetId: Int

        // Initialize the data set.
        override fun onCreate() {
            println("onCreate() is called for $mAppWidgetId")

            // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
            // for example downloading or creating content etc, should be deferred to onDataSetChanged()
            // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
            Log.d("WidgetService", "addList: before ${DataHandler.dovizList.size} ${DataHandler.hisseList.size} ${mWidgetItems.size}")

            addAllItems()
            Log.d("WidgetService", "addList: after ${DataHandler.dovizList.size} ${DataHandler.hisseList.size} ${mWidgetItems.size}")
            // We sleep for 3 seconds here to show how the empty view appears in the interim.
            // The empty view is set in the ListWidgetProvider and should be a sibling of the
            // collection view.
//            try {
//                Thread.sleep(3000)
//            } catch (e: InterruptedException) {
//                e.printStackTrace()
//            }
        }

        override fun onDestroy() {
            println("onDestroy() is called for $mAppWidgetId")
            // In onDestroy() you should tear down anything that was setup for your data source,
            // eg. cursors, connections, etc.
            mWidgetItems.clear()
        }

        override fun getCount(): Int {
            println("WidgetId $mAppWidgetId")
            println("Widget item count ${mWidgetItems.size}")
            return mWidgetItems.size
        }

        // Given the position (index) of a WidgetItem in the array, use the item's text value in
        // combination with the app widget item XML file to construct a RemoteViews object.
        override fun getViewAt(position: Int): RemoteViews {
            println("getViewAt() is called for $mAppWidgetId")
            val info = mWidgetItems[position]


            // position will always range from 0 to getCount() - 1.
            // construct a remote views item based on our widget item xml file, and set the
            // text based on the position.
            Log.d("WidgetService", "getViewAt: position = $position")
            val rv = RemoteViews(mContext.packageName, R.layout.widget_full_item)
            if (info.shortName == "Döviz" || info.shortName == "Hisse"){
                rv.setTextViewText(R.id.widget_separator, info.shortName)
                rv.setViewVisibility(R.id.widget_item_linear_textinfo, View.GONE)
                rv.setViewVisibility(R.id.widget_linear_price_info, View.GONE)
                rv.setViewVisibility(R.id.widget_item_chart, View.GONE)
                rv.setViewVisibility(R.id.widget_separator, View.VISIBLE)
                return rv
            } else {
                rv.setViewVisibility(R.id.widget_item_linear_textinfo, View.VISIBLE)
                rv.setViewVisibility(R.id.widget_linear_price_info, View.VISIBLE)
                rv.setViewVisibility(R.id.widget_item_chart, View.VISIBLE)
                rv.setViewVisibility(R.id.widget_separator, View.INVISIBLE)

            }
            // view içindeki elemanlara erişerek viewlara yeni değerler burada atanıyor.
            rv.setTextViewText(R.id.widget_text_item_short, info.shortName)
            rv.setTextViewText(R.id.widget_text_item_full, info.fullName)
            rv.setTextViewText(R.id.widget_text_item_price, "₺${info.priceHistory[info.priceHistory.size - 1]}")

            if (info.imagePath != ""){
                SavedPreference.getImagePath(mContext, info.shortName)
                    ?.let {
                        val bm = DataHandler.loadImageFromStorage(it, info.shortName)
                        if (bm != null)
                            rv.setImageViewBitmap(R.id.widget_item_chart, bm)
                    }
            }

            val lastClose = info.priceHistory[info.priceHistory.size - 2]
            val current = info.current

            val diff = current - lastClose
            rv.setTextViewText(R.id.widget_text_item_difference, String.format("%.2f", diff))
            if (diff < 0){
                rv.setTextColor(R.id.widget_text_item_difference, ContextCompat.getColor(mContext, R.color.brigthRed))
            }
            else if (diff >= 0){
                rv.setTextColor(R.id.widget_text_item_difference, ContextCompat.getColor(mContext, R.color.green))
            }
            //rv.setImageViewBitmap(R.id.widget_item_chart, ChartHandler.getBitmapOfChart(mWidgetItems[position], mContext))
//            rv.setImageViewBitmap(R.id.widget_item_chart, createBitmapOfChart(mWidgetItems[position]))
            // Next, we set a fill-intent which will be used to fill-in the pending intent template
            // which is set on the collection view in ListWidgetProvider.

            val options = AppWidgetManager.getInstance(mContext).getAppWidgetOptions(mAppWidgetId)
            Log.d("WidgetService", "getViewAt: Width = ${options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH)}")
            if (options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) < 320){
                rv.setViewVisibility(R.id.widget_item_chart, View.GONE)
            }
            else if (options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) >= 320){
                rv.setViewVisibility(R.id.widget_item_chart, View.VISIBLE)
            }
            if (options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) < 220){
                rv.setViewVisibility(R.id.widget_text_item_full, View.GONE)
            }
            else if (options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH) >= 220){
                rv.setViewVisibility(R.id.widget_text_item_full, View.VISIBLE)
            }

            val extras = Bundle()
            extras.putInt(Widget.EXTRA_ITEM, position)
            val fillInIntent = Intent()
            fillInIntent.putExtras(extras)
            // Make it possible to distinguish the individual on-click
            // action of a given item
            rv.setOnClickFillInIntent(R.id.widget_item_parent, fillInIntent)

            // You can do heaving lifting in here, synchronously. For example, if you need to
            // process an image, fetch something from the network, etc., it is ok to do it here,
            // synchronously. A loading view will show up in lieu of the actual contents in the
            // interim.

            return rv
        }

        override fun getLoadingView(): RemoteViews? {
            // You can create a custom loading view (for instance when getViewAt() is slow.) If you
            // return null here, you will get the default loading view.
            println("getLoadingView")
            Log.d("WidgetService", "getLoadingView: before ${DataHandler.dovizList.size} ${DataHandler.hisseList.size} ${mWidgetItems.size}")

            addAllItems()
            Log.d("WidgetService", "getLoadingView: after ${DataHandler.dovizList.size} ${DataHandler.hisseList.size} ${mWidgetItems.size}")
            return null
        }

        override fun getViewTypeCount(): Int {
            return 1
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun hasStableIds(): Boolean {
            return true
        }

        override fun onDataSetChanged() {
            // This is triggered when you call AppWidgetManager notifyAppWidgetViewDataChanged
            // on the collection view corresponding to this factory. You can do heaving lifting in
            // here, synchronously. For example, if you need to process an image, fetch something
            // from the network, etc., it is ok to do it here, synchronously. The widget will remain
            // in its current state while work is being done here, so you don't need to worry about
            // locking up the widget.
            Log.d("WidgetService", "onDataSetChanged: before ${DataHandler.dovizList.size} ${DataHandler.hisseList.size} ${mWidgetItems.size}")

            addAllItems()
            Log.d("WidgetService", "onDataSetChanged: after ${DataHandler.dovizList.size} ${DataHandler.hisseList.size} ${mWidgetItems.size}")
        }

        init {
            mAppWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        private fun addAllItems(){
            mWidgetItems.removeAll{true}
            mWidgetItems.add(PiyasaBilgisi("Döviz", "", mutableListOf(), "", 0.0, "", true))
            DataHandler.dovizList.forEach{
                mWidgetItems.add(it)
            }
            if (DataHandler.dovizList.none { it.isFav })
                mWidgetItems.removeAt(0)

            mWidgetItems.add(PiyasaBilgisi("Hisse", "", mutableListOf(), "", 0.0, "", true))
            DataHandler.hisseList.forEach{
                mWidgetItems.add(it)
            }
            if (DataHandler.hisseList.none { it.isFav })
                mWidgetItems.removeAt(mWidgetItems.size - DataHandler.hisseList.size - 1)
            mWidgetItems = mWidgetItems.filter { it.isFav } as MutableList<PiyasaBilgisi>

            if (mWidgetItems.size == 0){
                Log.d("WidgetService", "addAllItems: inside widget.size == 0")
                DataHandler.dovizList.forEach{
                    mWidgetItems.add(it)
                }
            }
        }
    }
}