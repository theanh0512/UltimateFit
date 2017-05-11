package ultimate.fit.ultimatefit.activity;

import android.app.ActivityOptions;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
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
import android.util.Log;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ultimate.fit.ultimatefit.R;
import ultimate.fit.ultimatefit.adapter.PagerAdapter;
import ultimate.fit.ultimatefit.adapter.PlanAdapter;
import ultimate.fit.ultimatefit.data.ExerciseColumns;
import ultimate.fit.ultimatefit.data.PlanColumns;
import ultimate.fit.ultimatefit.data.UltimateFitProvider;
import ultimate.fit.ultimatefit.data.generated.values.SetsValuesBuilder;
import ultimate.fit.ultimatefit.data.generated.values.Workout_exercisesValuesBuilder;
import ultimate.fit.ultimatefit.data.generated.values.WorkoutsValuesBuilder;
import ultimate.fit.ultimatefit.fragment.TabPlanFragment;
import ultimate.fit.ultimatefit.fragment.TabWorkoutFragment;
import ultimate.fit.ultimatefit.model.Plan;
import ultimate.fit.ultimatefit.model.Set;
import ultimate.fit.ultimatefit.model.Workout;
import ultimate.fit.ultimatefit.model.WorkoutExercise;
import ultimate.fit.ultimatefit.utils.Config;
import ultimate.fit.ultimatefit.utils.GetDataTask;
import ultimate.fit.ultimatefit.utils.SharedPreferenceHelper;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TabPlanFragment.OnFragmentInteractionListener,
        TabWorkoutFragment.OnFragmentInteractionListener, TabPlanFragment.ItemsListClickHandler, TabWorkoutFragment.ItemsListClickHandler {

    public static final String ANONYMOUS = "anonymous";
    private static final String TAG = "MainActivity";
    private static final int RC_SIGN_IN = 123;
    public static PagerAdapter adapter;
    public static DatabaseReference mPlanDatabaseReference;
    public static String userName;
    public static String userEmail;
    public static List<Plan> uploadedPlans;
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
        toolbar.setNavigationContentDescription(R.string.cd_toggle_navigation);

        userName = ANONYMOUS;
        userEmail = ANONYMOUS;
        //Initialize Firebase components
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();

        //Reference the message portion of the database
        mPlanDatabaseReference = mFirebaseDatabase.getReference().child("plans");
        activity = this;
        uploadedPlans = new ArrayList<>();

        setupNavigationDrawer();

        setupTabLayout();

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //user is signed in
                    onSignedInInitialize(user.getDisplayName(), user.getEmail());
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
                    final Plan uploadedPlan = dataSnapshot.getValue(Plan.class);
                    uploadedPlans.add(uploadedPlan);

                    //apply downloaded plans
                    //ToDo: need to ensure no duplicated plan downloaded
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String uploadedPlanGoal = uploadedPlan.getPlanGoal();
                            int uploadedPlanNumOfWeek = uploadedPlan.getNumOfWeek();
                            int uploadedPlanDayPerWeek = uploadedPlan.getDayPerWeek();
                            String uploadedPlanName = uploadedPlan.getPlanName();
                            String creatorEmail = uploadedPlan.getCreatorEmail();
                            String planUuid = uploadedPlan.getPlanUuid();
                            Cursor planCursor = activity.getContentResolver().query(UltimateFitProvider.Plans.CONTENT_URI, null,
                                    PlanColumns.NAME + " = '" + uploadedPlanName + "' AND " +
                                            PlanColumns.GOAL + " = '" + uploadedPlanGoal + "' AND " +
                                            PlanColumns.DAY_PER_WEEK + " = " + uploadedPlanDayPerWeek + " AND " +
                                            PlanColumns.NUM_OF_WEEK + " = " + uploadedPlanNumOfWeek, null, null);
                            if (!planCursor.moveToFirst()) {
                                ContentValues planContentValues = new ContentValues();
                                planContentValues.put(PlanColumns.NAME, uploadedPlanName);
                                planContentValues.put(PlanColumns.GOAL, uploadedPlanGoal);
                                planContentValues.put(PlanColumns.NUM_OF_WEEK, uploadedPlanNumOfWeek);
                                planContentValues.put(PlanColumns.DAY_PER_WEEK, uploadedPlanDayPerWeek);
                                planContentValues.put(PlanColumns.CREATOR, creatorEmail);
                                planContentValues.put(PlanColumns.PLAN_UUID, planUuid);
                                Uri uri = activity.getContentResolver().insert(UltimateFitProvider.Plans.CONTENT_URI, planContentValues);
                                List<Workout> workouts = uploadedPlan.getWorkouts();
                                long planId = ContentUris.parseId(uri);

                                //ensure creator set up the workout already
                                if(workouts != null && workouts.size() > 0) {
                                    //insert workout
                                    ContentValues[] workoutContentValues = new ContentValues[workouts.size()];
                                    for (int i = 0; i < workoutContentValues.length; i++) {
                                        workoutContentValues[i] = new WorkoutsValuesBuilder()
                                                .dayNumber(workouts.get(i).getDayNumber())
                                                .planId(planId)
                                                .bodyPart(workouts.get(i).getWorkoutBodyPart()).values();
                                        Uri workoutUri = activity.getContentResolver().insert(UltimateFitProvider.Workouts.CONTENT_URI, workoutContentValues[i]);
                                        long workoutId = ContentUris.parseId(workoutUri);
                                        List<WorkoutExercise> workoutExercises = workouts.get(i).getWorkoutExercises();

                                        //ensure creator set up the workoutExercise already
                                        if(workoutExercises != null && workoutExercises.size() > 0) {
                                            //insert workoutExercise
                                            ContentValues[] workoutExerciseContentValues = new ContentValues[workoutExercises.size()];
                                            for (int j = 0; j < workoutExerciseContentValues.length; j++) {
                                                WorkoutExercise workoutExercise = workoutExercises.get(j);
                                                List<Set> sets = workoutExercise.getSets();
                                                String joinedExerciseIds;
                                                String[] exerciseIdArray = {};
                                                //if sets are already generated
                                                if (sets != null && sets.size() > 0) {
                                                    int noOfSet = workoutExercise.getNoOfSets();
                                                    int noOfExercises = sets.size()/noOfSet;
                                                    exerciseIdArray = new String[noOfExercises];
                                                    for (int k = 0; k < noOfExercises; k++) {
                                                        String exerciseName = sets.get(k).getExerciseName();
                                                        Cursor exerciseCursor = activity.getContentResolver().query(UltimateFitProvider.Exercises.CONTENT_URI, null,
                                                                ExerciseColumns.EXERCISE_NAME + " = '" + exerciseName + "'", null, null);
                                                        exerciseCursor.moveToFirst();
                                                        long exerciseId = exerciseCursor.getLong(exerciseCursor.getColumnIndex(ExerciseColumns.ID));
                                                        exerciseIdArray[k] = String.valueOf(exerciseId);
                                                        exerciseCursor.close();
                                                    }
                                                    joinedExerciseIds = strJoin(exerciseIdArray, ",");
                                                } else {//set is not generated
                                                    Cursor exerciseCursor = activity.getContentResolver().query(UltimateFitProvider.Exercises.CONTENT_URI, null,
                                                            ExerciseColumns.EXERCISE_NAME + " = '" + workoutExercise.getFirstExerciseName() + "'", null, null);
                                                    exerciseCursor.moveToFirst();
                                                    long exerciseId = exerciseCursor.getLong(exerciseCursor.getColumnIndex(ExerciseColumns.ID));
                                                    joinedExerciseIds = String.valueOf(exerciseId);
                                                    exerciseCursor.close();
                                                }
                                                workoutExerciseContentValues[j] = new Workout_exercisesValuesBuilder()
                                                        .firstExerciseImage(workoutExercise.getFirstExerciseImage())
                                                        .firstExerciseName(workoutExercise.getFirstExerciseName())
                                                        .rep(workoutExercise.getRep())
                                                        .set(workoutExercise.getNoOfSets())
                                                        .workoutId(workoutId)
                                                        .exerciseIds(joinedExerciseIds).values();
                                                Uri workoutExerciseUri = activity.getContentResolver().insert(UltimateFitProvider.WorkoutExercises.CONTENT_URI, workoutExerciseContentValues[j]);
                                                long workoutExerciseId = ContentUris.parseId(workoutExerciseUri);

                                                //insert sets if they are already generated
                                                if (sets != null && sets.size() > 0) {
                                                    ContentValues[] setContentValues = new ContentValues[sets.size()];
                                                    for (int k = 0; k < sets.size(); k++) {
                                                        Set set = sets.get(k);
                                                        String exerciseName = set.getExerciseName();
                                                        setContentValues[k] = new SetsValuesBuilder()
                                                                .exerciseNumber(set.getExerciseNumber())
                                                                .weight(0)
                                                                .exerciseId(Long.parseLong(exerciseIdArray[k%exerciseIdArray.length]))
                                                                .rep(set.getNoOfRep())
                                                                .setName(exerciseName)
                                                                .workoutExerciseId(workoutExerciseId)
                                                                .setNumber(set.getSetNumber()).values();
                                                        activity.getContentResolver().insert(UltimateFitProvider.Sets.CONTENT_URI, setContentValues[k]);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                Log.d(TAG, "The plan is already saved" + uploadedPlan.getPlanName());
                            }
                        }
                    });
                    thread.start();
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

    private String strJoin(String[] stringArray, String separator) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0, il = stringArray.length; i < il; i++) {
            if (i > 0)
                stringBuilder.append(separator);
            stringBuilder.append(stringArray[i]);
        }
        return stringBuilder.toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Attach auth listener
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private void onSignedOutCleanup() {
        userEmail = ANONYMOUS;
        userName = ANONYMOUS;
        detachDatabaseReadListener();
        textViewWelcome.setText(R.string.remind_login);
        imageViewAccount.setVisibility(View.GONE);
        navMenu.findItem(R.id.navigation_view_item_login).setVisible(true);
        navMenu.findItem(R.id.navigation_view_item_logout).setVisible(false);
        uploadedPlans.clear();
    }

    private void onSignedInInitialize(String userName, String userEmail) {
        MainActivity.userEmail = userEmail;
        MainActivity.userName = userName;
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
                Toast.makeText(this, R.string.toast_signed_in, Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, R.string.toast_signin_cancel, Toast.LENGTH_SHORT).show();
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
        if (Build.VERSION.SDK_INT >= 21) {
            // Call some material design APIs here
            startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
        } else {
            startActivity(intent);
            // Implement this feature without material design
        }

    }
}
