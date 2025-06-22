package com.mycompany.javaproject;

// File: Prisoner.java
public class Prisoner extends Person {
    private int yearsInPrison; // Or perhaps a startDate and endDate would be more flexible
    private boolean released;
    private RegionData regionDataRef; // Association with RegionData
    // private WarStats warStatsRef; // Access via regionDataRef

    // Constructor
    public Prisoner(String name, int age, int yearsInPrison, boolean released, RegionData regionDataRef) {
        super(name, age);
        this.yearsInPrison = yearsInPrison;
        this.released = released;
        this.regionDataRef = regionDataRef;
    }

    // Getters
    public int getYearsInPrison() {
        return yearsInPrison;
    }

    public boolean isReleased() {
        return released;
    }

    public RegionData getRegionDataRef() {
        return regionDataRef;
    }

    // Setters
    public void setYearsInPrison(int yearsInPrison) {
        this.yearsInPrison = yearsInPrison;
    }

    public void setReleased(boolean released) {
        this.released = released;
    }

    public void setRegionDataRef(RegionData regionDataRef) {
        this.regionDataRef = regionDataRef;
    }

    // As per UML
    public void updateState(boolean releasedStatus, int newYearsInPrison) {
        this.released = releasedStatus;
        this.yearsInPrison = newYearsInPrison; // Or adjust based on time
        System.out.println("Prisoner state updated for " + getName());
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Years in Prison: " + yearsInPrison);
        System.out.println("Released: " + (released ? "Yes" : "No"));
        if (regionDataRef != null) {
            System.out.println("Region: " + regionDataRef.getRegion());
            System.out.println("Date of Record: " + regionDataRef.getDate());
        }
    }
}