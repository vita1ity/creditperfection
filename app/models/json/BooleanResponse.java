package models.json;

public class BooleanResponse extends JSONResponse {

	private boolean statement;
	
	public BooleanResponse(String status, boolean statement) {
		super(status);
		this.statement = statement;
	}

	public boolean isStatement() {
		return statement;
	}

	public void setStatement(boolean statement) {
		this.statement = statement;
	}

	
}
