package data.netauthorize;

import java.util.ArrayList;
import java.util.List;

public class Auth {

	public String name;
	
	public String key;
	
	public Auth(String name, String key) {
		this.name = name;
		this.key = key;
	}
	
	// One Auth user can have many customer
	public List<Customer> customers;
	
	public void addCustomer(Customer customer) {
		if (customers == null) {
			customers = new ArrayList<Customer>();
		}
		customers.add(customer);
	}
}
