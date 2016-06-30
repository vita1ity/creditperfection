package repository.impl;

import java.util.List;

import javax.inject.Singleton;

import com.avaje.ebean.Model.Finder;

import models.Transaction;
import repository.TransactionRepository;

@Singleton
public class TransactionRepositoryImpl implements TransactionRepository { 

	private Finder<Long, Transaction> find = new Finder<Long, Transaction>(Transaction.class);

	@Override
	public List<Transaction> findAll() {
		List<Transaction> allTransactions = find.all();
		return allTransactions;
	}

	@Override
	public Transaction findById(long id) {
		Transaction transaction = find.byId(id);
		return transaction;
	}
	
}
