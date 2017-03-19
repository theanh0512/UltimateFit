package ultimate.fit.ultimatefit.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;
import ultimate.fit.ultimatefit.R;
import ultimate.fit.ultimatefit.data.UltimateFitDatabase;
import ultimate.fit.ultimatefit.data.UltimateFitProvider;
import ultimate.fit.ultimatefit.data.WorkoutExerciseColumns;
import ultimate.fit.ultimatefit.data.generated.values.Workout_exercisesValuesBuilder;

/**
 * Created by Pham on 18/2/2017.
 */

public class WorkoutExerciseAdapter extends RecyclerView.Adapter<WorkoutExerciseAdapter.ViewHolder> {
    private static final String LOG_TAG = WorkoutExerciseAdapter.class.getSimpleName();
    final private WorkoutExerciseAdapterOnClickHandler clickHandler;
    private Cursor cursor;
    private Context context;

    public WorkoutExerciseAdapter(Context context, WorkoutExerciseAdapterOnClickHandler clickHandler) {
        this.context = context;
        this.clickHandler = clickHandler;
    }

    @Override
    public WorkoutExerciseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.list_item_workout_exercise, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final WorkoutExerciseAdapter.ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        String exerciseIds = cursor.getString(cursor.getColumnIndex(WorkoutExerciseColumns.EXERCISE_IDS));
        String exerciseName = "";
        String[] exerciseIdArray = exerciseIds.split(",");
        if (exerciseIdArray.length == 1)
            exerciseName = cursor.getString(cursor.getColumnIndex(WorkoutExerciseColumns.FIRST_EXERCISE_NAME));
        else if (exerciseIdArray.length == 2)
            exerciseName = "Super Set";
        else exerciseName = "Giant Set";
        holder.textViewWorkoutExerciseName.setText(String.format(Locale.ENGLISH, "%s", exerciseName));
        int set = cursor.getInt(cursor.getColumnIndex(WorkoutExerciseColumns.SET));
        int rep = cursor.getInt(cursor.getColumnIndex(WorkoutExerciseColumns.REP));
        holder.editTextRep.setText(String.valueOf(rep));
        holder.editTextSet.setText(String.valueOf(set));
        final String imagePath = cursor.getString(cursor.getColumnIndex(WorkoutExerciseColumns.FIRST_EXERCISE_IMAGE));
        try {
            Picasso.with(context).load(imagePath).placeholder(R.drawable.ic_place_holder)
                    .error(R.drawable.ic_error_fallback).into(holder.imageViewWorkoutExerciseImage, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(context)
                            .load(imagePath)
                            .into(holder.imageViewWorkoutExerciseImage, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
//                                        Log.v("Picasso","Could not fetch image");
                                }
                            });
                }
            });
        } catch (IndexOutOfBoundsException e) {
        }
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

    public interface WorkoutExerciseAdapterOnClickHandler {
        void onClick(int workoutExerciseId);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.imageViewWorkoutExerciseImage)
        ImageView imageViewWorkoutExerciseImage;
        @BindView(R.id.textViewWorkoutExerciseName)
        TextView textViewWorkoutExerciseName;
        @BindView(R.id.edit_text_workout_exercise_rep)
        EditText editTextRep;
        @BindView(R.id.edit_text_workout_exercise_set)
        EditText editTextSet;

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
            int workoutExerciseId = cursor.getInt(0);
            clickHandler.onClick(workoutExerciseId);
        }

        @OnEditorAction(R.id.edit_text_workout_exercise_rep)
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            int position = getAdapterPosition();
            cursor.moveToPosition(position);
            final int workoutExerciseId = cursor.getInt(0);
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                if (!event.isShiftPressed()) {
                    // the user is done typing.

                    //final Context context = context;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ContentValues contentValues = new Workout_exercisesValuesBuilder().rep(Integer.valueOf(editTextRep.getText().toString())).values();
                            context.getContentResolver().update(UltimateFitProvider.WorkoutExercises.CONTENT_URI,
                                    contentValues, UltimateFitDatabase.Tables.WORKOUT_EXERCISES + "." + WorkoutExerciseColumns.ID + "=" + workoutExerciseId, null);
                        }
                    }).start();
                    return true; // consume.
                }
            }
            return false; // pass on to other listeners.

        }

        @OnEditorAction(R.id.edit_text_workout_exercise_set)
        public boolean onEditorActionSet(TextView v, int actionId, KeyEvent event) {
            int position = getAdapterPosition();
            cursor.moveToPosition(position);
            final int workoutExerciseId = cursor.getInt(0);
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                if (!event.isShiftPressed()) {
                    // the user is done typing.

                    //final Context context = context;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            ContentValues contentValues = new Workout_exercisesValuesBuilder().set(Integer.valueOf(editTextSet.getText().toString())).values();
                            context.getContentResolver().update(UltimateFitProvider.WorkoutExercises.CONTENT_URI,
                                    contentValues, UltimateFitDatabase.Tables.WORKOUT_EXERCISES + "." + WorkoutExerciseColumns.ID + "=" + workoutExerciseId, null);
                        }
                    }).start();
                    return true; // consume.
                }
            }
            return false; // pass on to other listeners.

        }
    }
}
