package network;

import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * The Client class.
 *
 * @author Aahash Kevin Sundararuban (@sundararuban)
 * Emre Tunçel (@tunel)
 * Mohammad Taha (@taham)
 * Nora Schwaabe (@schwaabe)
 * Robert Stefan Scholz (@scholzr)
 */
public class Client {
    /**
     * Username Attribute of the client.
     */
    private String username;
    /**
     * network.Client socket.
     */
    private Socket s;
    /**
     * Buffered-reader for receiving messages from the network.ClientHandler.
     */
    private BufferedReader buffReader;
    /**
     * Buffered-writer for sending messages to the network.ClientHandler.
     */
    private BufferedWriter buffWriter;

    /**
     * Instantiates a new client object.
     *
     * @param username Username of the client
     * @param socket   Socket of the client
     */
    public Client(String username, Socket socket) {
        try {
            this.username = username;
            this.s = socket;
            this.buffReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.buffWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        } catch (IOException e) {
            programmSchliessen(buffReader, buffWriter, s);
        }
    }

    /**
     * The message sending method:
     * Sends messages to the network.ClientHandler via the buffered-reader.
     */
    public void sendMessage() {
        try {
            buffWriter.write(username);
            buffWriter.newLine();
            buffWriter.flush();
            while (true) {
                Scanner scanner = new Scanner(System.in);
                String message = scanner.nextLine().trim();
                if (message.trim().equals("bye")) {
                    programmSchliessen(buffReader, buffWriter, s);
                }
                if (message.trim().length() > 0) {
                    buffWriter.write(message);
                    buffWriter.newLine();
                    buffWriter.flush();
                }
            }
        } catch (IOException e) {
            programmSchliessen(buffReader, buffWriter, s);
        }
    }

    /**
     * The message receiving method:
     * Receives messages from the network.ClientHandler via the buffered-reader.
     */
    public void listenToMessage() {
        new Thread(() -> {
            String message;
            while (true) {
                try {
                    message = buffReader.readLine();
                    System.out.println(message);
                } catch (IOException e) {
                    programmSchliessen(buffReader, buffWriter, s);
                }
            }
        }).start();
    }

    /**
     * Clients main method:
     * Prints the user greeting and accepts the username.
     *
     * @param args arguments for the main method
     */
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Please enter a new name: ");
        String username = scanner.nextLine();
        Socket s = new Socket("localhost", 3000);
        Client client = new Client(username, s);
        while (true) {
            String response;
            client.listenToMessage();
            client.sendMessage();
            response = client.buffReader.readLine();
            if (response.equals("Welcome!")) {
                client.setUsername(username);
                break;
            }
            username = scanner.nextLine();
            client.setUsername(username);
        }
    }

    /**
     * Helper method to terminate the Program
     *
     * @param buffReader clients buffered-reader object
     * @param buffWriter clients buffered-writer object
     * @param s          clients socket object
     */
    public void programmSchliessen(BufferedReader buffReader, BufferedWriter buffWriter, Socket s) {
        System.out.println("Program closes!");
        System.exit(0);
        try {
            if (buffReader != null) {
                buffReader.close();
            }
            if (buffWriter != null) {
                buffWriter.close();
            }
            if (s != null) {
                s.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Setter for the username.
     */
    public void setUsername(String username) {
        this.username = username;
    }
}
