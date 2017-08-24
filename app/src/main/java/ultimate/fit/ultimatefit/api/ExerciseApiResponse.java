package ultimate.fit.ultimatefit.api;

import java.util.List;

/**
 * Created by Pham on 17/8/17.
 */

public class ExerciseApiResponse {
    private List<ExerciseResponse> array;
    private int page;

    public List<ExerciseResponse> getArray() {
        return array;
    }

    public int getPage() {
        return page;
    }
}
