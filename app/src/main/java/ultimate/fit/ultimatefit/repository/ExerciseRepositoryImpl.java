package ultimate.fit.ultimatefit.repository;

import android.arch.lifecycle.LiveData;

import java.util.List;

import javax.inject.Inject;

import ultimate.fit.ultimatefit.db.UltimateFitDatabase;
import ultimate.fit.ultimatefit.entity.Exercise;

/**
 * Created by Pham on 31/7/17.
 */

public class ExerciseRepositoryImpl implements ExerciseRepository {

    @Inject
    UltimateFitDatabase ultimateFitDatabase;

    public ExerciseRepositoryImpl(UltimateFitDatabase ultimateFitDatabase) {
        this.ultimateFitDatabase = ultimateFitDatabase;
    }

    @Override
    public LiveData<List<Exercise>> getExercises() {
        return null;
    }

    @Override
    public void addExercise(Exercise exercise) {

    }
}
