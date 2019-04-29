package com.lzc.demo.util.optimize;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.util.StringUtils;

import com.lzc.demo.dao.DaoContainer;
import com.lzc.demo.model.DivideHistoryInfo;
import com.lzc.demo.model.MapInfo;
import com.lzc.demo.model.StationInfo;
import com.lzc.demo.util.SysStringUtil;
import com.lzc.demo.util.Astar.AStar;
import com.lzc.demo.util.Astar.AStarMapInfo;
import com.lzc.demo.util.Astar.Node;

/**
 * 这个算法和OptimizeAlgoImpl1 的区别在于 没有考虑进去偏移量
 * 
 * @author TR
 *
 */
public class OptimizeAlgoImpl2 extends BaseOptimizeAlgo implements StartOptimizeInter {

	@Override
	public void startOptimize(List<DivideHistoryInfo> workerTaskList, String workerId) {
		// TODO Auto-generated method stub
		divideAlgo(workerId, workerTaskList, true);// 紧急任务
		divideAlgo(workerId, workerTaskList, false);// 普通任务

	}

	@Override
	public List<DivideHistoryInfo> divideAlgo(String workerId, List<DivideHistoryInfo> dList, boolean isEmergency) {
		// TODO Auto-generated method stub
		List<DivideHistoryInfo> queue = new ArrayList<DivideHistoryInfo>();
		DivideHistoryInfo dInfo = DaoContainer.dDao.selectLastComponentCurrentBatch(workerId);
		Iterator<DivideHistoryInfo> iter1 = dList.iterator();
		while (iter1.hasNext()) {
			DivideHistoryInfo d = iter1.next();
			if (isEmergency) {
				if (d.getType() == 1) {
					queue.add(d);
				}
			} else {
				if (d.getType() != 1) {
					queue.add(d);
				}
			}
		}
		String b = "0";
		Integer maxIndex = -1;
		boolean isHasPre = false;
		if (dInfo != null) {
			String resultStr = judgeIsHasPre(dInfo, queue);
			String a = resultStr.substring(0, 1);// 是否以上一个点为起始点
			b = resultStr.substring(1, 2);// 以左上角起始还是右上角起始
			// 如果之前有紧急任务 最大序号应该从最后一个紧急任务开始 + 1
			maxIndex = dInfo.getOrderNum() + 1;
			if ("1".equals(a)) {
				isHasPre = true;
			}

		}

		List<DivideHistoryInfo> newQueue = new ArrayList<DivideHistoryInfo>();
		int count = 0;
		while (queue.size() > 0) {
			if (count == 0) {
				DivideHistoryInfo element = new DivideHistoryInfo();
				if (dInfo != null) {
					newQueue.add(dInfo);
				} else {
					element = findNextComponent(null, queue, b);
					newQueue.add(element);
				}
			} else {
				DivideHistoryInfo preElement = newQueue.get(newQueue.size() - 1);
				DivideHistoryInfo element = findNextComponent(preElement, queue, b);
				newQueue.add(element);
			}
			count++;
		}
		updateOrderNum(newQueue, maxIndex, isHasPre);
		return newQueue;
	}

	public DivideHistoryInfo findNextComponent(DivideHistoryInfo preComponent, List<DivideHistoryInfo> dList,
			String startPointFlag) {
		AStar aStar = new AStar();
		AStarMapInfo mapInfo = MapInfo.getInstanse().getAstartMapInfo();
		String initxStr = "0";
		String inityStr = "0";
		if ("1".equals(startPointFlag)) {
			initxStr = "267";
			inityStr = "385";
		}
		Integer x = Integer.parseInt(initxStr);
		Integer y = Integer.parseInt(inityStr);
		// 如果上一个工位信息不空 说明不是第一个点了 需要通过它去寻找下一个零件
		if (preComponent != null) {
			String preStationName = "";
			if (!StringUtils.isEmpty(preComponent.getKnockingPoint())) {// 说明该零件有敲枪点
				preStationName = preComponent.getKnockingPoint();
			} else {// 没有敲枪点就用最后一个工位作为寻找下一个工位的依据
				String stationIds = preComponent.getStationIds();
				String stations[] = stationIds.split(",");
				preStationName = stations[stations.length - 1];
			}
			StationInfo preStationInfo = DaoContainer.sDao.findById(preStationName);
			String preX = preStationInfo.getxAxis();
			String preY = preStationInfo.getyAxis();
			preX = SysStringUtil.formatIntegerStr(preX);
			preY = SysStringUtil.formatIntegerStr(preY);
			// excel 索引从1 开始 地图索引从0开始 坐标值需要 -1
			x = Integer.parseInt(preX) - 1;
			y = Integer.parseInt(preY) - 1;

		}

		int minZoomPotinIndex = 0;
		int minPointDistanse = -1;
		Iterator<DivideHistoryInfo> it = dList.iterator();
		Node ssNode = new Node(x, y);
		int ecount = 0;
		while (it.hasNext()) {
			DivideHistoryInfo dinfo = it.next();
			String stationIds = dinfo.getStationIds();
			String[] stations = stationIds.split(",");
			String eStation = stations[0];// 不需要遍历stations了 已经排好序了 只要取第一个就可以
			StationInfo sinfo = DaoContainer.sDao.findById(eStation);
			String xstr = sinfo.getxAxis();
			String ystr = sinfo.getyAxis();
			xstr = SysStringUtil.formatIntegerStr(xstr);
			ystr = SysStringUtil.formatIntegerStr(ystr);
			Node eNode = new Node(Integer.parseInt(xstr) - 1, Integer.parseInt(ystr) - 1);
			mapInfo.setInfo(ssNode, eNode);
			aStar.start(mapInfo);
			Integer distanse_1 = eNode.G;// 起始位置和终止位置的距离
			Integer offset = dinfo.getOffset();
			/*
			 * if(offset != null){ distanse_1 += offset; }
			 */
			if (minPointDistanse == -1) {
				minPointDistanse = distanse_1;
				minZoomPotinIndex = ecount;
			} else {
				if (distanse_1 < minPointDistanse) {
					minPointDistanse = distanse_1;
					minZoomPotinIndex = ecount;
				}
				// 20180510 若出现同样距离最小的工位 则判断其一组工位数目 数目是1的 优先，其余任何数目都一样
				// 例如 01,02,03 01 可以避免先走 01,02,03 再回到01 而是：01,01,02,03
				// 这样就省去了03-01的距离
				else {
					if (distanse_1 == minPointDistanse) {
						if (stations.length == 1) {
							minZoomPotinIndex = ecount;
						}
					}
				}
			}

			ecount++;
		}
		// 找最小点方法没确定 先当做第一个点
		// *********************找到最小的点位置结束
		Iterator<DivideHistoryInfo> itt = dList.iterator();
		int count = 0;// 记录queue 循环次数
		// 这次的循环目的就是根据上面的minZoomPotinIndex 结果 取出queue中的元素分配给这个人 并且从queue中删除
		while (itt.hasNext()) {
			DivideHistoryInfo hm_1 = itt.next();
			if (count == minZoomPotinIndex) {
				itt.remove();
				return hm_1;
			}
			count++;
		}
		return null;
	}

}
