<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ attribute name="description" required="false" type="java.lang.String" description="SEO Description Meta Tag" %>
<%@ attribute name="canonical" required="false" type="java.lang.String" description="Canonical Link to this page" %>
<%@ attribute name="keywords" required="false" type="java.lang.String" description="SEO Keywords Meta Tag" %>
<%@ attribute name="title" required="false" type="java.lang.String" description="Page Title" %>

<c:if test="${not empty canonical}">
	<link rel="canonical" href="${canonical}" />
</c:if>

<c:if test="${not empty title}">
	<title>${title}</title>
</c:if>

<c:if test="${not empty description}">
	<meta name="description" content="${description}" />
</c:if>

<c:if test="${not empty keywords}">
	<meta name="keywords" content="${keywords}" />
</c:if>

<meta name="robots" content="NOODP" />
<meta name="robots" content="NOYDIR" />

