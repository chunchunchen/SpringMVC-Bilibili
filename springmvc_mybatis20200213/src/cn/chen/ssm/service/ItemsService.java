package cn.chen.ssm.service;
//商品管理

import java.util.List;

import cn.chen.ssm.po.ItemsCustom;
import cn.chen.ssm.po.ItemsQueryVo;

public interface ItemsService {
	//商品查询列表
	public List<ItemsCustom> findItemsList(ItemsQueryVo itemsQueryVo)throws Exception;
	
}
