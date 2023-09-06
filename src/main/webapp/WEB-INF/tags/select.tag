<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ tag import = "java.util.*" %>
<%@ attribute name="id" required="false" type="java.lang.String" %>
<%@ attribute name="name" required="false" type="java.lang.String" %>
<%@ attribute name="multiple" required="false" type="java.lang.String" %>
<%@ attribute name="size" required="false" type="java.lang.String" %>
<%@ attribute name="styleClass" required="false" type="java.lang.String" %>

<%@ attribute name="items" required="true" type="java.lang.Object" description="key,value map, or simply a list of values" %>
<%@ attribute name="value" required="false" type="java.lang.String" description="If there is only a single User selected value" %>

<select <c:if test="${not empty id}">id="${id}"</c:if> <c:if test="${not empty name}">name="${name}"</c:if> <c:if test="${not empty multiple}">multiple="${multiple}"</c:if> <c:if test="${not empty size}">size="${size}"</c:if> <c:if test="${not empty styleClass}">class="${styleClass}"</c:if> >
	<c:forEach var="item" items="${items}">
		<option value="${item.key}"${item.key == value ? 'selected' : ''}>${fn:escapeXml(item.value)}</option>
	</c:forEach>
</select>
