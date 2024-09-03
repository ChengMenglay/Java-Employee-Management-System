package Pages;

import db.DBConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

public class Report extends JDialog {
    private JPanel contentPane;
    private JTable payrollTable;
    private JTextField txtSearch;
    private JButton searchButton;
    private JButton calculateButton;
    private JButton updateButton;
    private JButton exitButton;
    private JTextField txtEmployeeId;
    private JTextField txtName;
    private JTextField txtWorkDay;
    private JTextField txtPH;
    private JTextField txtBonus;
    private JTextField txtInsurance;
    private JTextField txtRatePerDay;
    private JTextField txtRatePerHours;
    private JTextField txtTotalAmount;
    private JButton deleteButton;
    private JButton printButton;
    private JPanel startPicker;
    private JPanel endPicker;
    private JButton sortButton;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private int workDay;
    private float ph;
    private float bonus;
    private float insurance;
    private float ratePerDay;
    private float ratePerHours;
    private float totalAmount;
    private String employeeIdSearch;
    public Report() {
        startDatePicker();
        endDatePicker();
        setTitle("Report");
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1500,800));
        setLocationRelativeTo(null);
        txtTotalAmount.setEditable(false);
        setContentPane(contentPane);
        ResultSet rs = getPayroll(employeeIdSearch);
        txtEmployeeId.setEditable(false);
        txtName.setEditable(false);
        initialTable(rs);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                employeeIdSearch = txtSearch.getText();
                initialTable(getPayroll(employeeIdSearch));
                ResultSet rs = getPayroll(employeeIdSearch);
                try {
                    if (!rs.next()) { // Check if the ResultSet is empty
                        clearAllField();
                        JOptionPane.showMessageDialog(Report.this, "Employee given is not found, please try again!");
                        initialTable(getPayroll(""));
                    } else {
                        do{
                            txtEmployeeId.setText(rs.getString("employee_id_card"));
                            txtName.setText(rs.getString("employee_name"));
                            txtWorkDay.setText(String.valueOf(rs.getInt("working_day")));
                            txtPH.setText(String.valueOf(rs.getFloat("ph")));
                            txtBonus.setText(String.valueOf(rs.getFloat("bonus")));
                            txtInsurance.setText(String.valueOf(rs.getFloat("insurance")));
                            txtRatePerDay.setText(String.valueOf(rs.getFloat("rate_per_day")));
                            txtRatePerHours.setText(String.valueOf(rs.getFloat("rate_per_hour")));
                            txtTotalAmount.setText(String.valueOf(rs.getFloat("total_amount")));
                        }while (rs.next());
                    }
                } catch (SQLException ex) {
                    clearAllField();
                    ex.printStackTrace();
                }
            }
        });

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ResultSet rs = getPayroll(employeeIdSearch);
                try{
                    if(!rs.next()){
                        clearAllField();
                        JOptionPane.showMessageDialog(Report.this,"No employee Id found with the given Id!");
                    }else{
                        do{
                            workDay = Integer.parseInt(txtWorkDay.getText());
                            ph = Float.parseFloat(txtPH.getText());
                            bonus = Float.parseFloat(txtBonus.getText());
                            insurance = Float.parseFloat(txtInsurance.getText());
                            int salary = rs.getInt("base_salary");
                            ratePerDay = Float.parseFloat(txtRatePerDay.getText());
                            ratePerHours = Float.parseFloat(txtRatePerHours.getText());
                            totalAmount = (workDay*ratePerDay)+(ph*ratePerHours)+bonus+insurance+salary;
                            txtTotalAmount.setText(String.valueOf(totalAmount));
                            System.out.println(totalAmount);
                            txtTotalAmount.setEditable(false);
                        }while (rs.next());
                    }
                }catch (SQLException s){
                    s.printStackTrace();
                } catch (NumberFormatException ex){
                    JOptionPane.showMessageDialog(Report.this, "Please enter a valid number.");
                }
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(workDay==0 && ph==0 && insurance==0&& bonus==0&& ratePerDay==0&& ratePerHours==0&&totalAmount==0){
                    JOptionPane.showMessageDialog(Report.this, "You might missed to click Calculate button or input all the field!");
                }else{
                    if(updatePayroll()){
                        JOptionPane.showMessageDialog(Report.this,"Update a payroll data is successfully!");
                        clearAllField();
                        initialTable( getPayroll(employeeIdSearch));
                    }else{
                        clearAllField();
                        JOptionPane.showMessageDialog(Report.this, "Failed to update the employee.");
                    }
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(employeeIdSearch==null){
                    JOptionPane.showMessageDialog(Report.this,"No employee Id found with the given Id!");
                }else{
                    if(deletePayroll()){
                        JOptionPane.showMessageDialog(Report.this,"Delete a payroll data is successfully!");
                        initialTable(getPayroll(""));
                        clearAllField();
                    }else{
                        clearAllField();
                        JOptionPane.showMessageDialog(Report.this, "Failed to delete the employee.");
                    }
                }

            }
        });
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Home().setVisible(true);
            }
        });
        sortButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String startDate = startDatePicker.getDate() != null ? startDatePicker.getDate().toString() : null;
                String endDate = endDatePicker.getDate() != null ? endDatePicker.getDate().toString() : null;
                if (startDate != null && endDate != null) {
                    ResultSet rs = getPayrollByDate(startDate,endDate);
                    initialTable(rs);
                } else {
                    JOptionPane.showMessageDialog(Report.this, "Please select both start and end dates.");
                }
            }
        });
        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    boolean complete = payrollTable.print(JTable.PrintMode.FIT_WIDTH,
                            new MessageFormat("Payroll Report"),
                            new MessageFormat("Page {0}"));
                    if (complete) {
                        JOptionPane.showMessageDialog(null, "Printing Complete", "Print", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "Printing Cancelled", "Print", JOptionPane.WARNING_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Printing Failed: " + ex.getMessage(), "Print", JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        });
    }
    private void startDatePicker() {
        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra("yyyy-MM-dd");
        startDatePicker= new DatePicker(dateSettings);
        startPicker.add(startDatePicker); // Add the date picker to the center of the panel
    }
    private void endDatePicker() {
        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesBeforeCommonEra("yyyy-MM-dd");
        endDatePicker = new DatePicker(dateSettings);
        endPicker.add(endDatePicker);
    }
    private void clearAllField(){
        txtEmployeeId.setText("");
        txtName.setText("");
        txtWorkDay.setText("");
        txtPH.setText("");
        txtBonus.setText("");
        txtInsurance.setText("");
        txtRatePerDay.setText("");
        txtRatePerHours.setText("");
        txtSearch.setText("");
        txtTotalAmount.setText("");
        workDay=0;
        ph=0;
        insurance=0;
        bonus=0;
        ratePerDay=0;
        ratePerHours=0;
        totalAmount=0;
    }
    private void initialTable(ResultSet rs) {
        List<Object[]> payrollList = new ArrayList<>();
        String[] columns = {
                "Id Card", "Name", "Position", "Salary", "Working Day", "OT", "Bonus", "Insurance", "Rate Per Day", "Rate Per Hours", "Total Amount"
        };

        try {
            if(rs.next()){
                do{
                    String idCard = rs.getString("employee_id_card");
                    String name = rs.getString("employee_name");
                    String position = rs.getString("position_name");
                    int salary = rs.getInt("base_salary");
                    int workDay = rs.getInt("working_day");
                    float ph = rs.getFloat("ph");
                    float bonus = rs.getFloat("bonus");
                    float insurance = rs.getFloat("insurance");
                    float ratePerDay = rs.getFloat("rate_per_day");
                    float ratePerHour = rs.getFloat("rate_per_hour");
                    float totalAmount = rs.getFloat("total_amount");
                    // Add the data to the payrollList
                    payrollList.add(new Object[]{
                            idCard, name, position, salary, workDay, ph, bonus, insurance, ratePerDay, ratePerHour, totalAmount
                    });
                }while (rs.next());
            }
        } catch (SQLException s) {
            s.printStackTrace();
        }finally {
            try{
                if(rs != null) rs.close();
            }catch (SQLException s){
                s.printStackTrace();
            }

        }
        // Convert payrollList to an array and set it to the table model
        DefaultTableModel model = new DefaultTableModel(payrollList.toArray(new Object[0][0]), columns);
        payrollTable.setModel(model);
    }

    private static ResultSet getPayroll(String searchEmployeeId){
        Connection con = DBConnection.getConnection();
        ResultSet rs=null;
        if(searchEmployeeId != null && !searchEmployeeId.isEmpty()){
            try{
                PreparedStatement payrollStmt = con.prepareStatement("SELECT * \n" +
                        "FROM employee_management.payroll pay \n" +
                        "INNER JOIN employee_management.employee em ON em.employee_id = pay.employee_id \n" +
                        "INNER JOIN employee_management.position pos ON pos.position_id = em.position_id \n" +
                        "WHERE em.employee_id_card = ?");
                payrollStmt.setString(1,searchEmployeeId);
                rs = payrollStmt.executeQuery();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }else{
            try{
                PreparedStatement payrollStmt = con.prepareStatement("SELECT * FROM employee_management.payroll pay " +
                        "INNER JOIN employee_management.employee em ON em.employee_id = pay.employee_id " +
                        "INNER JOIN employee_management.position pos ON pos.position_id  =em.position_id");
                rs= payrollStmt.executeQuery();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        return rs;
    }
    private static ResultSet getPayrollByDate(String startDate, String endDate){
        Connection con = DBConnection.getConnection();
        ResultSet rs=null;
        if(startDate != null && endDate != null){
            try{
                PreparedStatement payrollStmt = con.prepareStatement("SELECT * FROM employee_management.payroll pay " +
                        "INNER JOIN employee_management.employee em ON em.employee_id = pay.employee_id " +
                        "INNER JOIN employee_management.position pos ON pos.position_id = em.position_id AND pay.created_at BETWEEN ? AND ? ");
                payrollStmt.setString(1,startDate);
                payrollStmt.setString(2,endDate);
                rs = payrollStmt.executeQuery();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        return rs;
    }
    private boolean updatePayroll(){
        Connection con =DBConnection.getConnection();
        try{
            PreparedStatement payrollStmt = con.prepareStatement("UPDATE employee_management.payroll pay\n" +
                    "JOIN employee_management.employee em ON em.employee_id = pay.employee_id\n" +
                    "SET pay.working_day = ?, \n" +
                    "    pay.ph = ?, \n" +
                    "    pay.bonus = ?, \n" +
                    "    pay.insurance = ?, \n" +
                    "    pay.rate_per_day = ?, \n" +
                    "    pay.rate_per_hour = ?, \n" +
                    "    pay.total_amount = ?\n" +
                    "WHERE em.employee_id_card = ?;");
            payrollStmt.setInt(1,workDay);
            payrollStmt.setFloat(2, ph);
            payrollStmt.setFloat(3,bonus);
            payrollStmt.setFloat(4,insurance);
            payrollStmt.setFloat(5,ratePerDay);
            payrollStmt.setFloat(6,ratePerHours);
            payrollStmt.setFloat(7,totalAmount);
            payrollStmt.setString(8,employeeIdSearch);
            int rowAffected = payrollStmt.executeUpdate();
            return rowAffected>0;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    private boolean deletePayroll(){
        Connection con = DBConnection.getConnection();
        try{
            PreparedStatement payrollStmt = con.prepareStatement("DELETE pay \n" +
                    "FROM employee_management.payroll pay\n" +
                    "JOIN employee_management.employee em ON em.employee_id = pay.employee_id\n" +
                    "WHERE em.employee_id_card = ?;");
            payrollStmt.setString(1,employeeIdSearch);
            int rowAffected  = payrollStmt.executeUpdate();
            return rowAffected>0;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
