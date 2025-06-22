// File: com/mycompany/javaproject/AddPrisonerDialog.java
package com.mycompany.javaproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Vector;

// reusing AddMartyrDialog.RegionDataWrapper; ensure it's accessible (e.g., public static inner class or separate public class)
// If AddMartyrDialog.RegionDataWrapper is public static class RegionDataWrapper in AddMartyrDialog:
// import com.mycompany.javaproject.AddMartyrDialog.RegionDataWrapper; 
// Or if you made RegionDataWrapper its own public class:
// import com.mycompany.javaproject.RegionDataWrapper; 


public class AddPrisonerDialog extends JDialog {

    private JTextField nameField;
    private JSpinner ageSpinner;
    private JSpinner yearsInPrisonSpinner;
    private JCheckBox releasedCheckBox;
    private JComboBox<AddMartyrDialog.RegionDataWrapper> regionDataComboBox; // Reusing the wrapper

    private JButton saveButton;
    private JButton cancelButton;

    private Prisoner resultPrisoner;
    private final Prisoner prisonerToEdit;
    private final List<RegionData> availableRegionDataEntries;

    // Constructor for "Add" mode
    public AddPrisonerDialog(Frame owner, List<RegionData> availableRegionData) {
        this(owner, availableRegionData, null);
    }

    // Main constructor for "Add" or "Edit" mode
    public AddPrisonerDialog(Frame owner, List<RegionData> availableRegionData, Prisoner prisonerToEdit) {
        super(owner, (prisonerToEdit == null ? "Add New Prisoner Record" : "Edit Prisoner Record"), true);
        this.availableRegionDataEntries = availableRegionData;
        this.prisonerToEdit = prisonerToEdit;

        initComponents();

        if (this.prisonerToEdit != null) {
            setTitle("Edit Prisoner Record");
            saveButton.setText("Update Prisoner");
            populateFieldsForEdit();
        } else {
            setTitle("Add New Prisoner Record");
            saveButton.setText("Save Prisoner");
        }

        pack();
        setLocationRelativeTo(owner);
        this.resultPrisoner = null;
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

        // Years in Prison
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Years in Prison:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.gridwidth = 2;
        yearsInPrisonSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 100, 1)); // Min 0 years
        inputPanel.add(yearsInPrisonSpinner, gbc);
        gbc.gridwidth = 1;

        // Released
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Released:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        releasedCheckBox = new JCheckBox();
        inputPanel.add(releasedCheckBox, gbc);
        
        // Associated Region/Date Entry
        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.EAST; // Adjusted gridy
        inputPanel.add(new JLabel("Associate with Event:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.gridwidth = 2;
        // Ensure AddMartyrDialog.RegionDataWrapper is accessible (public static inner class)
        Vector<AddMartyrDialog.RegionDataWrapper> regionDataWrappers = new Vector<>();
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
        if (prisonerToEdit == null) return;
        nameField.setText(prisonerToEdit.getName());
        ageSpinner.setValue(prisonerToEdit.getAge());
        yearsInPrisonSpinner.setValue(prisonerToEdit.getYearsInPrison());
        releasedCheckBox.setSelected(prisonerToEdit.isReleased());

        if (prisonerToEdit.getRegionDataRef() != null) {
            AddMartyrDialog.RegionDataWrapper wrapperToSelect = new AddMartyrDialog.RegionDataWrapper(prisonerToEdit.getRegionDataRef());
            regionDataComboBox.setSelectedItem(wrapperToSelect);
        }
    }

    private void saveAction(ActionEvent e) {
        String name = nameField.getText().trim();
        int age = (Integer) ageSpinner.getValue();
        int yearsInPrison = (Integer) yearsInPrisonSpinner.getValue();
        boolean released = releasedCheckBox.isSelected();
        AddMartyrDialog.RegionDataWrapper selectedWrapper = (AddMartyrDialog.RegionDataWrapper) regionDataComboBox.getSelectedItem();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (selectedWrapper == null && regionDataComboBox.isEnabled()) {
            JOptionPane.showMessageDialog(this, "You must select an associated Region/Date event.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        RegionData selectedRegionData = (selectedWrapper != null) ? selectedWrapper.getRegionData() :
                                         (prisonerToEdit != null ? prisonerToEdit.getRegionDataRef() : null);
        if (selectedRegionData == null) {
             JOptionPane.showMessageDialog(this, "Associated Region/Date event is missing.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (prisonerToEdit == null) { // Add mode
            this.resultPrisoner = new Prisoner(name, age, yearsInPrison, released, selectedRegionData);
        } else { // Edit mode
            prisonerToEdit.setName(name);
            prisonerToEdit.setAge(age);
            prisonerToEdit.setYearsInPrison(yearsInPrison);
            prisonerToEdit.setReleased(released);
            prisonerToEdit.setRegionDataRef(selectedRegionData);
            this.resultPrisoner = prisonerToEdit;
        }
        setVisible(false);
        dispose();
    }

    private void cancelAction(ActionEvent e) {
        this.resultPrisoner = null;
        setVisible(false);
        dispose();
    }

    public Prisoner getResultPrisoner() {
        return this.resultPrisoner;
    }
}
