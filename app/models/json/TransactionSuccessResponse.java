package models.json;

public class TransactionSuccessResponse extends JSONResponse {
	
	private String message;
	
	public TransactionSuccessResponse(String status, String message) {
		super(status);
		
		this.message = message;
	}

	public String getReportUrl() {
		return message;
	}

	public void setReportUrl(String message) {
		this.message = message;
	}
}
