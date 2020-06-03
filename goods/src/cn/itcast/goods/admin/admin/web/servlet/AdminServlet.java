package cn.itcast.goods.admin.admin.web.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.admin.admin.domain.Admin;
import cn.itcast.goods.admin.admin.service.AdminService;
import cn.itcast.servlet.BaseServlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AdminServlet
 */
public class AdminServlet extends BaseServlet {
	private AdminService adminService = new AdminService();
       

	public String  login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Admin form = CommonUtils.toBean(request.getParameterMap(), Admin.class);
		Admin admin = adminService.login(form);
		if(admin == null) {
			request.setAttribute("msg", "用户名或密码错误");
			return "/adminjsps/login.jsp";
		}
		request.getSession().setAttribute("admin",admin);
		return "/adminjsps/admin/index.jsp";
	}


}
