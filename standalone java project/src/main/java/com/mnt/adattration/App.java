package com.mnt.adattration;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.authorize.Environment;
import net.authorize.api.contract.v1.CreateTransactionRequest;
import net.authorize.api.contract.v1.CreateTransactionResponse;
import net.authorize.api.contract.v1.CustomerProfilePaymentType;
import net.authorize.api.contract.v1.MerchantAuthenticationType;
import net.authorize.api.contract.v1.MessageTypeEnum;
import net.authorize.api.contract.v1.PaymentProfile;
import net.authorize.api.contract.v1.TransactionRequestType;
import net.authorize.api.contract.v1.TransactionResponse;
import net.authorize.api.contract.v1.TransactionTypeEnum;
import net.authorize.api.controller.CreateTransactionController;
import net.authorize.api.controller.base.ApiOperationBase;

import com.mnt.adattration.data.Auth;
import com.mnt.adattration.data.Customer;

 
public class App 
{
    private static List<Auth> staticData = new ArrayList<Auth>();
	static {
		// We can add more merchants here. Right now testing with one only.
    	Auth a1 = new Auth("6Zg35L5nxCN","9P2S29j435EJpuvP");
    	
    	// We assume that merchant will already have customer and its payment profile added. 
    	// If not goto http://developer.authorize.net/api/reference/index.html#payment-transactions
    	// First add the customer and get the response , in response you will get customer ID and Payment ID
    	a1.addCustomer(new Customer("40402407").addPaymentId("36670312"));
    	a1.addCustomer(new Customer("40402414").addPaymentId("36670318"));
    	a1.addCustomer(new Customer("40402416").addPaymentId("36670320"));
    	a1.addCustomer(new Customer("40402418").addPaymentId("36670321"));
    	a1.addCustomer(new Customer("40402422").addPaymentId("36670325"));
    	staticData.add(a1);
    }
	
	public static void main( String[] args )
    {
		Random rand = new Random();
		
		// Randomly picking merchants
		for (int i = 0 ; i < 20; i++) {
			int index = rand.nextInt(staticData.size());
			makeRandomPayment(staticData.get(index));
		}
    }
	
	private static void makeRandomPayment(Auth _auth){
		 
		 // Picking up random Customer
		 Random rand = new Random();
	     int index = rand.nextInt(_auth.customers.size());
	     
	     CreateTransactionResponse response = chargeToCustomer(_auth, _auth.customers.get(index), 2.0 * rand.nextInt(40) );

        if (response!=null) {
            // If API Response is ok, go ahead and check the transaction response
            if (response.getMessages().getResultCode() == MessageTypeEnum.OK) {

                TransactionResponse result = response.getTransactionResponse();
                if (result.getResponseCode().equals("1")) {
                    System.out.println(result.getResponseCode());
                    System.out.println("Successful Transaction");
                    System.out.println(result.getAuthCode());
                    System.out.println(result.getTransId());
                }
                else
                {
                    System.out.println("Failed Transaction"+result.getResponseCode());
                }
            }
            else
            {
                System.out.println("Failed Transaction:  "+response.getMessages().getResultCode());
                try {
                	if( response.getTransactionResponse().getErrors() != null) {
                		System.out.println("Failed Reason: " + response.getTransactionResponse().getErrors().getError().get(0).getErrorText());
                	} 
                	System.out.println("Failed Reason: " + response.getMessages().getMessage().get(0).getText());
                } catch (Exception e){}
            }
        }
		
	}

	private static CreateTransactionResponse chargeToCustomer(Auth _auth, Customer _customer, double amount) {
		//Common code to set for all requests
		 ApiOperationBase.setEnvironment(Environment.SANDBOX);

	     MerchantAuthenticationType merchantAuthenticationType  = new MerchantAuthenticationType() ;
	     merchantAuthenticationType.setName(_auth.name);
	     merchantAuthenticationType.setTransactionKey(_auth.key);
	     
	     ApiOperationBase.setMerchantAuthentication(merchantAuthenticationType);
	     System.out.println("Making Transaction for " + _customer.customerId + " for payment Id " + _customer.paymentIds.get(0) + " with amount " + amount);
	     // Set the profile ID to charge
	     CustomerProfilePaymentType profileToCharge = new CustomerProfilePaymentType();
	     profileToCharge.setCustomerProfileId(_customer.customerId);
	     PaymentProfile paymentProfile = new PaymentProfile();
	     paymentProfile.setPaymentProfileId(_customer.paymentIds.get(0));
	     profileToCharge.setPaymentProfile(paymentProfile);

	     // Create the payment transaction request
        TransactionRequestType txnRequest = new TransactionRequestType();
        txnRequest.setTransactionType(TransactionTypeEnum.AUTH_CAPTURE_TRANSACTION.value());
        txnRequest.setProfile(profileToCharge);
        txnRequest.setAmount(new BigDecimal(amount).setScale(2, RoundingMode.CEILING));
        
        CreateTransactionRequest apiRequest = new CreateTransactionRequest();
        apiRequest.setTransactionRequest(txnRequest);
        CreateTransactionController controller = new CreateTransactionController(apiRequest);
        controller.execute();
        CreateTransactionResponse response = controller.getApiResponse();
		return response;
	}
}
