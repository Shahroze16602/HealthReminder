package eu.smartpatient.mytherapy.models;

public class MedicineModel {
    private String id;
    private String name;
    private String unit;
    private int timeHours;
    private int timeMinutes;
    private int dose;
    private String isSkipped;

    public MedicineModel() {
    }

    public MedicineModel(String name, String unit, int timeHours, int timeMinutes, int dose, String isSkipped) {
        this.name = name;
        this.unit = unit;
        this.timeHours = timeHours;
        this.timeMinutes = timeMinutes;
        this.dose = dose;
        this.isSkipped = isSkipped;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getTimeHours() {
        return timeHours;
    }

    public void setTimeHours(int timeHours) {
        this.timeHours = timeHours;
    }

    public int getTimeMinutes() {
        return timeMinutes;
    }

    public void setTimeMinutes(int timeMinutes) {
        this.timeMinutes = timeMinutes;
    }

    public int getDose() {
        return dose;
    }

    public void setDose(int dose) {
        this.dose = dose;
    }

    public String getIsSkipped() {
        return isSkipped;
    }

    public void setIsSkipped(String isSkipped) {
        this.isSkipped = isSkipped;
    }
}
