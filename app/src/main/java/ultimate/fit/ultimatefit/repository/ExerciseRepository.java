package ultimate.fit.ultimatefit.repository;

import android.arch.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;

import ultimate.fit.ultimatefit.api.UltimateFitService;
import ultimate.fit.ultimatefit.entity.Exercise;

/**
 * Created by Pham on 31/7/17.
 */

public class ExerciseRepository {
    UltimateFitService ultimateFitService;

    @Inject
    public ExerciseRepository(UltimateFitService ultimateFitService){
        this.ultimateFitService = ultimateFitService;
    }

    LiveData<List<Exercise>> getExercises() {
        return null;
    }

    public void addExercise(Exercise exercise) {

    }
}