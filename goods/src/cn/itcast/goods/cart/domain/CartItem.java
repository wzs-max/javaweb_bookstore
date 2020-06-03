package cn.itcast.goods.cart.domain;

import java.math.BigDecimal;

import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.user.domain.User;

public class CartItem {

	private String cartItemId;//主键
	private int quantity;//数量
	private Book book;//条目对应的图书，表中的bid
	private User user;//所属用户 
	
	//添加书本总价格,由于计算机对浮点数运算不准确，所以引入BigDecimail
	public double getSubtotal() {
		BigDecimal b1 = new BigDecimal(book.getCurrPrice()+"");
		BigDecimal b2 = new BigDecimal(quantity+"");
		BigDecimal b3 = b1.multiply(b2);
		return b3.doubleValue();
	}
	public String getCartItemId() {
		return cartItemId;
	}
	public void setCartItemId(String cartItemId) {
		this.cartItemId = cartItemId;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public Book getBook() {
		return book;
	}
	public void setBook(Book book) {
		this.book = book;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	@Override
	public String toString() {
		return "CartItem [cartItemId=" + cartItemId + ", quantity=" + quantity + ", book=" + book + ", user=" + user
				+ "]";
	}
	
	
}
