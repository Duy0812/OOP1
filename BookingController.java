package controller;

import model.entity.Booking;
import model.entity.Customer;
import model.entity.Invoice;
import java.time.LocalDate;
import java.util.List;

public class BookingController {
	private BookingManager bookingManager;

	// Tiêm (Inject) BookingManager vào để gọi Timer
	public BookingController(BookingManager bookingManager) {
		this.bookingManager = bookingManager;
	}

	public Invoice processBooking(Customer customer, String hallId, double hallPrice, List<Booking> bookingDB) {
		System.out.println("\n[HỆ THỐNG] Kiểm tra sảnh " + hallId + " hợp lệ và đang trống!");
		double depositAmount = hallPrice * 0.3;

		// Khởi tạo đối tượng Booking thực thụ và lưu vào DB
		String newBookingId = "BK_" + System.currentTimeMillis();
		Booking newBooking = new Booking(newBookingId, customer, hallId, LocalDate.now(), "Tối", hallPrice);
		bookingDB.add(newBooking);

		System.out.println("--- XÁC NHẬN ĐƠN ---");
		System.out.println("Tổng chi phí: " + String.format("%,.0f", hallPrice) + " VND");
		System.out.println(">> Số tiền CỌC (30%): " + String.format("%,.0f", depositAmount) + " VND");

		// Khởi động luồng đếm ngược tự động hủy 24h từ BookingManager
		bookingManager.setupTimeoutCancel(newBooking);

		return new Invoice("INV_" + System.currentTimeMillis(), newBookingId, depositAmount);
	}
}
