package ultimate.fit.ultimatefit.model;

import java.util.List;

/**
 * Created by Pham on 1/5/2017.
 */

public class Workout {
    private String workoutBodyPart;
    private int dayNumber;
    private List<WorkoutExercise> workoutExercises;
    private String note;

    public Workout() {
    }

    public Workout(String workoutBodyPart, int dayNumber, List<WorkoutExercise> workoutExercises, String note) {
        this.workoutBodyPart = workoutBodyPart;
        this.dayNumber = dayNumber;
        this.workoutExercises = workoutExercises;
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getWorkoutBodyPart() {
        return workoutBodyPart;
    }

    public void setWorkoutBodyPart(String workoutBodyPart) {
        this.workoutBodyPart = workoutBodyPart;
    }

    public int getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public List<WorkoutExercise> getWorkoutExercises() {
        return workoutExercises;
    }

    public void setWorkoutExercises(List<WorkoutExercise> workoutExercises) {
        this.workoutExercises = workoutExercises;
    }
}
