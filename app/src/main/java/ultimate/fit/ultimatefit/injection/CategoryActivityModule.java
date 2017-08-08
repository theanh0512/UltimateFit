package ultimate.fit.ultimatefit.injection;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import ultimate.fit.ultimatefit.ui.CategoryActivity;

/**
 * Created by Pham on 3/8/17.
 */
/**
 * Define CategoryActivity-specific dependencies here.
 */
@Module
public abstract class CategoryActivityModule {
    @ContributesAndroidInjector
    abstract CategoryActivity contributeCategoryActivity();
}
