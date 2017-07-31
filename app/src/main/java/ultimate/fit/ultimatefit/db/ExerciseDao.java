package ultimate.fit.ultimatefit.db;

import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;

import ultimate.fit.ultimatefit.entity.Exercise;

/**
 * Created by Pham on 31/7/17.
 */

public interface ExerciseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Exercise exercise);
}
