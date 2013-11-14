<link rel="stylesheet" href="http://www.informatics.jax.org/webshare/css/mgi.css" type="text/css">
<script type="text/javascript" src="${configBean.WEBSHARE_URL}js/jquery-1.10.2.min.js"></script>

<style type="text/css">

.stripe1 {background-color:#FFF;}
.stripe2 {background-color:#EFEFEF;}
  
.popupLegend {
margin-left:20px;
border-collapse: collapse;
border: 1px solid #666;
font-size: 9px;
color: #555;
padding: 2px;
}

.popupLegend td{
border: 1px solid #666;
font-size: 9px;
color: #555;
padding: 2px;
text-align: center;
}

.sexSymbol {
font-size: 12px;
}
  
.genoPopupHeader {
  min-height:82px; 
  min-width:500px;
  background-color:#DFEFFF; 
  margin-bottom:5px;
  padding:4px;
  border:thin solid #002255;
}

.mpSystemRow {
  font-weight:bold;
  font-family: Arial,Helvetica;
  font-size: 12px;
  line-height: 15px;
 } 
.mpTerm {  
  font-family: Verdana,Arial,Helvetica;
  font-size: 12px;
  line-height: 14px;
}
.mpNote {
  font-family: Verdana,Arial,Helvetica;
  font-size: 10px;
  line-height: 15px;
  margin-bottom:3px;
}

.genotypeType {
  font-family: Verdana,Arial,Helvetica;
  font-size: 10px;
}

.comboAndStrain {
  font-family: Verdana,Arial,Helvetica;
  font-size: 10px;
}

.genotypeCombo {
  line-height:22px;
}
.cellLines {
  font-family: Verdana,Arial,Helvetica;
  font-size: 10px;
  padding-top: 6px;
}


.hmGeno {
  border: thin solid rgb(249, 149, 0); 
  background-color: rgb(255, 193, 102); 
  height: 10px; 
  width: 30px; 
}
.htGeno {
  border: thin solid rgb(0, 206, 242); 
  background-color: rgb(168, 242, 255);
  height: 10px; 
  width: 30px; 
}
.cxGeno {
  border: thin solid rgb(158, 128, 215); 
  background-color: rgb(204, 204, 255); 
  height: 10px; 
  width: 30px; 
}
.cnGeno {
  border: thin solid rgb(166, 208, 128); 
  background-color: rgb(211, 255, 168); 
  height: 10px; 
  width: 30px; 
}
.tgGeno {
  border: thin solid rgb(255, 113, 198); 
  background-color: rgb(255, 179, 224); 
  height: 10px; 
  width: 30px; 
}
.otGeno {
  border: thin solid rgb(96, 64, 32); 
  background-color: rgb(227, 200, 172); 
  height: 10px; 
  width: 30px; 
}

.dmFootnote ul, .dmFootnote li
{
	margin:0px;
	padding:0px;
}
.sexTd
{
	text-align: center;
	vertical-align:top;
}

.termDiv
{
	margin-bottom: 10px;
}

.sexDiv th
{
	font-size: 10px;
	color: #025;
	font-weight: bold;
}

.borderTop td
{
	border-top: solid 1px #eee;
}
.mp_glyph {height:15px; width:15px;}
</style>

<%@ include file="/WEB-INF/jsp/phenotype_table_geno_imports.jsp" %>