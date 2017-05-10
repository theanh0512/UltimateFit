package ultimate.fit.ultimatefit.model;

import java.util.List;

/**
 * Created by Pham on 29/4/2017.
 */

public class Plan {
    private String planName;
    private String creatorEmail;
    private String planGoal;
    private String planUuid;
    private int numOfWeek;
    private int dayPerWeek;
    private List<Workout> workouts;

    public Plan() {
    }

    public Plan(String planName, String planUuid, String creatorEmail, String planGoal, int numOfWeek, int dayPerWeek, List<Workout> workouts) {
        this.planName = planName;
        this.planUuid = planUuid;
        this.creatorEmail = creatorEmail;
        this.planGoal = planGoal;
        this.numOfWeek = numOfWeek;
        this.dayPerWeek = dayPerWeek;
        this.workouts = workouts;
    }

    public String getPlanUuid() {
        return planUuid;
    }

    public void setPlanUuid(String planUuid) {
        this.planUuid = planUuid;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPlanGoal() {
        return planGoal;
    }

    public void setPlanGoal(String planGoal) {
        this.planGoal = planGoal;
    }

    public int getNumOfWeek() {
        return numOfWeek;
    }

    public void setNumOfWeek(int numOfWeek) {
        this.numOfWeek = numOfWeek;
    }

    public int getDayPerWeek() {
        return dayPerWeek;
    }

    public void setDayPerWeek(int dayPerWeek) {
        this.dayPerWeek = dayPerWeek;
    }

    public List<Workout> getWorkouts() {
        return workouts;
    }

    public void setWorkouts(List<Workout> workouts) {
        this.workouts = workouts;
    }
}
