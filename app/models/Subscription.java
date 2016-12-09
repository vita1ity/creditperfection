package models;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import forms.SubscriptionForm;
import models.enums.SubscriptionStatus;

@Entity
public class Subscription extends Model {
	
	@Id
	private long id;
	
	@OneToOne
	@JoinColumn(nullable = false)
	private User user;
	
	@ManyToOne
    @JoinColumn(nullable = false)
	private CreditCard creditCard;
	
	@ManyToOne
	@JoinColumn(nullable = false)
	private Product product;
	
	@Column(nullable = false)
	private SubscriptionStatus status;
	
	@Column(nullable = false)
	private LocalDateTime subscriptionDate;
	
	@Column(nullable = false)
	private LocalDateTime lastChargeDate;
	
	@JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "subscription")
    private Discount discount;
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public SubscriptionStatus getStatus() {
		return status;
	}

	public void setStatus(SubscriptionStatus status) {
		this.status = status;
	}

	public LocalDateTime getSubscriptionDate() {
		return subscriptionDate;
	}

	public void setSubscriptionDate(LocalDateTime subscriptionDate) {
		this.subscriptionDate = subscriptionDate;
	}

	public LocalDateTime getLastChargeDate() {
		return lastChargeDate;
	}

	public void setLastChargeDate(LocalDateTime lastChargeDate) {
		this.lastChargeDate = lastChargeDate;
	}

	
	public Discount getDiscount() {
		return discount;
	}

	public void setDiscount(Discount discount) {
		this.discount = discount;
	}

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

	public Subscription(long id, SubscriptionStatus status, LocalDateTime subscriptionDate,
			LocalDateTime lastChargeDate) {
		super();
		this.id = id;
		this.status = status;
		this.subscriptionDate = subscriptionDate;
		this.lastChargeDate = lastChargeDate;
	}

	@Override
	public String toString() {
		return "Subscription [id=" + id + ", status=" + status + ", subscriptionDate=" + subscriptionDate
				+ ", lastChargeDate=" + lastChargeDate + "]";
	}
	
	
	
	
	
}
