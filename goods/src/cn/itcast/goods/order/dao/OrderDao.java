package cn.itcast.goods.order.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.order.domain.Order;
import cn.itcast.goods.order.domain.OrderItem;
import cn.itcast.goods.pager.Expression;
import cn.itcast.goods.pager.PageBean;
import cn.itcast.goods.pager.PageConstants;
import cn.itcast.jdbc.TxQueryRunner;

public class OrderDao {

	QueryRunner qr = new TxQueryRunner();
	
	/*
	 * 查询订单状态
	 */
	
	public int findStatus(String oid) throws SQLException {
		String sql = "select status from t_order where oid=?";
		Number number = (Number)qr.query(sql, new ScalarHandler(), oid);
		return number.intValue();
	}
	
	/**
	 * 修改订单状态
	 * @param oid
	 * @return
	 * @throws SQLException
	 */
	public void updateStatus(String oid,int status) throws SQLException {
		String sql = "update t_order set status = ? where oid = ?";
		qr.update(sql,status,oid);
	}
	/*
	 * 加载订单
	 */
	public Order load(String oid) throws SQLException {
		String sql = "select * from t_order where oid = ?";
		Order order = qr.query(sql, new BeanHandler<Order>(Order.class),oid);
		loadOrderList(order);
		return order;
	}
	/*
	 * 生成订单
	 */
	public void add(Order order) throws SQLException {
		//插入订单
		String sql = "insert into t_order values(?,?,?,?,?,?)";
		Object[] params = {
				order.getOid(),order.getOrdertime(),order.getTotal(),order.getStatus(),
				order.getAddress(),order.getOwner().getUid()
		};
		qr.update(sql,params);
		//批处理订单条目
		sql = "insert into t_orderitem values(?,?,?,?,?,?,?,?)";
		int len = order.getOrderItemList().size();
		Object[][] objs = new Object[len][];
		for(int i = 0;i < len;i++) {
			OrderItem item = order.getOrderItemList().get(i);
			objs[i] =  new Object[] {
					item.getOrderItemId(),item.getQuantity(),item.getSubtotal(),
					item.getBook().getBid(),item.getBook().getBname(),item.getBook().getCurrPrice(),
					item.getBook().getImage_b(),order.getOid()
			};
			
		}
		qr.batch(sql, objs);
	}
	
	/*
	 * 按用户查询订单
	 */
	public PageBean<Order> findByUser(String uid,int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<>();
		exprList.add(new Expression("uid","=",uid));
		return findByCriteria(exprList, pc);
	}
	/*
	 * 通用的查询方法【包含分页】
	 */
	public PageBean<Order> findByCriteria(List<Expression> exprList,int pc) throws SQLException{
		//设置页面大小
		 int ps = PageConstants.ORDER_PAGE_SIZE;
		 
		 StringBuilder whereSql =new  StringBuilder(" where 1 = 1 ");
		 java.util.List<Object> params = new ArrayList<Object>();
		 for(Expression expr:exprList) {
			 whereSql.append(" and ").append(expr.getName())
			 .append(" ").append(expr.getOperator()).append(" ");
			 if(!expr.getOperator().equals("is null")) {
				 whereSql.append("?");
				 params.add(expr.getValue());
			 }
		 }
		 //获得总记录数
		String sql = "select count(*) from t_order "+whereSql;
		Number number = (Number)qr.query(sql,new ScalarHandler(),params.toArray());
		int tr = number.intValue();
		//得到beanList
		sql = "select * from t_order "+whereSql + " order by ordertime desc limit ?,?";
		params.add((pc-1)*ps);//当前页首行下标
		params.add(ps);//每页大小
		List<Order> beanList = qr.query(sql, new BeanListHandler<Order>(Order.class),params.toArray());
		for(Order order:beanList) {
			loadOrderList(order);
		}
		PageBean<Order> pb = new PageBean<>();
		pb.setBeanList(beanList);//设置数据
		pb.setPc(pc);//设置当前页
		pb.setPs(ps);//设置页面大小，自定义页面大小
		pb.setTr(tr);//设置总记录数
		return pb;
		
	}
	/*
	 * 为指定的order加载所有的orderItem
	 */
	private void loadOrderList(Order order) throws SQLException {
		String sql = "select * from t_orderitem where oid = ?";
		List<Map<String, Object>> mapList = qr.query(sql, new MapListHandler(),order.getOid());
		List<OrderItem> orderItemList = toOrderItemList(mapList);
		order.setOrderItemList(orderItemList);
	}
	private List<OrderItem> toOrderItemList(List<Map<String, Object>> mapList) {
		List<OrderItem> orderItemList = new ArrayList<>();
		for (Map<String,Object> map: mapList) {
			OrderItem orderItem = toOrderItem(map);
			orderItemList.add(orderItem);
		}
		return orderItemList;
	}
	private OrderItem toOrderItem(Map<String, Object> map) {
		OrderItem orderItem = CommonUtils.toBean(map, OrderItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		orderItem.setBook(book);
		return orderItem;
	}
}
