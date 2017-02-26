package ultimate.fit.ultimatefit.data;

import android.net.Uri;

import net.simonvt.schematic.annotation.ContentProvider;
import net.simonvt.schematic.annotation.ContentUri;
import net.simonvt.schematic.annotation.InexactContentUri;
import net.simonvt.schematic.annotation.MapColumns;
import net.simonvt.schematic.annotation.TableEndpoint;

import java.util.HashMap;
import java.util.Map;

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
    }

    @TableEndpoint(table = UltimateFitDatabase.Tables.PLANS)
    public static class Plans {

        @ContentUri(
                path = Path.PLANS,
                type = "vnd.android.cursor.dir/plan",
                defaultSort = PlanColumns.NAME + " ASC")
        public static final Uri CONTENT_URI = buildUri(Path.PLANS);
        static final String WORKOUT_COUNT = "(SELECT COUNT(*) FROM "
                + UltimateFitDatabase.WORKOUTS
                + " WHERE "
                + UltimateFitDatabase.WORKOUTS
                + "."
                + WorkoutColumns.PLAN_ID
                + "="
                + UltimateFitDatabase.Tables.PLANS
                + "."
                + PlanColumns.ID
                + ")";

        @MapColumns
        public static Map<String, String> mapColumns() {
            Map<String, String> map = new HashMap<>();

            map.put(PlanColumns.WORKOUTS, WORKOUT_COUNT);

            return map;
        }

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

        public static Uri withWorkoutID(String workoutid) {
            return buildUri(Path.WORKOUTS, workoutid);
        }
    }
}
