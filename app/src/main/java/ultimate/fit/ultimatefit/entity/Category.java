package ultimate.fit.ultimatefit.entity;

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
    public final long ID;
    @SerializedName("image_path")
    public final String image;
    @SerializedName("category_name")
    public final String name;

    public Category(long ID, String image, String name) {
        this.ID = ID;
        this.image = image;
        this.name = name;
    }

    public long getID() {
        return ID;
    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }
}