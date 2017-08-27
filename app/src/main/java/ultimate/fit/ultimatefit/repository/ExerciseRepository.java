package ultimate.fit.ultimatefit.repository;

import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import ultimate.fit.ultimatefit.AppExecutors;
import ultimate.fit.ultimatefit.api.ApiResponse;
import ultimate.fit.ultimatefit.api.ExerciseApiResponse;
import ultimate.fit.ultimatefit.api.ExerciseResponse;
import ultimate.fit.ultimatefit.api.UltimateFitService;
import ultimate.fit.ultimatefit.db.ExerciseDao;
import ultimate.fit.ultimatefit.db.UltimateFitDatabase;
import ultimate.fit.ultimatefit.entity.Exercise;
import ultimate.fit.ultimatefit.entity.Resource;
import ultimate.fit.ultimatefit.utils.SharedPreferenceHelper;

/** Created by Pham on 31/7/17. */
@Singleton
public class ExerciseRepository {
  private final UltimateFitDatabase db;

  private final ExerciseDao exerciseDao;
  private final AppExecutors appExecutors;
  UltimateFitService ultimateFitService;
  private Context context;

  @Inject
  public ExerciseRepository(
      UltimateFitDatabase db,
      ExerciseDao exerciseDao,
      UltimateFitService ultimateFitService,
      Context context,
      AppExecutors appExecutors) {
    this.db = db;
    this.exerciseDao = exerciseDao;
    this.ultimateFitService = ultimateFitService;
    this.context = context;
    this.appExecutors = appExecutors;
  }

  public LiveData<Resource<List<Exercise>>> getExercises(int categoryPk) {
    return new NetworkBoundResource<List<Exercise>, ExerciseApiResponse>(appExecutors) {
      @Override
      protected void saveCallResult(@NonNull ExerciseApiResponse item) {
        List<Exercise> exerciseList = new ArrayList<>();
        for (ExerciseResponse exerciseResponse : item.getArray()) {
          exerciseList.add(exerciseResponse.getExercise());
        }
        exerciseDao.insertExercises(exerciseList);
      }

      @Override
      protected boolean shouldFetch(@Nullable List<Exercise> data) {
        return data == null || data.isEmpty();
      }

      @NonNull
      @Override
      protected LiveData<List<Exercise>> loadFromDb() {
        return exerciseDao.loadExercisesWithCategoryPk(categoryPk);
      }

      @NonNull
      @Override
      protected LiveData<ApiResponse<ExerciseApiResponse>> createCall() {
        //Todo: implement last updated for should fetch and page num
        long lastUpdated =
            SharedPreferenceHelper.getInstance(context)
                .getLong(SharedPreferenceHelper.Key.LAST_EXERCISE_MODIFIED_DATE_LONG);
        if (lastUpdated == 0) {
          lastUpdated = 1000000;
        }
        return ultimateFitService.getExercise(lastUpdated, 1);
      }
    }.asLiveData();
  }

  public LiveData<Resource<List<Exercise>>> getAllExercises() {
    return new NetworkBoundResource<List<Exercise>, ExerciseApiResponse>(appExecutors) {
      @Override
      protected void saveCallResult(@NonNull ExerciseApiResponse item) {
        List<Exercise> exerciseList = new ArrayList<>();
        for (ExerciseResponse exerciseResponse : item.getArray()) {
          exerciseList.add(exerciseResponse.getExercise());
        }
        exerciseDao.insertExercises(exerciseList);
      }

      @Override
      protected boolean shouldFetch(@Nullable List<Exercise> data) {
        return data == null || data.isEmpty();
      }

      @NonNull
      @Override
      protected LiveData<List<Exercise>> loadFromDb() {
        return exerciseDao.loadExercises();
      }

      @NonNull
      @Override
      protected LiveData<ApiResponse<ExerciseApiResponse>> createCall() {
        //Todo: implement last updated for should fetch and page num
        long lastUpdated =
            SharedPreferenceHelper.getInstance(context)
                .getLong(SharedPreferenceHelper.Key.LAST_EXERCISE_MODIFIED_DATE_LONG);
        if (lastUpdated == 0) {
          lastUpdated = 1000000;
        }
        return ultimateFitService.getExercise(lastUpdated, 1);
      }
    }.asLiveData();
  }
}
