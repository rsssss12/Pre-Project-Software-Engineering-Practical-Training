package game.Cards;

import game.Game;
import game.PlayerProfile;

/**
 * The cards.Countess class.
 *
 * @author Emre Tunçel (@tunel)
 * Robert Stefan Scholz (@scholzr)
 */

/**
 * Class for the card "Countess".
 */
public class Countess extends Card {
    public Countess(Game game) {
        super(game);
        this.value = 7;
        this.info = "If you have Prince or King → Play this card!";
        this.name = "Countess";
        this.needsPlayerChoice = false;
    }

    /**
     * Describes what effects will occur when a player use the Countess card.
     * Card effect: If Player A has a Prince or King card, he will have to discard the Countess.
     *
     * @param player   The player (Player A) uses the Countess card.
     * @param opponent Target Player (Player B).
     * @param card     The value of the card.
     */
    public void playThisCard(PlayerProfile player, PlayerProfile opponent, int card) {
        player.discardCard(player.getCountess());
        player.getCards().remove(player.getCountess());
    }
}
