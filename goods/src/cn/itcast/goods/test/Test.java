package cn.itcast.goods.test;

import java.sql.SQLException;

import cn.itcast.goods.user.dao.UserDao;
import cn.itcast.goods.user.service.UserService;

public class Test {
	@org.junit.Test
	public void test01() throws Exception {
//		UserDao userDao = new UserDao();
//		boolean ajaxValidateLoginname = userDao.ajaxValidateLoginname("zhangSan");
//		System.out.println(ajaxValidateLoginname);
		UserService userService = new UserService();
		boolean b = userService.ajaxValidateLoginname("zhangSan");
		System.out.println(b);
	}

}
