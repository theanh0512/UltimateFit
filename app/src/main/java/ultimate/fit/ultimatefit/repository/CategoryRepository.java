package ultimate.fit.ultimatefit.repository;

import android.arch.lifecycle.LiveData;

import java.util.List;

import ultimate.fit.ultimatefit.entity.Category;

/**
 * Created by Pham on 31/7/17.
 */

public interface CategoryRepository {
    LiveData<List<Category>> getCategory();

    void addCategory(Category category);
}
