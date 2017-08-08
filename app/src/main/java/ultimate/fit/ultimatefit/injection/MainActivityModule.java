package ultimate.fit.ultimatefit.injection;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ultimate.fit.ultimatefit.ui.MainActivity;

/**
 * Created by Pham on 3/8/17.
 * Define MainActivity-specific dependencies here.
 */
@Module
public abstract class MainActivityModule {
    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract MainActivity contributeMainActivity();
}
