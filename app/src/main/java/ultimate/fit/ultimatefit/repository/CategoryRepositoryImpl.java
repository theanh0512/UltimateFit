package ultimate.fit.ultimatefit.repository;

import android.arch.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;

import ultimate.fit.ultimatefit.db.UltimateFitDatabase;
import ultimate.fit.ultimatefit.entity.Category;

/**
 * Created by Pham on 31/7/17.
 */

public class CategoryRepositoryImpl implements CategoryRepository {
    @Inject
    UltimateFitDatabase ultimateFitDatabase;

    public CategoryRepositoryImpl(UltimateFitDatabase ultimateFitDatabase) {
        this.ultimateFitDatabase = ultimateFitDatabase;
    }

    @Override
    public LiveData<List<Category>> getCategory() {
        return null;
    }

    @Override
    public void addCategory(Category category) {

    }
}
