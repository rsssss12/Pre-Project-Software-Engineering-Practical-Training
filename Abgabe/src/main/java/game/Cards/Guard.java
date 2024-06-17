package game.Cards;

import game.Game;
import game.PlayerProfile;

/**
 * The cards.Guard class.
 *
 * @author Emre Tunçel (@tunel)
 * Robert Stefan Scholz (@scholzr)
 */

/**
 * Class for the card "Guard".
 */
public class Guard extends Card {
    public Guard(Game game) {
        super(game);
        this.value = 1;
        this.info = "Guess another player's hand (except Guard) → If correct, that player is out";
        this.name = "Guard";
        this.needsPlayerChoice = true;
    }

    /**
     * Describes that a player is using the Guard card.
     * Card effect: If Player A guesses another player's hand (except a Guard card) and he's correct, Player B will be eliminated.
     *
     * @param player   The player (Player A) use the Guard card.
     * @param opponent Target player (Player B).
     * @param guess    Player A has to guess the correct card of Player B.
     */
    public void playGuard(PlayerProfile player, PlayerProfile opponent, String guess) {
        if (opponent.getCardAt(0).getName().equals(guess)) {
            game.sendMessageToAllPlayers(player.getNickname() + " guessed " +
                    guess + " and it was correct!");
            game.sendMessageToAllPlayers(opponent.getNickname() + " has been eliminated!");
            opponent.discardCard(opponent.getCardAt(0));
            game.removePlayer(opponent);
        } else {
            game.sendMessageToAllPlayers(player.getNickname() + " guessed " +
                    guess + " and it was wrong.");
        }
    }
}