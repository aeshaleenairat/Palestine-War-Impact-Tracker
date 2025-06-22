// File: com/mycompany/javaproject/AddBorderDialog.java
package com.mycompany.javaproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
// لا نحتاج List أو Vector هنا لأننا لا نختار من قائمة RegionData،
// بل يتم تمرير RegionData الذي نعمل عليه إلى هذا الحوار.

public class AddBorderDialog extends JDialog {

    private JTextField borderNameField;
    private JCheckBox isClosedCheckBox; // true if closed, false if open

    private JButton saveButton;
    private JButton cancelButton;

    private Border resultBorder; // Will hold the new or edited Border object
    private final Border borderToEdit; // Stores the Border being edited; null if in "add" mode
    private final RegionData associatedRegionData; // The RegionData this border belongs to

    // Constructor for "Add" mode
    public AddBorderDialog(Frame owner, RegionData associatedRegionData) {
        this(owner, associatedRegionData, null); // Call the main constructor with borderToEdit as null
    }

    // Main constructor for "Add" or "Edit" mode
    public AddBorderDialog(Frame owner, RegionData associatedRegionData, Border borderToEdit) {
        // The title will now show which Region/Date this border is for
        super(owner,
              (borderToEdit == null ? "Add New Border for " : "Edit Border for ") +
              (associatedRegionData != null ? associatedRegionData.getRegion() + " (" + associatedRegionData.getDate() + ")" : "Unknown Region/Date"),
              true); // Modal
        
        this.associatedRegionData = associatedRegionData;
        this.borderToEdit = borderToEdit;

        if (this.associatedRegionData == null) {
            // This should ideally not happen if the dialog is called correctly
            JOptionPane.showMessageDialog(owner, "Error: Associated Region/Date data is missing.", "Dialog Initialization Error", JOptionPane.ERROR_MESSAGE);
            // We might want to dispose the dialog or prevent it from showing fully
            // For now, some components might be disabled or behave unexpectedly.
        }

        initComponents(); // Initialize all GUI components

        if (this.borderToEdit != null) {
            // Title is already set considering edit mode from super()
            saveButton.setText("Update Border"); // Change save button text for edit mode
            populateFieldsForEdit();     // Fill fields with data from borderToEdit
        } else {
            // Title is already set considering add mode from super()
            saveButton.setText("Save Border");  // Default save button text for add mode
        }

        pack(); // Size dialog to fit components
        setLocationRelativeTo(owner); // Center dialog
        this.resultBorder = null; // Initialize result to null
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Border Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Border Name:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; //gbc.gridwidth = 2; // Not needed if only one component
        borderNameField = new JTextField(25);
        inputPanel.add(borderNameField, gbc);
        // gbc.gridwidth = 1; // Reset if it was changed

        // Row 1: Is Closed Status
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        isClosedCheckBox = new JCheckBox("Mark as Closed"); // Text implies checking means it's closed
        isClosedCheckBox.setToolTipText("Check this box if the border is currently closed.");
        inputPanel.add(isClosedCheckBox, gbc);
        
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

        // Disable inputs if associatedRegionData is missing (though this is a fallback)
        if (associatedRegionData == null) {
            borderNameField.setEnabled(false);
            isClosedCheckBox.setEnabled(false);
            saveButton.setEnabled(false);
        }
    }

    // Fills the input fields with data from borderToEdit when in "edit" mode
    private void populateFieldsForEdit() {
        if (borderToEdit == null) return;

        borderNameField.setText(borderToEdit.getBorderName());
        // If getIsClosed() can return null, handle it. Otherwise, direct assignment is fine.
        isClosedCheckBox.setSelected(borderToEdit.getIsClosed() != null && borderToEdit.getIsClosed());
    }

    private void saveAction(ActionEvent e) {
        if (associatedRegionData == null) {
             JOptionPane.showMessageDialog(this, "Cannot save: Associated Region/Date data is missing.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String borderName = borderNameField.getText().trim();
        Boolean isClosed = isClosedCheckBox.isSelected(); // true if checked (Closed), false if unchecked (Open)

        // Validation
        if (borderName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Border Name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (borderToEdit == null) { // Add mode
            // Create a new Border object, associating it with the passed RegionData
            this.resultBorder = new Border(borderName, isClosed, this.associatedRegionData);
            System.out.println("AddBorderDialog: New Border object created: " + this.resultBorder.getBorderName());
        } else { // Edit mode
            // Update the fields of the existing borderToEdit object
            borderToEdit.setBorderName(borderName);
            borderToEdit.setIsClosed(isClosed);
            // The regionDataRef of borderToEdit should already be correct and not change here.
            // If it could change, that logic would be more complex.
            this.resultBorder = borderToEdit; // The result is the modified existing object
            System.out.println("AddBorderDialog: Border object updated: " + this.resultBorder.getBorderName());
        }

        setVisible(false);
        dispose();
    }

    private void cancelAction(ActionEvent e) {
        this.resultBorder = null; // No result on cancel
        setVisible(false);
        dispose();
    }

    // Returns the new or edited Border object, or null if cancelled
    public Border getResultBorder() {
        System.out.println("AddBorderDialog: getResultBorder() called. Returning: " + (this.resultBorder != null ? this.resultBorder.getBorderName() : "null"));
        return this.resultBorder;
    }
}