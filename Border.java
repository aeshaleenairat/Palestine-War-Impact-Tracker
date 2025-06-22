// File: com/mycompany/javaproject/Border.java
package com.mycompany.javaproject;

public class Border {
    private String borderName;
    private Boolean isClosed;         // استخدام Boolean يسمح بقيمة null (غير معروف) إذا احتجنا لذلك
    private RegionData regionDataRef; // مرجع إلى RegionData الذي يرتبط به هذا المعبر

    // الكونستركتور
    public Border(String borderName, Boolean isClosed, RegionData regionDataRef) {
        this.borderName = borderName;
        this.isClosed = isClosed;
        this.regionDataRef = regionDataRef; // يجب أن يكون هذا هو كائن RegionData الفعلي
    }

    // Getters
    public String getBorderName() {
        return borderName;
    }

    public Boolean getIsClosed() { // يمكن تسميتها isClosed() أيضًا
        return isClosed;
    }

    public RegionData getRegionDataRef() {
        return regionDataRef;
    }

    // Setters
    public void setBorderName(String borderName) {
        this.borderName = borderName;
    }

    public void setIsClosed(Boolean isClosed) {
        this.isClosed = isClosed;
    }

    public void setRegionDataRef(RegionData regionDataRef) {
        this.regionDataRef = regionDataRef;
    }

    // ميثود مساعدة لعرض الحالة كنص
    public String getStatusString() {
        if (isClosed == null) {
            return "Unknown"; // إذا كانت الحالة غير معروفة
        }
        return isClosed ? "Closed" : "Open";
    }

    // ميثود displayStatus من مخطط UML (للطباعة النصية إذا لزم الأمر)
    public void displayStatus() {
        System.out.println("  Border Name: " + borderName + ", Status: " + getStatusString());
        if (regionDataRef != null) {
            System.out.println("  Relevant to Region: " + regionDataRef.getRegion() + " (Data for " + regionDataRef.getDate() + ")");
        }
    }

    // إعادة تعريف toString لعرض معلومات مفيدة (مفيد للاختبار والطباعة)
    @Override
    public String toString() {
        String regionInfo = "N/A";
        if (regionDataRef != null) {
            regionInfo = regionDataRef.getRegion() + " (" + regionDataRef.getDate() + ")";
        }
        return "Border: " + borderName + ", Status: " + getStatusString() + ", Associated Event: " + regionInfo;
    }

    // مهم: إذا كنت ستخزن كائنات Border في مجموعات (Sets) أو تستخدم indexOf في List
    // للبحث عن كائنات بناءً على محتواها وليس فقط على مرجعها، يجب إعادة تعريف equals و hashCode.
    // للتبسيط الآن، سنفترض أننا نتعامل مع المراجع أو أن equals/hashCode الافتراضية كافية
    // أو أننا نبحث عن طريق الاسم والـ RegionDataRef.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Border border = (Border) o;
        // Compare based on name and the associated RegionData object's identity or equals method
        // This is a simplified equals. A more robust one would handle nulls carefully.
        return borderName.equals(border.borderName) &&
               ((regionDataRef == null && border.regionDataRef == null) || 
                (regionDataRef != null && regionDataRef.equals(border.regionDataRef))); 
                // Assuming RegionData has a proper equals method if comparing by content.
                // Or simply regionDataRef == border.regionDataRef if comparing by instance.
    }

    @Override
    public int hashCode() {
        // Simplified hashCode
        int result = borderName.hashCode();
        result = 31 * result + (regionDataRef != null ? regionDataRef.hashCode() : 0);
        return result;
    }
}