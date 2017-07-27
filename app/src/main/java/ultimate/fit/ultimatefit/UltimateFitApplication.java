package ultimate.fit.ultimatefit;

import android.app.Application;

import ultimate.fit.ultimatefit.injection.NetComponent;
import ultimate.fit.ultimatefit.injection.NetModule;
import ultimate.fit.ultimatefit.injection.AppModule;
import ultimate.fit.ultimatefit.utils.Config;

import ultimate.fit.ultimatefit.injection.DaggerNetComponent;

/**
 * Created by Pham on 27/7/17.
 */

public class UltimateFitApplication extends Application {
    private NetComponent netComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        // Dagger%COMPONENT_NAME%
        netComponent = DaggerNetComponent.builder()
                // list of modules that are part of this component need to be created here too
                .appModule(new AppModule(this)) // This also corresponds to the name of your module: %component_name%Module
                .netModule(new NetModule(Config.MAIN_URL))
                .build();

        // If a Dagger 2 component does not have any constructor arguments for any of its modules,
        // then we can use .create() as a shortcut instead:
        //  netComponent = com.codepath.dagger.components.DaggerNetComponent.create();
    }

    public NetComponent getNetComponent() {
        return netComponent;
    }
}
