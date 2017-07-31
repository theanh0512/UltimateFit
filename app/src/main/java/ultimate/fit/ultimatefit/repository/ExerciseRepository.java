package ultimate.fit.ultimatefit.repository;

import android.arch.lifecycle.LiveData;

import java.util.List;

import ultimate.fit.ultimatefit.entity.Exercise;

/**
 * Created by Pham on 31/7/17.
 */

public interface ExerciseRepository {
    LiveData<List<Exercise>> getExercises();

    void addExercise(Exercise exercise);
}
