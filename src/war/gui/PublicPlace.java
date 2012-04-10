package war.gui;

import client.gui.entities.Draggable;
import client.gui.entities.Droppable;
import logic.card.CardLogic;
import logic.client.LogicDroppable;
import carddeckplatform.game.R;
import android.content.Context;
import android.graphics.Canvas;

public class PublicPlace extends Droppable {
	
	public PublicPlace(Context context, int x,int y, LogicDroppable logicDroppable){
		this.x = x;
		this.y = y;
		this.logicDroppable = logicDroppable;
		this.context = context;
	}
	
	@Override
	public int sensitivityRadius() {
		// TODO Auto-generated method stub
		return 300;
	}
	
	@Override
	public void onClick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onHover() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void addDraggable(Draggable draggable) {
		// TODO Auto-generated method stub
		draggable.setLocation(getX(), getY());

// 		if(draggable.getContainer()!=null)
//			draggable.getContainer().  remove the card from the previous droppable logic.

		draggable.setContainer(this);
		logicDroppable.addCard(draggable.getCardLogic());
	}

	@Override
	public void onDrop(Draggable draggable) {
		// TODO Auto-generated method stub
		addDraggable(draggable);
		logicDroppable.onDropHandler(draggable.getCardLogic());
	}

	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CardLogic getDraggable() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeDraggable(Draggable draggable) {
		// TODO Auto-generated method stub
		logicDroppable.getCards().remove(draggable.getCardLogic());
	}



}