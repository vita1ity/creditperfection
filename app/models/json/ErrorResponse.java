package models.json;

public class ErrorResponse extends JSONResponse {

	private String errorCode;
	
	private String errorMessage;
	
	public ErrorResponse(String status, String errorCode, String errorMessage) {
		super(status);
		
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public String toString() {
		return "ErrorResponse [errorCode=" + errorCode + ", errorMessage=" + errorMessage + ", getStatus()="
				+ getStatus() + "]";
	}
	
	
	
}
