package com.lzc.demo.util.optimize;
/**
 * 算法实例简单工厂
 * 通过这个类获取具体的算法实现类
 * @author TR
 *
 */
public class OptimizeAlgoFactory {
	private static StartOptimizeInter algo = null;
	public static StartOptimizeInter getAlgoInstanse(){
		if(algo == null){
			algo = new OptimizeAlgoImpl2();
		}
		return algo;
	}
}
