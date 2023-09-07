<c:set var="sectionCount" value="${sectionCount + 1}"/>

<c:set var="stripe" value="stripe1"/>
<c:if test="${(sectionCount % 2) == 0}">
   <c:set var="stripe" value="stripe2"/>
</c:if>
