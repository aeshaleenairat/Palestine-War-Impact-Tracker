// File: com/mycompany/javaproject/EducationImpactTableModel.java
package com.mycompany.javaproject;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ArrayList;

public class EducationImpactTableModel extends AbstractTableModel {

    private List<EducationImpact> impactList;
    private final String[] columnNames = {"Schools Destroyed", "Students Displaced", "Status"};

    public EducationImpactTableModel(List<EducationImpact> initialImpactList) {
        this.impactList = new ArrayList<>();
        if (initialImpactList != null) {
            this.impactList.addAll(initialImpactList);
        }
    }

    @Override
    public int getRowCount() { return impactList.size(); }

    @Override
    public int getColumnCount() { return columnNames.length; }

    @Override
   public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= impactList.size()) return null;
        EducationImpact impact = impactList.get(rowIndex);
        switch (columnIndex) {
            case 0: return impact.getSchoolDestroyed();
            case 1: return impact.getStudentDisplaced();
            case 2: return impact.getStatusString(); // <<<< التصحيح: استخدم getStatusString()
            default: return null;
        }
    }

    @Override
    public String getColumnName(int column) { return columnNames[column]; }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) { return false; }

    public void setData(List<EducationImpact> newImpactList) {
        this.impactList = new ArrayList<>();
        if (newImpactList != null) {
            this.impactList.addAll(newImpactList);
        }
        fireTableDataChanged();
    }

    public EducationImpact getImpactAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < impactList.size()) {
            return impactList.get(rowIndex);
        }
        return null;
    }
}
