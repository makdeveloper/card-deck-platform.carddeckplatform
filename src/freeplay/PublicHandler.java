package freeplay;

import utils.Card;
import utils.Player;
import utils.Public;
import handlers.PublicEventsHandler;

public class PublicHandler implements PublicEventsHandler{

	@Override
	public boolean onCardAdded(Public publicZone, Player player, Card card) {
		// TODO Auto-generated method stub
		card.reveal();
		return false;
	}

	@Override
	public boolean onCardRemoved(Public publicZone, Player player, Card card) {
		// TODO Auto-generated method stub
		card.hide();
		return false;
	}

	@Override
	public boolean onCardRevealed(Public publicZone, Player player, Card card) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onRoundEnd(Public publicZone, Player player) {
		// TODO Auto-generated method stub
		return false;
	}

}
