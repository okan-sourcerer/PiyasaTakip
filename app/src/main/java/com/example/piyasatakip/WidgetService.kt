package com.example.piyasatakip

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView

class WidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return ListRemoteViewsFactory(applicationContext, intent)
    }

    internal inner class ListRemoteViewsFactory(private val mContext: Context, intent: Intent) :
        RemoteViewsFactory {
        private val mWidgetItems: MutableList<PiyasaBilgisi> = ArrayList()
        private val mAppWidgetId: Int

        // Initialize the data set.
        override fun onCreate() {
            println("onCreate() is called for $mAppWidgetId")

            // In onCreate() you setup any connections / cursors to your data source. Heavy lifting,
            // for example downloading or creating content etc, should be deferred to onDataSetChanged()
            // or getViewAt(). Taking more than 20 seconds in this call will result in an ANR.
            mWidgetItems.add(DataHandler.dovizList[0])
            mWidgetItems.add(DataHandler.hisseList[0])
            // We sleep for 3 seconds here to show how the empty view appears in the interim.
            // The empty view is set in the ListWidgetProvider and should be a sibling of the
            // collection view.
            try {
                Thread.sleep(3000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }

        override fun onDestroy() {
            println("onDestroy() is called for $mAppWidgetId")
            // In onDestroy() you should tear down anything that was setup for your data source,
            // eg. cursors, connections, etc.
            mWidgetItems.clear()
        }

        override fun getCount(): Int {
            println("WidgetId $mAppWidgetId")
            return mCount
        }

        // Given the position (index) of a WidgetItem in the array, use the item's text value in
        // combination with the app widget item XML file to construct a RemoteViews object.
        override fun getViewAt(position: Int): RemoteViews {
            println("getViewAt() is called for $mAppWidgetId")
            // position will always range from 0 to getCount() - 1.
            // construct a remote views item based on our widget item xml file, and set the
            // text based on the position.
            val rv = RemoteViews(mContext.packageName, R.layout.widget_full_item)
            // view içindeki elemanlara erişerek viewlara yeni değerler burada atanıyor.
            rv.setTextViewText(R.id.widget_text_item_short, mWidgetItems[position].shortName)
            rv.setTextViewText(R.id.widget_text_item_full, mWidgetItems[position].fullName)
            rv.setTextViewText(R.id.widget_text_item_price, mWidgetItems[position].price[mWidgetItems[position].price.size - 1].toString())
            rv.setTextViewText(R.id.widget_text_item_difference, mWidgetItems[position].difference)
            //rv.setImageViewBitmap(R.id.widget_item_chart, ChartHandler.getBitmapOfChart(mWidgetItems[position], mContext))
//            rv.setImageViewBitmap(R.id.widget_item_chart, createBitmapOfChart(mWidgetItems[position]))
            // Next, we set a fill-intent which will be used to fill-in the pending intent template
            // which is set on the collection view in ListWidgetProvider.
            val extras = Bundle()
            extras.putInt(Widget.EXTRA_ITEM, position)
            val fillInIntent = Intent()
            fillInIntent.putExtras(extras)
            // Make it possible to distinguish the individual on-click
            // action of a given item
            rv.setOnClickFillInIntent(R.id.widget_item_text, fillInIntent)

            // You can do heaving lifting in here, synchronously. For example, if you need to
            // process an image, fetch something from the network, etc., it is ok to do it here,
            // synchronously. A loading view will show up in lieu of the actual contents in the
            // interim.
            try {
                println("Loading view $position")
                Thread.sleep(500)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            // Return the remote views object.
            return rv
        }

        override fun getLoadingView(): RemoteViews? {
            // You can create a custom loading view (for instance when getViewAt() is slow.) If you
            // return null here, you will get the default loading view.
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
        }

        private val mCount = 2

        init {
            mAppWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        fun createBitmapOfChart(data: PiyasaBilgisi): Bitmap? {
            val mInflater = applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            //Inflate the layout into a view and configure it the way you like
            val view = RelativeLayout(applicationContext)
            mInflater.inflate(R.layout.widget_image, view, true)
            val tv = view.findViewById(R.id.widget_temp_image) as AAChartView
            tv.aa_drawChartWithChartModel(ChartHandler.setData(data))

            //Provide it with a layout params. It should necessarily be wrapping the
            //content as we not really going to have a parent for it.
            view.layoutParams = ViewGroup.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
            )

            //Pre-measure the view so that height and width don't remain null.
            view.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )

            //Assign a size and position to the view and all of its descendants
            view.layout(0, 0, view.measuredWidth, view.measuredHeight)

            //Create the bitmap
            val bitmap = Bitmap.createBitmap(
                view.measuredWidth,
                view.measuredHeight,
                Bitmap.Config.ARGB_8888
            )
            //Create a canvas with the specified bitmap to draw into
            val c = Canvas(bitmap)

            //Render this view (and all of its children) to the given Canvas
            view.draw(c)
            return bitmap
        }
    }
}