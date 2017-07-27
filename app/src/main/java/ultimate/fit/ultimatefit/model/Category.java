package ultimate.fit.ultimatefit.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Pham on 27/7/17.
 */

@Entity(tableName = "categories")
public class Category {
    @SerializedName("_id")
    @PrimaryKey(autoGenerate = true)
    public final String ID;
    @SerializedName("image_path")
    private final String imagePath;
    @SerializedName("category_name")
    private final String categoryName;

    public Category(String ID, String imagePath, String categoryName) {
        this.ID = ID;
        this.imagePath = imagePath;
        this.categoryName = categoryName;
    }

    public String getID() {
        return ID;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getCategoryName() {
        return categoryName;
    }
}