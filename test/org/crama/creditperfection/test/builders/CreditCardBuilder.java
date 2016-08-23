package org.crama.creditperfection.test.builders;

import java.time.YearMonth;

import forms.CreditCardForm;
import models.CreditCard;
import models.enums.CardType;

public class CreditCardBuilder {

	private long id = 0l;
	
	private String name = "Card Owner";
	
	private CardType cardType = CardType.VISA;
	
	private String digits = "1111 1111 1111 1111";
	
	private YearMonth expDate = YearMonth.of(2016, 1);
	
	private int cvv = 111;
	

	public CreditCardBuilder id(long id) {
		this.id = id;
		return this;
	}
	
	public CreditCardBuilder name(String name) {
		this.name = name;
		return this;
	}
	
	public CreditCardBuilder cardType(CardType cardType) {
		this.cardType = cardType;
		return this;
	}
	
	public CreditCardBuilder digits(String digits) {
		this.digits = digits;
		return this;
	}
	
	public CreditCardBuilder expDate(YearMonth expDate) {
		this.expDate = expDate;
		return this;
	}
	
	public CreditCardBuilder cvv(int cvv) {
		this.cvv = cvv;
		return this;
	}
	
	public CreditCard build() {
		
		CreditCard creditCard = new CreditCard();
		
		creditCard.setId(id);
		creditCard.setName(name);
		creditCard.setDigits(digits);
		creditCard.setCardType(cardType);
		creditCard.setExpDate(expDate);
		creditCard.setCvv(cvv);
		
		return creditCard;
		
	}
	
	public CreditCardForm buildForm() {
		
		CreditCardForm creditCardForm = new CreditCardForm();
		
		creditCardForm.setId(Long.toString(id));
		creditCardForm.setName(name);
		creditCardForm.setDigits(digits);
		if (cardType == null) {
			creditCardForm.setCardType("");
		}
		else {
			creditCardForm.setCardType(cardType.toString());
		}
		if (expDate == null) {
			creditCardForm.setMonth("");
			creditCardForm.setYear("");
		}
		else {
			creditCardForm.setMonth(Integer.toString(expDate.getMonth().getValue()));
			creditCardForm.setYear(Integer.toString(expDate.getYear()));
		}
		if (cvv == 0) {
			creditCardForm.setCvv("");
		}
		else {
			creditCardForm.setCvv(Integer.toString(cvv));
		}
		
		return creditCardForm;
		
	}
	
}
