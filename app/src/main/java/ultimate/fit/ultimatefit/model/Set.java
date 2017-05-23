package ultimate.fit.ultimatefit.model;

/**
 * Created by Pham on 1/5/2017.
 */

public class Set {
    private String exerciseName;
    private int setNumber;
    private int exerciseNumber;
    private int noOfRep;
    private double weightRatio;

    public Set(String exerciseName, int setNumber, int exerciseNumber, int noOfRep, double weightRatio) {

        this.exerciseName = exerciseName;
        this.setNumber = setNumber;
        this.exerciseNumber = exerciseNumber;
        this.noOfRep = noOfRep;
        this.weightRatio = weightRatio;
    }

    public Set() {
    }

    public double getWeightRatio() {
        return weightRatio;
    }

    public void setWeightRatio(double weightRatio) {
        this.weightRatio = weightRatio;
    }

    public int getNoOfRep() {
        return noOfRep;
    }

    public void setNoOfRep(int noOfRep) {
        this.noOfRep = noOfRep;
    }

    public String getExerciseName() {
        return exerciseName;
    }

    public void setExerciseName(String exerciseName) {
        this.exerciseName = exerciseName;
    }

    public int getSetNumber() {
        return setNumber;
    }

    public void setSetNumber(int setNumber) {
        this.setNumber = setNumber;
    }

    public int getExerciseNumber() {
        return exerciseNumber;
    }

    public void setExerciseNumber(int exerciseNumber) {
        this.exerciseNumber = exerciseNumber;
    }
}
