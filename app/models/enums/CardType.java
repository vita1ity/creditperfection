package models.enums;

public enum CardType {
	
	// MASTER_CARD("MasterCard"),	
	// AMERICAN_EXPRESS("American Express"),
	VISA("Visa"),
	DISCOVER("Discover");
	
	public final String value;
	
    private CardType(String value) {
        this.value = value;
    }
    
    public CardType getType(String value) {
    	if (value.equals("VISA")) {
    		return CardType.VISA;
    	}
    	else if (value.equals("DISCOVER")) {
    		return CardType.DISCOVER;
    	}
//    	else if (value.equals("MASTER_CARD")) {
//    		return CardType.MASTER_CARD;
//    	}
//    	else if (value.equals("AMERICAN_EXPRESS")) {
//    		return CardType.AMERICAN_EXPRESS;
//    	}
    	else return null;
    }
    
    
}
