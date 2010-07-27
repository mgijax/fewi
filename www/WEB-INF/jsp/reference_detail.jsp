<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Reference Detail</title>
</head>
<body>
	ID: ${reference.jnumID}<br/>
	Journal: ${reference.journal}<br/>
	Title: ${reference.title}<br/>
	Authors: ${reference.authors}<br/>
	Markers: (<a href="/proto/mgi/marker/references/${reference.referenceKey}">${fn:length(reference.markerKeys)}</a>)
</body>
</html>