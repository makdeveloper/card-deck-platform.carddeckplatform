package utils.droppableLayouts;

import java.io.Serializable;
import java.util.AbstractList;

import utils.Card;
import utils.Point;
import utils.StandardSizes;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import client.gui.entities.Droppable;

public abstract class DroppableLayout implements Serializable {
	public enum LayoutType {
		NONE, LINE, HEAP, DECK;
		public DroppableLayout getLayout(Droppable droppable){
			DroppableLayout answer=null;
			switch(this){
				case LINE:{
					answer=new LineLayout(droppable);
					break;
				}
				case HEAP:{
					answer=new HeapLayout(droppable); 
					break;
				}
				case DECK:{
					answer=new DeckLayout(droppable); 
					break;
				}
				case NONE:{
					break;
				}
				default:{}
			}
			return answer;
		}
		
		public Point getScale(){
			Point answer=null;
			switch(this){
			case LINE:{
				answer=StandardSizes.LINE_AREA;
				break;
			}
			case HEAP:{
				answer=StandardSizes.HEAP_AREA;
				break;
			}
			case DECK:{
				answer=StandardSizes.HEAP_AREA;
				break;
			}
			case NONE:{
				answer=StandardSizes.FREE_AREA;
				break;
			}
			default:{
				answer=StandardSizes.HEAP_AREA;
				break;
			}
		}
		return answer;
		}
	}
		
	protected Droppable droppable;
	AnimationRunnable animationRunnable = null;

	public abstract void rearrange(int index, float width, float height);

	public DroppableLayout(Droppable droppable) {
		this.droppable = droppable;
	}

	protected float[][] shift(float[][] animationArgs, float x, float y) {
		for (int i = 0; i < animationArgs[0].length; i++) {
			animationArgs[0][i] = animationArgs[0][i] + x;
			animationArgs[1][i] = animationArgs[1][i] + y;
		}
		return animationArgs;

	}

	protected float[][] normalizePosition(float[][] animationArgs, float width,
			float height) {
		int length = animationArgs[0].length;
		if (length == 0)
			return animationArgs;
		float maxX = animationArgs[0][0];
		float maxY = animationArgs[1][0];
		float minX = animationArgs[0][0];
		float minY = animationArgs[1][0];

		for (int i = 0; i < length; i++) {
			if (animationArgs[0][i] > maxX)
				maxX = animationArgs[0][i];
			if (animationArgs[1][i] > maxY)
				maxY = animationArgs[1][i];
			if (animationArgs[0][i] < minX)
				minX = animationArgs[0][i];
			if (animationArgs[1][i] < minY)
				minY = animationArgs[1][i];

		}
		if ((maxX - minX) != 0)
			for (int i = 0; i < length; i++) {
				animationArgs[0][i] = (animationArgs[0][i] - minX)
						/ (maxX - minX) * width;
			}
		if ((maxY - minY) != 0)
			for (int i = 0; i < length; i++) {
				animationArgs[1][i] = (animationArgs[1][i] - minY)
						/ (maxY - minY) * height;
			}

		return animationArgs;

	}

	class AnimationRunnable implements Runnable {

		public AnimationRunnable(float[][] animationArgs,
				AbstractList<Card> abstractList, long duration) {
			this.animationArgs = animationArgs;
			this.abstractList = abstractList;
			this.duration = duration;
			
//			for(Card c : abstractList){
//				if(c.getAnimationFlags().checkRearrangeCondition())
//					c.getAnimationFlags().rearrange=true;
//			}
		}

		public void stopAnimation() {
			running = false;

		}

		boolean running = true;
		float[][] animationArgs;
		AbstractList<Card> abstractList;
		long duration;

		@Override
		public void run() {
			// save the initial value
			Interpolator animateInterpolator = new AccelerateDecelerateInterpolator();
			System.out.println("rearrange:" + animationArgs[0].length);
			float[] totalDx = new float[animationArgs[0].length];
			float[] totalDy = new float[animationArgs[0].length];
			float[] totalDAngle = new float[animationArgs[0].length];
			float[][] reserved = new float[3][animationArgs[0].length];

			for (int i = 0; i < totalDx.length; i++) {
				totalDx[i] = animationArgs[0][i] - abstractList.get(i).getX();
				totalDy[i] = animationArgs[1][i] - abstractList.get(i).getY();
				totalDAngle[i] = animationArgs[2][i]
						- abstractList.get(i).getAngle();

				Card card = abstractList.get(i);
				reserved[0][i] = card.getX();
				reserved[1][i] = card.getY();
				reserved[2][i] = card.getAngle();
			}

			long startTime = System.currentTimeMillis();
			long endTime = startTime + duration;
			long curTime = System.currentTimeMillis();

			float percentTime = (float) (curTime - startTime)
					/ (float) (endTime - startTime);
			float percentDistance = animateInterpolator
					.getInterpolation(percentTime);

			float[] curDx = new float[animationArgs[0].length];
			float[] curDy = new float[animationArgs[0].length];
			float[] curDAngle = new float[animationArgs[0].length];
			for (int i = 0; i < totalDx.length; i++) {
				curDx[i] = percentDistance * totalDx[i];
				curDy[i] = percentDistance * totalDy[i];
				curDAngle[i] = percentDistance * totalDAngle[i];

			}
			for (int i = 0; i < totalDx.length; i++) {
				//setLocationAndAngle(abstractList.get(i),reserved[0][i] + curDx[i],
				//		reserved[1][i] + curDy[i],reserved[2][i] + curDAngle[i]);
				Card card = abstractList.get(i);
				setLocationAndAngle(card, reserved[0][i] + curDx[i], reserved[1][i] + curDy[i], reserved[2][i] + curDAngle[i]);
			}
			while (percentTime < 1.0 && running) {
				curTime = System.currentTimeMillis();
				percentTime = (float) (curTime - startTime)
						/ (float) (endTime - startTime);
				percentDistance = animateInterpolator
						.getInterpolation(percentTime);

				for (int i = 0; i < totalDx.length; i++) {
					curDx[i] = percentDistance * totalDx[i];
					curDy[i] = percentDistance * totalDy[i];
					curDAngle[i] = percentDistance * totalDAngle[i];
					Card card = abstractList.get(i);
					//setLocationAndAngle(card,reserved[0][i] + curDx[i], reserved[1][i]
					//		+ curDy[i],reserved[2][i] + curDAngle[i]);
					
					
					setLocationAndAngle(card, reserved[0][i] + curDx[i], reserved[1][i] + curDy[i], reserved[2][i] + curDAngle[i]);
				}
			}
//			if (running){
//				for (int i = 0; i < animationArgs[0].length; i++) {
//					Card card = abstractList.get(i);
//					setLocationAndAngle(card, animationArgs[0][i], animationArgs[1][i], animationArgs[2][i]);
//					//card.setLocation(animationArgs[0][i], animationArgs[1][i]);
//					//card.setAngle(animationArgs[2][i]);
//				}
//			}
			
			for(Card card : abstractList){
				if(droppable.getCards().contains(card)){
					card.getAnimationFlags().rearrange = false;
				}
			}
		}

		private void setLocationAndAngle(Card card, float x, float y, float angle) {
			// will rearrange if:
			// (1) the card is still belongs to the droppbale.
			// (2) the rearrange flag is on.
			// (3) all the other animation flags are off (no other animation affects the card).
			
			// 					(1)									(2)											(3)
			if (droppable.getCards().contains(card) && card.getAnimationFlags().checkRearrangeCondition()){
				card.setLocation(x, y);
				card.setAngle(angle);
			}
		}

	}

	public abstract LayoutType getType();

	public void stopAnimation() {

		if (this.animationRunnable != null)
			animationRunnable.stopAnimation();
	}
	
	public void animate(AbstractList<Card> abstractList,
			float[][] animationArgs, long duration) {

		if (animationRunnable != null)
			animationRunnable.stopAnimation();

		this.animationRunnable = new AnimationRunnable(animationArgs,
				abstractList, duration);

		new Thread(animationRunnable).start();
		
		
	}
}
