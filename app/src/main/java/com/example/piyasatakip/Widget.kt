package com.example.piyasatakip

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.RemoteViews
import android.widget.Toast

/**
 * Implementation of App Widget functionality.
 */

class Widget : AppWidgetProvider() {
    // Called when the BroadcastReceiver receives an Intent broadcast.
    // Checks to see whether the intent's action is TOAST_ACTION. If it is, the app widget
    // displays a Toast message for the current item.
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == TOAST_ACTION) {
            var viewIndex = intent.getIntExtra(EXTRA_ITEM, 0)
            Toast.makeText(context, "Item" + ++viewIndex + " selected", Toast.LENGTH_SHORT).show()
        }
        super.onReceive(context, intent)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {


        // There may be multiple widgets active, so update all of them
        // update each of the widgets with the remote adapter
        for (i in appWidgetIds.indices) {
            // Here we setup the intent which points to the StackViewService which will
            // provide the views for this collection.
            val intent = Intent(context, WidgetService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i])

            // When intents are compared, the extras are ignored, so we need to embed the extras
            // into the data so that the extras will not be ignored.
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))

            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.widget)
            views.setRemoteAdapter(R.id.list_view, intent)

            // The empty view is displayed when the collection has no items. It should be a sibling
            // of the collection view.
            views.setEmptyView(R.id.list_view, R.id.empty_view)

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
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i])
            intent.data = Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME))
            val toastPendingIntent = PendingIntent.getBroadcast(
                context, 0, toastIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            views.setPendingIntentTemplate(R.id.list_view, toastPendingIntent)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetIds, views)
        }
    }

    companion object {
        const val TOAST_ACTION = "com.example.android.stackwidget.TOAST_ACTION"
        const val EXTRA_ITEM = "com.example.android.stackwidget.EXTRA_ITEM"
    }
//    override fun onUpdate(
//        context: Context,
//        appWidgetManager: AppWidgetManager,
//        appWidgetIds: IntArray
//    ) {
//        // There may be multiple widgets active, so update all of them
//        for (appWidgetId in appWidgetIds) {
//            updateAppWidget(context, appWidgetManager, appWidgetId)
//        }
//    }
//
//    override fun onEnabled(context: Context) {
//        val remoteView = RemoteViews(context.packageName, R.layout.widget)
//        // Enter relevant functionality for when the first widget is created
//    }
//
//    override fun onDisabled(context: Context) {
//        // Enter relevant functionality for when the last widget is disabled
//    }
//
//    private fun updateAppWidget(
//        context: Context,
//        appWidgetManager: AppWidgetManager,
//        appWidgetId: Int
//    ) {
//        val widgetText = context.getString(R.string.appwidget_text)
//        // Construct the RemoteViews object
//        val views = RemoteViews(context.packageName, R.layout.widget)
//
//        // Instruct the widget manager to update the widget
//        appWidgetManager.updateAppWidget(appWidgetId, views)
//    }
}