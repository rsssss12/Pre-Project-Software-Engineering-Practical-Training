package game.Cards;

import game.Game;
import game.PlayerProfile;

/**
 * The cards.King class.
 *
 * @author Emre Tunçel (@tunel)
 * Robert Stefan Scholz (@scholzr)
 */

/**
 * Class for the card "King".
 */

public class King extends Card {
    public King(Game game) {
        super(game);
        this.value = 6;
        this.info = "Trade hands with another player";
        this.name = "King";
        this.needsPlayerChoice = true;
    }

    /**
     * Describes what effects will occur when a player use the King card.
     * Card effect: Player A will swap cards with Player B.
     *
     * @param player   The player (Player A) uses the King card.
     * @param opponent Target player (Player B).
     * @param card     The value of the card.
     */
    public void playThisCard(PlayerProfile player, PlayerProfile opponent, int card) {
        player.getCards().remove(card);
        opponent.giveCard(player.getCards().remove(0));
        player.giveCard(opponent.getCards().remove(0));
        game.sendMessageToAllPlayers(player.getNickname() + " and " +
                opponent.getNickname() + " swapped their cards.");
    }
}
