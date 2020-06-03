package cn.itcast.goods.order.web.servlet;

import static org.hamcrest.CoreMatchers.nullValue;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.cart.domain.CartItem;
import cn.itcast.goods.cart.service.CartItemService;
import cn.itcast.goods.order.domain.Order;
import cn.itcast.goods.order.domain.OrderItem;
import cn.itcast.goods.order.service.OrderService;
import cn.itcast.goods.pager.PageBean;
import cn.itcast.goods.user.domain.User;
import cn.itcast.servlet.BaseServlet;

public class OrderServlet extends BaseServlet {

	OrderService orderService = new OrderService();
	CartItemService cartItemService = new CartItemService();
	
	public String payment(HttpServletRequest req,HttpServletResponse resp) throws ServletException,SQLException, Exception{
		Properties props = new Properties();
		props.load(this.getClass().getClassLoader().getResourceAsStream("payment.properties"));
		
		String p0_Cmd = "Buy";
		String p1_MerId = props.getProperty("p1_MerId");
		String p2_Order = req.getParameter("oid");
		String p3_Amt = "0.01";
		String p4_Cur = "CNY";
		String p5_Pid = "";
		String p6_Pcat = "";
		String p7_Pdesc = "";
		String p8_Url = props.getProperty("p8_Url");
		String p9_SAF = "";
		String pa_MP = "";
		String pd_FrpId = req.getParameter("yh");
		String pr_NeedResponse = "1";
		
		String keyValue = props.getProperty("keyValue");
		String hmac = PaymentUtil.buildHmac(p0_Cmd, p1_MerId, p2_Order, p3_Amt, p4_Cur, p5_Pid, p6_Pcat, p7_Pdesc,
				p8_Url, p9_SAF, pa_MP, pd_FrpId, pr_NeedResponse, keyValue);
		StringBuilder sb = new StringBuilder("https://www.yeepay.com/app-merchant-proxy/node");
		sb.append("?").append("p0_Cmd=").append(p0_Cmd);
		sb.append("&").append("p1_MerId=").append(p1_MerId);
		sb.append("&").append("p2_Order=").append(p2_Order);
		sb.append("&").append("p3_Amt=").append(p3_Amt);
		sb.append("&").append("p4_Cur=").append(p4_Cur);
		sb.append("&").append("p5_Pid=").append(p5_Pid);
		sb.append("&").append("p6_Pcat=").append(p6_Pcat);
		sb.append("&").append("p7_Pdesc=").append(p7_Pdesc);
		sb.append("&").append("p8_Url=").append(p8_Url);
		sb.append("&").append("p9_SAF=").append(p9_SAF);
		sb.append("&").append("pa_MP=").append(pa_MP);
		sb.append("&").append("pd_FrpId=").append(pd_FrpId);
		sb.append("&").append("pr_NeedResponse=").append(pr_NeedResponse);
		sb.append("&").append("hmac=").append(hmac);
		
		resp.sendRedirect(sb.toString());
		return null;
	}
	public String back(HttpServletRequest req,HttpServletResponse resp) throws ServletException,SQLException{
		return null;
	}
	/*
	 * 支付准备
	 */
	public String paymentPre(HttpServletRequest req,HttpServletResponse resp) throws ServletException,SQLException{
		req.setAttribute("order", orderService.load(req.getParameter("oid")));
		return "f:/jsps/order/pay.jsp";
	}
	
	/*
	 * 取消订单
	 */
	public String cancel(HttpServletRequest req,HttpServletResponse resp) throws ServletException,SQLException{
		
		String oid = req.getParameter("oid");
		//校验订单状态
		int findStatus = orderService.findStatus(oid);
		if(findStatus != 1) {
			req.setAttribute("code", "error");
			req.setAttribute("msg", "状态不对，无法取消！");
			return "f:/jsps/msg.jsp";
		}
		//修改状态
		orderService.updateStatus(oid,5);
		req.setAttribute("code", "success");
		req.setAttribute("msg", "您的订单已取消！");
		return "f:/jsps/msg.jsp";
	}
	
	/*
	 * 确认收货
	 */
public String confirm(HttpServletRequest req,HttpServletResponse resp) throws ServletException,SQLException{
		
		String oid = req.getParameter("oid");
		//校验订单状态
		int findStatus = orderService.findStatus(oid);
		if(findStatus != 3) {
			req.setAttribute("code", "error");
			req.setAttribute("msg", "状态不对，无法确认收货！");
			return "f:/jsps/msg.jsp";
		}
		//修改状态
		orderService.updateStatus(oid, 4);
		req.setAttribute("code", "success");
		req.setAttribute("msg", "交易成功！");
		return "f:/jsps/msg.jsp";
	}
	
	/*
	 * 加载订单
	 */
	public String load(HttpServletRequest req,HttpServletResponse resp) throws ServletException,SQLException{
		String oid = req.getParameter("oid");
		Order order = orderService.load(oid);
		String btn = req.getParameter("btn");
		req.setAttribute("btn", btn);
		req.setAttribute("order", order);
		return "f:/jsps/order/desc.jsp";
	}
	/*
	 * 生成订单
	 */
	
	public String createOrder(HttpServletRequest req,HttpServletResponse resp) throws ServletException,SQLException{
		//获取所有购物车条目的id，查询
		String cartItemIds = req.getParameter("cartItemIds");//此参数是多个cartItemId组成的字符串
		List<CartItem> cartItemList = cartItemService.loadCartItems(cartItemIds);
		//创建order
		Order order = new Order();
		order.setOid(CommonUtils.uuid());
		order.setOrdertime(String.format("%tF %<tT", new Date()));
		order.setStatus(1);
		order.setAddress(req.getParameter("address"));
		User owner = (User)req.getSession().getAttribute("sessionUser");
		order.setOwner(owner);
		BigDecimal total = new BigDecimal("0");
		for (CartItem cartItem : cartItemList) {
			total = total.add(new BigDecimal(cartItem.getSubtotal()+""));
		}
		order.setTotal(total.doubleValue());
		//3.创建List<OrderItem>
		List<OrderItem> orderItemList = new ArrayList<OrderItem>();
		for (CartItem cartItem : cartItemList) {
			OrderItem orderItem = new OrderItem();
			orderItem.setOrderItemId(CommonUtils.uuid());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setSubtotal(cartItem.getSubtotal());
			orderItem.setBook(cartItem.getBook());
			orderItem.setOrder(order);
			orderItemList.add(orderItem);
		}
		order.setOrderItemList(orderItemList);
		//调用service方法
		orderService.createOrder(order);
		//删除购物车条目
		cartItemService.batchDelete(cartItemIds);
		req.setAttribute("order", order);
		return "f:/jsps/order/ordersucc.jsp";
	}
	
	
	//获取当前页码
	private int getPc(HttpServletRequest req) {
		int pc = 1;
		String param =req.getParameter("pc");
		if(param != null  && !param.trim().isEmpty()) {
			try {
				pc = Integer.parseInt(param);
			}catch (Exception e) {
			}
			
		}
		return pc;
	}
	//截取url
	public String getUrl(HttpServletRequest req) {
		String url = req.getRequestURI() + "?"+req.getQueryString();
		int index = url.lastIndexOf("&pc=");
		if(index != -1) {
			url = url.substring(0,index);
		}
		return url;
	}
	/*
	 * 我的订单
	 */
	public String myOrders(HttpServletRequest req,HttpServletResponse resp) throws ServletException,SQLException{
		int pc = getPc(req);
		String url = getUrl(req);
		User user = (User)req.getSession().getAttribute("sessionUser");
		PageBean<Order> pb = orderService.myOrders(user.getUid(), pc);
		pb.setUrl(url);
		req.setAttribute("pb",pb);
		return "f:/jsps/order/list.jsp";
	}
}
