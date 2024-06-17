package game.Cards;

import game.Game;
import game.PlayerProfile;

/**
 * The Card class.
 *
 * @author Emre Tunçel (@tunel)
 * Robert Stefan Scholz (@scholzr)
 */

public abstract class Card {
    public final Game game;
    protected String info;
    protected String name;
    protected int value;
    public boolean needsPlayerChoice;

    /**
     * Player can retrieve information about a card.
     *
     * @return Information of a card.
     */
    public String getCardInfo() {
        return info;
    }

    /**
     * Player is playing in a game.
     *
     * @param game Game that is still going.
     */
    public Card(Game game) {
        this.game = game;
    }

    /**
     * Player will know the value of a card.
     *
     * @return Value of a card.
     */
    public int getValue() {
        return value;
    }

    /**
     * Player will know the name of a card.
     *
     * @return Name of a card.
     */
    public String getName() {
        return name;
    }

    /**
     * Player A is playing a card.
     *
     * @param player   The player (Player A) uses a card.
     * @param Opponent Target player (Player B).
     * @param card     The value of a card.
     */
    public void playThisCard(PlayerProfile player, PlayerProfile Opponent, int card) {
    }

    /**
     * Player A is playing the Guard card.
     *
     * @param player   The player (Player A) use the Guard card.
     * @param Opponent Target player (Player B).
     * @param guess    Player A has to guess the correct card of Player B.
     */
    public void playGuard(PlayerProfile player, PlayerProfile Opponent, String guess) {
    }

}
