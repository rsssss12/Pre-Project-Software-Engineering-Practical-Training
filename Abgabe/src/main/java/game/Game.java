package game;

import game.Cards.*;
import network.Server;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * The Game class
 *
 * @author Aahash Kevin Sundararuban (@sundararuban)
 * Emre Tunçel (@tunel)
 * Mohammad Taha (@taham)
 */

public class Game {

    public static ArrayList<PlayerProfile> players = new ArrayList<>();

    public static ArrayList<PlayerProfile> allPlayers = new ArrayList<>();

    public ArrayList<Card> deck = new ArrayList<>();

    public ArrayList<Card> discardedThree = new ArrayList<>();

    public boolean isRunning = false;

    private PlayerProfile playerAtTurn;

    private int round = 0;

    private static int winnerScore;

    private Server server;

    public Card firstRemovedCard;

    public ArrayList<String> allCardTypes = new ArrayList<>(
            Arrays.asList(
                    "Priest",
                    "Baron",
                    "Handmaid",
                    "Prince",
                    "King",
                    "Countess",
                    "Princess"));

    /**
     * A game will be created.
     */
    public void run(Server server) {
        this.server = server;
        isRunning = true;
        System.out.println("The game has started!");
        switch (allPlayers.size()) {
            case 2 -> winnerScore = 7;
            case 3 -> winnerScore = 5;
            case 4 -> winnerScore = 3;
        }
        newRound();
    }

    public void newRound() {
        System.out.println("A new round has started!");
        sendMessageToAllPlayers("A new round has started!");
        round++;
        clearPlayersHands();
        retrieveEliminatedPlayers();
        if (players.size() < 2) {
            sendMessageToAllPlayers("There are not enough players to play the game.");
            endGame();
            return;
        }
        createDeck();
        shuffleCards();
        if (players.size() == 2) {
            discardedThree.clear();
            showFirstThreeCards();
        }
        firstRemovedCard = deck.remove(0);
        dealCards();
        getScores();
        if (round > 1) {
            playerAtTurn = players.get(0);
            sendMessageToAllPlayers("It is " + playerAtTurn.getNickname() + "'s turn");
            players.get(0).giveCard(deck.remove(0));
            playerAtTurn.checkForCountess();
            if(playerAtTurn.mustPlayCountess){
                sendMessageToPlayer(playerAtTurn," You must play Countess!");
            }
            players.add(players.remove(0));
        }
        showPlayersTheirCards();
        if (round == 1) {
            playNextTurn();
        }
    }

    private void retrieveEliminatedPlayers() {
        for (PlayerProfile temp : allPlayers) {
            if (!players.contains(temp)) {
                players.add(temp);
            }
        }
    }

    public void showFirstThreeCards() {
        StringBuilder str = new StringBuilder();
        str.append("The first three cards are: ");
        for (int i = 0; i < 3; i++) {
            str.append(deck.get(i).getName()).append(" ");
        }
        discardedThree.add(deck.remove(0));
        discardedThree.add(deck.remove(0));
        discardedThree.add(deck.remove(0));
        sendMessageToAllPlayers(str.toString());
    }

    public PlayerProfile getPlayerProfileFromName(String nickname) {
        for (PlayerProfile temp : players) {
            if (temp.getNickname().equals(nickname)) {
                return temp;
            }
        }
        return null;
    }

    /**
     * @return The number of participants in a game.
     */
    public boolean isFull() {
        return allPlayers.size() >= 4;
    }

    /**
     * @return Turn after a player has played a card.
     */
    public PlayerProfile getPlayerAtTurn() {
        return playerAtTurn;
    }

    /**
     * @return Player List will be retrieved.
     */
    public String getPlayerList() {
        StringBuilder playerList = new StringBuilder();
        for (PlayerProfile player : players) {
            if (player.getCards().size() > 0) {
                playerList.append(player.getNickname()).append(", ");
            }
        }
        return playerList.toString();
    }

    /**
     * @param player The player participating in the current game.
     * @return Rival Player List will be retrieved.
     */
    public String getRivalPlayersList(PlayerProfile player) {
        StringBuilder playerList = new StringBuilder();
        for (PlayerProfile temp : players) {
            if (!temp.getNickname().equals(player.getNickname())) {
                playerList.append(temp.getNickname()).append(", ");
            }
        }
        return playerList.toString();
    }

    public ArrayList<PlayerProfile> getRivalPlayers(PlayerProfile player) {
        ArrayList<PlayerProfile> rivals = new ArrayList<>();
        for (PlayerProfile temp : players) {
            if (temp.equals(player)) {
                rivals.add(temp);
            }
        }
        return rivals;
    }

    /**
     * @return Indicates the Score of a player.
     */
    public boolean checkIfThereIsRivalName(String rivalName) {
        for (PlayerProfile temp : getRivalPlayers(getPlayerProfileFromName(rivalName))) {
            if (temp.equals(getPlayerProfileFromName(rivalName))) {
                return true;
            }
        }
        return false;
    }

    public String getScores() {
        StringBuilder scores = new StringBuilder();
        scores.append("Scores are: ");
        for (PlayerProfile temp : allPlayers) {
            scores.append("\n").append(temp.getNickname()).append(": ").append(temp.getScore());
        }
        scores.append("\nYou need a score of ").append(winnerScore).append(" to win.");
        return scores.toString();
    }

    /**
     * @param player  The player participating in the current game.
     * @param message Message that the player is writing.
     */
    public void sendMessageToPlayer(PlayerProfile player, String message) {
        player.getClientHandler().sendMessageToThisClient(message);
    }

    /**
     * @param message Player can send messages to all player.
     */
    public void sendMessageToAllPlayers(String message) {
        for (PlayerProfile player : allPlayers) {
            player.getClientHandler().sendMessageToThisClient(message);
        }
    }

    /**
     * Deck will be created.
     */
    public void createDeck() {
        for (int i = 0; i < 5; i++) {
            this.deck.add(new Guard(this));
        }
        this.deck.add(new Priest(this));
        this.deck.add(new Priest(this));
        this.deck.add(new Baron(this));
        this.deck.add(new Baron(this));
        this.deck.add(new Handmaid(this));
        this.deck.add(new Handmaid(this));
        this.deck.add(new Prince(this));
        this.deck.add(new Prince(this));
        this.deck.add(new King(this));
        this.deck.add(new Countess(this));
        this.deck.add(new Princess(this));
    }

    public void shuffleCards() {
        Collections.shuffle(deck);
    }

    /**
     * Card will be handed to all players.
     */
    public void dealCards() {
        for (PlayerProfile player : players) {
            player.giveCard(deck.remove(0));
        }
    }

    /**
     * @author Aahash Kevin Sundararuban (@sundararuban)
     * Emre Tunçel (@tunel)
     * Mohammad Taha (@taham)
     *
     * Method to play the next turn.
     */
    public void playNextTurn() {
        try {
            eliminatePlayers();
            if (!checkGameRequirements()) {
                if (getWinner().equals("no")) {
                    clearDeck();
                    newRound();
                } else {
                    sendMessageToAllPlayers(getWinner() + " won the game!!!");
                    endGame();
                }
                return;
            }
            playerAtTurn = players.get(0);
            playerAtTurn.isProtected = false;
            players.get(0).giveCard(deck.remove(0));
            playerAtTurn.checkForCountess();
            sendMessageToAllPlayers("Game: It is " + playerAtTurn.getNickname() + "'s turn.\n" +
                    "There are " + deck.size() + " cards left");
            sendMessageToPlayer(playerAtTurn, playerAtTurn.showCards());
            sendMessageToPlayer(playerAtTurn, """
                    Please choose which card you want to play:\s
                    Type %playCard1 to play your first card\s
                    Type %playCard2 to play your second card""");
            if (playerAtTurn.mustPlayCountess) {
                sendMessageToPlayer(playerAtTurn, " You must play Countess!");
            }
            players.add(players.remove(0));
        }catch(IndexOutOfBoundsException e){
            endGame();
        }
    }

    public void showPlayersTheirCards() {
        for (PlayerProfile player : players) {
            sendMessageToPlayer(player, player.showCards());
        }
    }

    public String getWinner() {
        for (PlayerProfile temp : allPlayers) {
            if (temp.getScore() == winnerScore)
                return temp.getNickname();
        }
        return "no";
    }

    public void eliminatePlayers() {
        for (PlayerProfile temp : players) {
            if (temp.getCards().size() == 0) {
                players.remove(temp);
                sendMessageToAllPlayers(temp.getNickname() + " has been eliminated!");
                return;
            }
        }
    }

    public boolean checkGameRequirements() {
        if (players.size() == 1) {
            players.get(0).incrementScore();
            sendMessageToAllPlayers(players.get(0).getNickname() + " won this round!");
            sendMessageToAllPlayers(getScores());
            return false;
        }
        if (deck.size() == 0) {
            sendMessageToAllPlayers("There are no more cards left!" +
                    "\nThe round has ended.");
            decideRoundWinner();
            return false;
        }
        return true;
    }

    public void removePlayer(PlayerProfile player) {
        players.remove(player);
        if (player.getCards().size() > 0) {
            sendMessageToAllPlayers(player.getNickname() +
                    " had a " + player.getCardAt(0).getName() +
                    " in his/her hand.");
        }
    }

    public void playerLeft(PlayerProfile player) {
        if(playerAtTurn == null){
            return;
        }
        if (playerAtTurn.equals(player)) {
            playNextTurn();
        }
    }

    public void decideRoundWinner() {
        ArrayList<PlayerProfile> winners = new ArrayList<>();
        winners.add(players.get(0));
        for (PlayerProfile player : players) {
            if (player.getCardAt(0).getValue() > winners.get(0).getCardAt(0).getValue()) {
                winners.remove(winners.get(0));
                winners.add(player);
            }
            if (player.getCardAt(0).getValue() == winners.get(0).getCardAt(0).getValue()) {
                winners.add(player);
            }
        }
        if (winners.contains(players.get(0))) {
            winners.remove(players.get(0));
        }
        if (winners.size() > 1) {
            for (PlayerProfile winner : winners) {
                if (winner.calculateDiscardedCardSum() < winners.get(0).calculateDiscardedCardSum()) {
                    winners.remove(winner);
                    continue;
                }
                if (winner.calculateDiscardedCardSum() > winners.get(0).calculateDiscardedCardSum()) {
                    winners.remove(winners.get(0));
                }
            }
        }
        StringBuilder winnersString = new StringBuilder();
        for (PlayerProfile winner : winners) {
            winner.incrementScore();
            winnersString.append(winner.getNickname()).append("\n");
        }
        sendMessageToAllPlayers("Winner(s) of this round are " + "\n" + winnersString);
    }

    public void clearDeck() {
        deck.clear();
    }

    public void clearPlayersHands() {
        for (PlayerProfile player : allPlayers) {
            player.getCards().clear();
            player.getDiscardedCards().clear();
        }
    }

    public String allDiscardedCards() {
        StringBuilder str = new StringBuilder();
        for (PlayerProfile temp : allPlayers) {
            str.append("\n").append(temp.showDiscardedCards());
        }
        if(discardedThree.size() > 0){
            str.append("\n").append("First three discarded cards are:");
            for (Card temp: discardedThree) {
                str.append("\n").append(temp.getName());
            }
        }
        return str.toString();
    }

    public void endGame() {
        sendMessageToAllPlayers("The game has ended");
        allPlayers.clear();
        players.clear();
        server.setGame(null);
    }
}
