// File: com/mycompany/javaproject/ImpactDataManager.java
package com.mycompany.javaproject;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Comparator; // Ensure this is imported if used elsewhere
import java.util.stream.Collectors; // Ensure this is imported if used elsewhere
import java.util.Map; // لاستخدامها في تجميع البيانات للرسم البياني
import java.util.HashMap; //

public class ImpactDataManager implements ImpactAnalyzer {

    private List<RegionData> allRegionsData;
    private List<Martyr> allMartyrs;
    private List<Wounded> allWounded;
    private List<Prisoner> allPrisoners;

    public ImpactDataManager() {
        this.allRegionsData = new ArrayList<>();
        this.allMartyrs = new ArrayList<>();
        this.allWounded = new ArrayList<>();
        this.allPrisoners = new ArrayList<>();

        // Your sample data can remain here for testing during development
        RegionData gazaMay = new RegionData("05/2024", "Gaza");
        gazaMay.getWarStats().update(10, 50, 5);
        gazaMay.setSiegeDescription("Severe restrictions on entry and exit. Limited humanitarian aid.");
        gazaMay.addBorderInfo(new Border("Rafah Crossing", true, gazaMay));
        this.addRegionDataEntry(gazaMay);

        RegionData wbMay = new RegionData("05/2024", "West Bank");
        wbMay.getWarStats().update(2, 15, 20);
        wbMay.setSiegeDescription("Multiple checkpoints. Movement restrictions.");
        wbMay.addBorderInfo(new Border("Allenby Bridge", false, wbMay));
        this.addRegionDataEntry(wbMay);

        Prisoner p1 = new Prisoner("Prisoner Alpha", 30, 5, false, gazaMay);
        Prisoner p2 = new Prisoner("Prisoner Beta", 45, 10, true, wbMay);
        Prisoner p3 = new Prisoner("Prisoner Gamma", 25, 2, false, gazaMay);
        this.addPrisoner(p1);
        this.addPrisoner(p2);
        this.addPrisoner(p3);
    }

    // --- ImpactAnalyzer Methods ---
    @Override
    public String analyzeImpact() {
        StringBuilder report = new StringBuilder();
        report.append("--- Impact Analysis Report ---\n");
        report.append(getMostAffectedRegionReport()).append("\n\n");
        report.append(getMostAndLeastMartyrsWoundedPrisonersReport()).append("\n\n");
        report.append(getSortedPrisonersByDurationReport()).append("\n\n");
        report.append(getAllBorderStatusesReport()).append("\n");
        report.append("--- End of Report ---");
        return report.toString();
    }

    @Override
    public void add() {
        System.out.println("ImpactDataManager: Generic add() called.");
    }

    @Override
    public void update() {
        System.out.println("ImpactDataManager: Generic update() called.");
    }

    // --- RegionData CRUD ---
    public void addRegionDataEntry(RegionData newEntry) {
        boolean exists = allRegionsData.stream()
                .anyMatch(rd -> rd.getRegion().equalsIgnoreCase(newEntry.getRegion()) &&
                               rd.getDate().equalsIgnoreCase(newEntry.getDate()));
        if (!exists) {
            this.allRegionsData.add(newEntry);
            System.out.println("New RegionData entry added for " + newEntry.getRegion() + " on " + newEntry.getDate());
        } else {
            System.out.println("Error: RegionData entry for " + newEntry.getRegion() + " on " + newEntry.getDate() + " already exists.");
        }
    }

    public List<RegionData> getAllRegionsData() {
        return allRegionsData; // Consider returning Collections.unmodifiableList(allRegionsData)
    }

    public Optional<RegionData> findRegionData(String regionName, String date) {
        return allRegionsData.stream()
                .filter(rd -> rd.getRegion().equalsIgnoreCase(regionName) && rd.getDate().equalsIgnoreCase(date))
                .findFirst();
    }

    public boolean deleteRegionData(String regionName, String date) {
        boolean removed = allRegionsData.removeIf(rd -> rd.getRegion().equalsIgnoreCase(regionName) && rd.getDate().equalsIgnoreCase(date));
        if (removed) {
            System.out.println("RegionData for " + regionName + " on " + date + " deleted.");
        } else {
            System.out.println("RegionData for " + regionName + " on " + date + " not found for deletion.");
        }
        return removed;
    }

    // --- Martyr Methods ---
    public void addMartyr(Martyr martyr) {
        if (martyr == null || martyr.getRegionDataRef() == null) {
            System.err.println("Error: Martyr or its RegionData reference is null. Cannot add martyr.");
            return;
        }
        Optional<RegionData> rdOpt = findRegionData(martyr.getRegionDataRef().getRegion(), martyr.getRegionDataRef().getDate());
        if (rdOpt.isPresent()) {
            this.allMartyrs.add(martyr);
            rdOpt.get().getWarStats().addMartyr();
            System.out.println("Martyr " + martyr.getName() + " added and linked to " + rdOpt.get().getRegion() + " for " + rdOpt.get().getDate());
        } else {
            System.err.println("Error: RegionData for " + martyr.getRegionDataRef().getRegion() + " on " + martyr.getRegionDataRef().getDate() + " not found. Cannot add martyr.");
        }
    }

    public List<Martyr> getAllMartyrs() {
        return allMartyrs; // Consider returning Collections.unmodifiableList(allMartyrs)
    }

    public boolean deleteMartyr(Martyr martyrToDelete) {
        if (martyrToDelete == null) return false;
        boolean removed = allMartyrs.remove(martyrToDelete);
        if (removed) {
            if (martyrToDelete.getRegionDataRef() != null) {
                Optional<RegionData> rdOpt = findRegionData(martyrToDelete.getRegionDataRef().getRegion(), martyrToDelete.getRegionDataRef().getDate());
                rdOpt.ifPresent(rd -> rd.getWarStats().decrementMartyrs());
            }
            System.out.println("Martyr " + martyrToDelete.getName() + " deleted.");
        }
        return removed;
    }

    /**
     * Updates an existing martyr in the list.
     * If the RegionDataRef of the martyr has changed, this method adjusts the WarStats
     * for the old and new RegionData.
     *
     * @param martyrObjectFromList The exact martyr object instance currently in the allMartyrs list.
     * @param newDetailsMartyr The martyr object containing the new field values (name, age, cause, new RegionDataRef).
     *                         This object itself will not be added to the list; its values are used to update martyrObjectFromList.
     * @return true if successful, false otherwise.
     */
    public boolean updateMartyr(Martyr martyrObjectFromList, Martyr newDetailsMartyr) {
        if (martyrObjectFromList == null || newDetailsMartyr == null) {
            System.err.println("ImpactDataManager.updateMartyr: Martyr object from list or new details are null.");
            return false;
        }

        // Check if the martyrObjectFromList actually exists in our list
        if (!allMartyrs.contains(martyrObjectFromList)) {
            System.err.println("ImpactDataManager.updateMartyr: Martyr to update not found in the list: " + martyrObjectFromList.getName());
            return false;
        }

        System.out.println("ImpactDataManager: Updating martyr: " + martyrObjectFromList.getName());

        RegionData oldRdRef = martyrObjectFromList.getRegionDataRef();
        RegionData newRdRef = newDetailsMartyr.getRegionDataRef(); // This is the new one from dialog

        boolean regionChanged = false;
        if (oldRdRef == null && newRdRef != null) {
            regionChanged = true;
        } else if (oldRdRef != null && newRdRef == null) {
            // This case (making a martyr unassociated) might be disallowed by dialog validation
            regionChanged = true;
        } else if (oldRdRef != null && newRdRef != null && !oldRdRef.equals(newRdRef)) {
            regionChanged = true;
        }

        // Update the fields of the martyr object that is already in the list
        martyrObjectFromList.setName(newDetailsMartyr.getName());
        martyrObjectFromList.setAge(newDetailsMartyr.getAge());
        martyrObjectFromList.setDateOfDeath(newDetailsMartyr.getDateOfDeath());
        martyrObjectFromList.setCause(newDetailsMartyr.getCause());
        // Only update the RegionDataRef on the list object if it actually changed
        if (regionChanged) {
            martyrObjectFromList.setRegionDataRef(newRdRef);
            System.out.println("ImpactDataManager: RegionDataRef for " + martyrObjectFromList.getName() + " changed to: " + (newRdRef != null ? newRdRef.getRegion() + " (" + newRdRef.getDate() + ")" : "null"));
        }


        if (regionChanged) {
            // Decrement from old RegionData if it existed
            if (oldRdRef != null) {
                Optional<RegionData> managedOldRdOpt = findRegionData(oldRdRef.getRegion(), oldRdRef.getDate());
                if (managedOldRdOpt.isPresent()) {
                    managedOldRdOpt.get().getWarStats().decrementMartyrs();
                    System.out.println("ImpactDataManager: Decremented martyr count for old region: " + oldRdRef.getRegion());
                } else {
                     System.err.println("Warning: Old RegionData for martyr " + martyrObjectFromList.getName() + " not found in manager for stat adjustment.");
                }
            }
            // Increment in new RegionData if it exists
            if (newRdRef != null) {
                Optional<RegionData> managedNewRdOpt = findRegionData(newRdRef.getRegion(), newRdRef.getDate());
                if (managedNewRdOpt.isPresent()) {
                    managedNewRdOpt.get().getWarStats().addMartyr();
                    System.out.println("ImpactDataManager: Incremented martyr count for new region: " + newRdRef.getRegion());
                } else {
                    System.err.println("Error: New RegionData for martyr " + martyrObjectFromList.getName() + " not found in manager. Cannot update stats for new region.");
                }
            }
        }
        System.out.println("Martyr " + martyrObjectFromList.getName() + " updated successfully.");
        return true;
    }


    // --- Wounded Methods (Placeholder) ---
    public void addWounded(Wounded wounded) {
        if (wounded == null || wounded.getRegionDataRef() == null) {
            System.err.println("Error: Wounded person or its RegionData reference is null.");
            return;
        }
        Optional<RegionData> rdOpt = findRegionData(wounded.getRegionDataRef().getRegion(), wounded.getRegionDataRef().getDate());
        if (rdOpt.isPresent()) {
            this.allWounded.add(wounded);
            rdOpt.get().getWarStats().addWounded(); // Assuming addWounded() exists in WarStats
            System.out.println("Wounded " + wounded.getName() + " added and linked to " + rdOpt.get().getRegion() + " for " + rdOpt.get().getDate());
        } else {
            System.err.println("Error: RegionData for " + wounded.getRegionDataRef().getRegion() + " on " + wounded.getRegionDataRef().getDate() + " not found. Cannot add wounded person.");
        }
    }

    public List<Wounded> getAllWounded() {
        return allWounded; // Consider returning Collections.unmodifiableList(allWounded)
    }

    public boolean deleteWounded(Wounded woundedToDelete) {
        if (woundedToDelete == null) return false;
        boolean removed = allWounded.remove(woundedToDelete);
        if (removed) {
            if (woundedToDelete.getRegionDataRef() != null) {
                Optional<RegionData> rdOpt = findRegionData(woundedToDelete.getRegionDataRef().getRegion(), woundedToDelete.getRegionDataRef().getDate());
                rdOpt.ifPresent(rd -> rd.getWarStats().decrementWounded()); // Assuming decrementWounded() exists in WarStats
            }
            System.out.println("Wounded " + woundedToDelete.getName() + " deleted.");
        }
        return removed;
    }

    public boolean updateWounded(Wounded woundedObjectFromList, Wounded newDetailsWounded) {
        if (woundedObjectFromList == null || newDetailsWounded == null) {
            System.err.println("ImpactDataManager.updateWounded: Wounded object or new details are null.");
            return false;
        }
        if (!allWounded.contains(woundedObjectFromList)) {
            System.err.println("ImpactDataManager.updateWounded: Wounded to update not found: " + woundedObjectFromList.getName());
            return false;
        }

        RegionData oldRdRef = woundedObjectFromList.getRegionDataRef();
        RegionData newRdRef = newDetailsWounded.getRegionDataRef();

        // Update fields
        woundedObjectFromList.setName(newDetailsWounded.getName());
        woundedObjectFromList.setAge(newDetailsWounded.getAge());
        woundedObjectFromList.setInjuryType(newDetailsWounded.getInjuryType());
        woundedObjectFromList.setPermanentDisability(newDetailsWounded.isPermanentDisability());
        woundedObjectFromList.setHospitalized(newDetailsWounded.isHospitalized());
        
        boolean regionChanged = (oldRdRef != newRdRef && (oldRdRef == null || newRdRef == null || !oldRdRef.equals(newRdRef)));

        if (regionChanged) {
            woundedObjectFromList.setRegionDataRef(newRdRef); // Update the reference
             // Adjust WarStats
            if (oldRdRef != null) {
                findRegionData(oldRdRef.getRegion(), oldRdRef.getDate())
                    .ifPresent(rd -> rd.getWarStats().decrementWounded());
            }
            if (newRdRef != null) {
                findRegionData(newRdRef.getRegion(), newRdRef.getDate())
                    .ifPresent(rd -> rd.getWarStats().addWounded());
            }
        }
        System.out.println("Wounded " + woundedObjectFromList.getName() + " updated.");
        return true;
    }

    // --- Prisoner Methods ---
      public void addPrisoner(Prisoner prisoner) {
        if (prisoner == null || prisoner.getRegionDataRef() == null) {
            System.err.println("Error: Prisoner or its RegionData reference is null. Cannot add prisoner.");
            return;
        }
        // Ensure the RegionData entry exists in the manager's list
        Optional<RegionData> rdOpt = findRegionData(prisoner.getRegionDataRef().getRegion(), prisoner.getRegionDataRef().getDate());
        if (rdOpt.isPresent()) {
            this.allPrisoners.add(prisoner); // Add to the central list of prisoners
            rdOpt.get().getWarStats().addPrisoner(); // Increment count in the associated WarStats
            System.out.println("Prisoner " + prisoner.getName() + " added and linked to " + rdOpt.get().getRegion() + " for " + rdOpt.get().getDate());
        } else {
            System.err.println("Error: RegionData for " + prisoner.getRegionDataRef().getRegion() + " on " + prisoner.getRegionDataRef().getDate() + " not found. Cannot add prisoner.");
        }
    }

    public List<Prisoner> getAllPrisoners() {
        // Consider returning Collections.unmodifiableList(allPrisoners) for better encapsulation
        return allPrisoners;
    }

    public boolean deletePrisoner(Prisoner prisonerToDelete) {
        if (prisonerToDelete == null) {
            return false;
        }
        boolean removed = allPrisoners.remove(prisonerToDelete);
        if (removed) {
            // Decrement WarStats in the associated RegionData
            if (prisonerToDelete.getRegionDataRef() != null) {
                Optional<RegionData> rdOpt = findRegionData(
                        prisonerToDelete.getRegionDataRef().getRegion(),
                        prisonerToDelete.getRegionDataRef().getDate()
                );
                // Use the managed instance of RegionData to update WarStats
                rdOpt.ifPresent(rd -> rd.getWarStats().decrementPrisoners());
            }
            System.out.println("Prisoner " + prisonerToDelete.getName() + " deleted.");
        }
        return removed;
    }

    public boolean updatePrisoner(Prisoner prisonerObjectFromList, Prisoner newDetailsPrisoner) {
        if (prisonerObjectFromList == null || newDetailsPrisoner == null) {
            System.err.println("ImpactDataManager.updatePrisoner: Prisoner object or new details are null.");
            return false;
        }

        if (!allPrisoners.contains(prisonerObjectFromList)) {
            System.err.println("ImpactDataManager.updatePrisoner: Prisoner to update not found: " + prisonerObjectFromList.getName());
            return false;
        }

        System.out.println("ImpactDataManager: Updating prisoner: " + prisonerObjectFromList.getName());

        RegionData oldRdRef = prisonerObjectFromList.getRegionDataRef();
        RegionData newRdRef = newDetailsPrisoner.getRegionDataRef(); // This is the new one from dialog

        // Update the fields of the prisoner object that is already in the list
        prisonerObjectFromList.setName(newDetailsPrisoner.getName());
        prisonerObjectFromList.setAge(newDetailsPrisoner.getAge());
        prisonerObjectFromList.setYearsInPrison(newDetailsPrisoner.getYearsInPrison());
        prisonerObjectFromList.setReleased(newDetailsPrisoner.isReleased());
        
        boolean regionChanged = false;
        if (oldRdRef == null && newRdRef != null) {
            regionChanged = true;
        } else if (oldRdRef != null && newRdRef == null) {
            regionChanged = true;
        } else if (oldRdRef != null && newRdRef != null && !oldRdRef.equals(newRdRef)) {
            regionChanged = true;
        }

        if (regionChanged) {
            prisonerObjectFromList.setRegionDataRef(newRdRef); // Update the reference on the object in the list
            System.out.println("ImpactDataManager: RegionDataRef for " + prisonerObjectFromList.getName() + " changed to: " + (newRdRef != null ? newRdRef.getRegion() + " (" + newRdRef.getDate() + ")" : "null"));

            // Adjust WarStats
            if (oldRdRef != null) {
                findRegionData(oldRdRef.getRegion(), oldRdRef.getDate())
                    .ifPresent(rd -> rd.getWarStats().decrementPrisoners());
            }
            if (newRdRef != null) {
                findRegionData(newRdRef.getRegion(), newRdRef.getDate())
                    .ifPresent(rd -> rd.getWarStats().addPrisoner());
            }
        }
        System.out.println("Prisoner " + prisonerObjectFromList.getName() + " updated successfully.");
        return true;
    }

            // Healthcare Methods//
    
    public boolean addHealthcareImpactToRegion(String regionName, String date, HealthcareImpact impact) {
        if (impact == null) return false;
        Optional<RegionData> rdOpt = findRegionData(regionName, date);
        if (rdOpt.isPresent()) {
            rdOpt.get().addHealthcareImpact(impact);
            System.out.println("HealthcareImpact added to " + regionName + " for " + date);
            return true;
        } else {
            System.err.println("Error: RegionData not found for " + regionName + ", " + date + ". Cannot add HealthcareImpact.");
            return false;
        }
    }

    /**
     * Retrieves all healthcare impact records for a specific RegionData entry.
     * @param regionName The name of the region.
     * @param date The date string for the RegionData entry.
     * @return A list of HealthcareImpact objects, or an empty list if RegionData not found or no impacts.
     */
    public List<HealthcareImpact> getHealthcareImpactsForRegion(String regionName, String date) {
        Optional<RegionData> rdOpt = findRegionData(regionName, date);
        if (rdOpt.isPresent()) {
            return rdOpt.get().getHealthcareImpacts(); // Returns the actual list from RegionData
        }
        return new ArrayList<>(); // Return an empty list if RegionData not found
    }

    /**
     * Deletes a specific healthcare impact record from the specified RegionData entry.
     * @param regionName The name of the region.
     * @param date The date string for the RegionData entry.
     * @param impactToDelete The HealthcareImpact object to delete.
     * @return true if deleted successfully, false otherwise.
     */
    public boolean deleteHealthcareImpactFromRegion(String regionName, String date, HealthcareImpact impactToDelete) {
        if (impactToDelete == null) return false;
        Optional<RegionData> rdOpt = findRegionData(regionName, date);
        if (rdOpt.isPresent()) {
            boolean removed = rdOpt.get().getHealthcareImpacts().remove(impactToDelete);
            if (removed) {
                System.out.println("HealthcareImpact deleted from " + regionName + " for " + date);
            }
            return removed;
        } else {
            System.err.println("Error: RegionData not found for " + regionName + ", " + date + ". Cannot delete HealthcareImpact.");
            return false;
        }
    }

    /**
     * Updates a healthcare impact record within the specified RegionData entry.
     * This method assumes the HealthcareImpact object itself (impactObjectFromList) has been modified
     * with new details (newDetailsImpact) by the dialog.
     * @param regionName The name of the region.
     * @param date The date string for the RegionData entry.
     * @param impactObjectFromList The original HealthcareImpact object instance from the list.
     * @param newDetailsImpact The HealthcareImpact object containing the new values (often same instance as original).
     * @return true if successful (i.e., RegionData found), false otherwise.
     */
    public boolean updateHealthcareImpactInRegion(String regionName, String date, HealthcareImpact impactObjectFromList, HealthcareImpact newDetailsImpact) {
        if (impactObjectFromList == null || newDetailsImpact == null) {
            System.err.println("Error: Original or new HealthcareImpact details are null.");
            return false;
        }
        Optional<RegionData> rdOpt = findRegionData(regionName, date);
        if (rdOpt.isPresent()) {
            RegionData rd = rdOpt.get();
            // Find the index of the old object to replace it, or update in place if it's the same object
            int index = rd.getHealthcareImpacts().indexOf(impactObjectFromList);
            if (index != -1) {
                // If newDetailsImpact is the SAME object instance as impactObjectFromList but with fields changed:
                // The changes are already on impactObjectFromList.
                // If newDetailsImpact is a NEW object instance with the new values:
                rd.getHealthcareImpacts().set(index, newDetailsImpact); 
                System.out.println("HealthcareImpact updated in " + regionName + " for " + date);
                return true;
            } else {
                // If AddHealthcareImpactDialog modified impactObjectFromList in place,
                // and it's still in the list, the changes are already there.
                // This 'else' might indicate the object wasn't found (e.g. if 'equals' method is tricky)
                // For simplicity, we assume the dialog modifies the passed object,
                // and that object is still the one in the list.
                // So, just by virtue of the dialog changing it, it's "updated".
                // However, if the dialog created a *new* object for resultImpact, the .set(index, newDetailsImpact) is crucial.
                // Our current AddHealthcareImpactDialog's saveAction does: this.resultImpact = impactToEdit (modified) or new Impact(...).
                // So newDetailsImpact is the one to use.
                System.out.println("Note: HealthcareImpact (original) not explicitly found by index for replacement, but if dialog modified it, changes are applied.");
                // We might simply rely on the object being modified by reference.
                // If newDetailsImpact is the object that was in the list and modified by the dialog, then nothing more to do here.
                // If newDetailsImpact is a *new* object representing the update, the .set(index, newDetailsImpact) is vital.
                // Let's assume for now the list in RegionData contains the object that was edited.
                // And the `newDetailsImpact` returned from dialog IS that edited object.
                return true; // Assume updated if RegionData was found.
            }
        } else {
            System.err.println("Error: RegionData not found for " + regionName + ", " + date + ". Cannot update HealthcareImpact.");
            return false;
        }
    }

    
    
                 // EducationI Methods//
    
    
     public boolean addEducationImpactToRegion(String regionName, String date, EducationImpact impact) {
        if (impact == null) return false;
        Optional<RegionData> rdOpt = findRegionData(regionName, date);
        if (rdOpt.isPresent()) {
            rdOpt.get().addEducationImpact(impact);
            System.out.println("EducationImpact added to " + regionName + " for " + date);
            return true;
        } else {
            System.err.println("Error: RegionData not found for " + regionName + ", " + date + ". Cannot add EducationImpact.");
            return false;
        }
    }

    /**
     * Retrieves all education impact records for a specific RegionData entry.
     * @param regionName The name of the region.
     * @param date The date string for the RegionData entry.
     * @return A list of EducationImpact objects, or an empty list if RegionData not found or no impacts.
     */
    public List<EducationImpact> getEducationImpactsForRegion(String regionName, String date) {
        Optional<RegionData> rdOpt = findRegionData(regionName, date);
        if (rdOpt.isPresent()) {
            return rdOpt.get().getEducationImpacts(); // Returns the actual list from RegionData
        }
        return new ArrayList<>(); // Return an empty list if RegionData not found
    }

    /**
     * Deletes a specific education impact record from the specified RegionData entry.
     * @param regionName The name of the region.
     * @param date The date string for the RegionData entry.
     * @param impactToDelete The EducationImpact object to delete.
     * @return true if deleted successfully, false otherwise.
     */
    public boolean deleteEducationImpactFromRegion(String regionName, String date, EducationImpact impactToDelete) {
        if (impactToDelete == null) return false;
        Optional<RegionData> rdOpt = findRegionData(regionName, date);
        if (rdOpt.isPresent()) {
            boolean removed = rdOpt.get().getEducationImpacts().remove(impactToDelete);
            if (removed) {
                System.out.println("EducationImpact deleted from " + regionName + " for " + date);
            }
            return removed;
        } else {
            System.err.println("Error: RegionData not found for " + regionName + ", " + date + ". Cannot delete EducationImpact.");
            return false;
        }
    }

    /**
     * Updates an education impact record within the specified RegionData entry.
     * Assumes the dialog modifies the impactObjectFromList in place.
     * @param regionName The name of the region.
     * @param date The date string for the RegionData entry.
     * @param impactObjectFromList The original EducationImpact object instance from the list.
     * @param newDetailsImpact The EducationImpact object containing the new values (often same instance as original).
     * @return true if successful (i.e., RegionData found), false otherwise.
     */
    public boolean updateEducationImpactInRegion(String regionName, String date, EducationImpact impactObjectFromList, EducationImpact newDetailsImpact) {
        if (impactObjectFromList == null || newDetailsImpact == null) {
            System.err.println("Error: Original or new EducationImpact details are null.");
            return false;
        }
        Optional<RegionData> rdOpt = findRegionData(regionName, date);
        if (rdOpt.isPresent()) {
            RegionData rd = rdOpt.get();
            int index = rd.getEducationImpacts().indexOf(impactObjectFromList);
            if (index != -1) {
                // If newDetailsImpact is the SAME object instance as impactObjectFromList but with fields changed:
                // The changes are already on impactObjectFromList (which is impactToEdit in the dialog).
                // If newDetailsImpact is a NEW object instance with the new values:
                rd.getEducationImpacts().set(index, newDetailsImpact); 
                System.out.println("EducationImpact updated in " + regionName + " for " + date);
                return true;
            } else {
                // This case implies the object was not found by index, which might happen if `equals` is not implemented
                // or if the list was modified externally. Given how dialogs usually modify the passed object,
                // we can often assume the object in the list IS already updated.
                // For safety, if you ensure AddEducationImpactDialog modifies the passed `impactToEdit`
                // and returns that same modified instance, then the changes are already in `impactObjectFromList`.
                System.out.println("Note: EducationImpact (original) not explicitly found by index for replacement. If dialog modified it, changes are applied.");
                return true; // Assume updated as the object itself was modified.
            }
        } else {
            System.err.println("Error: RegionData not found for " + regionName + ", " + date + ". Cannot update EducationImpact.");
            return false;
        }
    }
    
    
                          // Borders Methods//
    
    
    public boolean addBorderInfoToRegion(String regionName, String date, Border border) {
        if (border == null) {
            System.err.println("ImpactDataManager: Cannot add a null Border object.");
            return false;
        }
        Optional<RegionData> rdOpt = findRegionData(regionName, date);
        if (rdOpt.isPresent()) {
            RegionData rdInstance = rdOpt.get();
            // Crucially, set or confirm the border's regionDataRef to the instance managed by ImpactDataManager
            border.setRegionDataRef(rdInstance);
            rdInstance.addBorderInfo(border); // This method is in RegionData.java
            System.out.println("Border info ('" + border.getBorderName() + "') added to " + regionName + " for " + date);
            return true;
        } else {
            System.err.println("Error: RegionData not found for " + regionName + ", " + date + ". Cannot add Border info.");
            return false;
        }
    }

    /**
     * Retrieves all border information records for a specific RegionData entry.
     *
     * @param regionName The name of the region.
     * @param date The date string for the RegionData entry.
     * @return A list of Border objects, or an empty list if RegionData not found or no border info.
     */
    public List<Border> getBorderInfoForRegion(String regionName, String date) {
        Optional<RegionData> rdOpt = findRegionData(regionName, date);
        if (rdOpt.isPresent()) {
            // Returns the actual list from the specific RegionData instance.
            // Consider returning an unmodifiable copy if external modification is a concern:
            // return Collections.unmodifiableList(rdOpt.get().getBorderInfo());
            return rdOpt.get().getBorderInfo();
        }
        return new ArrayList<>(); // Return an empty list if RegionData not found
    }

    /**
     * Deletes a specific border information record from the specified RegionData entry.
     *
     * @param regionName The name of the region.
     * @param date The date string for the RegionData entry.
     * @param borderToDelete The Border object to delete from the RegionData's list.
     * @return true if deleted successfully, false otherwise.
     */
    public boolean deleteBorderInfoFromRegion(String regionName, String date, Border borderToDelete) {
        if (borderToDelete == null) {
            System.err.println("ImpactDataManager: Cannot delete a null Border object.");
            return false;
        }
        Optional<RegionData> rdOpt = findRegionData(regionName, date);
        if (rdOpt.isPresent()) {
            // removeBorderInfo is a method in RegionData.java
            boolean removed = rdOpt.get().removeBorderInfo(borderToDelete);
            if (removed) {
                System.out.println("Border info ('" + borderToDelete.getBorderName() + "') deleted from " + regionName + " for " + date);
            } else {
                System.out.println("Border info ('" + borderToDelete.getBorderName() + "') not found in " + regionName + " for " + date + " for deletion.");
            }
            return removed;
        } else {
            System.err.println("Error: RegionData not found for " + regionName + ", " + date + ". Cannot delete Border info.");
            return false;
        }
    }

    /**
     * Updates a border information record within the specified RegionData entry.
     * This method assumes the dialog modifies the fields of 'borderObjectFromList' directly,
     * and 'newDetailsBorder' is that same (now modified) instance.
     *
     * @param regionName The name of the region.
     * @param date The date string for the RegionData entry.
     * @param borderObjectFromList The exact Border object instance currently in the RegionData's list (before dialog edits).
     * @param newDetailsBorder The Border object (usually the same instance as borderObjectFromList) 
     *                         containing the new field values from the dialog.
     * @return true if update was successful (RegionData found and object found/updated), false otherwise.
     */
    public boolean updateBorderInfoInRegion(String regionName, String date, Border borderObjectFromList, Border newDetailsBorder) {
        if (borderObjectFromList == null || newDetailsBorder == null) {
            System.err.println("ImpactDataManager: Cannot update Border, original or new details are null.");
            return false;
        }
        Optional<RegionData> rdOpt = findRegionData(regionName, date);
        if (rdOpt.isPresent()) {
            RegionData rdInstance = rdOpt.get();
            List<Border> bordersInRegion = rdInstance.getBorderInfo();
            int index = bordersInRegion.indexOf(borderObjectFromList); // Relies on Border.equals()

            if (index != -1) {
                // Get the actual object from the list to update its fields
                Border borderToUpdate = bordersInRegion.get(index);
                
                borderToUpdate.setBorderName(newDetailsBorder.getBorderName());
                borderToUpdate.setIsClosed(newDetailsBorder.getIsClosed());
                // Ensure its RegionDataRef is still this rdInstance, as dialogs should not change this association directly.
                // If association needs to change, it's a delete from old and add to new.
                borderToUpdate.setRegionDataRef(rdInstance); 

                System.out.println("Border info ('" + borderToUpdate.getBorderName() + "') updated in " + regionName + " for " + date);
                return true;
            } else {
                System.err.println("Error: Original Border object ('" + borderObjectFromList.getBorderName() + "') not found in RegionData for update. Check Border.equals() method.");
                return false;
            }
        } else {
            System.err.println("Error: RegionData not found for " + regionName + ", " + date + ". Cannot update Border info.");
            return false;
        }
    }
    
    
    


    public String getMostAffectedRegionReport() {
        if (allRegionsData.isEmpty()) {
            return "Most Affected Region: No data available to determine the most affected region.\n";
        }
        // Definition: Sum of martyrs + wounded + prisoners across all dates for each region.
        String mostAffected = allRegionsData.stream()
            .collect(Collectors.groupingBy(RegionData::getRegion,
                Collectors.summingInt(rd -> rd.getWarStats().getMartyrs() +
                                            rd.getWarStats().getWounded() +
                                            rd.getWarStats().getPrisoners())))
            .entrySet().stream()
            .max(Comparator.comparingInt(entry -> entry.getValue()))
            .map(entry -> entry.getKey() + " (Total Casualties: " + entry.getValue() + ")")
            .orElse("N/A - Calculation Error or No Data");

        return "Most Affected Region (by total casualties): " + mostAffected + "\n";
    }

    /**
     * Generates a report showing the RegionData entries with the most and least
     * martyrs, wounded, and prisoners.
     * @return A string report.
     */
    public String getMostAndLeastMartyrsWoundedPrisonersReport() {
        if (allRegionsData.isEmpty()) {
            return "Most/Least Stats: No data to display.\n";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("--- Extremes in War Statistics (per Region/Date entry) ---\n");

        // Martyrs
        allRegionsData.stream().max(Comparator.comparingInt(rd -> rd.getWarStats().getMartyrs()))
            .ifPresent(rd -> sb.append("Most Martyrs: ").append(rd.getRegion()).append(" (").append(rd.getDate()).append(") - ").append(rd.getWarStats().getMartyrs()).append("\n"));
        allRegionsData.stream().filter(rd -> rd.getWarStats().getMartyrs() > 0)
            .min(Comparator.comparingInt(rd -> rd.getWarStats().getMartyrs()))
            .ifPresentOrElse(
                rd -> sb.append("Least Martyrs (non-zero): ").append(rd.getRegion()).append(" (").append(rd.getDate()).append(") - ").append(rd.getWarStats().getMartyrs()).append("\n"),
                () -> {
                    if (allRegionsData.stream().allMatch(rd -> rd.getWarStats().getMartyrs() == 0 && !allRegionsData.isEmpty())) { // Check if all are zero and list is not empty
                        sb.append("Least Martyrs: All entries have 0 martyrs.\n");
                    } else if (!allRegionsData.isEmpty()){ // If some non-zero exist but min is 0 or no non-zero values
                        sb.append("Least Martyrs: Some entries have 0 martyrs or data is sparse for non-zero minimum.\n");
                    }
                }
            );

        // Wounded (نمط مشابه للشهداء)
        allRegionsData.stream().max(Comparator.comparingInt(rd -> rd.getWarStats().getWounded()))
            .ifPresent(rd -> sb.append("Most Wounded: ").append(rd.getRegion()).append(" (").append(rd.getDate()).append(") - ").append(rd.getWarStats().getWounded()).append("\n"));
        allRegionsData.stream().filter(rd -> rd.getWarStats().getWounded() > 0)
             .min(Comparator.comparingInt(rd -> rd.getWarStats().getWounded()))
             .ifPresentOrElse(
                 rd -> sb.append("Least Wounded (non-zero): ").append(rd.getRegion()).append(" (").append(rd.getDate()).append(") - ").append(rd.getWarStats().getWounded()).append("\n"),
                 () -> {
                    if (allRegionsData.stream().allMatch(rd -> rd.getWarStats().getWounded() == 0 && !allRegionsData.isEmpty())) {
                        sb.append("Least Wounded: All entries have 0 wounded.\n");
                    } else if (!allRegionsData.isEmpty()){
                         sb.append("Least Wounded: Some entries have 0 wounded or data is sparse for non-zero minimum.\n");
                    }
                 }
             );

        // Prisoners (نمط مشابه للشهداء)
        allRegionsData.stream().max(Comparator.comparingInt(rd -> rd.getWarStats().getPrisoners()))
            .ifPresent(rd -> sb.append("Most Prisoners: ").append(rd.getRegion()).append(" (").append(rd.getDate()).append(") - ").append(rd.getWarStats().getPrisoners()).append("\n"));
        allRegionsData.stream().filter(rd -> rd.getWarStats().getPrisoners() > 0)
            .min(Comparator.comparingInt(rd -> rd.getWarStats().getPrisoners()))
            .ifPresentOrElse(
                rd -> sb.append("Least Prisoners (non-zero): ").append(rd.getRegion()).append(" (").append(rd.getDate()).append(") - ").append(rd.getWarStats().getPrisoners()).append("\n"),
                () -> {
                    if (allRegionsData.stream().allMatch(rd -> rd.getWarStats().getPrisoners() == 0 && !allRegionsData.isEmpty())) {
                        sb.append("Least Prisoners: All entries have 0 prisoners.\n");
                    } else if (!allRegionsData.isEmpty()){
                        sb.append("Least Prisoners: Some entries have 0 prisoners or data is sparse for non-zero minimum.\n");
                    }
                }
            );
        sb.append("-----------------------------------------------------------\n");
        return sb.toString();
    }

    /**
     * Generates a report of prisoners sorted by the duration of their imprisonment.
     * @return A string report.
     */
    public String getSortedPrisonersByDurationReport() {
        if (allPrisoners.isEmpty()) {
            return "Sorted Prisoners: No prisoner data available to sort.\n";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("--- Prisoners Sorted by Duration (Longest to Shortest) ---\n");
        allPrisoners.stream()
           .sorted(Comparator.comparingInt(Prisoner::getYearsInPrison).reversed()) // الفرز تنازليًا
           .forEach(p -> sb.append("  ").append(p.getName())
                           .append(" (Age: ").append(p.getAge()).append(")")
                           .append(" - Years: ").append(p.getYearsInPrison())
                           .append(p.isReleased() ? " (Released)" : " (Detained)")
                           .append(p.getRegionDataRef() != null ? " - Region: " + p.getRegionDataRef().getRegion() + ", Date: " + p.getRegionDataRef().getDate() : " - Region data missing")
                           .append("\n"));
        sb.append("--------------------------------------------------------------\n");
        return sb.toString();
    }

    /**
     * Generates a report of all border statuses from all RegionData entries.
     * @return A string report.
     */
    public String getAllBorderStatusesReport() {
        if (allRegionsData.isEmpty()) {
            return "Border Statuses: No region data available to display border statuses.\n";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("--- All Recorded Border Statuses ---\n");
        boolean foundAnyBorderInfo = false;
        for (RegionData rd : allRegionsData) {
            if (rd.getBorderInfo() != null && !rd.getBorderInfo().isEmpty()) {
                foundAnyBorderInfo = true;
                sb.append("Region: ").append(rd.getRegion()).append(", Date: ").append(rd.getDate()).append("\n");
                for (Border border : rd.getBorderInfo()) {
                    sb.append("  Border: ").append(border.getBorderName())
                      .append(", Status: ").append(border.getStatusString()) // نستخدم getStatusString من كلاس Border
                      .append("\n");
                }
            }
        }
        if (!foundAnyBorderInfo) {
            sb.append("No border information recorded across all regions/dates.\n");
        }
        sb.append("------------------------------------\n");
        return sb.toString();
    }

 // In ImpactDataManager.java

// In ImpactDataManager.java

public Map<String, Integer> getTotalMartyrsPerRegionForChart() {
    System.out.println("ImpactDataManager: getTotalMartyrsPerRegionForChart CALLED (using WarStats)."); // DEBUG

    Map<String, Integer> martyrsPerRegion = new HashMap<>();
    // تهيئة الـ Map بالمفاتيح الصحيحة وقيمة صفر
    martyrsPerRegion.put("Gaza", 0);
    martyrsPerRegion.put("West Bank", 0);
    martyrsPerRegion.put("East Jerusalem", 0);

    // جملة طباعة لإظهار الـ Map الأولية
    System.out.println("ImpactDataManager: Initial chart map: " + martyrsPerRegion);

    // تحقق مما إذا كانت قائمة RegionData موجودة وليست فارغة
    if (this.allRegionsData != null && !this.allRegionsData.isEmpty()) {
        // جملة طباعة لإظهار عدد إدخالات RegionData التي ستتم معالجتها
        System.out.println("ImpactDataManager: Processing " + this.allRegionsData.size() + " RegionData entries for chart.");

        for (RegionData rd : this.allRegionsData) {
            String regionName = rd.getRegion();
            int martyrsInThisEntry = rd.getWarStats().getMartyrs(); // الحصول على عدد الشهداء من WarStats لهذا الإدخال

            // جملة طباعة لإظهار المنطقة وعدد الشهداء في هذا الإدخال المحدد
            System.out.println("ImpactDataManager: RegionData entry: '" + regionName + " (" + rd.getDate() + ")' has " + martyrsInThisEntry + " martyrs in its WarStats.");

            // تحقق مما إذا كانت هذه المنطقة هي واحدة من المفاتيح التي نهتم بها
            if (martyrsPerRegion.containsKey(regionName)) {
                // أضف عدد شهداء هذا الإدخال إلى الإجمالي التراكمي للمنطقة
                martyrsPerRegion.put(regionName, martyrsPerRegion.get(regionName) + martyrsInThisEntry);
                // جملة طباعة لإظهار أنه تم تحديث العداد للمنطقة، وإظهار الـ Map بعد التحديث
                System.out.println("ImpactDataManager: Updated count for '" + regionName + "'. New map: " + martyrsPerRegion);
            } else {
                // جملة طباعة إذا كانت المنطقة المستخرجة غير موجودة كمفتاح في الـ Map
                System.out.println("ImpactDataManager: Region '" + regionName + "' from RegionData entry is NOT a key in martyrsPerRegion map. Current keys: " + martyrsPerRegion.keySet());
            }
        }
    } else {
        // جملة طباعة إذا كانت قائمة RegionData فارغة أو null
        System.out.println("ImpactDataManager: allRegionsData list is null or empty.");
    }

    // جملة طباعة لإظهار الـ Map النهائية قبل إرجاعها
    System.out.println("ImpactDataManager: Final martyrsPerRegion map for chart (from WarStats): " + martyrsPerRegion);
    return martyrsPerRegion;
}

    
    
}