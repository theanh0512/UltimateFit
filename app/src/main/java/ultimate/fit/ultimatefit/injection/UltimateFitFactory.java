package ultimate.fit.ultimatefit.injection;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import ultimate.fit.ultimatefit.UltimateFitApplication;

public class UltimateFitFactory extends ViewModelProvider.NewInstanceFactory {

    private UltimateFitApplication application;

    public UltimateFitFactory(UltimateFitApplication application) {
        this.application = application;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        T t = super.create(modelClass);
        if (t instanceof NetComponent.Injectable) {
            ((NetComponent.Injectable) t).inject(application.getNetComponent());
        }
        return t;
    }
}
