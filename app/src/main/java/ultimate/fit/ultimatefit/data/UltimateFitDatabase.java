package ultimate.fit.ultimatefit.data;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.IfNotExists;
import net.simonvt.schematic.annotation.Table;

@Database(version = UltimateFitDatabase.VERSION)
public class UltimateFitDatabase {
    public static final int VERSION = 1;

    @Table(WorkoutColumns.class)
    public static final String WORKOUTS = "workouts";

    @Table(ExerciseColumns.class)
    @IfNotExists
    public static final String EXERCISES = "exercises";

    public static class Tables {
        @Table(PlanColumns.class)
        @IfNotExists
        public static final String PLANS = "plans";

        @Table(CategoryColumns.class)
        @IfNotExists
        public static final String CATEGORIES = "categories";

        @Table(WorkoutExerciseColumns.class)
        @IfNotExists
        public static final String WORKOUT_EXERCISES = "workout_exercises";

        @Table(SetColumns.class)
        @IfNotExists
        public static final String SETS = "sets";
    }
}
