package lk.ijse.inp.simplechatapplication.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

public class ClientFormController implements Initializable {

    @FXML
    private Button btnSend;

    @FXML
    private AnchorPane clientPane;

    @FXML
    private TextArea textAreaClient;

    @FXML
    private TextField txtClient;

    @FXML
    private Button btnFile;

    @FXML
    private ImageView imageView;

    @FXML
    void btnSendOnAction(MouseEvent event) {
        sendClient();
    }

    Socket socket;
    DataOutputStream dataOutputStream;
    DataInputStream dataInputStream;
    String message;

    @FXML
    void btnFileOnAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(new Stage());
        System.out.println(file.getName());
        if (file != null) {
            try {
                byte[] imageBytes = Files.readAllBytes(file.toPath());
                dataOutputStream.writeUTF("IMAGE");
                dataOutputStream.writeInt(imageBytes.length);
                dataOutputStream.write(imageBytes);
                dataOutputStream.flush();
                textAreaClient.appendText(file.getName() + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new Thread(() -> {
            try {
                socket = new Socket("localhost", 4000);
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                while (true) {
                    message = dataInputStream.readUTF();

                    if (message.equals("IMAGE")) {
                        int length = dataInputStream.readInt();
                        byte[] imageBytes = new byte[length];
                        dataInputStream.readFully(imageBytes);
                        ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
                        Image image = new Image(bais);
                        imageView.setImage(image);
                    }

                    textAreaClient.appendText("Server : " + message + "\n");

                    if (message.equalsIgnoreCase("exit")) {
                        break;
                    }
                }
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        textAreaClient.setEditable(false);
    }

    private void sendClient() {
        try {
            String clientMessage = txtClient.getText();
            dataOutputStream.writeUTF(clientMessage);
            textAreaClient.appendText("Client : " + clientMessage + "\n");
            txtClient.clear();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


