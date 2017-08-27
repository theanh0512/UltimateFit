package ultimate.fit.ultimatefit.ui.category;

import android.arch.lifecycle.LifecycleActivity;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import javax.inject.Inject;

import ultimate.fit.ultimatefit.R;
import ultimate.fit.ultimatefit.databinding.ActivityCategoryBinding;
import ultimate.fit.ultimatefit.injection.Injectable;

public class CategoryActivity extends LifecycleActivity implements Injectable {
    private static final String LOG_TAG = CategoryActivity.class.getSimpleName();
    private static final int CATEGORY_LOADER = 3;
    private static final int EXERCISE_LOADER = 4;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    int clickedCategoryId = 1;
    int workoutId = -1;
    int workoutExerciseId = -1;
    String exerciseIds;
    private CategoryAdapter categoryAdapter;
    private ExerciseAdapter exerciseAdapter;

    private CategoryViewModel categoryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCategoryBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_category);
        setActionBar(binding.toolbarCategory);
        getActionBar().setDisplayHomeAsUpEnabled(false);

        Intent intent = getIntent();
        if (intent.hasExtra("workoutId")) {
            workoutId = intent.getIntExtra("workoutId", 0);
        }
        if (intent.hasExtra("workoutExerciseId")) {
            workoutExerciseId = intent.getIntExtra("workoutExerciseId", 0);
            exerciseIds = intent.getStringExtra("exerciseIds");
        }

        categoryViewModel = ViewModelProviders.of(this, viewModelFactory).get(CategoryViewModel.class);
        categoryViewModel.setLastUpdated(100000);
        categoryViewModel.getCategoryApiResponseList().observe(this, listResource -> {
            binding.included.setCategoryApiResponseResource(listResource);
            categoryAdapter.replace(listResource == null ? null : listResource.data);
            binding.included.executePendingBindings();
            binding.executePendingBindings();
        });

        exerciseAdapter = new ExerciseAdapter(exercise -> {

        });

        categoryAdapter = new CategoryAdapter(category -> {
            Toast.makeText(this, "clicked " + category.category.name, Toast.LENGTH_LONG).show();
            categoryViewModel.setCategoryPk(category.pk);
            categoryViewModel.getExerciseList().observe(this, listResource -> {
                exerciseAdapter.replace(listResource == null ? null : listResource.data);
                binding.included.executePendingBindings();
                binding.executePendingBindings();
            });
            binding.included.recyclerviewExercise.setVisibility(View.VISIBLE);
            binding.included.recyclerviewCategory.setVisibility(View.INVISIBLE);
        });
        binding.included.recyclerviewExercise.setAdapter(exerciseAdapter);
        binding.included.recyclerviewExercise.setHasFixedSize(true);
        binding.included.recyclerviewExercise.setLayoutManager(new LinearLayoutManager(this));

        binding.included.recyclerviewCategory.setAdapter(categoryAdapter);
        binding.included.recyclerviewCategory.setHasFixedSize(true);
        binding.included.recyclerviewCategory.setLayoutManager(new LinearLayoutManager(this));

//        exerciseAdapter1 = new ExerciseAdapter1(this, (exerciseId, exerciseImagePath, exerciseName) -> {
//            Log.i(LOG_TAG, "exercise ID: " + exerciseId);
//            new Thread(() -> {
//                if (workoutId != -1) {
//                    ContentValues workoutExerciseContentValues = new Workout_exercisesValuesBuilder().exerciseIds(String.valueOf(exerciseId)).firstExerciseName(exerciseName)
//                            .firstExerciseImage(exerciseImagePath).workoutId(workoutId).rep(8).set(4).values();
//                    context.getContentResolver().insert(UltimateFitProvider2.WorkoutExercises.CONTENT_URI, workoutExerciseContentValues);
//                } else {
//                    ContentValues workoutExerciseContentValues = new Workout_exercisesValuesBuilder().exerciseIds(exerciseIds + "," + String.valueOf(exerciseId)).values();
//                    context.getContentResolver().update(UltimateFitProvider2.WorkoutExercises.CONTENT_URI, workoutExerciseContentValues,
//                            UltimateFitDatabase.Tables.WORKOUT_EXERCISES + "." + WorkoutExerciseColumns.ID + "=" + workoutExerciseId, null);
//                }
//            }).start();
//
//            Intent data = new Intent();
//            if (getParent() == null) {
//                setResult(RESULT_OK, data);
//            } else {
//                getParent().setResult(RESULT_OK, data);
//            }
//            finish();
//
////                recyclerViewCategory.setVisibility(View.VISIBLE);
////                recyclerViewExercise.setVisibility(View.INVISIBLE);
//        });
//        binding.included.recyclerviewExercise.setAdapter(exerciseAdapter1);

    }


//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        switch (id) {
//            case CATEGORY_LOADER:
//                return new CursorLoader(this, UltimateFitProvider2.Categories.CONTENT_URI, null, null, null, null);
//            case EXERCISE_LOADER:
//                return new CursorLoader(this, UltimateFitProvider2.Exercises.fromCategory(clickedCategoryId), null, null, null, null);
//            default:
//                return null;
//        }
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
//        switch (loader.getId()) {
//            case CATEGORY_LOADER:
//                categoryAdapter1.swapCursor(data);
//                break;
//            case EXERCISE_LOADER:
//                exerciseAdapter1.swapCursor(data);
//                break;
//        }
//
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//        switch (loader.getId()) {
//            case CATEGORY_LOADER:
//                categoryAdapter1.swapCursor(null);
//                break;
//            case EXERCISE_LOADER:
//                exerciseAdapter1.swapCursor(null);
//                break;
//        }
//    }

}
