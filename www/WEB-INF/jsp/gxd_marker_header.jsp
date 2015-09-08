<c:if test="${not empty marker}">
    <!-- marker header table -->
    <table class="summaryHeader">
	<tr >
	  <td class="summaryHeaderCat1Gxd">
	       <div style="padding-top:7px;">Symbol</div>
	       <div style="padding-top:3px;">Name</div>
	       <div style="padding-top:2px;">ID</span>
	  </td>
	  <td class="summaryHeaderData1">
	    <a style="font-size:large;  font-weight: bold;" 
	      href="${configBean.FEWI_URL}marker/${marker.primaryID}">${marker.symbol}</a>
	    <br/>
	    <span style="font-weight: bold;">${marker.name}</span>
	    <br/>
	    <span style="">${marker.primaryID}</span>
	  </td>
	</tr>
    </table>
</c:if>
<c:if test="${empty marker}">
    <!-- no marker object -->
</c:if>
