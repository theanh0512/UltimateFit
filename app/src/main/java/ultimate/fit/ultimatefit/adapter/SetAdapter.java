package ultimate.fit.ultimatefit.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;
import ultimate.fit.ultimatefit.R;
import ultimate.fit.ultimatefit.data.ExerciseColumns;
import ultimate.fit.ultimatefit.data.SetColumns;
import ultimate.fit.ultimatefit.data.UltimateFitDatabase;
import ultimate.fit.ultimatefit.data.UltimateFitProvider;
import ultimate.fit.ultimatefit.data.generated.values.ExercisesValuesBuilder;
import ultimate.fit.ultimatefit.data.generated.values.SetsValuesBuilder;
import ultimate.fit.ultimatefit.utils.CalculationMethods;

/**
 * Created by Pham on 18/2/2017.
 */

public class SetAdapter extends RecyclerView.Adapter<SetAdapter.ViewHolder> {
    private static final String LOG_TAG = SetAdapter.class.getSimpleName();
    final private SetAdapterOnClickHandler clickHandler;
    private double oneRepMax;
    private Cursor cursor;
    private Context context;
    private boolean isPersonalTrainerMode;
    private boolean isManuallyInputWeight;

    public SetAdapter(Context context, SetAdapterOnClickHandler clickHandler) {
        this.context = context;
        this.clickHandler = clickHandler;
    }

    @Override
    public SetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.list_item_set, parent, false);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        isPersonalTrainerMode = preferences.getBoolean(context.getString(R.string.pref_is_pt_key), false);
        isManuallyInputWeight = preferences.getBoolean(context.getString(R.string.pref_is_weight_manually_input_key), false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SetAdapter.ViewHolder holder, int position) {
        cursor.moveToPosition(position);
        String exerciseName = cursor.getString(cursor.getColumnIndex(SetColumns.SET_NAME));
        holder.textViewSetName.setText(String.format(Locale.ENGLISH, "%s", exerciseName));
        int setNumber = cursor.getInt(cursor.getColumnIndex(SetColumns.SET_NUMBER));
        final long exerciseId = cursor.getLong(cursor.getColumnIndex(SetColumns.EXERCISE_ID));
        int exerciseNumber = cursor.getInt(cursor.getColumnIndex(SetColumns.EXERCISE_NUMBER));
        if (exerciseNumber == 0) {
            holder.textViewHeader.setText(context.getString(R.string.format_set, setNumber + 1));
            holder.separatorContainer.setVisibility(View.VISIBLE);
        } else holder.separatorContainer.setVisibility(View.GONE);
        double weight = cursor.getDouble(cursor.getColumnIndex(SetColumns.WEIGHT));
        double weightRatio = cursor.getDouble(cursor.getColumnIndex(SetColumns.WEIGHT_RATIO));
        int rep = cursor.getInt(cursor.getColumnIndex(SetColumns.REP));
        if (!isPersonalTrainerMode) {
            //if is not manually input weight mode, the weight to do will be calculated with set ratio
            if (!isManuallyInputWeight) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Cursor exerciseCursor = context.getContentResolver().query(UltimateFitProvider.Exercises.withId(exerciseId), null, null, null, null);
                            exerciseCursor.moveToFirst();
                            oneRepMax = exerciseCursor.getDouble(exerciseCursor.getColumnIndex(ExerciseColumns.ONE_REP_MAX));
                            exerciseCursor.close();
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
                weight = oneRepMax * weightRatio;
            }
        } else
            weight = weightRatio; //personal trainer mode. PT will be editing weight ratio instead of weight
        holder.editTextRep.setText(String.valueOf(rep));
        holder.editTextWeight.setText(String.valueOf(weight));
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

    public interface SetAdapterOnClickHandler {
        void onClick(int exerciseId);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.text_view_set_name)
        TextView textViewSetName;
        @BindView(R.id.edit_text_set_rep)
        EditText editTextRep;
        @BindView(R.id.edit_text_set_weight)
        EditText editTextWeight;
        @BindView(R.id.separatorContainer)
        FrameLayout separatorContainer;
        @BindView(R.id.text_view_header)
        TextView textViewHeader;

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
            int exerciseId = cursor.getInt(cursor.getColumnIndex(SetColumns.EXERCISE_ID));
            clickHandler.onClick(exerciseId);
        }

        @OnEditorAction(R.id.edit_text_set_rep)
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            int position = getAdapterPosition();
            cursor.moveToPosition(position);
            final int setId = cursor.getInt(0);
            final long exerciseId = cursor.getLong(cursor.getColumnIndex(SetColumns.EXERCISE_ID));
            final double weight = cursor.getDouble(cursor.getColumnIndex(SetColumns.WEIGHT));
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                if ((event != null && !event.isShiftPressed()) || event == null) {
                    // the user is done typing.

                    //final Context context = context;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (weight > 0) {
                                Cursor exerciseCursor = context.getContentResolver().query(UltimateFitProvider.Exercises.withId(exerciseId), null, null, null, null);
                                exerciseCursor.moveToFirst();
                                oneRepMax = exerciseCursor.getDouble(exerciseCursor.getColumnIndex(ExerciseColumns.ONE_REP_MAX));
                                double calculatedOneRepMax = CalculationMethods.weightMaxForOneRep(weight, Integer.valueOf(editTextRep.getText().toString()));
                                if (calculatedOneRepMax > oneRepMax) {
                                    ContentValues exerciseContentValues = new ExercisesValuesBuilder().oneRepMax(calculatedOneRepMax).values();
                                    context.getContentResolver().update(UltimateFitProvider.Exercises.CONTENT_URI,
                                            exerciseContentValues, UltimateFitDatabase.EXERCISES + "." + ExerciseColumns.ID + "=" + exerciseId, null);
                                }
                                exerciseCursor.close();

                            }
                            ContentValues contentValues = new SetsValuesBuilder().rep(Integer.valueOf(editTextRep.getText().toString())).values();
                            context.getContentResolver().update(UltimateFitProvider.Sets.CONTENT_URI,
                                    contentValues, UltimateFitDatabase.Tables.SETS + "." + SetColumns.ID + "=" + setId, null);
                        }
                    }).start();
                    return true; // consume.
                }
            }
            return false; // pass on to other listeners.

        }

        @OnEditorAction(R.id.edit_text_set_weight)
        public boolean onEditorActionSet(TextView v, int actionId, KeyEvent event) {
            int position = getAdapterPosition();
            cursor.moveToPosition(position);
            final int setId = cursor.getInt(0);
            final long exerciseId = cursor.getLong(cursor.getColumnIndex(SetColumns.EXERCISE_ID));
            final int numberOfRep = cursor.getInt(cursor.getColumnIndex(SetColumns.REP));
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    actionId == EditorInfo.IME_ACTION_DONE ||
                    event.getAction() == KeyEvent.ACTION_DOWN &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                if ((event != null && !event.isShiftPressed()) || event == null) {
                    // the user is done typing.

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //only update one rep max in normal mode
                            if (numberOfRep != 0 && !isPersonalTrainerMode) {
                                Cursor exerciseCursor = context.getContentResolver().query(UltimateFitProvider.Exercises.withId(exerciseId), null, null, null, null);
                                exerciseCursor.moveToFirst();
                                oneRepMax = exerciseCursor.getDouble(exerciseCursor.getColumnIndex(ExerciseColumns.ONE_REP_MAX));
                                double calculatedOneRepMax = CalculationMethods.weightMaxForOneRep(Double.valueOf(editTextWeight.getText().toString()), numberOfRep);
                                if (calculatedOneRepMax > oneRepMax) {
                                    ContentValues exerciseContentValues = new ExercisesValuesBuilder().oneRepMax(calculatedOneRepMax).values();
                                    context.getContentResolver().update(UltimateFitProvider.Exercises.CONTENT_URI,
                                            exerciseContentValues, UltimateFitDatabase.EXERCISES + "." + ExerciseColumns.ID + "=" + exerciseId, null);
                                }
                                exerciseCursor.close();
                            }
                            if (isPersonalTrainerMode) {
                                //will update weight ratio instead
                                ContentValues contentValues = new SetsValuesBuilder().weightRatio(Double.valueOf(editTextWeight.getText().toString())).values();
                                context.getContentResolver().update(UltimateFitProvider.Sets.CONTENT_URI,
                                        contentValues, UltimateFitDatabase.Tables.SETS + "." + SetColumns.ID + "=" + setId, null);
                            } else {
                                //normal mode: update weight
                                ContentValues contentValues = new SetsValuesBuilder().weight(Double.valueOf(editTextWeight.getText().toString())).values();
                                context.getContentResolver().update(UltimateFitProvider.Sets.CONTENT_URI,
                                        contentValues, UltimateFitDatabase.Tables.SETS + "." + SetColumns.ID + "=" + setId, null);
                            }
                        }
                    }).start();
                    return true; // consume.
                }
            }
            return false; // pass on to other listeners.

        }

        @OnFocusChange(R.id.edit_text_set_weight)
        public void onFocusChangeWeight(View v, boolean hasFocus) {
            if (!hasFocus) {
                int position = getAdapterPosition();
                if (position != -1) {
                    cursor.moveToPosition(position);
                    final int setId = cursor.getInt(0);
                    final long exerciseId = cursor.getLong(cursor.getColumnIndex(SetColumns.EXERCISE_ID));
                    final int numberOfRep = cursor.getInt(cursor.getColumnIndex(SetColumns.REP));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (numberOfRep != 0 && !isPersonalTrainerMode) {
                                Cursor exerciseCursor = context.getContentResolver().query(UltimateFitProvider.Exercises.withId(exerciseId), null, null, null, null);
                                exerciseCursor.moveToFirst();
                                oneRepMax = exerciseCursor.getDouble(exerciseCursor.getColumnIndex(ExerciseColumns.ONE_REP_MAX));
                                double calculatedOneRepMax = CalculationMethods.weightMaxForOneRep(Double.valueOf(editTextWeight.getText().toString()), numberOfRep);
                                if (calculatedOneRepMax > oneRepMax) {
                                    ContentValues exerciseContentValues = new ExercisesValuesBuilder().oneRepMax(calculatedOneRepMax).values();
                                    context.getContentResolver().update(UltimateFitProvider.Exercises.CONTENT_URI,
                                            exerciseContentValues, UltimateFitDatabase.EXERCISES + "." + ExerciseColumns.ID + "=" + exerciseId, null);
                                }
                                exerciseCursor.close();

                            }
                            if (isPersonalTrainerMode) {
                                //will update weight ratio instead
                                ContentValues contentValues = new SetsValuesBuilder().weightRatio(Double.valueOf(editTextWeight.getText().toString())).values();
                                context.getContentResolver().update(UltimateFitProvider.Sets.CONTENT_URI,
                                        contentValues, UltimateFitDatabase.Tables.SETS + "." + SetColumns.ID + "=" + setId, null);
                            } else {
                                //normal mode: update weight
                                ContentValues contentValues = new SetsValuesBuilder().weight(Double.valueOf(editTextWeight.getText().toString())).values();
                                context.getContentResolver().update(UltimateFitProvider.Sets.CONTENT_URI,
                                        contentValues, UltimateFitDatabase.Tables.SETS + "." + SetColumns.ID + "=" + setId, null);
                            }
                        }
                    }).start();
                }
            }
        }

        @OnFocusChange(R.id.edit_text_set_rep)
        public void onFocusChangeRep(View v, boolean hasFocus) {
            if (!hasFocus) {
                int position = getAdapterPosition();
                if (position != -1) {
                    cursor.moveToPosition(position);
                    final int setId = cursor.getInt(0);
                    final long exerciseId = cursor.getLong(cursor.getColumnIndex(SetColumns.EXERCISE_ID));
                    final double weight = cursor.getDouble(cursor.getColumnIndex(SetColumns.WEIGHT));
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (weight > 0) {
                                Cursor exerciseCursor = context.getContentResolver().query(UltimateFitProvider.Exercises.withId(exerciseId), null, null, null, null);
                                exerciseCursor.moveToFirst();
                                oneRepMax = exerciseCursor.getDouble(exerciseCursor.getColumnIndex(ExerciseColumns.ONE_REP_MAX));
                                double calculatedOneRepMax = CalculationMethods.weightMaxForOneRep(weight, Integer.valueOf(editTextRep.getText().toString()));
                                if (calculatedOneRepMax > oneRepMax) {
                                    ContentValues exerciseContentValues = new ExercisesValuesBuilder().oneRepMax(calculatedOneRepMax).values();
                                    context.getContentResolver().update(UltimateFitProvider.Exercises.CONTENT_URI,
                                            exerciseContentValues, UltimateFitDatabase.EXERCISES + "." + ExerciseColumns.ID + "=" + exerciseId, null);
                                }
                                exerciseCursor.close();

                            }
                            ContentValues contentValues = new SetsValuesBuilder().rep(Integer.valueOf(editTextRep.getText().toString())).values();
                            context.getContentResolver().update(UltimateFitProvider.Sets.CONTENT_URI,
                                    contentValues, UltimateFitDatabase.Tables.SETS + "." + SetColumns.ID + "=" + setId, null);
                        }
                    }).start();
                }
            }
        }
    }
}
