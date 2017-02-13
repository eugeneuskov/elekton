import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class App {

    private Conn conn = new Conn();
    private final String USER_AGENT = "Mozilla/5.0";

    public JPanel[] resizePane(JPanel[] pane, int size, boolean needCopy) {

        JPanel[] tmp = new JPanel[size];
        if (needCopy == true)
            System.arraycopy(pane, 0, tmp, 0, pane.length);
        return tmp;

    }

    public void clearPanel(JPanel[] pane, JPanel box) {
        int countPanels = pane.length;
        if (countPanels > 0)
            for (int i = 0; i < countPanels; i++)
                box.remove(pane[i]);
    }

    public void resetAnswerTextColor(JPanel[] pane) {
        int countPanels = pane.length;
        if (countPanels > 0)
            for (int i = 0; i < countPanels; i++) {
                Component[] components = pane[i].getComponents();
                int componentsCount = components.length;
                for (int j = 0; j < componentsCount; j++)
                    if (components[j].getClass().getName().equals("RowArea"))
                        components[j].setForeground(new Color(0, 0, 0));
            }

    }

    public ResultSet getLicenseValue() throws ClassNotFoundException, SQLException {
        return conn.getLicenseValue();
    }

    public String checkLicenseKey(String licenseKey) throws Exception {
        String url = "http://elekton.cookwebfor.me/activate_license.php?license=" + licenseKey;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        /*int responseCode = con.getResponseCode();
        System.out.println("Response Code : " + responseCode);*/

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    public boolean CheckActivateProgramm(String licenseKey) throws Exception{
        String url = "http://elekton.cookwebfor.me/check_license.php?check=" + licenseKey;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String responseLicense = response.toString();
        if (responseLicense.equals("success"))
            return true;
        else
            return false;
    }

    public Map<String, String> getTestDetail(int testId) {
        Map<String, String> testMap = new HashMap<String, String>();
        try {
            String s = conn.getTestData(testId);
            if (!s.equals("")) {
                ResultSet singleTestQuestion;
                String singleTestAnswer;
                JSONObject obj = new JSONObject(s);
                JSONArray arr = obj.getJSONArray("testData");
                int arrCount = arr.length();
                for (int i = 0; i < arrCount; i++) {
                    JSONObject testData = arr.getJSONObject(i);
                    singleTestQuestion = conn.getTestAnswered(testData.getInt("q"), testData.getInt("a"));
                    if (singleTestQuestion.next()) {
                        if (singleTestQuestion.getBoolean(3) == true)
                            singleTestAnswer = singleTestQuestion.getString(2) + "__t";
                        else
                            singleTestAnswer = singleTestQuestion.getString(2) + "__f";
                        testMap.put(singleTestQuestion.getString(1), singleTestAnswer);
                    }
                }
            }
        }
        catch (ClassNotFoundException cnfe) {}
        catch (SQLException sqle) {}
        catch (JSONException je) {}
        return testMap;
    }

}
