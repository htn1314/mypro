package com.problem.web;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.alibaba.fastjson.JSONObject;
import com.problem.ano.Param;
import com.problem.ano.RequestMapping;
import com.problem.ano.ResponseBody;
import com.problem.ano.URLMapping;
 
 
public abstract class DispatcherServlet implements Servlet {
	private ServletConfig cfg;
	private Map<String,URLMapping> mapping = new HashMap<String,URLMapping>();
	public void init(ServletConfig cfg) throws ServletException {
		this.cfg = cfg;
		Method [] ms = this.getClass().getDeclaredMethods();
		for(Method m : ms) {
			RequestMapping rqm = m.getAnnotation( RequestMapping.class );
			//rqm=@com.problem.ano.RequestMapping(method=*, value=logout.action)
			if(rqm != null) {
				URLMapping urlmp = new URLMapping();
				urlmp.setInvokeMethod(m);
				//urlmp=URLMapping [value=null, method=null, invokeMethod=public com.problem.dto.ResultData com.problem.web.QuestionController.update(com.problem.vo.UpdateQuestionVO)]
				//说明这个方法一定添加了RequestMapping注解。
				String url = rqm.value();//注解中的字符串，就是key。
				//url=getall.action
				url = trim(url);
				//getall.action 确保url不会有其他/ 之类
				urlmp.setValue(url);
				urlmp.setMethod(rqm.method());
				mapping.put(url, urlmp);
				/*for(Entry<String, URLMapping>  entry: mapping.entrySet()){
					String key=entry.getKey();
					URLMapping value=entry.getValue();
					System.out.println("key="+key);//key=updatestatus.action
					System.out.println("value="+value);
//URLMapping [value=updatestatus.action, method=*, 
//invokeMethod=public com.problem.dto.ResultData com.problem.web.QuestionController.updateStatus(com.problem.vo.UpdateStatusVO)]
				}*/
			}
		}
		
	}

	//去除前后斜杠。
	private static String trim(String url) {
		StringBuilder sb = new StringBuilder(url);
		while(sb.charAt(0)=='/' || sb.charAt(0) =='\\') {
			 sb.delete(0, 1);
		}
		while(sb.charAt(sb.length()-1)=='/' || sb.charAt(sb.length()-1) =='\\') {
			 sb.delete(sb.length()-1, sb.length());
		}
		return sb.toString();
	}
	
	
	@Override
	public ServletConfig getServletConfig() {
		return cfg;
	}

	@Override
	public void service(ServletRequest rq, ServletResponse rp) throws ServletException, IOException {
		try {
			dispatch((HttpServletRequest)rq, (HttpServletResponse)rp);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getServletInfo() {
		return cfg.getServletName();
	}

	@Override
	public void destroy() {}
	 
	private void dispatch(HttpServletRequest rq, HttpServletResponse rp) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String servletpath = rq.getServletPath();
		String uri = rq.getRequestURI();//获取URI。   
		int index = uri.indexOf( servletpath );
		index +=(servletpath.length() +1 );
		String suffix = uri.substring(index, uri.length());
		///test1/hello.action 
		//去除/test1，留下   hello.action 
		//System.out.println("suffix="+suffix);
		URLMapping urlm = mapping.get(suffix);
		if( urlm != null ) {
				Method method = urlm.getInvokeMethod();
			    if("*".equals( urlm.getMethod() )) {//默认对请求方法不作要求。
			    	invokeMethod(method,rq,rp);
			    }else if(rq.getMethod().equalsIgnoreCase(urlm.getMethod() )){//请求方法匹配上。
			    	invokeMethod(method,rq,rp);
			    }else {
			    	methodNotAllow(rq, rp);
			    }
		}else {
			notFound(rq,rp);
		}
	}
	
	private void invokeMethod(Method method, HttpServletRequest rq, HttpServletResponse rp) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		//1 参数填充。
		Object [] parameter_values = fillParameter(method,rq,rp); 
		//System.out.println(Arrays.toString(parameter_values));
		//2 调用方法。
		Object rs = method.invoke(this, parameter_values);
		//3 返回结果。
		ResponseBody resbody = null;
		if( (resbody=method.getAnnotation(ResponseBody.class) ) != null ) {
			//3.1 返回JSON。
			returnJSONObject( rs,resbody.charset() ,rp );
		}else if(rs != null && rs instanceof String){ //没有ResponseBody注解。
			//3转发或者重定向。
			String url = rs.toString();
			 if( isRedirect ( url )) {
				 //是重定向。
				 redirect(url,rq, rp);
			 }else {
				 //是转发。
				 requestDispather(url,rq,rp);
			 }
		}
		
	}
	 
	private void requestDispather(String url, HttpServletRequest rq, HttpServletResponse rp) {
		 try {
				rq.getRequestDispatcher(url).forward(rq, rp);
			} catch (ServletException | IOException e) {
				e.printStackTrace();
			}
	}

	private void redirect(String redirecturl,HttpServletRequest rq,HttpServletResponse rp) {
		try {
			rp.setStatus(302);
			rp.sendRedirect(redirecturl.replaceAll("redirect:", rq.getServletContext().getContextPath()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private  boolean isRedirect(String url) {
		return url.startsWith("redirect:");
	}
	//返回JSON。
	private void returnJSONObject(Object result ,String charset, HttpServletResponse rp) {
		//借助alibaba的JSON框架。
		String jsonstr = JSONObject.toJSONString(result);
		try {
			byte[] bs = jsonstr.getBytes( charset );
			rp.setContentLength(  bs.length );
			rp.setCharacterEncoding(charset);
			rp.setContentType("application/json");
			rp.getOutputStream().write(bs);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 参数填充。
	 * 这里，我将参数分为：内置类型、基本类型、javaBean、List、Map、String、其它 这6大类。
	 * 内置类型：request,response,session,context对象。
	 * 基本类型： 8大基本类型 + 包装类型都算。
	 * List :  List<?>类型。 
	 * Map :   Map<?,?>类型。
	 * String ： 字符串类型。
	 * javaBean ： 含有@Param注解并且isjavabean属性为true的参数。此参数需要参与反射，进行HTTP参数绑定。
	 * 其它类型： Date 等。
	 * 
	 * 
	 * @param method
	 * @param rq
	 * @param rp
	 * @return
	 */
	private Object[] fillParameter(Method method,HttpServletRequest rq,HttpServletResponse rp) {
		Object []parameter_values = new Object[method.getParameterCount()];//参数值。
		Parameter[] params = method.getParameters();//参数对象。
		for(int i=0; i< params.length;i++) {
			Parameter param =  params[i];
			//根据类型赋值。
			Class type = param.getType();
			Object value = null;
			//内置类型。
			boolean next_flag = false;
			if(  ServletRequest.class.isAssignableFrom( type ) ||  HttpServletRequest.class.isAssignableFrom( type )) {
				value = rq;
				next_flag = true;
			}else if(  ServletResponse.class.isAssignableFrom( type ) ||  HttpServletResponse.class.isAssignableFrom( type )) {
				value = rp;
				next_flag = true;
			}else if(HttpSession.class.isAssignableFrom( type ) ) {
				value = rq.getSession();
				next_flag = true;
			}else if(ServletContext.class.isAssignableFrom( type ) ) {
				value = rq.getServletContext();
				next_flag = true;
			} 
			//如果是内置类型。跳过
		
			if(next_flag) {
				parameter_values[i] = value;
				continue;
			}
			
			//如果参数有注解，优先使用参数的注解指定的名字。
			Param pram_ano = param.getAnnotation( Param.class );
			String parameter_name = (pram_ano ==null ? param.getName() :   pram_ano.value() );//是否是参数名。
			value = rq.getParameter(parameter_name);//value有可能为null。

			if( pram_ano != null && pram_ano.isjavabean() ) {
				//是一个实体类对象。需要从rq中获取参数并放入实体对象中。
				try {
					value = invoteSetter(type, rq);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}else  if ( type == int.class || type == Integer.class ) {
				if( isNotEmpty(value) )
				value =  Integer.parseInt( value +"" );
			}else if ( type == long.class || type == Long.class) {
				if( isNotEmpty(value) )
				value = Long.parseLong(value+"");
			}else if(  type == float.class || type == Float.class) {
				if( isNotEmpty(value) )
				value = Float.parseFloat( value +"");
			}else if(  type == boolean.class || type == Boolean.class) {
				if( isNotEmpty(value) )
				value = Boolean.parseBoolean(value+"");
			}else if(type == Date.class) {
					
			}else if(type == List.class ) {
				
			}else if(type == Map.class) {
				
			}
			parameter_values[i] = value;
		}
		return parameter_values;
	}
 
	private boolean isNotEmpty(Object str) {
		return !isEmpty(str);
	}
	private boolean isEmpty(Object str) {
		return str ==null || str.equals("");
	}
	private void notFound(HttpServletRequest rq, HttpServletResponse rp) {
		rp.setStatus(404);
		rp.setContentType("text/html;charset=UTF-8");
		try {
			rp.getWriter().append("你要找的资源不存在哦~，亲~");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void methodNotAllow(HttpServletRequest rq, HttpServletResponse rp) {
		rp.setStatus(404);
		rp.setContentType("text/html;charset=UTF-8");
		try {
			rp.getWriter().append("请求方法不允许。当前请求方法为："+rq.getMethod());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private Object invoteSetter(Class cls,HttpServletRequest rq,int ...times) throws Exception {
		Object obj = cls.newInstance();// 反射创建对象。
		// ① 当前cls所在的类，定义了哪些属性。才能知道setter方法的名字。
		// ② 每个属性是什么类型的。才能知道要调用rs.getInt还是rs.getString ...
		Method []ms = cls.getMethods();
		for (Method m : ms) {
			
			if(!isSetterMethod(m))continue;
			String fieldname = getFieldName(m);// 属性的名字。
			Class<?> type = getFieldType(m);
			Method settermethod = m;
			Object value = rq.getParameter(fieldname);//属性名即为参数名。
			if ( type == int.class || type == Integer.class ) {
				if(isNotEmpty(value))
				value =  Integer.parseInt( value +"" );
			}else if ( type == long.class || type == Long.class) {
				if(isNotEmpty(value))
				value = Long.parseLong(value+"");
			}else if(  type == float.class || type == Float.class) {
				if(isNotEmpty(value))
				value = Float.parseFloat( value +"");
			}else if(  type == boolean.class || type == Boolean.class) {
				if(isNotEmpty(value))
				value = Boolean.parseBoolean(value+"");
			}else if(type == Date.class) {
				
			}else if(type == List.class ) {
				//规则待定。
			}else if(type == Map.class) {
				//规则待定。
			}else if(type == String.class){
				
			}else {
				if(times ==null || times.length == 0)times = new int[]{0};//防止出现新类型递归出错。
				if(times[0] >10) {
					throw new RuntimeException(type+"  类型未处理。请更新框架 ");
				}
				times[0]++;
				value = invoteSetter(type, rq,times);
			}
			if(isNotEmpty(value))
			settermethod.invoke(obj, value);
		}
		return obj;
	}
	
	public static boolean isSetterMethod(Method settermethod) {
		return settermethod.getName().startsWith("set") && 
			   settermethod.getName().charAt(3)>='A' && 
			   settermethod.getName().charAt(3)<='Z' &&
			   settermethod.getParameterCount() ==1;
	}
	
	public static Class getFieldType(Method settermethod) {
		return settermethod.getParameterTypes()[0];
	}
	
	public static String getFieldName(Method settermethod) {
		String name = settermethod.getName();
		name = name.substring(3,name.length());
		name = name.substring(0, 1).toLowerCase() + name.substring(1, name.length());
		return name;
	}
	 
	 
}
