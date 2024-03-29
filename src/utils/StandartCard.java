package utils;

import handlers.CardEventsHandler;

import java.io.Serializable;

import carddeckplatform.game.BitmapHolder;


public  class StandartCard extends Card {
	
	public  enum Color implements Serializable{
		HEART("h"),
		DIAMOND("d"),
		SPADE("s"),
		CLUB("c");
		final String code;
		
		Color( String code){
			this.code = code;
		}
		public String getCode(){
			return code;
		}
	}
	private final Color color;
	private final int value;
	
	public StandartCard(CardEventsHandler handler,String frontImg,String backImg, int value,Color color) {		
		super(handler,frontImg,backImg);
		this.value=value;	
		this.color=color;
		this.scale = new Point(5,10);
		this.handScale = new Point(3,5);
		BitmapHolder.get().scaleBitmap(frontImg, this.scale);
		BitmapHolder.get().scaleBitmap(backImg, this.scale);
		
//		BitmapHolder.get().scaleBitmap(frontImg, scale);
//		BitmapHolder.get().scaleBitmap(backImg, scale);
		
	}
	
	/**
	 * gets the color of the card.
	 * @return
	 */
	public Color getColor() {
		return color;
	}
	
	/**
	 * gets the value of the card.
	 * @return
	 */
	public int getValue() {
		return value;
	}

	@Override
	public int compareTo(Card otherStandartCard) {
		StandartCard otherCard=(StandartCard)otherStandartCard;
		
//		if(getColor().getCode().equals("h") && otherCard.getColor().getCode().equals("d"))
//			return 1;
//		if(getColor().getCode().equals("h") && otherCard.getColor().getCode().equals("s"))
//			return 1;
//		if(getColor().getCode().equals("h") && otherCard.getColor().getCode().equals("c"))
//			return 1;
//		
//		
//		
//		if(getColor().getCode().equals("d") && otherCard.getColor().getCode().equals("h"))
//			return -1;
//		if(getColor().getCode().equals("d") && otherCard.getColor().getCode().equals("s"))
//			return 1;
//		if(getColor().getCode().equals("d") && otherCard.getColor().getCode().equals("c"))
//			return 1;
//		
//		if(getColor().getCode().equals("s") && otherCard.getColor().getCode().equals("d"))
//			return -1;
//		if(getColor().getCode().equals("s") && otherCard.getColor().getCode().equals("h"))
//			return -1;
//		if(getColor().getCode().equals("s") && otherCard.getColor().getCode().equals("c"))
//			return 1;
//		
//		
//		
//		if(getColor().getCode().equals("c") && otherCard.getColor().getCode().equals("d"))
//			return -1;
//		if(getColor().getCode().equals("c") && otherCard.getColor().getCode().equals("s"))
//			return -1;
//		if(getColor().getCode().equals("c") && otherCard.getColor().getCode().equals("h"))
//			return -1;
//		
//		
		if (this.value<otherCard.value){
			return -1;
		}else if (this.value>otherCard.value){
			return 1;
		}
		return 0;
	}	
	
}
