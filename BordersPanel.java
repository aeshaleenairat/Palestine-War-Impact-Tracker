// File: com/mycompany/javaproject/BordersPanel.java
package com.mycompany.javaproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BordersPanel extends JPanel {
    private ImpactDataManager dataManager;

    // UI Components for selecting Region/Date
    private JComboBox<String> regionComboBox;
    private JSpinner yearSpinner;
    private JComboBox<String> monthComboBox;
    private JButton loadBordersButton;

    // UI Components for displaying Border information
    private JTable bordersTable;
    private BorderTableModel borderTableModel; // Make sure BorderTableModel.java exists
    private JButton addBorderButton;
    private JButton editBorderButton;
    private JButton deleteBorderButton;

    private String currentSelectedRegion = null;
    private String currentSelectedDateKey = null;
    private RegionData currentRegionDataObject = null; // To store the loaded RegionData object

    public BordersPanel(ImpactDataManager dataManager) {
        this.dataManager = dataManager;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top panel for Region/Date selection
        JPanel selectionPanel = createSelectionPanel();
        add(selectionPanel, BorderLayout.NORTH);

        // Center panel for the table of borders
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        // Bottom panel for action buttons
        JPanel actionButtonPanel = createActionButtonPanel();
        add(actionButtonPanel, BorderLayout.SOUTH);

        // Initial state: buttons disabled until a region/date is loaded
        addBorderButton.setEnabled(false);
        editBorderButton.setEnabled(false);
        deleteBorderButton.setEnabled(false);
    }

    private JPanel createSelectionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Select Region and Date to View/Manage Border Statuses"));

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

        loadBordersButton = new JButton("Load Border Statuses");
        loadBordersButton.addActionListener(this::handleLoadBordersAction);
        panel.add(loadBordersButton);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(5,5));
        borderTableModel = new BorderTableModel(new ArrayList<>()); // Initialize with empty list
        bordersTable = new JTable(borderTableModel);
        bordersTable.setFillsViewportHeight(true);
        bordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(bordersTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createActionButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addBorderButton = new JButton("Add Border Info...");
        editBorderButton = new JButton("Edit Selected Border...");
        deleteBorderButton = new JButton("Delete Selected Border");

        addBorderButton.addActionListener(this::handleAddBorderAction);
        editBorderButton.addActionListener(this::handleEditBorderAction);
        deleteBorderButton.addActionListener(this::handleDeleteBorderAction);
        
        bordersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });

        panel.add(addBorderButton);
        panel.add(editBorderButton);
        panel.add(deleteBorderButton);
        return panel;
    }

    private void handleLoadBordersAction(ActionEvent e) {
        currentSelectedRegion = (String) regionComboBox.getSelectedItem();
        int year = (Integer) yearSpinner.getValue();
        String selectedMonthFull = (String) monthComboBox.getSelectedItem();

        if (currentSelectedRegion == null || selectedMonthFull == null) {
            JOptionPane.showMessageDialog(this, "Please select a valid region, year, and month.", "Selection Missing", JOptionPane.WARNING_MESSAGE);
            currentRegionDataObject = null; // Reset
            borderTableModel.setData(new ArrayList<>()); // Clear table
            updateButtonStates();
            return;
        }
        String monthStr = selectedMonthFull.substring(0, 2);
        currentSelectedDateKey = monthStr + "/" + year;

        // Fetch the RegionData object itself
        currentRegionDataObject = dataManager.findRegionData(currentSelectedRegion, currentSelectedDateKey).orElse(null);

        if (currentRegionDataObject == null) {
            JOptionPane.showMessageDialog(this,
                    "No base Region/Date entry found for " + currentSelectedRegion + " (" + currentSelectedDateKey + ").\n" +
                    "Please add it first via the 'Overview & Regions' tab before managing borders.",
                    "Region/Date Entry Missing", JOptionPane.ERROR_MESSAGE);
            borderTableModel.setData(new ArrayList<>()); // Clear table
        } else {
            System.out.println("Loading border info for: " + currentSelectedRegion + " " + currentSelectedDateKey);
            // Get borders directly from the loaded RegionData object
            List<Border> borders = currentRegionDataObject.getBorderInfo(); 
            borderTableModel.setData(borders);
            if (borders.isEmpty()){
                System.out.println("No border info found for the selection.");
            }
        }
        updateButtonStates(); // Update button states based on whether RegionData is loaded
    }

    private void updateButtonStates() {
        boolean regionDataLoaded = (currentRegionDataObject != null);
        addBorderButton.setEnabled(regionDataLoaded); // Can add if a RegionData is loaded

        boolean rowSelected = bordersTable.getSelectedRow() != -1;
        editBorderButton.setEnabled(regionDataLoaded && rowSelected);
        deleteBorderButton.setEnabled(regionDataLoaded && rowSelected);
    }
    
    private void handleAddBorderAction(ActionEvent e) {
        if (currentRegionDataObject == null) { // Check if a RegionData context is loaded
            JOptionPane.showMessageDialog(this, "Please load data for a specific region and date first.", "Context Missing", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        // Pass the currently loaded RegionData object to the dialog
        AddBorderDialog dialog = new AddBorderDialog(owner, currentRegionDataObject);
        dialog.setVisible(true);

        Border newBorder = dialog.getResultBorder();
        if (newBorder != null) {
            // The dialog already associated newBorder with currentRegionDataObject
            // We just need to ensure it's added to the list within currentRegionDataObject
            // ImpactDataManager.addBorderInfoToRegion handles adding to the list AND ensures correct back-ref
            if (dataManager.addBorderInfoToRegion(currentSelectedRegion, currentSelectedDateKey, newBorder)) {
                refreshCurrentBordersTable();
                JOptionPane.showMessageDialog(this, "Border info '" + newBorder.getBorderName() + "' added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                // This might happen if currentRegionDataObject became null between check and call, or other DM error
                JOptionPane.showMessageDialog(this, "Failed to add border info.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleEditBorderAction(ActionEvent e) {
        if (currentRegionDataObject == null) { JOptionPane.showMessageDialog(this,"Load region/date first.","Error",JOptionPane.ERROR_MESSAGE); return; }
        int selectedRow = bordersTable.getSelectedRow();
        if (selectedRow == -1) { JOptionPane.showMessageDialog(this,"Select a border to edit.","No Selection",JOptionPane.WARNING_MESSAGE); return; }
        
        Border borderToEdit = borderTableModel.getBorderAt(bordersTable.convertRowIndexToModel(selectedRow));
        if (borderToEdit == null) { JOptionPane.showMessageDialog(this,"Could not get border data.","Error",JOptionPane.ERROR_MESSAGE); return; }

        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        // Pass the currentRegionDataObject and the borderToEdit
        AddBorderDialog dialog = new AddBorderDialog(owner, currentRegionDataObject, borderToEdit);
        dialog.setVisible(true);

        Border result = dialog.getResultBorder(); // This is the borderToEdit instance with modified fields
        if (result != null) {
            // The dialog modified borderToEdit's fields.
            // ImpactDataManager.updateBorderInfoInRegion will find this object in the list and confirm its state or re-set.
            if (dataManager.updateBorderInfoInRegion(currentSelectedRegion, currentSelectedDateKey, borderToEdit, result)) {
                 refreshCurrentBordersTable();
                 JOptionPane.showMessageDialog(this, "Border info '" + result.getBorderName() + "' updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                 JOptionPane.showMessageDialog(this, "Failed to update border info.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDeleteBorderAction(ActionEvent e) {
        if (currentRegionDataObject == null) { JOptionPane.showMessageDialog(this,"Load region/date first.","Error",JOptionPane.ERROR_MESSAGE); return; }
        int selectedRow = bordersTable.getSelectedRow();
        if (selectedRow == -1) { JOptionPane.showMessageDialog(this,"Select a border to delete.","No Selection",JOptionPane.WARNING_MESSAGE); return; }

        Border borderToDelete = borderTableModel.getBorderAt(bordersTable.convertRowIndexToModel(selectedRow));
        if (borderToDelete == null) { JOptionPane.showMessageDialog(this,"Could not get border data.","Error",JOptionPane.ERROR_MESSAGE); return; }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete border: " + borderToDelete.getBorderName() + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // We pass currentRegionDataObject to ensure we delete from the correct context
            if (dataManager.deleteBorderInfoFromRegion(currentSelectedRegion, currentSelectedDateKey, borderToDelete)) {
                refreshCurrentBordersTable();
                JOptionPane.showMessageDialog(this, "Border info '" + borderToDelete.getBorderName() + "' deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete border info.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    
    
    private void refreshCurrentBordersTable() {
        if (currentRegionDataObject != null && borderTableModel != null) {
            // Get borders directly from the loaded RegionData object's list
            List<Border> borders = currentRegionDataObject.getBorderInfo();
            borderTableModel.setData(borders);
        } else if (borderTableModel != null) {
             borderTableModel.setData(new ArrayList<>()); // Clear table
        }
        updateButtonStates(); // Always update button states after a refresh
    }
}