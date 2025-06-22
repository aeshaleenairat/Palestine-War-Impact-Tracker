package com.mycompany.javaproject;

// File: Person.java
public abstract class Person {
    protected String name;
    protected int age;

    // Constructor
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setAge(int age) {
        this.age = age;
    }

    // Abstract method to be implemented by subclasses
    // or a common method if displayInfo has a default behavior.
    // The UML suggests it's a concrete method in Person.
    public void displayInfo() {
        System.out.println("Name: " + name);
        System.out.println("Age: " + age);
        // Subclasses can override this to add more specific information
    }
}