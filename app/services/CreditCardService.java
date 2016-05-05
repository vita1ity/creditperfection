package services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.inject.Singleton;

import models.CreditCard;
import models.json.ErrorResponse;
import models.json.JSONResponse;
import models.json.TransactionSuccessResponse;
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
import net.authorize.api.contract.v1.TransactionResponse.Messages.Message;
import net.authorize.api.contract.v1.TransactionTypeEnum;
import net.authorize.api.controller.CreateTransactionController;
import net.authorize.api.controller.base.ApiOperationBase;


@Singleton
public class CreditCardService {
	
	public ANetApiResponse charge(String apiLoginId, String transactionKey, Double amount, CreditCard userCreditCard) {


        //Common code to set for all requests
        //ApiOperationBase.setEnvironment(Environment.SANDBOX);
		ApiOperationBase.setEnvironment(Environment.PRODUCTION);

        MerchantAuthenticationType merchantAuthenticationType  = new MerchantAuthenticationType() ;
        merchantAuthenticationType.setName(apiLoginId);
        merchantAuthenticationType.setTransactionKey(transactionKey);
        ApiOperationBase.setMerchantAuthentication(merchantAuthenticationType);

        // Populate the payment data
        PaymentType paymentType = new PaymentType();
        CreditCardType creditCard = new CreditCardType();
        creditCard.setCardNumber(userCreditCard.digits);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
        String expDateStr = userCreditCard.expDate.format(formatter);
        creditCard.setExpirationDate(expDateStr);
        creditCard.setCardCode(Integer.toString(userCreditCard.cvv));
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
            
		return response;

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
                    
                    jsonResponse = new TransactionSuccessResponse("SUCCESS", "Successful Credit Card Transaction");
                    
                    return jsonResponse;
                }
                else {
                    System.out.println("Failed Transaction" + result.getResponseCode());
                    
                    List<Message> messages = result.getMessages().getMessage();
                    StringBuilder errorMessages = new StringBuilder();
                    for (Message m: messages) {
                    	System.out.println("Message: " + m.getDescription());
                    	errorMessages.append(m.getDescription() + "\n");
                    }
                    
                    System.out.println("Error messages:");
                    System.out.println(errorMessages);
                    
                    
                    jsonResponse = new ErrorResponse("ERROR", "201", errorMessages.toString());
                    
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
	
}
