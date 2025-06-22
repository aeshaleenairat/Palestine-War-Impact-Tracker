package com.mycompany.javaproject;

// File: Martyr.java
public class Martyr extends Person {
    private String dateOfDeath;
    private String cause;
    private RegionData regionDataRef; // Represents the association with RegionData
    // private WarStats warStatsRef; // This might be better accessed via regionDataRef.getWarStats()

    // Constructor
    public Martyr(String name, int age, String dateOfDeath, String cause, RegionData regionDataRef) {
        super(name, age);
        this.dateOfDeath = dateOfDeath;
        this.cause = cause;
        this.regionDataRef = regionDataRef;
        // If we decide to store a direct reference to WarStats:
        // if (regionDataRef != null) {
        //     this.warStatsRef = regionDataRef.getWarStats();
        // }
    }

    // Getters
    public String getDateOfDeath() {
        return dateOfDeath;
    }

    public String getCause() {
        return cause;
    }

    public RegionData getRegionDataRef() {
        return regionDataRef;
    }

    // Setters
    public void setDateOfDeath(String dateOfDeath) {
        this.dateOfDeath = dateOfDeath;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public void setRegionDataRef(RegionData regionDataRef) {
        this.regionDataRef = regionDataRef;
        // if (regionDataRef != null) {
        //     this.warStatsRef = regionDataRef.getWarStats();
        // }
    }

    // Override displayInfo to include martyr-specific details
    @Override
    public void displayInfo() {
        super.displayInfo(); // Calls Person's displayInfo
        System.out.println("Date of Death: " + dateOfDeath);
        System.out.println("Cause of Death: " + cause);
        if (regionDataRef != null) {
            System.out.println("Region: " + regionDataRef.getRegion());
            System.out.println("Date of Record: " + regionDataRef.getDate());
        }
    }
}