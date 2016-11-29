package models.enums;

public enum UserStateFilter {
	
	BOTH(0),
	ONLY_ACTIVE(1),
	ONLY_INACTIVE(2);	
	
	private int value;
	
	private UserStateFilter(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
}