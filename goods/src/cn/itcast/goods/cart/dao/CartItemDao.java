package cn.itcast.goods.cart.dao;

import static org.hamcrest.CoreMatchers.nullValue;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.cart.domain.CartItem;
import cn.itcast.goods.user.domain.User;
import cn.itcast.jdbc.TxQueryRunner;

public class CartItemDao {
	
	QueryRunner qr = new TxQueryRunner();
	
	/*
	 * 按CartItemId查询
	 */
	public CartItem findByCartItemId(String cartItemId) throws SQLException {
		String sql = "select * from t_cartitem c,t_book b where c.bid = b.bid and c.cartItemId = ?";
		Map<String, Object> map = qr.query(sql, new MapHandler(),cartItemId);
		return toCartItem(map);
	}
	
	/*
	 * 把一个map映射成CartItem
	 */
	private CartItem toCartItem(Map<String,Object> map) {
		if(map == null || map.size() == 0) return null;
		CartItem cartItem = CommonUtils.toBean(map, CartItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		User user = CommonUtils.toBean(map, User.class);
		cartItem.setBook(book);
		cartItem.setUser(user);
		return cartItem;
	}
	/*
	 * 把多个map映射成多个cartItem
	 */
	private List<CartItem> toCartItemList(List<Map<String, Object>> mapList) {
		List<CartItem> cartItems = new ArrayList<CartItem>();
		for (Map<String, Object> map : mapList) {
			CartItem cartItem = toCartItem(map);
			cartItems.add(cartItem);
		}
		return cartItems;
		
	}
	/**
	 * 通过uid查询购物车条目
	 * @throws SQLException 
	 */
	public List<CartItem> findByUser(String uid) throws SQLException{
		String sql = "select * from t_cartitem c,t_book b where c.bid = b.bid and uid = ? order by c.orderBy";
		List<Map<String, Object>> mapList = qr.query(sql, new MapListHandler(),uid);
		List<CartItem> cartItemList = toCartItemList(mapList);
		return cartItemList;
		
	}
	/*
	 * 查询购物车的某本图书的购物车条目是否存在，目的是如果存在的话，在此基础上重新计算该书的数量
	 */
	public CartItem findByUidAndBid(String uid,String bid) throws SQLException {
		String sql = "select * from t_cartitem where uid = ? and bid = ?";
		Map<String, Object> map = qr.query(sql, new MapHandler(),uid,bid);
		return toCartItem(map);
	}
	/*
	 * 修改某本图书的数量
	 */
	public void updateQuantity(String cartItemId,int quantity) throws SQLException {
		String sql = "update t_cartitem set quantity = ? where cartItemId = ?";
		qr.update(sql,quantity,cartItemId);
	}
	
	/*
	 * 添加购物条目
	 */
	public void addCartItem(CartItem cartItem) throws SQLException {
		String sql = "insert into t_cartitem(cartItemId,quantity,bid,uid)"
				+ " values(?,?,?,?)";
		Object[] params = {cartItem.getCartItemId(),cartItem.getQuantity(),cartItem.getBook().getBid(),cartItem.getUser().getUid()};
		qr.update(sql,params);
	}
	//拼接？号
	private String toWhereSql(int len) {
		StringBuilder sb = new StringBuilder();
		sb.append(" cartItemId in(");
		for (int i = 0; i < len ; i++) {
			sb.append("?");
			if(i < len-1) {
				sb.append(",");
			}
		}
		sb.append(")");
		return sb.toString();
	}
	/*
	 * 返回被选中的购物条目
	 */
	public List<CartItem> loadCartItems(String cartItemIds) throws SQLException{
		Object[] cartItemIdArray = cartItemIds.split(",");
		String whereSql = toWhereSql(cartItemIdArray.length);
		String sql = "select * from t_cartitem c,t_book b where c.bid = b.bid and "+whereSql;
		List<Map<String,Object>> list = qr.query(sql, new MapListHandler(),cartItemIdArray);
		List<CartItem> itemList = toCartItemList(list);
		return itemList;
	}
	/*
	 * 删除购物车条目
	 */
	public void batchDelete(String cartIteIds) throws SQLException {
		Object[] cartItemIdArray = cartIteIds.split(",");
		String whereSql = toWhereSql(cartItemIdArray.length);
		String sql = "delete from t_cartitem where "+whereSql;
		qr.update(sql,cartItemIdArray);
	}
}
