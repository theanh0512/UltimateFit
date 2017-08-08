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
import android.arch.lifecycle.ViewModelProvider;
import android.arch.persistence.room.Room;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ultimate.fit.ultimatefit.api.UltimateFitService;
import ultimate.fit.ultimatefit.db.UltimateFitDatabase;
import ultimate.fit.ultimatefit.viewmodel.UltimateFitViewModelFactory;

@Module(subcomponents = ViewModelSubComponent.class)
public class AppModule {
    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application;
    }

    @Singleton
    @Provides
    UltimateFitService provideUltimateFitService() {
        return new Retrofit.Builder()
                .baseUrl("http://ultimatefitbackend.azurewebsites.net/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UltimateFitService.class);
    }

    @Singleton
    @Provides
    ViewModelProvider.Factory provideViewModelFactory(
            ViewModelSubComponent.Builder viewModelSubComponent) {

        return new UltimateFitViewModelFactory(viewModelSubComponent.build());
    }

    @Provides
    @Singleton
    UltimateFitDatabase providesUltimateFitDatabase(Context context) {
        return Room.databaseBuilder(context.getApplicationContext(), UltimateFitDatabase.class, "ultimate_fit.db").build();
    }
}
