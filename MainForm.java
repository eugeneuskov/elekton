import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.awt.*;
import java.awt.event.*;


public class MainForm extends JFrame{

    private JPanel rootPanel;
    private JPanel menuPanel;
    private JPanel contentPanel;
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
    private JButton goToLearnButton;
    private JButton goToTestButton;
    private JButton goToReferenceButton;
    private JButton exitButton;
    private JPanel mainPanel;
    private JMenuItem menuItemSettings;
    private JPanel learnPanel;
    private JPanel testPanel;

    private Conn conn = new Conn();
    private App app = new App();

    private int activeUserId = 0;
    private String activeUserFullname = "";
    private int activeUserRole = 0;

    public MainForm() {
        try {
            this.activeUserId = conn.getActiveUser();
            this.activeUserFullname = conn.getUserFullname(this.activeUserId);
            this.activeUserRole = conn.getUserRole(this.activeUserId);
        }
        catch(ClassNotFoundException e) {}
        catch (SQLException sqle){}

        setTitle("Добро пожаловать, " + this.activeUserFullname);

        setContentPane(rootPanel);

        Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);

        menuItemLearn.setCursor(handCursor);
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

        ImageIcon iconLearn = new ImageIcon("assets/learn.jpg");
        ImageIcon iconLearnHover = new ImageIcon("assets/learn_hover.jpg");
        goToLearnButton.setBorderPainted(false);
        goToLearnButton.setFocusPainted(false);
        goToLearnButton.setContentAreaFilled(false);
        goToLearnButton.setCursor(handCursor);
        goToLearnButton.setIcon(iconLearn);
        goToLearnButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                goToLearnButton.setIcon(iconLearnHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                goToLearnButton.setIcon(iconLearn);
            }
        });
        goToLearnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Learn learn = new Learn();
                setVisible(false);
            }
        });


        ImageIcon iconTest = new ImageIcon("assets/test.jpg");
        ImageIcon iconTestHover = new ImageIcon("assets/test_hover.jpg");
        goToTestButton.setBorderPainted(false);
        goToTestButton.setFocusPainted(false);
        goToTestButton.setContentAreaFilled(false);
        goToTestButton.setCursor(handCursor);
        goToTestButton.setIcon(iconTest);
        goToTestButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                goToTestButton.setIcon(iconTestHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                goToTestButton.setIcon(iconTest);
            }
        });
        goToTestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Test test = new Test();
                setVisible(false);
            }
        });

        ImageIcon iconRefernce = new ImageIcon("assets/reference.jpg");
        ImageIcon iconRefernceHover = new ImageIcon("assets/reference_hover.jpg");
        goToReferenceButton.setBorderPainted(false);
        goToReferenceButton.setFocusPainted(false);
        goToReferenceButton.setContentAreaFilled(false);
        goToReferenceButton.setCursor(handCursor);
        goToReferenceButton.setIcon(iconRefernce);
        goToReferenceButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                goToReferenceButton.setIcon(iconRefernceHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                goToReferenceButton.setIcon(iconRefernce);
            }
        });
        goToReferenceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        ImageIcon iconExit = new ImageIcon("assets/exit.jpg");
        ImageIcon iconExitHover = new ImageIcon("assets/exit_hover.jpg");
        exitButton.setBorderPainted(false);
        exitButton.setFocusPainted(false);
        exitButton.setContentAreaFilled(false);
        exitButton.setCursor(handCursor);
        exitButton.setIcon(iconExit);
        exitButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                exitButton.setIcon(iconExitHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                exitButton.setIcon(iconExit);
            }
        });
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
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
                setVisible(false);
            }
        });

        // go to test
        menuItemStartTest.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Test test = new Test();
                setVisible(false);
            }
        });

        // exit
        menuItemExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

    }

}