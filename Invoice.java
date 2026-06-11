package model.entity;

import pattern.observer.IObserver;
import pattern.observer.ISubject;
import java.util.ArrayList;
import java.util.List;

public class Invoice implements ISubject {
	private String invoiceId;
	private String bookingId;
	private double totalAmount;
	private boolean isPaid;
	private List<IObserver> observers = new ArrayList<>();

	public Invoice(String invoiceId, String bookingId, double totalAmount) {
		this.invoiceId = invoiceId;
		this.bookingId = bookingId;
		this.totalAmount = totalAmount;
		this.isPaid = false;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public boolean isPaid() {
		return isPaid;
	}

	public void setPaid(boolean paid) {
		this.isPaid = paid;
	}

	@Override
	public void attach(IObserver observer) {
		observers.add(observer);
	}

	@Override
	public void notifyObservers() {
		for (IObserver obs : observers) {
			obs.update("Hóa đơn " + invoiceId + " trị giá " + String.format("%,.0f", totalAmount)
					+ " VND đã thanh toán thành công!");
		}
	}
}
