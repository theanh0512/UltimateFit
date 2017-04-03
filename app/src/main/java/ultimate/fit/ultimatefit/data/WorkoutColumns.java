package ultimate.fit.ultimatefit.data;

import android.support.annotation.Nullable;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.DefaultValue;
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

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String DAY_NUMBER = "day_number";

    @DataType(DataType.Type.TEXT)
    @Nullable
    @DefaultValue("'Set body part(s)'")
    String BODY_PART = "body_part";

    //A plan can contains multiple workouts
    @DataType(INTEGER)
    @References(table = Tables.PLANS, column = PlanColumns.ID)
    String PLAN_ID = "planId";
}
