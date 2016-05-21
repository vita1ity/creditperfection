package models.enums;

public enum Month {
	
	JAN(1, "01-Jan"),
	FEB(2, "02-Feb"),
	MAR(3, "03-Mar"),
	APR(4, "04-Apr"),
	MAY(5, "05-May"),
	JUN(6, "06-Jun"),
	JUL(7, "07-Jul"),
	AUG(8, "08-Aug"),
	SEP(9, "09-Sep"),
	OCT(10, "10-Oct"),
	NOV(11, "11-Nov"),
	DEC(12, "12-Dec");
	
	private final int num;	
	private final String name;
	
	private Month(int num, String name) {
		this.num = num;
		this.name = name;
	}

	public int getNum() {
		return num;
	}

	public String getName() {
		return name;
	}
	
	
}
