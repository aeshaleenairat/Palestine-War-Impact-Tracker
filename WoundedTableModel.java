// File: com/mycompany/javaproject/WoundedTableModel.java
package com.mycompany.javaproject;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ArrayList;

public class WoundedTableModel extends AbstractTableModel {

    private List<Wounded> woundedList;
    private final String[] columnNames = {"Name", "Age", "Injury Type", "Permanent Disability", "Hospitalized", "Region", "Record Date"};

    public WoundedTableModel(List<Wounded> initialWoundedList) {
        this.woundedList = new ArrayList<>();
        if (initialWoundedList != null) {
            this.woundedList.addAll(initialWoundedList);
        }
    }

    @Override
    public int getRowCount() {
        return woundedList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= woundedList.size()) {
            return null;
        }
        Wounded wounded = woundedList.get(rowIndex);

        switch (columnIndex) {
            case 0: return wounded.getName();
            case 1: return wounded.getAge();
            case 2: return wounded.getInjuryType();
            case 3: return wounded.isPermanentDisability() ? "Yes" : "No";
            case 4: return wounded.isHospitalized() ? "Yes" : "No";
            case 5: return (wounded.getRegionDataRef() != null) ? wounded.getRegionDataRef().getRegion() : "N/A";
            case 6: return (wounded.getRegionDataRef() != null) ? wounded.getRegionDataRef().getDate() : "N/A";
            default: return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; // Read-only for now
    }

    public void setData(List<Wounded> newWoundedList) {
        this.woundedList = new ArrayList<>(); // Create new internal list
        if (newWoundedList != null) {
            this.woundedList.addAll(newWoundedList);
        }
        fireTableDataChanged();
    }

    public Wounded getWoundedAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < woundedList.size()) {
            return woundedList.get(rowIndex);
        }
        return null;
    }
}