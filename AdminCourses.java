import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.List;

public class AdminCourses extends JFrame {
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
    private JPanel contentPanel;
    private JButton buttonCourseAdd;
    private JPanel actionPanel;
    private JTextField textField1;
    private JButton buttonCourseSearch;
    private JMenuItem menuItemSettings;
    private JPanel coursesPanel;
    private int countCourses = 0;
    private JPanel[] listPane = new JPanel[countCourses];

    private Conn conn = new Conn();
    private App app = new App();

    private int activeUserId = 0;
    private String activeUserFullname = "";
    private int activeUserRole = 0;


    private void addCoursePanel(String courseName, int dataId, int id) {
        JTextField textCourses  = new JTextField(courseName);
        textCourses.setPreferredSize(new Dimension(400, 24));
        RowButton saveButton    = new RowButton("Переименовать", dataId, id);
        RowButton lessonsButton = new RowButton("Перейти к урокам", dataId, id);
        RowButton deleteButton  = new RowButton("Удалить", dataId, id);
        GridBagConstraints c    = new GridBagConstraints();
        c.fill      = GridBagConstraints.HORIZONTAL;
        c.weightx   = 0.0;
        c.gridx     = 0;
        c.gridy     = id;
        c.insets    = new Insets(10,0,0,0);
        listPane[id] = new JPanel();
        listPane[id].setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        listPane[id].setBackground(new Color(219, 244, 249));
        listPane[id].add(textCourses);
        listPane[id].add(Box.createHorizontalGlue());
        listPane[id].add(saveButton);
        listPane[id].add(Box.createRigidArea(new Dimension(10, 0)));
        listPane[id].add(deleteButton);

        contentPanel.add(listPane[id], c);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    conn.updateCourse(saveButton.getDataId(), textCourses.getText());
                }
                catch(ClassNotFoundException cnfe) {}
                catch (SQLException sqle){}
            }
        });
        lessonsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdminTickets admin_tickets = new AdminTickets(lessonsButton.getDataId());
                dispose();
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    conn.deleteCourse(deleteButton.getDataId());
                    contentPanel.remove(listPane[deleteButton.getIndex()]);
                    contentPanel.revalidate();
                    contentPanel.repaint();
                }
                catch(ClassNotFoundException cnfe) {}
                catch (SQLException sqle){}
            }
        });
    }

    public AdminCourses() {

        try {
            this.activeUserId = conn.getActiveUser();
            this.activeUserFullname = conn.getUserFullname(this.activeUserId);
            this.activeUserRole = conn.getUserRole(this.activeUserId);
        }
        catch(ClassNotFoundException e) {}
        catch (SQLException sqle){}

        setTitle("Администрирование курсов обучения");

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
            ResultSet courses = conn.getCourses();
            countCourses      = conn.getCountCourses();
            listPane = app.resizePane(listPane, countCourses, false);
            int id = 0;
            while(courses.next()) {
                int dataId = courses.getInt(1);
                String courseName = courses.getString(2);
                addCoursePanel(courseName, dataId, id);
                id++;
            }
        }
        catch(ClassNotFoundException e) {}
        catch (SQLException sqle){}

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
                AdminTestList admin_test_list = new AdminTestList();
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
        menuItemSettings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdminSettings admin_settings = new AdminSettings();
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


        // actions

        buttonCourseAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newCourseName = (String)JOptionPane.showInputDialog(
                        AdminCourses.this,
                        "Наименование нового курса:",
                        "Создание курса",
                        JOptionPane.PLAIN_MESSAGE);
                if (newCourseName != "") {
                    try {
                        int id = conn.addCourse(newCourseName);
                        countCourses++;
                        listPane = app.resizePane(listPane, countCourses, true);
                        addCoursePanel(newCourseName, id, countCourses - 1);
                        contentPanel.revalidate();
                        contentPanel.repaint();
                    }
                    catch(ClassNotFoundException cnfe) {}
                    catch (SQLException sqle){}
                }
            }
        });
    }

}
