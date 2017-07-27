package ultimate.fit.ultimatefit.api;

import android.arch.lifecycle.LiveData;

import retrofit2.http.GET;
import retrofit2.http.Path;
import ultimate.fit.ultimatefit.model.Category;

/**
 * Created by Pham on 27/7/17.
 */

public interface UltimateFitService {
    @GET("categories-list/{lastUpdated}")
    LiveData<Category> getCategory(@Path("lastUpdated") long lastUpdated);
}
