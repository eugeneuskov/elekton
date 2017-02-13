import javafx.scene.effect.SepiaTone;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;


public class Conn {

    public static Connection conn;
    public static Statement statmt;
    public static PreparedStatement pstmt;
    public static ResultSet resSet;

    // --------ПОДКЛЮЧЕНИЕ К БАЗЕ ДАННЫХ--------
    public static void Conn() throws ClassNotFoundException, SQLException {
        conn = null;
        Class.forName("org.sqlite.JDBC");
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:" + System.getProperty("user.dir") + "\\db\\exam.s3db");
        }
        catch (SQLException e) {}
    }


    /* регистрация и авторизация */
    public void clearActiveUsers() throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();
        statmt = conn.createStatement();
        statmt.executeUpdate("UPDATE `users` SET `is_active` = 0");
    }

    public Boolean checkLogin(String login) throws ClassNotFoundException, SQLException {
        int existId = 0;
        String sql = "SELECT * FROM `users` WHERE `login` LIKE ?";
        if (conn == null)
            Conn();

        pstmt = conn.prepareStatement(sql);

        pstmt.setString(1, login);
        resSet = pstmt.executeQuery();
        while (resSet.next())
            existId = resSet.getInt("id");

        if (existId == 0)
            return true;
        else
            return false;
    }


    public Boolean registration(String[] userData) throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();
        String sql = "INSERT INTO `users` "
                + "(`lastname`, `firstname`,`middlename`,`company`,`section`,`position`,`login`,`password`) "
                + "VALUES (?,?,?,?,?,?,?,?)";

        pstmt = conn.prepareStatement(sql);

        int indexParam;
        for (int i = 0; i < userData.length; i++) {
            indexParam = i + 1;
            String value = String.valueOf(userData[i]);
            pstmt.setString(indexParam, value);
        }

        pstmt.executeUpdate();

        return true;
    }


    public String getPasswordByLogin(String login) throws ClassNotFoundException, SQLException {
        String password = "";
        String sql = "SELECT `password` FROM `users` WHERE `login` LIKE ?";
        if (conn == null)
            Conn();

        pstmt = conn.prepareStatement(sql);

        pstmt.setString(1, login);
        resSet = pstmt.executeQuery();
        while (resSet.next())
            password = resSet.getString("password");

        return password;
    }

    public void setActiveByLogin(String login) throws ClassNotFoundException, SQLException {
        String sql = "UPDATE `users` SET `is_active` = 1 WHERE `login` LIKE ?";
        if (conn == null)
            Conn();

        pstmt = conn.prepareStatement(sql);

        pstmt.setString(1, login);
        pstmt.executeUpdate();
    }

    public int getActiveUser() throws ClassNotFoundException, SQLException {
        int id = 0;
        if (conn == null)
            Conn();

        statmt = conn.createStatement();
        resSet = statmt.executeQuery("SELECT `id` FROM `users` WHERE `is_active` = 1");

        while (resSet.next())
            id = resSet.getInt("id");

        return id;
    }

    public int getUserRole(int id) throws ClassNotFoundException, SQLException {
        int role = 0;
        if (conn == null)
            Conn();

        String sql = "SELECT `role` FROM `users` WHERE `id` = ?";
        pstmt = conn.prepareStatement(sql);

        pstmt.setInt(1, id);
        resSet = pstmt.executeQuery();

        while (resSet.next())
            role = resSet.getInt("role");

        return role;
    }

    public String getUserFullname(int id) throws ClassNotFoundException, SQLException {
        String userFullname = "";
        String sql = "SELECT `firstname`, `middlename`, `lastname` FROM `users` WHERE `id` = ?";
        if (conn == null)
            Conn();

        pstmt = conn.prepareStatement(sql);

        pstmt.setInt(1, id);
        resSet = pstmt.executeQuery();
        while (resSet.next()) {
            String firstname = resSet.getString("firstname");
            String middlename = resSet.getString("middlename");
            String lastname = resSet.getString("lastname");

            userFullname = firstname + " " + middlename + " " + lastname;
        }

        return userFullname;
    }

    // администрирование курсов
    public ResultSet getCourses() throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();

        statmt = conn.createStatement();
        return statmt.executeQuery("SELECT * FROM `courses`");
    }

    public int getFirstCourse() throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();
        statmt = conn.createStatement();
        resSet = statmt.executeQuery("SELECT id FROM `courses` LIMIT 1");

        return resSet.getInt(1);
    }

    public ResultSet getRandomCourse() throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();
        statmt = conn.createStatement();
        resSet = statmt.executeQuery("SELECT `id`, `course_name` FROM `courses` ORDER BY RANDOM() LIMIT 1");
        return resSet;
    }

    /*public ResultSet getCourseByName(String findName) throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();
        String sql = "SELECT * FROM `courses` "
                + "WHERE `course_name` LIKE '%" + findName + "%'";

        statmt = conn.createStatement();
        return statmt.executeQuery(sql);
    }*/

    public int getCountCourses() throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();

        statmt = conn.createStatement();
        resSet = statmt.executeQuery("SELECT COUNT(*) FROM `courses`");

        return resSet.getInt(1);
    }

    public int addCourse(String сourseName) throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();
        String sql = "INSERT INTO `courses` "
                + "(`course_name`) "
                + "VALUES (?)";

        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, сourseName);
        pstmt.executeUpdate();

        statmt = conn.createStatement();
        resSet = statmt.executeQuery("SELECT last_insert_rowid()");

        return resSet.getInt(1);
    }

    public void updateCourse(int id, String newName) throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();
        String sql = "UPDATE `courses` "
                + "SET `course_name` = ? "
                + "WHERE `id` = ?";

        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, newName);
        pstmt.setInt(2, id);
        pstmt.executeUpdate();
    }

    public void deleteCourse(int id) throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();
        String sql = "DELETE FROM `courses` "
                + "WHERE `id` = ?";

        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        pstmt.executeUpdate();
    }

    // администрирование билетов
    public ResultSet getTicketsByCourseId(int idCourse) throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();
        String sql = "SELECT q.* FROM `questions` q "
                + "LEFT JOIN `courses` c ON c.id = q.id_course "
                + "WHERE c.id = " + idCourse;

        statmt = conn.createStatement();
        resSet = statmt.executeQuery(sql);
        return resSet;
    }

    public int getCountQuestionsByCourse(int idCourse) throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();
        String sql = "SELECT COUNT(*) FROM `questions` "
                + "WHERE id_course = " + idCourse;

        statmt = conn.createStatement();
        resSet = statmt.executeQuery(sql);

        return resSet.getInt(1);
    }

    public int addQuestion(String questionText, int idCourse) throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();
        String sql = "INSERT INTO `questions` "
                + "(`id_course`, `text_question`) "
                + "VALUES (?,?)";

        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, idCourse);
        pstmt.setString(2, questionText);
        pstmt.executeUpdate();

        statmt = conn.createStatement();
        resSet = statmt.executeQuery("SELECT last_insert_rowid()");

        return resSet.getInt(1);
    }

    public void updateQuestion(int id, String textQuestion) throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();
        String sql = "UPDATE `questions` "
                + "SET `text_question` = ? "
                + "WHERE `id` = ?";

        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, textQuestion);
        pstmt.setInt(2, id);
        pstmt.executeUpdate();
    }

    public void deleteQuestion(int id) throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();
        String sql = "DELETE FROM `answers` "
                + "WHERE `id_question` = ?";
        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        pstmt.executeUpdate();

        sql = "DELETE FROM `questions` "
                + "WHERE `id` = ?";
        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        pstmt.executeUpdate();
    }

    public ResultSet getQuestionAnswers(int idQuestion) throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();
        String sql = "SELECT * FROM `answers` "
                + "WHERE `id_question` = " + idQuestion;

        statmt = conn.createStatement();
        resSet = statmt.executeQuery(sql);

        return resSet;
    }

    public int getCountAnswersByQuestion(int idQuestion) throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();
        String sql = "SELECT COUNT(*) FROM `answers` "
                + "WHERE id_question = " + idQuestion;

        statmt = conn.createStatement();
        resSet = statmt.executeQuery(sql);

        return resSet.getInt(1);
    }

    public int AddAnswer(String answerText, int idQuestion) throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();
        String sql = "INSERT INTO `answers` "
                + "(`id_question`, `text_answer`) "
                + "VALUES (?,?)";

        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, idQuestion);
        pstmt.setString(2, answerText);
        pstmt.executeUpdate();

        statmt = conn.createStatement();
        resSet = statmt.executeQuery("SELECT last_insert_rowid()");

        return resSet.getInt(1);
    }

    public void SetCorrectAnswer(int idAnswer, boolean isCorrect) throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();
        String sql = "UPDATE `answers` "
                + "SET `is_correct` = ? "
                + "WHERE `id` = ?";

        pstmt = conn.prepareStatement(sql);
        pstmt.setBoolean(1, isCorrect);
        pstmt.setInt(2, idAnswer);
        pstmt.executeUpdate();
    }

    public void updateAnswer(int idAnswer, String textAnswer) throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();
        String sql = "UPDATE `answers` "
                + "SET `text_answer` = ? "
                + "WHERE `id` = ?";

        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, textAnswer);
        pstmt.setInt(2, idAnswer);
        pstmt.executeUpdate();
    }

    public void deleteAnswer(int idAnswer) throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();

        String sql = "DELETE FROM `answers` "
                + "WHERE `id` = ?";
        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, idAnswer);
        pstmt.executeUpdate();
    }



    // обучение
    public ResultSet getCourseLesson(int idCourse, int row) throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();

        String sql = "SELECT q.text_question, a.text_answer FROM `questions` q "
                + "LEFT JOIN `answers` a ON a.id_question = q.id "
                + "WHERE q.id_course = ? AND a.is_correct = 1 "
                + "ORDER BY q.id LIMIT ? ,?";
        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, idCourse);
        pstmt.setInt(2, row-1);
        pstmt.setInt(3, row);

        return pstmt.executeQuery();
    }


    // тестирование
    public ResultSet[] getTestQuestion(ArrayList<Integer> answeredQuestion, int idCourse) throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();

        ResultSet[] questionData = new ResultSet[2];
        int answeredSize = answeredQuestion.size();
        String answered = "";
        if (answeredSize > 0) {
            for(int i = 0; i < answeredSize; i++) {
                if (i > 0)
                    answered += ",";
                answered += "" + answeredQuestion.get(i);
            }
        }


        String sql = "SELECT `id`, `text_question` FROM `questions` "
                + "WHERE `id_course` = ? AND `id` NOT IN (?) "
                + "ORDER BY RANDOM() LIMIT 1";
        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, idCourse);
        pstmt.setString(2, answered);
        ResultSet question = pstmt.executeQuery();

        questionData[0] = question;

        sql = "SELECT `id`, `text_answer` FROM `answers` "
                + "WHERE `id_question` = ? "
                + "ORDER BY RANDOM()";

        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, question.getInt("id"));
        ResultSet answers = pstmt.executeQuery();

        questionData[1] = answers;


        return questionData;
    }

    public boolean getAnswerResult(int idAnswer) throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();

        String sql = "SELECT `is_correct` FROM `answers` WHERE `id` = ?";
        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, idAnswer);
        ResultSet answer = pstmt.executeQuery();
        return answer.getBoolean(1);
    }

    public int startTest(int user, int course) throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();

        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        String sql = "INSERT INTO `examinations` "
                + "(`date`, `id_user`, `id_course`) "
                + "VALUES (?, ?, ?)";
        pstmt = conn.prepareStatement(sql);
        pstmt.setLong(1, timestamp.getTime());
        pstmt.setInt(2, user);
        pstmt.setInt(3, course);
        pstmt.executeUpdate();

        statmt = conn.createStatement();
        resSet = statmt.executeQuery("SELECT last_insert_rowid()");

        return resSet.getInt(1);
    }

    public void endTest(int id, double result, String testData) throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();

        String sql = "UPDATE `examinations` "
                + "SET `result` = ?, "
                + "`data` = ? "
                + "WHERE `id` = ?";
        pstmt = conn.prepareStatement(sql);
        pstmt.setDouble(1, result);
        pstmt.setString(2, testData);
        pstmt.setInt(3, id);
        pstmt.executeUpdate();
    }

    public String getMark(double percent) throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();

        String sql = "SELECT `value` FROM `marks` "
                + "WHERE `percent` <= " + percent + " "
                + "ORDER BY id ASC LIMIT 1";
        statmt = conn.createStatement();
        resSet = statmt.executeQuery(sql);
        if (resSet.next())
            return resSet.getString(1);
        else
            return "";
    }

    public int getPagesForTestList() throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();
        statmt = conn.createStatement();
        resSet = statmt.executeQuery("SELECT COUNT(*) FROM `examinations`");
        int allTests = resSet.getInt(1);
        int pages = allTests / 15;
        int mod = allTests % 15;
        if (mod > 0)
            pages++;
        return pages;
    }

    public ResultSet getTests(int page) throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();

        String limit = "";
        if (page == 1)
            limit = " LIMIT 15";
        else if (page > 1)
            limit = " LIMIT " + ((page * 15) - 15) + ", 15";

        String sql = "SELECT t.id, t.date, t.result, "
                + "u.lastname, u.firstname, u.middlename, u.company, "
                + "c.course_name "
                + "FROM `examinations` t "
                + "LEFT JOIN `users` u ON u.id = t.id_user "
                + "LEFT JOIN `courses` c ON c.id = t.id_course "
                + "ORDER BY date DESC" + limit;

        statmt = conn.createStatement();
        return statmt.executeQuery(sql);
    }

    public String getTestData(int id) throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();

        String sql = "SELECT `data` FROM `examinations` "
                + "WHERE `id` = ?";
        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        resSet = pstmt.executeQuery();
        if (resSet.next())
            return resSet.getString(1);
        else
            return "";
    }

    public ResultSet getTestAnswered(int qId, int aId) throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();

        String sql = "SELECT q.text_question, a.text_answer, a.is_correct "
                + "FROM questions q, answers a "
                + "WHERE q.id = ? AND a.id = ?";
        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, qId);
        pstmt.setInt(2, aId);
        return pstmt.executeQuery();
    }

    public int getCountTested() throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();
        statmt = conn.createStatement();
        resSet = statmt.executeQuery("SELECT COUNT(*) FROM `examinations`");
        return resSet.getInt(1);
    }


    // настройки
    public ResultSet getSettings() throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();
        statmt = conn.createStatement();
        return statmt.executeQuery("SELECT * FROM `settings` WHERE `id` <> 6");
    }

    public void setSettings(Map<String, String> settingsMap) throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();
        String sql = "";
        String markText = "";
        int markId = 0;
        boolean isMark = false;
        for (Map.Entry<String, String> e : settingsMap.entrySet()) {
            sql = "UPDATE `settings` "
                    + "SET `value` = '" + e.getValue() + "' "
                    + "WHERE `key` LIKE '" + e.getKey() + "'";
            statmt = conn.createStatement();
            statmt.executeUpdate(sql);

            isMark = false;
            if (e.getKey().equals("mark_five")) {
                isMark = true;
                markId = 1;
                markText = "Отлично";
            } else if (e.getKey().equals("mark_four")) {
                isMark = true;
                markId = 2;
                markText = "Хорошо";
            } else if (e.getKey().equals("mark_three")) {
                isMark = true;
                markId = 3;
                markText = "Удовлетворительно";
            }
            if (isMark) {
                sql = "UPDATE `marks` "
                        + "SET `percent` = " + e.getValue() + ", "
                        + "`value` = '" + markText + "' "
                        + "WHERE `id` = " + markId;
                statmt = conn.createStatement();
                statmt.executeUpdate(sql);
            }
        }
    }

    public String getSettingByKey(String settingKey) throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();
        statmt = conn.createStatement();
        resSet = statmt.executeQuery("SELECT `value` FROM `settings` WHERE `key` LIKE '" + settingKey + "'");
        return resSet.getString(1);
    }


    // лицензия
    public ResultSet getLicenseValue() throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();
        statmt = conn.createStatement();
        return statmt.executeQuery("SELECT `value`, `result` FROM `settings` WHERE `key` LIKE 'license'");
    }

    public boolean getActivationResult() throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();
        statmt = conn.createStatement();
        resSet = statmt.executeQuery("SELECT `result` FROM `settings` WHERE `key` LIKE 'license'");
        String result = resSet.getString(1);
        if (result.equals("1"))
            return true;
        else
            return  false;
    }

    public void setActivationProgramm(String licenseKey, String licenseResult) throws ClassNotFoundException, SQLException {
        if (conn == null)
            Conn();

        String numberResult = "";
        if (licenseResult.equals("fail"))
            numberResult = "0";
        else if (licenseResult.equals("success"))
            numberResult = "1";

        String sql = "UPDATE `settings` "
                + "SET `value` = ?, "
                + "`result` = ? "
                + "WHERE `key` LIKE 'license'";
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, licenseKey);
        pstmt.setString(2, numberResult);
        pstmt.executeUpdate();
    }



    // --------Закрытие--------
    public static void CloseDB() throws ClassNotFoundException, SQLException {
        if (conn != null)
            conn.close();
        if (statmt != null)
            statmt.close();
        if (resSet != null)
            resSet.close();

        System.out.println("Соединения закрыты");
    }


}