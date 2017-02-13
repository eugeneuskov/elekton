import javax.swing.*;
import javax.xml.transform.Result;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


public class Test extends JFrame {
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
    private JComboBox boxCourses;
    private JButton startTestButton;
    private JPanel contentPanel;
    private JLabel questionLabel;
    private JPanel answersBox;
    private JMenuItem menuItemSettings;
    private JButton goToNextButton;
    private JLabel nameRandomCourse;
    private JLabel chooseCourses;
    private JLabel testLabel;

    private JLabel labelQuestion = new JLabel();
    private int currentCourse;
    private int currentQuestion;
    private Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
    private int countQuestion;
    private int progressTest = 0;
    private int rightAnswers = 0;
    private ArrayList<Integer> answeredQuestions = new ArrayList<Integer>();
    private int countAnswers = 0;
    private JPanel[] answersPane = new JPanel[countAnswers];
    private App app = new App();
    private GridBagLayout gbl = new GridBagLayout();
    private Color answerColor = new Color(0, 0, 0);
    private Color choosedColor = new Color(22, 189, 92);
    private Font testFont = new Font("", Font.PLAIN, 16);
    private int idTest;
    private String testData;

    private Conn conn = new Conn();

    private int activeUserId = 0;
    private String activeUserFullname = "";
    private int activeUserRole = 0;
    private int userAnswer = 0;
    private boolean resumeTest = false;


    private GridBagConstraints setDefaultC() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill               = GridBagConstraints.NONE;
        c.anchor             = GridBagConstraints.WEST;
        c.weightx            = 1.0;
        c.weighty            = 0.0;
        c.gridheight         = 1;
        c.gridwidth          = GridBagConstraints.REMAINDER;
        c.ipadx              = 0;
        c.ipady              = 0;
        c.gridx              = GridBagConstraints.RELATIVE;
        c.gridy              = GridBagConstraints.RELATIVE;
        c.insets             = new Insets(20,20,0,20);

        return c;
    }

    private void displayQuestion(ResultSet[] questionData, GridBagConstraints c) throws ClassNotFoundException, SQLException {
        ResultSet question   = questionData[0];
        ResultSet answers    = questionData[1];
        userAnswer           = 0;
        goToNextButton.setEnabled(false);
        while (question.next()) {
            currentQuestion = question.getInt("id");
            answeredQuestions.add(answeredQuestions.size(), currentQuestion);
            labelQuestion.setFont(testFont);
            labelQuestion.setText(question.getString("text_question"));
        }
        gbl.setConstraints(labelQuestion, c);
        answersBox.add(labelQuestion, c);

        app.clearPanel(answersPane, answersBox);
        countAnswers = conn.getCountAnswersByQuestion(currentQuestion);
        answersPane  = app.resizePane(answersPane, countAnswers, false);
        int id = 0;
        while (answers.next()) {
            RowArea txt_answer = new RowArea(answers.getInt("id"));
            txt_answer.setText(answers.getString("text_answer"));
            txt_answer.setCursor(handCursor);
            txt_answer.setBackground(new Color(219, 244, 249));
            txt_answer.setFont(testFont);
            txt_answer.setForeground(answerColor);
            txt_answer.setLineWrap(true);
            txt_answer.setWrapStyleWord(true);
            txt_answer.setEditable(false);
            Dimension answerLabelSize = txt_answer.getPreferredSize();
            answerLabelSize.width = 1000;
            txt_answer.setPreferredSize(answerLabelSize);
            gbl.setConstraints(txt_answer, c);

            txt_answer.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (txt_answer.getForeground() != choosedColor)
                        txt_answer.setForeground(new Color(2, 91, 119));
                }
            });
            txt_answer.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseExited(MouseEvent e) {
                    if (txt_answer.getForeground() != choosedColor)
                        txt_answer.setForeground(answerColor);
                }
            });
            txt_answer.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    userAnswer = txt_answer.getDataId();
                    app.resetAnswerTextColor(answersPane);
                    txt_answer.setForeground(choosedColor);
                    goToNextButton.setEnabled(true);
                }
            });

            answersPane[id] = new JPanel();
            answersPane[id].setBackground(new Color(219, 244, 249));
            answersPane[id].add(txt_answer);
            answersBox.add(answersPane[id], c);
            id++;
        }

        answersBox.revalidate();
        answersBox.repaint();
    }

    private void startTest() {
        rightAnswers = 0;
        progressTest = 0;
        answeredQuestions.clear();
        testData = "{\"testData\":[";
        try {
            GridBagConstraints c = setDefaultC();
            idTest = conn.startTest(activeUserId, currentCourse);
            ResultSet[] questionData = conn.getTestQuestion(answeredQuestions, currentCourse);
            displayQuestion(questionData, c);

            resumeTest = true;
            goToNextButton.setVisible(resumeTest);
        }
        catch(ClassNotFoundException cnfe) {}
        catch (SQLException sqle){}
    }


    public Test() {

        try {
            this.activeUserId = conn.getActiveUser();
            this.activeUserFullname = conn.getUserFullname(this.activeUserId);
            this.activeUserRole = conn.getUserRole(this.activeUserId);
        }
        catch(ClassNotFoundException e) {}
        catch (SQLException sqle){}

        setTitle("Тестирование");

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

        goToNextButton.setVisible(false);
        goToNextButton.setEnabled(false);

        try {
            int isRandom = Integer.parseInt(conn.getSettingByKey("random_test"));
            if (isRandom == 0) {
                ResultSet courses = conn.getCourses();
                while (courses.next())
                    boxCourses.addItem(new ComboItem(courses.getInt(1), courses.getString(2)));
                currentCourse = conn.getFirstCourse();
                countQuestion = conn.getCountQuestionsByCourse(currentCourse);
                nameRandomCourse.setVisible(false);
                chooseCourses.setVisible(true);
                boxCourses.setVisible(true);
                startTestButton.setVisible(true);
            } else {
                ResultSet randomCourse = conn.getRandomCourse();
                currentCourse = randomCourse.getInt(1);
                nameRandomCourse.setText("Тест: \"" + randomCourse.getString(2) + "\"");
                chooseCourses.setVisible(false);
                boxCourses.setVisible(false);
                startTestButton.setVisible(false);
                nameRandomCourse.setVisible(true);
                startTest();
            }
        }
        catch(ClassNotFoundException e) {}
        catch (SQLException sqle){}

        GridBagConstraints c = setDefaultC();

        boxCourses.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object item = boxCourses.getSelectedItem();
                currentCourse = ((ComboItem)item).getKey();
            }
        });

        startTestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startTest();
            }
        });

        goToNextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isCorrect = false;
                try {
                    isCorrect = conn.getAnswerResult(userAnswer);
                }
                catch(ClassNotFoundException cnfe) {}
                catch (SQLException sqle){}

                if (isCorrect == true)
                    rightAnswers++;
                progressTest++;

                testData += "{\"q\":" + currentQuestion + ",\"a\":" + userAnswer + "},";

                if (countQuestion == progressTest) {
                    resumeTest = false;
                    double percent = (100 / countQuestion) * rightAnswers;
                    testData = testData.substring(0, testData.length() - 1) + "]}";
                    String resultTest = "";
                    try {
                        conn.endTest(idTest, percent, testData);
                        resultTest = conn.getMark(percent);
                        if (resultTest.equals(""))
                            resultTest = "Неудовлетворительно";
                    }
                    catch(ClassNotFoundException cnfe) {}
                    catch (SQLException sqle){}
                    app.clearPanel(answersPane, answersBox);
                    String finalText = "Тест завершен. "
                            + "Результат прохождения теста: \"" + resultTest + "\"";
                    labelQuestion.setText(finalText);
                    answersBox.revalidate();
                    answersBox.repaint();
                } else {
                    try {
                        ResultSet[] questionData = conn.getTestQuestion(answeredQuestions, currentCourse);
                        displayQuestion(questionData, c);
                    }
                    catch(ClassNotFoundException cnfe) {}
                    catch (SQLException sqle){}
                }
                goToNextButton.setVisible(resumeTest);
            }
        });

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
