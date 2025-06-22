// File: com/mycompany/javaproject/MartyrTableModel.java
package com.mycompany.javaproject;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ArrayList;

public class MartyrTableModel extends AbstractTableModel {

 private List<Martyr> martyrList; // Can now be re-assigned
 private final String[] columnNames = {"Name", "Age", "Date of Death", "Cause", "Region", "Record Date"};

public MartyrTableModel(List<Martyr> initialMartyrList) {
    // Initialize with a new list containing copies or just a new list
    this.martyrList = new ArrayList<>();
    if (initialMartyrList != null) {
        this.martyrList.addAll(initialMartyrList); // Populate the new list
    }
}
    @Override
    public int getRowCount() {
        return martyrList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= martyrList.size()) {
            return null; // Or throw an exception
        }
        Martyr martyr = martyrList.get(rowIndex);

        switch (columnIndex) {
            case 0: // Name
                return martyr.getName();
            case 1: // Age
                return martyr.getAge();
            case 2: // Date of Death
                return martyr.getDateOfDeath();
            case 3: // Cause
                return martyr.getCause();
            case 4: // Region (from associated RegionData)
                return (martyr.getRegionDataRef() != null) ? martyr.getRegionDataRef().getRegion() : "N/A";
            case 5: // Record Date (from associated RegionData)
                return (martyr.getRegionDataRef() != null) ? martyr.getRegionDataRef().getDate() : "N/A";
            default:
                return null; // Should not happen
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    // Optional: Define if cells are editable (by default, they are not)
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; // For now, make the table read-only
    }
    
    
    
      public Martyr getMartyrAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < martyrList.size()) {
            return martyrList.get(rowIndex);
        }
        return null;
    }
    
    
    
    
    
    
    
    
    
    

    // Method to update the data in the table model
    // This should be called when the underlying list of martyrs in ImpactDataManager changes.
public void setData(List<Martyr> newMartyrList) {
    // DEBUG: Print the size and content of the list being passed in
    if (newMartyrList != null) {
        System.out.println("MartyrTableModel.setData: Received newMartyrList with size: " + newMartyrList.size());
        for (Martyr m : newMartyrList) {
            System.out.println("MartyrTableModel.setData: Martyr in new list: " + m.getName());
        }
    } else {
        System.out.println("MartyrTableModel.setData: Received newMartyrList is null.");
    }

    // Create a new list for the table model to hold its data
    // This completely decouples it from the list in ImpactDataManager for this update cycle
    this.martyrList = new ArrayList<>();
    if (newMartyrList != null) {
        this.martyrList.addAll(newMartyrList); // Add all items from the source list
    }

    System.out.println("MartyrTableModel.setData: Internal martyrList.size after creating new list and addAll: " + this.martyrList.size());
    // No need for previousSize here as we are effectively replacing the entire data set.
    // If you needed previousSize for more granular fire... events, you'd calculate it before replacing this.martyrList.
    fireTableDataChanged(); // Notify the table that its entire data might have changed
    System.out.println("MartyrTableModel: fireTableDataChanged() called.");
}

    // Helper method to add a single martyr and notify the table (if managing list internally)
    // Or, more commonly, the list is managed by ImpactDataManager, and setData is called.
    public void addMartyr(Martyr martyr) {
        martyrList.add(martyr);
        // Notify table that a row has been inserted at the end
        fireTableRowsInserted(martyrList.size() - 1, martyrList.size() - 1);
    }

    // You might also want methods to remove a martyr or update a martyr,
    // followed by the appropriate fireTable... notification.
    // For now, setData will be the primary way to refresh from ImpactDataManager.
}