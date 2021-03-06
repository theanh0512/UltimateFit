package ultimate.fit.ultimatefit.data;

import android.support.annotation.Nullable;

import net.simonvt.schematic.annotation.DataType;
import net.simonvt.schematic.annotation.NotNull;
import net.simonvt.schematic.annotation.PrimaryKey;


public interface CategoryColumns {
    @DataType(DataType.Type.INTEGER)
    @PrimaryKey
    String ID = "_id";

    @DataType(DataType.Type.TEXT)
    @Nullable
    String IMAGE_PATH = "image_path";

    @DataType(DataType.Type.TEXT)
    @NotNull
    String CATEGORY_NAME = "category_name";
}
