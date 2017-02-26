package ultimate.fit.ultimatefit.fragment;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ultimate.fit.ultimatefit.R;
import ultimate.fit.ultimatefit.activity.MainActivity;
import ultimate.fit.ultimatefit.data.PlanColumns;
import ultimate.fit.ultimatefit.data.UltimateFitProvider;
import ultimate.fit.ultimatefit.data.generated.values.WorkoutsValuesBuilder;

/**
 * Created by User on 12/10/2016.
 */

public class AddPlanFragment extends Fragment {

    @BindView(R.id.editNameText)
    EditText editNameText;
    @BindView(R.id.editGoalText)
    EditText editGoalText;
    @BindView(R.id.editNumOfWeekText)
    EditText editNumOfWeekText;
    @BindView(R.id.editDayPerWeekText)
    EditText editDayPerWeekText;
    @BindView(R.id.buttonSavePlan)
    Button buttonSavePlan;


    public AddPlanFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_add_plan, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @OnClick(R.id.buttonSavePlan)
    public void onClickSavePlan() {
        final Context context = getActivity().getApplicationContext();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ContentValues planContentValues = new ContentValues();
                planContentValues.put(PlanColumns.NAME, editNameText.getText().toString());
                planContentValues.put(PlanColumns.GOAL, editGoalText.getText().toString());
                int numOfWeek = Integer.parseInt(editNumOfWeekText.getText().toString());
                int dayPerWeek = Integer.parseInt(editDayPerWeekText.getText().toString());
                planContentValues.put(PlanColumns.NUM_OF_WEEK, numOfWeek);
                planContentValues.put(PlanColumns.DAY_PER_WEEK, dayPerWeek);
                Uri uri = context.getContentResolver().insert(UltimateFitProvider.Plans.CONTENT_URI, planContentValues);

                long planId = ContentUris.parseId(uri);
                ContentValues[] workoutContentValues = new ContentValues[numOfWeek * dayPerWeek];
                for (int i = 0; i < workoutContentValues.length; i++) {
                    workoutContentValues[i] = new WorkoutsValuesBuilder().dayNumber(i + 1).planId(planId).values();
                }
                context.getContentResolver().bulkInsert(UltimateFitProvider.Workouts.CONTENT_URI, workoutContentValues);
            }
        });
        thread.start();
        Toast.makeText(this.getActivity(), R.string.plan_add_message, Toast.LENGTH_SHORT).show();
        Intent backToMain = new Intent(this.getActivity(), MainActivity.class);
        backToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(backToMain);
    }
}
