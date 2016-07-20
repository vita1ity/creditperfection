package models.json;

public abstract class JSONResponse {
	
	private String status;
	
	public JSONResponse(String status) {
		this.status = status;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "JSONResponse [status=" + status + "]";
	}
	
	
	
}
