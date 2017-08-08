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
    private final String imagePath;
    @SerializedName("exercise_image_2_path")
    private final String imagePath2;
    @SerializedName("video_path")
    private final String videoPath;
    @SerializedName("exercise_name")
    private final String exerciseName;
    @SerializedName("description")
    private final String description;
    @SerializedName("one_rep_max")
    private final String oneRepMax;
    @SerializedName("category_id")
    private final String categoryId;

    public Exercise(long ID, String imagePath, String imagePath2, String videoPath, String exerciseName, String description, String oneRepMax, String categoryId) {
        this.ID = ID;
        this.imagePath = imagePath;
        this.imagePath2 = imagePath2;
        this.videoPath = videoPath;
        this.exerciseName = exerciseName;
        this.description = description;
        this.oneRepMax = oneRepMax;
        this.categoryId = categoryId;
    }

    public long getID() {
        return ID;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public String getImagePath2() {
        return imagePath2;
    }

    public String getVideoPath() {
        return videoPath;
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