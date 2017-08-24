package ultimate.fit.ultimatefit.ui.category;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import ultimate.fit.ultimatefit.entity.CategoryApiResponse;
import ultimate.fit.ultimatefit.entity.Exercise;
import ultimate.fit.ultimatefit.entity.Resource;
import ultimate.fit.ultimatefit.repository.CategoryRepository;
import ultimate.fit.ultimatefit.repository.ExerciseRepository;
import ultimate.fit.ultimatefit.utils.AbsentLiveData;

/**
 * Created by Pham on 7/8/17.
 */

public class CategoryViewModel extends ViewModel {
    private final LiveData<Resource<List<CategoryApiResponse>>> categoryApiResponseList;
    private final LiveData<Resource<List<Exercise>>> exerciseList;
    private final MutableLiveData<Long> lastUpdated = new MutableLiveData<>();
    private final MutableLiveData<Integer> categoryPk = new MutableLiveData<>();

    @Inject
    public CategoryViewModel(CategoryRepository categoryRepository, ExerciseRepository exerciseRepository) {
        categoryApiResponseList = Transformations.switchMap(lastUpdated, lastUpdated -> {
            if (lastUpdated == null) return AbsentLiveData.create();
            else return categoryRepository.getCategory();
        });
        exerciseList = Transformations.switchMap(categoryPk, categoryPk -> {
            if (categoryPk == null) return AbsentLiveData.create();
            else return exerciseRepository.getExercises(categoryPk);
        });
    }

    LiveData<Resource<List<CategoryApiResponse>>> getCategoryApiResponseList() {
        return categoryApiResponseList;
    }

    LiveData<Resource<List<Exercise>>> getExerciseList() {
        return exerciseList;
    }


    public void setLastUpdated(long lastUpdated) {
        if (Objects.equals(lastUpdated, this.lastUpdated.getValue())) return;
        this.lastUpdated.setValue(lastUpdated);
    }

    public void setCategoryPk(int categoryPk) {
        if (Objects.equals(categoryPk, this.categoryPk.getValue())) return;
        this.categoryPk.setValue(categoryPk);
    }
}
