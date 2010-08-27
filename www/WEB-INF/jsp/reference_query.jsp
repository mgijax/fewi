<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	
	<link href="/fewi/mgi_qf.css" rel="stylesheet" type="text/css"/>
	
	<title>Reference Query</title>
</head>
<body>

<!-- begin header bar -->
<div id="titleBarWrapper" style="max-width:1200px" userdoc="marker_help.shtml">	
	<!--myTitle -->
	<span class="titleBarMainTitle">References Query</span>
</div>
<!-- end header bar -->

<form:form method="GET" commandName="referenceQueryForm" action="/fewi/mgi/reference/summary">

<!-- query form table -->
<TABLE WIDTH="100%">
	<TR>
		<TD COLSPAN="2" align="left">
			<INPUT CLASS="buttonLabel" VALUE="Search" TYPE="submit">
			&nbsp;&nbsp;
			<INPUT TYPE="reset">
		</td>
	</tr>

	<!-- gene symbol/name section -->
	<TR CLASS="stripe1">
		<TD CLASS="cat1">Author</TD>
		<TD CLASS="data1">
    		<form:input path="author" maxlength="256" />
    		<form:radiobutton path="authorScope" value="any"/> Any Author(s)<br/>
    		<form:radiobutton path="authorScope" value="first"/> First Author<br/>
    		<form:radiobutton path="authorScope" value="last"/> Last Author
		</TD>
	</TR>

    <!-- map position section -->
	<TR CLASS="stripe2">
		<TD CLASS="cat2">Journal</TD>
		<TD CLASS="data1">
    		<form:input path="journal" maxlength="256" />
		</TD>
	</TR>
	<tr  CLASS="stripe1">
		<td  CLASS="cat1">Year</td>
		<td  CLASS="data1"><form:input path="year" cols="40" /></td>
	</tr>
	<tr  CLASS="stripe2">
		<td  CLASS="cat2">Text</td>
		<td  CLASS="data1">
			<form:textarea path="text"  rows="3" cols="40" /><br/>
			<form:checkbox path="inTitle" value="inTitle"/> In Title 
			<form:checkbox path="inAbstract" value="inAbstract"/> In Abstract
		</td>
	</tr>
	<tr>
		<td colspan="2">OR</td>
	</tr>
	<tr  CLASS="stripe1">
		<td  CLASS="cat1">
			PubMed ID or<br/>
			MGI Reference ID
		</td>
		<td  CLASS="data1"><form:input path="id" maxlength="256" /></td>
	</tr>
    <TR>
		<TD COLSPAN="3" align="left">
			<INPUT CLASS="buttonLabel" VALUE="Search" TYPE="submit">
			&nbsp;&nbsp;
			<INPUT TYPE="reset">
		</TD>
    </TR>
</TABLE>
</form:form>


</body>
</html>
