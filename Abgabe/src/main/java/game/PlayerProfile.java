package game;

import game.Cards.Card;
import network.ClientHandler;

import java.util.ArrayList;

/**
 * The PlayerProfile class
 *
 * @author Emre Tunçel (@tunel)
 * Nora Schwaabe (@schwaabe)
 */

public class PlayerProfile {
    /**
     * Bridge between user and server.
     */
    private final ClientHandler clientHandler;

    /**
     * Score number, how many times a player has won.
     */
    private int score;

    /**
     * The name of a player.
     */
    private String nickname;

    public boolean isProtected = false;

    public boolean mustPlayCountess = false;

    /**
     * The cards will be shown on a list.
     */
    private ArrayList<Card> cards = new ArrayList<>();

    private ArrayList<Card> discardedCards = new ArrayList<>();

    /**
     * @param nickname      The name of a player.
     * @param clientHandler Bridge between user and server.
     */
    public PlayerProfile(String nickname, ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
        this.score = 0;
        this.nickname = nickname;
    }

    /**
     * @return Score.
     */
    public int getScore() {
        return score;
    }

    /**
     * @return Name of the player.
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * @return Receive cards.
     */
    public ArrayList<Card> getCards() {
        return cards;
    }

    public ArrayList<Card> getDiscardedCards() {
        return discardedCards;
    }

    /**
     * @param i Amount of cards that a player can get.
     * @return Amount of i cards.
     */
    public Card getCardAt(int i) {
        return cards.get(i);
    }

    /**
     * @return The amount of cards that the player is holding.
     */
    public String showCards() {
        StringBuilder helper = new StringBuilder("Your cards are: ");
        int i = 1;
        for (Card temp : cards) {
            helper.append("[").append(i).append("] ").append(temp.getName()).append(" ");
            i++;
        }
        return helper.toString();
    }

    public void incrementScore() {
        this.score++;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void giveCard(Card card) {
        cards.add(card);
    }

    public ClientHandler getClientHandler() {
        return clientHandler;
    }

    public void checkForCountess() {
        for (int i = 0; i < 2; i++) {
            if (getCardAt(i).getName().equals("King") || getCardAt(i).getName().equals("Prince")) {
                if (getCardAt(1 - i).getName().equals("Countess")) {
                    mustPlayCountess = true;
                }
            }
        }
    }

    public Card getCountess() {
        for (Card temp : cards) {
            if (temp.getName().equals("Countess")) {
                return temp;
            }
        }
        return null;
    }

    public void discardCard(Card card) {
        discardedCards.add(card);
    }

    public int calculateDiscardedCardSum() {
        int sum = 0;
        for (Card temp : discardedCards) {
            sum += temp.getValue();
        }
        return sum;
    }

    public String showDiscardedCards() {
        StringBuilder str = new StringBuilder();
        str.append(nickname).append(" has discarded so far: ");
        if (discardedCards.size() == 0) {
            return str.toString();
        }
        for (Card temp : discardedCards) {
            str.append(temp.getName()).append(", ");
        }
        return str.toString();
    }
}
