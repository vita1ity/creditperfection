package repository;

import java.util.List;

import com.google.inject.ImplementedBy;

import models.Transaction;
import repository.impl.TransactionRepositoryImpl;

@ImplementedBy(TransactionRepositoryImpl.class)
public interface TransactionRepository {

	List<Transaction> findAll();

	Transaction findById(long id);

	void save(Transaction transaction);

	void update(Transaction transaction);

	boolean delete(Transaction transaction);

}
