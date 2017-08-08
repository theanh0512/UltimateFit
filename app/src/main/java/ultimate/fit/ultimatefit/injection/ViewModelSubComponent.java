package ultimate.fit.ultimatefit.injection;

import dagger.Subcomponent;
import ultimate.fit.ultimatefit.viewmodel.CategoryViewModel;
import ultimate.fit.ultimatefit.viewmodel.ExerciseViewModel;
import ultimate.fit.ultimatefit.viewmodel.UltimateFitViewModelFactory;

/**
 * A sub component to create ViewModels. It is called by the
 * {@link UltimateFitViewModelFactory}.
 */
@Subcomponent
public interface ViewModelSubComponent {
    CategoryViewModel categoryViewModel();

    ExerciseViewModel exerciseViewModel();

    @Subcomponent.Builder
    interface Builder {
        ViewModelSubComponent build();
    }
}
