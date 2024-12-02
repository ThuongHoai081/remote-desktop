package com.remote.client.presentation;

import com.remote.client.HelloApplication;
import com.remote.client.infrastructure.ConnectionInitiatorClient;
import com.remote.client.infrastructure.ConnectionInitiatorServer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.net.InetAddress;

public class MainController {

    private static final String EMAIL_REGEX = "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$";

    @FXML
    private AnchorPane rootPane;

    @FXML
    private AnchorPane login_form;

    @FXML
    private AnchorPane remote_form;

    @FXML
    private AnchorPane signup_form;

    @FXML
    private Label toLogin;

    @FXML
    private ImageView toRemote;

    @FXML
    private Label toSignup;

    @FXML
    private ImageView toUser;

    @FXML
    private TextField showPassword;

    @FXML
    private PasswordField hiddenPassword;

    @FXML
    private CheckBox checkbox;

    @FXML
    private TextField emailLogin;

    @FXML
    private TextField emailSignup;

    @FXML
    private PasswordField passwordLogin;

    @FXML
    private TextField passwordSignup;

    @FXML
    private TextField usernameSignup;

    @FXML
    private TextField yourIP;

    @FXML
    private TextField serverPasswordField;

    @FXML
    private TextField ipAddressTextField;

    @FXML
    private Button connectBtn;

    public void initialize() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = null;
            try {
                networkInterfaces = NetworkInterface.getNetworkInterfaces();
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();

                if (!networkInterface.isUp() || networkInterface.isLoopback()) continue;

                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();

                while (addresses.hasMoreElements()) {
                    InetAddress inetAddress = addresses.nextElement();

                    if (inetAddress.getHostAddress().contains(".") && !inetAddress.isLoopbackAddress()) {
                        String ipv4Address = inetAddress.getHostAddress();
                        yourIP.setText(ipv4Address);
                        System.out.print("IP: " + ipv4Address + "\n");
                        return;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    // điều hướng
    @FXML
    public void switchForm(MouseEvent e){
        if(e.getSource() == toRemote){
            remote_form.setVisible(true);
            login_form.setVisible(false);
            signup_form.setVisible(false);
        } else if (e.getSource() == toSignup) {
            remote_form.setVisible(false);
            login_form.setVisible(false);
            signup_form.setVisible(true);
        } else if (e.getSource() == toUser) {
            remote_form.setVisible(false);
            login_form.setVisible(true);
            signup_form.setVisible(false);
        } else if (e.getSource() == toLogin) {
            remote_form.setVisible(false);
            login_form.setVisible(true);
            signup_form.setVisible(false);
        }
    }
    //show and hidden password
    @FXML
    void changeVisibility(){
        if(checkbox.isSelected()){
            showPassword.setText(hiddenPassword.getText());
            hiddenPassword.setVisible(false);
            showPassword.setVisible(true);
            return;
        }
        hiddenPassword.setText(showPassword.getText());
        hiddenPassword.setVisible(true);
        showPassword.setVisible(false);
    }
    // đi đến màn hình chờ máy khác kết nối
    @FXML
    private void PasswordPressed() throws IOException {
        String password = hiddenPassword.getText();
        System.out.println("Password is " + password);

        ConnectionInitiatorServer connInit = ConnectionInitiatorServer.getInstance(password);

        AnchorPane fxmlLoader =  FXMLLoader.load(HelloApplication.class.getResource("ScreenView.fxml"));
        AnchorPane pane = fxmlLoader;
        rootPane.getChildren().setAll(pane);
    }

    @FXML
    private void submitBtnPressed() throws IOException {

        String serverIp = ipAddressTextField.getText();
        String password = serverPasswordField.getText();

        ConnectionInitiatorClient connInit = ConnectionInitiatorClient.getInstance(serverIp);

        // kiểm tra password
        if(connInit.checkPassword(password)) {

            Parent pane = FXMLLoader.load(HelloApplication.class.getResource("ClientFramesView.fxml"));

            Stage mainStage = (Stage) connectBtn.getScene().getWindow();
            mainStage.close();

            Stage stage = new Stage();
            Scene scene = new Scene(pane);
            stage.setScene(scene);
            stage.setTitle("Connected to " + serverIp);
            stage.show();

           /* Parent messagePane = FXMLLoader.load(HelloApplication.class.getResource("MessageView.fxml"));
            Stage messageStage = new Stage();
            Scene messageScene = new Scene(messagePane);
            messageStage.setScene(messageScene);
            messageStage.setTitle("Messages");
            messageStage.show();*/
        }
    }

    //xử lý đăng nhập
    @FXML
    public void handleLoign(){
        String email = emailLogin.getText();
        String password = passwordLogin.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Thông tin không được để trống");
            return;
        }

        // Giả sử kiểm tra email và mật khẩu từ cơ sở dữ liệu
        boolean isLoginSuccessful = checkLogin(email, password);


        if (isLoginSuccessful) {
            showAlert("Success", "Đăng nhập thành công");
        } else {
            showAlert("Error", "Sai email hoặc mật khẩu");
        }
    }

    private boolean checkLogin(String email, String password) {
        // Kiểm tra email và password có khớp trong DB không
        return "user@example.com".equals(email) && "password123".equals(password);
    }
    //xử lý đăng ký
    @FXML
    public void handleSignup() {
        String email = emailSignup.getText();
        String password = passwordSignup.getText();
        String username = usernameSignup.getText();

        if (email.isEmpty() || password.isEmpty() || username.isEmpty()) {
            showAlert("Error", "Thông tin không được để trống");
            return;
        }

        // Kiểm tra các điều kiện với Regex
        if (!validateEmail(email)) {
            showAlert("Error", "Email không hợp lệ");
            return;
        }

        // Nếu tất cả đều hợp lệ, tiến hành đăng ký
        showAlert("Success", "Đăng ký thành công");
    }

    // Phương thức kiểm tra email
    private boolean validateEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // Phương thức hiển thị thông báo
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


