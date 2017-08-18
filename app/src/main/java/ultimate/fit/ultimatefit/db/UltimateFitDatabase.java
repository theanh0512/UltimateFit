package ultimate.fit.ultimatefit.db;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import ultimate.fit.ultimatefit.entity.Category;
import ultimate.fit.ultimatefit.entity.Exercise;

@Database(entities = {Exercise.class, Category.class}, version = 1)
public abstract class UltimateFitDatabase extends RoomDatabase {

    public abstract ExerciseDao exerciseDao();

    public abstract CategoryDao categoryDao();

}