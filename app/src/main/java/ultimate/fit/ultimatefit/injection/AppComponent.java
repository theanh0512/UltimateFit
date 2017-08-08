package ultimate.fit.ultimatefit.injection;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.support.AndroidSupportInjectionModule;
import ultimate.fit.ultimatefit.UltimateFitApplication;

/**
 * Created by Pham on 27/7/17.
 */
@Component(modules = {
        /* Use AndroidInjectionModule.class if you're not using support library */
        AndroidSupportInjectionModule.class,
        AppModule.class,
        MainActivityModule.class})
public interface AppComponent {
    void inject(UltimateFitApplication app);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(UltimateFitApplication application);

        AppComponent build();
    }
}