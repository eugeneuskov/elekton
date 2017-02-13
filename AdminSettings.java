import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

public class AdminSettings extends JFrame {
    private JPanel rootPanel;
    private JPanel menuPanel;
    private JMenuBar menuBar;
    private JMenuItem menuItemLearn;
    private JMenu menuTest;
    private JMenuItem menuItemStartTest;
    private JMenuItem menuItemRemoteTest;
    private JMenu menuAdmin;
    private JMenuItem menuItemTestList;
    private JMenuItem menuItemCourses;
    private JMenuItem menuItemTickets;
    private JMenu menuReference;
    private JMenuItem menuItemHelp;
    private JMenuItem menuItemAbout;
    private JMenuItem menuItemExit;
    private JMenuItem menuItemSettings;
    private JPanel contentPanel;
    private JTextField markFiveValue;
    private JTextField markFourValue;
    private JTextField markThreeValue;
    private JCheckBox randomTest;
    private JTextField wayToDataBase;
    private JButton saveSettingsButton;
    private JTextField activationKeyValue;
    private JButton setActivate;
    private JLabel activationResponseLabel;
    private JTextField saveFilePath;
    private JButton openDialog;
    private JFileChooser dialog = new JFileChooser();

    private Conn conn = new Conn();
    private App app = new App();

    private int activeUserId = 0;
    private String activeUserFullname = "";
    private int activeUserRole = 0;
    private GridBagLayout gbl = new GridBagLayout();

    public AdminSettings() {

        try {
            this.activeUserId = conn.getActiveUser();
            this.activeUserFullname = conn.getUserFullname(this.activeUserId);
            this.activeUserRole = conn.getUserRole(this.activeUserId);
        }
        catch(ClassNotFoundException e) {}
        catch (SQLException sqle){}

        setTitle("Настройки программы");

        setContentPane(rootPanel);

        Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

        menuItemLearn.setCursor(handCursor);
        menuItemStartTest.setCursor(handCursor);
        menuItemRemoteTest.setCursor(handCursor);
        menuReference.setCursor(handCursor);
        menuItemHelp.setCursor(handCursor);
        menuItemAbout.setCursor(handCursor);
        menuItemExit.setCursor(handCursor);

        if (this.activeUserRole == 1) {
            menuAdmin.setVisible(true);
            menuItemTestList.setCursor(handCursor);
            menuItemCourses.setCursor(handCursor);
            menuItemTickets.setCursor(handCursor);
        }

        try {
            ResultSet settings = conn.getSettings();
            while (settings.next()) {
                if (settings.getString(2).equals("mark_five"))
                    markFiveValue.setText(settings.getString(3));
                else if (settings.getString(2).equals("mark_four"))
                    markFourValue.setText(settings.getString(3));
                else if (settings.getString(2).equals("mark_three"))
                    markThreeValue.setText(settings.getString(3));
                else if (settings.getString(2).equals("random_test"))
                    if (settings.getString(3) == "1")
                        randomTest.setSelected(true);
                    else
                        randomTest.setSelected(false);
                else if (settings.getString(2).equals("save_file_path"))
                    saveFilePath.setText(settings.getString(3));
            }
        }
        catch(ClassNotFoundException cnfe) {}
        catch (SQLException sqle) {}

        try {
            ResultSet license = app.getLicenseValue();
            String licenseKey = license.getString(1);
            String licenseResponse = license.getString(2);
            if (licenseResponse.equals("1")) {
                activationKeyValue.setText(licenseKey);
                activationKeyValue.setEditable(false);
                setActivate.setVisible(false);
                activationResponseLabel.setText("Программа активирована");
            } else {
                if (licenseKey.equals("")) {
                    activationResponseLabel.setText("Программа без лицензии");
                } else {
                    activationResponseLabel.setText("Не корректный ключ активации");
                    activationKeyValue.setText(licenseKey);
                }
                setActivate.setVisible(true);
                activationKeyValue.setEditable(true);

                setActivate.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String activationKey = activationKeyValue.getText();
                        if (!activationKey.equals("")) {
                            try {
                                String licenseResult = app.checkLicenseKey(activationKey);
                                try {
                                    conn.setActivationProgramm(activationKey, licenseResult);
                                }
                                catch (ClassNotFoundException cnfe) {}
                                catch (SQLException sqle) {}
                                if (licenseResult.equals("fail")) {
                                    activationResponseLabel.setText("Не корректный или не действительный ключ активации");
                                } else if (licenseResult.equals("success")) {
                                    activationResponseLabel.setText("Программа успешно активирована");
                                    activationKeyValue.setEditable(false);
                                    setActivate.setVisible(false);
                                }
                            } catch (Exception ee) {}
                        }
                    }
                });
            }
        }
        catch (ClassNotFoundException cnfe) {}
        catch (SQLException sqle) {}

        dialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        dialog.setApproveButtonText("Выбрать");
        dialog.setDialogTitle("Выберите директорию для сохранения docx-файлов");
        dialog.setDialogType(JFileChooser.OPEN_DIALOG);
        dialog.setMultiSelectionEnabled(false);
        dialog.setCurrentDirectory(new File(saveFilePath.getText()));

        pack();
        setSize(800, 500);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setVisible(true);


        // go to learn
        menuItemLearn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Learn learn = new Learn();
                dispose();
            }
        });

        // go to test
        menuItemStartTest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Test test = new Test();
                dispose();
            }
        });

        // admin
        menuItemTestList.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdminTestList admin_tests = new AdminTestList();
                dispose();
            }
        });
        menuItemCourses.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdminCourses admin_courses = new AdminCourses();
                dispose();
            }
        });
        menuItemTickets.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdminTickets admin_tickets = new AdminTickets(0);
                dispose();
            }
        });

        // back to main
        menuItemExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainForm main_form = new MainForm();
                dispose();
            }
        });

        saveSettingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String randomValue = "0";
                if (randomTest.isSelected() == true)
                    randomValue = "1";
                Map<String, String> settingsMap = new HashMap<String, String>();
                settingsMap.put("mark_five", markFiveValue.getText());
                settingsMap.put("mark_four", markFourValue.getText());
                settingsMap.put("mark_three", markThreeValue.getText());
                settingsMap.put("random_test", randomValue);
                settingsMap.put("save_file_path", saveFilePath.getText());

                try {
                    conn.setSettings(settingsMap);
                }
                catch(ClassNotFoundException cnfe) {}
                catch (SQLException sqle) {}
            }
        });
        openDialog.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.showOpenDialog(contentPanel);
                saveFilePath.setText(dialog.getSelectedFile().toString());
            }
        });
    }

}
