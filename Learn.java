import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Learn extends JFrame{
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
    private JComboBox boxCourses;
    private JButton navPrev;
    private JButton navNext;
    private JLabel textQuestion;
    private JLabel textAnswer;
    private JMenuItem menuItemSettings;

    private int currentCourse;
    private int currentQuestion = 0;

    private Conn conn = new Conn();

    private int activeUserId = 0;
    private String activeUserFullname = "";
    private int activeUserRole = 0;


    private void displayLesson(boolean move) {
        boolean needGetLesson = true;
        int countQuestions = 0;

        if (move == true) {
            try {
                countQuestions = conn.getCountQuestionsByCourse(currentCourse);
            }
            catch (ClassNotFoundException e) {}
            catch (SQLException sqle) {}
            currentQuestion++;
            if (countQuestions <= currentQuestion)
                navNext.setEnabled(false);
            else
                navNext.setEnabled(true);

            if (currentQuestion > 1)
                navPrev.setEnabled(true);
            else
                navPrev.setEnabled(false);
        } else {
            if (currentQuestion > 0) {
                currentQuestion--;
                navNext.setEnabled(true);
            } else {
                needGetLesson = false;
            }
        }

        if (currentQuestion < 2)
            navPrev.setEnabled(false);
        else
            navPrev.setEnabled(true);

        if (needGetLesson == true) {
            try {
                ResultSet lesson = conn.getCourseLesson(currentCourse, currentQuestion);
                String txtA = "";
                textQuestion.setText("");
                textAnswer.setText("");
                while (lesson.next()) {
                    if (textQuestion.getText() == "")
                        textQuestion.setText(lesson.getString(1));
                    if (txtA != "")
                        txtA += "<br>";
                    txtA += lesson.getString(2);
                }
                textAnswer.setText("<html>" + txtA + "</html>");
                if (textQuestion.getText() == "")
                    currentQuestion--;
            }
            catch (ClassNotFoundException e) {}
            catch (SQLException sqle) {}
        }

    }

    public Learn() {

        try {
            this.activeUserId = conn.getActiveUser();
            this.activeUserFullname = conn.getUserFullname(this.activeUserId);
            this.activeUserRole = conn.getUserRole(this.activeUserId);
        }
        catch(ClassNotFoundException e) {}
        catch (SQLException sqle){}

        setTitle("Обучение");

        setContentPane(rootPanel);

        Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

        menuTest.setCursor(handCursor);
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

            menuItemTestList.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    AdminTestList admin_test_list = new AdminTestList();
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
            menuItemSettings.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    AdminSettings admin_settings = new AdminSettings();
                    dispose();
                }
            });
        }


        try {
            ResultSet courses = conn.getCourses();
            while (courses.next())
                boxCourses.addItem(new ComboItem(courses.getInt(1), courses.getString(2)));
            currentCourse = conn.getFirstCourse();
            displayLesson(true);
        }
        catch(ClassNotFoundException e) {}
        catch (SQLException sqle){}

        boxCourses.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object item = boxCourses.getSelectedItem();
                currentCourse = ((ComboItem)item).getKey();
                currentQuestion = 0;
                displayLesson(true);
            }
        });


        pack();
        setSize(800, 500);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setVisible(true);

        navPrev.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayLesson(false);
            }
        });
        navNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayLesson(true);
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

        // back to main
        menuItemExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainForm main_form = new MainForm();
                dispose();
            }
        });

    }

}
