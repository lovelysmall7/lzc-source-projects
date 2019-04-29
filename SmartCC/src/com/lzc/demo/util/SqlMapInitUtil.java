package com.lzc.demo.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class SqlMapInitUtil {
	private static SqlSessionFactory sqlsessionfactory;//�洢SqlSessionFactory ����
	public static Map<String,String> workerComponentMap;
	static{
		workerComponentMap = new HashMap<String,String>();
		workerComponentMap.put("bba-001", "刮水器");
		workerComponentMap.put("bba-002", "散热器格栅");
		workerComponentMap.put("bba-003", "前挡风玻璃");
		workerComponentMap.put("bba-004", "发动机罩");
		workerComponentMap.put("bba-005", "发动机罩");
		//设置配置路径，mybitis是以SqlMapConfig.xml为主路径。因为SqlMapConfig中的mapper关联了user.xml
        //因为在config根目录下，所以可以直接引用而不用带config
        String resource = "SqlMapConfig.xml";
        //SqlMapConfig.xml读给输入流，使用mybitis的Resources类下的getResourceAsStream实现
        InputStream inputStream;
		try {
			inputStream = Resources.getResourceAsStream(resource);
			sqlsessionfactory = new SqlSessionFactoryBuilder().build(inputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //创建Mybitis的SqlSessionFactory工厂类
       
	}
	public static SqlSession getSqlSession(){
		return sqlsessionfactory.openSession();
	}
}
