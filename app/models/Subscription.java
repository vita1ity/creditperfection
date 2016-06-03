package models;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.avaje.ebean.Model;

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
	public Product product;
	
	@Column(nullable = false)
	public SubscriptionStatus status;
	
	@Column(nullable = false)
	public LocalDateTime subscriptionDate;
	
	public Subscription() {
		
	}
	
	public Subscription(User user, Product product, SubscriptionStatus status, LocalDateTime subscriptionDate) {
		super();
		this.user = user;
		this.product = product;
		this.status = status;
		this.subscriptionDate = subscriptionDate;
	}
	
	public static Finder<Long, Subscription> find = new Finder<Long, Subscription>(Subscription.class);
	
}
