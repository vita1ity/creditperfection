package repository;

import java.util.List;

import com.avaje.ebean.PagedList;
import com.google.inject.ImplementedBy;

import models.Subscription;
import models.User;
import models.enums.SubscriptionStatus;
import repository.impl.SubscriptionRepositoryImpl;

@ImplementedBy(SubscriptionRepositoryImpl.class)
public interface SubscriptionRepository {

	Subscription findByUser(User user);

	List<Subscription> findExcludingStatus(SubscriptionStatus status);

	Subscription findById(long id);

	List<Subscription> findAll();
	
	void save(Subscription subscription);

	void update(Subscription subscription);

	void delete(Subscription subscription);

	PagedList<Subscription> getSubscriptionsPage(int page, int pageSize);

	PagedList<Subscription> findByStatus(SubscriptionStatus status, int page, int pageSize);
	
}
