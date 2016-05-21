package models.json;

import models.Transaction;

public class TransactionResponse extends JSONResponse {

	private String message;
	private Transaction transaction;

	public TransactionResponse(String status, String message, Transaction transaction) {
		super(status);
		this.message = message;
		this.transaction = transaction;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	public void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}
	
	
}
