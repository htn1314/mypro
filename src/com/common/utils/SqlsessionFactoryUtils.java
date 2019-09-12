package com.common.utils;
import java.io.IOException;
import java.io.InputStream;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class SqlsessionFactoryUtils {
	public static SqlSessionFactory sqlsessionfactory; 
	static {
		try {
			//加载映射文件
			InputStream is = Resources.getResourceAsStream("mybatis.xml");
			//创建SqlsessionFactory工厂
			sqlsessionfactory =new SqlSessionFactoryBuilder().build(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static SqlSessionFactory getSqlSessionFactory() {
		return sqlsessionfactory;
	}
}
