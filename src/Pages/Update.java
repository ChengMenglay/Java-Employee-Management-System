package Pages;

import Constants.KeyValue;
import db.DBConnection;
import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Update extends JDialog {
    private JPanel contentPane;
    private JPanel subContentPanel;
    private JPanel leftPanel;
    private JPanel rightPanel;
    private JButton searchButton;
    private JTextField txtSearch;
    private JTextField txtEmployeeId;
    private JTextField txtName;
    private JComboBox genderComboBox;
    private JTextField txtEmail;
    private JTextField txtContact;
    private JTextField txtAddress;
    private JComboBox departmentComboBox;
    private JComboBox positionComboBox;
    private JTextField txtSalary;
    private JComboBox statusComboBox;
    private JTextField txtBankName;
    private JTextField txtBackAccount;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton exitButton;
    private JPanel btnPanel;
    private JPanel dobPicker;
    private JPanel dohPicker;
    private JButton addButton;
    private DatePicker dobDatePicker;
    private DatePicker dohDatePicker;
    private String txtSearchVal;
    private int anEmployeeId;
    public Update() {
        genderComboBox();
        departmentComboBox();
        positionComboBox();
        statusComboBox();
        dobDatePicker();
        dohDatePicker();
        setTitle("Employee Information");
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1300,700));
        setLocationRelativeTo(null);
        setContentPane(contentPane);
        ActionListener listener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Home().setVisible(true);
            }
        };
        updateButton.addActionListener(listener);
        deleteButton.addActionListener(listener);
        exitButton.addActionListener(listener);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                txtSearchVal = txtSearch.getText();
                ResultSet rs= getAnEmployee(txtSearchVal);
                try {
                    if(!rs.next()){
                        clearAllFields();
                        JOptionPane.showMessageDialog(null, "No employee found with the given ID card.");
                    }else {
                        do {
                            anEmployeeId= rs.getInt("employee_id");
                            txtEmployeeId.setText(rs.getString("employee_id_card"));
                            txtName.setText(rs.getString("employee_name"));
                            genderComboBox.setSelectedIndex(rs.getInt("gender_id"));
                            txtEmail.setText(rs.getString("email"));
                            txtContact.setText(rs.getString("phone_number"));
                            txtAddress.setText(rs.getString("address"));
                            departmentComboBox.setSelectedIndex(rs.getInt("department_id"));
                            positionComboBox.setSelectedIndex(rs.getInt("position_id"));

                            // Set the date in the DatePicker components
                            Date dobDate = rs.getDate("date_of_birth");
                            Date dohDate = rs.getDate("date_of_hiring");

                            if (dobDate != null) {
                                dobDatePicker.setDate(dobDate.toLocalDate());
                            } else {
                                dobDatePicker.clear(); // Clear the DatePicker if no date is available
                            }

                            if (dohDate != null) {
                                dohDatePicker.setDate(dohDate.toLocalDate());
                            } else {
                                dohDatePicker.clear(); // Clear the DatePicker if no date is available
                            }

                                txtSalary.setText(String.valueOf(rs.getInt("base_salary")));
                            statusComboBox.setSelectedIndex(rs.getInt("status_id"));
                            txtBankName.setText(rs.getString("bank_name"));
                            txtBackAccount.setText(rs.getString("bank_account_number"));
                        } while (rs.next());
                    }
                }catch (SQLException s){
                    s.printStackTrace();
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(txtEmployeeId.getText().isEmpty()){
                    JOptionPane.showMessageDialog(Update.this,"Please search and select an employee to delete!");
                }else{
                    int confirm = JOptionPane.showConfirmDialog(Update.this,"Are you sure that you want to remove this employee?","Confirm Delete", JOptionPane.YES_NO_OPTION);
                    if(confirm==JOptionPane.YES_OPTION){
                        try{
                            boolean deleted = deleteAnEmployee(txtEmployeeId.getText());
                            if(deleted){
                                JOptionPane.showMessageDialog(Update.this,"Employee removed successfully!");
                            }else{
                        JOptionPane.showMessageDialog(Update.this,"Fail to remove the employee!");
                            }
                        }catch (NumberFormatException ex){
                            JOptionPane.showMessageDialog(Update.this,"Invalid ID");
                        }
                    }
                }
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateAnEmployee(anEmployeeId);

            }
        });
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String employeeId = txtEmployeeId.getText();
                    String employeeName = txtName.getText();
                    KeyValue gender = (KeyValue) genderComboBox.getSelectedItem();
                    int genderId = gender.getKey();
                    String email = txtEmail.getText();
                    String contact = txtContact.getText();
                    String address = txtAddress.getText();
                    int salary = Integer.parseInt(txtSalary.getText().trim());
                    KeyValue status = (KeyValue) statusComboBox.getSelectedItem();
                    int statusId = status.getKey();
                    String bankName = txtBankName.getText();
                    String bankAccountNumber = txtBackAccount.getText();
                    // Retrieving LocalDate from DatePicker components
                    LocalDate dobLocalDate = ((DatePicker) dobPicker.getComponent(0)).getDate();
                    LocalDate dohLocalDate = ((DatePicker) dohPicker.getComponent(0)).getDate();

                    // Converting LocalDate to java.sql.Date
                    java.sql.Date dob = java.sql.Date.valueOf(dobLocalDate);
                    java.sql.Date doh = java.sql.Date.valueOf(dohLocalDate);
                    KeyValue department = (KeyValue) departmentComboBox.getSelectedItem();
                    int departmentId = department.getKey();
                    KeyValue position = (KeyValue) positionComboBox.getSelectedItem();
                    int positionId= position.getKey();
                    // Validation logic (this can be customized as needed)
                    if (employeeId.isEmpty() || employeeName.isEmpty() || genderId == 0 ||
                            email.isEmpty() || contact.isEmpty() || address.isEmpty() ||
                            salary <= 0 || statusId == 0 || bankName.isEmpty() ||
                            bankAccountNumber.isEmpty() || dobLocalDate == null || dohLocalDate == null ||
                            departmentId == 0 || positionId == 0) {

                        JOptionPane.showMessageDialog(Update.this, "You missed something! Please fill in all required fields.");
                    } else {
                        // Process and store the data
                        boolean insertEmployee= insertEmployeeData(employeeId,employeeName,genderId,email,contact,address,departmentId,positionId,dob,doh,salary,statusId,bankName,bankAccountNumber);
                        if(insertEmployee){
                            JOptionPane.showMessageDialog(Update.this,"Insert an employee data was successfully!");
                            dispose();
                            new Home().setVisible(true);
                        }else{
                            JOptionPane.showMessageDialog(Update.this,"Can't inset the employee data!");
                        }
                    }

                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(Update.this, "Please enter a valid number for the salary.");
                }
            }
        });
    }
    private void clearAllFields() {
        txtEmployeeId.setText("");
        txtName.setText("");
        genderComboBox.setSelectedIndex(0); // Assuming the first index is "Select gender"
        txtEmail.setText("");
        txtContact.setText("");
        txtAddress.setText("");
        departmentComboBox.setSelectedIndex(0); // Assuming the first index is "Select department"
        positionComboBox.setSelectedIndex(0); // Assuming the first index is "Select position"
        dobDatePicker.clear(); // Clear the DatePicker
        dohDatePicker.clear(); // Clear the DatePicker
        txtSalary.setText("");
        statusComboBox.setSelectedIndex(0); // Assuming the first index is "Select status"
        txtBankName.setText("");
        txtBackAccount.setText("");
    }
    private void dobDatePicker() {
        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra("yyyy-MM-dd");
        dobDatePicker= new DatePicker(dateSettings);
        dobPicker.add(dobDatePicker); // Add the date picker to the center of the panel
    }
    private void dohDatePicker() {
        DatePickerSettings dateSettings = new DatePickerSettings();
        dateSettings.setFormatForDatesCommonEra("yyyy-MM-dd");
        dohDatePicker = new DatePicker(dateSettings);
        dohPicker.add(dohDatePicker); // Add the date picker to the center of the panel
    }
    private void genderComboBox(){
        List<KeyValue>genderItems = getGender();
        for (KeyValue kw: genderItems) genderComboBox.addItem(kw);
    }
    private void departmentComboBox(){
        List<KeyValue> departmentItems= getDepartment();
        for(KeyValue kw: departmentItems) departmentComboBox.addItem(kw);
    }
    private void positionComboBox(){
        List<KeyValue> positionItems= getPosition();
        for(KeyValue kw: positionItems) positionComboBox.addItem(kw);
    }
    private void statusComboBox(){
        List<KeyValue> statusItems= getStatus();
        for(KeyValue kw: statusItems) statusComboBox.addItem(kw);
    }

    private static List<KeyValue> getGender(){
        Connection con = DBConnection.getConnection();
        List<KeyValue> genderList = new ArrayList<>();
        try{
            PreparedStatement genderStmt = con.prepareStatement("SELECT * FROM employee_management.gender");
            ResultSet rs = genderStmt.executeQuery();
            genderList.add(new KeyValue(0, "Select gender"));
            while (rs.next()){
                String genderName = rs.getString("gender_name");
                int genderId = rs.getInt("gender_id");
                genderList.add(new KeyValue(genderId,genderName));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return genderList;
    }
    private static List<KeyValue> getDepartment(){
        Connection con  = DBConnection.getConnection();
        List<KeyValue> departmentList = new ArrayList<>();
        try{
            PreparedStatement departmentStmt = con.prepareStatement("SELECT * FROM employee_management.department");
            ResultSet rs = departmentStmt.executeQuery();
            departmentList.add(new KeyValue(0, "Select department"));
            while (rs.next()){
                String departmentName= rs.getString("department_name");
                int departmentId = rs.getInt("department_id");
                departmentList.add(new KeyValue(departmentId,departmentName));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return departmentList;
    }
    private static List<KeyValue> getPosition(){
        Connection con  = DBConnection.getConnection();
        List<KeyValue> positionList = new ArrayList<>();
        try{
            PreparedStatement positionStmt = con.prepareStatement("SELECT * FROM employee_management.position");
            ResultSet rs = positionStmt.executeQuery();
            positionList.add(new KeyValue(0, "Select position"));
            while (rs.next()){
                String positionName= rs.getString("position_name");
                int positionId = rs.getInt("position_id");
                positionList.add(new KeyValue(positionId,positionName));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return positionList;
    }
    private static List<KeyValue> getStatus(){
        Connection con  = DBConnection.getConnection();
        List<KeyValue> statusList = new ArrayList<>();
        try{
            PreparedStatement departmentStmt = con.prepareStatement("SELECT * FROM employee_management.status");
            ResultSet rs = departmentStmt.executeQuery();
            statusList.add(new KeyValue(0, "Select status"));
            while (rs.next()){
                String statusName= rs.getString("status_name");
                int statusId = rs.getInt("status_id");
                statusList.add(new KeyValue(statusId,statusName));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return statusList;
    }
    private static ResultSet getAnEmployee(String EmployeeId){
        Connection con = DBConnection.getConnection();
        ResultSet rs =null;
        try {
            if(EmployeeId != null && !EmployeeId.isEmpty() && EmployeeId != ""){
                PreparedStatement anEmployeeStmt = con.prepareStatement("SELECT * FROM employee_management.employee e\n" +
                        "INNER JOIN employee_management.gender ON gender.gender_id = e.gender_id\n" +
                        "INNER JOIN employee_management.department de ON de.department_id = e.department_id\n" +
                        "INNER JOIN employee_management.position po ON po.position_id = e.position_id\n" +
                        "INNER JOIN employee_management.status ON status.status_id = e.status_id\n"+
                        "WHERE e.employee_id_card =?");
                anEmployeeStmt.setString(1,EmployeeId);
                rs= anEmployeeStmt.executeQuery();
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return rs;
    }
    public static boolean insertEmployeeData(String employee_id_card, String employee_name, int gender_id, String email, String phone_number, String address, int department_id, int position_id, Date date_of_birth, Date date_of_hiring, int base_salary, int status_id, String bank_name, String bank_account_number ){
        Connection con = DBConnection.getConnection();
        ResultSet rs = null;
        try{
            PreparedStatement checkStmt = con.prepareStatement("SELECT * FROM employee_management.employee WHERE employee_id_card = ?");
            checkStmt.setString(1, employee_id_card);
            rs = checkStmt.executeQuery();

            if (rs.next()) {
                // ID card already exists, show a message and return false
                JOptionPane.showMessageDialog(null, "Employee with ID card " + employee_id_card + " already exists.");
                return false;
            }
            String query="INSERT INTO employee_management.employee (employee_id_card, employee_name, gender_id, email, phone_number, address, department_id, position_id, date_of_birth, date_of_hiring, base_salary, status_id, bank_name, bank_account_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement insertStmt = con.prepareStatement(query);
            insertStmt.setString(1,employee_id_card);
            insertStmt.setString(2,employee_name);
            insertStmt.setInt(3, gender_id);
            insertStmt.setString(4,email);
            insertStmt.setString(5,phone_number);
            insertStmt.setString(6,address);
            insertStmt.setInt(7, department_id);
            insertStmt.setInt(8, position_id);
            insertStmt.setDate(9, date_of_birth);
            insertStmt.setDate(10, date_of_hiring);
            insertStmt.setInt(11, base_salary);
            insertStmt.setInt(12, status_id);
            insertStmt.setString(13, bank_name);
            insertStmt.setString(14, bank_account_number);
            int rowAffected= insertStmt.executeUpdate();
            return rowAffected>0;
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
    private static boolean deleteAnEmployee(String employeeId){
        Connection con = DBConnection.getConnection();
        try {
            PreparedStatement employeeIdStmt = con.prepareStatement("DELETE FROM employee_management.employee WHERE employee_id_card = ?");
            employeeIdStmt.setString(1, employeeId);
            int rowAffected = employeeIdStmt.executeUpdate();

            if (rowAffected > 0) {
                return true;
            } else {
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    private boolean updateAnEmployee(int employeeId) {
        Connection con = DBConnection.getConnection();
        boolean updated = false;
        try {
            PreparedStatement updateStmt = con.prepareStatement(
                    "UPDATE employee_management.employee SET " +
                            "employee_id_card = ?, employee_name = ?, gender_id = ?, email = ?, phone_number = ?, address = ?, " +
                            "department_id = ?, position_id = ?, date_of_birth = ?, date_of_hiring = ?, base_salary = ?, " +
                            "status_id = ?, bank_name = ?, bank_account_number = ? WHERE employee_id = ?"
            );
            // Setting the parameters with the values from the form fields
            updateStmt.setString(1, txtEmployeeId.getText());
            updateStmt.setString(2, txtName.getText());
            updateStmt.setInt(3, ((KeyValue) genderComboBox.getSelectedItem()).getKey());
            updateStmt.setString(4, txtEmail.getText());
            updateStmt.setString(5, txtContact.getText());
            updateStmt.setString(6, txtAddress.getText());
            updateStmt.setInt(7, ((KeyValue) departmentComboBox.getSelectedItem()).getKey());
            updateStmt.setInt(8, ((KeyValue) positionComboBox.getSelectedItem()).getKey());

            // Formatting and setting dates from DatePicker components
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            updateStmt.setString(9, dobDatePicker.getDate() != null ? dobDatePicker.getDate().format(formatter) : null);
            updateStmt.setString(10, dohDatePicker.getDate() != null ? dohDatePicker.getDate().format(formatter) : null);

            updateStmt.setInt(11, Integer.parseInt(txtSalary.getText()));
            updateStmt.setInt(12, ((KeyValue) statusComboBox.getSelectedItem()).getKey());
            updateStmt.setString(13, txtBankName.getText());
            updateStmt.setString(14, txtBackAccount.getText());

            // Setting the employeeId for the WHERE clause
            updateStmt.setInt(15, employeeId);

            // Executing the update
            int rowsAffected = updateStmt.executeUpdate();
            if (rowsAffected > 0) {
                updated = true;
                JOptionPane.showMessageDialog(this, "Employee updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update the employee.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return updated;
    }
    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
