import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.io.FileOutputStream;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;


public class AdminTestList extends JFrame {
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
    private JPanel listPanel;
    private JMenuItem menuItemSettings;
    private JPanel pagesPanel;
    private JButton printButton;

    private Conn conn = new Conn();
    private App app = new App();

    private int activeUserId = 0;
    private String activeUserFullname = "";
    private int activeUserRole = 0;
    private GridBagLayout gbl = new GridBagLayout();
    private String saveFilePath;
    private Cursor handCursor = new Cursor(Cursor.HAND_CURSOR);
    private int testId;


    private void displayTestList(int page)  {

        Component[] components = listPanel.getComponents();
        int componentsCount = components.length;
        for (int j = 0; j < componentsCount; j++)
            listPanel.remove(components[j]);

        try {
            ResultSet testList = conn.getTests(page);
            int countList      = conn.getCountTested();

            listPanel.setLayout(new GridBagLayout());
            GridBagConstraints c = new GridBagConstraints();
            c.fill               = GridBagConstraints.HORIZONTAL;
            c.anchor             = GridBagConstraints.WEST;
            c.weightx            = 0.5;
            c.weighty            = 0.0;
            c.gridy              = 0;

            c.gridx              = 0;
            JLabel labelNumber   = new JLabel("№");
            gbl.setConstraints(labelNumber, c);
            listPanel.add(labelNumber, c);

            c.gridx              = 1;
            JLabel labelDate     = new JLabel("Дата");
            gbl.setConstraints(labelDate, c);
            listPanel.add(labelDate, c);

            c.gridx              = 2;
            JLabel labelNames    = new JLabel("ФИО");
            gbl.setConstraints(labelNames, c);
            listPanel.add(labelNames, c);

            c.gridx              = 3;
            JLabel labelCompany  = new JLabel("Компания");
            gbl.setConstraints(labelCompany, c);
            listPanel.add(labelCompany, c);

            c.gridx              = 4;
            JLabel labelСourse   = new JLabel("Курс");
            gbl.setConstraints(labelСourse, c);
            listPanel.add(labelСourse, c);

            c.gridx              = 5;
            JLabel labelResult   = new JLabel("Результат");
            gbl.setConstraints(labelResult, c);
            listPanel.add(labelResult, c);

            RowLabel[][] resultsLabels = new RowLabel[countList][6];
            int y = 1;
            int id = 0;
            char fName;
            char mName;
            String lName;
            while (testList.next()) {
                testId = testList.getInt(1);
                c.gridy = y;

                c.gridx = 0;
                resultsLabels[id][0] = new RowLabel("" + testId, testId);
                gbl.setConstraints(resultsLabels[id][0], c);
                listPanel.add(resultsLabels[id][0], c);

                c.gridx = 1;
                SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy");
                String date = formatter.format(testList.getLong(2));
                resultsLabels[id][1] = new RowLabel(date, testId);
                gbl.setConstraints(resultsLabels[id][1], c);
                listPanel.add(resultsLabels[id][1], c);

                c.gridx = 2;
                lName = testList.getString(4);
                fName = testList.getString(5).charAt(0);
                mName = testList.getString(6).charAt(0);
                String FIO = "" + lName + " " + fName + ". " + mName + ".";
                resultsLabels[id][2] = new RowLabel(FIO, testId);
                gbl.setConstraints(resultsLabels[id][2], c);
                listPanel.add(resultsLabels[id][2], c);

                c.gridx = 3;
                resultsLabels[id][3] = new RowLabel(testList.getString(7), testId);
                gbl.setConstraints(resultsLabels[id][3], c);
                listPanel.add(resultsLabels[id][3], c);

                c.gridx = 4;
                resultsLabels[id][4] = new RowLabel(testList.getString(8), testId);
                gbl.setConstraints(resultsLabels[id][4], c);
                listPanel.add(resultsLabels[id][4], c);

                c.gridx = 5;
                resultsLabels[id][5] = new RowLabel("" + testList.getDouble(3), testId);
                resultsLabels[id][5].setCursor(handCursor);
                gbl.setConstraints(resultsLabels[id][5], c);
                listPanel.add(resultsLabels[id][5], c);

                resultsLabels[id][5].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        RowLabel source = (RowLabel) e.getComponent();
                        Map<String, String> testMap = app.getTestDetail(source.getDataId());
                        for (Map.Entry<String, String> me : testMap.entrySet()) {
                            String answer = me.getValue().substring(0, me.getValue().length() - 3);
                            String check = me.getValue().substring(me.getValue().length() - 3);
                            boolean isCorrect = false;
                            if (check.equals("__t"))
                                isCorrect = true;
                            System.out.print(me.getKey() + " :: ");
                            System.out.print(answer + " :: ");
                            System.out.println(isCorrect);
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        RowLabel source = (RowLabel) e.getComponent();
                        source.setForeground(new Color(2, 91, 119));
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        RowLabel source = (RowLabel) e.getComponent();
                        source.setForeground(new Color(0, 0, 0));
                    }
                });

                y++;
                id++;
            }
            listPanel.revalidate();
            listPanel.repaint();
        }
        catch(ClassNotFoundException cnfe) {}
        catch (SQLException sqle) {}
    }

    public AdminTestList() {

        try {
            this.activeUserId       = conn.getActiveUser();
            this.activeUserFullname = conn.getUserFullname(this.activeUserId);
            this.activeUserRole     = conn.getUserRole(this.activeUserId);
            this.saveFilePath       = conn.getSettingByKey("save_file_path");
        }
        catch(ClassNotFoundException e) {}
        catch (SQLException sqle){}

        setTitle("Список пройденных тестов");

        setContentPane(rootPanel);

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
            int countPages = conn.getPagesForTestList();
            if (countPages > 1) {
                for (int i = 0; i < countPages; i++) {
                    int buttonData = i + 1;
                    RowButton page_button = new RowButton("" + buttonData, i, buttonData);
                    pagesPanel.add(page_button);
                    page_button.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            displayTestList(buttonData);
                        }
                    });
                }
            }
        }
        catch (ClassNotFoundException cnfe) {}
        catch (SQLException sqle) {}

        displayTestList(1);



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

        // back to main
        menuItemExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainForm main_form = new MainForm();
                dispose();
            }
        });

        printButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String fileName = (String)JOptionPane.showInputDialog(
                        AdminTestList.this,
                        "Введите имя файла",
                        "Сохранение файла",
                        JOptionPane.PLAIN_MESSAGE
                );
                if (!fileName.equals("")) {
                    try {
                        ResultSet listForFile = conn.getTests(0);

                        Workbook book = new HSSFWorkbook();
                        Sheet sheet = book.createSheet("Тесты");
                        Row row;
                        Cell cell;

                        row = sheet.createRow(0);
                        cell = row.createCell(0);
                        cell.setCellValue("№");
                        cell = row.createCell(1);
                        cell.setCellValue("Дата");
                        cell = row.createCell(2);
                        cell.setCellValue("ФИО");
                        cell = row.createCell(3);
                        cell.setCellValue("Компания");
                        cell = row.createCell(4);
                        cell.setCellValue("Курс");
                        cell = row.createCell(5);
                        cell.setCellValue("Результат");

                        int i = 1;
                        while (listForFile.next()) {
                            i++;
                            row = sheet.createRow(i);

                            cell = row.createCell(0);
                            cell.setCellValue("" + listForFile.getInt(1));

                            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yy");
                            cell = row.createCell(1);
                            cell.setCellValue(formatter.format(listForFile.getLong(2)));

                            cell = row.createCell(2);
                            cell.setCellValue("" + listForFile.getString(4) + " " + listForFile.getString(5).charAt(0) + ". " + listForFile.getString(6).charAt(0) + ".");

                            cell = row.createCell(3);
                            cell.setCellValue(listForFile.getString(7));

                            cell = row.createCell(4);
                            cell.setCellValue(listForFile.getString(8));

                            cell = row.createCell(5);
                            cell.setCellValue(listForFile.getString(3));
                        }

                        sheet.autoSizeColumn(1);
                        FileOutputStream fileStream = new FileOutputStream( saveFilePath + "/" + fileName + ".xls");
                        book.write(fileStream);
                        fileStream.close();
                    } catch (Exception ee) {
                        ee.printStackTrace();
                    }
                }
            }
        });
    }

}
