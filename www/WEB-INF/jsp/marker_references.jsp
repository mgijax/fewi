<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Reference Summary</title>
</head>
<body>

<table border='1'>
	<tr align="left" bgcolor="#ffffff" valign="top">
    	<td align="right" bgcolor="#d0e0f0" width="8%">
      		<font color="#000000" face="Arial, Helvetica">
			<font size="+2">&nbsp;</font><b>Symbol</b><br>
			<b>Name</b><br>
			<b>ID</b>
      		</font>
    	</td>
    	<td width="*">
      		<a href="/proto/mgi/marker/${marker.markerKey}">
      			${marker.symbol}</a><br>
      		<b>${marker.name}<br>
      		${marker.primaryID}
    	</td>
	</tr>
</table>

<table>
	<thead>
	  <th>ID</th>
	  <th>Journal</th>
	  <th>Title</th>
	</thead>
	<tr>
	  <td>
		<c:if test="${!referenceList.firstPage}">
		  <a href="?page=previous">&lt;&lt; Prev</a>
		</c:if>
		<c:if test="${!referenceList.lastPage}">
		  <a href="?page=next">Next &gt;&gt;</a>
		</c:if>
	  </td>
	</tr>
  	<c:forEach var="reference" items="${referenceList.pageList}">
    <tr>
      <td>
      	<a href='/proto/mgi/reference/${reference.referenceKey}'>
      		${reference.jnumID}</a>
      </td>
      <td>${reference.journal}</td>
      <td>${reference.title}</td>
    </tr>
	</c:forEach>
</table>

</body>
</html>