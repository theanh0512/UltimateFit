package ultimate.fit.ultimatefit.model;

/**
 * Created by Pham on 23/3/2017.
 */

public class Exercise {
    int exerciseId;
    String exerciseName;

    public Exercise() {
    }

    public Exercise(int exerciseId, String exerciseName) {
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
    }

    public int getExerciseId() {
        return exerciseId;
    }

    public void setExerciseId(int exerciseId) {
        this.exerciseId = exerciseId;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }
}
