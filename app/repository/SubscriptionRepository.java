package repository;

import java.util.List;

import com.google.inject.ImplementedBy;

import models.Subscription;
import models.User;
import models.enums.SubscriptionStatus;
import repository.impl.SubscriptionRepositoryImpl;

@ImplementedBy(SubscriptionRepositoryImpl.class)
public interface SubscriptionRepository {

	Subscription findByUser(User user);

	List<Subscription> findByStatus(SubscriptionStatus status);

	List<Subscription> findExcludingStatus(SubscriptionStatus status);

	Subscription findById(long id);

	List<Subscription> findAll();
	
	void save(Subscription subscription);

	void update(Subscription subscription);

	void delete(Subscription subscription);
	
}
