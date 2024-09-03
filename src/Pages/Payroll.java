package Pages;

import db.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Payroll extends JDialog {
    private JPanel contentPane;
    private JPanel subPanel;
    private JTextField txtSearch;
    private JButton searchBtn;
    private JTextField txtSalary;
    private JTextField txtName;
    private JTextField txtDepartment;
    private JTextField txtPosition;
    private JTextField txtWorkDay;
    private JTextField txtPH;
    private JTextField txtBonus;
    private JTextField txtInsurance;
    private JTextField txtRatePerDay;
    private JTextField txtRatePerHours;
    private JTextField txtTotalAmount;
    private JButton saveButton;
    private JButton exitButton;
    private JButton calculateButton;
    private String employeeIdSearch;
    private int workDay;
    private float ph;
    private float bonus;
    private float insurance;
    private float ratePerDay;
    private float ratePerHours;
    private float totalAmount;
    public Payroll() {
        setTitle("Payroll");
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1300,700));
        setLocationRelativeTo(null);
        setContentPane(contentPane);
        txtName.setEditable(false);
        txtDepartment.setEditable(false);
        txtPosition.setEditable(false);
        txtSalary.setEditable(false);
        txtTotalAmount.setEditable(false);

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Home().setVisible(true);
            }
        });
        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                employeeIdSearch = txtSearch.getText();
                ResultSet rs = getAnEmployee(employeeIdSearch);
                try{
                    if(!rs.next()){
                        JOptionPane.showMessageDialog(Payroll.this,"No employee Id found with the given Id!");
                    }else{
                        do{
                            txtName.setText(rs.getString("employee_name"));
                            txtDepartment.setText(rs.getString("department_name"));
                            txtPosition.setText(rs.getString("position_name"));
                            txtSalary.setText(String.valueOf(rs.getInt("base_salary")));
                        }while (rs.next());
                    }
                }catch (SQLException s){
                    s.printStackTrace();
                }
            }
        });
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ResultSet rs = getAnEmployee(employeeIdSearch);
                try{
                    if(!rs.next()){
                        JOptionPane.showMessageDialog(Payroll.this,"No employee Id found with the given Id!");
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
                            txtTotalAmount.setEditable(false);
                            txtTotalAmount.setText(String.valueOf(totalAmount));
                        }while (rs.next());
                    }
                }catch (SQLException s){
                    s.printStackTrace();
                } catch (NumberFormatException ex){
                    JOptionPane.showMessageDialog(Payroll.this, "Please enter a valid number.");
                }
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ResultSet rs = getAnEmployee(employeeIdSearch);
                try {
                    if(!rs.next()){
                        JOptionPane.showMessageDialog(Payroll.this,"No employee Id found with the given Id!");
                    }else{
                        do{
                            int employee_id = rs.getInt("employee_id");
                            if(workDay==0 && ph==0 && insurance==0&& bonus==0&& ratePerDay==0&& ratePerHours==0&&totalAmount==0){
                                JOptionPane.showMessageDialog(Payroll.this, "You might missed to click Calculate button or input all the field!");
                            }else{
                                if(addPayroll(workDay,ph,bonus,insurance,ratePerDay,ratePerHours,totalAmount,employee_id )){
                                    JOptionPane.showMessageDialog(Payroll.this, "Add employee's payroll is successfully.");
                                    dispose();
                                    new Home().setVisible(true);
                                }else{
                                    JOptionPane.showMessageDialog(Payroll.this, "You might missed something in the input field, please check and input again!");
                                    clearAllField();
                                    return;
                                }
                            }

                        }while (rs.next());
                    }
                }catch (SQLException s){
                    s.printStackTrace();
                }catch (NumberFormatException n){
                    JOptionPane.showMessageDialog(Payroll.this, "Type of number rejected from database!");
                }
            }
        });
    }
    private void clearAllField(){
        txtName.setText("");
        txtPosition.setText("");
        txtDepartment.setText("");
        txtSalary.setText("");
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
    private static ResultSet getAnEmployee(String employeeId){
        Connection con = DBConnection.getConnection();
        ResultSet rs=null;
        try{
            PreparedStatement data = con.prepareStatement("SELECT * FROM employee_management.employee e\n" +
                    "INNER JOIN employee_management.gender ON gender.gender_id = e.gender_id\n" +
                    "INNER JOIN employee_management.department de ON de.department_id = e.department_id\n" +
                    "INNER JOIN employee_management.position po ON po.position_id = e.position_id\n" +
                    "INNER JOIN employee_management.status ON status.status_id = e.status_id\n"+
                    "WHERE e.employee_id_card =?");
            data.setString(1, employeeId);
            rs = data.executeQuery();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return rs;
    }
    private static boolean addPayroll(int working_day, float ph, float bonus, float insurance, float rate_per_day, float rate_per_hours, float total_amount, int employee_id){
        Connection con = DBConnection.getConnection();
        ResultSet rs = null;
        try{
            PreparedStatement checkStmt = con.prepareStatement("SELECT * FROM employee_management.employee WHERE employee_id = ?");
            checkStmt.setInt(1, employee_id);
            rs = checkStmt.executeQuery();

            if (rs.next()) {
                // ID card already exists, show a message and return false
                JOptionPane.showMessageDialog(null, "This employee's payroll already exists.");
                return false;
            }
            PreparedStatement payrollStmt = con.prepareStatement("INSERT INTO employee_management.payroll(working_day, ph, bonus, insurance, rate_per_day, rate_per_hour, total_amount, employee_id) VALUES (?,?,?,?,?,?,?,?)");
            payrollStmt.setInt(1,working_day);
            payrollStmt.setFloat(2, ph);
            payrollStmt.setFloat(3, bonus);
            payrollStmt.setFloat(4, insurance);
            payrollStmt.setFloat(5,rate_per_day);
            payrollStmt.setFloat(6,rate_per_hours);
            payrollStmt.setFloat(7,total_amount);
            payrollStmt.setInt(8,employee_id);
            int rowAffected = payrollStmt.executeUpdate();
            return rowAffected>0;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
