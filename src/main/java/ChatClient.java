import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;

public class ChatClient extends Application {
    private Socket socket;
    private PrintWriter out;
    private String clientName; // To store client's name

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Chat Room");

        TextArea messageArea = new TextArea();
        messageArea.setEditable(false);
        TextField inputField = new TextField();
        Button sendButton = new Button("Send");

        // Prompt user for their name
        TextInputDialog dialog = new TextInputDialog("User");
        dialog.setTitle("Username");
        dialog.setHeaderText("Enter your username:");
        dialog.setContentText("Username:");
        clientName = dialog.showAndWait().orElse("User");

        // Show sent message in the chat and send it to the server
        sendButton.setOnAction(e -> {
            String message = inputField.getText();
            if (!message.isEmpty()) {
                String fullMessage = clientName + ": " + message; // Include client's name in the message
                messageArea.appendText(fullMessage + "\n"); // Display message
                sendMessage(fullMessage); // Send message to the server
                inputField.clear(); // Clear input field after sending
            }
        });

        VBox vbox = new VBox(messageArea, inputField, sendButton);
        Scene scene = new Scene(vbox, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();

        connectToServer(messageArea);
    }

    // Connect to server and listen for incoming messages
    private void connectToServer(TextArea messageArea) {
        try {
            socket = new Socket("localhost", 12345);
            out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(() -> {
                String message;
                try {
                    while ((message = in.readLine()) != null) {
                        messageArea.appendText(message + "\n"); // Display received messages
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Send the message to the server
    private void sendMessage(String message) {
        if (out != null && !message.trim().isEmpty()) {
            out.println(message); // Send to server
        }
    }
}
