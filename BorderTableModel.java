// File: com/mycompany/javaproject/BorderTableModel.java
package com.mycompany.javaproject;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.ArrayList;

public class BorderTableModel extends AbstractTableModel {

    private List<Border> borderList;
    // بما أن هذا النموذج سيعرض معابر لـ RegionData محدد،
    // قد لا نحتاج لأعمدة "Region" و "Date" هنا، لأنها ستكون معروفة من السياق.
    // إذا أردت لاحقًا نموذجًا يعرض *كل* المعابر من *كل* المناطق، فستحتاج لتلك الأعمدة.
    private final String[] columnNames = {"Border Name", "Status (Open/Closed)"};

    public BorderTableModel(List<Border> initialBorderList) {
        this.borderList = new ArrayList<>(); // ابدأ بقائمة فارغة
        if (initialBorderList != null) {
            this.borderList.addAll(initialBorderList);
        }
    }

    @Override
    public int getRowCount() {
        return borderList.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < 0 || rowIndex >= borderList.size()) {
            return null; // أو ألقِ استثناءً إذا كان هذا خطأ غير متوقع
        }
        Border border = borderList.get(rowIndex);

        switch (columnIndex) {
            case 0: // Border Name
                return border.getBorderName();
            case 1: // Status (Open/Closed)
                return border.getStatusString(); // نستخدم ميثود getStatusString من كلاس Border
            default:
                return null; // يجب ألا يحدث هذا
        }
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    // الخلايا للقراءة فقط حاليًا
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    /**
     * ميثود لتحديث بيانات النموذج بقائمة جديدة من المعابر.
     * تُستخدم هذه عند تحميل معابر لـ RegionData مختلف أو بعد عملية إضافة/تعديل/حذف.
     * @param newBorderList القائمة الجديدة لكائنات Border.
     */
    public void setData(List<Border> newBorderList) {
        this.borderList = new ArrayList<>(); // أنشئ قائمة داخلية جديدة دائمًا
        if (newBorderList != null) {
            this.borderList.addAll(newBorderList);
        }
        // أخبر الجدول بأن جميع البيانات قد تغيرت (أو أجزاء منها)
        fireTableDataChanged(); 
    }

    /**
     * ميثود مساعدة لجلب كائن Border من صف معين في النموذج.
     * مفيدة عند التعامل مع تحديدات المستخدم في الجدول (للتعديل أو الحذف).
     * @param rowIndex فهرس الصف.
     * @return كائن Border في الصف المحدد، أو null إذا كان الفهرس غير صالح.
     */
    public Border getBorderAt(int rowIndex) {
        if (rowIndex >= 0 && rowIndex < borderList.size()) {
            return borderList.get(rowIndex);
        }
        return null;
    }
}