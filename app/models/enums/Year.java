package models.enums;

public enum Year {
	
	SIXTEEN(2016),
	SEVENTEEN(2017),
	EIGHTEEN(2018),
	NINETEEN(2019),
	TWENTY(2020),
	TWENTYONE(2021),
	TWENTYTWO(2022),
	TWENTYTHREE(2023),
	TWENTYFOUR(2024),
	TWENTYFIVE(2025),
	TWENTYSIX(2026),
	TWENTYSEVEN(2027),
	TWENTYEIGHT(2028),
	TWENTYNINE(2029),
	THIRTY(2030);
	
	private int value;
	
	private Year(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}
	
}
