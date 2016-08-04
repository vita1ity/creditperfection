package repository.impl;

import java.util.List;

import javax.inject.Singleton;

import com.avaje.ebean.Model.Finder;

import models.CreditCard;
import repository.CreditCardRepository;

@Singleton
public class CreditCardRepositoryImpl implements CreditCardRepository {

	private Finder<Long, CreditCard> find = new Finder<Long, CreditCard>(CreditCard.class);

	@Override
	public List<CreditCard> getAll() {
		List<CreditCard> creditCardList = find.all();
		return creditCardList;
	}

	@Override
	public CreditCard getById(long id) {
		CreditCard creditCard = find.byId(id);
		return creditCard;
	}

	@Override
	public void save(CreditCard creditCard) {
		creditCard.save();
		
	}

	@Override
	public void update(CreditCard creditCardDB) {
		creditCardDB.updateCreditCardInfo(creditCardDB);
    	creditCardDB.save();
		
	}

	@Override
	public boolean delete(CreditCard creditCard) {
		return creditCard.delete();
	}
	
	
	
}
