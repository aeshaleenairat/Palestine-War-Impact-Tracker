// File: com/mycompany/javaproject/AddMartyrDialog.java
package com.mycompany.javaproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Vector; // For JComboBox items

public class AddMartyrDialog extends JDialog {

    private JTextField nameField;
    private JSpinner ageSpinner;
    private JTextField dateOfDeathField;
    private JTextArea causeArea;
    private JComboBox<RegionDataWrapper> regionDataComboBox;

    private JButton saveButton;
    private JButton cancelButton;

    private Martyr resultMartyr; // Will hold the new or edited martyr object to be returned
    private final Martyr martyrToEdit; // Stores the martyr being edited; null if in "add" mode
    private final List<RegionData> availableRegionDataEntries;

    // Inner class for displaying RegionData in JComboBox
    public static class RegionDataWrapper {
        private final RegionData regionData;

        public RegionDataWrapper(RegionData regionData) {
            this.regionData = regionData;
        }

        public RegionData getRegionData() {
            return regionData;
        }

        @Override
        public String toString() {
            return regionData.getRegion() + " (" + regionData.getDate() + ")";
        }

        // It's good practice to override equals and hashCode if these wrappers are used in collections
        // where object equality based on the underlying RegionData is important.
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            RegionDataWrapper that = (RegionDataWrapper) obj;
            return regionData.equals(that.regionData);
        }

        @Override
        public int hashCode() {
            return regionData.hashCode();
        }
    }

    // Constructor for "Add" mode
    public AddMartyrDialog(Frame owner, List<RegionData> availableRegionData) {
        this(owner, availableRegionData, null); // Call the main constructor with martyrToEdit as null
    }

    // Main constructor for "Add" or "Edit" mode
    public AddMartyrDialog(Frame owner, List<RegionData> availableRegionData, Martyr martyrToEdit) {
        super(owner, (martyrToEdit == null ? "Add New Martyr Record" : "Edit Martyr Record"), true); // Modal
        this.availableRegionDataEntries = availableRegionData;
        this.martyrToEdit = martyrToEdit; // If null, it's "add" mode; otherwise "edit" mode

        initComponents(); // Initialize all GUI components

        if (this.martyrToEdit != null) {
            setTitle("Edit Martyr Record"); // Set title for edit mode
            saveButton.setText("Update Martyr"); // Change save button text for edit mode
            populateFieldsForEdit();     // Fill fields with data from martyrToEdit
        } else {
            setTitle("Add New Martyr Record"); // Set title for add mode
            saveButton.setText("Save Martyr");  // Default save button text for add mode
        }

        pack(); // Size dialog to fit components
        setLocationRelativeTo(owner); // Center dialog
        this.resultMartyr = null; // Initialize result to null
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.gridwidth = 2;
        nameField = new JTextField(25);
        inputPanel.add(nameField, gbc);
        gbc.gridwidth = 1;

        // Row 1: Age
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Age:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.gridwidth = 2;
        ageSpinner = new JSpinner(new SpinnerNumberModel(30, 0, 150, 1));
        inputPanel.add(ageSpinner, gbc);
        gbc.gridwidth = 1;
        
        // Row 2: Date of Death
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Date of Death (e.g., DD/MM/YYYY):"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.gridwidth = 2;
        dateOfDeathField = new JTextField(15);
        inputPanel.add(dateOfDeathField, gbc);
        gbc.gridwidth = 1;

        // Row 3-4: Cause of Death
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.NORTHEAST;
        inputPanel.add(new JLabel("Cause of Death:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; gbc.gridwidth = 2; gbc.gridheight = 2;
        gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        causeArea = new JTextArea(3, 25);
        causeArea.setLineWrap(true);
        causeArea.setWrapStyleWord(true);
        JScrollPane causeScrollPane = new JScrollPane(causeArea);
        inputPanel.add(causeScrollPane, gbc);
        gbc.gridwidth = 1; gbc.gridheight = 1; gbc.fill = GridBagConstraints.HORIZONTAL; // Reset
        gbc.weightx = 0; gbc.weighty = 0; // Reset

        // Row 5: Associated Region/Date Entry
        gbc.gridx = 0; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Associate with Event (Region/Date):"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.gridwidth = 2;
        Vector<RegionDataWrapper> regionDataWrappers = new Vector<>();
        if (availableRegionDataEntries != null) {
            for (RegionData rd : availableRegionDataEntries) {
                regionDataWrappers.add(new RegionDataWrapper(rd));
            }
        }
        regionDataComboBox = new JComboBox<>(regionDataWrappers);
        if (regionDataWrappers.isEmpty()) {
            regionDataComboBox.setEnabled(false);
        }
        inputPanel.add(regionDataComboBox, gbc);
        gbc.gridwidth = 1;

        // --- Button Panel ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton(); // Text will be set in constructor based on mode
        cancelButton = new JButton("Cancel");

        saveButton.addActionListener(this::saveAction);
        cancelButton.addActionListener(this::cancelAction);

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Fills the input fields with data from martyrToEdit when in "edit" mode
    private void populateFieldsForEdit() {
        if (martyrToEdit == null) return; // Should not happen if called correctly

        nameField.setText(martyrToEdit.getName());
        ageSpinner.setValue(martyrToEdit.getAge());
        dateOfDeathField.setText(martyrToEdit.getDateOfDeath());
        causeArea.setText(martyrToEdit.getCause());

        if (martyrToEdit.getRegionDataRef() != null) {
            RegionDataWrapper wrapperToSelect = new RegionDataWrapper(martyrToEdit.getRegionDataRef());
            regionDataComboBox.setSelectedItem(wrapperToSelect);
            // If regionDataComboBox might be disabled due to no available regions initially,
            // but we are editing, we might need to enable it if it holds the martyr's current region.
            // However, availableRegionDataEntries should ideally always contain the martyr's current region.
        }
    }

    private void saveAction(ActionEvent e) {
        System.out.println("AddMartyrDialog: saveAction CALLED! Mode: " + (martyrToEdit == null ? "Add" : "Edit"));

        String name = nameField.getText().trim();
        int age = (Integer) ageSpinner.getValue();
        String dateOfDeath = dateOfDeathField.getText().trim();
        String cause = causeArea.getText().trim();
        RegionDataWrapper selectedWrapper = (RegionDataWrapper) regionDataComboBox.getSelectedItem();

        // Validation
        if (name.isEmpty() || dateOfDeath.isEmpty() || cause.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name, Date of Death, and Cause cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (selectedWrapper == null && regionDataComboBox.isEnabled()) {
            JOptionPane.showMessageDialog(this, "You must select an associated Region/Date event.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        RegionData selectedRegionData = (selectedWrapper != null) ? selectedWrapper.getRegionData() : 
                                         (martyrToEdit != null ? martyrToEdit.getRegionDataRef() : null); // Fallback for edit mode if combo was somehow not selectable but had a value

        if (selectedRegionData == null) {
            JOptionPane.showMessageDialog(this, "Associated Region/Date event is missing or could not be determined.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (martyrToEdit == null) { // Add mode
            this.resultMartyr = new Martyr(name, age, dateOfDeath, cause, selectedRegionData);
            System.out.println("AddMartyrDialog: New Martyr object created: " + this.resultMartyr.getName());
        } else { // Edit mode
            // Update the fields of the existing martyrToEdit object
            martyrToEdit.setName(name);
            martyrToEdit.setAge(age);
            martyrToEdit.setDateOfDeath(dateOfDeath);
            martyrToEdit.setCause(cause);
            // Store the original RegionData before potentially changing it
            // RegionData oldRd = martyrToEdit.getRegionDataRef();
            martyrToEdit.setRegionDataRef(selectedRegionData); // Update the region reference
            
            this.resultMartyr = martyrToEdit; // The result is the modified existing object
            System.out.println("AddMartyrDialog: Martyr object updated: " + this.resultMartyr.getName());
        }

        setVisible(false);
        dispose();
    }

    private void cancelAction(ActionEvent e) {
        this.resultMartyr = null; // No result on cancel
        setVisible(false);
        dispose();
    }

    // Returns the new or edited martyr object, or null if cancelled
    public Martyr getNewMartyr() { // Renamed from getNewMartyr to getResultMartyr for clarity in edit mode
        System.out.println("AddMartyrDialog: getResultMartyr() called. Returning: " + (this.resultMartyr != null ? this.resultMartyr.getName() : "null"));
        return this.resultMartyr;
    }
}