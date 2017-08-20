package ultimate.fit.ultimatefit.ui.category;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import ultimate.fit.ultimatefit.R;
import ultimate.fit.ultimatefit.adapter.CategoryAdapter1;
import ultimate.fit.ultimatefit.adapter.ExerciseAdapter1;
import ultimate.fit.ultimatefit.data.UltimateFitDatabase;
import ultimate.fit.ultimatefit.data.UltimateFitProvider;
import ultimate.fit.ultimatefit.data.WorkoutExerciseColumns;
import ultimate.fit.ultimatefit.data.generated.values.Workout_exercisesValuesBuilder;
import ultimate.fit.ultimatefit.databinding.ActivityCategoryBinding;

public class CategoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, HasActivityInjector {
    private static final String LOG_TAG = CategoryActivity.class.getSimpleName();
    private static final int CATEGORY_LOADER = 3;
    private static final int EXERCISE_LOADER = 4;
    @Inject
    DispatchingAndroidInjector<Activity> dispatchingAndroidInjector;
    int clickedCategoryId = 1;
    int workoutId = -1;
    int workoutExerciseId = -1;
    String exerciseIds;
    private CategoryAdapter1 categoryAdapter1;
    private ExerciseAdapter1 exerciseAdapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCategoryBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_category);
        setSupportActionBar(binding.toolbarCategory);

        Intent intent = getIntent();
        if (intent.hasExtra("workoutId"))
            workoutId = intent.getIntExtra("workoutId", 0);
        if (intent.hasExtra("workoutExerciseId")) {
            workoutExerciseId = intent.getIntExtra("workoutExerciseId", 0);
            exerciseIds = intent.getStringExtra("exerciseIds");
        }

        final LoaderManager.LoaderCallbacks callbacks = this;

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        categoryAdapter1 = new CategoryAdapter1(this, categoryId -> {
            Log.i(LOG_TAG, "category ID: " + categoryId);
            clickedCategoryId = categoryId;
            binding.included.recyclerviewCategory.setVisibility(View.INVISIBLE);
            binding.included.recyclerviewExercise.setVisibility(View.VISIBLE);
            getSupportLoaderManager().restartLoader(EXERCISE_LOADER, null, callbacks);
        });
        binding.included.recyclerviewCategory.setAdapter(categoryAdapter1);
        binding.included.recyclerviewCategory.setHasFixedSize(true);
        binding.included.recyclerviewCategory.setLayoutManager(new LinearLayoutManager(this));
        final Context context = this;

        exerciseAdapter1 = new ExerciseAdapter1(this, (exerciseId, exerciseImagePath, exerciseName) -> {
            Log.i(LOG_TAG, "exercise ID: " + exerciseId);
            new Thread(() -> {
                if (workoutId != -1) {
                    ContentValues workoutExerciseContentValues = new Workout_exercisesValuesBuilder().exerciseIds(String.valueOf(exerciseId)).firstExerciseName(exerciseName)
                            .firstExerciseImage(exerciseImagePath).workoutId(workoutId).rep(8).set(4).values();
                    context.getContentResolver().insert(UltimateFitProvider.WorkoutExercises.CONTENT_URI, workoutExerciseContentValues);
                } else {
                    ContentValues workoutExerciseContentValues = new Workout_exercisesValuesBuilder().exerciseIds(exerciseIds + "," + String.valueOf(exerciseId)).values();
                    context.getContentResolver().update(UltimateFitProvider.WorkoutExercises.CONTENT_URI, workoutExerciseContentValues,
                            UltimateFitDatabase.Tables.WORKOUT_EXERCISES + "." + WorkoutExerciseColumns.ID + "=" + workoutExerciseId, null);
                }
            }).start();

            Intent data = new Intent();
            if (getParent() == null) {
                setResult(RESULT_OK, data);
            } else {
                getParent().setResult(RESULT_OK, data);
            }
            finish();

//                recyclerViewCategory.setVisibility(View.VISIBLE);
//                recyclerViewExercise.setVisibility(View.INVISIBLE);
        });
        binding.included.recyclerviewExercise.setAdapter(exerciseAdapter1);
        binding.included.recyclerviewExercise.setHasFixedSize(true);
        binding.included.recyclerviewExercise.setLayoutManager(new LinearLayoutManager(this));

        getSupportLoaderManager().initLoader(CATEGORY_LOADER, null, this);
        getSupportLoaderManager().initLoader(EXERCISE_LOADER, null, this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case CATEGORY_LOADER:
                return new CursorLoader(this, UltimateFitProvider.Categories.CONTENT_URI, null, null, null, null);
            case EXERCISE_LOADER:
                return new CursorLoader(this, UltimateFitProvider.Exercises.fromCategory(clickedCategoryId), null, null, null, null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case CATEGORY_LOADER:
                categoryAdapter1.swapCursor(data);
                break;
            case EXERCISE_LOADER:
                exerciseAdapter1.swapCursor(data);
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case CATEGORY_LOADER:
                categoryAdapter1.swapCursor(null);
                break;
            case EXERCISE_LOADER:
                exerciseAdapter1.swapCursor(null);
                break;
        }
    }

    @Override
    public AndroidInjector<Activity> activityInjector() {
        return dispatchingAndroidInjector;
    }
}
