package ultimate.fit.ultimatefit.ui;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import butterknife.OnFocusChange;
import ultimate.fit.ultimatefit.R;
import ultimate.fit.ultimatefit.adapter.WorkoutExerciseAdapter;
import ultimate.fit.ultimatefit.data.UltimateFitDatabase;
import ultimate.fit.ultimatefit.data.UltimateFitProvider;
import ultimate.fit.ultimatefit.data.WorkoutColumns;
import ultimate.fit.ultimatefit.data.generated.values.WorkoutsValuesBuilder;

public class WorkoutActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = WorkoutActivity.class.getSimpleName();
    private static final int PICK_EXERCISE_REQUEST = 101;
    private static final int WORKOUT_EXERCISE_LOADER = 102;
    @BindView(R.id.fab_add_exercise)
    FloatingActionButton fabAddExercise;
    @BindView(R.id.recyclerview_exercise_list)
    RecyclerView recyclerViewExerciseList;
    @BindView(R.id.toolbar_workout)
    Toolbar toolbarWorkout;
    @BindView(R.id.textViewDayNumber)
    TextView textViewDayNumber;
    @BindView(R.id.editTextBodyPart)
    EditText editTextBodyPart;
    WorkoutExerciseAdapter workoutExerciseAdapter;

    int workoutId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        ButterKnife.bind(this);
        setSupportActionBar(toolbarWorkout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            int dayNumber = bundle.getInt("dayNumber");
            String bodyPart = bundle.getString("bodyPart");
            workoutId = bundle.getInt("workoutId");
            textViewDayNumber.setText(String.valueOf(dayNumber));
            editTextBodyPart.setText(bodyPart);
        }
        final Context context = this;
        workoutExerciseAdapter = new WorkoutExerciseAdapter(this, new WorkoutExerciseAdapter.WorkoutExerciseAdapterOnClickHandler() {
            @Override
            public void onClick(int workoutExerciseId, int noOfSet) {
                Intent intent = new Intent(context, WorkoutExerciseActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("workoutExerciseId", workoutExerciseId);
                bundle.putInt("set", noOfSet);
                intent.putExtras(bundle);
                if (Build.VERSION.SDK_INT >= 21) {
                    // Call some material design APIs here
                    startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(WorkoutActivity.this).toBundle());
                } else {
                    startActivity(intent);
                    // Implement this feature without material design
                }
            }
        });
        recyclerViewExerciseList.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewExerciseList.setHasFixedSize(true);
        recyclerViewExerciseList.setAdapter(workoutExerciseAdapter);
        getSupportLoaderManager().initLoader(WORKOUT_EXERCISE_LOADER, null, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_EXERCISE_REQUEST) {
            if (resultCode == RESULT_OK) {
                getSupportLoaderManager().restartLoader(WORKOUT_EXERCISE_LOADER, null, this);
            }
        }
    }

    @OnClick(R.id.fab_add_exercise)
    public void onClickAddExercise() {
        Intent addExerciseIntent = new Intent(this, CategoryActivity.class);
        addExerciseIntent.putExtra("workoutId", workoutId);
        startActivityForResult(addExerciseIntent, PICK_EXERCISE_REQUEST);
    }

    @OnEditorAction(R.id.editTextBodyPart)
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            if ((event != null && !event.isShiftPressed()) || event == null) {
                // the user is done typing.

                final Context context = this;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ContentValues workoutContentValues = new WorkoutsValuesBuilder().bodyPart(editTextBodyPart.getText().toString()).values();
                        context.getContentResolver().update(UltimateFitProvider.Workouts.CONTENT_URI,
                                workoutContentValues, UltimateFitDatabase.WORKOUTS + "." + WorkoutColumns.ID + "=" + workoutId, null);
                    }
                }).start();
                return true; // consume.
            }
        }
        return false; // pass on to other listeners.
    }

    @OnFocusChange(R.id.editTextBodyPart)
    public void onFocusChangeBodyPart(View v, boolean hasFocus) {
        if (!hasFocus) {
            final Context context = this;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ContentValues workoutContentValues = new WorkoutsValuesBuilder().bodyPart(editTextBodyPart.getText().toString()).values();
                    context.getContentResolver().update(UltimateFitProvider.Workouts.CONTENT_URI,
                            workoutContentValues, UltimateFitDatabase.WORKOUTS + "." + WorkoutColumns.ID + "=" + workoutId, null);
                }
            }).start();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_note:
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(this);

                LayoutInflater inflater_actionbar = LayoutInflater.from(this);
                View v = inflater_actionbar.inflate(R.layout.dialog_note, null);
                builderSingle.setView(v);
                AlertDialog alertDialog;
                alertDialog = builderSingle.create();

                Cursor workoutCursor = getContentResolver().query(UltimateFitProvider.Workouts.withId(workoutId), null, null, null, null);
                workoutCursor.moveToFirst();
                String originalNote = workoutCursor.getString(workoutCursor.getColumnIndex(WorkoutColumns.NOTE_OF_WORKOUT));
                EditText editTextNote = (EditText) v.findViewById(R.id.edit_text_note);
                editTextNote.setText(originalNote);
                workoutCursor.close();

                Button buttonSave = (Button) v.findViewById(R.id.button_save);
                buttonSave.setOnClickListener(v1 -> {
                            new Thread(() -> {
                                String note = editTextNote.getText().toString();
                                ContentValues workoutContentValues = new WorkoutsValuesBuilder().noteOfWorkout(note).values();
                                getContentResolver().update(UltimateFitProvider.Workouts.CONTENT_URI,
                                        workoutContentValues, UltimateFitDatabase.WORKOUTS + "." + WorkoutColumns.ID + "=" + workoutId, null);
                            }).start();
                            alertDialog.cancel();
                        }
                );

                alertDialog.setCanceledOnTouchOutside(true);
                alertDialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, UltimateFitProvider.WorkoutExercises.fromWorkout(workoutId), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        workoutExerciseAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        workoutExerciseAdapter.swapCursor(null);
    }
}
