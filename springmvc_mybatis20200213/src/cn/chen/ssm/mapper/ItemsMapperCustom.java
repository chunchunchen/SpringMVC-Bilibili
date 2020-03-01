package cn.chen.ssm.mapper;

import java.util.List;

import cn.chen.ssm.po.ItemsCustom;
import cn.chen.ssm.po.ItemsQueryVo;

public interface ItemsMapperCustom {
	//商品查询列表
	public List<ItemsCustom> findItemsList(ItemsQueryVo itemsQueryVo) throws Exception;
}