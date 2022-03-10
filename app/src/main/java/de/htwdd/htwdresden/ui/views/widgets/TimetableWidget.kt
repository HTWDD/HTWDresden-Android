package de.htwdd.htwdresden.ui.views.widgets

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.RemoteViews
import de.htwdd.htwdresden.R
import de.htwdd.htwdresden.network.RestApi
import de.htwdd.htwdresden.ui.models.Timetable
import de.htwdd.htwdresden.ui.views.activities.MainActivity
import de.htwdd.htwdresden.utils.extensions.*
import de.htwdd.htwdresden.utils.holders.CryptoSharedPreferencesHolder
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.util.*

class TimetableWidget: AppWidgetProvider() {

    companion object {

        private val cph = CryptoSharedPreferencesHolder.instance
        private val disposable = CompositeDisposable()

        fun updateAppWidget(
            context: Context?,
            appWidgetManager: AppWidgetManager?,
            appWidgetId: Int
        ) {
            context.guard { return }
            appWidgetManager.guard { return }

            val views = RemoteViews(context!!.packageName, R.layout.widget_timetable)
            views.setTextViewText(R.id.tvDate, Date().format("dd. MMMM"))

            val intent = Intent(context, MainActivity::class.java).apply {
                data = if (cph.getStudyAuth() == null) { Uri.parse("htw://studygroup") } else { Uri.parse("htw://timetable") }
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            val pendingIntent = PendingIntent.getActivity(context, 0,intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE);
            views.setOnClickPendingIntent(R.id.widget_timetable_layout, pendingIntent)

            // request time table for actual day
            request(context, appWidgetManager, appWidgetId, views)


            // On Studyauth token change, refresh
            cph.onChanged().runInUiThread().subscribe {
                when (it) {
                    is CryptoSharedPreferencesHolder.SubscribeType.StudyToken -> request(context, appWidgetManager, appWidgetId, views)
                }
            }.addTo(disposable)

            appWidgetManager!!.updateAppWidget(appWidgetId, views)
        }

        private fun request(context: Context?, appWidgetManager: AppWidgetManager?, appWidgetId: Int, views: RemoteViews) {
            val auth = cph.getStudyAuth()
            RestApi.timetableEndpoint
                .timetable(auth?.group ?: "", auth?.major ?: "", auth?.studyYear ?: "")
                .runInThread(Schedulers.io())
                .map { it.map { jTimetable -> Timetable.from(jTimetable) } }
                .map { it.filter { timetable -> timetable.lessonDays.contains(Date().format("MM-dd-yyyy")) } }
                .runInUiThread()
                .subscribe({ timetables ->
                    context.guard { return@subscribe }
                    views.removeAllViews(R.id.llTimetable)
                    if (timetables.isNotEmpty()) {
                        timetables.sortedWith(compareBy { it.beginTime }).forEach { timetable ->
                            val entryView = RemoteViews(context!!.packageName, R.layout.widget_timetable_entry)
                            entryView.setTextViewText(R.id.tvTime, "${timetable.beginTime.format("HH:mm")} - ${timetable.endTime.format("HH:mm")}")
                            entryView.setTextViewText(R.id.tvTimetableName, timetable.name)
                            views.addView(R.id.llTimetable, entryView)
                        }
                    } else {
                        val entryView = RemoteViews(context!!.packageName, R.layout.widget_timetable_empty_entry)
                        views.addView(R.id.llTimetable, entryView)
                    }
                    appWidgetManager?.updateAppWidget(appWidgetId, views)
                }, {
                    error(it)
                    context.guard { return@subscribe }
                    views.removeAllViews(R.id.llTimetable)
                    val entryView = RemoteViews(context!!.packageName, R.layout.widget_timetable_error_entry)
                    views.addView(R.id.llTimetable, entryView)
                    appWidgetManager?.updateAppWidget(appWidgetId, views)
                })
                .addTo(disposable)
        }
    }

    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        appWidgetIds?.forEach {
            updateAppWidget(context, appWidgetManager, it)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        updateAppWidget(context, appWidgetManager, appWidgetId)
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
    }
}