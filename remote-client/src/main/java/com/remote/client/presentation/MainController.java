package com.remote.client.presentation;

import com.remote.client.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class MainController {

    private static final String EMAIL_REGEX = "^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$";

    @FXML
    private AnchorPane login_form;

    @FXML
    private BorderPane message_form;

    @FXML
    private AnchorPane remote_form;

    @FXML
    private AnchorPane signup_form;

    @FXML
    private Label toLogin;

    @FXML
    private ImageView toMessage;

    @FXML
    private ImageView toRemote;

    @FXML
    private Label toSignup;

    @FXML
    private ImageView toUser;

    @FXML
    private TextField messageInput;

    @FXML
    private VBox messageContainer;

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
    private Label voiceAlert;

    // điều hướng
    @FXML
    public void switchForm(MouseEvent e){
        if(e.getSource() == toRemote){
            remote_form.setVisible(true);
            message_form.setVisible(false);
            login_form.setVisible(false);
            signup_form.setVisible(false);
        } else if (e.getSource() == toSignup) {
            remote_form.setVisible(false);
            message_form.setVisible(false);
            login_form.setVisible(false);
            signup_form.setVisible(true);
        } else if (e.getSource() == toUser) {
            remote_form.setVisible(false);
            message_form.setVisible(false);
            login_form.setVisible(true);
            signup_form.setVisible(false);
        } else if (e.getSource() == toLogin) {
            remote_form.setVisible(false);
            message_form.setVisible(false);
            login_form.setVisible(true);
            signup_form.setVisible(false);
        } else if (e.getSource() == toMessage) {
            remote_form.setVisible(false);
            message_form.setVisible(true);
            login_form.setVisible(false);
            signup_form.setVisible(false);
        }
    }
    // xử lý gửi tin nhắn
    @FXML
    private void handleSendMessage() {
        // Lấy nội dung tin nhắn từ TextField
        String messageText = messageInput.getText();
        if (!messageText.isEmpty()) {
            // Thêm tin nhắn của người dùng (outgoing message)
            addOutgoingMessage(messageText);

            // Mô phỏng tin nhắn phản hồi (incoming message)
            addIncomingMessage("This is an auto-response.");

            // Xóa TextField sau khi gửi
            messageInput.clear();
        }
    }
    @FXML
    private void handleSendfile() throws FileNotFoundException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn file");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"),
                new FileChooser.ExtensionFilter("All Files", "*.docx*", "*.xlsx*")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            System.out.println("File đã chọn: " + selectedFile.getAbsolutePath());

            if (selectedFile.getName().endsWith(".png") || selectedFile.getName().endsWith(".jpg") || selectedFile.getName().endsWith(".jpeg")) {
                HBox messageBox = new HBox();
                messageBox.setAlignment(Pos.CENTER_RIGHT);
                messageBox.setStyle("-fx-padding: 10;");

                // Tạo ImageView để hiển thị ảnh
                Image image = new Image(new FileInputStream(selectedFile));
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(200);
                imageView.setFitHeight(150);
                imageView.setPreserveRatio(true);
                TextFlow textFlow = new TextFlow(imageView);
                textFlow.setStyle("-fx-background-color: #6699FF; -fx-padding: 10; -fx-background-radius: 10;");

                messageBox.getChildren().add(textFlow);  // Thêm ImageView vào HBox
                messageContainer.getChildren().add(messageBox);  // Thêm HBox vào container chứa tin nhắn
            } else if(selectedFile.getName().endsWith(".docx")){
                HBox fileBox = new HBox();
                fileBox.setAlignment(Pos.CENTER_RIGHT);
                fileBox.setStyle("-fx-padding: 10;");
                fileBox.setSpacing(10);

                Label fileNameLabel = new Label(selectedFile.getName());
                fileNameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

                long fileSizeInKB = selectedFile.length() / 1024;
                Label fileSizeLabel = new Label(fileSizeInKB + " KB");
                fileSizeLabel.setStyle("-fx-text-fill: white;");

                Button downloadButton = new Button("Tải về");
                downloadButton.setStyle("-fx-background-color: white; -fx-text-fill: purple; -fx-background-radius: 10;");
                TextFlow textFlow = new TextFlow(fileNameLabel,fileSizeLabel, downloadButton);
                textFlow.setStyle("-fx-background-color: #6699FF; -fx-padding: 10; -fx-background-radius: 10;");


                fileBox.getChildren().addAll( textFlow);

                messageContainer.getChildren().add(fileBox);
            }

        }
    }

    //Xử lý sự kiện khi chuột được nhấn giữ (voice btn)
    @FXML
    private void OnMousePressed() {
        voiceAlert.setVisible(true);
    }
    //Xử lý sự kiện khi chuột được thả (voice btn)
    @FXML
    private void OnMouseReleased() {
        voiceAlert.setVisible(false);

        HBox voiceBox = new HBox();
        voiceBox.setAlignment(Pos.CENTER_RIGHT);
        voiceBox.setStyle("-fx-padding: 10;");

        ImageView play = new ImageView(HelloApplication.class.getResource("image/play.png").toExternalForm());
        play.setFitHeight(16);
        play.setFitWidth(16);
        play.setCursor(Cursor.HAND);
        play.setPreserveRatio(true);

        ImageView pause = new ImageView(HelloApplication.class.getResource("image/pause.png").toExternalForm());
        pause.setFitHeight(16);
        pause.setFitWidth(16);
        pause.setCursor(Cursor.HAND);
        pause.setPreserveRatio(true);

        Slider slider = new Slider();

        Label duration = new Label("1:12");

        TextFlow textFlow = new TextFlow(play,slider, duration);
       textFlow.setStyle("-fx-background-color: #6699FF; -fx-padding: 10; -fx-background-radius: 10;");

        voiceBox.getChildren().addAll( textFlow);

       messageContainer.getChildren().add(voiceBox);

        play.setOnMouseClicked(event -> textFlow.getChildren().set(0, pause));

        pause.setOnMouseClicked(event -> textFlow.getChildren().set(0, play));
    }

    // hiện tin nhắn của bản thân
    @FXML
    private void addOutgoingMessage(String message) {
        HBox messageBox = new HBox();
        messageBox.setAlignment(Pos.CENTER_RIGHT);
        messageBox.setStyle("-fx-padding: 10;");

        Text text = new Text(message);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: #6699FF; -fx-padding: 5; -fx-background-radius: 10;");

        messageBox.getChildren().add(textFlow);
        messageContainer.getChildren().add(messageBox);
    }
    // hiện tin nhắn của đối phương
    @FXML
    private void addIncomingMessage(String message) {
        HBox messageBox = new HBox();
        messageBox.setAlignment(Pos.CENTER_LEFT);
        messageBox.setStyle("-fx-padding: 10;");

        Text text = new Text(message);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: #FFFFFF; -fx-padding: 5;-fx-background-radius: 10;");

        messageBox.getChildren().add(textFlow);
        messageContainer.getChildren().add(messageBox);
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


