package ultimate.fit.ultimatefit.injection;

import javax.inject.Singleton;

import dagger.Component;
import ultimate.fit.ultimatefit.ui.MainActivity;

/**
 * Created by Pham on 27/7/17.
 */

@Singleton
@Component(modules={AppModule.class, NetModule.class})
public interface NetComponent {
    void inject(MainActivity activity);
    // void inject(MyFragment fragment);
    // void inject(MyService service);
}