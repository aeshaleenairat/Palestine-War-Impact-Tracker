// File: com/mycompany/javaproject/AddRegionDataDialog.java
package com.mycompany.javaproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar; // For year spinner default

public class AddRegionDataDialog extends JDialog {

    private JComboBox<String> regionComboBox;
    private JSpinner yearSpinner;
    private JComboBox<String> monthComboBox;
    private JTextArea siegeDescriptionArea;
    private JSpinner martyrsSpinner;
    private JSpinner woundedSpinner;
    private JSpinner prisonersSpinner;

    private JButton saveButton;
    private JButton cancelButton;

    private RegionData newRegionData; // To store the created RegionData if saved

    public AddRegionDataDialog(Frame owner) {
        super(owner, "Add New Region/Date Entry", true); // true for modal
        initComponents();
        pack(); // Adjusts dialog size to fit components
        setLocationRelativeTo(owner); // Center relative to the owner frame
        newRegionData = null; // Initialize as null, indicating no data saved yet
    }

    private void initComponents() {
        // Set main layout for the dialog's content pane
        setLayout(new BorderLayout(10, 10));
        // Add padding around the content pane
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // --- Input Panel: Holds all the input fields ---
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Padding around components
        gbc.fill = GridBagConstraints.HORIZONTAL; // Make components fill horizontal space

        // Row 0: Region
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST; // Align label to the right
        inputPanel.add(new JLabel("Region:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST; // Align component to the left
        gbc.gridwidth = 2; // Component spans 2 columns
        String[] regions = {"Gaza", "West Bank", "East Jerusalem"};
        regionComboBox = new JComboBox<>(regions);
        inputPanel.add(regionComboBox, gbc);
        gbc.gridwidth = 1; // Reset gridwidth for next components

        // Row 1: Year
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Year:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 2;
        SpinnerModel yearModel = new SpinnerNumberModel(Calendar.getInstance().get(Calendar.YEAR), 2000, 2030, 1);
        yearSpinner = new JSpinner(yearModel);
        yearSpinner.setEditor(new JSpinner.NumberEditor(yearSpinner, "#")); // No thousands separator
        inputPanel.add(yearSpinner, gbc);
        gbc.gridwidth = 1;

        // Row 2: Month
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Month:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 2;
        // Format "MM (MonthName)" for user-friendliness and easy parsing
        String[] months = {
                "01 (January)", "02 (February)", "03 (March)", "04 (April)",
                "05 (May)", "06 (June)", "07 (July)", "08 (August)",
                "09 (September)", "10 (October)", "11 (November)", "12 (December)"
        };
        monthComboBox = new JComboBox<>(months);
        inputPanel.add(monthComboBox, gbc);
        gbc.gridwidth = 1;

        // Row 3 & 4: Siege Description (spanning multiple rows in layout)
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.NORTHEAST; // Align label top-right, with textarea
        inputPanel.add(new JLabel("Siege Description:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.gridheight = 2; // Text area spans 2 rows vertically in the grid
        gbc.fill = GridBagConstraints.BOTH; // Allow textarea to expand in both directions
        gbc.weightx = 1.0; // Allow horizontal expansion
        gbc.weighty = 1.0; // Allow vertical expansion
        siegeDescriptionArea = new JTextArea(5, 30); // Rows, Columns hint
        siegeDescriptionArea.setLineWrap(true);
        siegeDescriptionArea.setWrapStyleWord(true);
        JScrollPane siegeScrollPane = new JScrollPane(siegeDescriptionArea);
        inputPanel.add(siegeScrollPane, gbc);
        // Reset constraints that affect other components
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0;
        gbc.weighty = 0;

        // Row 5: Initial Martyrs
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Initial Martyrs:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 2;
        martyrsSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1)); // Value, Min, Max, Step
        inputPanel.add(martyrsSpinner, gbc);
        gbc.gridwidth = 1;

        // Row 6: Initial Wounded
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Initial Wounded:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 2;
        woundedSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        inputPanel.add(woundedSpinner, gbc);
        gbc.gridwidth = 1;

        // Row 7: Initial Prisoners
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Initial Prisoners:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridwidth = 2;
        prisonersSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));
        inputPanel.add(prisonersSpinner, gbc);
        gbc.gridwidth = 1;

        // --- Button Panel: Holds Save and Cancel buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // Align buttons to the right
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        // Add action listeners using method references for conciseness
        saveButton.addActionListener(this::saveAction);
        cancelButton.addActionListener(this::cancelAction);

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Add the input panel and button panel to the dialog's content pane
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void saveAction(ActionEvent e) {
        String region = (String) regionComboBox.getSelectedItem();
        int year = (Integer) yearSpinner.getValue();
        String selectedMonthFull = (String) monthComboBox.getSelectedItem();
        // Extract the "MM" part from "MM (Month Name)"
        String monthStr = selectedMonthFull.substring(0, 2);

        // Construct dateKey, e.g., "05/2024"
        String dateKey = monthStr + "/" + year;
        String siegeDesc = siegeDescriptionArea.getText().trim();

        // Basic validation: region and siege description should not be empty
        if (region == null || siegeDesc.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Region and Siege Description cannot be empty.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return; // Stop processing if validation fails
        }

        // Create the new RegionData object
        this.newRegionData = new RegionData(dateKey, region);
        this.newRegionData.setSiegeDescription(siegeDesc);

        // Get initial WarStats values from spinners
        int martyrs = (Integer) martyrsSpinner.getValue();
        int wounded = (Integer) woundedSpinner.getValue();
        int prisoners = (Integer) prisonersSpinner.getValue();
        // Update the WarStats for the new RegionData object
        // The getWarStats() in RegionData returns an initialized WarStats object (all zeros)
        // The update() method in WarStats (as we defined it) replaces these values.
        this.newRegionData.getWarStats().update(martyrs, wounded, prisoners);

        setVisible(false); // Hide the dialog
        dispose();         // Release dialog resources
    }

    private void cancelAction(ActionEvent e) {
        this.newRegionData = null; // Ensure no data is returned if cancelled
        setVisible(false);
        dispose();
    }

    /**
     * Call this method after the dialog has been shown and closed
     * to retrieve the newly created RegionData object.
     * Returns null if the dialog was cancelled or closed without saving.
     *
     * @return The created RegionData object, or null if dialog was cancelled.
     */
    public RegionData getNewRegionData() {
        return this.newRegionData;
    }
}