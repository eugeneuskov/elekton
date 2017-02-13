import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Users extends JFrame {

    private JPanel rootPanel;
    private JTextField editLogin;
    private JPasswordField editPassword;
    private JButton buttonOk;
    private JButton buttonRegister;
    private JButton buttonCancel;
    private JLabel errorLoginLabel;

    private Conn conn = new Conn();


    private static boolean validatePassword(String originalPassword, String storedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        String[] parts = storedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);

        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = skf.generateSecret(spec).getEncoded();

        int diff = hash.length ^ testHash.length;
        for(int i = 0; i < hash.length && i < testHash.length; i++)
        {
            diff |= hash[i] ^ testHash[i];
        }
        return diff == 0;
    }

    private static byte[] fromHex(String hex) throws NoSuchAlgorithmException {
        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i<bytes.length ;i++)
        {
            bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    private void doCheckLogin() throws NoSuchAlgorithmException, InvalidKeySpecException, ClassNotFoundException, SQLException {
        String uLogin = editLogin.getText();
        char[] uPassword = editPassword.getPassword();
        if (uLogin == "" || uPassword.length == 0) {
            errorLoginLabel.setText("Заполните все поля");
        } else {
            errorLoginLabel.setText("");
            String hash = conn.getPasswordByLogin(uLogin);
            boolean matched = validatePassword(String.valueOf(uPassword), hash);
            if (matched) {
                conn.setActiveByLogin(uLogin);
                MainForm main_form = new MainForm();
                setVisible(false);
            } else {
                errorLoginLabel.setText("Неверный пароль");
            }
        }
    }

    public Users() {

        super("Добро пожаловать в систему обучения");

        /*boolean needCheckActivation = false;
        String licenseKey = "";
        String licenseResult = "";*/
        try {
            conn.clearActiveUsers();
            /*ResultSet license = conn.getLicenseValue();
            licenseKey = license.getString(1);
            licenseResult = license.getString(2);
            if (licenseResult.equals("1"))
                needCheckActivation = true;*/
        }
        catch(ClassNotFoundException cnfe) {}
        catch (SQLException sqle) {}

        /*if (needCheckActivation == true) {
            try {
                isActive = app.CheckActivateProgramm(licenseKey);
            } catch (Exception ee) {}
            if (isActive == false) {
                try {
                    conn.setActivationProgramm(licenseKey, "0");
                }
                catch(ClassNotFoundException cnfe) {}
                catch (SQLException sqle) {}
            }
        }

        try {
            isActive = conn.getActivationResult();
        }
        catch(ClassNotFoundException cnfe) {}
        catch (SQLException sqle) {}*/

        setContentPane(rootPanel);

        pack();
        setSize(450, 250);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        editPassword.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    doCheckLogin();
                }
                catch(ClassNotFoundException cnfe) {}
                catch (SQLException sqle) {}
                catch(NoSuchAlgorithmException ae) {}
                catch (InvalidKeySpecException ke) {}
            }
        });

        buttonOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    doCheckLogin();
                }
                catch(ClassNotFoundException cnfe) {}
                catch (SQLException sqle) {}
                catch(NoSuchAlgorithmException ae) {}
                catch (InvalidKeySpecException ke) {}
            }
        });

        buttonRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Register register_form = new Register();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        setVisible(true);
    }

}
