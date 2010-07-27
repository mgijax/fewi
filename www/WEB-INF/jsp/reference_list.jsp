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
<table>
	<thead>
	  <th>ID</th>
	  <th>Journal</th>
	  <th>Title</th>
	</thead>
	<tr>
	  <td colspan=3 align=middle>
		<c:if test='${!referenceList.firstPage}'>
		  <a href="?page=${referenceList.page - 1}">&lt;&lt; Prev</a>
		</c:if>
		&nbsp;&nbsp;&nbsp;${referenceList.page} of ${referenceList.pageCount}&nbsp;&nbsp;&nbsp;
		<c:if test='${!referenceList.lastPage}'>
		  <a href="?page=${referenceList.page + 1}">Next &gt;&gt;</a>
		</c:if>
	  </td>
	</tr>	
  	<c:forEach var="reference" items="${referenceList.pageList}">
    <tr>
      <td>
      	<a href='${reference.referenceKey}'>
      		${reference.jnumID}</a>
      </td>
      <td>${reference.journal}</td>
      <td>${reference.title}</td>
    </tr>
	</c:forEach>
</table>
</body>
</html>