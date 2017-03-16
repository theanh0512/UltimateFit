package ultimate.fit.ultimatefit.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;
import ultimate.fit.ultimatefit.R;
import ultimate.fit.ultimatefit.data.UltimateFitDatabase;
import ultimate.fit.ultimatefit.data.UltimateFitProvider;
import ultimate.fit.ultimatefit.data.WorkoutColumns;
import ultimate.fit.ultimatefit.data.generated.values.WorkoutsValuesBuilder;

public class WorkoutActivity extends AppCompatActivity {
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
    int workoutId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        ButterKnife.bind(this);
        setSupportActionBar(toolbarWorkout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        int dayNumber = bundle.getInt("dayNumber");
        String bodyPart = bundle.getString("bodyPart");
        workoutId = bundle.getInt("workoutId");
        textViewDayNumber.setText(String.valueOf(dayNumber));
        editTextBodyPart.setText(bodyPart);
    }

    @OnClick(R.id.fab_add_exercise)
    public void onClickAddExercise() {
        Intent addExerciseIntent = new Intent(this, CategoryActivity.class);
        startActivity(addExerciseIntent);
    }

    @OnEditorAction(R.id.editTextBodyPart)
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                actionId == EditorInfo.IME_ACTION_DONE ||
                event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            if (!event.isShiftPressed()) {
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

}
