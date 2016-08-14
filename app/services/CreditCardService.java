package services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import forms.CreditCardForm;
import models.AuthNetAccount;
import models.CreditCard;
import models.Transaction;
import models.enums.CardType;
import models.json.ErrorResponse;
import models.json.JSONResponse;
import models.json.MessageResponse;
import models.json.MultipleErrorResponse;
import net.authorize.Environment;
import net.authorize.api.contract.v1.ANetApiResponse;
import net.authorize.api.contract.v1.CreateTransactionRequest;
import net.authorize.api.contract.v1.CreateTransactionResponse;
import net.authorize.api.contract.v1.CreditCardType;
import net.authorize.api.contract.v1.MerchantAuthenticationType;
import net.authorize.api.contract.v1.MessageTypeEnum;
import net.authorize.api.contract.v1.PaymentType;
import net.authorize.api.contract.v1.TransactionRequestType;
import net.authorize.api.contract.v1.TransactionResponse;
import net.authorize.api.contract.v1.TransactionResponse.Errors.Error;
import net.authorize.api.contract.v1.TransactionTypeEnum;
import net.authorize.api.controller.CreateTransactionController;
import net.authorize.api.controller.base.ApiOperationBase;
import play.Configuration;
import play.Logger;
import repository.CreditCardRepository;


@Singleton
public class CreditCardService {
	
	@Inject
	private Configuration conf;
	
	@Inject
	private AuthNetAccountService authNetAccountService;
	
	@Inject 
	private CreditCardRepository creditCardRepository;
	
	public CreditCard createCreditCard(CreditCardForm creditCardForm) {
		
		CreditCard creditCard = new CreditCard();
		if (creditCardForm.getId() != null) {
			creditCard.setId(Long.parseLong(creditCardForm.getId()));
		}
		creditCard.setName(creditCardForm.getName());
		creditCard.setCardType(CardType.valueOf(creditCardForm.getCardType()));
		creditCard.setDigits(creditCardForm.getDigits());
		creditCard.setCvv(Integer.parseInt(creditCardForm.getCvv()));
		String expDateStr = creditCardForm.getMonth() + "/" + creditCardForm.getYear();
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
    	YearMonth expDate = YearMonth.parse(expDateStr, formatter);
    	creditCard.setExpDate(expDate); 
		
		return creditCard;
	}
	
	public List<CreditCard> getAll() {
		return creditCardRepository.getAll();
	}
	
	public CreditCard getById(long id) {
		return creditCardRepository.getById(id);
	}
	
	public AuthNetAccount chooseMerchantAccount() {
		List<AuthNetAccount> authNetAccounts = authNetAccountService.getAll();
		if (authNetAccounts.size() == 0) {
			
	    	String loginId = conf.getString("authorise.net.login.id");
		    String transactionKey = conf.getString("authorise.net.transaction.key");
		    
		    AuthNetAccount defaultAccount = new AuthNetAccount(loginId, transactionKey);
		    
			return defaultAccount;
		}
		else {
			Collections.sort(authNetAccounts);
			
			for (int i = 0; i < authNetAccounts.size(); i++) {
				
				AuthNetAccount account = authNetAccounts.get(i); 
				
				if (account.getIsLastUsed()) {
					//if last account was previously used return first account
					
					if (i == authNetAccounts.size() - 1) {
						AuthNetAccount firstAccount = authNetAccounts.get(0);
						account.setIsLastUsed(false);
						firstAccount.setIsLastUsed(true);
						authNetAccountService.update(account);
						authNetAccountService.update(firstAccount);
						return firstAccount;
					}
					else {
						AuthNetAccount nextAccount = authNetAccounts.get(i + 1);
						account.setIsLastUsed(false);
						nextAccount.setIsLastUsed(true);
						authNetAccountService.update(account);
						authNetAccountService.update(nextAccount);
						return nextAccount;
					}
					
				}
			}
			
			//last used account not found. use first
			AuthNetAccount firstAccount = authNetAccounts.get(0);
			firstAccount.setIsLastUsed(true);
			authNetAccountService.update(firstAccount);
			return firstAccount;
			
		}
	}
	
	public ANetApiResponse charge(Double amount, CreditCard userCreditCard) {

		Logger.info("Charging credit card: " + userCreditCard);
		
		AuthNetAccount account = chooseMerchantAccount();
		
		Logger.info("Account: " + account);
		
		try {
	        //Common code to set for all requests
	        //ApiOperationBase.setEnvironment(Environment.SANDBOX);
			ApiOperationBase.setEnvironment(Environment.PRODUCTION);
	
	        MerchantAuthenticationType merchantAuthenticationType  = new MerchantAuthenticationType() ;
	        merchantAuthenticationType.setName(account.getLoginId());
	        merchantAuthenticationType.setTransactionKey(account.getTransactionKey());
	        ApiOperationBase.setMerchantAuthentication(merchantAuthenticationType);
	
	        // Populate the payment data
	        PaymentType paymentType = new PaymentType();
	        CreditCardType creditCard = new CreditCardType();
	        creditCard.setCardNumber(userCreditCard.getDigits());
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
	        String expDateStr = userCreditCard.getExpDate().format(formatter);
	        creditCard.setExpirationDate(expDateStr);
	        creditCard.setCardCode(Integer.toString(userCreditCard.getCvv()));
	        paymentType.setCreditCard(creditCard);
	
	        // Create the payment transaction request
	        TransactionRequestType txnRequest = new TransactionRequestType();
	        
	        txnRequest.setTransactionType(TransactionTypeEnum.AUTH_CAPTURE_TRANSACTION.value());
	        txnRequest.setPayment(paymentType);
	        txnRequest.setAmount(new BigDecimal(amount).setScale(2, RoundingMode.CEILING));
	
	        // Make the API Request
	        CreateTransactionRequest apiRequest = new CreateTransactionRequest();
	        apiRequest.setTransactionRequest(txnRequest);
	        CreateTransactionController controller = new CreateTransactionController(apiRequest);
	        controller.execute();
	
	        CreateTransactionResponse response = controller.getApiResponse();
	        Logger.error(response.getMessages().getMessage().get(0).getCode());
	            
			return response;
		}
		catch (Exception e) {
			Logger.error(e.getMessage(), e);
		}
		return null;
		

    }
	
	public String getTransactionId(CreateTransactionResponse response) {
		TransactionResponse result = response.getTransactionResponse();
		return result.getTransId();
	}
	
	public JSONResponse checkTransaction(CreateTransactionResponse response) {
		
		JSONResponse jsonResponse = null;
		
		if (response != null) {

            // If API Response is ok, go ahead and check the transaction response
            if (response.getMessages().getResultCode() == MessageTypeEnum.OK) {

                TransactionResponse result = response.getTransactionResponse();
                if (result.getResponseCode().equals("1")) {
                    System.out.println(result.getResponseCode());
                    System.out.println("Successful Credit Card Transaction");
                    System.out.println(result.getAuthCode());
                    System.out.println(result.getTransId());
                    
                    jsonResponse = new MessageResponse("SUCCESS", "Successful Credit Card Transaction");
                    
                    return jsonResponse;
                }
                else {
                    System.out.println("Failed Transaction with Response Code: " + result.getResponseCode());
                    
                	List<Error> errors = result.getErrors().getError();
                	String errorCode = null;
                	String errorMessage = null;
                	List<models.json.Error> errs = new ArrayList<models.json.Error>();
                	for (Error err: errors) {
                		System.out.println(err.getErrorCode() + ", " + err.getErrorText());
                		errorCode = err.getErrorCode();
                		errorMessage = err.getErrorText();
                		errs.add(new models.json.Error(errorCode, errorMessage));
                		
                	}
                	jsonResponse = new MultipleErrorResponse("ERROR", errs);
                    
                    return jsonResponse;
                    
                }
            }
            else {
                 List<net.authorize.api.contract.v1.MessagesType.Message> messages = response.getMessages().getMessage();
                 StringBuilder errorMessages = new StringBuilder();
                 for (net.authorize.api.contract.v1.MessagesType.Message m: messages) {
                 	System.out.println("Message: " + m.getText());
                 	
                 	errorMessages.append(m.getText() + "\n");
                 }
                 System.out.println("Failed Transaction:  " + response.getMessages().getResultCode());
                 
                 System.out.println("Error messages:");
                 System.out.println(errorMessages);
                
                
                jsonResponse = new ErrorResponse("ERROR", "201", errorMessages.toString());
                
                return jsonResponse;
            }
        }
        else {
        	jsonResponse = new ErrorResponse("ERROR", "201", "Transaction Failed");
        	
        	return jsonResponse;
        }
		
	}
	
	
	public ANetApiResponse refundTransaction(Transaction transaction) {
        
		AuthNetAccount account = chooseMerchantAccount();
		
        //Common code to set for all requests
        ApiOperationBase.setEnvironment(Environment.PRODUCTION);

        MerchantAuthenticationType merchantAuthenticationType  = new MerchantAuthenticationType() ;
        merchantAuthenticationType.setName(account.getLoginId());
        merchantAuthenticationType.setTransactionKey(account.getTransactionKey());
        ApiOperationBase.setMerchantAuthentication(merchantAuthenticationType);

        // Create a payment object, last 4 of the credit card and expiration date are required
        PaymentType paymentType = new PaymentType();
        CreditCardType creditCard = new CreditCardType();
        creditCard.setCardNumber(transaction.getCreditCard().getDigits());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        creditCard.setExpirationDate(transaction.getCreditCard().getExpDate().format(formatter));
        paymentType.setCreditCard(creditCard);

        // Create the payment transaction request
        TransactionRequestType txnRequest = new TransactionRequestType();
        txnRequest.setTransactionType(TransactionTypeEnum.REFUND_TRANSACTION.value());
        txnRequest.setRefTransId(transaction.getTransactionId());
        txnRequest.setAmount(new BigDecimal(transaction.getAmount()));
        txnRequest.setPayment(paymentType);

        // Make the API Request
        CreateTransactionRequest apiRequest = new CreateTransactionRequest();
        apiRequest.setTransactionRequest(txnRequest);
        CreateTransactionController controller = new CreateTransactionController(apiRequest);
        controller.execute(); 

        CreateTransactionResponse response = controller.getApiResponse();

        return response;
        
    }

	public void save(CreditCard creditCard) {
		creditCardRepository.save(creditCard);
		
	}

	public void update(CreditCard creditCardDB) {
		creditCardRepository.update(creditCardDB);
		
	}

	public boolean delete(CreditCard creditCard) {
		return creditCardRepository.delete(creditCard);
	}

	
}
