// File: com/mycompany/javaproject/AddHealthcareImpactDialog.java
package com.mycompany.javaproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AddHealthcareImpactDialog extends JDialog {

    private JSpinner hospitalsDestroyedSpinner;
    private JSpinner untreatedPatientsSpinner;
    private JCheckBox statusCheckBox; // true for "Operational", false for "Non-Operational"

    private JButton saveButton;
    private JButton cancelButton;

    private HealthcareImpact resultImpact; // Will hold the new or edited object
    private final HealthcareImpact impactToEdit;

    // Constructor for "Add" mode
    public AddHealthcareImpactDialog(Frame owner, String regionName, String date) {
        this(owner, regionName, date, null);
    }

    // Main constructor for "Add" or "Edit" mode
    public AddHealthcareImpactDialog(Frame owner, String regionName, String date, HealthcareImpact impactToEdit) {
        super(owner, 
              (impactToEdit == null ? "Add Healthcare Impact for " : "Edit Healthcare Impact for ") + regionName + " (" + date + ")", 
              true); // Modal
        this.impactToEdit = impactToEdit;

        initComponents();

        if (this.impactToEdit != null) {
            saveButton.setText("Update Impact");
            populateFieldsForEdit();
        } else {
            saveButton.setText("Save Impact");
        }

        // Set a reasonable size or pack
        // setSize(400, 250);
        pack(); 
        setLocationRelativeTo(owner);
        this.resultImpact = null;
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Hospitals Destroyed
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Hospitals Destroyed/Damaged:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        hospitalsDestroyedSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1)); // Min 0
        inputPanel.add(hospitalsDestroyedSpinner, gbc);

        // Untreated Patients
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Untreated Patients (Estimate):"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        untreatedPatientsSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100000, 10)); // Min 0
        inputPanel.add(untreatedPatientsSpinner, gbc);

        // Status
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Operational Status:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        statusCheckBox = new JCheckBox("Operational / Partially Operational");
        statusCheckBox.setToolTipText("Check if the healthcare system/facility is at least partially operational.");
        inputPanel.add(statusCheckBox, gbc);
        
        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton(); // Text will be set in constructor
        cancelButton = new JButton("Cancel");

        saveButton.addActionListener(this::saveAction);
        cancelButton.addActionListener(this::cancelAction);

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void populateFieldsForEdit() {
        if (impactToEdit == null) return;
        hospitalsDestroyedSpinner.setValue(impactToEdit.getHospitalDestroyed());
        untreatedPatientsSpinner.setValue(impactToEdit.getUntreatedPatients());
        statusCheckBox.setSelected(impactToEdit.isStatus());
    }

    private void saveAction(ActionEvent e) {
        int hospitals = (Integer) hospitalsDestroyedSpinner.getValue();
        int patients = (Integer) untreatedPatientsSpinner.getValue();
        boolean status = statusCheckBox.isSelected();

        // Basic validation could be added here if needed (e.g., ensuring numbers are not negative, though JSpinner handles min)

        if (impactToEdit == null) { // Add mode
            this.resultImpact = new HealthcareImpact(hospitals, patients, status);
        } else { // Edit mode
            impactToEdit.setHospitalDestroyed(hospitals);
            impactToEdit.setUntreatedPatients(patients);
            impactToEdit.setStatus(status);
            this.resultImpact = impactToEdit;
        }
        setVisible(false);
        dispose();
    }

    private void cancelAction(ActionEvent e) {
        this.resultImpact = null;
        setVisible(false);
        dispose();
    }

    public HealthcareImpact getResultImpact() {
        return this.resultImpact;
    }
}