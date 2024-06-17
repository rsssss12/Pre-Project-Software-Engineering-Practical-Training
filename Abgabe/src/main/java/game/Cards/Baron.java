package game.Cards;

import game.Game;
import game.PlayerProfile;

/**
 * The cards.Baron class.
 *
 * @author Emre Tunçel (@tunel)
 * Robert Stefan Scholz (@scholzr)
 */

/**
 * Class for the card "Baron".
 */
public class Baron extends Card {
    public Baron(Game game) {
        super(game);
        this.value = 3;
        this.info = "Compare hands → Player with lower hand is out";
        this.name = "Baron";
        this.needsPlayerChoice = true;
    }

    /**
     * Describes what effects will occur when a player use the Baron card.
     * Card effect: Player A compares a card with Player B. If the card's value of Player B is lower than the card of Player A, Player B will be eliminated.
     *
     * @param player   The player (Player A) uses the Baron card.
     * @param opponent Target player (Player B).
     * @param card     The value of the card.
     */

    public void playThisCard(PlayerProfile player, PlayerProfile opponent, int card) {
        player.getCards().remove(player.getCardAt(card));
        if (player.getCardAt(0).value < opponent.getCardAt(0).value) {
            game.sendMessageToAllPlayers(player.getNickname() + " lost the comparison!" +
                    "\n" + player.getNickname() + " is eliminated!");
            player.discardCard(player.getCardAt(0));
            game.removePlayer(player);
            return;
        }
        if (player.getCardAt(0).value > opponent.getCardAt(0).value) {
            game.sendMessageToAllPlayers(opponent.getNickname() + " lost the comparison!" +
                    "\n" + opponent.getNickname() + " is eliminated!");
            opponent.discardCard(opponent.getCardAt(0));
            game.removePlayer(opponent);
            return;
        }
        game.sendMessageToAllPlayers("It is a tie! \n" +
                "No one is eliminated.");
    }
}
