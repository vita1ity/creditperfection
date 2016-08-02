package repository.impl;

import java.util.List;

import javax.inject.Singleton;

import com.avaje.ebean.Model.Finder;

import models.Subscription;
import models.User;
import models.enums.SubscriptionStatus;
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
	public List<Subscription> findByStatus(SubscriptionStatus status) {
		List<Subscription> subscriptionList = find.where().eq("status", status).findList();
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
	
}
