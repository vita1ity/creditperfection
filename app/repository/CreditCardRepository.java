package repository;

import java.util.List;

import com.google.inject.ImplementedBy;

import models.CreditCard;
import repository.impl.CreditCardRepositoryImpl;

@ImplementedBy(CreditCardRepositoryImpl.class)
public interface CreditCardRepository {

	List<CreditCard> getAll();

	CreditCard getById(long id);

}
