// File: com/mycompany/javaproject/EducationPanel.java
package com.mycompany.javaproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EducationPanel extends JPanel {
    private ImpactDataManager dataManager;

    // UI Components for selecting Region/Date
    private JComboBox<String> regionComboBox;
    private JSpinner yearSpinner;
    private JComboBox<String> monthComboBox;
    private JButton loadImpactsButton;

    // UI Components for displaying EducationImpacts
    private JTable educationImpactsTable;
    private EducationImpactTableModel educationImpactTableModel;
    private JButton addImpactButton;
    private JButton editImpactButton;
    private JButton deleteImpactButton;

    private String currentSelectedRegion = null;
    private String currentSelectedDateKey = null;

    public EducationPanel(ImpactDataManager dataManager) {
        this.dataManager = dataManager;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel selectionPanel = createSelectionPanel();
        add(selectionPanel, BorderLayout.NORTH);

        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        JPanel actionButtonPanel = createActionButtonPanel();
        add(actionButtonPanel, BorderLayout.SOUTH);

        addImpactButton.setEnabled(false);
        editImpactButton.setEnabled(false);
        deleteImpactButton.setEnabled(false);
    }

    private JPanel createSelectionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Select Region and Date to View/Manage Education Impact"));

        panel.add(new JLabel("Region:"));
        String[] regions = {"Gaza", "West Bank", "East Jerusalem"};
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

        loadImpactsButton = new JButton("Load Education Impacts");
        loadImpactsButton.addActionListener(this::handleLoadEducationImpactsAction);
        panel.add(loadImpactsButton);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5,5));
        educationImpactTableModel = new EducationImpactTableModel(new ArrayList<>());
        educationImpactsTable = new JTable(educationImpactTableModel);
        educationImpactsTable.setFillsViewportHeight(true);
        educationImpactsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(educationImpactsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createActionButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addImpactButton = new JButton("Add Education Impact...");
        editImpactButton = new JButton("Edit Selected Impact...");
        deleteImpactButton = new JButton("Delete Selected Impact");

        addImpactButton.addActionListener(this::handleAddImpactAction);
        editImpactButton.addActionListener(this::handleEditImpactAction);
        deleteImpactButton.addActionListener(this::handleDeleteImpactAction);
        
        educationImpactsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });

        panel.add(addImpactButton);
        panel.add(editImpactButton);
        panel.add(deleteImpactButton);
        return panel;
    }

    private void handleLoadEducationImpactsAction(ActionEvent e) {
        currentSelectedRegion = (String) regionComboBox.getSelectedItem();
        int year = (Integer) yearSpinner.getValue();
        String selectedMonthFull = (String) monthComboBox.getSelectedItem();

        if (currentSelectedRegion == null || selectedMonthFull == null) {
            JOptionPane.showMessageDialog(this, "Please select a valid region, year, and month.", "Selection Missing", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String monthStr = selectedMonthFull.substring(0, 2);
        currentSelectedDateKey = monthStr + "/" + year;

        if (!dataManager.findRegionData(currentSelectedRegion, currentSelectedDateKey).isPresent()) {
            JOptionPane.showMessageDialog(this,
                    "No base Region/Date entry found for " + currentSelectedRegion + " (" + currentSelectedDateKey + ").\n" +
                    "Please add it first via the 'Overview & Regions' tab.",
                    "Region/Date Entry Missing", JOptionPane.ERROR_MESSAGE);
            educationImpactTableModel.setData(new ArrayList<>());
            addImpactButton.setEnabled(false);
            return;
        }
        
        addImpactButton.setEnabled(true);
        System.out.println("Loading education impacts for: " + currentSelectedRegion + " " + currentSelectedDateKey);
        List<EducationImpact> impacts = dataManager.getEducationImpactsForRegion(currentSelectedRegion, currentSelectedDateKey);
        educationImpactTableModel.setData(impacts);
        if (impacts.isEmpty()){
            System.out.println("No education impacts found for the selection.");
        }
        updateButtonStates();
    }

    private void updateButtonStates() {
        boolean rowSelected = educationImpactsTable.getSelectedRow() != -1;
        // Add button is enabled if a region/date is loaded and its base RegionData entry exists
        addImpactButton.setEnabled(currentSelectedRegion != null && currentSelectedDateKey != null && 
                                   dataManager.findRegionData(currentSelectedRegion, currentSelectedDateKey).isPresent());
        editImpactButton.setEnabled(rowSelected);
        deleteImpactButton.setEnabled(rowSelected);
    }
    
    private void handleAddImpactAction(ActionEvent e) {
        if (currentSelectedRegion == null || currentSelectedDateKey == null) {
            JOptionPane.showMessageDialog(this, "Please load data for a specific region and date first.", "Region Not Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        AddEducationImpactDialog dialog = new AddEducationImpactDialog(owner, currentSelectedRegion, currentSelectedDateKey);
        dialog.setVisible(true);

        EducationImpact newImpact = dialog.getResultImpact();
        if (newImpact != null) {
            if (dataManager.addEducationImpactToRegion(currentSelectedRegion, currentSelectedDateKey, newImpact)) {
                refreshCurrentImpactsTable();
                JOptionPane.showMessageDialog(this, "Education impact added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add education impact.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleEditImpactAction(ActionEvent e) {
        int selectedRow = educationImpactsTable.getSelectedRow();
        if (selectedRow == -1) { JOptionPane.showMessageDialog(this,"Select an impact to edit.","No Selection",JOptionPane.WARNING_MESSAGE); return; }
        
        EducationImpact impactToEdit = educationImpactTableModel.getImpactAt(educationImpactsTable.convertRowIndexToModel(selectedRow));
        if (impactToEdit == null) { JOptionPane.showMessageDialog(this,"Could not get impact data.","Error",JOptionPane.ERROR_MESSAGE); return; }

        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        AddEducationImpactDialog dialog = new AddEducationImpactDialog(owner, currentSelectedRegion, currentSelectedDateKey, impactToEdit);
        dialog.setVisible(true);

        EducationImpact result = dialog.getResultImpact();
        if (result != null) {
            if (dataManager.updateEducationImpactInRegion(currentSelectedRegion, currentSelectedDateKey, impactToEdit, result)) {
                 refreshCurrentImpactsTable();
                 JOptionPane.showMessageDialog(this, "Education impact updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                 JOptionPane.showMessageDialog(this, "Failed to update education impact.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDeleteImpactAction(ActionEvent e) {
        int selectedRow = educationImpactsTable.getSelectedRow();
        if (selectedRow == -1) { JOptionPane.showMessageDialog(this,"Select an impact to delete.","No Selection",JOptionPane.WARNING_MESSAGE); return; }

        EducationImpact impactToDelete = educationImpactTableModel.getImpactAt(educationImpactsTable.convertRowIndexToModel(selectedRow));
        if (impactToDelete == null) { JOptionPane.showMessageDialog(this,"Could not get impact data.","Error",JOptionPane.ERROR_MESSAGE); return; }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete this education impact record?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            if (dataManager.deleteEducationImpactFromRegion(currentSelectedRegion, currentSelectedDateKey, impactToDelete)) {
                refreshCurrentImpactsTable();
                JOptionPane.showMessageDialog(this, "Education impact deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete education impact.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void refreshCurrentImpactsTable() {
        if (currentSelectedRegion != null && currentSelectedDateKey != null && educationImpactTableModel != null) {
            List<EducationImpact> impacts = dataManager.getEducationImpactsForRegion(currentSelectedRegion, currentSelectedDateKey);
            educationImpactTableModel.setData(impacts);
            updateButtonStates();
        } else if (educationImpactTableModel != null) { // Clear table if no valid selection
             educationImpactTableModel.setData(new ArrayList<>());
             updateButtonStates();
        }
    }
}