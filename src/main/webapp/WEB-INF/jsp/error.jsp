<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>
 
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>
  <title>Error Page</title>
<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>


<!-- header bar -->
<div id="titleBarWrapper" style="max-width:1200px" userdoc="">    
    <!--myTitle -->
    <span class="titleBarMainTitle">Error Page</span>
</div>



${e:forHtml(errorMsg)}



<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
