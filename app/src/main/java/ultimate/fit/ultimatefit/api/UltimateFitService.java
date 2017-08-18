package ultimate.fit.ultimatefit.api;

import android.arch.lifecycle.LiveData;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Pham on 27/7/17.
 */

public interface UltimateFitService {
    @GET("categories-list/{lastUpdated}")
    LiveData<ApiResponse<List<CategoryApiResponse>>> getCategory(@Path("lastUpdated") long lastUpdated);

    @GET("exercises-list/{lastUpdated}/{pageNum}")
    LiveData<ApiResponse<ExerciseApiResponse>> getExercise(@Path("lastUpdated") long lastUpdated, @Path("pageNum") int pageNum);
}
