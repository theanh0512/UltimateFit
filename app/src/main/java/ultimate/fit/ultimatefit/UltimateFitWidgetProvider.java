package ultimate.fit.ultimatefit;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.RemoteViews;

import java.util.Locale;

import ultimate.fit.ultimatefit.activity.WorkoutActivity;
import ultimate.fit.ultimatefit.adapter.PlanAdapter;
import ultimate.fit.ultimatefit.data.UltimateFitProvider;
import ultimate.fit.ultimatefit.data.WorkoutColumns;

/**
 * Implementation of App Widget functionality.
 */
public class UltimateFitWidgetProvider extends AppWidgetProvider {

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId) {

        Cursor setCursor = context.getContentResolver().query(UltimateFitProvider.Workouts.fromPlan(PlanAdapter.currentAppliedPlanID), null, null, null, null);
        setCursor.moveToFirst();
        int dayNumber = setCursor.getInt(setCursor.getColumnIndex(WorkoutColumns.DAY_NUMBER));
        String bodyPart = setCursor.getString(setCursor.getColumnIndex(WorkoutColumns.BODY_PART));
        CharSequence widgetTextDay = String.format(Locale.ENGLISH, "%s", context.getString(R.string.text_view_day) + dayNumber);
        CharSequence widgetTextBodyPart = String.format(Locale.ENGLISH, "%s", bodyPart);
        int workoutId = setCursor.getInt(setCursor.getColumnIndex(WorkoutColumns.ID));
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.ultimate_fit_widget_provider);
        views.setTextViewText(R.id.appwidget_text_view_day, widgetTextDay);
        views.setTextViewText(R.id.appwidget_text_view_body_part, widgetTextBodyPart);

        //Create an Intent to launch WorkoutActivity when clicked
        Intent intent = new Intent(context, WorkoutActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("dayNumber", dayNumber);
        bundle.putString("bodyPart", bodyPart);
        bundle.putInt("workoutId", workoutId);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        views.setOnClickPendingIntent(R.id.appwidget_holder, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

