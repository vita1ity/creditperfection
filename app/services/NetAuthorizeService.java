package services;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.inject.Singleton;

import net.authorize.Environment;
import net.authorize.api.contract.v1.CreateTransactionRequest;
import net.authorize.api.contract.v1.CreateTransactionResponse;
import net.authorize.api.contract.v1.CustomerProfilePaymentType;
import net.authorize.api.contract.v1.MerchantAuthenticationType;
import net.authorize.api.contract.v1.PaymentProfile;
import net.authorize.api.contract.v1.TransactionRequestType;
import net.authorize.api.contract.v1.TransactionTypeEnum;
import net.authorize.api.controller.CreateTransactionController;
import net.authorize.api.controller.base.ApiOperationBase;
import data.netauthorize.Auth;
import data.netauthorize.Customer;

public class NetAuthorizeService {

	@Singleton
	public  CreateTransactionResponse chargeToCustomer(Auth _auth, Customer _customer, double amount) {
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
