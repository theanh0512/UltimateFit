package ultimate.fit.ultimatefit.viewmodel;

import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import ultimate.fit.ultimatefit.repository.ExerciseRepository;

/**
 * Created by Pham on 7/8/17.
 */

public class ExerciseViewModel extends ViewModel {
    @Inject
    public ExerciseViewModel(ExerciseRepository repository) {
    }
}
