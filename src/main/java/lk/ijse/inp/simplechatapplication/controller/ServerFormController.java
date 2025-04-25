package lk.ijse.inp.simplechatapplication.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

public class ServerFormController implements Initializable {

    @FXML
    private Button btnSend;

    @FXML
    private AnchorPane serverPane;

    @FXML
    private TextArea textAreaServer;

    @FXML
    private TextField txtServer;

    @FXML
    private Button btnFile;

    @FXML
    private ImageView imageView;

    @FXML
    void btnSendOnAction(MouseEvent event) {
        sendServer();
    }

    ServerSocket serverSocket;
    Socket socket;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
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
                textAreaServer.appendText(file.getName() + "\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(4000);
                socket = serverSocket.accept();

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

                    textAreaServer.appendText("Client : " + message + "\n");

                    if (message.equalsIgnoreCase("exit")) {
                        break;
                    }
                }
                socket.close();
                serverSocket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        textAreaServer.setEditable(false);
    }

    private void sendServer() {
        try {
            String serverMessage = txtServer.getText();
            dataOutputStream.writeUTF(serverMessage);
            textAreaServer.appendText("Server : " + serverMessage + "\n");
            txtServer.clear();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
