package com.mycompany.javaproject;

// File: WarStats.java
public class WarStats {
    private int martyrs;
    private int wounded;
    private int prisoners;

    // Constructor
    public WarStats(int martyrs, int wounded, int prisoners) {
        this.martyrs = martyrs;
        this.wounded = wounded;
        this.prisoners = prisoners;
    }

    // Default constructor
    public WarStats() {
        this(0, 0, 0); // Initialize with zero counts
    }

    // Getters
    public int getMartyrs() {
        return martyrs;
    }

    public int getWounded() {
        return wounded;
    }

    public int getPrisoners() {
        return prisoners;
    }

    // Setters
    public void setMartyrs(int martyrs) {
        this.martyrs = martyrs;
    }

    public void setWounded(int wounded) {
        this.wounded = wounded;
    }

    public void setPrisoners(int prisoners) {
        this.prisoners = prisoners;
    }

    // Methods to increment counts (as per UML 'add' and 'update' might relate to this)
    public void addMartyr() {
        this.martyrs++;
    }
    public void addMartyrs(int count) {
        this.martyrs += count;
    }

    public void addWounded() {
        this.wounded++;
    }
    public void addWounded(int count) {
        this.wounded += count;
    }

    public void addPrisoner() {
        this.prisoners++;
    }
    public void addPrisoners(int count) {
        this.prisoners += count;
    }
       public void decrementWounded() { if (this.wounded > 0) this.wounded--; }
   public void decrementPrisoners() {
        if (this.prisoners > 0) {
            this.prisoners--;
        }
    }
    // Update method as per UML (could mean replacing all stats, or adding to them)
    // Option 1: Replace stats entirely
    public void update(int newMartyrs, int newWounded, int newPrisoners) {
        this.martyrs = newMartyrs;
        this.wounded = newWounded;
        this.prisoners = newPrisoners;
    }

    // Option 2: Add to existing stats (more like an 'add' operation for multiple items)
    // public void update(int additionalMartyrs, int additionalWounded, int additionalPrisoners) {
    //     this.martyrs += additionalMartyrs;
    //     this.wounded += additionalWounded;
    //     this.prisoners += additionalPrisoners;
    // }

    // Display method as per UML
    public void display() {
        System.out.println("--- War Statistics ---");
        System.out.println("Martyrs: " + martyrs);
        System.out.println("Wounded: " + wounded);
        System.out.println("Prisoners: " + prisoners);
        System.out.println("----------------------");
    }

    @Override
    public String toString() {
        return "Martyrs: " + martyrs + ", Wounded: " + wounded + ", Prisoners: " + prisoners;
    }
    
    public void decrementMartyrs() {
    if (this.martyrs > 0) {
        this.martyrs--;
    }
}
}