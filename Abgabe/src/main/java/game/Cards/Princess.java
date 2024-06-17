package game.Cards;

import game.Game;
import game.PlayerProfile;

/**
 * The cards.Princess class.
 *
 * @author Emre Tunçel (@tunel)
 * Robert Stefan Scholz (@scholzr)
 */

/**
 * Class for the card "Princess".
 */
public class Princess extends Card {
    public Princess(Game game) {
        super(game);
        this.value = 8;
        this.info = "If you play this card → You'll lose";
        this.name = "Princess";
        this.needsPlayerChoice = false;
    }

    /**
     * Describes what effects will occur when a player use the Princess card.
     * Card effect: If Player A uses the Princess card, Player A will be eliminated.
     *
     * @param player   The player (Player A) uses the Princess card.
     * @param opponent Target player (Player B).
     * @param card     The value of the card.
     */
    public void playThisCard(PlayerProfile player, PlayerProfile opponent, int card) {
        game.sendMessageToAllPlayers(player.getNickname() + " played the princess and is eliminated!");
        game.removePlayer(player);
    }
}
