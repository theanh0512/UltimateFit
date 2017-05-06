package ultimate.fit.ultimatefit.activity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

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

    public static final String ANONYMOUS = "anonymous";
    private static final int RC_SIGN_IN = 123;
    public static PagerAdapter adapter;
    public static DatabaseReference mPlanDatabaseReference;
    public static String mUsername;
    ActionBarDrawerToggle toggle;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    TextView textViewWelcome;
    ImageView imageViewAccount;
    FragmentActivity activity;
    Menu navMenu;
    //Firebase stuffs
    private FirebaseDatabase mFirebaseDatabase;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Ultimate Fit");

        mUsername = ANONYMOUS;
        //Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        //Reference the message portion of the database
        mPlanDatabaseReference = mFirebaseDatabase.getReference().child("plans");
        activity = this;

        setupNavigationDrawer();

        setupTabLayout();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //user is signed in
                    onSignedInInitialize(user.getDisplayName());
                } else {
                    //user is signed out
                    onSignedOutCleanup();
                    //setIsSmartLockEnabled to false will disable the ability to auto sign in user on subsequent attempts
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
                                            new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        //Init SharedPreferenceLoader and set currentAppliedPlanId
        PlanAdapter.currentAppliedPlanID = SharedPreferenceHelper.getInstance(getApplicationContext()).getInt(SharedPreferenceHelper.Key.CURRENT_APPLIED_PLANID_INT);
        GetDataTask getDataTaskCategory = new GetDataTask(this, Config.CATEGORY_URL);
        getDataTaskCategory.execute();
        GetDataTask getDataTaskExercise = new GetDataTask(this, Config.EXERCISE_URL, 1);
        getDataTaskExercise.execute();
    }

    private void setupTabLayout() {
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
    }

    private void setupNavigationDrawer() {
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

        textViewWelcome = (TextView) navigationView
                .getHeaderView(0).findViewById(R.id.navigation_drawer_account_information_display_name);
        imageViewAccount = (ImageView) navigationView
                .getHeaderView(0).findViewById(R.id.navigation_drawer_user_account_picture_profile);
        navMenu = navigationView.getMenu();
        textViewWelcome.setText(R.string.remind_login);
        imageViewAccount.setVisibility(View.GONE);
        navigationView.setNavigationItemSelectedListener
                (
                        new NavigationView.OnNavigationItemSelectedListener() {
                            @Override
                            public boolean onNavigationItemSelected(final MenuItem item) {
                                drawer.closeDrawer(GravityCompat.START);

                                switch (item.getItemId()) {
                                    case R.id.navigation_view_item_login:
                                        item.setChecked(true);
                                        break;

                                    case R.id.navigation_view_item_logout:
                                        item.setChecked(true);
                                        AuthUI.getInstance().signOut(activity);
                                        break;
                                }

                                return true;
                            }
                        }
                );
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

    @Override
    protected void onResume() {
        super.onResume();
        //Attach auth listener
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private void onSignedOutCleanup() {
        mUsername = ANONYMOUS;
        detachDatabaseReadListener();
        textViewWelcome.setText(R.string.remind_login);
        imageViewAccount.setVisibility(View.GONE);
        navMenu.findItem(R.id.navigation_view_item_login).setVisible(true);
        navMenu.findItem(R.id.navigation_view_item_logout).setVisible(false);
    }

    private void onSignedInInitialize(String userName) {
        mUsername = userName;
        attachDatabaseReadListener();
        textViewWelcome.setText(String.format("%s %s", getString(R.string.welcome), userName));
        imageViewAccount.setVisibility(View.VISIBLE);
        navMenu.findItem(R.id.navigation_view_item_login).setVisible(false);
        navMenu.findItem(R.id.navigation_view_item_logout).setVisible(true);
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
        //Remove auth listener
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
        detachDatabaseReadListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
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
