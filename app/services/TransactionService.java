package services;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import forms.TransactionForm;
import models.CreditCard;
import models.Product;
import models.Transaction;
import models.User;
import models.enums.TransactionStatus;
import repository.TransactionRepository;

@Singleton
public class TransactionService {

	@Inject
	private TransactionRepository transactionRepository;
	
	@Inject
	private UserService userService;
	
	@Inject
	private CreditCardService creditCardService;
	
	@Inject
	private ProductService productService;
	
	public Transaction createTransaction(TransactionForm transactionForm) {
		
		User user = userService.getById(Long.parseLong(transactionForm.getUserId()));
		CreditCard creditCard = creditCardService.getById(Long.parseLong(transactionForm.getCardId()));
		Product product = productService.getById(Long.parseLong(transactionForm.getProductId()));
		double amount = Double.parseDouble(transactionForm.getAmount());
		String transactionId = transactionForm.getTransactionId();
		
		TransactionStatus status = null;
		if (transactionForm.getStatus() != null) {
			status = transactionForm.getStatus();
		}
		else {
			status = TransactionStatus.SUCCESSFUL;
		}
		
		Transaction transaction = new Transaction(user, creditCard, product, amount, transactionId, status);
		
		if (transactionForm.getId() != null){
			transaction.setId(Long.parseLong(transactionForm.getId()));
		}
		return transaction;
	}
	
	public List<Transaction> findAll() {
		return transactionRepository.findAll();
	}
	
	public Transaction findById(long id) {
		return transactionRepository.findById(id);
	}

	public void save(Transaction transaction) {
		transactionRepository.save(transaction);
		
	}

	public void update(Transaction transaction) {
		transactionRepository.update(transaction);		
	}

	public boolean delete(Transaction transaction) {
		return transactionRepository.delete(transaction);
		
	}
}
