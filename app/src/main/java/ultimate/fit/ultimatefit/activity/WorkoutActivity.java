package ultimate.fit.ultimatefit.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ultimate.fit.ultimatefit.R;
import ultimate.fit.ultimatefit.activity.CategoryActivity;

public class WorkoutActivity extends AppCompatActivity {
    @BindView(R.id.fab_add_exercise)
    FloatingActionButton fabAddExercise;
    @BindView(R.id.recyclerview_exercise_list)
    RecyclerView recyclerViewExerciseList;
    @BindView(R.id.toolbar_workout)
    Toolbar toolbarWorkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        ButterKnife.bind(this);
        setSupportActionBar(toolbarWorkout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @OnClick(R.id.fab_add_exercise)
    public void onClickAddExercise() {
        Intent addExerciseIntent = new Intent(this, CategoryActivity.class);
        startActivity(addExerciseIntent);
    }

}
