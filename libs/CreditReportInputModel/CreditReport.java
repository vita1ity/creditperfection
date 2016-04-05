package models.CreditReportInputModel;

public abstract class CreditReport {
	
	private AccountHistory accountHistory;
	private CreditScore creditScore;
	private CreditSummary creditSummary;
	private PersonalInformation personalInfo;
	
	public AccountHistory getAccountHistory() {
		return AccountHistory;
	}

	public CreditScore getCreditScore() {
		return this.CreditScore;
	}
	
	public CreditSummary getCreditSummary() {
		return this.CreditSummary;
	}

	public PersonalInformation getPersonalInformation() {
		return this.PersonalInformation;
	}
	
	public String parseCreditReport (String string) {
		return new String();
	}
	
}
