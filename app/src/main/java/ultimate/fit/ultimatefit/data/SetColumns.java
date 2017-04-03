package ultimate.fit.ultimatefit.data;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;


public interface SetColumns {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    String ID = "_id";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String EXERCISE_ID = "exercise_id";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String SET_NAME = "set_name";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String WORKOUT_EXERCISE_ID = "workout_exercise_id";

    @DataType(DataType.Type.INTEGER)
    String WEIGHT = "weight";

    @DataType(DataType.Type.INTEGER)
    String REP = "rep_in_set";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String SET_NUMBER = "set_number";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String EXERCISE_NUMBER = "exercise_number";
}
