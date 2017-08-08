package ultimate.fit.ultimatefit.ui;

import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
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

import com.crashlytics.android.Crashlytics;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import io.fabric.sdk.android.Fabric;
import ultimate.fit.ultimatefit.R;
import ultimate.fit.ultimatefit.adapter.PagerAdapter;
import ultimate.fit.ultimatefit.adapter.PlanAdapter;
import ultimate.fit.ultimatefit.data.ExerciseColumns;
import ultimate.fit.ultimatefit.data.PlanColumns;
import ultimate.fit.ultimatefit.data.SetColumns;
import ultimate.fit.ultimatefit.data.UltimateFitProvider;
import ultimate.fit.ultimatefit.data.WorkoutColumns;
import ultimate.fit.ultimatefit.data.WorkoutExerciseColumns;
import ultimate.fit.ultimatefit.data.generated.values.SetsValuesBuilder;
import ultimate.fit.ultimatefit.data.generated.values.Workout_exercisesValuesBuilder;
import ultimate.fit.ultimatefit.data.generated.values.WorkoutsValuesBuilder;
import ultimate.fit.ultimatefit.model.Plan;
import ultimate.fit.ultimatefit.model.Set;
import ultimate.fit.ultimatefit.model.Workout;
import ultimate.fit.ultimatefit.model.WorkoutExercise;
import ultimate.fit.ultimatefit.ui.fragment.TabPlanFragment;
import ultimate.fit.ultimatefit.ui.fragment.TabWorkoutFragment;
import ultimate.fit.ultimatefit.utils.Config;
import ultimate.fit.ultimatefit.utils.GetDataTask;
import ultimate.fit.ultimatefit.utils.SharedPreferenceHelper;
import ultimate.fit.ultimatefit.utils.Utils.InternetVsLocalSize;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TabPlanFragment.OnFragmentInteractionListener,
        TabWorkoutFragment.OnFragmentInteractionListener, TabPlanFragment.ItemsListClickHandler, TabWorkoutFragment.ItemsListClickHandler, HasSupportFragmentInjector {

    public static final String ANONYMOUS = "anonymous";
    public static final String DATA_DOWNLOADED = "fit.ultimate.data_downloaded";
    private static final String TAG = "MainActivity";
    private static final int UPDATE_INTERVAL = 1000 * 5 * 60;
    private static final int RC_SIGN_IN = 123;
    public static PagerAdapter adapter;
    public static DatabaseReference mPlanDatabaseReference;
    public static String userName;
    public static String userEmail;
    public static List<Plan> uploadedPlans;
    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;
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
    boolean isDataUpdated = false;
    boolean isSyncAllowed = false;
    //Firebase stuffs
    private FirebaseDatabase mFirebaseDatabase;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(DATA_DOWNLOADED)) {
                isDataUpdated = true;
                Log.d(TAG, "onReceive: data updated");
                DateTime dateTime = new DateTime(DateTimeZone.UTC);
                long currentTime = dateTime.getMillis();
                long lastUpdatedTime = SharedPreferenceHelper.getInstance(MainActivity.this).getLong(SharedPreferenceHelper.Key.LAST_UPDATED_PLAN_TIME);
                long currentInterval = lastUpdatedTime - currentTime;
                if (!userName.equals(ANONYMOUS) && currentInterval >= UPDATE_INTERVAL)
                    attachDatabaseReadListener();
                //ToDo: Notify plan tab
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Ultimate Fit");
        toolbar.setNavigationContentDescription(R.string.cd_toggle_navigation);

        IntentFilter filter = new IntentFilter();
        filter.addAction(DATA_DOWNLOADED);
        registerReceiver(broadcastReceiver, filter);

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

        mAuthStateListener = firebaseAuth -> {
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
                                .setTheme(R.style.BrownTheme)
                                .build(),
                        RC_SIGN_IN);
            }
        };

        //Init SharedPreferenceLoader and set currentAppliedPlanId
        PlanAdapter.currentAppliedPlanID = SharedPreferenceHelper.getInstance(getApplicationContext()).getInt(SharedPreferenceHelper.Key.CURRENT_APPLIED_PLANID_INT);
        GetDataTask getDataTaskCategory = new GetDataTask(this, Config.CATEGORY_URL);
        getDataTaskCategory.execute();
        GetDataTask getDataTaskExercise = new GetDataTask(this, Config.EXERCISE_URL, 1);
        getDataTaskExercise.execute();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        isSyncAllowed = preferences.getBoolean(this.getString(R.string.pref_allow_sync_key), false);
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
        toggle.setToolbarNavigationClickListener(v -> {
            if (drawer.isDrawerVisible(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                drawer.openDrawer(GravityCompat.START);
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
                        item -> {
                            drawer.closeDrawer(GravityCompat.START);

                            switch (item.getItemId()) {
                                case R.id.navigation_view_item_login:
                                    item.setChecked(true);
                                    break;

                                case R.id.navigation_view_item_logout:
                                    item.setChecked(true);
                                    AuthUI.getInstance().signOut(activity);
                                    break;

                                case R.id.navigation_view_item_setting:
                                    item.setChecked(true);
                                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                                    break;

                                case R.id.navigation_view_item_sync:
                                    item.setChecked(true);
                                    attachDatabaseReadListener();
                                    Toast.makeText(this, R.string.toast_synced, Toast.LENGTH_SHORT).show();
                                    break;
                            }

                            return true;
                        }
                );
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.d(TAG, "attachDatabaseReadListener: data updating");
                    //This message will get called whenever a new message is inserted into a message list
                    //It is also triggered for every child message in the list when the listener is first attached

                    //Passing the class as parameter, the code will deserialize the message from the database into FriendlyMessage object
                    final Plan uploadedPlan = dataSnapshot.getValue(Plan.class);
                    uploadedPlans.add(uploadedPlan);

                    //apply downloaded plans
                    //ToDo: need to ensure no duplicated plan downloaded
                    Thread thread = new Thread(() -> {
                        String uploadedPlanGoal = uploadedPlan.getPlanGoal();
                        int uploadedPlanNumOfWeek = uploadedPlan.getNumOfWeek();
                        int uploadedPlanDayPerWeek = uploadedPlan.getDayPerWeek();
                        String uploadedPlanName = uploadedPlan.getPlanName();
                        String creatorEmail = uploadedPlan.getCreatorEmail();
                        String planUuid = uploadedPlan.getPlanUuid();
                        String[] args = {uploadedPlanName, uploadedPlanGoal, String.valueOf(uploadedPlanDayPerWeek), String.valueOf(uploadedPlanNumOfWeek)};
                        Cursor planCursor = activity.getContentResolver().query(UltimateFitProvider.Plans.CONTENT_URI, null,
                                PlanColumns.NAME + " = ? AND " + PlanColumns.GOAL + " = ? AND " + PlanColumns.DAY_PER_WEEK + " = ? AND " +
                                        PlanColumns.NUM_OF_WEEK + " = ?", args, null);
                        boolean isUpdating = false;
                        if (planCursor != null && planCursor.moveToFirst()) isUpdating = true;
                        ContentValues planContentValues = new ContentValues();
                        planContentValues.put(PlanColumns.NAME, uploadedPlanName);
                        planContentValues.put(PlanColumns.GOAL, uploadedPlanGoal);
                        planContentValues.put(PlanColumns.NUM_OF_WEEK, uploadedPlanNumOfWeek);
                        planContentValues.put(PlanColumns.DAY_PER_WEEK, uploadedPlanDayPerWeek);
                        planContentValues.put(PlanColumns.CREATOR, creatorEmail);
                        planContentValues.put(PlanColumns.PLAN_UUID, planUuid);
                        Uri uri;
                        long planId = 0;
                        if (!isUpdating) {
                            uri = activity.getContentResolver().insert(UltimateFitProvider.Plans.CONTENT_URI, planContentValues);
                            planId = ContentUris.parseId(uri);
                        } else {
                            if (isSyncAllowed) {
                                planId = planCursor.getLong(planCursor.getColumnIndex(PlanColumns.ID));
                                activity.getContentResolver().update(UltimateFitProvider.Plans.CONTENT_URI, planContentValues,
                                        PlanColumns.ID + " = " + planId, null);
                            }
                        }
                        List<Workout> workouts = uploadedPlan.getWorkouts();

                        //ensure creator set up the workout already
                        //workouts are generated with plan. They will never be null
                        if (workouts != null && workouts.size() > 0) {
                            //insert workout
                            ContentValues[] workoutContentValues = new ContentValues[workouts.size()];
                            for (int i = 0; i < workoutContentValues.length; i++) {
                                workoutContentValues[i] = new WorkoutsValuesBuilder()
                                        .dayNumber(workouts.get(i).getDayNumber())
                                        .planId(planId)
                                        .noteOfWorkout(workouts.get(i).getNote())
                                        .bodyPart(workouts.get(i).getWorkoutBodyPart()).values();
                                Uri workoutUri;
                                long workoutId = 0;
                                if (!isUpdating) {
                                    workoutUri = activity.getContentResolver().insert(UltimateFitProvider.Workouts.CONTENT_URI, workoutContentValues[i]);
                                    workoutId = ContentUris.parseId(workoutUri);
                                } else {
                                    if (isSyncAllowed) {
                                        String[] argsWorkout = {String.valueOf(planId), String.valueOf(workouts.get(i).getDayNumber())};
                                        Cursor workoutCursor = activity.getContentResolver().query(UltimateFitProvider.Workouts.CONTENT_URI, null,
                                                WorkoutColumns.PLAN_ID + " = ? AND " + WorkoutColumns.DAY_NUMBER + " = ?", argsWorkout, null);
                                        workoutCursor.moveToFirst();
                                        workoutId = workoutCursor.getLong(workoutCursor.getColumnIndex(WorkoutColumns.ID));
                                        activity.getContentResolver().update(UltimateFitProvider.Workouts.CONTENT_URI, workoutContentValues[i],
                                                WorkoutColumns.ID + " = " + workoutId, null);
                                        workoutCursor.close();
                                    }
                                }
                                List<WorkoutExercise> workoutExercises = workouts.get(i).getWorkoutExercises();

                                //Workout Exercise is not guaranteed to be generated.
                                //ensure creator set up the workoutExercise already
                                //Delete old workout exercises and sets
                                if (isUpdating && isSyncAllowed) {
                                    Cursor workoutExerciseCursor = activity.getContentResolver().query(UltimateFitProvider.WorkoutExercises.CONTENT_URI, null,
                                            WorkoutExerciseColumns.WORKOUT_ID + " = " + workoutId, null, null);
                                    if (workoutExerciseCursor != null && workoutExerciseCursor.moveToFirst()) {
                                        int toBeSavedWESize = workoutExercises.size();
                                        int currentWESize = workoutExerciseCursor.getCount();
                                        InternetVsLocalSize comparedResult =
                                                toBeSavedWESize > currentWESize ? InternetVsLocalSize.MORE :
                                                        toBeSavedWESize == currentWESize ? InternetVsLocalSize.EQUAL : InternetVsLocalSize.LESS;
                                        switch (comparedResult) {
                                            case MORE:
                                                updateWorkoutExercise(workoutId, workoutExercises, workoutExerciseCursor);
                                                //workout exercise is not in local database
                                                for (int j = currentWESize; j < toBeSavedWESize; j++) {
                                                    WorkoutExercise workoutExercise = workoutExercises.get(j);
                                                    insertWorkoutExercises(workoutId, j, workoutExercise);
                                                }
                                                break;

                                            case EQUAL:
                                                updateWorkoutExercise(workoutId, workoutExercises, workoutExerciseCursor);
                                                break;
                                            case LESS:
                                                updateWorkoutExercise(workoutId, workoutExercises, workoutExerciseCursor);
                                                break;
                                        }
                                    } else {
                                        if (workoutExercises != null) {
                                            for (int j = 0; j < workoutExercises.size(); j++) {
                                                WorkoutExercise workoutExercise = workoutExercises.get(j);
                                                insertWorkoutExercises(workoutId, j, workoutExercise);
                                            }
                                        }
                                        //workout exercise has not been created

                                    }
                                    if (workoutExerciseCursor != null)
                                        workoutExerciseCursor.close();
                                } else {
                                    //workout exercise has not been created due to 1st time installation
                                    if (workoutExercises != null) {
                                        for (int j = 0; j < workoutExercises.size(); j++) {
                                            WorkoutExercise workoutExercise = workoutExercises.get(j);
                                            insertWorkoutExercises(workoutId, j, workoutExercise);
                                        }
                                    }
                                }
                            }
                        }
                        planCursor.close();
                    });
                    thread.start();
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    DateTime dateTime = new DateTime(DateTimeZone.UTC);
                    long lastUpdated = dateTime.getMillis();
                    SharedPreferenceHelper.getInstance().put(SharedPreferenceHelper.Key.LAST_UPDATED_PLAN_TIME, lastUpdated);
                    Intent intent = new Intent(TabPlanFragment.PLANS_LOADED);
                    sendBroadcast(intent);
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

    private void insertWorkoutExercises(long workoutId, int j, WorkoutExercise workoutExercise) {
        String[] exerciseIdArray = {};
        List<Set> sets = workoutExercise.getSets();
        String joinedExerciseIds = "0";
        //if sets are already generated
        if (sets != null && sets.size() > 0) {
            int noOfSet = workoutExercise.getNoOfSets();
            int noOfExercises = sets.size() / noOfSet;
            exerciseIdArray = new String[noOfExercises];
            for (int k = 0; k < noOfExercises; k++) {
                String exerciseName = sets.get(k).getExerciseName();
                String[] argsExercise = {exerciseName};
                Cursor exerciseCursor = activity.getContentResolver().query(UltimateFitProvider.Exercises.CONTENT_URI, null,
                        ExerciseColumns.EXERCISE_NAME + " = ?", argsExercise, null);
                if (exerciseCursor != null && exerciseCursor.moveToFirst()) {
                    long exerciseId = exerciseCursor.getLong(exerciseCursor.getColumnIndex(ExerciseColumns.ID));
                    exerciseIdArray[k] = String.valueOf(exerciseId);
                    exerciseCursor.close();
                }
            }
            joinedExerciseIds = strJoin(exerciseIdArray, ",");
        } else {//set is not generated
            String[] argsExercise = {workoutExercise.getFirstExerciseName()};
            Cursor exerciseCursor = activity.getContentResolver().query(UltimateFitProvider.Exercises.CONTENT_URI, null,
                    ExerciseColumns.EXERCISE_NAME + " = ?", argsExercise, null);
            if (exerciseCursor != null && exerciseCursor.moveToFirst()) {
                long exerciseId = exerciseCursor.getLong(exerciseCursor.getColumnIndex(ExerciseColumns.ID));
                joinedExerciseIds = String.valueOf(exerciseId);
                exerciseIdArray = new String[1];
                exerciseIdArray[0] = String.valueOf(exerciseId);
                exerciseCursor.close();
            }

        }
        ContentValues workoutExerciseContentValues = new Workout_exercisesValuesBuilder()
                .firstExerciseImage(workoutExercise.getFirstExerciseImage())
                .firstExerciseName(workoutExercise.getFirstExerciseName())
                .rep(workoutExercise.getRep())
                .set(workoutExercise.getNoOfSets())
                .workoutId(workoutId)
                .workoutExerciseNumber(j)
                .exerciseIds(joinedExerciseIds).values();
        Uri workoutExerciseUri = activity.getContentResolver().insert(UltimateFitProvider.WorkoutExercises.CONTENT_URI,
                workoutExerciseContentValues);
        long workoutExerciseId = ContentUris.parseId(workoutExerciseUri);

        //insert sets if they are already generated
        if (sets != null && sets.size() > 0) {
            ContentValues[] setContentValues = new ContentValues[sets.size()];
            for (int k = 0; k < sets.size(); k++) {
                Set set = sets.get(k);
                String exerciseName = set.getExerciseName();
                setContentValues[k] = new SetsValuesBuilder()
                        .exerciseNumber(set.getExerciseNumber())
                        .exerciseId(Long.parseLong(exerciseIdArray[k % exerciseIdArray.length]))
                        .rep(set.getNoOfRep())
                        .weightRatio(set.getWeightRatio())
                        .setName(exerciseName)
                        .workoutExerciseId(workoutExerciseId)
                        .setPosition(k)
                        .setNumber(set.getSetNumber()).values();
                activity.getContentResolver().insert(UltimateFitProvider.Sets.CONTENT_URI, setContentValues[k]);
            }
        }
    }

    private void updateWorkoutExercise(long workoutId, List<WorkoutExercise> workoutExercises, Cursor workoutExerciseCursor) {
        while (workoutExerciseCursor.moveToNext()) {
            String[] exerciseIdArray = {};
            long workoutExerciseId = workoutExerciseCursor.getLong
                    (workoutExerciseCursor.getColumnIndex(WorkoutExerciseColumns.ID));
            int workoutExerciseNumber = workoutExerciseCursor.getInt
                    (workoutExerciseCursor.getColumnIndex(WorkoutExerciseColumns.WORKOUT_EXERCISE_NUMBER));
            Optional<WorkoutExercise> workoutExerciseOptional = workoutExercises.
                    stream().
                    filter(w -> w.getWorkoutExerciseNumber() == workoutExerciseNumber).
                    findFirst();

            if (workoutExerciseOptional.isPresent()) {
                WorkoutExercise workoutExercise = workoutExerciseOptional.get();
                Cursor setCursor = activity.getContentResolver().query(UltimateFitProvider.Sets.CONTENT_URI, null,
                        SetColumns.WORKOUT_EXERCISE_ID + " = " + workoutExerciseId, null, null);
                if (setCursor != null && setCursor.moveToFirst()) {
                    List<Set> sets = workoutExercise.getSets();
                    if (sets.size() > 0) {
                        int noOfSet = workoutExercise.getNoOfSets();
                        int noOfExercises = sets.size() / noOfSet;
                        exerciseIdArray = new String[noOfExercises];
                        if (setCursor.getCount() > 0) {
                            int toBeSavedSetsSize = sets.size();
                            int currentSetsSize = setCursor.getCount();
                            InternetVsLocalSize comparedSetResult =
                                    toBeSavedSetsSize > currentSetsSize ? InternetVsLocalSize.MORE :
                                            toBeSavedSetsSize == currentSetsSize ? InternetVsLocalSize.EQUAL : InternetVsLocalSize.LESS;
                            switch (comparedSetResult) {
                                case MORE:
                                    int count = 0;
                                    while (setCursor.moveToNext()) {
                                        int setPosition = setCursor.getInt(setCursor.getColumnIndex(SetColumns.SET_POSITION));
                                        long setId = setCursor.getLong(setCursor.getColumnIndex(SetColumns.ID));
                                        Optional<Set> setOptional = sets.
                                                stream().
                                                filter(s -> s.getSetPosition() == setPosition).
                                                findFirst();
                                        if (setOptional.isPresent()) {
                                            Set set = setOptional.get();
                                            ContentValues setContentValues =
                                                    getSetContentValuesFromSet(set, count, noOfExercises, exerciseIdArray, workoutExerciseId, setPosition);
                                            if (setContentValues != null)
                                                activity.getContentResolver().update(UltimateFitProvider.Sets.CONTENT_URI, setContentValues,
                                                        SetColumns.ID + " = " + setId, null);
                                        }
                                    }
                                    for (int j = currentSetsSize; j < sets.size(); j++) {
                                        Set set = sets.get(j);
                                        ContentValues setContentValues =
                                                getSetContentValuesFromSet(set, count, noOfExercises, exerciseIdArray, workoutExerciseId, j);
                                        if (setContentValues != null)
                                            activity.getContentResolver().insert(UltimateFitProvider.Sets.CONTENT_URI, setContentValues);
                                    }
                                    break;
                                case LESS:
                                    int count_less = 0;
                                    while (setCursor.moveToNext()) {
                                        int setPosition = setCursor.getInt(setCursor.getColumnIndex(SetColumns.SET_POSITION));
                                        long setId = setCursor.getLong(setCursor.getColumnIndex(SetColumns.ID));
                                        Optional<Set> setOptional = sets.
                                                stream().
                                                filter(s -> s.getSetPosition() == setPosition).
                                                findFirst();
                                        if (setOptional.isPresent()) {
                                            Set set = setOptional.get();
                                            ContentValues setContentValues =
                                                    getSetContentValuesFromSet(set, count_less, noOfExercises, exerciseIdArray, workoutExerciseId, setPosition);
                                            if (setContentValues != null)
                                                activity.getContentResolver().update(UltimateFitProvider.Sets.CONTENT_URI, setContentValues,
                                                        SetColumns.ID + " = " + setId, null);
                                        } else {
                                            activity.getContentResolver().delete(UltimateFitProvider.Sets.CONTENT_URI,
                                                    SetColumns.ID + " = " + setId, null);
                                        }
                                    }
                                    break;
                                case EQUAL:
                                    int count_equal = 0;
                                    while (setCursor.moveToNext()) {
                                        int setPosition = setCursor.getInt(setCursor.getColumnIndex(SetColumns.SET_POSITION));
                                        long setId = setCursor.getLong(setCursor.getColumnIndex(SetColumns.ID));
                                        Optional<Set> setOptional = sets.
                                                stream().
                                                filter(s -> s.getSetPosition() == setPosition).
                                                findFirst();
                                        if (setOptional.isPresent()) {
                                            Set set = setOptional.get();
                                            ContentValues setContentValues =
                                                    getSetContentValuesFromSet(set, count_equal, noOfExercises, exerciseIdArray, workoutExerciseId, setPosition);
                                            if (setContentValues != null)
                                                activity.getContentResolver().update(UltimateFitProvider.Sets.CONTENT_URI, setContentValues,
                                                        SetColumns.ID + " = " + setId, null);
                                        }
                                    }
                                    break;
                            }
                        } else//no set for this workout exercise
                        {
                            int count = 0;
                            for (int j = 0; j < sets.size(); j++) {
                                Set set = sets.get(j);
                                ContentValues setContentValues =
                                        getSetContentValuesFromSet(set, count, noOfExercises, exerciseIdArray, workoutExerciseId, j);
                                if (setContentValues != null)
                                    activity.getContentResolver().insert(UltimateFitProvider.Sets.CONTENT_URI, setContentValues);
                            }
                        }
                    }
                }
                if (setCursor != null)
                    setCursor.close();

                String joinedExerciseIds = strJoin(exerciseIdArray, ",");
                ContentValues workoutExerciseContentValues = new Workout_exercisesValuesBuilder()
                        .firstExerciseImage(workoutExercise.getFirstExerciseImage())
                        .firstExerciseName(workoutExercise.getFirstExerciseName())
                        .rep(workoutExercise.getRep())
                        .set(workoutExercise.getNoOfSets())
                        .workoutId(workoutId)
                        .workoutExerciseNumber(workoutExerciseNumber)
                        .exerciseIds(joinedExerciseIds).values();
                if (isSyncAllowed) {
                    activity.getContentResolver().update(UltimateFitProvider.WorkoutExercises.CONTENT_URI, workoutExerciseContentValues,
                            WorkoutExerciseColumns.ID + " = " + workoutExerciseId, null);
                }
            } else {
                activity.getContentResolver().delete(UltimateFitProvider.Sets.CONTENT_URI,
                        SetColumns.WORKOUT_EXERCISE_ID + " = " + workoutExerciseId, null);
                activity.getContentResolver().delete(UltimateFitProvider.WorkoutExercises.CONTENT_URI,
                        WorkoutExerciseColumns.ID + " = " + workoutExerciseId, null);
            }
        }
    }

    private ContentValues getSetContentValuesFromSet(Set set, int count, int noOfExercises, String[] exerciseIdArray, long workoutExerciseId, int setPosition) {
        ContentValues setContentValues = null;
        String exerciseName = set.getExerciseName();
        String[] argsExercise = {exerciseName};
        Cursor exerciseCursor = activity.getContentResolver().query(UltimateFitProvider.Exercises.CONTENT_URI, null,
                ExerciseColumns.EXERCISE_NAME + " = ?", argsExercise, null);
        long exerciseId;
        if (exerciseCursor != null && exerciseCursor.moveToFirst()) {
            exerciseId = exerciseCursor.getLong(exerciseCursor.getColumnIndex(ExerciseColumns.ID));
            if (count < noOfExercises) {
                exerciseIdArray[count] = String.valueOf(exerciseId);
                count++;
            }
            exerciseCursor.close();
            setContentValues = new SetsValuesBuilder()
                    .exerciseNumber(set.getExerciseNumber())
                    .exerciseId(exerciseId)
                    .rep(set.getNoOfRep())
                    .weightRatio(set.getWeightRatio())
                    .setName(exerciseName)
                    .workoutExerciseId(workoutExerciseId)
                    .setPosition(setPosition)
                    .setNumber(set.getSetNumber()).values();
        }
        return setContentValues;
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
        if (isDataUpdated)
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
                Toast.makeText(this, R.string.toast_sign_in_cancel, Toast.LENGTH_SHORT).show();
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
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
        } else {
            startActivity(intent);
            // Implement this feature without material design
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public AndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
