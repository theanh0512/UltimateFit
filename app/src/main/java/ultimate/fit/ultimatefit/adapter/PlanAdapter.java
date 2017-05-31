package ultimate.fit.ultimatefit.adapter;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import ultimate.fit.ultimatefit.R;
import ultimate.fit.ultimatefit.UltimateFitWidgetProvider;
import ultimate.fit.ultimatefit.activity.MainActivity;
import ultimate.fit.ultimatefit.data.PlanColumns;
import ultimate.fit.ultimatefit.data.SetColumns;
import ultimate.fit.ultimatefit.data.UltimateFitProvider;
import ultimate.fit.ultimatefit.data.WorkoutColumns;
import ultimate.fit.ultimatefit.data.WorkoutExerciseColumns;
import ultimate.fit.ultimatefit.fragment.TabWorkoutFragment;
import ultimate.fit.ultimatefit.model.Plan;
import ultimate.fit.ultimatefit.model.Set;
import ultimate.fit.ultimatefit.model.Workout;
import ultimate.fit.ultimatefit.model.WorkoutExercise;
import ultimate.fit.ultimatefit.utils.SharedPreferenceHelper;

/**
 * Created by Pham on 18/2/2017.
 */

public class PlanAdapter extends RecyclerView.Adapter<PlanAdapter.ViewHolder> {
    private static final String LOG_TAG = PlanAdapter.class.getSimpleName();
    public static int currentAppliedPlanID = 0;
    private final Context context;
    private boolean updated = false;
    private PlanAdapterOnClickHandler clickHandler;
    private Cursor cursor;

    public PlanAdapter(Context context, PlanAdapterOnClickHandler clickHandler) {
        this.context = context;
        this.clickHandler = clickHandler;
    }

    @Override
    public PlanAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (parent instanceof RecyclerView) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_plan, parent, false);
            view.setFocusable(true);
            return new ViewHolder(view);
        } else {
            throw new RuntimeException("NOT BOUND TO RECYCLER_VIEW");
        }
    }

    @Override
    public void onBindViewHolder(final PlanAdapter.ViewHolder holder, int position) {
        final int pos = position;
        cursor.moveToPosition(position);
        final String planName = cursor.getString(cursor.getColumnIndex(PlanColumns.NAME));
        final String planGoal = cursor.getString(cursor.getColumnIndex(PlanColumns.GOAL));
        String creatorEmail = cursor.getString(cursor.getColumnIndex(PlanColumns.CREATOR));
        holder.textViewPlanName.setText(String.format(Locale.ENGLISH, "%s", planName));
        holder.textViewPlanGoal.setText(String.format(Locale.ENGLISH, context.getString(R.string.text_view_goal) + "%s", planGoal));
        holder.textViewCreator.setText(String.format(Locale.ENGLISH, context.getString(R.string.text_view_creator) + "%s", creatorEmail));
        final int numOfWeeks = cursor.getInt(cursor.getColumnIndex(PlanColumns.NUM_OF_WEEK));
        holder.textViewPlanNumOfWeeks.setText(String.format(Locale.ENGLISH, "%s", numOfWeeks) + context.getString(R.string.text_view_week) + (numOfWeeks == 1 ? "" : "s"));
        //ToDo: if cannot click button, add button to the plan detail instead
        if (cursor.getInt(cursor.getColumnIndex(PlanColumns.ID)) == currentAppliedPlanID) {
            holder.buttonApplyPlan.setVisibility(View.INVISIBLE);
            holder.imageViewOnGoingCheck.setVisibility(View.VISIBLE);
        } else {
            holder.buttonApplyPlan.setVisibility(View.VISIBLE);
            holder.imageViewOnGoingCheck.setVisibility(View.INVISIBLE);
        }
        holder.buttonApplyPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursor.moveToPosition(pos);
                holder.buttonApplyPlan.setVisibility(View.INVISIBLE);
                currentAppliedPlanID = cursor.getInt(cursor.getColumnIndex(PlanColumns.ID));
                SharedPreferenceHelper.getInstance().put(SharedPreferenceHelper.Key.CURRENT_APPLIED_PLANID_INT, currentAppliedPlanID);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ContentValues cv = new ContentValues();
                        cv.put(PlanColumns.APPLIED_DATE, new DateTime().getMillis());
                        context.getContentResolver().update(UltimateFitProvider.Plans.withId(currentAppliedPlanID), cv, null, null);
                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                TabWorkoutFragment tabWorkoutFragment = (TabWorkoutFragment) MainActivity.adapter.getRegisteredFragment(0);
                if (tabWorkoutFragment != null) {
                    tabWorkoutFragment.getLoaderManager().restartLoader(2000, null, tabWorkoutFragment);
                }
                holder.imageViewOnGoingCheck.setVisibility(View.VISIBLE);
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, UltimateFitWidgetProvider.class));
                for (int appWidgetId : appWidgetIds) {
                    UltimateFitWidgetProvider.updateAppWidget(context, appWidgetManager, appWidgetId);
                }
            }
        });
        holder.buttonUploadPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursor.moveToPosition(pos);

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!Objects.equals(MainActivity.userName, MainActivity.ANONYMOUS)) {
                                getPlanDataToUpload(planName, planGoal);
                                updated = true;
                            } else
                                updated = false;
                        } catch (Exception e) {
                            // TODO: handle exception
                            Log.e("log_tag", "Error Parsing Data " + e.toString());
                        }
                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (updated)
                    Toast.makeText(context, R.string.toast_plan_uploaded, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, R.string.toast_sign_in_to_upload, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getPlanDataToUpload(String planName, String planGoal) {
        int dayPerWeek = cursor.getInt(cursor.getColumnIndex(PlanColumns.DAY_PER_WEEK));
        int numOfWeek = cursor.getInt(cursor.getColumnIndex(PlanColumns.NUM_OF_WEEK));
        long planId = cursor.getLong(cursor.getColumnIndex(PlanColumns.ID));
        String planUuid = cursor.getString(cursor.getColumnIndex(PlanColumns.PLAN_UUID));
        String creatorEmail = cursor.getString(cursor.getColumnIndex(PlanColumns.CREATOR));

        List<Workout> workouts = new ArrayList<>();
        //query workout
        Cursor workoutCursor = context.getContentResolver().query(UltimateFitProvider.Workouts.CONTENT_URI, null,
                WorkoutColumns.PLAN_ID + " = '" + planId + "'", null, null);
        for (workoutCursor.moveToFirst(); !workoutCursor.isAfterLast(); workoutCursor.moveToNext()) {
            int dayNumber = workoutCursor.getInt(workoutCursor.getColumnIndex(WorkoutColumns.DAY_NUMBER));
            String workoutBodyPart = workoutCursor.getString(workoutCursor.getColumnIndex(WorkoutColumns.BODY_PART));
            long workoutId = workoutCursor.getInt(workoutCursor.getColumnIndex(WorkoutColumns.ID));

            List<WorkoutExercise> workoutExercises = new ArrayList<>();
            //query workoutExercise
            Cursor workoutExerciseCursor = context.getContentResolver().query(UltimateFitProvider.WorkoutExercises.CONTENT_URI, null,
                    WorkoutExerciseColumns.WORKOUT_ID + " = '" + workoutId + "'", null, null);
            for (workoutExerciseCursor.moveToFirst(); !workoutExerciseCursor.isAfterLast(); workoutExerciseCursor.moveToNext()) {
                int noOfSets = workoutExerciseCursor.getInt(workoutExerciseCursor.getColumnIndex(WorkoutExerciseColumns.SET));
                int rep = workoutExerciseCursor.getInt(workoutExerciseCursor.getColumnIndex(WorkoutExerciseColumns.REP));
                int workoutExerciseNumber = workoutExerciseCursor.getInt(workoutExerciseCursor.getColumnIndex(WorkoutExerciseColumns.WORKOUT_EXERCISE_NUMBER));
                String firstExerciseName = workoutExerciseCursor.getString(workoutExerciseCursor.getColumnIndex(WorkoutExerciseColumns.FIRST_EXERCISE_NAME));
                String firstExerciseImage = workoutExerciseCursor.getString(workoutExerciseCursor.getColumnIndex(WorkoutExerciseColumns.FIRST_EXERCISE_IMAGE));
                int workoutExerciseId = workoutExerciseCursor.getInt(workoutCursor.getColumnIndex(WorkoutExerciseColumns.ID));

                List<Set> sets = new ArrayList<>();
                //query set
                Cursor setCursor = context.getContentResolver().query(UltimateFitProvider.Sets.CONTENT_URI, null,
                        SetColumns.WORKOUT_EXERCISE_ID + " = '" + workoutExerciseId + "'", null, null);
                for (setCursor.moveToFirst(); !setCursor.isAfterLast(); setCursor.moveToNext()) {
                    String exerciseName = setCursor.getString(setCursor.getColumnIndex(SetColumns.SET_NAME));
                    int exerciseNumber = setCursor.getInt(setCursor.getColumnIndex(SetColumns.EXERCISE_NUMBER));
                    int setNumber = setCursor.getInt(setCursor.getColumnIndex(SetColumns.SET_NUMBER));
                    int noOfRep = setCursor.getInt(setCursor.getColumnIndex(SetColumns.REP));
                    double weightRatio = setCursor.getDouble(setCursor.getColumnIndex(SetColumns.WEIGHT_RATIO));
                    int setPosition = setCursor.getInt(setCursor.getColumnIndex(SetColumns.SET_POSITION));
                    Set set = new Set(exerciseName, setNumber, exerciseNumber, noOfRep, weightRatio, setPosition);
                    sets.add(set);
                }
                setCursor.close();

                WorkoutExercise workoutExercise = new WorkoutExercise(firstExerciseName, firstExerciseImage, noOfSets, rep, sets, workoutExerciseNumber);
                workoutExercises.add(workoutExercise);
            }
            workoutExerciseCursor.close();

            Workout workout = new Workout(workoutBodyPart, dayNumber, workoutExercises);
            workouts.add(workout);
        }
        workoutCursor.close();
        Plan toBeUploadedPlan = new Plan(planName, planUuid, creatorEmail, planGoal, numOfWeek, dayPerWeek, workouts);
        //either upload new or update if already uploaded with UUID
        MainActivity.mPlanDatabaseReference.child(planUuid).setValue(toBeUploadedPlan);
    }

    @Override
    public int getItemCount() {
        if (cursor != null) {
            return cursor.getCount();
        }
        return 0;
    }

    public void swapCursor(Cursor newCursor) {
        cursor = newCursor;
        notifyDataSetChanged();
    }

    public interface PlanAdapterOnClickHandler {
        void onClick(int planId);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.image_view_on_going_check)
        ImageView imageViewOnGoingCheck;
        @BindView(R.id.text_view_plan_name)
        TextView textViewPlanName;
        @BindView(R.id.text_view_plan_num_of_weeks)
        TextView textViewPlanNumOfWeeks;
        @BindView(R.id.button_apply_plan)
        Button buttonApplyPlan;
        @BindView(R.id.button_upload_plan)
        Button buttonUploadPlan;
        @BindView(R.id.text_view_plan_goal)
        TextView textViewPlanGoal;
        @BindView(R.id.text_view_creator)
        TextView textViewCreator;

        ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setClickable(true);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            cursor.moveToPosition(position);
            int planId = cursor.getInt(cursor.getColumnIndex(PlanColumns.ID));
            clickHandler.onClick(planId);
        }
    }
}
