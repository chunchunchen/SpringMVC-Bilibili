> 注：本文档是在学习SpringMVC的哔哩哔哩视频课程(av18288362)时做的。

[TOC]

# 一、SpringMVC框架

## 1.1 什么是SpringMVC？

springmvc是spring框架的一个模块，springmvc和spring无需通过中间整合层进行整合。

springmvc是一个基于mvc的web框架。

![image-20200206095010000](C:\Users\Chen\AppData\Roaming\Typora\typora-user-images\image-20200206095010000.png)

## 1.2 MVC在B/S系统下的应用

mvc是一个设计模式，mvc在b/s系统 下的应用:

![img](file:///C:\Users\Chen\AppData\Local\Temp\ksohtml17164\wps1.png)

## 1.3 SpringMVC框架

![img](file:///C:\Users\Chen\AppData\Local\Temp\ksohtml17164\wps2.png)

1. 发起请求到前端控制器(DispatcherServlet)。
2. 前端控制器请求HandlerMapping查找 Handler。可以根据xml配置、注解进行查找。
3. 处理器映射器HandlerMapping向前端控制器返回Handler。
4. 前端控制器调用处理器适配器去执行Handler。
5. 处理器适配器去执行Handler。
6. Handler执行完成给适配器返回ModelAndView。
7. 处理器适配器向前端控制器返回ModelAndView。ModelAndView是springmvc框架的一个底层对象，包括 Model和view。
8. 前端控制器请求视图解析器去进行视图解析。根据逻辑视图名解析成真正的视图(jsp)。
9. 视图解析器向前端控制器返回View。
10. 前端控制器进行视图渲染。视图渲染将模型数据(在ModelAndView对象中)填充到request域。
11. 前端控制器向用户响应结果 。

> **组件**

1、前端控制器DispatcherServlet（不需要程序员开发）

​       作用是接收请求，响应结果，相当于转发器，中央处理器。有了DispatcherServlet减少了其它组件之间的耦合度。

2、处理器映射器HandlerMapping(不需要程序员开发)

​        作用：根据请求的url查找Handler

3、处理器适配器HandlerAdapter

​       作用：按照特定规则（HandlerAdapter要求的规则）去执行Handler

4、**处理器Handler(==需要程序员开发==)**

​        注意：编写Handler时按照HandlerAdapter的要求去做，这样适配器才可以去正确执行Handler

5、视图解析器View resolver(不需要程序员开发)

​        作用：进行视图解析，根据逻辑视图名解析成真正的视图（view）

6、**视图View(==需要程序员开发jsp==)**

​        View是一个接口，实现类支持不同的View类型（jsp、freemarker、pdf...）

# 二、入门程序

## 2.1 需求

以案例作为需求，使用商品订单管理的案例。功能需求是，商品列表查询。

## 2.2 开发环境

数据库版本：mysql 8.0.12

java版本：jdk 1.8.0_181

​                  eclipse Release 4.8.0 (Photon)

SpringMVC版本：3.2

​                 需要spring3.2所有jar（一定包括spring-webmvc-3.2.0.RELEASE.jar）

## 2.3 配置前端控制器

新建dynamic webproject，导入spring包。新建config目录（source folder），该目录下新建springmvc.xml配置文件（该配置文件需要在init-param中使用）。配置web.xml，关键代码如下

```xml
<!-- springmvc前端控制器 -->
  <servlet>
  	<servlet-name>springmvc</servlet-name>
  	<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
  	<!-- 注意，如果上面的配置加了.class配成DispatcherServlet.class，就会报错！切记！ -->
  	<!-- contextConfigLocation配置SpringMVC加载的配置文件（配置处理器映射器、适配器等）
  	如果不配置contextConfigLocation，默认加载的是/WEB-INF/servlet名称-servlet.xml（此处即springmvc-servlet.xml） -->
  	<init-param>
  		<param-name>contextConfigLocation</param-name>
  		<param-value>classpath:springmvc.xml</param-value>
  	</init-param>
  </servlet>
  <servlet-mapping>
  	<servlet-name>springmvc</servlet-name>
  	<!-- ulr-pattern有三种配置:
  	第一种:*。action，访问以.action结尾的url时用DispatcherServlet解析
  	第二种:/,所有访问的地址都由DispatcherServlet解析，对于静态文件的解析需要配置不让DispatcherServlet进行解析
  	使用此种方法可以实现RESTful风格的url
  	第三种:/*,这样配置不对，使用这种配置，最终要转发到一个jsp页面时，仍然会由DispatcherServlet解析jsp，不能根据jsp页面找到handler,会报错
  	 -->
  	<url-pattern>*.action</url-pattern>
  </servlet-mapping>
```

## 2.4 配置处理器适配器

在2.3中新建的classpath下的springmvc.xml中配置处理器适配器。关键代码如下

```xml
<!-- 处理器适配器
	所有的处理器适配器都实现HandlerAdapter接口 -->
<bean class="org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter"/>
```

------

> > 源码分析
>
> 查看spring-webmvc-3.2.0.RELEASE.jar中的类org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter.class，部分代码如下：
>
> ```java
> public class SimpleControllerHandlerAdapter implements HandlerAdapter {
> 
> 	public boolean supports(Object handler) {
> 		return (handler instanceof Controller);
> 	}
>     ...
> ```
>
> 此适配器能执行实现Controller接口的Handler。再点开上面的Controller代码
>
> ```java
> public interface Controller {
> 
> 	/**
> 	 * Process the request and return a ModelAndView object which the DispatcherServlet
> 	 * will render. A <code>null</code> return value is not an error: It indicates that
> 	 * this object completed request processing itself, thus there is no ModelAndView
> 	 * to render.
> 	 * @param request current HTTP request
> 	 * @param response current HTTP response
> 	 * @return a ModelAndView to render, or <code>null</code> if handled directly
> 	 * @throws Exception in case of errors
> 	 */
> 	ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;
> 
> }
> ```
>
> 发现Controller中只有一个方法，返回的是一个ModelAndView。

------



## 2.5 开发Handler

2.4中已经配置了HandlerAdapter，它(即SimpleControllerHandlerAdapter)能执行实现了Controller接口的Handler，正好趁热打铁编写Handler。在工程的src下新建包cn.chen.ssm.controller，包下新建类ItemsController1.java。关键代码如下

```java
public class ItemsController1 implements Controller{

	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//调用service查找数据库，查询商品列表。此处先用静态数据模拟
		List<Items> itemsList = new ArrayList<Items>();
		//向list中填充静态数据
		...	
		//返回ModelAndView
		ModelAndView modelAndView = new ModelAndView();
		//addObject相当于request的setAttribut方法，在jsp页面中通过itemsList取数据
		modelAndView.addObject("itemsList", itemsList);
		
		//指定视图
		modelAndView.setViewName("/WEB-INF/jsp/items/itemsList.jsp");
		
		return modelAndView;
	}
	
}
```

> - [x] ItemsController1最后需要返回一个视图，因此需要先在WEB-INF下新建jsp文件夹，在jsp文件夹下新建items子文件夹，新建一个jsp文件itemsList.jsp。
>
>   *若jsp文件报错javax.servlet.jsp.JspException cannot be resolved to a type，将tomcat/lib/下的jsp-api.jar拷到项目的lib里面。*
>
> - [x] 上述代码中使用了pojo对象，需要新建包cn.chen.ssm.po，新建数据库表对应的四个类，例如Items.java部分代码如下
>
> ```java
> public class Items {
>     private Integer id;
>     private String name;
>     public Integer getId() {
>         return id;
>     }
>     public void setId(Integer id) {
>         this.id = id;
>     }
>     public String getName() {
>         return name;
>     }
>     public void setName(String name) {
>         this.name = name == null ? null : name.trim();
>     }
>     ...
> }
> ```

## 2.6 视图编写

在2.5中，已经编写了视图的jsp。部分代码如下

```jsp
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>查询商品列表</title>
</head>
<body> 
<form action="${pageContext.request.contextPath }/item/queryItem.action" method="post">
查询条件：
<table width="100%" border=1>
<tr>
<td><input type="submit" value="查询"/></td>
</tr>
</table>
商品列表：
<table width="100%" border=1>
<tr>
	<td>商品名称</td>
	<td>商品价格</td>
	<td>生产日期</td>
	<td>商品描述</td>
	<td>操作</td>
</tr>
<c:forEach items="${itemsList }" var="item">
<tr>
	<td>${item.name }</td>
	<td>${item.price }</td>
	<td><fmt:formatDate value="${item.createtime}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
	<td>${item.detail }</td>
	
	<td><a href="${pageContext.request.contextPath }/item/editItem.action?id=${item.id}">修改</a></td>

</tr>
</c:forEach>

</table>
</form>
</body>

</html>
```



## 2.7 配置Handler

在2.3中新建的classpath下的springmvc.xml中配置Handler。关键代码如下

```xml
<!-- 配置Handler -->
	<bean name="/queryItems.action"  class="cn.chen.ssm.controller.ItemsController1"/>
<!--  上面的配置如果是直接在controller类上右键复制类名，去掉头尾是cn/chen/ssm/controller/ItemsController1，
		如果直接把这个贴到class里面就会报错，一定记得改成是.分割的cn.chen.ssm.controller.ItemsController1-->
```



## 2.8 配置处理器映射器

在2.3中新建的classpath下的springmvc.xml中配置处理器映射器。关键代码如下

```xml
<!-- 处理器映射器
     将bean的name作为url进行查找，需要在配置Handler时指定beanname(就是url) -->
<bean 
      class="org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping"/>
```



## 2.9 配置视图解析器

配置解析JSP的视图解析器，关键代码如下

```xml
<!-- 视图解析器 
	解析jsp视图，默认使用jstl标签，classpath下得有jstl的包-->
	<bean 
		class="org.springframework.web.servlet.view.InternalResourceViewResolver" />
```



## 2.10 部署调试

访问地址http://localhost:8080/springmvcfirst20200205/queryItems.action。

> - 报错啦，网页端先报500，刷新后报404，具体如下图，
>
> <img src="C:\Users\Chen\AppData\Roaming\Typora\typora-user-images\image-20200206155053099.png" alt="image-20200206155053099" style="zoom: 80%;" />
>
> <img src="C:\Users\Chen\AppData\Roaming\Typora\typora-user-images\image-20200206155113835.png" alt="image-20200206155113835" style="zoom:80%;" />
>
> 经查，报错提示是.DispatcherServlet.class这个类无法实例化(*Error instantiating servlet class [org.springframework.web.servlet.DispatcherServlet.class]*)，排查发现是在配置前端控制器时，servlet-class配置错了，末尾多加了.class，错误配置为
>
> ```xml
> <servlet-class>org.springframework.web.servlet.DispatcherServlet.class</servlet-class>
> ```
>
> 正确配置为
>
> ```xml
> <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
> ```
>
> - 解决完上述问题后再运行，还是报错500
>
>   <img src="C:\Users\Chen\AppData\Roaming\Typora\typora-user-images\image-20200206161809009.png" alt="image-20200206161809009" style="zoom:80%;" />
>
>   <img src="C:\Users\Chen\AppData\Roaming\Typora\typora-user-images\image-20200206161838984.png" alt="image-20200206161838984" style="zoom:80%;" />
>
> 注意到其中一句
>
> ```java
> org.springframework.beans.factory.CannotLoadBeanClassException: Error loading class [cn/chen/ssm/controller/ItemsController1] for bean with name '/queryItems.action' defined in class path resource [springmvc.xml]: problem with class file or dependent class; nested exception is java.lang.NoClassDefFoundError: IllegalName: cn/chen/ssm/controller/ItemsController1
> ```
>
> *IllegalName: cn/chen/ssm/controller/ItemsController1*即指controller的名字不合法，仔细检查后发现，配置Handler时，类名确实写错了，错误配置为
>
> ```xml
> <bean name="/queryItems.action" class="cn/chen/ssm/controller/ItemsController1"/>
> ```
>
> 正确配置为
>
> ```xml
> <bean name="/queryItems.action" class="cn.chen.ssm.controller.ItemsController1"/>
> ```

修改完上述bug后，把tomcat/webapps下的项目删了重新publish一下再启动，就可以看到效果啦

![image-20200206163342835](C:\Users\Chen\AppData\Roaming\Typora\typora-user-images\image-20200206163342835.png)

