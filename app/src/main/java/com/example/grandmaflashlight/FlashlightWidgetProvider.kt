package com.example.grandmaflashlight

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.content.ContextCompat
import android.content.pm.PackageManager

class FlashlightWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_TOGGLE_FLASHLIGHT) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val ids = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS)
                ?: appWidgetManager.getAppWidgetIds(
                    android.content.ComponentName(context, FlashlightWidgetProvider::class.java)
                )
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(context, R.string.camera_permission_required, Toast.LENGTH_SHORT).show()
                context.startActivity(Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            } else {
                val newState = FlashlightHelper.toggleTorch(context)
                if (newState != null) {
                    for (id in ids) {
                        updateAppWidget(context, appWidgetManager, id, newState)
                    }
                }
            }
        } else {
            super.onReceive(context, intent)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        torchOn: Boolean = FlashlightHelper.getTorchState(context)
    ) {
        val views = RemoteViews(context.packageName, R.layout.widget_flashlight).apply {
            setTextViewText(
                R.id.widget_label,
                if (torchOn) context.getString(R.string.tap_to_turn_off)
                else context.getString(R.string.tap_to_turn_on)
            )
            setInt(
                R.id.widget_root,
                "setBackgroundResource",
                if (torchOn) R.drawable.widget_background_on else R.drawable.widget_background
            )
            setOnClickPendingIntent(
                R.id.widget_root,
                android.app.PendingIntent.getBroadcast(
                    context,
                    appWidgetId,
                    Intent(context, FlashlightWidgetProvider::class.java).apply {
                        action = ACTION_TOGGLE_FLASHLIGHT
                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
                    },
                    android.app.PendingIntent.FLAG_UPDATE_CURRENT or
                        android.app.PendingIntent.FLAG_IMMUTABLE
                )
            )
        }
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    companion object {
        private const val ACTION_TOGGLE_FLASHLIGHT = "com.example.grandmaflashlight.TOGGLE_FLASHLIGHT"
    }
}
