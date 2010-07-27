<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<title>Marker Summary</title>
</head>
<body>

<table>
	<tr align="left" bgcolor="#ffffff" valign="top">
    	<td align="right" bgcolor="#d0e0f0" width="8%">
      		<font color="#000000" face="Arial, Helvetica">
			<font size="+2">&nbsp;</font><b>JNum</b><br>
			<b>Citiation</b>
      		</font>
    	</td>
    	<td width="*">
      		<a href="/proto/mgi/reference/${reference.referenceKey}">
      			${reference.jnumID}</a><br>
      		${reference.citation}
    	</td>
	</tr>
</table>

<table>
	<tr>	  
	  <th><div id="symbol">Symbol</div></th>
	  <th><div id="name">Name</div></th>
	  <th><div id="loc">Location</div></th>	  
	</tr>
  	<c:forEach var="marker" items="${markerList}">
    <tr>
      <td>
      	<a href='${marker.marker.markerKey}'>${marker.marker.symbol}</a>
      </td>
      <td>${marker.marker.name}</td>
      <td>${marker.marker.markerType}</td>
    </tr>
	</c:forEach>
</table>

</body>
</html>