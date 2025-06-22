package com.mycompany.javaproject;

// File: EducationImpact.java
public class EducationImpact {
   private int schoolDestroyed;    // عدد المدارس المدمرة
    private int studentDisplaced;   // عدد الطلاب النازحين
    private boolean status;         // حالة النظام التعليمي/المرفق (مثلاً: يعمل، متوقف جزئيًا، إلخ)
    // Constructor
    public EducationImpact(int schoolDestroyed, int studentDisplaced, boolean status) {
        this.schoolDestroyed = schoolDestroyed;
        this.studentDisplaced = studentDisplaced;
        this.status = status;
    }

    // Getters
    public int getSchoolDestroyed() {
        return schoolDestroyed;
    }

    public int getStudentDisplaced() {
        return studentDisplaced;
    }

    public boolean isStatus() { // "is" prefix is conventional for boolean getters
        return status;
    }

    // Setters
    public void setSchoolDestroyed(int schoolDestroyed) {
        this.schoolDestroyed = schoolDestroyed;
    }

    public void setStudentDisplaced(int studentDisplaced) {
        this.studentDisplaced = studentDisplaced;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
    public String getStatusString() {
        return status ? "Operational/Partially Operational" : "Non-Operational/Severely Affected";
    }

    // display() method as per UML
    public void display() {
        System.out.println("  --- Education Impact Detail ---");
        System.out.println("  Schools Destroyed: " + schoolDestroyed);
        System.out.println("  Students Displaced: " + studentDisplaced);
        //System.out.println("  Operational Status: " + (status ? "Operational/Partially Operational" : "Non-Operational/Severely Affected"));
         System.out.println("  Operational Status: " + getStatusString());
// This interpretation of 'status' is an assumption, adjust as per project needs
    }

    @Override
    public String toString() {
        return "Schools Destroyed: " + schoolDestroyed +
               ", Students Displaced: " + studentDisplaced +
               ", Status: " + getStatusString();
    }
}