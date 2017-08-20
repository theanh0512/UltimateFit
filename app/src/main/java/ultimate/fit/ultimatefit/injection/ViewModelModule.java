package ultimate.fit.ultimatefit.injection;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import ultimate.fit.ultimatefit.ui.category.CategoryViewModel;
import ultimate.fit.ultimatefit.viewmodel.ExerciseViewModel;
import ultimate.fit.ultimatefit.viewmodel.UltimateFitViewModelFactory;

@Module
abstract class ViewModelModule {
  @Binds
  @IntoMap
  @ViewModelKey(CategoryViewModel.class)
  abstract ViewModel bindCategoryViewModel(CategoryViewModel categoryViewModel);

  @Binds
  @IntoMap
  @ViewModelKey(ExerciseViewModel.class)
  abstract ViewModel bindExerciseViewModel(ExerciseViewModel exerciseViewModel);

  @Binds
  abstract ViewModelProvider.Factory bindViewModelFactory(UltimateFitViewModelFactory factory);
}
