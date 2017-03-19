package ultimate.fit.ultimatefit.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.TableEndpoint;

@ContentProvider(authority = UltimateFitProvider.AUTHORITY, database = UltimateFitDatabase.class)
public class UltimateFitProvider {
    public static final String AUTHORITY = "ultimate.fit.ultimatefit.data.UltimateFitProvider";
    static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    private static Uri buildUri(String... paths) {
        Uri.Builder builder = BASE_CONTENT_URI.buildUpon();
        for (String path : paths) {
            builder.appendPath(path);
        }
        return builder.build();
    }

    interface Path {
        String WORKOUTS = "workouts";
        String PLANS = "plans";
        String FROM_PLAN = "fromPlan";
        String CATEGORIES = "categories";
        String EXERCISES = "exercises";
        String FROM_CATEGORY = "fromCategory";
        String WORKOUT_EXERCISES = "workoutExercises";
        String SETS = "sets";
        String FROM_WORKOUT = "fromWorkout";
        String FROM_WORKOUT_EXERCISE = "fromWorkoutExercise";
    }

    @TableEndpoint(table = UltimateFitDatabase.Tables.PLANS)
    public static class Plans {

        @ContentUri(
                path = Path.PLANS,
                type = "vnd.android.cursor.dir/plan",
                defaultSort = PlanColumns.NAME + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.PLANS);

        @InexactContentUri(
                path = Path.PLANS + "/#",
                name = "PLAN_ID",
                type = "vnd.android.cursor.item/plan",
                whereColumn = PlanColumns.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.PLANS, String.valueOf(id));
        }
    }

    @TableEndpoint(table = UltimateFitDatabase.WORKOUTS)
    public static class Workouts {

        @ContentUri(
                path = Path.WORKOUTS,
                type = "vnd.android.cursor.dir/workout",
                defaultSort = WorkoutColumns.DAY_NUMBER + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.WORKOUTS);

        @InexactContentUri(
                path = Path.WORKOUTS + "/#",
                name = "WORKOUT_ID",
                type = "vnd.android.cursor.item/workout",
                whereColumn = WorkoutColumns.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.WORKOUTS, String.valueOf(id));
        }

        @InexactContentUri(
                name = "WORKOUTS_FROM_PLAN",
                path = Path.WORKOUTS + "/" + Path.FROM_PLAN + "/#",
                type = "vnd.android.cursor.dir/plan",
                whereColumn = WorkoutColumns.PLAN_ID,
                join = "left join " + UltimateFitDatabase.Tables.PLANS + " on " + UltimateFitDatabase.WORKOUTS
                        + "."
                        + WorkoutColumns.PLAN_ID
                        + "="
                        + UltimateFitDatabase.Tables.PLANS
                        + "."
                        + PlanColumns.ID,
                pathSegment = 2)
        public static Uri fromPlan(long planId) {
            return buildUri(Path.WORKOUTS, Path.FROM_PLAN, String.valueOf(planId));
        }

        public static Uri withWorkoutID(String workoutid) {
            return buildUri(Path.WORKOUTS, workoutid);
        }
    }

    @TableEndpoint(table = UltimateFitDatabase.Tables.CATEGORIES)
    public static class Categories {

        @ContentUri(
                path = Path.CATEGORIES,
                type = "vnd.android.cursor.dir/category",
                defaultSort = CategoryColumns.CATEGORY_NAME + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.CATEGORIES);

        @InexactContentUri(
                path = Path.CATEGORIES + "/#",
                name = "CATEGORY_ID",
                type = "vnd.android.cursor.item/category",
                whereColumn = CategoryColumns.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.CATEGORIES, String.valueOf(id));
        }
    }

    @TableEndpoint(table = UltimateFitDatabase.EXERCISES)
    public static class Exercises {

        @ContentUri(
                path = Path.EXERCISES,
                type = "vnd.android.cursor.dir/exercise",
                defaultSort = ExerciseColumns.EXERCISE_NAME + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.EXERCISES);

        @InexactContentUri(
                path = Path.EXERCISES + "/#",
                name = "EXERCISE_ID",
                type = "vnd.android.cursor.item/exercise",
                whereColumn = ExerciseColumns.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.EXERCISES, String.valueOf(id));
        }

        @InexactContentUri(
                name = "EXERCISES_FROM_CATEGORY",
                path = Path.EXERCISES + "/" + Path.FROM_CATEGORY + "/#",
                type = "vnd.android.cursor.dir/category",
                whereColumn = ExerciseColumns.CATEGORY_ID,
                join = "left join " + UltimateFitDatabase.Tables.CATEGORIES + " on " + UltimateFitDatabase.EXERCISES
                        + "."
                        + ExerciseColumns.CATEGORY_ID
                        + "="
                        + UltimateFitDatabase.Tables.CATEGORIES
                        + "."
                        + CategoryColumns.ID,
                pathSegment = 2)
        public static Uri fromCategory(long categoryId) {
            return buildUri(Path.EXERCISES, Path.FROM_CATEGORY, String.valueOf(categoryId));
        }
    }

    @TableEndpoint(table = UltimateFitDatabase.Tables.WORKOUT_EXERCISES)
    public static class WorkoutExercises {

        @ContentUri(
                path = Path.WORKOUT_EXERCISES,
                type = "vnd.android.cursor.dir/workoutexercise",
                defaultSort = WorkoutExerciseColumns.WORKOUT_ID + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.WORKOUT_EXERCISES);

        @InexactContentUri(
                path = Path.WORKOUT_EXERCISES + "/#",
                name = "WORKOUT_EXERCISES_ID",
                type = "vnd.android.cursor.item/workoutexercise",
                whereColumn = WorkoutExerciseColumns.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.WORKOUT_EXERCISES, String.valueOf(id));
        }

        @InexactContentUri(
                name = "WORKOUT_EXERCISES_FROM_WORKOUT",
                path = Path.WORKOUT_EXERCISES + "/" + Path.FROM_WORKOUT + "/#",
                type = "vnd.android.cursor.dir/workout",
                whereColumn = WorkoutExerciseColumns.WORKOUT_ID,
                join = "left join " + UltimateFitDatabase.WORKOUTS + " on " + UltimateFitDatabase.WORKOUTS
                        + "."
                        + WorkoutColumns.ID
                        + "="
                        + UltimateFitDatabase.Tables.WORKOUT_EXERCISES
                        + "."
                        + WorkoutExerciseColumns.WORKOUT_ID,
                pathSegment = 2)
        public static Uri fromWorkout(long workoutId) {
            return buildUri(Path.WORKOUT_EXERCISES, Path.FROM_WORKOUT, String.valueOf(workoutId));
        }
    }

    @TableEndpoint(table = UltimateFitDatabase.Tables.SETS)
    public static class Sets {

        @ContentUri(
                path = Path.SETS,
                type = "vnd.android.cursor.dir/set",
                defaultSort = SetColumns.SET_NUMBER + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.SETS);

        @InexactContentUri(
                path = Path.SETS + "/#",
                name = "SET_ID",
                type = "vnd.android.cursor.item/set",
                whereColumn = SetColumns.ID,
                pathSegment = 1)
        public static Uri withId(long id) {
            return buildUri(Path.SETS, String.valueOf(id));
        }

        @InexactContentUri(
                name = "SETS_FROM_WORKOUT_EXERCISE",
                path = Path.SETS + "/" + Path.FROM_WORKOUT_EXERCISE + "/#",
                type = "vnd.android.cursor.dir/workoutexercise",
                whereColumn = SetColumns.WORKOUT_EXERCISE_ID,
                join = "left join " + UltimateFitDatabase.Tables.WORKOUT_EXERCISES + " on " + UltimateFitDatabase.Tables.SETS
                        + "."
                        + SetColumns.WORKOUT_EXERCISE_ID
                        + "="
                        + UltimateFitDatabase.Tables.WORKOUT_EXERCISES
                        + "."
                        + WorkoutExerciseColumns.ID,
                pathSegment = 2)
        public static Uri fromWorkoutExercise(long workoutExerciseId) {
            return buildUri(Path.SETS, Path.FROM_WORKOUT_EXERCISE, String.valueOf(workoutExerciseId));
        }
    }
}
