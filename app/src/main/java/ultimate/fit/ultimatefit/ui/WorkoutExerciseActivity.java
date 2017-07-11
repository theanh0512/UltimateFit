package ultimate.fit.ultimatefit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import ultimate.fit.ultimatefit.R;
import ultimate.fit.ultimatefit.ui.fragment.WorkoutExerciseActivityFragment;

public class WorkoutExerciseActivity extends AppCompatActivity {
    WorkoutExerciseActivityFragment workoutExerciseActivityFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_exercise);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_workoutexercise);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        workoutExerciseActivityFragment = new WorkoutExerciseActivityFragment();
        workoutExerciseActivityFragment.setArguments(bundle);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.container, workoutExerciseActivityFragment);
            transaction.commit();
        }
    }

}
