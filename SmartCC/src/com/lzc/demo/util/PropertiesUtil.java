package com.lzc.demo.util;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
	/*
	 *静态量， properties 配置文件中对应的变量名
	 */
	private static final String pythonPath = "pythonPath";
	private static final String workerListFilePath = "workerListFilePath";
	private static final String pythonPath1 = "pythonPath1";
	private static final String initx = "initx";
	private static final String inity = "inity";
	private static final String em = "em";//发布环境
	public static Properties configs = new Properties(); //properties 配置文件
	public static Properties props = new Properties(); //创建 Session 实例时需要传递的基本参数

	/**
	 * 初始化静态量
	 */
	static {
		System.out.println("初始化连接属性......");
		InputStream inputStream = PropertiesUtil.class
				.getResourceAsStream("config.properties");
		try {
			configs.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
			}
		}
		props.put(pythonPath, configs.getProperty(pythonPath));
		props.put(workerListFilePath, configs.getProperty(workerListFilePath));
		props.put(pythonPath1, configs.getProperty(pythonPath1));
		props.put(initx, configs.getProperty(initx));
		props.put(inity, configs.getProperty(inity));
		props.put(em, configs.getProperty(em));
	}
}
