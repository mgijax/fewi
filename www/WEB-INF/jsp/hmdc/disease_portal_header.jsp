<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

		<!-- import jquery UI specifically for this page -->
		<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}/assets/hmdc/bower_components/bootstrap/dist/css/bootstrap.css" />
		<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}/assets/css/hmdc/search.css" />
		<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}/assets/css/hmdc/queryBuilder.type.css" />

		<!-- Combo-handled YUI JS files: -->
		<script type="text/javascript" src="${configBean.WEBSHARE_URL}js/yui-2.8.custom.min.js"></script>

		<!-- jQuery -->
		<script type="text/javascript" src="${configBean.WEBSHARE_URL}js/jquery-1.10.2.min.js"></script>
		<script type="text/javascript" src="${configBean.WEBSHARE_URL}js/jquery-ui-1.11.4.js"></script>

		<!-- Combo-handled YUI CSS files: -->
		<link rel="stylesheet" type="text/css" href="${configBean.WEBSHARE_URL}css/yui-reset.css">
		<link rel="stylesheet" type="text/css" href="${configBean.WEBSHARE_URL}css/yui-2.8-combo.css">

		<link href="${configBean.WEBSHARE_URL}css/mgi_template01.css" rel="stylesheet" type="text/css"/>

		<meta http-equiv="X-UA-Compatible" content="chrome=1">

		<link rel="stylesheet" type="text/css" href="${configBean.WEBSHARE_URL}css/jquery-ui-1.10.2.custom.min.css" />

		<%--@ include file="/WEB-INF/jsp/google_analytics_pageview.jsp" --%>

		<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/disease_portal.css" />

		<!--===================== End Template Head  ===========================-->

		<base href="/diseasePortal">

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

<style>
#headerContainer {
    background-color: #DDDDDD;
    height: 148px;
    width: 100%;
    /*border:1px solid;*/
}
#hdpBannerContainer {
    background-color: #DDDDDD;
    height: 148px;
    position:relative;
    width: 820px;
    margin: 0 auto;
    /*border:1px solid;*/
}
.headerHomepageLinks {
  text-decoration:none;
}
a.headerHomepageLinks:link {color:#000000;}
a.headerHomepageLinks:visited {color:#000000;}
a.headerHomepageLinks:hover {color:#000000;}
a.headerHomepageLinks:active {color:#000000;}

.tab-nav
{
	float: left;
	/*margin-right: 2px; */
	padding: 5px 10px;
	cursor: pointer;
}
.tab-content
{
	clear: both;
	text-align:center;
	border: 1px solid gray;
	border-top-width: 0;
	padding: .25em .5em;
}
.inactive-tab
{
	border: 1px solid #A3A3A3;
	border-bottom-color: black;
	background: #D8D8D8 url(http://yui.yahooapis.com/2.8.2r1/build/assets/skins/sam/sprite.png) repeat-x;
	margin-top: 4px;
}
.inactive-tab:hover
{
	background:#bfdaff url(http://yui.yahooapis.com/2.8.2r1/build/assets/skins/sam/sprite.png) repeat-x left -1300px;
	outline:0;
}
.active-tab, .active-tab a:hover
{
	border: 1px solid gray;
	border-top-color: black;
	border-bottom-width: 0;
	padding-bottom: 10px;
}
.active-content
{
    display: block;
}
.inactive-content
{
    display: none;
}
#hmdcTabsUL
{
    margin-left: 30px;
    margin-right: 30px;
    border: none;
}
</style>

<style>
   .yui-skin-sam .yui-dt th {
      background:url(${configBean.WEBSHARE_URL}images/cre/SpriteYuiOverRide.png)
      repeat-x 0 -1300px;
   }
   .yui-skin-sam th.yui-dt-asc,.yui-skin-sam th.yui-dt-desc {
      background:url(${configBean.WEBSHARE_URL}images/cre/SpriteYuiOverRide.png)
      repeat-x 0 -1400px;
   }
   .yui-skin-sam th.yui-dt-sortable .yui-dt-liner {
      background:url(${configBean.WEBSHARE_URL}images/cre/creSortableArrow.png)
      no-repeat right;
   }
   .yui-skin-sam th.yui-dt-asc .yui-dt-liner {
      background:url(${configBean.WEBSHARE_URL}images/cre/creDownArrow.png)
      no-repeat right;
   }
   .yui-skin-sam th.yui-dt-desc .yui-dt-liner {
      background:url(${configBean.WEBSHARE_URL}images/cre/creUpArrow.png)
      no-repeat right;
   }
   .facetFilter .yui-panel .bd {
      width: 285px
   }

</style>

<!--begin custom header content for this example-->
<script type="text/javascript">
    document.documentElement.className = "yui-pe";
</script>
