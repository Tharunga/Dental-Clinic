<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String error = (String) request.getAttribute("error");
    String success = (String) request.getAttribute("success");
    if (error != null) {
%>
<div class="alert alert-error"><%= error %></div>
<%
    }
    if (success != null) {
%>
<div class="alert alert-ok"><%= success %></div>
<%
    }
%>
