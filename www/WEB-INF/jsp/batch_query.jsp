<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

${templateBean.templateHeadHtml}

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<title>MGI Batch Query</title>

${templateBean.templateBodyStartHtml}

<style type="text/css">
    /*:focus { -moz-outline-style: none; }*/
    a { outline:expression(hideFocus='true'); outline:0; }
</style>

<div id="titleBarWrapper" style="max-width:1200px" userdoc="batch_qf_help.shtml">	
	<!--myTitle -->
	<span class="titleBarMainTitle">MGI Batch Query</span>
</div>

    <form:form enctype="multipart/form-data" method="post" action="${configBean.FEWI_URL}batch/summary" commandName="batchQueryForm">
	
    <table class="pad5 borderedTable results">
        <tr class="stripe1"><td colspan="3">
                <p>Download gene/marker data for a batch of IDs or symbols. Results may be in web or tab-delimited format.</p>
			    &nbsp;&nbsp;<input CLASS="buttonLabel" value="Search" type="submit">
			    &nbsp;&nbsp;<INPUT CLASS="buttonLabel" TYPE="reset">
        </td></tr>
        <tr height="30px"><td class="resultsHeader">Input</td><td class="resultsHeader">Output</td></tr>
        <tr class="stripe1">
            <td valign="top">
                <table class="noBorder">
                    <tr valign="top"><td><font class="label">Type: </font>
                        &nbsp;&nbsp;<form:select path="idType" items="${batchQueryForm.idTypes}">
                        	<form:options items="${idTypes}" />
                        	</form:select>
                    </td>
                    </tr>
                    <tr><td>
                        <font class="label">Source:</font></td></tr>
                    <tr>
                    
                    <td>
                        <div id='nowrap' style='padding-top:2px;'>
	                        <div id='nojs' style='border-top: 1px solid #000000; padding-left: 5px; background: #eeeeee;display: block;'>
	                            <table style="border:0px;">
	                                <tr valign="top">
	                                    <td><span class="label">ID/Symbols List:</span><br/>
	                                <tr valign="top">
	                                    <td>&nbsp;&nbsp;
	                                    <form:textarea id="ids" path="ids" class="formWidth"></form:textarea><br/>
	                                    &nbsp;&nbsp;&nbsp;&nbsp;<span class="example">*tab, comma, space, and newline separated ids.</span></td>
	                                </tr>
	                            </table>
	                            <table style="border-style:none;">
	                                <tr>
	                                    <td colspan="3" valign="top"><span class="label">ID/Symbols File:</span><br>
	                                    &nbsp;&nbsp;<input type="file" name="idFile" value=""></td>
	                                </tr>
	                                <tr>
	                                    <td valign="top"><span class="label nowrap">File Type:</span></td>
	                                    <td valign="top"><span class="label nowrap">ID/Symbols column:</span></td>
	                                </tr>
	                                <tr>
	                                    <td valign="top">
	                                        <span class="nowrap">&nbsp;&nbsp;<form:radiobutton path="fileType" value="tab"/>tab-delimited</span><br>
	                                        <span class="nowrap">&nbsp;&nbsp;<form:radiobutton path="fileType" value="csv"/>comma separated</span></td>
	                                    <td valign="top">
	                                        <span class="nowrap">&nbsp;&nbsp;<form:input path="idColumn" size="3"></form:input></span><br>
	                                        &nbsp;&nbsp;<span class="example">*ID/Symbols parsed from a single column</span></td>
	                                </tr>
	                            </table>                       
	                        </div>
                        </div>
                        
                        <div id='json' style='display: none; padding-top:2px;'>
                            <ul id="btab">
                                <li><a id="list" href="#" onClick="selectTab('list')" class="on">Enter Text</a></li>
                                <li><a id="file" href="#" onClick="selectTab('file')" class="off">Upload File</a></li>
                            </ul>                       
                            <table id="IDlist" style='display: block; background: #eeeeee; height: 150px; border-style:none;'>
                                <tr valign="top">
                                    <td><span class="label">ID/Symbols List:</span><br/>
                                <tr valign="top">
                                    <td>&nbsp;&nbsp;<form:textarea id="ids" path="ids" class="formWidth"></form:textarea><br/>
                                    &nbsp;&nbsp;&nbsp;&nbsp;<span class="example">*tab, space, and newline separated ids.</span></td>
                                </tr>
                            </table>
                            <table id="IDfile" style='display: none; background: #eeeeee; height: 150px; border-style:none;'>
                                <tr>
                                    <td colspan="3" valign="top"><span class="label">ID/Symbols File:</span><br>
                                    &nbsp;&nbsp;<input type="file" name="idFile" value=""></td>
                                </tr>
                                <tr>
                                    <td valign="top"><span class="label nowrap">File Type:</span></td>
                                    <td valign="top"><span class="label nowrap">ID/Symbols column:</span></td>
                                </tr>
                                <tr>
                                    <td valign="top">
                                        <span class="nowrap">&nbsp;&nbsp;<form:radiobutton path="fileType" value="tab"/>tab-delimited</span><br>
                                        <span class="nowrap">&nbsp;&nbsp;<form:radiobutton path="fileType" value="csv"/>comma separated</span></td>
                                    <td valign="top">
                                        <span class="nowrap">&nbsp;&nbsp;<form:input path="idColumn" size="3"></form:input></span><br>
                                        &nbsp;&nbsp;<span class="example">*ID/Symbols parsed from a single column</span></td>
                                </tr>
                            </table>                             
                        </div>
                    </td></tr>
                </table>
            </td>
            <td valign="top">
                <table class="noBorder">
                    <tr>
                        <td><font class="label">Gene Attributes:</font></td>
                    </tr>
                    <tr>
                        <td>
							<form:checkboxes path="attributes" items="${batchQueryForm.attributeList}" />
			            </td>
			        </tr>
			        <tr>
                        <td>&nbsp;</td>
                    </tr>
                    <tr style="vertical-align: top;">
                        <td><font class="label">Additional Information:</font></td>
                    </tr>
                    <tr style="vertical-align: top;">
                        <td>
							<form:radiobuttons path="association" items="${batchQueryForm.associationList}" />
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
        <tr class="stripe1"><td colspan="3">&nbsp;&nbsp;<input CLASS="buttonLabel" value="Search" type="submit">
        &nbsp;&nbsp;<INPUT CLASS="buttonLabel" TYPE="reset">
    </table>
    </form:form>
    
<script type="text/javascript">

    function batchQueryInit(){
	    changeVisibility('json');
	    var div = YAHOO.util.Dom.get('nowrap');
	    if( div ) {
	        div.innerHTML = '';
	    }
	};
	
	YAHOO.util.Event.onDOMReady(batchQueryInit);

    function changeVisibility(id) {

        myID = YAHOO.util.Dom.get(id);

        if (myID.style.display == "block") {
            myID.style.display = "none";
        } else {
            myID.style.display = "block";
        }
    }

    function selectTab(tid) {
        if (YAHOO.util.Dom.hasClass(tid, 'off')){
            var oid = 'list';
	        if (tid == 'list') {
	            var oid = 'file';
	        }       
            YAHOO.util.Dom.removeClass(oid, 'on');
            YAHOO.util.Dom.addClass(oid, 'off');
            YAHOO.util.Dom.removeClass(tid, 'off');
            YAHOO.util.Dom.addClass(tid, 'on');
            
	        changeVisibility('IDlist');
	        changeVisibility('IDfile');        
        }
    }  

</script>

${templateBean.templateBodyStopHtml}