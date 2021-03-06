package repository.impl;

import java.time.LocalDate;
import java.util.List;

import javax.inject.Singleton;

import com.avaje.ebean.Expr;
import com.avaje.ebean.Model.Finder;
import com.avaje.ebean.PagedList;

import models.Subscription;
import models.User;
import models.enums.SubscriptionStatus;
import play.Logger;
import repository.SubscriptionRepository;

@Singleton
public class SubscriptionRepositoryImpl implements SubscriptionRepository {

	private Finder<Long, Subscription> find = new Finder<Long, Subscription>(Subscription.class);

	@Override
	public Subscription findByUser(User user) {
		Subscription subscription = find.where().eq("user", user).findUnique();
		return subscription;
	}
	
	@Override
	public PagedList<Subscription> findByStatus(SubscriptionStatus status, int page, int pageSize) {
		PagedList<Subscription> subscriptionList = find.where().eq("status", status).findPagedList(page, pageSize);
		return subscriptionList;
	}
	
	@Override
	public List<Subscription> findExcludingStatus(SubscriptionStatus status) {
		List<Subscription> subscriptionList = find.where().ne("status", status).findList();
		return subscriptionList;
	}

	@Override
	public Subscription findById(long id) {
		Subscription subscription = find.byId(id);
		return subscription;
	}
	
	@Override
	public List<Subscription> findAll() {
		List<Subscription> allSubscriptions = find.all();
		return allSubscriptions;
	}

	@Override
	public void save(Subscription subscription) {
		subscription.save();
		
	}

	@Override
	public void update(Subscription subscription) {
		subscription.update();
		
	}

	@Override
	public void delete(Subscription subscription) {
		subscription.delete();
		
	}

	@Override
	public PagedList<Subscription> getSubscriptionsPage(int page, int pageSize) {	
		PagedList<Subscription> pagedList = find.findPagedList(page, pageSize);
		return pagedList;
	}

	@Override
	public List<Subscription> findFailedToRenew(LocalDate startDate, LocalDate endDate) {
		
		List<Subscription> subscriptionList = find.where()
				.and(Expr.between("renewFailedDate", startDate, endDate), Expr.eq("status", SubscriptionStatus.CANCELLED)).findList();
		
		Logger.info("Subscriptions Failed to renew: " + subscriptionList.size());
		for (Subscription s: subscriptionList) {
			Logger.info("Subsciption failed to renew: " + s);
		}
		
		return subscriptionList;
	}
	
}
