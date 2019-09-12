package com.problem.web;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;

import com.common.utils.SqlsessionFactoryUtils;
import com.problem.ano.Param;
import com.problem.ano.RequestMapping;
import com.problem.daoMapper.UserDaoMapper;
import com.problem.entity.User;

@WebServlet("/user/*")
public class UserController extends  DispatcherServlet {
	/**
	 * 用户登陆。
	 */
	@RequestMapping("login.action")
	public String login(HttpSession ss,@Param(isjavabean=true)User user) throws ServletException, IOException {
		System.out.println(user);
		User rsu = (User) ss.getAttribute("loginuser");
		if(rsu != null ) {
			 return "redirect:/";
		}else {
			  SqlSession openSession = new SqlsessionFactoryUtils().getSqlSessionFactory().openSession();
			  UserDaoMapper mapper = openSession.getMapper(UserDaoMapper.class);
			  rsu = mapper.findByUserNameAndPassword(user);
			  System.out.println(rsu);
			  if(rsu != null ) {
				  //登陆成功。
				  ss.setAttribute("loginuser", rsu );
				  return "redirect:/";
			  }else {
				   return "/login.jsp";
			  }
		}
	}
	
	@RequestMapping("logout.action")
	public String logout(HttpSession ss) {
		ss.invalidate();
		return "redirect:/";
	}
 
}
