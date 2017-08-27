package ultimate.fit.ultimatefit.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ultimate.fit.ultimatefit.R;
import ultimate.fit.ultimatefit.ui.AddPlanActivity;
import ultimate.fit.ultimatefit.adapter.PlanAdapter;
import ultimate.fit.ultimatefit.data.UltimateFitProvider2;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TabPlanFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TabPlanFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TabPlanFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = TabPlanFragment.class.getSimpleName();
    public static final String PLANS_LOADED = "fit.ultimate.plans_loaded";
    private static final int PLAN_LOADER = 1000;
    private static final String TAG = "TabPlanFragment";
    ItemsListClickHandler handler;
    @BindView(R.id.fab_add_plan)
    FloatingActionButton fabAddPlan;
    @BindView(R.id.recyclerview_plan)
    RecyclerView recyclerViewPlan;
    private PlanAdapter planAdapter;
    private OnFragmentInteractionListener mListener;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(PLANS_LOADED)) {
                Log.d(TAG, "onReceive: data updated");
                planAdapter.notifyDataSetChanged();
                //ToDo: Notify plan tab
            }
        }
    };

    public TabPlanFragment() {
        // Required empty public constructor
    }

    public static TabPlanFragment newInstance(String param1, String param2) {
        TabPlanFragment fragment = new TabPlanFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(PLANS_LOADED);
        getActivity().registerReceiver(broadcastReceiver, filter);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_tab_plan, container, false);
        ButterKnife.bind(this, rootView);

        planAdapter = new PlanAdapter(getContext(), new PlanAdapter.PlanAdapterOnClickHandler() {
            @Override
            public void onClick(int planId) {
                Log.i(LOG_TAG, "plan ID: " + planId);
                handler.onHandleItemClickFromTabPlan(planId);
            }
        });
        recyclerViewPlan.setAdapter(planAdapter);
        recyclerViewPlan.setHasFixedSize(true);
        recyclerViewPlan.setLayoutManager(new LinearLayoutManager(getContext()));
        getLoaderManager().initLoader(PLAN_LOADER, null, this);

        return rootView;
    }

    @OnClick(R.id.fab_add_plan)
    public void onClickAddPlan() {
        Intent addPlanIntent = new Intent(this.getActivity(), AddPlanActivity.class);
        startActivity(addPlanIntent);
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
            handler = (ItemsListClickHandler) getActivity();

        } catch (ClassCastException e) {
            Log.e(TabPlanFragment.class.getSimpleName(), "The activity does not implement the interface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), UltimateFitProvider2.Plans.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        planAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        planAdapter.swapCursor(null);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public interface ItemsListClickHandler {
        public void onHandleItemClickFromTabPlan(int planId);
    }

}
