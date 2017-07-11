package ultimate.fit.ultimatefit.ui.fragment;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import ultimate.fit.ultimatefit.R;
import ultimate.fit.ultimatefit.data.ExerciseColumns;
import ultimate.fit.ultimatefit.data.UltimateFitProvider;


public class ExerciseFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    long exerciseId = 1;

    @BindView(R.id.text_view_exercise_name)
    TextView textViewExerciseName;
    @BindView(R.id.text_view_instruction)
    TextView textViewInstruction;
    @BindView(R.id.image_view_exercise_1)
    ImageView imageViewExercise1;
    @BindView(R.id.image_view_exercise_2)
    ImageView imageViewExercise2;
    @BindView(R.id.edit_text_one_rep_max)
    EditText editTextOneRepMax;
    @BindView(R.id.text_view_unit)
    TextView textViewUnit;
    String exerciseName;
    String instruction;
    String exerciseImage1;
    String exerciseImage2;
    double oneRepMax;

    public ExerciseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment ExerciseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExerciseFragment newInstance(long param1) {
        ExerciseFragment fragment = new ExerciseFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            exerciseId = getArguments().getLong(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_exercise, container, false);
        ButterKnife.bind(this, rootView);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Cursor exerciseCursor = getActivity().getContentResolver().query(UltimateFitProvider.Exercises.withId(exerciseId), null, null, null, null);
                    exerciseCursor.moveToFirst();
                    exerciseName = exerciseCursor.getString(exerciseCursor.getColumnIndex(ExerciseColumns.EXERCISE_NAME));
                    exerciseImage1 = exerciseCursor.getString(exerciseCursor.getColumnIndex(ExerciseColumns.IMAGE_PATH));
                    exerciseImage2 = exerciseCursor.getString(exerciseCursor.getColumnIndex(ExerciseColumns.IMAGE_2_PATH));
                    instruction = exerciseCursor.getString(exerciseCursor.getColumnIndex(ExerciseColumns.DESCRIPTION));
                    oneRepMax = exerciseCursor.getDouble(exerciseCursor.getColumnIndex(ExerciseColumns.ONE_REP_MAX));

                    exerciseCursor.close();
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
        CharSequence http = "http://";
        final String imagePath1 = exerciseImage1.contains(http) ? exerciseImage1.replace("http://", "https://") : exerciseImage1;
        final String imagePath2 = exerciseImage2.contains(http) ? exerciseImage2.replace("http://", "https://") : exerciseImage2;

        textViewExerciseName.setText(exerciseName);
        textViewInstruction.setText(instruction);
        textViewInstruction.setMovementMethod(new ScrollingMovementMethod());
        editTextOneRepMax.setText(String.valueOf(oneRepMax));
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean isManuallyInput = preferences.getBoolean(getString(R.string.pref_orm_manual_input_key),true);
        editTextOneRepMax.setEnabled(isManuallyInput);
        String unit = preferences.getString(getString(R.string.pref_unit_key), getString(R.string.pref_unit_kg));
        textViewUnit.setText(unit);
        try {
            Picasso.with(getActivity()).load(imagePath1).placeholder(R.drawable.ic_place_holder)
                    .error(R.drawable.ic_error_fallback).into(imageViewExercise1, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(getActivity())
                            .load(imagePath1)
                            .into(imageViewExercise1, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
//                                        Log.v("Picasso","Could not fetch image");
                                }
                            });
                }
            });
        } catch (IndexOutOfBoundsException e) {
        }
        try {
            Picasso.with(getActivity()).load(imagePath2).placeholder(R.drawable.ic_place_holder)
                    .error(R.drawable.ic_error_fallback).into(imageViewExercise2, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(getActivity())
                            .load(imagePath2)
                            .into(imageViewExercise2, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {
//                                        Log.v("Picasso","Could not fetch image");
                                }
                            });
                }
            });
        } catch (IndexOutOfBoundsException e) {
        }

        return rootView;
    }
}
