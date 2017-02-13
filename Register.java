import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.Arrays;


public class Register extends JFrame {
    private JPanel rootPanel;
    private JTextField editFirstname;
    private JTextField editMiddlename;
    private JTextField editLastName;
    private JTextField editCompany;
    private JTextField editSection;
    private JTextField editPosition;
    private JTextField editLogin;
    private JPasswordField editPassword1;
    private JPasswordField editPassword2;
    private JButton buttonRegistration;
    private JButton buttonCancel;
    private JLabel wrongDataLabel;

    private Conn conn = new Conn();


    private String doCheckData() {
        String error = "";
        if (editFirstname.getText() == ""
                || editMiddlename.getText() == ""
                || editLastName.getText() == ""
                || editCompany.getText() == ""
                || editSection.getText() == ""
                || editPosition.getText() == ""
                || editLogin.getText() == ""
                || editPassword1.getPassword().length == 0
                || editPassword2.getPassword().length == 0) {
            error = "Заполните все поля";
        } else {
            char[] password = editPassword1.getPassword();
            char[] confirm = editPassword2.getPassword();
            if (!Arrays.equals(password, confirm))
                error = "Пароль и подтверждение пароля не совпадают";
        }
        return error;
    }

    private Boolean checkLogin() {
        try {
            return conn.checkLogin(editLogin.getText());
        }
        catch(ClassNotFoundException e) { return false; }
        catch (SQLException sqle){ return false; }
    }

    private void doRegister() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String hash = PasswordActions.generatePasswordHash(editPassword1.getPassword());
        String[] newUserData = {
            editFirstname.getText(),
            editMiddlename.getText(),
            editLastName.getText(),
            editCompany.getText(),
            editSection.getText(),
            editPosition.getText(),
            editLogin.getText(),
            hash
        };

        try {
            conn.registration(newUserData);
        }
        catch(ClassNotFoundException e) {}
        catch (SQLException sqle){
            String error = sqle.getMessage();
            System.out.println(error);
        }
    }

    public Register() {
        super("Регистрация в системе");

        setContentPane(rootPanel);

        pack();
        setSize(850, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        buttonRegistration.addActionListener(new ActionListener()  {
            @Override
            public void actionPerformed(ActionEvent e) {
                String isCorrect = doCheckData();
                if (isCorrect != "")
                    wrongDataLabel.setText(isCorrect);
                else
                    if (checkLogin()) {
                        try {
                            doRegister();
                            dispose();
                        }
                        catch(NoSuchAlgorithmException ae) {}
                        catch (InvalidKeySpecException ke){}
                    } else {
                        wrongDataLabel.setText("Пользователь с таким логином уже существует");
                    }
            }
        });

        setVisible(true);
    }
}
