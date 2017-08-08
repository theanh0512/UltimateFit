package ultimate.fit.ultimatefit.injection;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ultimate.fit.ultimatefit.ui.fragment.TabPlanFragment;
import ultimate.fit.ultimatefit.ui.fragment.TabWorkoutFragment;

@Module
public abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract TabPlanFragment contributeTabPlanFragment();

    @ContributesAndroidInjector
    abstract TabWorkoutFragment contributeTabWorkoutFragment();
}
