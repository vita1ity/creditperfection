package models;

import java.time.YearMonth;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

import play.data.format.Formats;

@Entity
public class CreditCard extends Model {
	
    @Id
    public int id;
    
    public String name;
    
    public CardType cardType;
    
    public String digits;
    
    @Formats.DateTime(pattern="MM/yyyy")
    public YearMonth expDate;
    
    public int cvv;
    
    @ManyToOne(cascade = CascadeType.ALL)
    public User user;

	public CreditCard(String name, CardType cardType, String digits, YearMonth expDate, int cvv, User user) {
		super();
		
		this.name = name;
		this.cardType = cardType;
		this.digits = digits;
		this.expDate = expDate;
		this.cvv = cvv;
		this.user = user;
	}

	@Override
	public String toString() {
		return "CreditCard [id=" + id + ", name=" + name + ", cardType=" + cardType + ", digits=" + digits
				+ ", expDate=" + expDate + ", cvv=" + cvv + "]";
	}
    
    
    
}
