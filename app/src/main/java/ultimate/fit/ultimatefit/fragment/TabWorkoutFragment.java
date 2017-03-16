package ultimate.fit.ultimatefit.fragment;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.joda.time.DateTime;
import org.joda.time.Days;

import butterknife.BindView;
import butterknife.ButterKnife;
import ultimate.fit.ultimatefit.R;
import ultimate.fit.ultimatefit.adapter.PlanAdapter;
import ultimate.fit.ultimatefit.adapter.WorkoutAdapter;
import ultimate.fit.ultimatefit.data.PlanColumns;
import ultimate.fit.ultimatefit.data.UltimateFitProvider;

public class TabWorkoutFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = TabWorkoutFragment.class.getSimpleName();
    private static final int WORKOUT_LOADER = 2000;
    public WorkoutAdapter workoutAdapter;
    ItemsListClickHandler handler;
    @BindView(R.id.fab_add_workout)
    FloatingActionButton fabAddWorkout;
    @BindView(R.id.recyclerview_workout)
    RecyclerView recyclerViewWorkout;
    private OnFragmentInteractionListener mListener;

    public TabWorkoutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_tab_workout, container, false);
        ButterKnife.bind(this, rootView);

        workoutAdapter = new WorkoutAdapter(getContext(), new WorkoutAdapter.WorkoutAdapterOnClickHandler() {
            @Override
            public void onClick(int workoutId,int dayNumber,String bodyPart) {
                handler.onHandleItemClickFromTabWorkout(workoutId,dayNumber,bodyPart);
            }
        });
        recyclerViewWorkout.setAdapter(workoutAdapter);
        recyclerViewWorkout.setHasFixedSize(true);
        recyclerViewWorkout.setLayoutManager(new LinearLayoutManager(getContext()));
        getLoaderManager().initLoader(WORKOUT_LOADER, null, this);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        try {
            handler = (TabWorkoutFragment.ItemsListClickHandler) getActivity();

        } catch (ClassCastException e) {
            Log.e(LOG_TAG, "The activity does not implement the interface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), UltimateFitProvider.Workouts.fromPlan(PlanAdapter.currentAppliedPlanID), null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        workoutAdapter.swapCursor(data);
        if(data.moveToPosition(0)) {
            DateTime today = new DateTime();
            DateTime relativeDate = new DateTime(data.getLong(data.getColumnIndex(PlanColumns.APPLIED_DATE)));
            int positionToScroll = Days.daysBetween(relativeDate, today).getDays();
            recyclerViewWorkout.getLayoutManager().scrollToPosition(positionToScroll);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        workoutAdapter.swapCursor(null);
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public interface ItemsListClickHandler {
        public void onHandleItemClickFromTabWorkout(int workoutId, int dayNumber,String bodyPart);
    }
}
