<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>left</title>
    <base target="body"/>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="content-type" content="text/html;charset=utf-8">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<script type="text/javascript" src="<c:url value='/jquery/jquery-1.5.1.js'/>"></script>
	<script type="text/javascript" src="<c:url value='/menu/mymenu.js'/>"></script>
	<link rel="stylesheet" href="<c:url value='/menu/mymenu.css'/>" type="text/css" media="all">
	<link rel="stylesheet" type="text/css" href="<c:url value='/jsps/css/left.css'/>">
<script language="javascript">
/*
注意：1.对象名和第一个参数必须相同
	2.第二个参数是菜单上的大标题*/
	
var bar = new Q6MenuBar("bar", "小吴网上书城");
$(function() {
	bar.colorStyle = 4;//指定所有菜单背景颜色 包含0 1 2 3 4 四种样式
	bar.config.imgDir = "<c:url value='/menu/img/'/>";//菜单前的图片样式
	bar.config.radioButton=true;//是否排斥，多个一级菜单是否可以同时打开
/*
 bar.add("程序设计", "Java Javascript", "/goods/jsps/book/list.jsp", "body"); 
 
	第一个参数：一级分类名称
	第二个参数：二级分类名称
	第三个参数：点击二级分类后的url
	第四个参数：链接的内容在哪个框架页显示
 */
 <c:forEach items = "${parents}" var = "parent">/*遍历一级*/
 	<c:forEach items = "${parent.children}" var = "child">/*遍历二级*/
 		bar.add("${parent.cname}", "${child.cname}", "/goods/BookServlet?method=findByCategory&cid=${child.cid}", "body");
	</c:forEach>
</c:forEach>
	
	
// 	bar.add("程序设计", "JSP", "/goods/jsps/book/list.jsp", "body");
// 	bar.add("程序设计", "C C++ VC VC++", "/goods/jsps/book/list.jsp", "body");
	
// 	bar.add("办公室用书", "微软Office", "/goods/jsps/book/list.jsp", "body");
// 	bar.add("办公室用书", "计算机初级入门", "/goods/jsps/book/list.jsp", "body");
	
// 	bar.add("图形 图像 多媒体", "Photoshop", "/goods/jsps/book/list.jsp", "body");
// 	bar.add("图形 图像 多媒体", "3DS MAX", "/goods/jsps/book/list.jsp", "body");
// 	bar.add("图形 图像 多媒体", "网页设计", "/goods/jsps/book/list.jsp", "body");
// 	bar.add("图形 图像 多媒体", "Flush", "/goods/jsps/book/list.jsp", "body");
	
// 	bar.add("操作系统/系统开发", "Windows", "/goods/jsps/book/list.jsp", "body");
// 	bar.add("操作系统/系统开发", "Linux", "/goods/jsps/book/list.jsp", "body");
// 	bar.add("操作系统/系统开发", "系统开发", "/goods/jsps/book/list.jsp", "body");
	
	$("#menu").html(bar.toString());//显示分类
});
</script>
</head>
  
<body>  
  <div id="menu"></div>
</body>
</html>
