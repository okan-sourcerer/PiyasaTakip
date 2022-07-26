package com.example.piyasatakip

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.RemoteViews


class Widget : AppWidgetProvider() {
    // Called when the BroadcastReceiver receives an Intent broadcast.
    // Check actions using intent.action and if statements to do perform specific actions.
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
    }

    /**
     * Automatically updates every 30 min or when widget is resized.
     */
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // load data for the widget.
        DataHandler.loadData(context)

        super.onUpdate(context, appWidgetManager, appWidgetIds)
        // There may be multiple widgets active, so update all of them
        // update each of the widgets with the remote adapter
        for (i in appWidgetIds.indices) {
            Log.d("Widget", "onUpdate: ")
            // Here we setup the intent which points to the StackViewService which will
            // provide the views for this collection.
            val intent = Intent(context, WidgetService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i])

            // When intents are compared, the extras are ignored, so we need to embed the extras
            // into the data so that the extras will not be ignored.
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetIds, constructRemoteViews(context, intent, i))
        }
    }

    companion object {
        const val TOAST_ACTION = "com.example.android.stackwidget.TOAST_ACTION"
        const val EXTRA_ITEM = "com.example.android.stackwidget.EXTRA_ITEM"
    }

    /**
     * Called when widget is resized or device theme is changed. Updates the widget.
     */
    override fun onAppWidgetOptionsChanged(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        Log.d("Widget", "onAppWidgetOptionsChanged: context null ?=${context == null}")

//        updateMyWidgets(context)
        appWidgetManager!!.notifyAppWidgetViewDataChanged(appWidgetId, R.id.list_view) // en son eklenen satır. Widgetin yeniden boyutlandırıldığında grafiğin kaldırılmasını sağlıyor.
        appWidgetManager.updateAppWidget(appWidgetId, RemoteViews(context!!.packageName, R.layout.widget))

        onUpdate(context,appWidgetManager, appWidgetManager.getAppWidgetIds(ComponentName(context, Widget::class.java)))
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
    }

    private fun constructRemoteViews(context: Context?, intent: Intent, widgetId: Int): RemoteViews{

        // Construct the RemoteViews object
        val views = RemoteViews(context?.packageName, R.layout.widget)
        views.setRemoteAdapter(R.id.list_view, intent)

        // The empty view is displayed when the collection has no items. It should be a sibling
        // of the collection view.
//        views.setEmptyView(R.id.list_view, R.id.empty_view)

        // This section makes it possible for items to have individualized behavior.
        // It does this by setting up a pending intent template. Individuals items of a collection
        // cannot set up their own pending intents. Instead, the collection as a whole sets
        // up a pending intent template, and the individual items set a fillInIntent
        // to create unique behavior on an item-by-item basis.
        val toastIntent = Intent(context, Widget::class.java)

        // Set the action for the intent.
        // When the user touches a particular view, it will have the effect of
        // broadcasting TOAST_ACTION.
        toastIntent.action = TOAST_ACTION
        toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
        intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
        val toastPendingIntent = PendingIntent.getBroadcast(
            context, 0, toastIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        views.setPendingIntentTemplate(R.id.list_view, toastPendingIntent)

        return views
    }
}