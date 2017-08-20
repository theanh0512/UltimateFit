package ultimate.fit.ultimatefit.ui.category;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import ultimate.fit.ultimatefit.api.CategoryApiResponse;
import ultimate.fit.ultimatefit.entity.Resource;
import ultimate.fit.ultimatefit.repository.CategoryRepository;

/**
 * Created by Pham on 7/8/17.
 */

public class CategoryViewModel extends ViewModel {
    private final LiveData<Resource<CategoryApiResponse>> categoryApiResponse;

    @Inject
    public CategoryViewModel(CategoryRepository repository){

    }
}
