// File: com/mycompany/javaproject/AddWoundedDialog.java
package com.mycompany.javaproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Vector;

public class AddWoundedDialog extends JDialog {

    private JTextField nameField;
    private JSpinner ageSpinner;
    private JTextField injuryTypeField;
    private JCheckBox permanentDisabilityCheckBox;
    private JCheckBox hospitalizedCheckBox;
    private JComboBox<AddMartyrDialog.RegionDataWrapper> regionDataComboBox; // Reusing RegionDataWrapper

    private JButton saveButton;
    private JButton cancelButton;

    private Wounded resultWounded;
    private final Wounded woundedToEdit;
    private final List<RegionData> availableRegionDataEntries;

    // Constructor for "Add" mode
    public AddWoundedDialog(Frame owner, List<RegionData> availableRegionData) {
        this(owner, availableRegionData, null);
    }

    // Main constructor for "Add" or "Edit" mode
    public AddWoundedDialog(Frame owner, List<RegionData> availableRegionData, Wounded woundedToEdit) {
        super(owner, (woundedToEdit == null ? "Add New Wounded Record" : "Edit Wounded Record"), true);
        this.availableRegionDataEntries = availableRegionData;
        this.woundedToEdit = woundedToEdit;

        initComponents();

        if (this.woundedToEdit != null) {
            setTitle("Edit Wounded Record");
            saveButton.setText("Update Wounded");
            populateFieldsForEdit();
        } else {
            setTitle("Add New Wounded Record");
            saveButton.setText("Save Wounded");
        }

        pack();
        setLocationRelativeTo(owner);
        this.resultWounded = null;
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.gridwidth = 2;
        nameField = new JTextField(25);
        inputPanel.add(nameField, gbc);
        gbc.gridwidth = 1;

        // Age
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Age:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.gridwidth = 2;
        ageSpinner = new JSpinner(new SpinnerNumberModel(30, 0, 150, 1));
        inputPanel.add(ageSpinner, gbc);
        gbc.gridwidth = 1;

        // Injury Type
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Injury Type:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.gridwidth = 2;
        injuryTypeField = new JTextField(25);
        inputPanel.add(injuryTypeField, gbc);
        gbc.gridwidth = 1;

        // Permanent Disability
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Permanent Disability:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        permanentDisabilityCheckBox = new JCheckBox();
        inputPanel.add(permanentDisabilityCheckBox, gbc);

        // Hospitalized
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Hospitalized:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        hospitalizedCheckBox = new JCheckBox();
        inputPanel.add(hospitalizedCheckBox, gbc);
        
        // Associated Region/Date Entry
        gbc.gridx = 0; gbc.gridy = 5; gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Associate with Event:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.gridwidth = 2;
        Vector<AddMartyrDialog.RegionDataWrapper> regionDataWrappers = new Vector<>(); // Reusing wrapper
        if (availableRegionDataEntries != null) {
            for (RegionData rd : availableRegionDataEntries) {
                regionDataWrappers.add(new AddMartyrDialog.RegionDataWrapper(rd));
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
        saveButton = new JButton(); // Text set in constructor
        cancelButton = new JButton("Cancel");

        saveButton.addActionListener(this::saveAction);
        cancelButton.addActionListener(this::cancelAction);

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void populateFieldsForEdit() {
        if (woundedToEdit == null) return;
        nameField.setText(woundedToEdit.getName());
        ageSpinner.setValue(woundedToEdit.getAge());
        injuryTypeField.setText(woundedToEdit.getInjuryType());
        permanentDisabilityCheckBox.setSelected(woundedToEdit.isPermanentDisability());
        hospitalizedCheckBox.setSelected(woundedToEdit.isHospitalized());

        if (woundedToEdit.getRegionDataRef() != null) {
            AddMartyrDialog.RegionDataWrapper wrapperToSelect = new AddMartyrDialog.RegionDataWrapper(woundedToEdit.getRegionDataRef());
            regionDataComboBox.setSelectedItem(wrapperToSelect);
        }
    }

    private void saveAction(ActionEvent e) {
        String name = nameField.getText().trim();
        int age = (Integer) ageSpinner.getValue();
        String injuryType = injuryTypeField.getText().trim();
        boolean pDisability = permanentDisabilityCheckBox.isSelected();
        boolean hospitalized = hospitalizedCheckBox.isSelected();
        AddMartyrDialog.RegionDataWrapper selectedWrapper = (AddMartyrDialog.RegionDataWrapper) regionDataComboBox.getSelectedItem();

        if (name.isEmpty() || injuryType.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name and Injury Type cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (selectedWrapper == null && regionDataComboBox.isEnabled()) {
            JOptionPane.showMessageDialog(this, "You must select an associated Region/Date event.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
     RegionData selectedRegionData = (selectedWrapper != null) ? selectedWrapper.getRegionData() : 
                                     (woundedToEdit != null ? woundedToEdit.getRegionDataRef() : null);
        if (selectedRegionData == null) {
             JOptionPane.showMessageDialog(this, "Associated Region/Date event is missing.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (woundedToEdit == null) { // Add mode
            this.resultWounded = new Wounded(name, age, injuryType, pDisability, hospitalized, selectedRegionData);
        } else { // Edit mode
            woundedToEdit.setName(name);
            woundedToEdit.setAge(age);
            woundedToEdit.setInjuryType(injuryType);
            woundedToEdit.setPermanentDisability(pDisability);
            woundedToEdit.setHospitalized(hospitalized);
            woundedToEdit.setRegionDataRef(selectedRegionData);
            this.resultWounded = woundedToEdit;
        }
        setVisible(false);
        dispose();
    }

    private void cancelAction(ActionEvent e) {
        this.resultWounded = null;
        setVisible(false);
        dispose();
    }

    public Wounded getResultWounded() {
        return this.resultWounded;
    }
}