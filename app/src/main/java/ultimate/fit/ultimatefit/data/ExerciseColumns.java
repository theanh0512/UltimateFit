package ultimate.fit.ultimatefit.data;

import android.support.annotation.Nullable;

import net.simonvt.schematic.annotation.AutoIncrement;
import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;
import net.simonvt.schematic.annotation.Unique;


public interface ExerciseColumns {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    @AutoIncrement
    String ID = "_id";

    @DataType(DataType.Type.INTEGER)
    @NotNull
    String CATEGORY_ID = "category_id";

    @DataType(DataType.Type.TEXT)
    @Nullable
    String IMAGE_PATH = "exercise_image_path";

    @DataType(DataType.Type.TEXT)
    @Nullable
    String IMAGE_2_PATH = "exercise_image_2_path";

    @DataType(DataType.Type.TEXT)
    @Nullable
    String VIDEO_PATH = "video_path";

    @DataType(DataType.Type.TEXT)
    @NotNull
    @Unique
    String EXERCISE_NAME = "exercise_name";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String DESCRIPTION = "description";

    @DataType(DataType.Type.REAL)
    String ONE_REP_MAX = "one_rep_max";
}
