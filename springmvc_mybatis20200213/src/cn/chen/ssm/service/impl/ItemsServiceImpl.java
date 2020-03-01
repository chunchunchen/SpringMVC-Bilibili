package cn.chen.ssm.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import cn.chen.ssm.mapper.ItemsMapperCustom;
import cn.chen.ssm.po.ItemsCustom;
import cn.chen.ssm.po.ItemsQueryVo;
import cn.chen.ssm.service.ItemsService;

//商品管理
public class ItemsServiceImpl implements ItemsService{

	@Autowired
	private ItemsMapperCustom itemsMapperCustom;
	
	public List<ItemsCustom> findItemsList(ItemsQueryVo itemsQueryVo) throws Exception {
		//通过ItemsMapperCustom查询数据库
		return itemsMapperCustom.findItemsList(itemsQueryVo);
	}
}
