package ultimate.fit.ultimatefit.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import ultimate.fit.ultimatefit.entity.Exercise;

/**
 * Created by Pham on 31/7/17.
 */
@Dao
public interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Exercise exercise);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertExercises(List<Exercise> exercises);

    @Query("SELECT * FROM exercises WHERE categoryPk = :categoryPk")
    LiveData<List<Exercise>> loadExercisesWithCategoryPk(int categoryPk);

    @Query("SELECT * FROM exercises")
    LiveData<List<Exercise>> loadExercises();
}
