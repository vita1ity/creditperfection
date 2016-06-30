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
		
		User user = userService.getById(Long.parseLong(transactionForm.userId));
		CreditCard creditCard = creditCardService.getById(Long.parseLong(transactionForm.cardId));
		Product product = productService.getById(Long.parseLong(transactionForm.productId));
		double amount = Double.parseDouble(transactionForm.amount);
		String transactionId = transactionForm.transactionId;
		
		TransactionStatus status = null;
		if (transactionForm.status != null) {
			status = transactionForm.status;
		}
		else {
			status = TransactionStatus.SUCCESSFUL;
		}
		
		Transaction transaction = new Transaction(user, creditCard, product, amount, transactionId, status);
		
		if (transactionForm.id != null){
			transaction.setId(Long.parseLong(transactionForm.id));
		}
		return transaction;
	}
	
	public List<Transaction> findAll() {
		return transactionRepository.findAll();
	}
	
	public Transaction findById(long id) {
		return transactionRepository.findById(id);
	}
}
