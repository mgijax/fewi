<form:form method="GET" commandName="fooQueryForm" action="${configBean.FEWI_URL}foo/summary">
	<TABLE WIDTH="100%" class="pad5 borderedTable">			
	  <!-- row 1-->
	  <tr>
	    <td class="queryCat1">Row 1</td>
	    <td class="queryParams1">
	      <div style="position:relative;">
	        <div style="float:left; width:300px;text-align:left;">
	          <form:input path="param1" cols="40" class=""/>
	        </div>
	        <div style="float:left; text-align:left;">
	          Enter something param1
	        </div>
	      </div>			
	    </td>
	  </tr>						
	  <!-- row 2-->
	  <tr>
	    <td class="queryCat2">Row 2</td>
	    <td class="queryParams2">
	      <div style="position:relative;">
	        <div style="float:left; width:300px;text-align:left;">
	          <form:input path="param2" cols="40" class=""/>
	        </div>
	        <div style="float:left; text-align:left;">
	          Enter something param2
	        </div>
	      </div>			
	    </td>
	  </tr>		
	  <!-- row 3-->
	  <tr>
	    <td class="queryCat1">Row 3</td>
	    <td class="queryParams1">
	      <div style="position:relative;">
	        <div style="float:left; width:300px;text-align:left;">
	          <form:input path="param3" cols="40" class=""/>
	        </div>
	        <div style="float:left; text-align:left;">
	          Enter something param3
	        </div>
	      </div>			
	    </td>
	  </tr>			
	  <!-- Submit/Reset-->
	  <tr>  
	    <td colspan="2" align="left">
	      <input class="buttonLabel" value="Search" type="submit">
	      &nbsp;&nbsp;
	      <input type="reset">
	    </td>
	  </tr>			  
	</table>
</form:form>