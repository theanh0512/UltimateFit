package ultimate.fit.ultimatefit.data;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.IfNotExists;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;

@Database(version = UltimateFitDatabase.VERSION)
public class UltimateFitDatabase {
    public static final int VERSION = 1;
    public static final String[] _MIGRATIONS = {
            // Put DDL/DML commands here, one string per VERSION increment
           "ALTER TABLE " + "workout_exercises"+ " ADD COLUMN " + WorkoutExerciseColumns.WORKOUT_EXERCISE_NUMBER + " INTEGER DEFAULT -1;",
//            "ALTER TABLE " + "workout_exercises"+ " ADD COLUMN " + WorkoutExerciseColumns.NOTE_OF_WORKOUT_EXERCISE + " STRING;",
//            "ALTER TABLE " + "workouts"+ " ADD COLUMN " + WorkoutColumns.NOTE_OF_WORKOUT + " STRING;"
    };

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

    @OnUpgrade
    public synchronized void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = oldVersion; i < newVersion; i++) {
            String migration = _MIGRATIONS[i-1];
            db.beginTransaction();
            try {
                db.execSQL(migration);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                Log.e("Database", "Error executing database migration: " + migration);
                break;
            } finally {
                db.endTransaction();
            }
        }
    }
}
