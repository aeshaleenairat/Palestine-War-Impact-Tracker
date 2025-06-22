// File: com/mycompany/javaproject/HealthcareImpactTableModel.java
package com.mycompany.javaproject;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ArrayList;

public class HealthcareImpactTableModel extends AbstractTableModel {

    private List<HealthcareImpact> impactList;
    // الأعمدة المقترحة، يمكنك تعديلها
    private final String[] columnNames = {"Hospitals Destroyed/Damaged", "Untreated Patients", "Status"};

    public HealthcareImpactTableModel(List<HealthcareImpact> initialImpactList) {
        this.impactList = new ArrayList<>();
        if (initialImpactList != null) {
            this.impactList.addAll(initialImpactList);
        }
    }

    @Override
    public int getRowCount() {
        return impactList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= impactList.size()) {
            return null;
        }
        HealthcareImpact impact = impactList.get(rowIndex);

        switch (columnIndex) {
            case 0: return impact.getHospitalDestroyed();
            case 1: return impact.getUntreatedPatients();
            case 2: return impact.getStatusString(); // استخدام ميثود getStatusString للعرض
            default: return null;
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; // للقراءة فقط حاليًا، يمكن جعله true إذا أردت تعديل مباشر
    }

    public void setData(List<HealthcareImpact> newImpactList) {
        this.impactList = new ArrayList<>(); // إنشاء قائمة داخلية جديدة
        if (newImpactList != null) {
            this.impactList.addAll(newImpactList);
        }
        fireTableDataChanged(); // إعلام الجدول بتغيير البيانات بالكامل
    }

    public HealthcareImpact getImpactAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < impactList.size()) {
            return impactList.get(rowIndex);
        }
        return null;
    }
}