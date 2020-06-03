package cn.itcast.goods.user.service;
/**
 * 用户模块业务层
 * @author 21696
 *
 */

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;

import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.sun.org.apache.xerces.internal.xs.StringList;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.user.dao.UserDao;
import cn.itcast.goods.user.domain.User;
import cn.itcast.goods.user.service.exception.UserException;
import cn.itcast.mail.Mail;
import cn.itcast.mail.MailUtils;

public class UserService {
	
	private UserDao userDao = new UserDao();
	/**
	 * 校验用户名
	 * @param loginname
	 * @return
	 * @throws Exception 
	 */
	public boolean ajaxValidateLoginname(String loginname)  {
			 
			try {
				return userDao.ajaxValidateLoginname(loginname);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				throw new RuntimeException(e);
			}
		
	}
	/**
	 * 校验email
	 * @param email
	 * @return
	 * @throws SQLException
	 */
	public boolean ajaxValidateEmail(String email) throws SQLException {
		 try {
				return userDao.ajaxValidateEmail(email);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				throw new RuntimeException(e);
			}
	}
	/**
	 * 注册功能
	 */
	public void regist(User user) {
		//数据补齐
		user.setUid(CommonUtils.uuid());//设置uuid
		user.setStatus(false);//设置激活码状态
		user.setActivationCode(CommonUtils.uuid()+CommonUtils.uuid());//设置激活码
		try {
			userDao.add(user);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
		/**
		 * 发邮件
		 */
		Properties prop = new Properties();
		try {
			prop.load(this.getClass().getClassLoader().getResourceAsStream("email_template.properties"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e1);
		}
		String host = prop.getProperty("host");//服务器主机名
		String name =prop.getProperty("username");//登录名
		String pass = prop.getProperty("password");//登录密码
		//System.out.println(host+","+name+","+pass);
		Session session = MailUtils.createSession(host, name, pass);
		
		String from = prop.getProperty("from");
		String to = user.getEmail();
		String subject = prop.getProperty("submject");
		String content = MessageFormat.format(prop.getProperty("content"),user.getActivationCode());
		//System.out.println(content);
		Mail mail = new Mail(from,to,subject,content);
		
		try {
			MailUtils.send(session, mail);//发邮件
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
	/*
	 * 激活
	 */
	public void activation(String code) throws Exception {
		/*
		 * 1.通过激活码查询用户
		 * 2.改变激活码状态
		 */	
			try {
				 User user = userDao.findByCode(code);
				 if(user == null) throw new UserException("无效的激活码!");
				 if(user.isStatus()) throw new UserException("您已经激活了，不要二次激活！");
				 userDao.updateStatus(user.getUid(), true);
			}catch(SQLException exception) {
				throw new RuntimeException(exception);
			}
	
	}
	/*
	 * 登录功能
	 */
	public User login(User user) {
		try {
			return userDao.findByLoginnameAndLoginpass(user.getLoginname(), user.getLoginpass());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
	/*
	 * 修改密码
	 */
	public void updatePassword(String uid,String newPass,String oldPass) throws UserException {
		
		try {
			//1.校验老密码
			boolean bool = userDao.findByUidAndPassword(uid, oldPass);
			
			if(!bool) {//如果老密码错误
				throw new UserException("老密码错误！");
			}
			//2.修改密码
			userDao.updatePassword(uid, newPass);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException(e);
		}
	}
}
