package ultimate.fit.ultimatefit.db;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import ultimate.fit.ultimatefit.entity.CategoryApiResponse;
import ultimate.fit.ultimatefit.entity.Exercise;

@Database(entities = {Exercise.class, CategoryApiResponse.class}, version = 1)
public abstract class UltimateFitDatabase extends RoomDatabase {

    public abstract ExerciseDao exerciseDao();

    public abstract CategoryDao categoryDao();

}
