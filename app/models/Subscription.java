package models;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.avaje.ebean.Model;

import forms.SubscriptionForm;
import models.enums.SubscriptionStatus;

@Entity
public class Subscription extends Model {
	
	@Id
	public long id;
	
	@OneToOne
	@JoinColumn(nullable = false)
	public User user;
	
	@ManyToOne
    @JoinColumn(nullable = false)
    public CreditCard creditCard;
	
	@ManyToOne
	@JoinColumn(nullable = false)
	public Product product;
	
	@Column(nullable = false)
	public SubscriptionStatus status;
	
	@Column(nullable = false)
	public LocalDateTime subscriptionDate;
	
	@Column(nullable = false)
	public LocalDateTime lastChargeDate;
	
	public Subscription() {
		
	}
	
	public Subscription(User user, CreditCard creditCard, Product product, SubscriptionStatus status, 
			LocalDateTime subscriptionDate, LocalDateTime lastChargeDate) {
		super();
		this.user = user;
		this.creditCard = creditCard;
		this.product = product;
		this.status = status;
		this.subscriptionDate = subscriptionDate;
		this.lastChargeDate = lastChargeDate;
	}
	
	public static Finder<Long, Subscription> find = new Finder<Long, Subscription>(Subscription.class);

	public static Subscription findByUser(User user) {
		Subscription subscription = Subscription.find.where().eq("user", user).findUnique();
		return subscription;
	}
	
	public static List<Subscription> findByStatus(SubscriptionStatus status) {
		List<Subscription> subscriptionList = Subscription.find.where().eq("status", status).findList();
		return subscriptionList;
	}
	
	public static List<Subscription> findExcludingStatus(SubscriptionStatus status) {
		List<Subscription> subscriptionList = Subscription.find.where().ne("status", status).findList();
		return subscriptionList;
	}
	
	public static Subscription createSubscription(SubscriptionForm subscriptionForm) {

		User user = User.find.byId(Long.parseLong(subscriptionForm.userId));
		CreditCard creditCard = CreditCard.find.byId(Long.parseLong(subscriptionForm.cardId));
		Product product = Product.find.byId(Long.parseLong(subscriptionForm.productId));
		
		SubscriptionStatus status = null;
		if (subscriptionForm.status != null) {
			status = subscriptionForm.status;
		}
		else {
			status = SubscriptionStatus.TRIAL;
		}
		
		Subscription subscription = new Subscription(user, creditCard, product, status, LocalDateTime.now(), LocalDateTime.now());
		
		if (subscriptionForm.id != null){
			subscription.id = Long.parseLong(subscriptionForm.id);
			Subscription subFromDB = Subscription.find.byId(subscription.id);
			subscription.subscriptionDate = subFromDB.subscriptionDate;
			subscription.lastChargeDate = subFromDB.lastChargeDate;
		}
		return subscription;
		
	}
	
}
