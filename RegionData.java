package com.mycompany.javaproject;

// File: RegionData.java
import com.mycompany.javaproject.Border;
import java.util.ArrayList;
import java.util.List;

public class RegionData {
    private String date; // Format: "MM/YYYY" or "YYYY-MM"
    private String region; // e.g., "Gaza", "West Bank", "East Jerusalem"
    private WarStats warStats; // Aggregated stats for this region/date
    private String siegeDescription; // From project description

    // Lists to hold other impact types
    // We will define these classes shortly
    private List<EducationImpact> educationImpacts;
    private List<HealthcareImpact> healthcareImpacts;
    private List<Border> borderInfo; // Or a single Border object if a region has one primary border status entry

    // UML specified attributes:
    // region: String (covered)
    // date: String (covered)
    // warStats: WarStats (covered)

    // Constructor
    public RegionData(String date, String region) {
        this.date = date;
        this.region = region;
        this.warStats = new WarStats(); // Initialize with empty stats
        this.siegeDescription = ""; // Initialize empty
        this.educationImpacts = new ArrayList<>();
        this.healthcareImpacts = new ArrayList<>();
        this.borderInfo = new ArrayList<>(); // Assuming multiple border points or status changes over time for the region
    }

    // Getters
    public String getDate() {
        return date;
    }

    public String getRegion() {
        return region;
    }

    public WarStats getWarStats() {
        return warStats;
    }

    public String getSiegeDescription() {
        return siegeDescription;
    }

    public List<EducationImpact> getEducationImpacts() {
        return educationImpacts;
    }

    public List<HealthcareImpact> getHealthcareImpacts() {
        return healthcareImpacts;
    }

    public List<Border> getBorderInfo() {
        return borderInfo;
    }

    // Setters
    public void setDate(String date) {
        this.date = date;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setWarStats(WarStats warStats) {
        this.warStats = warStats;
    }

    public void setSiegeDescription(String siegeDescription) {
        this.siegeDescription = siegeDescription;
    }

    // Methods to add impact details
    public void addEducationImpact(EducationImpact impact) {
        if (impact != null) {
            this.educationImpacts.add(impact);
        }
    } 
    
     public void removeEducationImpact(EducationImpact impact) {
        this.educationImpacts.remove(impact);
    }
    
    public boolean updateEducationImpact(EducationImpact oldImpact, EducationImpact newImpactDetails) {
        int index = educationImpacts.indexOf(oldImpact);
        if (index != -1) {
            educationImpacts.set(index, newImpactDetails);
            return true;
        }
        return false;
    }
    
    
     public void removeHealthcareImpact(HealthcareImpact impact) {
        this.healthcareImpacts.remove(impact);
    }
     

    public void addHealthcareImpact(HealthcareImpact impact) {
        if (impact != null) {
            this.healthcareImpacts.add(impact);
        }
    }
    
    
       public boolean updateHealthcareImpact(HealthcareImpact oldImpact, HealthcareImpact newImpactDetails) {
        int index = healthcareImpacts.indexOf(oldImpact);
        if (index != -1) {
            healthcareImpacts.set(index, newImpactDetails); // Replace with the new object
            return true;
        }
        return false;
    }
    
     
       public void addBorderInfo(Border border) {
        if (border != null) {
            // It's good practice to ensure the border being added is correctly
            // associated with this RegionData instance.
            if (border.getRegionDataRef() == null || border.getRegionDataRef() != this) {
                // System.out.println("RegionData: Setting back-reference for Border '" + border.getBorderName() + "' to this RegionData.");
                border.setRegionDataRef(this); // Ensure back-reference is correct
            }
            this.borderInfo.add(border);
        }
    }

    public boolean removeBorderInfo(Border border) {
        return this.borderInfo.remove(border);
    }

    // A simple update: find by name (assuming names are unique for a given RegionData) and update status.
    // A more robust update would find the exact Border object.
    public boolean updateBorderStatus(String borderName, boolean isClosed) {
        for (Border border : borderInfo) {
            if (border.getBorderName().equalsIgnoreCase(borderName)) {
                border.setIsClosed(isClosed);
                return true;
            }
        }
        return false; // Border not found
    }
    // OR if you want to replace the object:
    public boolean updateBorderObject(Border oldBorder, Border newBorderDetails) {
        int index = borderInfo.indexOf(oldBorder);
        if (index != -1) {
            // Ensure the new border details also point to this region data
            if (newBorderDetails != null) {
                newBorderDetails.setRegionDataRef(this);
                borderInfo.set(index, newBorderDetails);
                return true;
            }
        }
        return false;
    }
     
     

    // UML methods for RegionData: set(), get() (covered by standard getters/setters)
    // addRegion() - This method name seems a bit off for a class named RegionData.
    //             It might be intended for a manager class that holds multiple RegionData objects.
    //             Or, it could mean adding sub-region details if applicable, but the spec seems to point to Gaza, WB, EJ as the regions.
    //             Let's assume for now this might be a misplacement in the UML for this specific class,
    //             or it relates to initializing/setting the region name which the constructor and setRegion handle.

    // updateState() - This is quite generic. It could mean updating the siege description,
    //               or triggering updates in its contained objects.
    //               Let's define it to update the siege description for now.
    public void updateState(String newSiegeDescription) {
        this.setSiegeDescription(newSiegeDescription);
        System.out.println("State (siege description) updated for region " + region + " on " + date);
    }


    // Display method for overall region data (can be expanded)
    public void displayRegionSummary() {
        System.out.println("========================================");
        System.out.println("Region: " + region + " | Date: " + date);
        System.out.println("----------------------------------------");
        if (warStats != null) {
            warStats.display();
        }
        System.out.println("Siege Status: " + (siegeDescription.isEmpty() ? "Not specified" : siegeDescription));

        System.out.println("\n--- Education Impacts (" + educationImpacts.size() + ") ---");
        if (educationImpacts.isEmpty()) {
            System.out.println("No education impact data available.");
        } else {
            for (EducationImpact ei : educationImpacts) {
                ei.display(); // Assuming EducationImpact will have a display method
            }
        }

        System.out.println("\n--- Healthcare Impacts (" + healthcareImpacts.size() + ") ---");
        if (healthcareImpacts.isEmpty()) {
            System.out.println("No healthcare impact data available.");
        } else {
            for (HealthcareImpact hi : healthcareImpacts) {
                hi.displayStats(); // As per UML
            }
        }

        System.out.println("\n--- Border Information (" + borderInfo.size() + ") ---");
        if (borderInfo.isEmpty()) {
            System.out.println("No border information available.");
        } else {
            for (Border b : borderInfo) {
                b.displayStatus(); // As per UML
            }
        }
        System.out.println("========================================");
    }
}