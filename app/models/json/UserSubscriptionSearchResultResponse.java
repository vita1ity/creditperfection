package models.json;

import java.util.List;

import models.Subscription;
import models.User;

public class UserSubscriptionSearchResultResponse extends JSONResponse {

	private List<User> users;
	
	private List<Subscription> subscriptions;
	
	private int currentPage = 1;
	
	private int totalPageCount;
	
	
	public UserSubscriptionSearchResultResponse(String status, List<User> users, List<Subscription> subscriptions, int currentPage, int totalPageCount) {
		super(status);
		this.users = users;
		this.subscriptions = subscriptions;
		this.currentPage = currentPage;
		this.totalPageCount = totalPageCount;
	}

	
	public List<User> getUsers() {
		return users;
	}

	public void setUsers(List<User> users) {
		this.users = users;
	}

	public List<Subscription> getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(List<Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getTotalPageCount() {
		return totalPageCount;
	}

	public void setTotalPageCount(int totalPageCount) {
		this.totalPageCount = totalPageCount;
	}
	
}
