package ultimate.fit.ultimatefit.entity;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Pham on 17/8/17.
 */
@Entity(tableName = "category_response")
public class CategoryApiResponse {
    @PrimaryKey
    public final int pk;
    public final String model;

    @Embedded
    @SerializedName("fields")
    public final Category category;

    public CategoryApiResponse(int pk, String model, Category category) {
        this.pk = pk;
        this.model = model;
        this.category = category;
    }

    public static class Category {
        public final String image;
        public final String name;

        public Category(String image, String name) {
            this.image = image;
            this.name = name;
        }
    }
}
