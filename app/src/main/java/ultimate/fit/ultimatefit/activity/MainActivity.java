package ultimate.fit.ultimatefit.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import butterknife.BindView;
import butterknife.ButterKnife;
import ultimate.fit.ultimatefit.R;
import ultimate.fit.ultimatefit.adapter.PagerAdapter;
import ultimate.fit.ultimatefit.adapter.PlanAdapter;
import ultimate.fit.ultimatefit.fragment.TabPlanFragment;
import ultimate.fit.ultimatefit.fragment.TabWorkoutFragment;
import ultimate.fit.ultimatefit.model.Plan;
import ultimate.fit.ultimatefit.utils.Config;
import ultimate.fit.ultimatefit.utils.GetDataTask;
import ultimate.fit.ultimatefit.utils.SharedPreferenceHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TabPlanFragment.OnFragmentInteractionListener,
        TabWorkoutFragment.OnFragmentInteractionListener, TabPlanFragment.ItemsListClickHandler, TabWorkoutFragment.ItemsListClickHandler {

    public static PagerAdapter adapter;
    public static DatabaseReference mPlanDatabaseReference;
    ActionBarDrawerToggle toggle;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    //Firebase stuffs
    private FirebaseDatabase mFirebaseDatabase;
    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Ultimate Fit");

        //Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();

        //Reference the message portion of the database
        mPlanDatabaseReference = mFirebaseDatabase.getReference().child("plans");

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toggle.setDrawerIndicatorEnabled(false);
        toggle.setHomeAsUpIndicator(R.drawable.view_sequential);
        toggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        tabLayout.addTab(tabLayout.newTab().setText(R.string.title_tab_workout));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.title_tab_plan)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(), this);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //Init SharedPreferenceLoader and set currentAppliedPlanId
        PlanAdapter.currentAppliedPlanID = SharedPreferenceHelper.getInstance(getApplicationContext()).getInt(SharedPreferenceHelper.Key.CURRENT_APPLIED_PLANID_INT);
        GetDataTask getDataTaskCategory = new GetDataTask(this, Config.CATEGORY_URL);
        getDataTaskCategory.execute();
        GetDataTask getDataTaskExercise = new GetDataTask(this, Config.EXERCISE_URL, 1);
        getDataTaskExercise.execute();
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    //This message will get called whenever a new message is inserted into a message list
                    //It is also triggered for every child message in the list when the listener is first attached

                    //Passing the class as parameter, the code will deserialize the message from the database into FriendlyMessage object
                    Plan uploadedPlan = dataSnapshot.getValue(Plan.class);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    //This gets called when the contents of an existing message gets changed
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    //This gets called when one of our messages changed its position in the list
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //This gets called when there is error happened.
                    //Typically, it means that you don't have permission to read the data
                }
            };
            mPlanDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mPlanDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachDatabaseReadListener();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onHandleItemClickFromTabPlan(int planId) {

    }

    @Override
    public void onHandleItemClickFromTabWorkout(int workoutId, int dayNumber, String bodyPart) {
        Intent intent = new Intent(this, WorkoutActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("dayNumber", dayNumber);
        bundle.putString("bodyPart", bodyPart);
        bundle.putInt("workoutId", workoutId);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
