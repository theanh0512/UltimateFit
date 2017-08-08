package ultimate.fit.ultimatefit.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;

import ultimate.fit.ultimatefit.entity.Category;

/**
 * Created by Pham on 31/7/17.
 */
@Dao
public interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Category category);
}
