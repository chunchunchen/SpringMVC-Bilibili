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

本节先用==非注解==的方式实现。

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
  	第一种:*.action，访问以.action结尾的url时用DispatcherServlet解析
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



## 2.7 配置Handler(处理器)

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

------

遇到了一下问题，记录如下。

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

------

修改完上述bug后，把tomcat/webapps下的项目删了重新publish一下再启动，就可以看到效果啦

![image-20200209172656538](C:\Users\Chen\AppData\Roaming\Typora\typora-user-images\image-20200209172656538.png)

==***到此为止对应的码云地址为https://github.com/chunchunchen/SpringMVC-Bilibili.git，版本为e469efa8d5131a0c3a9d48d1ec6e234f80c80873​***==

------

> 此外，老师还讲了两种错误。一种是地址栏url输错了，把localhost:8080/springmvcfirst20200205/queryItems.action错输成了localhost:8080/springmvcfirst20200205/queryItemss.action，报错为
>
> <img src="C:\Users\Chen\AppData\Roaming\Typora\typora-user-images\image-20200206171114221.png" alt="image-20200206171114221" style="zoom:80%;" />
>
> 这是因为处理器映射器根据url找不到Handler。还有一种错误是springmvc.xml中配置和url输入的都是/queryItems.action，但是在controller里最后返回的viewname设置错了为itemsLists.jsp与jsp文件名不一致，则报错为
>
> <img src="C:\Users\Chen\AppData\Roaming\Typora\typora-user-images\image-20200206171836145.png" alt="image-20200206171836145" style="zoom:80%;" />
>
> 这是因为处理器映射器根据url找到了Handler，但是转发的jsp页面找不到，说明jsp页面地址错了。



# 三、 非注解的处理器映射器和适配器

## 3.1 非注解的处理器映射器

上面2.8用到的处理器映射器配置为

```xml
<bean class="org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping"/>
```

此外还有另一种映射器

```xml
<!--简单url映射  -->
	<bean class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
		<property name="mappings">
			<props>
				<!-- 对itemsController1进行url映射，url是/queryItems1.action -->
				<prop key="/queryItems1.action">itemsController1</prop>
				<prop key="/queryItems2.action">itemsController1</prop>
			</props>
		</property>
	</bean>
```

需要注意的是，上述配置中，prop里面配置的是controller的bean id，因此需要将2.7中Handler的配置加上id

```xml
<!-- 配置Handler -->
	<bean id="itemsController1" name="/queryItems.action"  class="cn.chen.ssm.controller.ItemsController1"/>
```

> 两种配置可以同时存在，都可以访问。如果上面Handler配置里去掉了name配置，则只能通过两个key指定的url来访问。

多个映射器可以并存，前端控制器会判断url，能让哪些映射器映射，就让正确的映射器处理。

## 3.2 非注解的处理器适配器

上面2.4节用到的处理器适配器为

```xml
<!-- 处理器适配器
	所有的处理器适配器都实现HandlerAdapter接口 -->
<bean class="org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter"/>
```

通过前面的源码分析我们知道，它要求编写的Handler实现Controller接口。

此外还有另一种非注解的适配器

```xml
<!-- 另一个非注解的适配器 -->
	<bean class="org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter"/>
```

> > 源码分析
>
> 查看源码,部分代码如下
>
> ```java
> public class HttpRequestHandlerAdapter implements HandlerAdapter {
> 
> 	public boolean supports(Object handler) {
> 		return (handler instanceof HttpRequestHandler);
> 	}
>     ...
> ```
>
> 再点开HttpRequestHandler接口，可以看出里面只有一个方法
>
> ```java
> public interface HttpRequestHandler {
> 
> 	/**
> 	 * Process the given request, generating a response.
> 	 * @param request current HTTP request
> 	 * @param response current HTTP response
> 	 * @throws ServletException in case of general errors
> 	 * @throws IOException in case of I/O errors
> 	 */
> 	void handleRequest(HttpServletRequest request, HttpServletResponse response)
> 			throws ServletException, IOException;
> 
> }
> ```
>
> 从上面的源码中，可以看出它要求编写的Handler实现HttpRequestHandler接口，里面包含一个方法。

下面编写它。新建一个ItemsController2.java，关键代码如下

```java
public class ItemsController2 implements HttpRequestHandler{
	public void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//调用service查找数据库，查询商品列表。此处先用静态数据模拟
		List<Items> itemsList = new ArrayList<Items>();
		//向list中填充静态数据
		...略
		//设置模型数据
		request.setAttribute("itemsList", itemsList);
		//设置转发的视图
		request.getRequestDispatcher("/WEB-INF/jsp/items/itemsList.jsp").forward(request, response);
	}
}
```

> 从上边可以看出此适配器器的handleRequest方法没有返回ModelAndView，可通过response修改设置响应内容，比如返回json数据：
>
> response.setCharacterEncoding("utf-8");
>
> response.setContentType("application/json;charset=utf-8");
>
> response.getWriter().write("json串");

Handler开发完以后，需要将对应的Handler配置和映射器配置都配进去，这里采用上面第二种方法

```xml
<!-- 配置另外一个Handler -->
	<bean id="itemsController2" class="cn.chen.ssm.controller.ItemsController2" />
	<!--简单url映射  -->
	<bean class="org.springframework.web.servlet.handler.SimpleUrlHandlerMapping">
		<property name="mappings">
			<props>
				<!-- 对itemsController1进行url映射，url是/queryItems1.action -->
				<prop key="/queryItems3.action">itemsController2</prop>
			</props>
		</property>
	</bean>
```

配置完后启动调试，访问http://localhost:8080/springmvcfirst20200205/queryItems3.action即可看到效果。

![image-20200209172609143](C:\Users\Chen\AppData\Roaming\Typora\typora-user-images\image-20200209172609143.png)

==到此为止，实现了第二种配置下的访问效果。上传代码到GitHub，地址https://github.com/chunchunchen/SpringMVC-Bilibili.git，版本为9316c4d0f8c6ecfe9002956e1aecff98eb138619。==

# 四、DispatcherServlet.properties

需要注意的一点是，即使将springmvc.xml中处理器映射器HandlerMapping和处理器适配器HandlerAdapter删掉，项目依然可以正常运行！这是因为，前端控制器DispatcherServlet提供一个默认的配置文件，位置在spring-webmvc-3.2.0.RELEASE.jar源代码的org.springframework.web.servlet包中，有一个DispatcherServlet.properties，部分代码如下

```properties
...
org.springframework.web.servlet.HandlerMapping=org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping,\
	org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping

org.springframework.web.servlet.HandlerAdapter=org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter,\
	org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter,\
	org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter
...
```

可以看出，里面提供了几种默认的配置，以逗号分隔。其中上面的org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping和org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter是在spring3.1之前使用的默认的处理器映射器和适配器，因此如果采用注解的开发，并且不在springmvc.xml中显示配置的话，就会使用默认的过期的映射器和适配器。

> > 源码分析
>
> org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping.class部分代码如下
>
> ```class
>  * @since 2.5
>  * @see RequestMapping
>  * @see AnnotationMethodHandlerAdapter
>  *
>  * @deprecated in Spring 3.2 in favor of
>  * {@link org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping RequestMappingHandlerMapping}
>  */
> ```
>
> 可以看出从spring2.5以后开始使用，而在spring3.2以后推荐用RequestMappingHandlerMapping。点开org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping.class源码
>
> ```class
>  * @author Arjen Poutsma
>  * @author Rossen Stoyanchev
>  * @since 3.1
>  */
> ```
>
> 可以看到从spring3.1以后开始使用这种注解的开发配置。

# 五、 注解的处理器映射器和适配器

在第四节中我们知道了，对于注解模式的开发，在spring3.1之前使用org.springframework.web.servlet.mvc.annotation.DefaultAnnotationHandlerMapping注解映射器。

在spring3.1之后使用org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping注解映射器。

在spring3.1之前使用org.springframework.web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter注解适配器。

在spring3.1之后使用org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter注解适配器。

## 5.1 配置注解的处理器映射器和适配器

注解映射器的一种配置方式如下

```xml
<!--注解映射器 -->
<bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping"/>
<!--注解适配器 -->
<bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter"/>
```

此外还有另一种方式

```xml
<!-- 使用 mvc:annotation-driven可以代替上边注解映射器和注解适配器配置。
	mvc:annotation-driven默认加载很多的参数绑定方法，
	比如json转换解析器就默认加载了，如果使用mvc:annotation-driven则不用配置上边的RequestMappingHandlerMapping和RequestMappingHandlerAdapter。
	实际开发时使用mvc:annotation-driven
	 -->
<mvc:annotation-driven></mvc:annotation-driven>
```

## 5.2 开发注解Handler

使用注解的映射器和注解的适配器（注解的映射器和注解的适配器要配对使用）。新建一个ItemsController3.java，关键代码如下（关注一下两个注解用法）

```java
@Controller
public class ItemsController3{
	
	//商品查询列表
	//@RequestMapping实现对queryItems方法和url进行映射，一个方法对应一个url
	//一般建议将url和方法名设成一样
	@RequestMapping("/queryItems")
	public ModelAndView queryItems() {
		
		//调用service查找数据库，查询商品列表。此处先用静态数据模拟
		List<Items> itemsList = new ArrayList<Items>();
		//向list中填充静态数据
		...		
		//返回ModelAndView
		ModelAndView modelAndView = new ModelAndView();
		//相当于request的setAttribut，在jsp中通过itemsList取数据
		modelAndView.addObject("itemsList",itemsList);
		//指定视图
		modelAndView.setViewName("/WEB-INF/jsp/items/itemsList.jsp");
		
		//返回ModelAndView
		return modelAndView;
	}
	
	//此处可以定义其它方法
	//商品添加
	//商品修改
}
```

> 注：由于这里写的url为@RequestMapping("/queryItems")，为了区别，将原来的第一种Handler配置改成下面（加一个_test）
>
> ```xml
> <bean id="itemsController1" name="/queryItems_test.action" class="cn.chen.ssm.controller.ItemsController1"/>
> ```



## 5.3 在Spring容器中加载Handler

配置Handler可以单独配置

```xml
<bean class="cn.itcast.ssm.controller.ItemsController3" />
```

但是如果Handler很多，一个一个配置就很麻烦，因此可以用下面注解扫描的方式

```xml
<!-- 可以扫描controller、service、...
这里让扫描controller，指定controller的包。
扫描controller注解,多个包中间使用半角逗号分隔
 -->
<context:component-scan base-package="cn.chen.ssm.controller"></context:component-scan>
```

在bask-package指定的包下，自动扫描标记@controller的控制器类。

## 5.4 部署调试

启动后访问http://localhost:8080/springmvcfirst20200205/queryItems.action，发现报错了

![image-20200209211507493](C:\Users\Chen\AppData\Roaming\Typora\typora-user-images\image-20200209211507493.png)

其中关键错误信息为

```java
java.lang.IllegalArgumentException
	at org.springframework.asm.ClassReader.<init>(Unknown Source)
	at org.springframework.asm.ClassReader.<init>(Unknown Source)
	at org.springframework.asm.ClassReader.<init>(Unknown Source)
```

查了下资料，发现是由于spring版本与Java版本以对应导致的。这个地方需要记住两点 

- spring 3.X版本支持到java7 
- spring 4.X版本支持Java8最低支持到Java6 

[参考链接]: https://blog.csdn.net/yangjiabei_0301/article/details/78247193

将eclipse中的java环境和tomcat环境都换成1.7

<img src="C:\Users\Chen\AppData\Roaming\Typora\typora-user-images\image-20200209212159964.png" alt="image-20200209212159964" style="zoom:80%;" />

<img src="C:\Users\Chen\AppData\Roaming\Typora\typora-user-images\image-20200209212256103.png" alt="image-20200209212256103" style="zoom:80%;" />

再次启动tomcat访问，发现可以了

![image-20200209212411639](C:\Users\Chen\AppData\Roaming\Typora\typora-user-images\image-20200209212411639.png)

**==至此注解情况下的开发访问也好了，上传GitHub地址https://github.com/chunchunchen/SpringMVC-Bilibili.git，版本为c7bff6ebfdfaf28ca33f5b9896af76f00cdd7d9a==**

# 六、 源码分析

通过前端控制器源码，分析SpringMVC的执行过程。

第一步：前端控制器接收请求，调用doDispatch

(文件位置org.springframework.web.servlet.DispatcherServlet.class，可从web.xml中点进来)

```java
protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpServletRequest processedRequest = request;
		HandlerExecutionChain mappedHandler = null;
		boolean multipartRequestParsed = false;
    ...
```

第二步：前端控制器调用处理器映射器查找Handler

```java
// Determine handler for the current request.
				mappedHandler = getHandler(processedRequest, false);
...
...
    
    protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
		for (HandlerMapping hm : this.handlerMappings) {
			if (logger.isTraceEnabled()) {
				logger.trace(
						"Testing handler map [" + hm + "] in DispatcherServlet with name '" + getServletName() + "'");
			}
			HandlerExecutionChain handler = hm.getHandler(request);
			if (handler != null) {
				return handler;
			}
		}
		return null;
	}
```

第三步，调用处理器适配器执行Handler，得到执行结果ModelAndView

```java
try {
					// Actually invoke the handler.
					mv = ha.handle(processedRequest, response, mappedHandler.getHandler());
				}
```

第四步，视图渲染，将model数据填充到request域。

视图解析，得到View:

```java
render(mv, request, response);
...
    //点进去render方法
    // We need to resolve the view name.
			view = resolveViewName(mv.getViewName(), mv.getModelInternal(), locale, request);
```

调用view的渲染方法

```java
view.render(mv.getModelInternal(), request, response);
```

其中调用

```java
renderMergedOutputModel(mergedModel, request, response);
```

对于jsp的解析器InternalResourceViewResolver调用

```java
// Expose the model object as request attributes.
		exposeModelAsRequestAttributes(model, requestToExpose);
```

对应的方法实现为

```java
protected void exposeModelAsRequestAttributes(Map<String, Object> model, HttpServletRequest request) throws Exception {
		for (Map.Entry<String, Object> entry : model.entrySet()) {
			String modelName = entry.getKey();
			Object modelValue = entry.getValue();
			if (modelValue != null) {
				request.setAttribute(modelName, modelValue);
				if (logger.isDebugEnabled()) {
					logger.debug("Added model object '" + modelName + "' of type [" + modelValue.getClass().getName() +
							"] to request in view with name '" + getBeanName() + "'");
				}
			}
			else {
				request.removeAttribute(modelName);
				if (logger.isDebugEnabled()) {
					logger.debug("Removed model object '" + modelName +
							"' from request in view with name '" + getBeanName() + "'");
				}
			}
		}
	}
```

可以看出将model作为map遍历，用setAttribute方法一个一个填入request中。



# 七、 入门程序小结

通过入门程序理解SpringMVC的前端控制器、**处理器映射器、处理器适配器**、视图解析器。

- 前端控制器配置(主要是url的pattern配置):

```xml
第一种:*。action，访问以.action结尾的url时用DispatcherServlet解析
第二种:/,所有访问的地址都由DispatcherServlet解析，对于静态文件的解析需要配置不让DispatcherServlet进行解析。使用此种方法可以实现RESTful风格的url
```

- 处理器映射器：

- 非注解处理器映射器(了解):

- 注解处理器映射器(掌握):

  对标记@Controller注解的类中，标识有@RequestMapping的方法进行映射。在@RequestMapping里面定义映射的url。因此使用注解的映射器不用在xml中配置url和Handler的映射关系。

- 处理器适配器

- 非注解处理器适配器(了解)

- 注解的处理器适配器(掌握)

  注解的处理器适配器和注解的处理器映射器是配对使用的。即不能使用非注解的处理器映射器进行映射。

- mvc的注解驱动

  ```xml
  <mvc:annotation-driven></mvc:annotation-driven>
  ```

  可以代替下面的配置

  ```xml
  <!--注解映射器 -->
  <bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping"/>
  <!--注解适配器 -->
  <bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter"/>
  ```

  实际开发中使用mvc:annotation-driven。

- 视图解析器配置前缀和后缀

  ```xml
  <!-- 视图解析器
  	解析jsp解析，默认使用jstl标签，classpath下的得有jstl的包
  	 -->
  	<bean
  		class="org.springframework.web.servlet.view.InternalResourceViewResolver">
  		<!-- 配置jsp路径的前缀 -->
  		<property name="prefix" value="/WEB-INF/jsp/"/>
  		<!-- 配置jsp路径的后缀 -->
  		<property name="suffix" value=".jsp"/>
  	</bean>
  ```

  上面的前后缀相当于配置了jsp文件的路径前缀和后缀名，因此程序中就可以只关注中间的文件部分，如果配置了前后缀，相应的ItemsController3可以改成下面的代码

  ```java
  //指定视图
  		//下边的路径，如果在视图解析器中配置jsp路径的前缀和jsp路径的后缀，
  		//modelAndView.setViewName("/WEB-INF/jsp/items/itemsList.jsp");
  		//则上边的路径配置可以不在程序中指定jsp路径的前缀和jsp路径的后缀
  		modelAndView.setViewName("items/itemsList");
  ```

==**上传GitHub地址https://github.com/chunchunchen/SpringMVC-Bilibili.git，版本为4cb916a33166e6aa4198b7708ce59ab082ce4385**==

# 八、SpringMVC和MyBatis整合

## 8.1 需求

使用SpringMVC和MyBatis完成商品列表查询。

## 8.2 整合思路

SpringMVC + MyBatis的系统架构

![img](file:///C:\Users\Chen\AppData\Local\Temp\ksohtml20020\wps1.png)

1. 第一步：整合dao层

   ​	MyBatis和spring整合，通过spring管理mapper接口。

   ​	使用mapper的扫描器自动扫描mapper接口在spring中进行注册。

2. 第二步：整合service层

   ​	通过spring管理service接口。

   ​	使用配置方式将service接口配置在spring配置文件中。

   ​	实现事务控制。	

3. 第三步：整合SpringMVC

   ​	由于SpringMVC是spring的模块，不需要整合。

   

## 8.3 环境准备

数据库版本：mysql 8.0.12

java版本：jdk 1.7.0_80  (注意这里把jdk换成1.7了)

​                  eclipse Release 4.8.0 (Photon)

SpringMVC版本：3.2

所需要的jar包：

​                   数据库驱动包

​                   MyBatis的jar包

​                   MyBatis和spring整合包

​                   log4j包

​                   dbcp数据库连接池包

​                   spring3.2所有jar

​                   jstl包

1. 新建工程，导入复制jar包
2. 新建资源文件夹
3. 新建properties文件
4. 新建包

工程目录如下：

![image-20200213232849151](C:\Users\Chen\AppData\Roaming\Typora\typora-user-images\image-20200213232849151.png)

## 8.4 整合dao

MyBatis和Spring进行整合。

### 8.4.1 SQLMapConfig.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
	
	<!-- 全局setting配置，根据需要添加 -->
	
	<!-- 配置别名 -->
	<typeAliases>
		<!-- 批量扫描别名 -->
		<package name="cn.itcast.ssm.po"/>
	</typeAliases>

	<!-- 配置mapper
	由于使用spring和mybatis的整合包进行mapper扫描，这里不需要配置了。
	必须遵循：mapper.xml和mapper.java文件同名且在一个目录 
	 -->

	<!-- <mappers>
	
	</mappers> -->
</configuration>
```



### 8.4.2 applicationContext-dao.xml

配置：

数据源

SqlSessionFactory

mapper扫描器

关键内容如下：

```xml
<!-- 加载db.properties文件中的内容，db.properties文件中key命名要有一定的特殊规则 -->
	<context:property-placeholder location="classpath:db.properties" />
	<!-- 配置数据源 ，dbcp -->

	<bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource"
		destroy-method="close">
		<property name="driverClassName" value="${jdbc.driver}" />
		<property name="url" value="${jdbc.url}" />
		<property name="username" value="${jdbc.username}" />
		<property name="password" value="${jdbc.password}" />
		<property name="maxActive" value="30" />
		<property name="maxIdle" value="5" />
	</bean>
	<!-- sqlSessionFactory -->
	<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
		<!-- 数据库连接池 -->
		<property name="dataSource" ref="dataSource" />
		<!-- 加载mybatis的全局配置文件 -->
		<property name="configLocation" value="classpath:mybatis/sqlMapConfig.xml" />
	</bean>
	<!-- mapper扫描器 -->
	<bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
		<!-- 扫描包路径，如果需要扫描多个包，中间使用半角逗号隔开 -->
		<property name="basePackage" value="cn.itcast.ssm.mapper"></property>
		<property name="sqlSessionFactoryBeanName" value="sqlSessionFactory" />
	</bean>
```

### 8.4.3 逆向工程生成po类及mapper(单表增删改查)

![image-20200301143727693](C:\Users\Chen\AppData\Roaming\Typora\typora-user-images\image-20200301143727693.png)

逆向工程百度一下吧，将生成的包拷贝进来。

### 8.4.4 手动定义商品查询mapper

针对综合查询mapper，一般情况会有关联查询，建议自定义mapper

#### 8.4.4.1 ItemsMapperCustom.xml

sql语句

SELECT * FROM items where items.name like '%笔记本%';

```xml
<mapper namespace="cn.chen.ssm.mapper.ItemsMapperCustom" >
	
	<!-- 定义商品查询的sql片段，这就是商品查询条件 -->
	<sql id="query_items_where">
		<!-- 使用动态sql，进行if判断，满足条件进行sql拼接 -->
		<!-- 商品查询条件通过ItemsQueryVo包装对象中itemsCustom属性传递 -->
		<if test="itemsCustom != null">
			<if test="itemsCustom.name!=null and itemsCustom.name!=''">
				items.name like '${itemsCustom.name}%';
			</if>
		</if>
	</sql>	  
	<!-- 商品列表查询 -->
	<!-- parameterType传入包装对象
			resultType建议使用扩展对象
	-->
	<select id="findItemsList" parameterType="cn.chen.ssm.po.ItemsQueryVo" 
			resultType="cn.chen.ssm.po.ItemsCustom">
		SELECT * FROM items 
		<where>
  			<include refid="query_items_where"></include>
  		</where>
	</select>
</mapper>
```

在这一步中，parameterType是传入对象，它包含items对象和一个扩展对象

```java
	//商品信息
	private Items items;
	
	//为了系统可扩展性，对原始 生成的po进行扩展
	private ItemsCustom itemsCustom;
```

其中扩展对象ItemsCustom为Items类的继承，以便将来扩展

```java
package cn.chen.ssm.po;
//商品信息的扩展类
public class ItemsCustom extends Items {

}
```

同时，ItemsCustom也是查询的返回resultType。

#### 8.4.4.2 ItemsMapperCustom.java

```java
public interface ItemsMapperCustom {
	//商品查询列表
	public List<ItemsCustom> findItemsList(ItemsQueryVo itemsQueryVo) throws Exception;
}
```

## 8.5 整合service

让Spring管理service接口。

### 8.5.1 定义service接口

新建一个包cn.chen.ssm.service，在里面定义service类ItemsService.java

```java
public interface ItemsService {
	//商品查询列表
	public List<ItemsCustom> findItemsList(ItemsQueryVo itemsQueryVo)throws Exception;
	
}
```

在cn.chen.ssm.service.impl中实现这个方

```java
//商品管理
public class ItemsServiceImpl implements ItemsService{

	@Autowired
	private ItemsMapperCustom itemsMapperCustom;
	public List<ItemsCustom> findItemsList(ItemsQueryVo itemsQueryVo) throws Exception {
		//通过ItemsMapperCustom查询数据库
		return itemsMapperCustom.findItemsList(itemsQueryVo);
	}
}
```

上面，由于ItemsMapperCustom.java和ItemsMapperCustom.xml同名且在同一个目录，所以在SQLMapConfig.xml中配置的mapper扫描器已经将ItemsMapperCustom加载到了Spring容器，这里只需要用@Autowired注入即可使用。

### 8.5.2 在Spring容器中配置service(applicationContext-service.xml)

创建applicationContext-service.xml，文件中配置service。

```xml
<!-- 商品管理的service -->
<bean id="itemsService" class="cn.chen.ssm.service.impl.ItemsServiceImpl"/>
```

### 8.5.3 事务控制(applicationContext-transaction.xml)

```xml
<!-- 事务管理器
	对MyBatis操作数据库的事务控制，Spring使用jdbc的事务控制类
 -->
<bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
 	<!-- 数据源
 	dataSource在applicationCont-dao.xml中配置了 
 	-->
 	<property name="dataSource" ref="dataSource"></property>
</bean>
<!-- 通知 -->
<tx:advice id="txAdvice" transaction-manager="transactionManager">
	<tx:attributes>
		<!-- 传播行为 -->
		<tx:method name="save*" propagation="REQUIRED"/>
		<tx:method name="delete*" propagation="REQUIRED"/>
		<tx:method name="insert*" propagation="REQUIRED"/>
		<tx:method name="update*" propagation="REQUIRED"/>
		<tx:method name="find*" propagation="SUPPORTS" read-only="true"/>
		<tx:method name="get*" propagation="SUPPORTS" read-only="true"/>
		<tx:method name="select*" propagation="SUPPORTS" read-only="true"/>
	</tx:attributes>
</tx:advice>
<!-- 配置aop切点 -->
<aop:config>
	<aop:advisor advice-ref="txAdvice" pointcut="execution(* cn.chen.ssm.service.impl.*.*(..))"/>
</aop:config>
```

## 8.6 整合SpringMVC

### 8.6.1 配置springmvc.xml

创建springmvc.xml文件，配置处理器映射器、适配器、视图解析器

```xml
	<!-- 可以扫描controller、service、...
	这里让扫描controller，指定controller的包
	 -->
	<context:component-scan base-package="cn.chen.ssm.controller"></context:component-scan>
	
	<!--注解映射器 -->
	<!-- 	<bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping"/> -->
	<!--注解适配器 -->
	<!-- 	<bean class="org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter"/> -->
	<!-- 使用 mvc:annotation-driven代替上边注解映射器和注解适配器配置
	mvc:annotation-driven默认加载很多的参数绑定方法，
	比如json转换解析器就默认加载了，如果使用mvc:annotation-driven不用配置上边的RequestMappingHandlerMapping和RequestMappingHandlerAdapter
	实际开发时使用mvc:annotation-driven
	 -->
	<mvc:annotation-driven></mvc:annotation-driven>
	
	<!-- 视图解析器 
	解析jsp视图，默认使用jstl标签，classpath下得有jstl的包-->
	<bean 
		class="org.springframework.web.servlet.view.InternalResourceViewResolver" >		
		<!-- 配置jsp路径的前缀 -->
		<property name="prefix" value="/WEB-INF/jsp/"/>
		<!-- 配置jsp路径的后缀 -->
		<property name="suffix" value=".jsp"/>
	</bean>
```

### 8.6.2 配置前端控制器

参考入门程序web.xml

### 8.6.3 编写Controller（就是Handler）

```java
//商品的controller
@Controller
public class ItemsController {
	@Autowired
	private ItemsService itemsService;
	
	@RequestMapping("/queryItems")
	public ModelAndView queryItems() throws Exception {
		
		//调用service查找数据库，查询商品列表
		List<ItemsCustom> itemsList = itemsService.findItemsList(null);
		
		//返回ModelAndView
		ModelAndView modelAndView = new ModelAndView();
		//相当于request的setAttribut，在jsp中通过itemsList取数据
		modelAndView.addObject("itemsList",itemsList);
		//指定视图
		//下边的路径，如果在视图解析器中配置jsp路径的前缀和jsp路径的后缀，
		//modelAndView.setViewName("/WEB-INF/jsp/items/itemsList.jsp");
		//则上边的路径配置可以不在程序中指定jsp路径的前缀和jsp路径的后缀
		modelAndView.setViewName("items/itemsList");
		
		//返回ModelAndView
		return modelAndView;
	}
}
```

### 8.6.4 编写jsp

参考入门程序

## 8.7 加载spring容器

将mapper、service、controller加载到spring容器

![image-20200301162349633](C:\Users\Chen\AppData\Roaming\Typora\typora-user-images\image-20200301162349633.png)

建议使用通配符的方法加载上面的配置文件。

在web.xml中，添加spring容器监听器，加载spring容器。

```xml
<!-- 加载spring容器 -->
	<context-param>
		<param-name>contextConfigLocation</param-name>
		<param-value>/WEB-INF/classes/spring/applicationContext-*.xml</param-value>
	</context-param>
	<listener>
		<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
	</listener>
```

## 8.8 调试

将项目加载到tomcat并启动，访问http://localhost:8080/springmvc_mybatis20200213/queryItems.action

一开始报错了，

> Error querying database.  Cause: org.springframework.jdbc.CannotGetJdbcConnectionException: Could not get JDBC Connection; nested exception is org.apache.commons.dbcp.SQLNestedException: Cannot create PoolableConnectionFactory (Unknown initial character set index '255' received from server. Initial client character set can be forced via the 'characterEncoding' property.)

上网查一下发现是数据库连接的问题，在db.properties里面原来的jdbc.url后面加上?useUnicode=true&characterEncoding=utf8，再重启tomcat后即可访问。

![image-20200301174740484](C:\Users\Chen\AppData\Roaming\Typora\typora-user-images\image-20200301174740484.png)

**==至此整合springmvc和MyBatis可以了，上传GitHub地址https://github.com/chunchunchen/SpringMVC-Bilibili.git，版本为c7bff6ebfdfaf28ca33f5b9896af76f00cdd7d9a==**