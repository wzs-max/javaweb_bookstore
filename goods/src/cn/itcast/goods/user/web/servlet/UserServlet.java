package cn.itcast.goods.user.web.servlet;

import static org.hamcrest.CoreMatchers.nullValue;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.mail.Session;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.dbutils.handlers.MapHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.user.domain.User;
import cn.itcast.goods.user.service.UserService;
import cn.itcast.goods.user.service.exception.UserException;
import cn.itcast.servlet.BaseServlet;

/**
 * 用户模块控制层
 */
public class UserServlet extends BaseServlet {
	
	/**
	 * 
	 */
	private UserService userService = new UserService();
   
	//用户名校验
	public String ajaxValidateLoginname(HttpServletRequest request, HttpServletResponse response) throws Exception {
			String loginname = request.getParameter("loginname");
			boolean b = userService.ajaxValidateLoginname(loginname);
			response.getWriter().print(b);
			return null;
	}
	//email校验
	public String ajaxValidateEmail(HttpServletRequest request, HttpServletResponse response) throws Exception  {
		String email = request.getParameter("email");
		boolean b = userService.ajaxValidateEmail(email);
		response.getWriter().print(b);
		return null;
}
	//验证码校验
	public String ajaxValidateVerifyCode(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//获取输入的验证码
		String verifyCode = request.getParameter("verifyCode");
		//获取图片上的验证码
		String vcode = (String)request.getSession().getAttribute("vCode");
		boolean b = verifyCode.equalsIgnoreCase(vcode);
		response.getWriter().print(b);
		return null;
}
	/**
	 *【验证已通过】 用户注册
	 * @throws Throwable 
	 */
	public String regist(HttpServletRequest request, HttpServletResponse response) throws Throwable {
		//封装表单数据到javabean中
		User formUser = CommonUtils.toBean(request.getParameterMap(), User.class);
		//System.out.println(formUser);
		//再次校验参数，目的是防止越过前端校验直接访问
		Map<String, String> errors = validateRegist(formUser, request.getSession());
		if(errors.size() > 0) {
			request.setAttribute("form",formUser);//用于表单回显
			request.setAttribute("errors", errors);
			return "f:jsps/user/regist.jsp";
		}
		//把表单数据交给service
		userService.regist(formUser);
		
		request.setAttribute("code", "success");
		request.setAttribute("msg", "注册成功，请马上到邮箱激活");
		
		return "f:/jsps/msg.jsp";
	}	
	
	
	
	/*
	 * 注册校验
	 */
	private Map<String,String> validateRegist(User formUser,HttpSession session) throws Exception{
		Map<String, String> errors = new HashMap<String, String>();
		/*
		 * 1.用户名校验
		 */
		String loginname = formUser.getLoginname();
		if(loginname == null || loginname.trim().isEmpty()) {
			errors.put("loginname", "用户名不能为空！");
		}else if(loginname.length() < 3 || loginname.length() >20) {
			errors.put("loginname", "用户名长度必须在3~20之间！");
		}else if(userService.ajaxValidateLoginname(loginname)) {
			errors.put("loginname", "用户名已被注册！");
		}
		/*
		 * 2.密码校验
		 */
		String loginpass = formUser.getLoginpass();
		if(loginpass == null || loginpass.trim().isEmpty()) {
			errors.put("loginpass", "密码不能为空！");
		}else if(loginpass.length() < 3 || loginpass.length() >20) {
			errors.put("loginpass", "密码长度必须在3~20之间！");
		}
		/*
		 * 3.确认密码校验
		 */
		String reloginpass = formUser.getReloginpass();
		if(reloginpass == null || reloginpass.trim().isEmpty()) {
			errors.put("reloginpass", "确认密码不能为空！");
		}else if(!reloginpass.equals(loginpass)) {
			errors.put("reloginpass", "两次密码不一致！");
		}
		/*
		 * 4.email校验
		 */
		String email = formUser.getEmail();
		//System.out.println(email+"3333333333");
		if(email == null || email.trim().isEmpty()) {
			errors.put("email", "Email不能为空！");
		}else if(!email.matches("^[a-z\\d]+(\\.[a-z\\d]+)*@([\\da-z](-[\\da-z])?)+(\\.{1,2}[a-z]+)+$")) {
			errors.put("email", "Email格式错误！");
		}else if(userService.ajaxValidateEmail(email)) {
			errors.put("email", "Email已被注册！");
		}
		/*
		 * 5.验证码校验
		 */
		String verifyCode = formUser.getVerifyCode();
		String vcode = (String) session.getAttribute("vCode");
		if(verifyCode == null || verifyCode.trim().isEmpty()) {
			errors.put("verifyCode", "验证码不能为空！");
		}else if(!verifyCode.equalsIgnoreCase(vcode)) {
			errors.put("verifyCode", "验证码错误！");
		}
		return errors;
	}
	//激活激活码功能
	public String activation(HttpServletRequest request, HttpServletResponse response)
	throws ServletException,IOException{
				/*
				 * 1.获取参数激活码
				 * 2.激活
				 */
		String code = request.getParameter("activationCode");
			try {
				userService.activation(code);
				request.setAttribute("code", "success");
				request.setAttribute("msg", "激活成功");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				request.setAttribute("msg",e.getMessage());
				request.setAttribute("code","error");
			}
			
		
				return "f:/jsps/msg.jsp";
	}

	/*
	 * 登录功能
	 */
	public String login(HttpServletRequest request,HttpServletResponse response) throws Exception {
		/*
		 * 1.封装表单数据到user
		 * 
		 * 2.校验表单数据(防止越过前端校验）
		 * 3.使用service查询得到user
		 * 4.查看用户是否存在，如果不存在：
		 * 保存错误信息：用户名或密码错误
		 * 保存用户数据：为了回显
		 * 转发到login.jsp重新登陆
		 * 5.如果存在，查看状态，如果状态为false
		 * 保存错误信息：您没有激活
		 * 保存表单数据：为了回显
		 * 转发到login.jsp重新登陆
		 * 6.状态为true
		 * 保存当前查询出的user到session中国
		 * 保存当前用户的名称到cookie中，注意编码
		 */	
		User formUser = CommonUtils.toBean(request.getParameterMap(), User.class);
		Map<String, String> errors = validateLogin(formUser,request.getSession());
		if(errors.size() > 0) {
			request.setAttribute("form",formUser);//用于表单回显
			request.setAttribute("errors", errors);
			return "f:jsps/user/login.jsp";
		}
		User user = userService.login(formUser);
		if(user == null) {						//注意验证码已经前端校验过了
			request.setAttribute("msg", "用户名或密码错误！");
			request.setAttribute("user", formUser);
			return "f:/jsps/user/login.jsp";
		}else {
			if(!user.isStatus()) {	//校验用户状态
				request.setAttribute("msg", "您还没有激活！");
				request.setAttribute("user", formUser);
				return "f:/jsps/user/login.jsp";
			}else {
				request.getSession().setAttribute("sessionUser", user);//【将user保存到session】
				String loginname = user.getLoginname();
				loginname = URLEncoder.encode(loginname, "utf-8");//【将字符串编码，防止乱码】
				Cookie cookie = new Cookie("loginname", loginname);//【将用户名保存到cookie】
				cookie.setMaxAge(60*60*24*10);//保存十天
				response.addCookie(cookie);
				return "r:/index.jsp";
				
			}
		}
	}
	/**
	 * 登录校验方法
	 * @param formUser
	 * @param session
	 * @return
	 * @throws Exception
	 */
	private Map<String,String> validateLogin(User formUser,HttpSession session) throws Exception{
		Map<String,String> errors = new HashMap<String,String>();
		return errors;
		
	}
	/**
	 * 修改密码
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public String updatePassword(HttpServletRequest request,HttpServletResponse response)  {
		/*
		 * 1.封装表单数据到user中
		 * 2.从session中获取uid
		 * 3.调用service方法	
		 */
		User formUser = CommonUtils.toBean(request.getParameterMap(), User.class);
		User user = (User) request.getSession().getAttribute("sessionUser");
		if(user == null) {
			request.setAttribute("msg","你还没有登录！");
			return "f:/jsps/user/login.jsp";
		}
		try {
			userService.updatePassword(user.getUid(), formUser.getNewloginpass(), formUser.getLoginpass());
			request.setAttribute("msg","修改密码成功");//保存错误信息
			request.setAttribute("code","success");//为了回显
			return "f:/jsps/msg.jsp";
		} catch (UserException e) {
			request.setAttribute("msg", e.getMessage());//保存错误信息
			request.setAttribute("user",formUser);//为了回显
			return "f:/jsps/user/pwd.jsp";
		}
		
		
	}
	/*
	 * 退出功能
	 */
	public String quit(HttpServletRequest request,HttpServletResponse response)  {
		request.getSession().invalidate();//销毁session
		return "r:/jsps/user/login.jsp";
	}
}
