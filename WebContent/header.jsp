<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
		<div class="layui-header">
			<div class="layui-logo">我的问题我做主</div>
			<!-- 头部区域（可配合layui已有的水平导航） -->
			<ul class="layui-nav layui-layout-left">
				<li class="layui-nav-item"><a href="">问题列表</a>
					<dl class="layui-nav-child">
						<dd>
							<a href="insert.jsp">添加</a>
						</dd>
					</dl></li>
			</ul>
			
			<c:if test="${empty loginuser}">
			<ul class="layui-nav layui-layout-right">
				<li class="layui-nav-item"><a href="javascript:;"> <img src="http://t.cn/RCzsdCq" class="layui-nav-img">这里是用户名</a>
					<dl class="layui-nav-child">
						<dd>
							<a href="login.jsp">登陆</a>
						</dd>
						<dd>
							<a href="">安全设置</a>
						</dd>
					</dl>
				</li>
			</ul>
			</c:if>
			<c:if test="${not empty loginuser}">
			<ul class="layui-nav layui-layout-right">
				<li class="layui-nav-item">
					<a href="user/logout.action">注销	</a>
				</li>
				</ul>
			</c:if>
			
		</div>