// File: com/mycompany/javaproject/AddEducationImpactDialog.java
package com.mycompany.javaproject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class AddEducationImpactDialog extends JDialog {

    private JSpinner schoolsDestroyedSpinner;
    private JSpinner studentsDisplacedSpinner;
    private JCheckBox statusCheckBox;

    private JButton saveButton;
    private JButton cancelButton;

    private EducationImpact resultImpact;
    private final EducationImpact impactToEdit;

    public AddEducationImpactDialog(Frame owner, String regionName, String date) {
        this(owner, regionName, date, null);
    }

    public AddEducationImpactDialog(Frame owner, String regionName, String date, EducationImpact impactToEdit) {
        super(owner, 
              (impactToEdit == null ? "Add Education Impact for " : "Edit Education Impact for ") + regionName + " (" + date + ")", 
              true);
        this.impactToEdit = impactToEdit;
        initComponents();
        if (this.impactToEdit != null) {
            saveButton.setText("Update Impact");
            populateFieldsForEdit();
        } else {
            saveButton.setText("Save Impact");
        }
        pack();
        setLocationRelativeTo(owner);
        this.resultImpact = null;
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Schools Destroyed:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        schoolsDestroyedSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1000, 1));
        inputPanel.add(schoolsDestroyedSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Students Displaced:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        studentsDisplacedSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100000, 10));
        inputPanel.add(studentsDisplacedSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Operational Status:"), gbc);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        statusCheckBox = new JCheckBox("Operational / Partially Operational");
        inputPanel.add(statusCheckBox, gbc);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        saveButton = new JButton(); 
        cancelButton = new JButton("Cancel");
        saveButton.addActionListener(this::saveAction);
        cancelButton.addActionListener(this::cancelAction);
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void populateFieldsForEdit() {
        if (impactToEdit == null) return;
        schoolsDestroyedSpinner.setValue(impactToEdit.getSchoolDestroyed());
        studentsDisplacedSpinner.setValue(impactToEdit.getStudentDisplaced());
        statusCheckBox.setSelected(impactToEdit.isStatus());
    }

    private void saveAction(ActionEvent e) {
        int schools = (Integer) schoolsDestroyedSpinner.getValue();
        int students = (Integer) studentsDisplacedSpinner.getValue();
        boolean status = statusCheckBox.isSelected();

        if (impactToEdit == null) {
            this.resultImpact = new EducationImpact(schools, students, status);
        } else {
            impactToEdit.setSchoolDestroyed(schools);
            impactToEdit.setStudentDisplaced(students);
            impactToEdit.setStatus(status);
            this.resultImpact = impactToEdit;
        }
        setVisible(false);
        dispose();
    }

    private void cancelAction(ActionEvent e) {
        this.resultImpact = null;
        setVisible(false);
        dispose();
    }

    public EducationImpact getResultImpact() {
        return this.resultImpact;
    }
}