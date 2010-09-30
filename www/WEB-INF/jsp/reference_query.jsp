<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.1/build/fonts/fonts-min.css" />
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.1/build/button/assets/skins/sam/button.css" />
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.8.1/build/container/assets/skins/sam/container.css" />
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.1/build/yahoo-dom-event/yahoo-dom-event.js"></script>

<script type="text/javascript" src="http://yui.yahooapis.com/2.8.1/build/connection/connection-min.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.1/build/element/element-min.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.1/build/button/button-min.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.1/build/dragdrop/dragdrop-min.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.8.1/build/container/container-min.js"></script>


<!--begin custom header content for this example-->
<script type="text/javascript">
document.documentElement.className = "yui-pe";
</script>

<style type="text/css">
#example {
    height:30em;
}

label { 
    display:block;
    float:left;
    width:45%;
    clear:left;
}

.clear {
    clear:both;
}

#resp {
    margin:10px;
    padding:5px;
    border:1px solid #ccc;
    background:#fff;
}

#resp li {
    font-family:monospace
}

.yui-pe .yui-pe-content {
    display:none;
}

</style>



${templateBean.templateHeadHtml}

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<title>Reference Query</title>

${templateBean.templateBodyStartHtml}

<!-- begin header bar -->
<div id="titleBarWrapper" style="max-width:1200px" userdoc="marker_help.shtml">	
	<!--myTitle -->
	<span class="titleBarMainTitle">References Query</span>
</div>
<!-- end header bar -->

<form:form method="GET" commandName="referenceQueryForm" action="/fewi/mgi/reference/summary">

<!-- query form table -->
<TABLE WIDTH="100%" class="border">
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
		<TD>
			<div style="position:relative;">
				<div style="float:left; width:300px;text-align:left;">
					<div id="authorAutoComplete">
						<form:input id="author" path="author" maxlength="256"/>
						<div id="authorContainer"></div>
					</div>
				</div>
				<div style="float:left; text-align:left;">
					<form:radiobutton path="authorScope" value="any"/> Any Author(s)<br/>
		    		<form:radiobutton path="authorScope" value="first"/> First Author<br/>
		    		<form:radiobutton path="authorScope" value="last"/> Last Author
				</div>
    		</div>		
		</TD>
	</TR>

    <!-- map position section -->
	<TR CLASS="stripe2">
		<TD CLASS="cat2">Journal</TD>
		<TD>
			<div style="position:relative;">
				<div style="float:left; width:300px;text-align:left;z-index=1000;">
					<div id="authorAutoComplete">
						<form:input id="journal" path="journal" maxlength="256" />
						<div id="journalContainer"></div>
					</div>
				</div>
				<div style="float:left; text-align:left;">
					Example text here.
				</div>
    		</div>
		</TD>
	</TR>
	<tr  CLASS="stripe1">
		<td CLASS="cat1">Year</td>
		<td>
			<div style="position:relative;">
				<div style="float:left; width:300px;text-align:left;">
					<form:input path="year" cols="40" class="formWidth"/>
				</div>
				<div style="float:left; text-align:left;">
					Example text here.
				</div>
    		</div>			
		</td>
	</tr>
	<tr  CLASS="stripe2">
		<td CLASS="cat2">Text</td>
		<td>	
			<div style="position:relative;">
				<div style="float:left; width:300px;text-align:left;">
					<form:textarea path="text"  rows="3" cols="40" class="formWidth"/><br/>
					<form:checkbox path="inTitle" value="inTitle"/> In Title 
					<form:checkbox path="inAbstract" value="inAbstract"/> In Abstract
				</div>
				<div style="float:left; text-align:left;">
					Example text here.
				</div>
    		</div>		
		</td>
	</tr>
	<tr>
		<td colspan="2">
			<div style="width:800px; text-align: center; font-weight: bold;">OR</div>
		</td>
	</tr>
	<tr  CLASS="stripe1">
		<td  CLASS="cat1">
			PubMed ID or<br/>
			MGI Reference ID
		</td>
		<td  CLASS="data1">
			<div style="position:relative;">
				<div style="float:left; width:300px;text-align:left;">
					<form:input path="id" maxlength="256" class="formWidth"/>
				</div>
				<div style="float:left; text-align:left;" class="example">
					Example text here.
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

<div>
<button id="show">Show dialog1</button> 
<button id="hide">Hide dialog1</button>
</div>


<div id="dialog1" class="yui-pe-content">
<div class="hd">Please enter your information</div>

<div class="bd">
<form:form method="GET" commandName="referenceQueryForm">
	<label for="firstname">First Name:</label><input type="textbox" name="firstname" />
	<label for="lastname">Last Name:</label><input type="textbox" name="lastname" />
	<label for="email">E-mail:</label><input type="textbox" name="email" /> 

	<label for="state[]">State:</label>
	<select multiple name="state[]">
		<option value="California">California</option>

		<option value="New Jersey">New Jersey</option>
		<option value="New York">New York</option>
	</select> 

	<div class="clear"></div>

	<label for="radiobuttons">Radio buttons:</label>
	<input type="radio" name="radiobuttons[]" value="1" checked/> 1
	<input type="radio" name="radiobuttons[]" value="2" /> 2
	
	<div class="clear"></div>

	<label for="check">Single checkbox:</label><input type="checkbox" name="check" value="1" /> 1
	
	<div class="clear"></div>
		
	<label for="textarea">Text area:</label><textarea name="textarea"></textarea>

	<div class="clear"></div>

	<label for="cbarray">Multi checkbox:</label>

	<input type="checkbox" name="cbarray[]" value="1" /> 1
	<input type="checkbox" name="cbarray[]" value="2" /> 2
</form:form>
</div>
</div>


<script type="text/javascript">
YAHOO.example.AuthorAutocomplete = function() {
    // Use an XHRDataSource
    var oDS = new YAHOO.util.XHRDataSource("/fewi/mgi/reference/autocomplete/author");
    // Set the responseType
    oDS.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
    // Define the schema of the JSON results
    oDS.responseSchema = {resultsList : "resultStrings"};

    // Instantiate the AutoComplete
    var oAC = new YAHOO.widget.AutoComplete("author", "authorContainer", oDS);
    // Throttle requests sent
    oAC.queryDelay = .3;
    oAC.minQueryLength = 2;
    oAC.maxResultsDisplayed = 1000;
    oAC.useIFrame = true;
    oAC.forceSelection = true;
    oAC.allowBrowserAutocomplete = false;
    oAC.delimChar = ";";

    return {
        oDS: oDS,
        oAC: oAC
    };
}();
</script>

<script type="text/javascript">
YAHOO.example.JournalAutocomplete = function() {
    // Use an XHRDataSource
    var oDS = new YAHOO.util.XHRDataSource("/fewi/mgi/reference/autocomplete/journal");
    // Set the responseType
    oDS.responseType = YAHOO.util.XHRDataSource.TYPE_JSON;
    // Define the schema of the JSON results
    oDS.responseSchema = {resultsList : "resultStrings"};

    // Instantiate the AutoComplete
    var oAC = new YAHOO.widget.AutoComplete("journal", "journalContainer", oDS);
    // Throttle requests sent
    oAC.queryDelay = .3;
    oAC.minQueryLength = 2;
    oAC.maxResultsDisplayed = 1000;
    oAC.useIFrame = false;
    oAC.forceSelection = true;
    oAC.allowBrowserAutocomplete = false;
    oAC.delimChar = ";";

    return {
        oDS: oDS,
        oAC: oAC
    };
}();


YAHOO.util.Event.onDOMReady(function () {
	
	// Define various event handlers for Dialog
	var handleSubmit = function() {
		this.submit();
	};
	var handleCancel = function() {
		this.cancel();
	};
	var handleSuccess = function(o) {
		var response = o.responseText;
		response = response.split("<!")[0];
		document.getElementById("resp").innerHTML = response;
	};
	var handleFailure = function(o) {
		alert("Submission failed: " + o.status);
	};

    // Remove progressively enhanced content class, just before creating the module
    YAHOO.util.Dom.removeClass("dialog1", "yui-pe-content");

	// Instantiate the Dialog
	dialog1 = new YAHOO.widget.Dialog("dialog1", 
							{ width : "30em",
							  fixedcenter : true,
							  visible : false, 
							  constraintoviewport : true,
							  buttons : [ { text:"Submit", handler:handleSubmit, isDefault:true },
								      { text:"Cancel", handler:handleCancel } ]
							});

	// Validate the entries in the form to require that both first and last name are entered
	dialog1.validate = function() {
		var data = this.getData();
		if (data.firstname == "" || data.lastname == "") {
			alert("Please enter your first and last names.");
			return false;
		} else {
			return true;
		}
	};

	// Wire up the success and failure handlers
	dialog1.callback = { success: handleSuccess,
						     failure: handleFailure };
	
	// Render the Dialog
	dialog1.render();

    // DataSource instance
    var facetYearDS = new YAHOO.util.DataSource("facet/year?${queryString}");
    facetYearDS.responseType = YAHOO.util.DataSource.TYPE_JSON;
    facetYearDS.responseSchema = {resultsList: "resultFacets"};

    var populateDialog = function () {
        alert('working...');
        facetYearDS.submit();
		dialog1.show();
    };

    

	YAHOO.util.Event.addListener("show", "click", populateDialog);
	YAHOO.util.Event.addListener("hide", "click", dialog1.hide, dialog1, true);
});

</script>

${templateBean.templateBodyStopHtml}