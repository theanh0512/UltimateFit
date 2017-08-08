package ultimate.fit.ultimatefit.repository;

import android.arch.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import ultimate.fit.ultimatefit.api.UltimateFitService;
import ultimate.fit.ultimatefit.entity.Category;

/**
 * Created by Pham on 31/7/17.
 */
@Singleton
public class CategoryRepository {
    UltimateFitService ultimateFitService;

    @Inject
    public CategoryRepository(UltimateFitService ultimateFitService) {
        this.ultimateFitService = ultimateFitService;
    }

    LiveData<List<Category>> getCategory() {
        return null;
    }

    public void addCategory(Category category) {

    }
}
