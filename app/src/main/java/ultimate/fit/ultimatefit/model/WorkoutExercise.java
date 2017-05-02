package ultimate.fit.ultimatefit.model;

import java.util.List;

/**
 * Created by Pham on 1/5/2017.
 */

public class WorkoutExercise {
    private String firstExerciseName;
    private String firstExerciseImage;
    private int noOfSets;
    private int rep;
    private List<Set> sets;

    public WorkoutExercise(String firstExerciseName, String firstExerciseImage, int noOfSets, int rep, List<Set> sets) {
        this.firstExerciseName = firstExerciseName;
        this.firstExerciseImage = firstExerciseImage;
        this.noOfSets = noOfSets;
        this.rep = rep;
        this.sets = sets;
    }

    public WorkoutExercise() {
    }

    public String getFirstExerciseName() {
        return firstExerciseName;
    }

    public void setFirstExerciseName(String firstExerciseName) {
        this.firstExerciseName = firstExerciseName;
    }

    public String getFirstExerciseImage() {
        return firstExerciseImage;
    }

    public void setFirstExerciseImage(String firstExerciseImage) {
        this.firstExerciseImage = firstExerciseImage;
    }

    public int getNoOfSets() {
        return noOfSets;
    }

    public void setNoOfSets(int noOfSets) {
        this.noOfSets = noOfSets;
    }

    public int getRep() {
        return rep;
    }

    public void setRep(int rep) {
        this.rep = rep;
    }

    public List<Set> getSets() {
        return sets;
    }

    public void setSets(List<Set> sets) {
        this.sets = sets;
    }
}
