package client.gui.animations;

import java.util.ArrayList;

import utils.Card;
import utils.Point;
import carddeckplatform.game.StaticFunctions;
import client.controller.ClientController;
import client.gui.entities.Droppable;

public class FlipAnimation extends Animation {

	public FlipAnimation(Droppable source, Droppable destination,Card card,boolean sendToCommunication) {
		this.source = source;
		this.destination = destination;
		this.card=card;
		this.sendToCommunication=sendToCommunication;
	}

	private Card card;
	final Droppable source;
	final Droppable destination;
	boolean sendToCommunication;

	@Override
	protected void animate() {
		float x = card.getX();
		float y = card.getY();
		
		card.getAnimationFlags().resetFlags();
		card.getAnimationFlags().flip=true;
		
		final ArrayList<Point> vector = StaticFunctions.midLine((int)x, (int)y, (int)destination.getX(), (int)destination.getY());
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {			        			
			e.printStackTrace();
		}
    	for(int i=0; i<vector.size(); i++){
    		final int index = i;

    		try {
    			Thread.sleep(2);
    		} catch (InterruptedException e) {			            			
    			e.printStackTrace();
    		}					
    		
    		card.setLocation(vector.get(index).getX(), vector.get(index).getY());
    		card.setAngle(i*2);            		
    	}
    	card.setLocation(destination.getX(), destination.getY());
    	card.setAngle(0);
		
		
	}

	@Override
	protected void postAnimation() {
		if(sendToCommunication){
			destination.onDrop(ClientController.get().getMe(), source,
					((Card) card));
			
		}else{
			source.removeCard(null,card);
			destination.addCard(null,card);
		}
	}

}
