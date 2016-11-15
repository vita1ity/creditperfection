package models.json;

public class PagedObjectResponse extends JSONResponse {
	
	private Object object;
	
	private int currentPage = 1;
	
	private int totalPageCount;
	
	

	public PagedObjectResponse(String status, Object object, int currentPage, int totalPageCount) {
		super(status);
		this.object = object;
		this.currentPage = currentPage;
		this.totalPageCount = totalPageCount;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getTotalPageCount() {
		return totalPageCount;
	}

	public void setTotalPageCount(int totalPageCount) {
		this.totalPageCount = totalPageCount;
	}
	
	
	
}
