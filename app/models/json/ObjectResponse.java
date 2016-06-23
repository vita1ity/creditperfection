package models.json;

public class ObjectResponse  extends JSONResponse {

	private String message;
	private Object object;

	public ObjectResponse(String status, String message, Object object) {
		super(status);
		this.message = message;
		this.object = object;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

}
