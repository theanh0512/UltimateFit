package ultimate.fit.ultimatefit.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;


public interface WorkoutExerciseColumns {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    String ID = "_id";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String WORKOUT_ID = "workout_id";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String EXERCISE_IDS = "exercise_ids";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String FIRST_EXERCISE_NAME = "first_exercise_name";

    @DataType(DataType.Type.INTEGER)
    String REP = "rep";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String FIRST_EXERCISE_IMAGE = "first_exercise_image";

    @DataType(DataType.Type.INTEGER)
    String SET = "no_of_set";
}
