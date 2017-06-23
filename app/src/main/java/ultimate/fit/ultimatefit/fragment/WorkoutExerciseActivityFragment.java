package ultimate.fit.ultimatefit.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;
import ultimate.fit.ultimatefit.R;
import ultimate.fit.ultimatefit.activity.CategoryActivity;
import ultimate.fit.ultimatefit.adapter.ExerciseArrayListAdapter;
import ultimate.fit.ultimatefit.adapter.SetAdapter;
import ultimate.fit.ultimatefit.data.ExerciseColumns;
import ultimate.fit.ultimatefit.data.SetColumns;
import ultimate.fit.ultimatefit.data.UltimateFitDatabase;
import ultimate.fit.ultimatefit.data.UltimateFitProvider;
import ultimate.fit.ultimatefit.data.WorkoutExerciseColumns;
import ultimate.fit.ultimatefit.data.generated.values.SetsValuesBuilder;
import ultimate.fit.ultimatefit.data.generated.values.Workout_exercisesValuesBuilder;
import ultimate.fit.ultimatefit.model.Exercise;

import static android.app.Activity.RESULT_OK;

/**
 * A placeholder fragment containing a simple view.
 */
public class WorkoutExerciseActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int SET_LOADER = 103;
    private static final int PICK_EXERCISE_REQUEST = 105;
    private static final String TAG_FRAGMENT = "Exercise Fragment";
    @BindView(R.id.fab_add_exercise_2)
    FloatingActionButton fabAddExercise;
    @BindView(R.id.recycler_view_horizontal_exercises)
    RecyclerView recyclerViewArrayListExercise;
    @BindView(R.id.recycler_view_set)
    RecyclerView recyclerViewSet;
    @BindView(R.id.edit_text_set)
    EditText editTextSet;
    @BindView(R.id.button_generate_sets)
    Button buttonGenerateSet;
    SetAdapter setAdapter;
    ExerciseArrayListAdapter exerciseArrayListAdapter;
    int workoutExerciseId;
    int noOfSet;
    ArrayList<Exercise> arrayListExercise;
    String exerciseIds;

    public WorkoutExerciseActivityFragment() {
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_workout_exercise, container, false);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            ButterKnife.bind(this, rootView);
            workoutExerciseId = bundle.getInt("workoutExerciseId");
            noOfSet = bundle.getInt("set");
            editTextSet.setText(String.valueOf(noOfSet));
            final Fragment fragment = this;
            setAdapter = new SetAdapter(getActivity(), exerciseId -> {
                ExerciseFragment exerciseFragment = ExerciseFragment.newInstance(exerciseId);
                fragment.getFragmentManager().beginTransaction()
                        .replace(R.id.container, exerciseFragment, TAG_FRAGMENT)
                        .addToBackStack(null)
                        .commit();
            });

            recyclerViewSet.setHasFixedSize(true);
            recyclerViewSet.setLayoutManager(new LinearLayoutManager(getActivity()));
            recyclerViewSet.setAdapter(setAdapter);

            exerciseArrayListAdapter = new ExerciseArrayListAdapter(getActivity(), exerciseId -> {
                Iterator<Exercise> iterator = arrayListExercise.iterator();
                boolean deleted = false;
                while (iterator.hasNext()) {
                    if (arrayListExercise.size() == 1) {
                        Toast.makeText(getActivity(), R.string.warning_one_exercise, Toast.LENGTH_SHORT).show();
                        break;
                    }
                    Exercise exercise = iterator.next();
                    if (exercise.getExerciseId() == exerciseId) {
                        iterator.remove();
                        deleted = true;
                        break;
                    }
                }
                exerciseArrayListAdapter.swapArrayList(arrayListExercise);

                //remove exerciseId in workoutExercise
                if (deleted) {
                    Thread thread = new Thread(() -> {
                        try {
                            ArrayList<String> currentExerciseIds = new ArrayList<>(Arrays.asList(exerciseIds.split(",")));
                            Iterator<String> iterator1 = currentExerciseIds.iterator();

                            while (iterator1.hasNext()) {
                                String exercise = iterator1.next();
                                if (Integer.parseInt(exercise) == exerciseId) {
                                    iterator1.remove();
                                    break;
                                }
                            }
                            String newExerciseIds = strJoin(currentExerciseIds.toArray(new String[currentExerciseIds.size()]), ",");
                            exerciseIds = newExerciseIds;
                            ContentValues contentValues;
                            if (currentExerciseIds.size() == 1) {
                                Cursor exerciseCursor = getActivity().getContentResolver().query(UltimateFitProvider.Exercises.withId(Integer.parseInt(newExerciseIds))
                                        , null, null, null, null);
                                exerciseCursor.moveToFirst();
                                String exerciseName = exerciseCursor.getString(exerciseCursor.getColumnIndex(ExerciseColumns.EXERCISE_NAME));
                                String exerciseImage = exerciseCursor.getString(exerciseCursor.getColumnIndex(ExerciseColumns.IMAGE_PATH));
                                contentValues = new Workout_exercisesValuesBuilder().exerciseIds(newExerciseIds).firstExerciseName(exerciseName)
                                        .firstExerciseImage(exerciseImage).values();
                                exerciseCursor.close();
                            } else
                                contentValues = new Workout_exercisesValuesBuilder().exerciseIds(newExerciseIds).values();
                            getActivity().getContentResolver().update(UltimateFitProvider.WorkoutExercises.CONTENT_URI,
                                    contentValues, UltimateFitDatabase.Tables.WORKOUT_EXERCISES + "." + WorkoutExerciseColumns.ID + "=" + workoutExerciseId, null);
                        } catch (Exception e) {
                            // TODO: handle exception
                            Log.e("log_tag", "Error Parsing Data " + e.toString());
                        }
                    });
                    thread.start();
                }
            });
            recyclerViewArrayListExercise.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            recyclerViewArrayListExercise.setAdapter(exerciseArrayListAdapter);
            //ToDo: see if we need to check if we only init loader when the button is already hidden
            getLoaderManager().initLoader(SET_LOADER, null, this);
            arrayListExercise = new ArrayList<>();
            loadExerciseHorizontal();
        }
        return rootView;
    }

    private String strJoin(String[] stringArray, String separator) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0, il = stringArray.length; i < il; i++) {
            if (i > 0)
                stringBuilder.append(separator);
            stringBuilder.append(stringArray[i]);
        }
        return stringBuilder.toString();
    }

    private void loadExerciseHorizontal() {
        //get exercises from workoutExercise
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    arrayListExercise.clear();
                    Cursor workoutExerciseCursor = getActivity().getContentResolver().query(UltimateFitProvider.WorkoutExercises.withId(workoutExerciseId), null, null, null, null);
                    workoutExerciseCursor.moveToFirst();
                    exerciseIds = workoutExerciseCursor.getString(workoutExerciseCursor.getColumnIndex(WorkoutExerciseColumns.EXERCISE_IDS));
                    String exerciseId[] = exerciseIds.split(",");
                    for (int i = 0; i < exerciseId.length; i++) {
                        Cursor exerciseCursor = getActivity().getContentResolver().query(UltimateFitProvider.Exercises.withId(Integer.parseInt(exerciseId[i])), null, null, null, null);
                        exerciseCursor.moveToFirst();
                        String exerciseName = exerciseCursor.getString(exerciseCursor.getColumnIndex(ExerciseColumns.EXERCISE_NAME));
                        Exercise exercise = new Exercise(Integer.parseInt(exerciseId[i]), exerciseName);
                        arrayListExercise.add(exercise);
                        exerciseCursor.close();
                    }
                    workoutExerciseCursor.close();
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
        exerciseArrayListAdapter.swapArrayList(arrayListExercise);
    }

    @OnClick(R.id.button_generate_sets)
    public void onClickGenerateSets() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getActivity().getContentResolver().delete(UltimateFitProvider.Sets.CONTENT_URI,
                            UltimateFitDatabase.Tables.SETS + "." + SetColumns.WORKOUT_EXERCISE_ID + "=" + workoutExerciseId, null);
                    ContentValues[] setValues = new ContentValues[arrayListExercise.size() * noOfSet];
                    int count = 0;
                    for (int j = 0; j < noOfSet; j++) {
                        for (int i = 0; i < arrayListExercise.size(); i++) {
                            int exerciseId = arrayListExercise.get(i).getExerciseId();
                            String exerciseName = arrayListExercise.get(i).getExerciseName();
                            setValues[count] = new SetsValuesBuilder().setName(exerciseName).exerciseId(exerciseId).workoutExerciseId(workoutExerciseId)
                                    .exerciseNumber(i).setNumber(j).rep(0).weightRatio(0.0).weight(0.0).setPosition(count).values();
                            count++;
                        }
                    }
                    getActivity().getContentResolver().bulkInsert(UltimateFitProvider.Sets.CONTENT_URI, setValues);
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
        getLoaderManager().restartLoader(SET_LOADER, null, this);
    }

    @OnClick(R.id.fab_add_exercise_2)
    public void onClickAddExercise(View view) {
        Intent addExerciseIntent = new Intent(getActivity(), CategoryActivity.class);
        addExerciseIntent.putExtra("workoutExerciseId", workoutExerciseId);
        addExerciseIntent.putExtra("exerciseIds", exerciseIds);
        startActivityForResult(addExerciseIntent, PICK_EXERCISE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_EXERCISE_REQUEST) {
            if (resultCode == RESULT_OK) {
                loadExerciseHorizontal();
            }
        }
    }

    @OnEditorAction(R.id.edit_text_set)
    public boolean onEditorActionSet(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            if ((event != null && !event.isShiftPressed()) || event == null) {
                // the user is done typing.

                final Context context = getActivity();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ContentValues contentValues = new Workout_exercisesValuesBuilder().set(Integer.valueOf(editTextSet.getText().toString())).values();
                        context.getContentResolver().update(UltimateFitProvider.WorkoutExercises.CONTENT_URI,
                                contentValues, UltimateFitDatabase.Tables.WORKOUT_EXERCISES + "." + WorkoutExerciseColumns.ID + "=" + workoutExerciseId, null);
                    }
                }).start();
                noOfSet = Integer.valueOf(editTextSet.getText().toString());
                return true; // consume.
            }
        }
        return false; // pass on to other listeners.

    }

    @OnFocusChange(R.id.edit_text_set)
    public void onFocusChangeSet(View v, boolean hasFocus) {
        if (!hasFocus) {
            final Context context = getActivity();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ContentValues contentValues = new Workout_exercisesValuesBuilder().set(Integer.valueOf(editTextSet.getText().toString())).values();
                    context.getContentResolver().update(UltimateFitProvider.WorkoutExercises.CONTENT_URI,
                            contentValues, UltimateFitDatabase.Tables.WORKOUT_EXERCISES + "." + WorkoutExerciseColumns.ID + "=" + workoutExerciseId, null);
                }
            }).start();
            noOfSet = Integer.valueOf(editTextSet.getText().toString());
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case SET_LOADER:
                return new CursorLoader(getActivity(), UltimateFitProvider.Sets.fromWorkoutExercise(workoutExerciseId), null, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case SET_LOADER:
                setAdapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case SET_LOADER:
                setAdapter.swapCursor(null);
                break;
        }
    }
}
