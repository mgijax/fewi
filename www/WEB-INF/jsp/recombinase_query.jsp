<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<style type="text/css">

</style>


<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<title>Recombinase Query</title>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<!-- begin header bar -->
<div id="titleBarWrapper" style="max-width:1200px" userdoc="recombinase_help.shtml">	
	<!--myTitle -->
	<span class="titleBarMainTitle">Recombinase Query</span>
</div>
<!-- end header bar -->

<form:form method="GET" commandName="recombinaseQueryForm" action="/fewi/mgi/recombinase/summary">
Not a real query form -- for testing purposes only:
<!-- query form table -->
<TABLE WIDTH="100%" class="border">
	<TR>
		<TD COLSPAN="2" align="left">
			<INPUT CLASS="buttonLabel" VALUE="Search" TYPE="submit">
			&nbsp;&nbsp;
			<INPUT TYPE="reset"> 
		</td>
	</tr>

	<!-- anatomical system section -->
	<tr  CLASS="stripe1">
		<td CLASS="cat1">System</td>
		<td>
			<div style="position:relative;">
				<div style="float:left; width:300px;text-align:left;">
					<form:input path="system" cols="40" class="formWidth"/>
				</div>
				<div style="float:left; text-align:left;">
					Enter an anatomical system.
				</div>
    		</div>			
		</td>
	</tr>
	<tr  CLASS="stripe2">
		<td CLASS="cat2">Driver</td>
		<td>
			<div style="position:relative;">
				<div style="float:left; width:300px;text-align:left;">
					<form:input path="driver" cols="40" class="formWidth"/>
				</div>
				<div style="float:left; text-align:left;">
					Enter a driver.
				</div>
    		</div>			
		</td>
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

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
