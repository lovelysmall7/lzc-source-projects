package com.lzc.demo.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.python.core.Py;
import org.python.core.PyFunction;
import org.python.core.PyObject;
import org.python.core.PySystemState;
import org.python.util.PythonInterpreter;

public class CallPyhonTest {
	public static void main(String[] args){
		try {
			String param = buildJson();
			param = param.replace('"', '#');
			String[] args1 = new String[] { "python", "D:\\python\\TSP_12.18.py", param };  
            Process pr = Runtime.getRuntime().exec(args1);
            BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
               // line = decodeUnicode(line);
                System.out.println("python response:" + line);
            }
            in.close();
            pr.waitFor();
            System.out.println("end");
        } catch (Exception e) {
            e.printStackTrace();
        }
		//excPy();
	}
	private static String buildJson(){
		String jsonStr = "";
		Map<String,Object> jMap = new HashMap<String,Object>();
		String[] a = {"05_159_2","05_156_1","05_174_2","05_169_2"};
		String[] b = {"05_163_2","05_147_1","05_152_1","05_163_1"};
		jMap.put("P00005", a);
		jMap.put("P00004", b);
		return JSONObject.fromObject(jMap).toString();
	}
	public static void excPy(){
		/*PySystemState sys = Py.getSystemState();
		sys.path.add("D:\\pyFile");
		
		PythonInterpreter interpreter = new PythonInterpreter();
		interpreter.exec("import B");
        interpreter.execfile("D:\\pyFile\\A.py");

        PyFunction pyFunction = interpreter.get("say", PyFunction.class); // 第一个参数为期望获得的函数（变量）的名字，第二个参数为期望返回的对象类型
*/ 
		PySystemState sys = Py.getSystemState();
		sys.path.add("D:\\python");
		sys.path.add("C:\\Users\\TR\\Downloads\\jython-2.5.0\\org\\python\\modules");
		PythonInterpreter interpreter = new PythonInterpreter();
		interpreter.exec("import random");
		interpreter.exec("import math");
		interpreter.exec("import sys");
		interpreter.exec("import numpy as np");
		interpreter.exec("import pandas as pd");
		interpreter.exec("import GA");
        interpreter.execfile("D:\\1211\\TSP_GA_W_V1.py");

        PyFunction pyFunction = interpreter.get("main", PyFunction.class);
		PyObject pyObject = pyFunction.__call__(); // 调用函数

        System.out.println(pyObject);
	}
}
