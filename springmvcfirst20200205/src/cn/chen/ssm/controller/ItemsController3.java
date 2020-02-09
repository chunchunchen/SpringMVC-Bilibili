package cn.chen.ssm.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.stereotype.Controller;
import cn.chen.ssm.po.Items;

/**
 * @author Chen
 * Description: ʵ��ע���Handler
 *
 */
//ʹ��Controllerע���ʶ����һ��������
@Controller
public class ItemsController3{
	
	//��Ʒ��ѯ�б�
	//@RequestMappingʵ�ֶ�queryItems������url����ӳ�䣬һ��������Ӧһ��url
	//һ�㽨�齫url�ͷ��������һ��
	@RequestMapping("/queryItems")
	public ModelAndView queryItems() {
		
		//����service�������ݿ⣬��ѯ��Ʒ�б��˴����þ�̬����ģ��
		List<Items> itemsList = new ArrayList<Items>();
		//��list����侲̬����
		Items items_1 = new Items();
		items_1.setName("����ʼǱ�");
		items_1.setPrice(6000f);
		items_1.setDetail("ThinkPad T430 ����ʼǱ����ԣ�");
		
		Items items_2 = new Items();
		items_2.setName("ƻ���ֻ�");
		items_2.setPrice(5000f);
		items_2.setDetail("iphone6ƻ���ֻ���");
		
		itemsList.add(items_1);
		itemsList.add(items_2);
		
		//����ModelAndView
		ModelAndView modelAndView = new ModelAndView();
		//�൱��request��setAttribut����jsp��ͨ��itemsListȡ����
		modelAndView.addObject("itemsList",itemsList);
		//ָ����ͼ
		modelAndView.setViewName("/WEB-INF/jsp/items/itemsList.jsp");
		
		//����ModelAndView
		return modelAndView;
	}
	
	//�˴����Զ�����������
	//��Ʒ���
	//��Ʒ�޸�
}
