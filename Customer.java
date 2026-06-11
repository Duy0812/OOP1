package model.entity;

import pattern.observer.IObserver;

public class Customer implements IObserver {
	private String customerId;
	private String fullName;
	private String email;
	private String password;

	public Customer(String customerId, String fullName, String email, String password) {
		this.customerId = customerId;
		this.fullName = fullName;
		this.email = email;
		this.password = password;
	}

	@Override
	public void update(String message) {
		System.out.println("\n[🔔 TIN NHẮN ĐẾN " + fullName.toUpperCase() + "] " + message);
	}

	public String getCustomerId() {
		return customerId;
	}

	public String getFullName() {
		return fullName;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}
}
