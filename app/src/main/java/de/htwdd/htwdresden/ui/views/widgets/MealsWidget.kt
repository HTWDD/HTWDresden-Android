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
import de.htwdd.htwdresden.ui.models.Meal
import de.htwdd.htwdresden.ui.views.activities.MainActivity
import de.htwdd.htwdresden.utils.extensions.format
import de.htwdd.htwdresden.utils.extensions.guard
import de.htwdd.htwdresden.utils.extensions.runInThread
import de.htwdd.htwdresden.utils.extensions.runInUiThread
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.util.*

class MealsWidget: AppWidgetProvider() {

    companion object {

        private val disposable = CompositeDisposable()

        fun updateAppWidget(
            context: Context?,
            appWidgetManager: AppWidgetManager?,
            appWidgetId: Int
        ) {
            context.guard { return }
            appWidgetManager.guard { return }
            val views = RemoteViews(context!!.packageName, R.layout.widget_meals)
            views.setTextViewText(R.id.tvDate, Date().format("dd. MMMM"))

            val intent = Intent(context, MainActivity::class.java).apply {
                data = Uri.parse("htw://meals")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            views.setOnClickPendingIntent(R.id.widget_meals_layout, PendingIntent.getActivity(context, PendingIntent.FLAG_UPDATE_CURRENT, intent, 0))

            request()
                .runInUiThread()
                .subscribe({ meals ->
                    @Suppress("SENSELESS_COMPARISON")
                    if (context != null) {
                        views.removeAllViews(R.id.llMeals)
                        meals.forEach { meal ->
                            val entryView = RemoteViews(context.packageName, R.layout.widget_meals_entry)
                            entryView.setTextViewText(R.id.tvMealName, meal.name)
                            entryView.setTextViewText(R.id.tvMealPrice, context.getString(R.string.mensa_euro_widget, meal.prices.students ?: 0.00))
                            views.addView(R.id.llMeals, entryView)
                        }
                        appWidgetManager?.updateAppWidget(appWidgetId, views)
                    }
                }, {
                    @Suppress("SENSELESS_COMPARISON")
                    if (context != null) {
                        val entryView = RemoteViews(context.packageName, R.layout.widget_meals_entry)
                        entryView.setTextViewText(R.id.tvMealName, context.getString(R.string.info_internet_no_connection))
                        views.addView(R.id.llMeals, entryView)
                        appWidgetManager?.updateAppWidget(appWidgetId, views)
                    }
                }).addTo(disposable)

            appWidgetManager!!.updateAppWidget(appWidgetId, views)
        }

        private fun request(): Observable<List<Meal>> {
            return RestApi
                .canteenService
                .getMeals("80", Date().format("yyyy-MM-dd"))
                .runInThread(Schedulers.io())
                .map { it.map { jMeal -> Meal.from(jMeal) } }
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