import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;


public class AdminTickets extends JFrame {
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
    private JPanel rootPanel;
    private JComboBox boxCourses;
    private JPanel questionsBox;
    private JPanel answersBox;
    private JButton newQuestion;
    private JButton newAnswer;
    private JMenuItem menuItemSettings;

    private int currentCourse;
    private int currentQuestion;
    private int countQuestions = 0;
    private JPanel[] questionsPane = new JPanel[countQuestions];
    private int countAnswers = 0;
    private JPanel[] answersPane = new JPanel[countAnswers];

    private Conn conn = new Conn();
    private App app = new App();

    private int activeUserId = 0;
    private String activeUserFullname = "";
    private int activeUserRole = 0;


    private void addAnswers(String textAnswer, int dataId, int id, boolean isCorrect) {
        JTextField txt_answer   = new JTextField(textAnswer);
        txt_answer.setPreferredSize(new Dimension(200, 24));
        RowButton saveButton    = new RowButton("Обновить", dataId, id);
        RowButton deleteButton  = new RowButton("Удалить", dataId, id);
        RowCheckBox corrected   = new RowCheckBox("Правильный", isCorrect, dataId, id);
        GridBagConstraints c    = new GridBagConstraints();
        c.fill      = GridBagConstraints.HORIZONTAL;
        c.weightx   = 0.0;
        c.gridx     = 0;
        c.gridy     = id;
        c.insets    = new Insets(10,0,0,0);
        answersPane[id] = new JPanel();
        answersPane[id].setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        answersPane[id].setBackground(new Color(219, 244, 249));
        answersPane[id].add(txt_answer);
        answersPane[id].add(Box.createHorizontalGlue());
        answersPane[id].add(corrected);
        answersPane[id].add(Box.createRigidArea(new Dimension(10, 0)));
        answersPane[id].add(saveButton);
        answersPane[id].add(Box.createRigidArea(new Dimension(10, 0)));
        answersPane[id].add(deleteButton);

        corrected.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    conn.SetCorrectAnswer(corrected.getDataId(), corrected.isSelected());
                }
                catch(ClassNotFoundException cnfe) {}
                catch (SQLException sqle){}
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    conn.updateAnswer(saveButton.getDataId(), txt_answer.getText());
                }
                catch(ClassNotFoundException cnfe) {}
                catch (SQLException sqle){}
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    conn.deleteAnswer(deleteButton.getDataId());
                    answersBox.remove(answersPane[deleteButton.getIndex()]);
                    answersBox.revalidate();
                    answersBox.repaint();
                }
                catch(ClassNotFoundException cnfe) {}
                catch (SQLException sqle){}
            }
        });

        answersBox.add(answersPane[id], c);
    }

    private void addQuestions(String textQuestion, int dataId, int id) {
        JTextField txt_question = new JTextField(textQuestion);
        txt_question.setPreferredSize(new Dimension(200, 24));
        RowButton answersButton = new RowButton("Ответы", dataId, id);
        RowButton saveButton    = new RowButton("Обновить", dataId, id);
        RowButton deleteButton  = new RowButton("Удалить", dataId, id);
        GridBagConstraints c    = new GridBagConstraints();
        c.fill      = GridBagConstraints.HORIZONTAL;
        c.weightx   = 0.0;
        c.gridx     = 0;
        c.gridy     = id;
        c.insets    = new Insets(10,0,0,0);
        questionsPane[id] = new JPanel();
        questionsPane[id].setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        questionsPane[id].setBackground(new Color(219, 244, 249));
        questionsPane[id].add(txt_question);
        questionsPane[id].add(Box.createHorizontalGlue());
        questionsPane[id].add(answersButton);
        questionsPane[id].add(Box.createRigidArea(new Dimension(10, 0)));
        questionsPane[id].add(saveButton);
        questionsPane[id].add(Box.createRigidArea(new Dimension(10, 0)));
        questionsPane[id].add(deleteButton);

        answersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    app.clearPanel(answersPane, answersBox);

                    currentQuestion   = answersButton.getDataId();
                    ResultSet answers = conn.getQuestionAnswers(currentQuestion);
                    countAnswers      = conn.getCountAnswersByQuestion(currentQuestion);

                    answersPane       = app.resizePane(answersPane, countAnswers, false);
                    int id = 0;
                    while (answers.next()) {
                        int dataId = answers.getInt(1);
                        String textTicket = answers.getString(3);
                        boolean isCorrect = answers.getBoolean(4);
                        addAnswers(textTicket, dataId, id, isCorrect);
                        id++;
                    }

                    answersBox.revalidate();
                    answersBox.repaint();
                }
                catch(ClassNotFoundException cnfe) {}
                catch (SQLException sqle){}
            }
        });
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    conn.updateQuestion(saveButton.getDataId(), txt_question.getText());
                }
                catch(ClassNotFoundException cnfe) {}
                catch (SQLException sqle){}
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    conn.deleteQuestion(deleteButton.getDataId());
                    questionsBox.remove(questionsPane[deleteButton.getIndex()]);
                    questionsBox.revalidate();
                    questionsBox.repaint();
                }
                catch(ClassNotFoundException cnfe) {}
                catch (SQLException sqle){}
            }
        });

        questionsBox.add(questionsPane[id], c);
    }

    private void getQuestionByCourse() {
        try {
            app.clearPanel(questionsPane, questionsBox);
            app.clearPanel(answersPane, answersBox);

            ResultSet tickets = conn.getTicketsByCourseId(currentCourse);
            countQuestions = conn.getCountQuestionsByCourse(currentCourse);
            questionsPane = app.resizePane(questionsPane, countQuestions, false);

            int id = 0;
            while (tickets.next()) {
                int dataId = tickets.getInt(1);
                String textTicket = tickets.getString(3);
                addQuestions(textTicket, dataId, id);
                id++;
            }
            questionsBox.revalidate();
            questionsBox.repaint();
        }
        catch(ClassNotFoundException cnfe) {}
        catch (SQLException sqle){}
    }

    public AdminTickets(int idCourse) {

        if (idCourse > 0) {
            currentCourse = idCourse;
        } else {
            try {
                currentCourse = conn.getFirstCourse();
            }
            catch(ClassNotFoundException e) {}
            catch (SQLException sqle){}
        }
        getQuestionByCourse();


        try {
            this.activeUserId = conn.getActiveUser();
            this.activeUserFullname = conn.getUserFullname(this.activeUserId);
            this.activeUserRole = conn.getUserRole(this.activeUserId);
        }
        catch(ClassNotFoundException e) {}
        catch (SQLException sqle){}

        setTitle("Администрирование экзаменационных билетов");

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
            while (courses.next())
                boxCourses.addItem(new ComboItem(courses.getInt(1), courses.getString(2)));
        }
        catch(ClassNotFoundException e) {}
        catch (SQLException sqle){}

        boxCourses.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object item = boxCourses.getSelectedItem();
                currentCourse = ((ComboItem)item).getKey();
                getQuestionByCourse();
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
        menuItemCourses.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AdminCourses admin_courses = new AdminCourses();
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
        newQuestion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newQuestionText = (String)JOptionPane.showInputDialog(
                        AdminTickets.this,
                        "Новый вопрос:",
                        "Добавление нового вопроса",
                        JOptionPane.PLAIN_MESSAGE);
                if (!newQuestionText.equals("")) {
                    try {
                        currentQuestion = conn.addQuestion(newQuestionText, currentCourse);
                        countQuestions++;
                        questionsPane = app.resizePane(questionsPane, countQuestions, true);
                        addQuestions(newQuestionText, currentQuestion, countQuestions - 1);
                        questionsBox.revalidate();
                        questionsBox.repaint();
                    }
                    catch(ClassNotFoundException cnfe) {}
                    catch (SQLException sqle){}
                }
            }
        });

        newAnswer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String newAnswerText = (String)JOptionPane.showInputDialog(
                        AdminTickets.this,
                        "Новый ответ:",
                        "Добавление нового ответа",
                        JOptionPane.PLAIN_MESSAGE);
                if (!newAnswerText.equals("")) {
                    try {
                        int dataId = conn.AddAnswer(newAnswerText, currentQuestion);
                        countAnswers++;
                        answersPane = app.resizePane(answersPane, countAnswers, true);
                        addAnswers(newAnswerText, dataId, countAnswers - 1, false);
                        answersBox.revalidate();
                        answersBox.repaint();
                    }
                    catch(ClassNotFoundException cnfe) {}
                    catch (SQLException sqle){}
                }
            }
        });

    }

}
