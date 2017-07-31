package ultimate.fit.ultimatefit.injection;

import android.arch.persistence.room.Room;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ultimate.fit.ultimatefit.api.UltimateFitService;
import ultimate.fit.ultimatefit.db.UltimateFitDatabase;
import ultimate.fit.ultimatefit.repository.CategoryRepository;
import ultimate.fit.ultimatefit.repository.CategoryRepositoryImpl;
import ultimate.fit.ultimatefit.repository.ExerciseRepository;
import ultimate.fit.ultimatefit.repository.ExerciseRepositoryImpl;

/**
 * Created by Pham on 27/7/17.
 */
@Module
public class NetModule {
    String baseUrl;

    public NetModule(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Singleton
    @Provides
    UltimateFitService provideUltimateFitService() {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UltimateFitService.class);
    }

    @Provides
    @Singleton
    CategoryRepository providesCategoryRepository(UltimateFitDatabase ultimateFitDatabase) {
        return new CategoryRepositoryImpl(ultimateFitDatabase);
    }

    @Provides
    @Singleton
    ExerciseRepository providesExerciseRepository(UltimateFitDatabase ultimateFitDatabase) {
        return new ExerciseRepositoryImpl(ultimateFitDatabase);
    }

    @Provides
    @Singleton
    UltimateFitDatabase providesUltimatefitDatabase(Context context) {
        return Room.databaseBuilder(context.getApplicationContext(), UltimateFitDatabase.class, "ultimate_fit_db").build();
    }
}
