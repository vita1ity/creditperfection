package models;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.avaje.ebean.Model;

import models.enums.DiscountStatus;
import models.enums.DiscountType;

@Entity
public class Discount extends Model {
	
	@Id
    private long id;
		  
    @OneToOne
    @JoinColumn(nullable = false)
    private Subscription subscription;
  
    private DiscountType discountType;
  
    private double discountAmount;
  
    @Column(nullable = false)
    private LocalDate startDate;
  
    @Column(nullable = false)
    private LocalDate endDate;
  
    private DiscountStatus discountStatus;

    public Discount() {
    	super();
    }
    
	public Discount(Subscription subscription, DiscountType discountType, double discountAmount, LocalDate startDate,
			LocalDate endDate, DiscountStatus discountStatus) {
		super();
		this.subscription = subscription;
		this.discountType = discountType;
		this.discountAmount = discountAmount;
		this.startDate = startDate;
		this.endDate = endDate;
		this.discountStatus = discountStatus;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public DiscountType getDiscountType() {
		return discountType;
	}

	public void setDiscountType(DiscountType discountType) {
		this.discountType = discountType;
	}

	public double getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(double discountAmount) {
		this.discountAmount = discountAmount;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public DiscountStatus getDiscountStatus() {
		return discountStatus;
	}

	public void setDiscountStatus(DiscountStatus discountStatus) {
		this.discountStatus = discountStatus;
	}

	@Override
	public String toString() {
		return "Discount [id=" + id + ", subscription=" + getSubscription().getId() + ", discountType=" + discountType
				+ ", discountAmount=" + discountAmount + ", startDate=" + startDate + ", endDate=" + endDate
				+ ", discountStatus=" + discountStatus + "]";
	}
	
		
}
