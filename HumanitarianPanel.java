// File: com/mycompany/javaproject/HumanitarianPanel.java
package com.mycompany.javaproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List; // Ensure this import is present
//import java.util.Optional;

public class HumanitarianPanel extends JPanel {
    private ImpactDataManager dataManager;
// Martyrs Components
    private JTable martyrsTable;
    private MartyrTableModel martyrTableModel;
    private JButton addMartyrButton;
    private JButton editMartyrButton;
    private JButton deleteMartyrButton;

    // Wounded Components
private JTable woundedTable;
private WoundedTableModel woundedTableModel;
private JButton addWoundedButton, editWoundedButton, deleteWoundedButton;

// Prisoners Components
    private JTable prisonersTable;
    private PrisonerTableModel prisonerTableModel;
    private JButton addPrisonerButton, editPrisonerButton, deletePrisonerButton;
   
    public HumanitarianPanel(ImpactDataManager dataManager) {
    this.dataManager = dataManager;
    setLayout(new BorderLayout()); // Main layout for HumanitarianPanel

    JTabbedPane tabbedPane = new JTabbedPane();

    JPanel martyrsSectionPanel = createMartyrsSection();
    JPanel woundedSectionPanel = createWoundedSection();
     JPanel prisonersSectionPanel = createPrisonersSection(); // For later

    tabbedPane.addTab("Martyrs", null, martyrsSectionPanel, "Manage Martyrs Records");
    tabbedPane.addTab("Wounded", null, woundedSectionPanel, "Manage Wounded Records");
    tabbedPane.addTab("Prisoners", null, prisonersSectionPanel, "Manage Prisoners Records");

    add(tabbedPane, BorderLayout.CENTER);

    // Initial data load
    refreshAllTables();
}

private void refreshAllTables() {
    refreshMartyrsTable();
    refreshWoundedTable();
    refreshPrisonersTable(); // For later
}

// In HumanitarianPanel.java

// --- Martyrs Section ---
private JPanel createMartyrsSection() {
    // Create the main panel for this section with a border layout and titled border
    JPanel sectionPanel = new JPanel(new BorderLayout(5, 5)); // 5px horizontal and vertical gap
    sectionPanel.setBorder(BorderFactory.createTitledBorder("Martyrs Records"));

    // Initialize the table model for martyrs.
    // Get the initial list of martyrs from the dataManager.
    // If getAllMartyrs() might return null, provide a default empty list.
    List<Martyr> initialMartyrs = dataManager.getAllMartyrs() != null ? dataManager.getAllMartyrs() : new ArrayList<>();
    martyrTableModel = new MartyrTableModel(initialMartyrs); // Pass the list to the model's constructor

    // Create the JTable using the custom table model
    martyrsTable = new JTable(martyrTableModel);

    // Configure common table properties using the helper method
    configureTable(martyrsTable); 

    // Add the table to a JScrollPane to make it scrollable if there are many rows
    JScrollPane scrollPane = new JScrollPane(martyrsTable);
    sectionPanel.add(scrollPane, BorderLayout.CENTER); // Add scroll pane to the center of this section panel

    // Create a panel for action buttons (Add, Edit, Delete)
    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Align buttons to the right

    // Initialize buttons
    addMartyrButton = new JButton("Add Martyr...");
    editMartyrButton = new JButton("Edit Selected Martyr...");
    deleteMartyrButton = new JButton("Delete Selected Martyr");

    // Initially, edit and delete buttons are disabled until a row is selected
    editMartyrButton.setEnabled(false);
    deleteMartyrButton.setEnabled(false);

    // Add buttons to the button panel
    buttonPanel.add(addMartyrButton);
    buttonPanel.add(editMartyrButton);
    buttonPanel.add(deleteMartyrButton);

    // Add the button panel to the bottom (SOUTH) of this section panel
    sectionPanel.add(buttonPanel, BorderLayout.SOUTH);

    // Add ActionListeners to the buttons
    addMartyrButton.addActionListener(this::handleAddMartyrAction);
    editMartyrButton.addActionListener(this::handleEditMartyrAction);
    deleteMartyrButton.addActionListener(this::handleDeleteMartyrAction);

    // Add a ListSelectionListener to the table to enable/disable edit and delete buttons
    // based on whether a row is selected or not.
    martyrsTable.getSelectionModel().addListSelectionListener(e -> {
        // getValueIsAdjusting() is true while the user is still dragging the mouse
        // to make a selection. We only want to react when the selection is final.
        if (!e.getValueIsAdjusting()) {
            // Check if any row is selected in the table
            boolean rowSelected = martyrsTable.getSelectedRow() != -1;
            editMartyrButton.setEnabled(rowSelected);
            deleteMartyrButton.setEnabled(rowSelected);
        }
    });
    
    return sectionPanel; // Return the fully constructed panel for the Martyrs section
}

 // --- Action Handlers for Martyrs ---
    private void handleAddMartyrAction(ActionEvent e) { // <<<< يجب أن يكون هذا التعريف موجودًا
        System.out.println("HumanitarianPanel: handleAddMartyrAction CALLED!");
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        List<RegionData> availableRegions = dataManager.getAllRegionsData();
        if (availableRegions == null || availableRegions.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No Region/Date entries exist. Please add a Region/Date entry first in the 'Overview & Regions' tab.",
                    "Cannot Add Martyr", JOptionPane.WARNING_MESSAGE);
            return;
        }

        AddMartyrDialog dialog = new AddMartyrDialog(owner, availableRegions);
        dialog.setVisible(true);

        Martyr newMartyr = dialog.getNewMartyr(); // Assuming getNewMartyr() is the correct method name in your dialog
        System.out.println("HumanitarianPanel: AddMartyrDialog closed. New martyr: " + (newMartyr != null ? newMartyr.getName() : "null"));

        if (newMartyr != null) {
            dataManager.addMartyr(newMartyr);
            System.out.println("HumanitarianPanel: Martyr added to dataManager. Attempting to refresh table.");
            refreshMartyrsTable(); // Make sure this method exists and works
            JOptionPane.showMessageDialog(this, "Martyr '" + newMartyr.getName() + "' added successfully.", "Martyr Added", JOptionPane.INFORMATION_MESSAGE);
        } else {
            System.out.println("HumanitarianPanel: No new martyr to add (dialog cancelled or error).");
        }
    }

    private void handleEditMartyrAction(ActionEvent e) { // <<<< يجب أن يكون هذا التعريف موجودًا
        System.out.println("HumanitarianPanel: handleEditMartyrAction CALLED!");

        int selectedViewRow = martyrsTable.getSelectedRow();
        if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a martyr to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = martyrsTable.convertRowIndexToModel(selectedViewRow);
        Martyr martyrToEdit = martyrTableModel.getMartyrAt(modelRow);

        if (martyrToEdit == null) {
            JOptionPane.showMessageDialog(this, "Could not retrieve martyr data for editing.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        List<RegionData> availableRegions = dataManager.getAllRegionsData();
        AddMartyrDialog dialog = new AddMartyrDialog(owner, availableRegions, martyrToEdit); // Using constructor for edit
        dialog.setVisible(true);

        Martyr resultFromDialog = dialog.getNewMartyr(); // Dialog returns the (potentially) modified object

        if (resultFromDialog != null) { // User saved changes
            // The resultFromDialog is the same instance as martyrToEdit, but with fields modified by the dialog.
            if (dataManager.updateMartyr(martyrToEdit, resultFromDialog)) { // Pass original and the (modified) result
                System.out.println("HumanitarianPanel: Martyr updated in dataManager. Refreshing table.");
                refreshMartyrsTable();
                JOptionPane.showMessageDialog(this, "Martyr '" + resultFromDialog.getName() + "' updated successfully.", "Update Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Could not update martyr: " + resultFromDialog.getName(), "Update Failed", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            System.out.println("HumanitarianPanel: Martyr edit dialog cancelled.");
        }
    }

    private void handleDeleteMartyrAction(ActionEvent e) { // <<<< يجب أن يكون هذا التعريف موجودًا
        System.out.println("HumanitarianPanel: handleDeleteMartyrAction CALLED!");
        int selectedViewRow = martyrsTable.getSelectedRow();
        if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a martyr to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = martyrsTable.convertRowIndexToModel(selectedViewRow);
        Martyr martyrToDelete = martyrTableModel.getMartyrAt(modelRow);

        if (martyrToDelete == null) {
            JOptionPane.showMessageDialog(this, "Could not retrieve martyr data for deletion.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete martyr: " + martyrToDelete.getName() + "?\nThis action cannot be undone.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirmation == JOptionPane.YES_OPTION) {
            boolean deleted = dataManager.deleteMartyr(martyrToDelete);
            if (deleted) {
                System.out.println("HumanitarianPanel: Martyr deleted from dataManager. Refreshing table.");
                refreshMartyrsTable();
                JOptionPane.showMessageDialog(this, "Martyr '" + martyrToDelete.getName() + "' deleted successfully.", "Deletion Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Could not delete martyr: " + martyrToDelete.getName(), "Deletion Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

   private JPanel createWoundedSection() {
    JPanel sectionPanel = new JPanel(new BorderLayout(5, 5));
    sectionPanel.setBorder(BorderFactory.createTitledBorder("Wounded Records"));
    List<Wounded> initialWounded = dataManager.getAllWounded() != null ? dataManager.getAllWounded() : new ArrayList<>();
    woundedTableModel = new WoundedTableModel(initialWounded);
    woundedTable = new JTable(woundedTableModel);
    configureTable(woundedTable);
    sectionPanel.add(new JScrollPane(woundedTable), BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    addWoundedButton = new JButton("Add Wounded...");
    editWoundedButton = new JButton("Edit Selected Wounded...");
    deleteWoundedButton = new JButton("Delete Selected Wounded");
    editWoundedButton.setEnabled(false); deleteWoundedButton.setEnabled(false);
    buttonPanel.add(addWoundedButton); buttonPanel.add(editWoundedButton); buttonPanel.add(deleteWoundedButton);
    sectionPanel.add(buttonPanel, BorderLayout.SOUTH);

    addWoundedButton.addActionListener(this::handleAddWoundedAction);
    editWoundedButton.addActionListener(this::handleEditWoundedAction);
    deleteWoundedButton.addActionListener(this::handleDeleteWoundedAction);
    woundedTable.getSelectionModel().addListSelectionListener(e -> updateButtonStates(woundedTable, editWoundedButton, deleteWoundedButton));
    return sectionPanel;
}

   
   public void refreshWoundedTable() {
    if (woundedTableModel != null && dataManager != null) {
        woundedTableModel.setData(dataManager.getAllWounded());
    }
}
  
   private void handleAddWoundedAction(ActionEvent e) {
    Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
    List<RegionData> availableRegions = dataManager.getAllRegionsData();
    if (availableRegions == null || availableRegions.isEmpty()) { /* ... show error ... */ return; }
    AddWoundedDialog dialog = new AddWoundedDialog(owner, availableRegions);
    dialog.setVisible(true);
    Wounded newWounded = dialog.getResultWounded();
    if (newWounded != null) {
        dataManager.addWounded(newWounded);
        refreshWoundedTable();
        JOptionPane.showMessageDialog(this, "Wounded '" + newWounded.getName() + "' added.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}

private void handleEditWoundedAction(ActionEvent e) {
    int selectedRow = woundedTable.getSelectedRow();
    if (selectedRow == -1) { /* ... show error ... */ return; }
    Wounded woundedToEdit = woundedTableModel.getWoundedAt(woundedTable.convertRowIndexToModel(selectedRow));
    if (woundedToEdit == null) { /* ... show error ... */ return; }
    Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
    AddWoundedDialog dialog = new AddWoundedDialog(owner, dataManager.getAllRegionsData(), woundedToEdit);
    dialog.setVisible(true);
    Wounded result = dialog.getResultWounded();
    if (result != null) { // User saved
        dataManager.updateWounded(woundedToEdit, result); // Pass original and (potentially modified) result
        refreshWoundedTable();
        JOptionPane.showMessageDialog(this, "Wounded '" + result.getName() + "' updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}

private void handleDeleteWoundedAction(ActionEvent e) {
    int selectedRow = woundedTable.getSelectedRow();
    if (selectedRow == -1) { /* ... show error ... */ return; }
    Wounded woundedToDelete = woundedTableModel.getWoundedAt(woundedTable.convertRowIndexToModel(selectedRow));
    if (woundedToDelete == null) { /* ... show error ... */ return; }
    int confirm = JOptionPane.showConfirmDialog(this, "Delete wounded: " + woundedToDelete.getName() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
        dataManager.deleteWounded(woundedToDelete);
        refreshWoundedTable();
        JOptionPane.showMessageDialog(this, "Wounded '" + woundedToDelete.getName() + "' deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
}



    // --- Prisoners Section (NEW) ---
    private JPanel createPrisonersSection() {
        JPanel sectionPanel = new JPanel(new BorderLayout(5, 5));
        sectionPanel.setBorder(BorderFactory.createTitledBorder("Prisoners Records"));

        List<Prisoner> initialPrisoners = dataManager.getAllPrisoners() != null ? dataManager.getAllPrisoners() : new ArrayList<>();
        prisonerTableModel = new PrisonerTableModel(initialPrisoners);
        prisonersTable = new JTable(prisonerTableModel);
        configureTable(prisonersTable); // Use helper method
        sectionPanel.add(new JScrollPane(prisonersTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addPrisonerButton = new JButton("Add Prisoner...");
        editPrisonerButton = new JButton("Edit Selected Prisoner...");
        deletePrisonerButton = new JButton("Delete Selected Prisoner");

        editPrisonerButton.setEnabled(false);
        deletePrisonerButton.setEnabled(false);

        buttonPanel.add(addPrisonerButton);
        buttonPanel.add(editPrisonerButton);
        buttonPanel.add(deletePrisonerButton);
        sectionPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add Action Listeners
        addPrisonerButton.addActionListener(this::handleAddPrisonerAction);
        editPrisonerButton.addActionListener(this::handleEditPrisonerAction);
        deletePrisonerButton.addActionListener(this::handleDeletePrisonerAction);

        // ListSelectionListener to enable/disable edit and delete buttons
        prisonersTable.getSelectionModel().addListSelectionListener(e -> 
            updateButtonStates(prisonersTable, editPrisonerButton, deletePrisonerButton)
        );
        
        return sectionPanel;
    }

    public void refreshPrisonersTable() {
        if (prisonerTableModel != null && dataManager != null) {
            prisonerTableModel.setData(dataManager.getAllPrisoners());
        }
    }

    private void handleAddPrisonerAction(ActionEvent e) {
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        List<RegionData> availableRegions = dataManager.getAllRegionsData();
        if (availableRegions == null || availableRegions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No Region/Date entries exist. Please add one first.", "Cannot Add Prisoner", JOptionPane.WARNING_MESSAGE);
            return;
        }
        AddPrisonerDialog dialog = new AddPrisonerDialog(owner, availableRegions);
        dialog.setVisible(true);
        Prisoner newPrisoner = dialog.getResultPrisoner();
        if (newPrisoner != null) {
            dataManager.addPrisoner(newPrisoner);
            refreshPrisonersTable();
            JOptionPane.showMessageDialog(this, "Prisoner '" + newPrisoner.getName() + "' added.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleEditPrisonerAction(ActionEvent e) {
        int selectedRow = prisonersTable.getSelectedRow();
        if (selectedRow == -1) { JOptionPane.showMessageDialog(this,"Please select a prisoner to edit.","No Selection",JOptionPane.WARNING_MESSAGE); return; }
        Prisoner prisonerToEdit = prisonerTableModel.getPrisonerAt(prisonersTable.convertRowIndexToModel(selectedRow));
        if (prisonerToEdit == null) { JOptionPane.showMessageDialog(this,"Could not retrieve prisoner data.","Error",JOptionPane.ERROR_MESSAGE); return; }
        
        Frame owner = (Frame) SwingUtilities.getWindowAncestor(this);
        AddPrisonerDialog dialog = new AddPrisonerDialog(owner, dataManager.getAllRegionsData(), prisonerToEdit);
        dialog.setVisible(true);
        Prisoner result = dialog.getResultPrisoner();
        if (result != null) { // User saved
            dataManager.updatePrisoner(prisonerToEdit, result); // Pass original and (potentially modified) result
            refreshPrisonersTable();
            JOptionPane.showMessageDialog(this, "Prisoner '" + result.getName() + "' updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleDeletePrisonerAction(ActionEvent e) {
        int selectedRow = prisonersTable.getSelectedRow();
        if (selectedRow == -1) { JOptionPane.showMessageDialog(this,"Please select a prisoner to delete.","No Selection",JOptionPane.WARNING_MESSAGE); return; }
        Prisoner prisonerToDelete = prisonerTableModel.getPrisonerAt(prisonersTable.convertRowIndexToModel(selectedRow));
        if (prisonerToDelete == null) { JOptionPane.showMessageDialog(this,"Could not retrieve prisoner data.","Error",JOptionPane.ERROR_MESSAGE); return; }

        int confirm = JOptionPane.showConfirmDialog(this, "Delete prisoner: " + prisonerToDelete.getName() + "?", "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            dataManager.deletePrisoner(prisonerToDelete);
            refreshPrisonersTable();
            JOptionPane.showMessageDialog(this, "Prisoner '" + prisonerToDelete.getName() + "' deleted.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }





// --- Helper methods ---
private void configureTable(JTable table) {
    table.setFillsViewportHeight(true);
    table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    // table.setAutoCreateRowSorter(true); // If you want sorting
}

private void updateButtonStates(JTable table, JButton editButton, JButton deleteButton) {
    boolean rowSelected = table.getSelectedRow() != -1;
    editButton.setEnabled(rowSelected);
    deleteButton.setEnabled(rowSelected);
}
    
    public void refreshMartyrsTable() {
        System.out.println("HumanitarianPanel: refreshMartyrsTable CALLED.");
        if (martyrTableModel != null && dataManager != null) {
            List<Martyr> updatedMartyrs = dataManager.getAllMartyrs();
            System.out.println("HumanitarianPanel: Fetched " + (updatedMartyrs != null ? updatedMartyrs.size() : "null list") + " martyrs from dataManager for table refresh.");
            martyrTableModel.setData(updatedMartyrs);
        } else {
            System.out.println("HumanitarianPanel: martyrTableModel or dataManager is null, cannot refresh.");
        }
    }
}