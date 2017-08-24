package ultimate.fit.ultimatefit.entity;

import android.arch.persistence.room.Entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Pham on 27/7/17.
 */

@Entity(tableName = "exercises", primaryKeys = {"name"})
public class Exercise {
    @SerializedName("image")
    public final String image;
    @SerializedName("image2")
    public final String image2;
    @SerializedName("video")
    public final String video;
    @SerializedName("name")
    public final String name;
    @SerializedName("description")
    public final String description;
    @SerializedName("one_rep_max")
    public final String oneRepMax;
    @SerializedName("category")
    public final int categoryPk;

    public Exercise(String image, String image2, String video, String name, String description, String oneRepMax, int categoryPk) {
        this.image = image;
        this.image2 = image2;
        this.video = video;
        this.name = name;
        this.description = description;
        this.oneRepMax = oneRepMax;
        this.categoryPk = categoryPk;
    }
}