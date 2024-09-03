package Pages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Home extends JDialog {
    private JPanel contentPane;
    private JButton addEmployeeButton;
    private JButton viewEmployeeButton;
    private JPanel titlePanel;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton allowanceButton;
    private JButton logoutButton;
    private JPanel subBottomPanel;
    private JPanel subTopPanel;

    public Home(){
        setTitle("Main");
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1300, 700));
        setLocationRelativeTo(null);
        setContentPane(contentPane);
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Login().setVisible(true);
            }
        });
        viewEmployeeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new View().setVisible(true);
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Update().setVisible(true);
            }
        });
        allowanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Payroll().setVisible(true);
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                new Report().setVisible(true);
            }
        });
    }
}

