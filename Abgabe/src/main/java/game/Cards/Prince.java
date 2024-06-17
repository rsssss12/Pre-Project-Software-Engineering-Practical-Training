package game.Cards;

import game.Game;
import game.PlayerProfile;

/**
 * The cards.Prince class.
 *
 * @author Emre Tunçel (@tunel)
 * Robert Stefan Scholz (@scholzr)
 */

/**
 * Class for the card "Prince".
 */
public class Prince extends Card {
    public Prince(Game game) {
        super(game);
        this.value = 5;
        this.info = "Choose a player to discard their hand";
        this.name = "Prince";
        this.needsPlayerChoice = true;
    }

    /**
     * Describes what effects will occur when a player use the Prince card.
     * Card effect: Player A will choose Player B to discard their hands.
     *
     * @param player   The player (Player A) uses the Prince card.
     * @param opponent Target player (Player B).
     * @param card     The value of the card.
     */
    public void playThisCard(PlayerProfile player, PlayerProfile opponent, int card) {
        if (player.equals(opponent)) {
            player.getCards().remove(card);
            try {
                player.giveCard(game.deck.remove(0));
            } catch (IndexOutOfBoundsException e) {
                player.giveCard(game.firstRemovedCard);
            }
            player.discardCard(player.getCardAt(0));
            game.sendMessageToAllPlayers(player.getNickname() + " discarded "
                     + player.getCardAt(0).getName() );
            player.getCards().remove(0);
            return;
        }
        if (opponent.getCardAt(0).getName().equals("Princess")) {
            opponent.getCardAt(0).playThisCard(opponent, null, 0);
        }
        opponent.discardCard(opponent.getCardAt(0));
        game.sendMessageToAllPlayers(opponent.getNickname() + " discarded "
                + opponent.getCardAt(0).getName() );
        opponent.getCards().remove(0);
        try {
            opponent.giveCard(game.deck.remove(0));
        } catch (IndexOutOfBoundsException e) {
            opponent.giveCard(game.firstRemovedCard);
        }
        player.getCards().remove(card);
    }
}
