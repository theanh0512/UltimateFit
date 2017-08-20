/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ultimate.fit.ultimatefit.injection;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ultimate.fit.ultimatefit.api.UltimateFitService;
import ultimate.fit.ultimatefit.db.CategoryDao;
import ultimate.fit.ultimatefit.db.ExerciseDao;
import ultimate.fit.ultimatefit.db.UltimateFitDatabase;
import ultimate.fit.ultimatefit.utils.LiveDataCallAdapterFactory;

@Module(includes = ViewModelModule.class)
class AppModule {
  @Singleton
  @Provides
  Context provideContext(Application application) {
    return application;
  }

  @Singleton
  @Provides
  UltimateFitService provideUltimateFitService() {
    return new Retrofit.Builder()
        .baseUrl("http://ultimatefitbackend.azurewebsites.net/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(new LiveDataCallAdapterFactory())
        .build()
        .create(UltimateFitService.class);
  }

  @Singleton
  @Provides
  UltimateFitDatabase providesUltimateFitDatabase(Context context) {
    return Room.databaseBuilder(
            context.getApplicationContext(), UltimateFitDatabase.class, "ultimate_fit.db")
        .build();
  }

  @Singleton
  @Provides
  CategoryDao provideCategoryDao(UltimateFitDatabase db) {
    return db.categoryDao();
  }

  @Singleton
  @Provides
  ExerciseDao provideExerciseDao(UltimateFitDatabase db) {
    return db.exerciseDao();
  }
}
