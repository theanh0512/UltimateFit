package ultimate.fit.ultimatefit.data;

import android.support.annotation.Nullable;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.References;

import ultimate.fit.ultimatefit.data.UltimateFitDatabase.Tables;

import static net.simonvt.schematic.annotation.DataType.Type.INTEGER;


public interface WorkoutColumns {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    String ID = "_id";

//    @DataType(DataType.Type.TEXT)
//    @NotNull
//    String DAY_IN_WEEK = "day_in_week";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String DAY_NUMBER = "day_number";

    @DataType(DataType.Type.TEXT)
    @Nullable
    String BODY_PART = "body_part";

//    @DataType(DataType.Type.INTEGER)
//    @Nullable
//    String APPLIED_DATE = "applied_date";

    //A plan can contains multiple workouts
    @DataType(INTEGER)
    @References(table = Tables.PLANS, column = PlanColumns.ID)
    String PLAN_ID = "planId";
}
