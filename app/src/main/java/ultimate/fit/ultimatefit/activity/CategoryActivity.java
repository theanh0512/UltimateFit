package ultimate.fit.ultimatefit.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ultimate.fit.ultimatefit.R;
import ultimate.fit.ultimatefit.adapter.CategoryAdapter;
import ultimate.fit.ultimatefit.adapter.ExerciseAdapter;
import ultimate.fit.ultimatefit.data.UltimateFitProvider;
import ultimate.fit.ultimatefit.data.generated.values.Workout_exercisesValuesBuilder;

public class CategoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = CategoryActivity.class.getSimpleName();
    private static final int CATEGORY_LOADER = 3;
    private static final int EXERCISE_LOADER = 4;
    @BindView(R.id.fab_add_category)
    FloatingActionButton fabAddCategory;
    @BindView(R.id.recyclerview_category)
    RecyclerView recyclerViewCategory;
    @BindView(R.id.recyclerview_exercise)
    RecyclerView recyclerViewExercise;
    @BindView(R.id.toolbarCategory)
    Toolbar toolbarCategory;
    int clickedCategoryId = 1;
    int workoutId;
    private CategoryAdapter categoryAdapter;
    private ExerciseAdapter exerciseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        ButterKnife.bind(this);
        setSupportActionBar(toolbarCategory);

        Intent intent = getIntent();
        if (intent.hasExtra("workoutId"))
            workoutId = intent.getIntExtra("workoutId", 0);

        final LoaderManager.LoaderCallbacks callbacks = this;

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        categoryAdapter = new CategoryAdapter(this, new CategoryAdapter.CategoryAdapterOnClickHandler() {
            @Override
            public void onClick(int categoryId) {
                Log.i(LOG_TAG, "category ID: " + categoryId);
                clickedCategoryId = categoryId;
                recyclerViewCategory.setVisibility(View.INVISIBLE);
                recyclerViewExercise.setVisibility(View.VISIBLE);
                getSupportLoaderManager().restartLoader(EXERCISE_LOADER, null, callbacks);
            }
        });
        recyclerViewCategory.setAdapter(categoryAdapter);
        recyclerViewCategory.setHasFixedSize(true);
        recyclerViewCategory.setLayoutManager(new LinearLayoutManager(this));
        final Context context = this;

        exerciseAdapter = new ExerciseAdapter(this, new ExerciseAdapter.ExerciseAdapterOnClickHandler() {
            @Override
            public void onClick(final int exerciseId, final String exerciseImagePath, final String exerciseName) {
                Log.i(LOG_TAG, "exercise ID: " + exerciseId);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ContentValues workoutExerciseContentValues = new Workout_exercisesValuesBuilder().exerciseIds(String.valueOf(exerciseId)).firstExerciseName(exerciseName)
                                .firstExerciseImage(exerciseImagePath).workoutId(workoutId).rep(8).set(4).values();
                        context.getContentResolver().insert(UltimateFitProvider.WorkoutExercises.CONTENT_URI, workoutExerciseContentValues);
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
            }
        });
        recyclerViewExercise.setAdapter(exerciseAdapter);
        recyclerViewExercise.setHasFixedSize(true);
        recyclerViewExercise.setLayoutManager(new LinearLayoutManager(this));

        getSupportLoaderManager().initLoader(CATEGORY_LOADER, null, this);
        getSupportLoaderManager().initLoader(EXERCISE_LOADER, null, this);
    }

    @OnClick(R.id.fab_add_category)
    public void onClickAddExercise(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
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
                categoryAdapter.swapCursor(data);
                break;
            case EXERCISE_LOADER:
                exerciseAdapter.swapCursor(data);
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case CATEGORY_LOADER:
                categoryAdapter.swapCursor(null);
                break;
            case EXERCISE_LOADER:
                exerciseAdapter.swapCursor(null);
                break;
        }
    }

}
