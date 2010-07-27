<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Marker Detail</title>
</head>
<body>
ID: ${marker.primaryID}<br/>
Symbol: ${marker.symbol}<br/>
Name: ${marker.name}<br/>
Type: ${marker.markerType}<br/>
References: (<a href="/proto/mgi/reference/marker/${marker.markerKey}">All)<br/>
<c:forEach var="reference" items="${marker.references}">
	<a href="/proto/mgi/reference/${reference.referenceKey}">
		${reference.jnumID}</a> ${reference.journal} ${reference.title}<br/>

</c:forEach>
</body>
</html>