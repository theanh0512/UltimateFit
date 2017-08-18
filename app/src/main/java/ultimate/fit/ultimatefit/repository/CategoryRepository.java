package ultimate.fit.ultimatefit.repository;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import ultimate.fit.ultimatefit.api.ApiResponse;
import ultimate.fit.ultimatefit.api.CategoryApiResponse;
import ultimate.fit.ultimatefit.api.UltimateFitService;
import ultimate.fit.ultimatefit.db.CategoryDao;
import ultimate.fit.ultimatefit.entity.Category;
import ultimate.fit.ultimatefit.entity.Resource;
import ultimate.fit.ultimatefit.utils.SharedPreferenceHelper;

/**
 * Created by Pham on 31/7/17.
 */
@Singleton
public class CategoryRepository {
    private final CategoryDao categoryDao;
    private final UltimateFitService ultimateFitService;
    private Context context;

    @Inject
    public CategoryRepository(CategoryDao categoryDao, UltimateFitService ultimateFitService, Context context) {
        this.categoryDao = categoryDao;
        this.ultimateFitService = ultimateFitService;
        this.context = context;
    }


    public LiveData<Resource<List<CategoryApiResponse>>> getCategory() {
        return new NetworkBoundResource<List<CategoryApiResponse>, List<CategoryApiResponse>>() {
            @Override
            protected void saveCallResult(@NonNull List<CategoryApiResponse> item) {

            }

            @Override
            protected boolean shouldFetch(@Nullable List<CategoryApiResponse> data) {
                return data == null || data.isEmpty();
            }

            @NonNull
            @Override
            protected LiveData<List<CategoryApiResponse>> loadFromDb() {
                final MutableLiveData<List<CategoryApiResponse>> data = new MutableLiveData<>();
                List<CategoryApiResponse> categoryApiResponseList = new ArrayList<>();
                List<Category> categoryList = categoryDao.loadAllCategories().getValue();
                if (categoryList != null) {
                    for (Category category : categoryList) {
                        categoryApiResponseList.add(new CategoryApiResponse(category));
                    }
                }
                data.setValue(categoryApiResponseList);
                return data;
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<CategoryApiResponse>>> createCall() {
                long lastUpdated = SharedPreferenceHelper.getInstance(context).getLong(SharedPreferenceHelper.Key.LAST_CATEGORY_MODIFIED_DATE_LONG);
                if (lastUpdated == 0) {
                    lastUpdated = 1000000;
                }
                return ultimateFitService.getCategory(lastUpdated);
            }
        }.getAsLiveData();
    }
}
