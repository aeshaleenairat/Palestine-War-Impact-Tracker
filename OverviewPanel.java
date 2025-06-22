// File: com/mycompany/javaproject/OverviewPanel.java
package com.mycompany.javaproject;

import java.util.Optional;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent; // Required for ActionEvent parameter
import java.util.Calendar; // For default year in JSpinner

public class OverviewPanel extends JPanel {
    private ImpactDataManager dataManager;

    // UI Components
    private JComboBox<String> regionComboBox;
    private JSpinner yearSpinner;
    private JComboBox<String> monthComboBox;
    private JTextArea siegeStatusArea;
    private JTable warStatsTable; // We'll address TableModel later for better JTable handling
    private JButton addRegionEntryButton;
    private JButton editRegionEntryButton;
    private JButton deleteRegionEntryButton; // Added for completeness
    private JButton viewSiegeButton;

    public OverviewPanel(ImpactDataManager dataManager) {
    
   // System.out.println("%%%% OVERVIEW PANEL CONSTRUCTOR CALLED %%%%"); // رسالة طباعة واضحة
   // JOptionPane.showMessageDialog(null, "OverviewPanel Initializing!"); // مربع حوار واضح
    
    
        this.dataManager = dataManager;
        setLayout(new BorderLayout(10, 10)); // Main layout with spacing
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding for the panel

          initSelectionPanel();
    initDisplayArea();
    initActionPanel(); // This initializes the buttons

    // Add action listeners for buttons
    addRegionEntryButton.addActionListener(this::handleAddRegionEntryAction);
    editRegionEntryButton.addActionListener(this::handleEditSiegeDescAction);       // <<<< Ensure this is active
    deleteRegionEntryButton.addActionListener(this::handleDeleteRegionEntryAction); // <<<< Ensure this is active
    viewSiegeButton.addActionListener(e -> loadDataForSelection());

    loadDataForSelection();
    }

    private void initSelectionPanel() {
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Select Region and Date"));

        selectionPanel.add(new JLabel("Region:"));
        String[] regions = {"Gaza", "West Bank", "East Jerusalem"};
        regionComboBox = new JComboBox<>(regions);
        selectionPanel.add(regionComboBox);

        selectionPanel.add(new JLabel("Year:"));
        SpinnerModel yearModel = new SpinnerNumberModel(
                Calendar.getInstance().get(Calendar.YEAR), // Default to current year
                2000, // Min year
                2030, // Max year
                1     // Step
        );
        yearSpinner = new JSpinner(yearModel);
        yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "#")); // Format year without commas
        selectionPanel.add(yearSpinner);

        selectionPanel.add(new JLabel("Month:"));
        // Format "MM (MonthName)" for consistency with AddRegionDataDialog
        String[] months = {
                "01 (January)", "02 (February)", "03 (March)", "04 (April)",
                "05 (May)", "06 (June)", "07 (July)", "08 (August)",
                "09 (September)", "10 (October)", "11 (November)", "12 (December)"
        };
        monthComboBox = new JComboBox<>(months);
        selectionPanel.add(monthComboBox);

        viewSiegeButton = new JButton("Load/View Details");
        selectionPanel.add(viewSiegeButton);

        add(selectionPanel, BorderLayout.NORTH);
    }

    private void initDisplayArea() {
        JPanel displayPanel = new JPanel(new GridLayout(1, 2, 10, 10)); // Two columns for siege and stats

        // Siege Status Area
        JPanel siegePanel = new JPanel(new BorderLayout());
        siegePanel.setBorder(BorderFactory.createTitledBorder("Siege Status & Description"));
        siegeStatusArea = new JTextArea(10, 30);
        siegeStatusArea.setEditable(false);
        siegeStatusArea.setLineWrap(true);
        siegeStatusArea.setWrapStyleWord(true);
        JScrollPane siegeScrollPane = new JScrollPane(siegeStatusArea);
        siegePanel.add(siegeScrollPane, BorderLayout.CENTER);
        displayPanel.add(siegePanel);

        // WarStats Table Area
        JPanel warStatsPanel = new JPanel(new BorderLayout());
        warStatsPanel.setBorder(BorderFactory.createTitledBorder("Humanitarian Statistics (selected period)"));
        String[] columnNames = {"Statistic", "Count"};
        // Initial data for the table structure. Will be updated dynamically.
        Object[][] data = {{"Martyrs", 0}, {"Wounded", 0}, {"Prisoners", 0}};
        warStatsTable = new JTable(data, columnNames); // For now, using default table model
        warStatsTable.setEnabled(false); // Not directly editable in this view
        warStatsPanel.add(new JScrollPane(warStatsTable), BorderLayout.CENTER);
        displayPanel.add(warStatsPanel);

        add(displayPanel, BorderLayout.CENTER);
    }

    // In OverviewPanel.java

// In OverviewPanel.java
// In OverviewPanel.java
private void initActionPanel() {
    JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
    actionPanel.setBorder(BorderFactory.createTitledBorder("Actions for Region/Date Entry")); // Restore original title

    // Remove or comment out the test button if you want
    // JButton testButton = new JButton("TEST BUTTON APPEARS?");
    // testButton.setPreferredSize(new Dimension(200, 50));
    // actionPanel.add(testButton);

    addRegionEntryButton = new JButton("Add New Region/Date Data");
    actionPanel.add(addRegionEntryButton);

    editRegionEntryButton = new JButton("Edit Selected Siege Description");
    editRegionEntryButton.setEnabled(false); // Keep it initially disabled
    actionPanel.add(editRegionEntryButton);    // <<<< Ensure this line is active

    deleteRegionEntryButton = new JButton("Delete Selected Period's Data");
    deleteRegionEntryButton.setEnabled(false); // Keep it initially disabled
    actionPanel.add(deleteRegionEntryButton);  // <<<< Ensure this line is active

    add(actionPanel, BorderLayout.SOUTH);
    // System.out.println("%%%% initActionPanel CALLED %%%%"); // Optional: remove test print
}

    private void loadDataForSelection() {
        String selectedRegion = (String) regionComboBox.getSelectedItem();
        int year = (Integer) yearSpinner.getValue();
        String selectedMonthFull = (String) monthComboBox.getSelectedItem();

        // Basic validation for selected items
        if (selectedRegion == null || selectedMonthFull == null) {
            siegeStatusArea.setText("Please select a valid region, year, and month.");
            updateWarStatsTable(null);
            editRegionEntryButton.setEnabled(false);
            deleteRegionEntryButton.setEnabled(false);
            return;
        }

        // Extract "MM" from "MM (MonthName)"
        String monthStr = selectedMonthFull.substring(0, 2);
        String dateKey = monthStr + "/" + year; // e.g., "05/2024"

        dataManager.findRegionData(selectedRegion, dateKey).ifPresentOrElse(
            rd -> { // Data found for the selected region/date
                siegeStatusArea.setText(rd.getSiegeDescription().isEmpty() ? "No siege description available." : rd.getSiegeDescription());
                updateWarStatsTable(rd.getWarStats());
                editRegionEntryButton.setEnabled(true);    // Enable edit button
                deleteRegionEntryButton.setEnabled(true);  // Enable delete button
            },
            () -> { // No data found for the selected region/date
                siegeStatusArea.setText("No data found for " + selectedRegion + " on " + dateKey + ".\n" +
                                        "Click 'Add New Region/Date Data' to create this entry.");
                updateWarStatsTable(null); // Clear or zero out the stats table
                editRegionEntryButton.setEnabled(false);   // Disable edit button
                deleteRegionEntryButton.setEnabled(false); // Disable delete button
            }
        );
    }

    // Updates the JTable with WarStats.
    // A custom TableModel would be a better approach for complex tables.
    private void updateWarStatsTable(WarStats stats) {
        if (stats != null) {
            warStatsTable.setValueAt(stats.getMartyrs(), 0, 1);   // Row 0, Col 1
            warStatsTable.setValueAt(stats.getWounded(), 1, 1);   // Row 1, Col 1
            warStatsTable.setValueAt(stats.getPrisoners(), 2, 1); // Row 2, Col 1
        } else {
            warStatsTable.setValueAt(0, 0, 1);
            warStatsTable.setValueAt(0, 1, 1);
            warStatsTable.setValueAt(0, 2, 1);
        }
    }

    // Action handler for the "Add New Region/Date Data" button
    private void handleAddRegionEntryAction(ActionEvent e) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this); // Get parent frame for dialog
        AddRegionDataDialog dialog = new AddRegionDataDialog(owner);
        dialog.setVisible(true); // Show dialog (execution pauses here until dialog is closed)

        // After dialog is closed, get the new data (if any)
        RegionData newEntry = dialog.getNewRegionData();
        if (newEntry != null) { // User clicked "Save" and data was created
            // Check if this exact entry already exists (ImpactDataManager also does this, but good for immediate feedback)
            if (dataManager.findRegionData(newEntry.getRegion(), newEntry.getDate()).isPresent()) {
                JOptionPane.showMessageDialog(this,
                        "Data for " + newEntry.getRegion() + " on " + newEntry.getDate() + " already exists.\n" +
                        "Load the existing entry to view or edit it.",
                        "Entry Already Exists", JOptionPane.WARNING_MESSAGE);
            } else {
                dataManager.addRegionDataEntry(newEntry);
                JOptionPane.showMessageDialog(this,
                        "New data entry for " + newEntry.getRegion() + " on " + newEntry.getDate() + " added successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);

                // If the newly added entry matches the current selection in OverviewPanel, refresh the view
                String currentSelectedRegion = (String) regionComboBox.getSelectedItem();
                int currentYear = (Integer) yearSpinner.getValue();
                String currentMonthFull = (String) monthComboBox.getSelectedItem();
                String currentMonthStr = currentMonthFull.substring(0, 2);
                String currentDateKey = currentMonthStr + "/" + currentYear;

                if (newEntry.getRegion().equals(currentSelectedRegion) && newEntry.getDate().equals(currentDateKey)) {
                    loadDataForSelection();
                }
            }
        }
        // If newEntry is null, the user cancelled the dialog; do nothing further.
    }

    // Action handler for the "Edit Selected Siege Description" button
    private void handleEditSiegeDescAction(ActionEvent e) {
        String selectedRegion = (String) regionComboBox.getSelectedItem();
        int year = (Integer) yearSpinner.getValue();
        String selectedMonthFull = (String) monthComboBox.getSelectedItem();

        if (selectedRegion == null || selectedMonthFull == null) return;

        String monthStr = selectedMonthFull.substring(0, 2);
        String dateKey = monthStr + "/" + year;

        Optional<RegionData> rdOptional = dataManager.findRegionData(selectedRegion, dateKey);
        if (rdOptional.isPresent()) {
            RegionData rdToEdit = rdOptional.get();
            String currentSiegeDesc = rdToEdit.getSiegeDescription();

            // Use JOptionPane.showInputDialog - it returns Object for this overload
            Object inputResult = JOptionPane.showInputDialog(
                    this,
                    "Edit Siege Description for " + selectedRegion + " (" + dateKey + "):",
                    "Edit Siege Description",
                    JOptionPane.PLAIN_MESSAGE,
                    null, // Icon
                    null, // Selection values (null means text input)
                    currentSiegeDesc // Initial value
            );

            String newSiegeDesc = null;
            if (inputResult != null) { // User clicked OK and didn't cancel
                newSiegeDesc = (String) inputResult; // <<<< Line 227 area, now corrected with cast
            }

            if (newSiegeDesc != null && !newSiegeDesc.trim().equals(currentSiegeDesc.trim())) {
                rdToEdit.setSiegeDescription(newSiegeDesc.trim());
                JOptionPane.showMessageDialog(this, "Siege description updated.", "Update Successful", JOptionPane.INFORMATION_MESSAGE);
                loadDataForSelection();
            }
        } else {
            JOptionPane.showMessageDialog(this, "No data found for " + selectedRegion + " on " + dateKey + " to edit.", "Data Not Found", JOptionPane.WARNING_MESSAGE);
        }
    }

    // Action handler for the "Delete Selected Period's Data" button
    private void handleDeleteRegionEntryAction(ActionEvent e) {
        String selectedRegion = (String) regionComboBox.getSelectedItem();
        int year = (Integer) yearSpinner.getValue();
        String selectedMonthFull = (String) monthComboBox.getSelectedItem();

        if (selectedRegion == null || selectedMonthFull == null) return; // Safety check

        String monthStr = selectedMonthFull.substring(0, 2);
        String dateKey = monthStr + "/" + year;

        int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete all data for:\n" +
                        "Region: " + selectedRegion + "\n" +
                        "Date: " + dateKey + "\n\n" +
                        "This action cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            boolean deleted = dataManager.deleteRegionData(selectedRegion, dateKey);
            if (deleted) {
                JOptionPane.showMessageDialog(this,
                        "Data for " + selectedRegion + " on " + dateKey + " deleted successfully.",
                        "Deletion Successful", JOptionPane.INFORMATION_MESSAGE);
                loadDataForSelection(); // Refresh the view (data will be gone)
            } else {
                // This might happen if data was deleted by another process or a bug
                JOptionPane.showMessageDialog(this,
                        "Could not delete data (it may no longer exist).",
                        "Deletion Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}