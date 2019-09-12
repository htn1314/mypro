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
			//����ӳ���ļ�
			InputStream is = Resources.getResourceAsStream("mybatis.xml");
			//����SqlsessionFactory����
			sqlsessionfactory =new SqlSessionFactoryBuilder().build(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static SqlSessionFactory getSqlSessionFactory() {
		return sqlsessionfactory;
	}
}
