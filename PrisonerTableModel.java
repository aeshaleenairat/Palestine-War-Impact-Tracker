// File: com/mycompany/javaproject/PrisonerTableModel.java
package com.mycompany.javaproject;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ArrayList;

public class PrisonerTableModel extends AbstractTableModel {

    private List<Prisoner> prisonerList;
    // أعمدة مقترحة، يمكنك تعديلها حسب الحاجة
    private final String[] columnNames = {"Name", "Age", "Years in Prison", "Released", "Region", "Record Date"};

    public PrisonerTableModel(List<Prisoner> initialPrisonerList) {
        this.prisonerList = new ArrayList<>();
        if (initialPrisonerList != null) {
            this.prisonerList.addAll(initialPrisonerList);
        }
    }

    @Override
    public int getRowCount() {
        return prisonerList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= prisonerList.size()) {
            return null;
        }
        Prisoner prisoner = prisonerList.get(rowIndex);

        switch (columnIndex) {
            case 0: return prisoner.getName();
            case 1: return prisoner.getAge();
            case 2: return prisoner.getYearsInPrison();
            case 3: return prisoner.isReleased() ? "Yes" : "No";
            case 4: return (prisoner.getRegionDataRef() != null) ? prisoner.getRegionDataRef().getRegion() : "N/A";
            case 5: return (prisoner.getRegionDataRef() != null) ? prisoner.getRegionDataRef().getDate() : "N/A";
            default: return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; // للقراءة فقط حاليًا
    }

    public void setData(List<Prisoner> newPrisonerList) {
        this.prisonerList = new ArrayList<>(); // إنشاء قائمة داخلية جديدة
        if (newPrisonerList != null) {
            this.prisonerList.addAll(newPrisonerList);
        }
        fireTableDataChanged(); // إعلام الجدول بتغيير البيانات بالكامل
    }

    public Prisoner getPrisonerAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < prisonerList.size()) {
            return prisonerList.get(rowIndex);
        }
        return null;
    }
}