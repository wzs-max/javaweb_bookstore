package cn.itcast.goods.cart.web.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.cart.domain.CartItem;
import cn.itcast.goods.cart.service.CartItemService;
import cn.itcast.goods.user.domain.User;
import cn.itcast.servlet.BaseServlet;

import static org.hamcrest.CoreMatchers.nullValue;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class CartItemServlet
 */
public class CartItemServlet extends BaseServlet {

	CartItemService cartItemService = new CartItemService();
	/*
	 * 查询购物车
	 */
	public String myCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//1.得到uid
		User user =(User) request.getSession().getAttribute("sessionUser");
		String uid = user.getUid();
		//2.得到购物车条目
		List<CartItem> cartItemList = cartItemService.myCart(uid);
		//3.将条目保存到requet域中
		request.setAttribute("cartItemList",cartItemList);
		return "f:/jsps/cart/list.jsp";
				
	}
/*
 * 添加购物车条目
 */
	public String add(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//1.封装表单数据(表单传过来的数据有bid和quantity)
		CartItem cartItem = CommonUtils.toBean(request.getParameterMap(), CartItem.class);
		Book book = CommonUtils.toBean(request.getParameterMap(), Book.class);
		User user =(User) request.getSession().getAttribute("sessionUser");
		cartItem.setBook(book);
		cartItem.setUser(user);
		//2.调用service
		cartItemService.add(cartItem);
		//3.查询出当前用户购物车所有条目
		return myCart(request, response);
	}
	/*
	 * 删除购物车条目
	 */
	public String batchDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String cartItemIds = request.getParameter("cartItemIds");
		cartItemService.batchDelete(cartItemIds);
		return myCart(request, response);
	}
	/*
	 * 修改数量
	 */
	public String updateQuantity(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String cartItemId = request.getParameter("cartItemId");
		int quantity =Integer.parseInt(request.getParameter("quantity")) ;
		CartItem cartItem = cartItemService.updateQuantity(cartItemId, quantity);//为什么不直接返回quantity呢？因为还要计算小计
		StringBuilder sb = new StringBuilder();
		sb.append("{");
		sb.append("\"quantity\"").append(":").append(cartItem.getQuantity());
		sb.append(",");
		sb.append("\"subtotal\"").append(":").append(cartItem.getSubtotal());
		sb.append("}");
		System.out.println(sb);
		response.getWriter().println(sb);
		return null;
		
	}
	/*
	 * 加载被选中的条目
	 */
	public String loadCartItems(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String cartItemIds = request.getParameter("cartItemIds");
		double total = Double.parseDouble(request.getParameter("total"));
		List<CartItem> cartItemList = cartItemService.loadCartItems(cartItemIds);
		request.setAttribute("cartItemList",cartItemList);
		request.setAttribute("total",total);
		request.setAttribute("cartItemIds", cartItemIds);
		return "f:/jsps/cart/showitem.jsp";
	}
}
