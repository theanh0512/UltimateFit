package ultimate.fit.ultimatefit.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import ultimate.fit.ultimatefit.entity.Category;

/**
 * Created by Pham on 31/7/17.
 */
@Dao
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Category category);

    @Query("SELECT * FROM categories")
    LiveData<List<Category>> loadAllCategories();
}
