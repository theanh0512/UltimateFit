package ultimate.fit.ultimatefit.data;

import android.support.annotation.Nullable;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.DefaultValue;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;


public interface PlanColumns {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    String ID = "_id";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String PLAN_UUID = "plan_uuid";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String NAME = "name";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String GOAL = "goal";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String CREATOR = "creator";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String DAY_PER_WEEK = "day_per_week";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String NUM_OF_WEEK = "num_of_week";

    @DataType(DataType.Type.INTEGER)
    @Nullable
    //@DefaultValue("0")
    String APPLIED_DATE = "applied_date";
}
