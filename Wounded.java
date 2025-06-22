package com.mycompany.javaproject;

// File: Wounded.java
public class Wounded extends Person {
    private String injuryType;
    private boolean permanentDisability;
    private boolean hospitalized;
    private RegionData regionDataRef; // Association with RegionData
    // private WarStats warStatsRef; // Access via regionDataRef

    // Constructor
    public Wounded(String name, int age, String injuryType, boolean permanentDisability, boolean hospitalized, RegionData regionDataRef) {
        super(name, age);
        this.injuryType = injuryType;
        this.permanentDisability = permanentDisability;
        this.hospitalized = hospitalized;
        this.regionDataRef = regionDataRef;
    }

    // Getters
    public String getInjuryType() {
        return injuryType;
    }

    public boolean isPermanentDisability() {
        return permanentDisability;
    }

    public boolean isHospitalized() {
        return hospitalized;
    }

    public RegionData getRegionDataRef() {
        return regionDataRef;
    }

    // Setters
    public void setInjuryType(String injuryType) {
        this.injuryType = injuryType;
    }

    public void setPermanentDisability(boolean permanentDisability) {
        this.permanentDisability = permanentDisability;
    }

    public void setHospitalized(boolean hospitalized) {
        this.hospitalized = hospitalized;
    }

    public void setRegionDataRef(RegionData regionDataRef) {
        this.regionDataRef = regionDataRef;
    }

    // As per UML
    public void updateState(boolean hospitalized, String injuryType) {
        this.hospitalized = hospitalized;
        this.injuryType = injuryType; // Or however state needs to be updated
        System.out.println("Wounded state updated for " + getName());
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Injury Type: " + injuryType);
        System.out.println("Permanent Disability: " + (permanentDisability ? "Yes" : "No"));
        System.out.println("Hospitalized: " + (hospitalized ? "Yes" : "No"));
        if (regionDataRef != null) {
            System.out.println("Region: " + regionDataRef.getRegion());
            System.out.println("Date of Record: " + regionDataRef.getDate());
        }
    }
}