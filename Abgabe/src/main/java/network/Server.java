package network;

import game.Game;

import java.io.*;
import java.net.*;

/**
 * The network.Server class
 *
 * @author Aahash Kevin Sundararuban (@sundararuban)
 * Emre Tunçel (@tunel)
 * Mohammad Taha (@taham)
 * Nora Schwaabe (@schwaabe)
 * Robert Stefan Scholz (@scholzr)
 */
public class Server {
    private final ServerSocket ss;

    public Game game;

    public Server(ServerSocket ss) {
        this.ss = ss;
    }

    /**
     * The connect method for the server:
     * Creates a new socket object and a network.ClientHandler thread for the connection.
     */
    public void connect() {
        try {
            while (!ss.isClosed()) {
                Socket s = ss.accept();
                System.out.println("Successfully connected.");
                ClientHandler clientHandler = new ClientHandler(s, this);
                clientHandler.start();
            }
        } catch (IOException e) {
            disconnect();
        }
    }

    /**
     * The disconnect method for the server:
     * Closes the server socket, buffered-reader and buffered-writer objects.
     */
    public void disconnect() {
        try {
            ss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Servers main method:
     * Creates the server socket and server objects and calls the connect method for connection with the clients.
     *
     * @param args arguments for the main method.
     */
    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(3000);
        Server server = new Server(ss);
        server.connect();
    }

    /**
     * Handles what happens when a player exits the game.
     *
     * @param clientHandler Thread to handle client side actions.
     */

    public void handleClientExit(ClientHandler clientHandler) {
        if (game != null && isPlayerInTheGame(clientHandler)) {
            Game.allPlayers.remove(clientHandler.getPlayerProfile());
            Game.players.remove(clientHandler.getPlayerProfile());
            game.playerLeft(clientHandler.getPlayerProfile());
            System.out.println("The game has " + Game.allPlayers.size() + " player(s).");
        }
    }

    /**
     * Helper method for the %creategame command  Generates error messages
     * for the possible errors.
     *
     * @param clientHandler Thread to handle client side actions.
     * @return True, if a new games has been created, otherwise return false.
     */

    public boolean canCreateGame(ClientHandler clientHandler) {
        if (game == null) {
            this.game = new Game();
            System.out.println("Game created");
            Game.allPlayers.add(clientHandler.getPlayerProfile());
            System.out.println("The game has " + Game.allPlayers.size() + " player(s).");
            return true;
        } else {
            return false;
        }
    }

    /**
     * Helper method for the %joingame command  Generates error messages
     * for the possible errors.
     *
     * @param clientHandler Thread to handle client side actions.
     * @return Receive a message if a player can join a game.
     */

    public String canJoinGame(ClientHandler clientHandler) {
        if (game == null) {
            return "The game has not been created yet. To create a game type %creategame";
        }
        if (isPlayerInTheGame(clientHandler)) {
            return "You are already in the game!";
        }
        if (game.isFull()) {
            return "Game full.";
        }
        if (game.isRunning) {
            return "Game running. You can not join a game that is running.";
        }
        Game.allPlayers.add(clientHandler.getPlayerProfile());
        System.out.println("The game has " + Game.allPlayers.size() + " player(s).");
        return "ok";
    }

    /**
     * Helper method for the %creategame command. Generates error messages
     * for the possible errors.
     *
     * @param clientHandler Thread to handle client side actions.
     * @return Receive a message why a player cannot join a game.
     */

    public String canStartGame(ClientHandler clientHandler) {
        if (game == null) {
            return "You can not start the game because it has not been created yet." +
                    "\nTo create a game type %creategame";
        }
        if (!isPlayerInTheGame(clientHandler)) {
            return "You can not start a game you have not joined." +
                    "\n To join the game type %joingame.";
        }
        if (Game.allPlayers.size() < 2) {
            return "Can not start the game because " +
                    "\nthere are only " + Game.allPlayers.size() + " player(s) in the game";
        }
        if (game.isRunning) {
            return "Game already in progress.";
        }
        return "ok";
    }

    /**
     * Helper method for %leavegame command. Generates error messages
     * for the possible errors.
     *
     * @param clientHandler Thread to handle client side actions.
     * @return Receive a message why a player cannot leave a game.
     */

    public String canLeaveGame(ClientHandler clientHandler) {
        if (game == null) {
            return "You can not leave the game because it has not been created yet." +
                    "\nTo create a game type %creategame";
        }
        if (!isPlayerInTheGame(clientHandler)) {
            return "You can not leave a game you have not joined." +
                    "\n To join the game type %joingame.";
        }
        Game.allPlayers.remove(clientHandler.getPlayerProfile());
        Game.players.remove(clientHandler.getPlayerProfile());
        game.playerLeft(clientHandler.getPlayerProfile());
        System.out.println("The game has " + Game.allPlayers.size() + " player(s).");
        return "ok";
    }

    /**
     * Helper method for %showcards command. Generates error messages
     * for the possible errors.
     *
     * @param clientHandler Thread to handle client side actions.
     * @return Receive a message, if a player can participate.
     */

    public String canShowCards(ClientHandler clientHandler) {
        if (game == null) {
            return "There are no games yet." +
                    "\nTo create a game type %creategame";
        }
        if (!isPlayerInTheGame(clientHandler)) {
            return "You are not in a game.";
        }
        if (clientHandler.getPlayerProfile().getCards().size() == 0) {
            return "You have no cards.";
        }
        return "ok";
    }

    public String canPlayCard(ClientHandler clientHandler){
        if (game == null) {
            return "There are no games yet." +
                    "\nTo create a game type %creategame";
        }
        if(!isPlayerInTheGame(clientHandler)) {
            return "You can not play a card, you are not in a game.";
        }
        if (clientHandler.getPlayerProfile().getCards().size() == 0) {
            return "You can not play a card, you have no cards.";
        }
        if (!game.getPlayerAtTurn().equals(clientHandler.getPlayerProfile())){
            return "It is not your turn. Please wait for your turn to play a card.";
        }
        return "ok";
    }

    public String canGetScores(){
        if (game == null) {
            return "There are no games yet." +
                    "\nTo create a game type %creategame";
        }
        if (!game.isRunning) {
            return "The game has not started yet. There are no scores yet.";
        }
        return "ok";
    }

    public String canGetTurn(){
        if (game == null) {
            return "There are no games yet." +
                    "\nTo create a game type %creategame";
        }
        if (!game.isRunning) {
            return "The game has not started yet. It is not anybody's turn.";
        }
        return "ok";
    }

    /**
     * Checks if the player is in the game.
     *
     * @param clientHandler Thread to handle client side actions.
     * @return True or false whether the player is on the list or not.
     */

    public boolean isPlayerInTheGame(ClientHandler clientHandler) {
        return Game.allPlayers.contains(clientHandler.getPlayerProfile());
    }

    public void setGame(Game game){
        this.game = game;
    }
}
