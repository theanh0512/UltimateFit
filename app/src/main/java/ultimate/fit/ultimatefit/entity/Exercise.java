package ultimate.fit.ultimatefit.entity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Pham on 27/7/17.
 */

@Entity(tableName = "exercises")
public class Exercise {
    @SerializedName("_id")
    @PrimaryKey(autoGenerate = true)
    public final long ID;
    @SerializedName("exercise_image_path")
    public final String image;
    @SerializedName("exercise_image_2_path")
    public final String image2;
    @SerializedName("video_path")
    public final String video;
    @SerializedName("exercise_name")
    public final String name;
    @SerializedName("description")
    public final String description;
    @SerializedName("one_rep_max")
    public final String oneRepMax;
    @SerializedName("category_id")
    public final String categoryId;

    public Exercise(long ID, String image, String image2, String video, String name, String description, String oneRepMax, String categoryId) {
        this.ID = ID;
        this.image = image;
        this.image2 = image2;
        this.video = video;
        this.name = name;
        this.description = description;
        this.oneRepMax = oneRepMax;
        this.categoryId = categoryId;
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

    public String getImage2() {
        return image2;
    }

    public String getVideo() {
        return video;
    }

    public String getDescription() {
        return description;
    }

    public String getOneRepMax() {
        return oneRepMax;
    }

    public String getCategoryId() {
        return categoryId;
    }
}