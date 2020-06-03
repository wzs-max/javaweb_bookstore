package cn.itcast.goods.cart.service;

import java.sql.SQLException;
import java.util.List;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.cart.dao.CartItemDao;
import cn.itcast.goods.cart.domain.CartItem;

public class CartItemService {

	CartItemDao cartItemDao = new CartItemDao();
	/*
	 * 更新图书数量
	 */
	public CartItem updateQuantity(String cartItemId,int quantity) {
		try {
			cartItemDao.updateQuantity(cartItemId, quantity);
			return cartItemDao.findByCartItemId(cartItemId);
		}catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/*
	 * 查询购物车
	 */
	public List<CartItem> myCart(String uid){
		try {
			return cartItemDao.findByUser(uid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	/*
	 * 添加条目
	 */
	public void add(CartItem cartItem) {
		//1.查询该条目是否存在
		try {
			CartItem cartItem2 = cartItemDao.findByUidAndBid(cartItem.getUser().getUid(), cartItem.getBook().getBid());
			if(cartItem2 == null) {//如果没有这个条目，添加
				cartItem.setCartItemId(CommonUtils.uuid());
				cartItemDao.addCartItem(cartItem);
			}else{//如果有这个条目，则修改数量
				int quantity = cartItem.getQuantity()+cartItem2.getQuantity();
				cartItemDao.updateQuantity(cartItem2.getCartItemId(), quantity);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
	/*
	 * 删除购物车条目
	 */
	public void batchDelete(String cartItemIds) {
		try {
			cartItemDao.batchDelete(cartItemIds);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
	/*
	 * 返回被选中的购物条目
	 */
	public List<CartItem> loadCartItems(String cartItemIds){
		try {
			return cartItemDao.loadCartItems(cartItemIds);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
}
