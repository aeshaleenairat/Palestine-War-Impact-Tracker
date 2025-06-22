package com.mycompany.javaproject;

// File: HealthcareImpact.java
public class HealthcareImpact {
       private int hospitalDestroyed; // عدد المستشفيات/المراكز المدمرة أو المتضررة
    private int untreatedPatients; // عدد المرضى غير المعالجين (تقديري)
    private boolean status;        // الحالة العامة للنظام الصحي أو المرفق (مثلاً: فعال، متوقف جزئيًا، متوقف كليًا)
                                   // يمكن تفسير true كـ "يعمل بشكل ما" و false كـ "متعطل بشكل كبير"
    // Constructor
    public HealthcareImpact(int hospitalDestroyed, int untreatedPatients, boolean status) {
        this.hospitalDestroyed = hospitalDestroyed;
        this.untreatedPatients = untreatedPatients;
        this.status = status;
    }

    // Getters
    public int getHospitalDestroyed() {
        return hospitalDestroyed;
    }

    public int getUntreatedPatients() {
        return untreatedPatients;
    }

    public boolean isStatus() {
        return status;
    }

    // Setters
    public void setHospitalDestroyed(int hospitalDestroyed) {
        this.hospitalDestroyed = hospitalDestroyed;
    }

    public void setUntreatedPatients(int untreatedPatients) {
        this.untreatedPatients = untreatedPatients;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    
     // displayStats() from UML - يمكن استخدامها للطباعة النصية أو لتنسيق العرض
    public String getStatusString() {
        return status ? "Operational/Partially Operational" : "Non-Operational/Severely Affected";
    }
    
    // displayStats() method as per UML
    public void displayStats() {
        System.out.println("  --- Healthcare Impact Detail ---");
        System.out.println("  Hospitals Destroyed/Damaged: " + hospitalDestroyed);
        System.out.println("  Untreated Patients (estimate): " + untreatedPatients);
        System.out.println("  System Status: " + (status ? "Functional/Partially Functional" : "Non-Functional/Overwhelmed"));
        // This interpretation of 'status' is an assumption
    }

    @Override
    public String toString() {
        return "Hospitals Destroyed: " + hospitalDestroyed +
               ", Untreated Patients: " + untreatedPatients +
               ", Status: " + (status ? "Functional" : "Non-Functional");
    }
}