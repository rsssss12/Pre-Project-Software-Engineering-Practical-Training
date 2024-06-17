package game.Cards;

import game.Game;
import game.PlayerProfile;

/**
 * The cards.Priest class.
 *
 * @author Emre Tunçel (@tunel)
 * Robert Stefan Scholz (@scholzr)
 */

/**
 * Class for the card "Priest".
 */
public class Priest extends Card {
    public Priest(Game game) {
        super(game);
        this.value = 2;
        this.info = "See another player's hand";
        this.name = "Priest";
        this.needsPlayerChoice = true;
    }

    /**
     * Describes what effects will occur when a player use the Priest card.
     * Card effect: Player A will see the cards from Player B.
     *
     * @param player   The player (Player A) uses the Priest card.
     * @param opponent Target player (Player B).
     * @param card     The value of the card.
     */
    public void playThisCard(PlayerProfile player, PlayerProfile opponent, int card) {
        player.getClientHandler().sendMessageToThisClient(opponent.getNickname() + " has a " + opponent.getCardAt(0).getName());
        player.getCards().remove(card);
        game.sendMessageToAllPlayers(player.getNickname() + " saw " +
                opponent.getNickname() + "'s card.");
    }
}
