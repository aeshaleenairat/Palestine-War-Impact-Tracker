package com.mycompany.javaproject;

// File: ImpactAnalyzer.java
import java.util.List; // Might be needed for analyzeImpact or other methods later

public interface ImpactAnalyzer {

    /**
     * Analyzes the overall impact based on the available data.
     * The specific analysis (e.g., finding the most affected region)
     * will be implemented by the class that implements this interface.
     * It might return a String report, or a specific object, or print to console.
     */
    
    String analyzeImpact(); // This is quite broad. We'll define specifics in the implementing class.

    /**
     * Adds new impact data.
     * The parameters will depend on what kind of data is being added
     * (e.g., a new RegionData entry, a new Martyr record to an existing RegionData).
     * This might be overloaded or made more specific in the implementing class.
     * For a general interface, we might leave it without parameters or use generic ones.
     */
    // Option 1: Generic add - specific implementations will cast or handle 'data'
    // void add(Object data);

    // Option 2: More specific, but then the interface is tied to RegionData
    // void add(RegionData regionData);

    // Option 3: Keep it simple as per UML and let implementation decide specifics.
    // The UML just says "add()". Perhaps it's for adding a new RegionData entry.
    void add(); // We will make this more concrete in ImpactDataManager

    /**
     * Updates existing impact data.
     * Similar to add(), the specifics will be in the implementing class.
     * It could mean updating a RegionData entry, or a specific record within it.
     */
    // Option 1: Generic update
    // void update(Object data);

    // Option 2: More specific
    // void update(RegionData regionData);

    // Option 3: Keep it simple as per UML.
    void update(); // We will make this more concrete in ImpactDataManager
}