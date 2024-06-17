package game.Cards;

import game.Game;
import game.PlayerProfile;

/**
 * The cards.Handmaid class.
 *
 * @author Emre Tunçel (@tunel)
 * Robert Stefan Scholz (@scholzr)
 */

/**
 * Class for the card "Handmaid".
 */
public class Handmaid extends Card {
    public Handmaid(Game game) {
        super(game);
        this.value = 4;
        this.info = "You're immune to other player's cards until their next turn";
        this.name = "Handmaid";
        this.needsPlayerChoice = false;
    }

    /**
     * Describes what effects will occur when a player use the Handmaid card.
     * Card effect: Player A will be immune to other player's cards effects for one round.
     *
     * @param player   The player (Player A) uses the Handmaid card.
     * @param opponent Target player (Player B).
     * @param card     The value of the card.
     */
    public void playThisCard(PlayerProfile player, PlayerProfile opponent, int card) {
        player.isProtected = true;
        player.getCards().remove(card);
        game.sendMessageToAllPlayers(player.getNickname() + " is now immune for a turn");
    }
}
