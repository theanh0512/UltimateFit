package ultimate.fit.ultimatefit.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentValues cv = new ContentValues();
                cv.put(PlanColumns.NAME, editNameText.getText().toString());
                cv.put(PlanColumns.GOAL, editGoalText.getText().toString());
                cv.put(PlanColumns.NUM_OF_WEEK, Integer.parseInt(editNumOfWeekText.getText().toString()));
                cv.put(PlanColumns.DAY_PER_WEEK, Integer.parseInt(editDayPerWeekText.getText().toString()));
                context.getContentResolver().insert(UltimateFitProvider.Plans.CONTENT_URI, cv);
            }
        }).start();
        Toast.makeText(this.getActivity(), R.string.plan_add_message, Toast.LENGTH_SHORT).show();
        Intent backToMain = new Intent(this.getActivity(), MainActivity.class);
        backToMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(backToMain);
    }
}
