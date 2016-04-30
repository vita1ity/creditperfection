package models.json;

public class CreditReportSuccessResponse extends JSONResponse {

	private String reportUrl;
	
	public CreditReportSuccessResponse(String status, String reportUrl) {
		super(status);
		
		this.reportUrl = reportUrl;
	}

	public String getReportUrl() {
		return reportUrl;
	}

	public void setReportUrl(String reportUrl) {
		this.reportUrl = reportUrl;
	}
	

}
