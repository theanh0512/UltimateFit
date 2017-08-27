package ultimate.fit.ultimatefit.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import ultimate.fit.ultimatefit.AppExecutors;
import ultimate.fit.ultimatefit.api.ApiResponse;
import ultimate.fit.ultimatefit.api.UltimateFitService;
import ultimate.fit.ultimatefit.db.CategoryDao;
import ultimate.fit.ultimatefit.entity.CategoryApiResponse;
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
    private final AppExecutors appExecutors;

    @Inject
    public CategoryRepository(CategoryDao categoryDao, UltimateFitService ultimateFitService,
                              Context context, AppExecutors appExecutors) {
        this.categoryDao = categoryDao;
        this.ultimateFitService = ultimateFitService;
        this.context = context;
        this.appExecutors = appExecutors;
    }


    public LiveData<Resource<List<CategoryApiResponse>>> getCategory() {
        return new NetworkBoundResource<List<CategoryApiResponse>, List<CategoryApiResponse>>(appExecutors) {
            @Override
            protected void saveCallResult(@NonNull List<CategoryApiResponse> item) {
                for (CategoryApiResponse categoryApiResponse : item) {
                    categoryDao.insert(categoryApiResponse);
                }
            }

            @Override
            protected boolean shouldFetch(@Nullable List<CategoryApiResponse> data) {
                return data == null || data.isEmpty();
            }

            @NonNull
            @Override
            protected LiveData<List<CategoryApiResponse>> loadFromDb() {

                return categoryDao.loadAllCategories();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<List<CategoryApiResponse>>> createCall() {
                //ToDo: implement last updated
                long lastUpdated = SharedPreferenceHelper.getInstance(context).getLong(SharedPreferenceHelper.Key.LAST_CATEGORY_MODIFIED_DATE_LONG);
                if (lastUpdated == 0) {
                    lastUpdated = 1000000;
                }
                return ultimateFitService.getCategory(lastUpdated);
            }
        }.asLiveData();
    }
}
