// File: com/mycompany/javaproject/HealthcarePanel.java
package com.mycompany.javaproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HealthcarePanel extends JPanel {
    private ImpactDataManager dataManager;

    // UI Components for selecting Region/Date
    private JComboBox<String> regionComboBox;
    private JSpinner yearSpinner;
    private JComboBox<String> monthComboBox;
    private JButton loadImpactsButton;

    // UI Components for displaying HealthcareImpacts
    private JTable healthcareImpactsTable;
    private HealthcareImpactTableModel healthcareImpactTableModel;
    private JButton addImpactButton;
    private JButton editImpactButton;
    private JButton deleteImpactButton;

    private String currentSelectedRegion = null;
    private String currentSelectedDateKey = null;

    public HealthcarePanel(ImpactDataManager dataManager) {
        this.dataManager = dataManager;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel for Region/Date selection
        JPanel selectionPanel = createSelectionPanel();
        add(selectionPanel, BorderLayout.NORTH);

        // Center panel for the table of impacts
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        // Bottom panel for action buttons
        JPanel actionButtonPanel = createActionButtonPanel();
        add(actionButtonPanel, BorderLayout.SOUTH);

        // Initial state: buttons disabled until a region/date is loaded
        addImpactButton.setEnabled(false);
        editImpactButton.setEnabled(false);
        deleteImpactButton.setEnabled(false);
    }

    private JPanel createSelectionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Select Region and Date to View/Manage Healthcare Impact"));

        panel.add(new JLabel("Region:"));
        String[] regions = {"Gaza", "West Bank", "East Jerusalem"}; // Should come from a central source ideally
        regionComboBox = new JComboBox<>(regions);
        panel.add(regionComboBox);

        panel.add(new JLabel("Year:"));
        SpinnerModel yearModel = new SpinnerNumberModel(Calendar.getInstance().get(Calendar.YEAR), 2000, 2030, 1);
        yearSpinner = new JSpinner(yearModel);
        yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "#"));
        panel.add(yearSpinner);

        panel.add(new JLabel("Month:"));
        String[] months = {"01 (January)", "02 (February)", "03 (March)", "04 (April)", "05 (May)", "06 (June)",
                           "07 (July)", "08 (August)", "09 (September)", "10 (October)", "11 (November)", "12 (December)"};
        monthComboBox = new JComboBox<>(months);
        panel.add(monthComboBox);

        loadImpactsButton = new JButton("Load Healthcare Impacts");
        loadImpactsButton.addActionListener(this::handleLoadHealthcareImpactsAction);
        panel.add(loadImpactsButton);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5,5));
        // Initialize with an empty list initially
        healthcareImpactTableModel = new HealthcareImpactTableModel(new ArrayList<>());
        healthcareImpactsTable = new JTable(healthcareImpactTableModel);
        healthcareImpactsTable.setFillsViewportHeight(true);
        healthcareImpactsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(healthcareImpactsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createActionButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addImpactButton = new JButton("Add Healthcare Impact...");
        editImpactButton = new JButton("Edit Selected Impact...");
        deleteImpactButton = new JButton("Delete Selected Impact");

        addImpactButton.addActionListener(this::handleAddImpactAction);
        editImpactButton.addActionListener(this::handleEditImpactAction);
        deleteImpactButton.addActionListener(this::handleDeleteImpactAction);
        
        healthcareImpactsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean rowSelected = healthcareImpactsTable.getSelectedRow() != -1;
                editImpactButton.setEnabled(rowSelected);
                deleteImpactButton.setEnabled(rowSelected);
            }
        });

        panel.add(addImpactButton);
        panel.add(editImpactButton);
        panel.add(deleteImpactButton);
        return panel;
    }

    private void handleLoadHealthcareImpactsAction(ActionEvent e) {
        currentSelectedRegion = (String) regionComboBox.getSelectedItem();
        int year = (Integer) yearSpinner.getValue();
        String selectedMonthFull = (String) monthComboBox.getSelectedItem();

        if (currentSelectedRegion == null || selectedMonthFull == null) {
            JOptionPane.showMessageDialog(this, "Please select a valid region, year, and month.", "Selection Missing", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String monthStr = selectedMonthFull.substring(0, 2);
        currentSelectedDateKey = monthStr + "/" + year;

        // Check if RegionData entry exists for this selection
        if (!dataManager.findRegionData(currentSelectedRegion, currentSelectedDateKey).isPresent()) {
            JOptionPane.showMessageDialog(this,
                    "No base Region/Date entry found for " + currentSelectedRegion + " (" + currentSelectedDateKey + ").\n" +
                    "Please add it first via the 'Overview & Regions' tab before adding healthcare impacts.",
                    "Region/Date Entry Missing", JOptionPane.ERROR_MESSAGE);
            healthcareImpactTableModel.setData(new ArrayList<>()); // Clear table
            addImpactButton.setEnabled(false); // Disable add if no region data
            return;
        }
        
        // Enable Add button now that a valid region/date is selected (and exists)
        addImpactButton.setEnabled(true);
        System.out.println("Loading healthcare impacts for: " + currentSelectedRegion + " " + currentSelectedDateKey);
        List<HealthcareImpact> impacts = dataManager.getHealthcareImpactsForRegion(currentSelectedRegion, currentSelectedDateKey);
        healthcareImpactTableModel.setData(impacts);
        if (impacts.isEmpty()){
            System.out.println("No healthcare impacts found for the selection.");
        }
        // Reset edit/delete button states as table data has changed
        updateButtonStates();
    }

    private void updateButtonStates() {
        boolean rowSelected = healthcareImpactsTable.getSelectedRow() != -1;
        editImpactButton.setEnabled(rowSelected);
        deleteImpactButton.setEnabled(rowSelected);
    }
    
    private void handleAddImpactAction(ActionEvent e) {
        if (currentSelectedRegion == null || currentSelectedDateKey == null) {
            JOptionPane.showMessageDialog(this, "Please load data for a specific region and date first.", "Region Not Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        AddHealthcareImpactDialog dialog = new AddHealthcareImpactDialog(owner, currentSelectedRegion, currentSelectedDateKey);
        dialog.setVisible(true);

        HealthcareImpact newImpact = dialog.getResultImpact();
        if (newImpact != null) {
            if (dataManager.addHealthcareImpactToRegion(currentSelectedRegion, currentSelectedDateKey, newImpact)) {
                refreshCurrentImpactsTable();
                JOptionPane.showMessageDialog(this, "Healthcare impact added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add healthcare impact.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleEditImpactAction(ActionEvent e) {
        int selectedRow = healthcareImpactsTable.getSelectedRow();
        if (selectedRow == -1) { JOptionPane.showMessageDialog(this,"Select an impact to edit.","No Selection",JOptionPane.WARNING_MESSAGE); return; }
        
        HealthcareImpact impactToEdit = healthcareImpactTableModel.getImpactAt(healthcareImpactsTable.convertRowIndexToModel(selectedRow));
        if (impactToEdit == null) { JOptionPane.showMessageDialog(this,"Could not get impact data.","Error",JOptionPane.ERROR_MESSAGE); return; }

        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        AddHealthcareImpactDialog dialog = new AddHealthcareImpactDialog(owner, currentSelectedRegion, currentSelectedDateKey, impactToEdit);
        dialog.setVisible(true);

        HealthcareImpact result = dialog.getResultImpact(); // This is the impactToEdit instance with modified fields
        if (result != null) {
            // The dialog modifies the impactToEdit object directly.
            // The updateHealthcareImpactInRegion method can just confirm its existence or handle more complex scenarios.
            // For now, we assume the object in the list (impactToEdit) is already updated.
            // We just need to trigger a table refresh if visual update is needed beyond model's internal state.
            if (dataManager.updateHealthcareImpactInRegion(currentSelectedRegion, currentSelectedDateKey, impactToEdit, result)) {
                 refreshCurrentImpactsTable();
                 JOptionPane.showMessageDialog(this, "Healthcare impact updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                 JOptionPane.showMessageDialog(this, "Failed to update healthcare impact.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDeleteImpactAction(ActionEvent e) {
        int selectedRow = healthcareImpactsTable.getSelectedRow();
        if (selectedRow == -1) { JOptionPane.showMessageDialog(this,"Select an impact to delete.","No Selection",JOptionPane.WARNING_MESSAGE); return; }

        HealthcareImpact impactToDelete = healthcareImpactTableModel.getImpactAt(healthcareImpactsTable.convertRowIndexToModel(selectedRow));
        if (impactToDelete == null) { JOptionPane.showMessageDialog(this,"Could not get impact data.","Error",JOptionPane.ERROR_MESSAGE); return; }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete this healthcare impact record?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dataManager.deleteHealthcareImpactFromRegion(currentSelectedRegion, currentSelectedDateKey, impactToDelete)) {
                refreshCurrentImpactsTable();
                JOptionPane.showMessageDialog(this, "Healthcare impact deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete healthcare impact.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void refreshCurrentImpactsTable() {
        if (currentSelectedRegion != null && currentSelectedDateKey != null && healthcareImpactTableModel != null) {
            List<HealthcareImpact> impacts = dataManager.getHealthcareImpactsForRegion(currentSelectedRegion, currentSelectedDateKey);
            healthcareImpactTableModel.setData(impacts);
            updateButtonStates(); // Update button states after table refresh
        } else {
             healthcareImpactTableModel.setData(new ArrayList<>()); // Clear table if no region/date selected
             updateButtonStates();
        }
    }
}