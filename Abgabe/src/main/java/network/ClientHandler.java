package network;

import game.Game;
import game.PlayerProfile;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

/**
 * The network.ClientHandler thread
 *
 * @author Aahash Kevin Sundararuban (@sundararuban)
 * Emre Tunçel (@tunel)
 * Mohammad Taha (@taham)
 * Nora Schwaabe (@schwaabe)
 * Robert Stefan Scholz (@scholzr)
 */
public class ClientHandler extends Thread {
    /**
     * Instantiates a new ArrayList containing all the ClientHandlers.
     */
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    /**
     * ClientHandlers socket.
     */
    private Socket s;
    /**
     * Buffered-reader for receiving messages from the client.
     */
    private BufferedReader buffReader;
    /**
     * Buffered-writer for sending messages to the client.
     */
    private BufferedWriter buffWriter;
    /**
     * Nickname Attribute of the network.ClientHandler. Corresponds to the clients username.
     */
    private String nickname;
    private PlayerProfile playerProfile;
    private Server server;

    /**
     * Instantiates a new network.ClientHandler object.
     *
     * @param socket Socket of the network.ClientHandler
     */
    public ClientHandler(Socket socket, Server server) {
        try {
            this.server = server;
            this.s = socket;
            this.buffReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.buffWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.playerProfile = new PlayerProfile(nickname, this);

        } catch (IOException e) {
            programmSchliessen(socket, buffReader, buffWriter);
        }
    }

    /**
     * Checks the ClientHandlers list for a given entry.
     *
     * @param nickname Nickname of the network.ClientHandler.Corresponds to the clients username.
     */
    public boolean nicknameTaken(String nickname) {
        for (ClientHandler temp : clientHandlers) {
            if (temp.nickname.equals(nickname)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Thread to handle client side actions.
     *
     * @param nickname Player entered a name.
     * @return Return the entered name to the ClientHandler.
     */
    public ClientHandler findTargetClient(String nickname) {
        for (ClientHandler temp : clientHandlers) {
            if (temp.nickname.equals(nickname)) {
                return temp;
            }
        }
        return null;
    }

    /**
     * Run method of the network.ClientHandler thread.
     */
    @Override
    public void run() {
        try {
            handshake();
            getMessageFromClient();
        } catch (IOException e) {
            programmSchliessen(s, buffReader, buffWriter);
        }
    }

    /**
     * @param message A message will be sent to the Client.
     */
    public void sendMessageToThisClient(String message) {
        try {
            buffWriter.write(message);
            buffWriter.newLine();
            buffWriter.flush();
        } catch (IOException e) {
            programmSchliessen(s, buffReader, buffWriter);
        }
    }

    /**
     * @throws IOException
     */
    public void handshake() throws IOException {
        while (true) {
            this.nickname = buffReader.readLine();
            if (!nicknameTaken(nickname)) {
                sendMessageToThisClient("Welcome " + nickname + " !");
                sendMessageToThisClient("For help type %help.");
                playerProfile.setNickname(nickname);
                break;
            }
            sendMessageToThisClient("Name is already taken. Please enter a new name: ");
        }
        clientHandlers.add(this);
        broadcastMessage(nickname + " has joined the chat!");
    }

    /**
     * @throws IOException
     */
    public void getMessageFromClient() throws IOException {
        String clientMessage;
        while (s.isConnected()) {
            clientMessage = buffReader.readLine();
            handleMessage(clientMessage);
        }
    }

    /**
     * @author Aahash Kevin Sundararuban (@sundararuban)
     * Emre Tunçel (@tunel)
     * Robert Stefan Scholz (@scholzr)
     *
     * Every message received from the Client is processed. Commands are
     * examined with helper methods in the server and the error messages
     * received from the server are sent to the Client.
     *
     * @param message The message from the Client.
     * @throws IOException
     */
    public void handleMessage(String message) throws IOException {
        switch (message) {
            case "%help":
                sendMessageToThisClient(help);
                break;
            case "%cardhelp":
                sendMessageToThisClient(cardhelp);
                break;
            case "%private":
                sendPrivateTextToUser(chooseUser());
                break;
            case "%creategame":
                if (server.canCreateGame(this)) {
                    sendMessageToThisClient("You have successfully created a game!");
                    broadcastMessage(nickname + " created a game");
                } else {
                    sendMessageToThisClient("You can't create a game because there already exists one.");
                }
                break;
            case "%joingame":
                if (server.canJoinGame(this).equals("ok")) {
                    sendMessageToThisClient("You have joined the game.");
                    broadcastMessage(nickname + " has joined the game.");
                } else {
                    sendMessageToThisClient(server.canJoinGame(this));
                }
                break;
            case "%startgame":
                if (server.canStartGame(this).equals("ok")) {
                    sendMessageToThisClient("You have successfully started the game!");
                    broadcastMessage(nickname + " has started the game.");
                    server.game.run(server);
                } else {
                    sendMessageToThisClient(server.canStartGame(this));
                }
                break;
            case "%leavegame":
                if (server.canLeaveGame(this).equals("ok")) {
                    sendMessageToThisClient("You have left the game.");
                    broadcastMessage(nickname + " left the game.");
                } else {
                    sendMessageToThisClient(server.canLeaveGame(this));
                }
                break;
            case "%showcards":
                if (server.canShowCards(this).equals("ok")) {
                    sendMessageToThisClient(playerProfile.showCards());
                } else {
                    sendMessageToThisClient(server.canShowCards(this));
                }
                break;
            case "%showdiscardedcards":
                if (server.canShowCards(this).equals("ok")) {
                    sendMessageToThisClient(server.game.allDiscardedCards());
                } else {
                    sendMessageToThisClient(server.canShowCards(this));
                }
                break;
            case "%playCard1":
                if (server.canPlayCard(this).equals("ok")) {
                    if (playerProfile.mustPlayCountess) {
                        playCountess();
                        break;
                    }
                    playCard(0);
                } else {
                    sendMessageToThisClient(server.canPlayCard(this));
                }
                break;
            case "%playCard2":
                if (server.canPlayCard(this).equals("ok")) {
                    if (playerProfile.mustPlayCountess) {
                        playCountess();
                        break;
                    }
                    playCard(1);
                } else {
                    sendMessageToThisClient(server.canPlayCard(this));
                }
                break;
            case "%scores":
                if (server.canGetScores().equals("ok")) {
                    sendMessageToThisClient(server.game.getScores());
                } else {
                    sendMessageToThisClient(server.canGetScores());
                }
                break;
            case "%turn":
                if (server.canGetTurn().equals("ok")) {
                    sendMessageToThisClient("It is " +
                            server.game.getPlayerAtTurn().getNickname()
                            + "'s turn");

                } else {
                    sendMessageToThisClient(server.canGetTurn());
                }
                break;
            default:
                chatWithRoom(message);
        }
    }

    /**
     * @return Provides the ability to choose another User in the chat.
     * @throws IOException
     */
    public ClientHandler chooseUser() throws IOException {
        sendMessageToThisClient("Please type in the users name:");
        String targetUsername = buffReader.readLine();
        if (targetUsername.equals(nickname)) {
            sendMessageToThisClient("You shouldn't talk to yourself! " +
                    "Please choose a user that is not you");
            return chooseUser();
        } else if (nicknameTaken(targetUsername)) {
            return findTargetClient(targetUsername);
        } else {
            sendMessageToThisClient("""
                    The username you have given has not been found. What would you like to do?\s
                     [1]: type a new username\s
                     Type anything else to exit.""");
            String option = buffReader.readLine();
            if ("1".equals(option)) {
                return chooseUser();
            }
            sendMessageToThisClient("Exiting private chat");
            return null;
        }
    }

    public PlayerProfile chooseOpponent() throws IOException {
        sendMessageToThisClient(server.game
                .getRivalPlayersList(playerProfile));
        String targetUsername = buffReader.readLine();
        if (targetUsername.equals(playerProfile.getNickname())) {
            sendMessageToThisClient("You can not choose yourself!" +
                    "\n" + "Please choose an opponent:");
            return chooseOpponent();
        }
        if (server.game.checkIfThereIsRivalName(targetUsername)) {
            if (server.game.getPlayerProfileFromName(targetUsername).isProtected) {
                sendMessageToThisClient("This player is protected!");
                return playerProfile;
            }
            return server.game.getPlayerProfileFromName(targetUsername);
        }
        return chooseOpponent();
    }

    public PlayerProfile choosePrinceOpponent() throws IOException{
        sendMessageToThisClient("Please choose an opponent: ");
        sendMessageToThisClient(server.game
                .getPlayerList());
        String targetUsername = buffReader.readLine();
        if (Game.players.contains(server.game.getPlayerProfileFromName(targetUsername))) {
            if (server.game.getPlayerProfileFromName(targetUsername).isProtected) {
                sendMessageToThisClient("This player is protected!\n" +
                        "You must play the prince on yourself!");
                return playerProfile;
            }
            return server.game.getPlayerProfileFromName(targetUsername);
        }
        return choosePrinceOpponent();
    }

    public String guessCard() throws IOException {
        sendMessageToThisClient("Make a guess: " +
                "\nPriest, Baron, Handmaid, Prince, King, Countess, Princess");
        String guess = buffReader.readLine();
        if (server.game.allCardTypes.contains(guess) && !guess.equals("Guard")) {
            broadcastMessage(nickname + " guessed " + guess);
            return guess;
        } else {
            sendMessageToThisClient("Unexpected guess");
            return guessCard();
        }
    }

    /**
     * @author Aahash Kevin Sundararuban (@sundararuban)
     * Emre Tunçel (@tunel)
     * Robert Stefan Scholz (@scholzr)
     *
     * The method for the Client to play a card of their choice.
     * For cards that require choices the Client is forced to make
     * these choices. Prince and Guard cards have additional Methods.
     *
     * @param cardNumber Clients choice of card.
     * @throws IOException
     */
    public void playCard(int cardNumber) throws IOException {
        sendMessageToThisClient("You have played the " +
                playerProfile.getCards().get(cardNumber).getName());
        sendMessageToThisClient(playerProfile.getCards().get(cardNumber).getCardInfo());
        broadcastMessage(nickname + " played " + playerProfile.getCards().get(cardNumber).getName());
        if (playerProfile.getCards().get(cardNumber).getName().equals("Prince")){
            PlayerProfile opponent = choosePrinceOpponent();
            sendMessageToThisClient("You chose: " + opponent.getNickname());
            broadcastMessage(nickname + " chose " + opponent.getNickname());
            playerProfile.discardCard(playerProfile.getCards().get(cardNumber));
            playerProfile.getCards().get(cardNumber)
                    .playThisCard(playerProfile, opponent, cardNumber);
            server.game.playNextTurn();
            return;
        }
        if (playerProfile.getCards().get(cardNumber).needsPlayerChoice) {
            sendMessageToThisClient("Please choose an opponent: ");
            PlayerProfile opponent = chooseOpponent();
            if (opponent.equals(playerProfile)) {
                playerProfile.discardCard(playerProfile.getCards().get(cardNumber));
                playerProfile.getCards().remove(cardNumber);
                server.game.playNextTurn();
                return;
            }
            sendMessageToThisClient("You chose: " + opponent.getNickname());
            broadcastMessage(nickname + " chose " + opponent.getNickname());
            if (playerProfile.getCards().get(cardNumber)
                    .getName().equals("Guard")) {
                String guessedCard = guessCard();
                playerProfile.getCards().get(cardNumber).playGuard(playerProfile,
                        opponent, guessedCard);
                playerProfile.discardCard(playerProfile.getCards().remove(cardNumber));
                server.game.playNextTurn();
                return;
            }
            playerProfile.discardCard(playerProfile.getCards().get(cardNumber));
            playerProfile.getCards().get(cardNumber)
                    .playThisCard(playerProfile, opponent, cardNumber);
            server.game.playNextTurn();
            return;
        }
        playerProfile.discardCard(playerProfile.getCards().get(cardNumber));
        playerProfile.getCards().get(cardNumber).playThisCard(playerProfile, null, cardNumber);
        server.game.playNextTurn();
    }

    public void playCountess() {
        playerProfile.getCountess().playThisCard(playerProfile, null, 0);
        broadcastMessage(nickname + " played the Countess.");
        sendMessageToThisClient("You played the Countess.");
        playerProfile.mustPlayCountess = false;
        server.game.playNextTurn();
    }

    /**
     * @param targetUser Private message to a user.
     * @throws IOException
     */
    public void sendPrivateTextToUser(ClientHandler targetUser) throws IOException {
        if (!(targetUser == null)) {
            sendMessageToThisClient("You are now sending a private message to "
                    + targetUser.nickname + ". To exit type: %exit");
            String chat = buffReader.readLine();
            while (!chat.equals("%exit") && nicknameTaken(targetUser.nickname)) {
                targetUser.sendMessageToThisClient(nickname + " in private: " + chat);
                chat = buffReader.readLine();
            }
            sendMessageToThisClient("You have left private chat with " + targetUser.nickname);
        }
    }

    /**
     * Broadcasting method:
     * Sends a given message to all clients except the one sending it
     *
     * @param message message to broadcast to all the users
     */
    public void broadcastMessage(String message) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (!clientHandler.nickname.equals(nickname)) {
                clientHandler.sendMessageToThisClient(message);
            }
        }
    }

    /**
     * @param message The player's name of a message will be shown next to the message.
     */
    public void chatWithRoom(String message) {
        for (ClientHandler clientHandler : clientHandlers) {
            if (!clientHandler.nickname.equals(nickname)) {
                clientHandler.sendMessageToThisClient(nickname + ": " + message);
            }
        }
    }

    /**
     * Remove method for the network.ClientHandler:
     * Removes this network.ClientHandler object from the ClientHandlers list and notifies other users
     */
    public void removeClientHandler() {
        clientHandlers.remove(this);
        if (server.game != null && server.isPlayerInTheGame(this)){
            broadcastMessage(nickname + " left the game.");
        }
        broadcastMessage(nickname + " left the room.");
    }

    /**
     * Helper method to terminate program
     *
     * @param socket         ClientHandlers socket object
     * @param bufferedReader ClientHandlers buffered-reader object
     * @param bufferedWriter ClientHandlers buffered-writer object
     */
    public void programmSchliessen(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        server.handleClientExit(this);
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PlayerProfile getPlayerProfile() {
        return playerProfile;
    }
    String help = """
            Type one of these commands to:
            bye => leave.
            %cardhelp => see what each card's value and info.
            %private => choose a user to send a private message.
            %creategame => to create a new game.
            %joingame => join an existing game.
            %startgame => start the game.
            %leavegame => leave the game.
            %showcards => see your cards.
            %showdiscardedcards => see all the discarded cards.
            %playCard1 => play the first card in your hand.
            %playCard2 => play the second card in your hand.
            %scores => see all the players' scores
            %turn => see which player is currently at turn""";
    String cardhelp = """
            Guard (1) : Guess another player's hand (except Guard) → If correct, that player is out.
            Priest (2) : See another player's hand
            Baron (3) : Compare hands → Player with lower hand is out
            Handmaid (4) : You're immune to other player's cards until their next turn
            Prince (5) : Choose a player to discard their hand
            King (6) : Trade hands with another player
            Countess (7) : If you have Prince or King → Play this card!
            Princess (8) : If you play this card → You'll lose
            """;
}
