package cn.itcast.goods.user.dao;
/**
 * 用户模块持久层
 * @author 21696
 *
 */

import java.sql.SQLException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.mchange.v2.c3p0.impl.NewPooledConnection;

import cn.itcast.goods.user.domain.User;
import cn.itcast.jdbc.TxQueryRunner;

public class UserDao {
	private QueryRunner qr = new TxQueryRunner();
	/**
	 * 校验用户名是否注册
	 * @param loginname
	 * @return
	 * @throws SQLException 
	 */
	public boolean ajaxValidateLoginname(String loginname) throws SQLException {
		String sql = "select count(1) from t_user where loginname =?";
		//System.out.println(qr.query(sql,new ScalarHandler(), loginname));
		Number number = (Number)qr.query(sql,new ScalarHandler(), loginname);
		//System.out.println(qr.query(sql,new ScalarHandler(), loginname));
		return number.intValue() > 0;
	}
	/**
	 * 校验email是否注册
	 * @param email
	 * @return
	 * @throws SQLException
	 */
	public boolean ajaxValidateEmail(String email) throws SQLException {
		String sql = "select count(1) from t_user where email = ?";
		Number number = (Number)qr.query(sql,new ScalarHandler(), email);
		//System.out.println(number);
		return number.intValue() > 0;
	}
	/*
	 * 添加用户
	 */
	public void add(User user) throws Exception {
		String sql = "insert into t_user values(?,?,?,?,?,?)";
		Object[] params = {user.getUid(),user.getLoginname(),user.getLoginpass(),user.getEmail(),
				user.isStatus(),user.getActivationCode()};
		qr.update(sql, params);
	}
	/**
	 * 通过激活码查询用户
	 * @throws Exception 
	 */
	public User findByCode(String code) throws Exception {
		String sql = "select * from t_user where activationCode = ?";
		return qr.query(sql, new BeanHandler<User>(User.class),code);
	}
	/*
	 * 修改激活码状态
	 */
	public void updateStatus(String uid,boolean status) throws Exception {
		String sql = "update t_user set status=? where uid = ?";
		qr.update(sql,status,uid);
	}
	
	/*
	 * 按用户名和密码查询
	 */
	public User findByLoginnameAndLoginpass(String loginname,String loginpass) throws Exception {
		String	sql = "select * from t_user where loginname = ? and loginpass = ?";
		return qr.query(sql, new BeanHandler<User>(User.class),loginname,loginpass);
	}
	
	/*
	 * 按uid和password查询
	 */
	public boolean findByUidAndPassword(String uid,String password) throws SQLException {
		String sql = "select count(*) from t_user where uid = ? and loginpass = ?";
		Number number  =(Number) qr.query(sql,new ScalarHandler(),uid,password);
		return number.intValue() > 0;
	}
	/*
	 * 修改密码
	 */
	public void updatePassword(String uid,String password) throws SQLException {
		String sql = "update t_user set loginpass = ? where uid = ? ";
		qr.update(sql,password,uid);
				
	}
	
	

}
