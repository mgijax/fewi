<%@ page import = "org.jax.mgi.fewi.util.FormatHelper" %>
<%@ page import = "mgi.frontend.datamodel.*" %>
<%@ page import = "org.jax.mgi.fewi.util.StyleAlternator" %>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<% StyleAlternator stripe  = (StyleAlternator)request.getAttribute("stripe"); %>
<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<title>Human – Mouse Disease Connection</title>

<meta http-equiv="X-UA-Compatible" content="chrome=1">

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<!-- import jquery UI specifically for this page -->
<link rel="stylesheet" type="text/css" href="${configBean.WEBSHARE_URL}css/jquery-ui-1.10.2.custom.min.css" />

<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/disease_portal.css" />
<%-- Please add styles to disease_portal.css
Also disease_portal.css need to be imported last in order for the styles to override jquery-ui
<style>
</style>
--%>
<!--[if IE]>
<style>

#toggleImg{height:1px;}

/*
body.yui-skin-sam div#outer {position:relative;}
#qwrap, #expressionSearch .yui-navset {position:absolute;}
#toggleQF { overflow:auto; }
*/
body.yui-skin-sam div#outer {position:relative;}
#expressionSearch .yui-navset {position:absolute;}
</style>
<![endif]-->

<!--begin custom header content for this example-->
<script type="text/javascript">
    document.documentElement.className = "yui-pe";
</script>

<%@ include file="/WEB-INF/jsp/templates/templateHdpBodyStart.html" %>
<!-- iframe for history managers use -->
<iframe id="yui-history-iframe" name="yui-history-iframe" src="${configBean.FEWI_URL}blank.html"></iframe>
<input id="yui-history-field" name="yui-history-field" type="hidden">

<div class="framed">
<%@ include file="/WEB-INF/jsp/hmdc/disease_portal_form.jsp" %>
<br clear="all" />

<%@ include file="/WEB-INF/jsp/static/home/hmdc_resource_links.jsp" %>
<%@ include file="/WEB-INF/jsp/static/home/hmdc_spotlight.jsp" %>
</div><!-- framed -->

<script type="text/javascript">
    var fewiurl = "${configBean.FEWI_URL}";
    var assemblyBuild = "${configBean.ASSEMBLY_VERSION}";
</script>

<c:set var="jsHome" value="${configBean.FEWI_URL}assets/js/"/>
<script type="text/javascript" src="${jsHome}disease_portal_autocomplete.js"></script>
<script type="text/javascript" src="${jsHome}disease_portal_upload.js"></script>
<% 	
 	String queryString = (String) request.getAttribute("querystring");
	// need to url encode the querystring
	request.setAttribute("encodedQueryString", FormatHelper.encodeQueryString(queryString));
%>
<script type="text/javascript">
	var querystring = "${encodedQueryString}";
</script>
<script type="text/javascript">
	var _idMap = {"organismHuman1":"organismHuman2",
		"organismHuman2":"organismHuman1",
		"organismMouse2":"organismMouse1",
		"organismMouse1":"organismMouse2",
		};
	$(function(){
		// register any help panels
		YAHOO.namespace("hdp.container");
		YAHOO.hdp.container.panelVcf = new YAHOO.widget.Panel("locationsFileHelp", { width:"520px", draggable:false, visible:false, constraintoviewport:true } );
		YAHOO.hdp.container.panelVcf.render();

		YAHOO.hdp.container.panelQueryHelp = new YAHOO.widget.Panel("queryHelp", { width:"520px", draggable:false, visible:false, constraintoviewport:true } );
		YAHOO.hdp.container.panelQueryHelp.render();
		//YAHOO.util.Event.addListener("locationsFileHelpImg", "mouseover", YAHOO.hdp.container.panelVcf.show, YAHOO.hdp.container.panelVcf, true);
		var _locationsFileHelpTOID;
		$("#locationsFileHelpImg").on("mouseover",function(e){
			_locationsFileHelpTOID = setTimeout(function(){YAHOO.hdp.container.panelVcf.show()},500);
		});
		$("#locationsFileHelpImg").on("mouseout",function(e){
			if(_locationsFileHelpTOID) clearTimeout(_locationsFileHelpTOID);
		});

		var _queryHelpTOID;
		$("#queryHelpImg").on("mouseover",function(e){
			_queryHelpTOID = setTimeout(function(){YAHOO.hdp.container.panelQueryHelp.show()},500);
		});
		$("#queryHelpImg").on("mouseout",function(e){
			if(_queryHelpTOID) clearTimeout(_queryHelpTOID);
		});

		// wire up the two radio buttons to mirror each other
		$("input.organism").change(function(e){
			var id = $(this).attr("id");
			var checked = $(this).prop("checked");
			if(checked && id in _idMap)
			{
				mirrorId = _idMap[id];
				$("#"+mirrorId).prop("checked",true);
			}
		});

		// make sure upload button is reset. Some browsers try to save your form state, but it won't work with our upload
		if(typeof resetLocationsFileFields == 'function' && typeof resetGeneFileFields == 'function')
		{
			resetLocationsFileFields();
			resetGeneFileFields();
		}
		else
		{
			alert("Some javascript failed to load. This form may not function properly.");
		}

		// handle the upload reset when user resets the form
		$("#diseasePortalQueryForm").on("reset",function(e){
			if(typeof resetLocationsFileFields == 'function' && typeof resetGeneFileFields == 'function')
			{
				resetLocationsFileFields();
				resetGeneFileFields();
			}
			// also make sure to reset second organism radio buttons
			$("#organismMouse2").prop("checked",true);
			$("#enableVcfFilter").prop("checked",true);
		});
	});
</script>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
