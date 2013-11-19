<style>
/* styles for autocomplete */.ui-autocomplete {
  max-height: 300px;
  overflow-y: auto;
  /* prevent horizontal scrollbar */
  overflow-x: hidden;
        font-size:90%;
}
/* IE 6 doesn't support max-height
 * we use height instead, but this forces the menu to always be this tall
 */
* html .ui-autocomplete {
  height: 300px;
} 
.ui-menu .ui-menu-item {
        padding-left:0.4em;
} 
.ui-menu .ui-menu-item a {
        padding:0px;
} 

span.smallGrey { font-size: 75%; color: #999999; }

#locationsDiv 
{
	display: table;
}
#locationsDiv div
{
	display: table-cell;
	vertical-align: middle;
	padding: 0px 6px;
}
.organism
{
	margin-left: 6px;
	margin-right: 3px;
}
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

.hide { display:none; }
#alertBoxPosition
{
	top: 120px;left: 260px;
}
#hiddenFileForm span
{
	padding-left: 50px;
	font-style: italic;
	color: yellowgreen;
	font-weight: bold;
}
<![endif]-->
</style>

<style>
textarea {resize:none; background-color:#FFE4B5;}
#hdpPageWrapper {
  width: 820px;
  height: 330px;
  position: relative;
  margin: 0 auto;
  font-family: Verdana,Arial,Helvetica;
  /*background-color:#DDDDDD;*/
}
#hdpQueryFormWrapper{
  left:6px;
  width: 804px;
  height: 320px;
  border:2px solid;
  border-radius:22px;
  position:absolute;
  background-color:#D0E0F0;
}
.formButtons{
  font-family:Verdana, Arial,Helvetica;
  color:#002255;
  font-weight:bolder;
  border: 1px #7D95B9 solid;
  padding:2px;
  cursor: pointer;
  background-color:#FFE4B5;
}
</style>

<div id="diseasePortalSearch" class="yui-navset">
<ul class="yui-nav">
    <li class="selected"><a href="#disease-portal-search"><em>Human Disease Search</em></a></li>
</ul>
<div class="yui-content">
<div id="standard-qf">

	
<div id="hdpPageWrapper" >

  <div id="hdpQueryFormWrapper" >
    <div class='relativePos' >
    <form:form method="GET" commandName="diseasePortalQueryForm" action="${configBean.FEWI_URL}diseasePortal/summary">

    <div style="position:absolute; top:2px; left:2px; width:250px;">
    <div class='relativePos'>
      <div style="position:absolute; top:6px; left:15px; ">
        <span class='queryHeaderText'>Search by genes</span>
      </div>
      <div style="position:absolute; top:60px; left:6px; ">
      <form:textarea path="genes" style="height:80px; width:240px;" class=""/>
      Ex:
      <a href="${configBean.FEWI_URL}diseasePortal/summary?genes=Bmp4">Bmp4</a>,
      <a href="${configBean.FEWI_URL}diseasePortal/summary?genes=Pax*">Pax*</a>,
      <a href="${configBean.FEWI_URL}diseasePortal/summary?genes=NM_013627">NM_013627</a>
      <br/><br/>
      Enter symbols, names or ids.<br/>
      Use * for wildcard.
      </div>
    </div>
    </div>

    <div style="position:absolute; top:2px; left:254px; width:300px;">
    <div class='relativePos'>
      <div style="position:absolute; top:6px; left:15px; ">
        <span class='queryHeaderText'>Search by genome locations</span></br>
        <div style='margin-left:6px; padding-top:5px;'>
        <label><input id="organismHuman1" name="organism" class="organism" type="radio" value="human"/>Human(GRCh37)</label>
        <label><input id="organismMouse1" name="organism" class="organism" type="radio" value="mouse" checked="checked"/>Mouse(GRCm38)</label>
        </div>
      </div>
      <div style="position:absolute; top:60px; left:6px; ">
      <textarea id="locations" name="locations" style="height:80px; width:280px;"></textarea>
      Ex:
      <a href="${configBean.FEWI_URL}diseasePortal/summary?locations=Chr12:3000000-10000000">Chr12:3000000-10000000</a>
      <br/><br/>
      Need to convert genome build?<br/>
      Use this <a href='http://www.ncbi.nlm.nih.gov/genome/tools/remap#tab=asm'>converter tool</a>.
      </div>
    </div>
    </div>

    <div style="position:absolute; top:2px; left:546px; width:250px;">
    <div class='relativePos'>
      <div style="position:absolute; top:6px; left:15px; ">
        <span class='queryHeaderText'>Search by disease</br> or phenotype terms</span>
      </div>
      <div style="position:absolute; top:60px; left:6px; ">
      <textarea id="phenotypes" name="phenotypes" style="height:80px; width:240px;"></textarea>
      Ex:
      <a href="${configBean.FEWI_URL}diseasePortal/summary?phenotypes=diabetes">diabetes</a>,
      <a href="${configBean.FEWI_URL}diseasePortal/summary?phenotypes=105830">105830</a>
      <br/><br/>
      Select from autocomplete or continue typing.  Use quotes for exact match.
      </div>
    </div>
    </div>

     <input id="locationsFileName" type="hidden" name="locationsFileName" value="">
    <input style="position:absolute; top:225px; left:700px; width:60px; font-size:20px;"
      name="submit" class="formButtons" value="GO" type="submit"><br/>
    <input class="formButtons"
      style="position:absolute; top:265px; left:700px; width:60px; font-size:14px;" type="reset" >

    </form:form>

    <div style="position:absolute; top:220px; left:20px; ">
      <div style='font-size:150%;'>
        ------------------------------------------------------------------------
      </div>
      <form action="${configBean.FEWI_URL}diseasePortal/uploadFile" method="post" enctype="multipart/form-data"
		id="hiddenFileForm" name="hiddenFileForm" target="hiddenfileform_if">
       <div style='font-size:150%; margin-top:12px; margin-left:20px;'>
        Upload a VCF File: <input id="locationsFileInput" type="file" name="file">
      </div>
      <div style='margin-left:220px; padding-top:5px;'>
	<!-- These are here to make the user feel better, but should not be submitted as extra organism values -->
        <label><input id="organismHuman2" name="organismIgnore" class="organism" type="radio" value="human"/>Human(GRCh37)</label>
        <label><input id="organismMouse2" name="organismIgnore" class="organism" type="radio" value="mouse" checked="checked"/>Mouse(GRCm38)</label>
      	<c:if test="${not empty locationsFileName}"><br/><span id="locationsFileNotify">(Using cached file [${locationsFileName}])</span></c:if>
      </div>
	    <input type="hidden" name="field" value="locationsFile">
	    <input type="hidden" name="type" value="vcf">
      </form>
    </div>



    </div>
  </div> <!-- hdpQueryFormWrapper -->
</div> <!-- hdpPageWrapper -->
</div> <!-- standard-qf -->
</div> <!-- yui-content -->
</div> <!--  diseasePortalSearch -->

<script type="text/javascript">
var _idMap = {"organismHuman1":"organismHuman2",
        "organismHuman2":"organismHuman1",
        "organismMouse2":"organismMouse1",
        "organismMouse1":"organismMouse2",
        };
$(function(){
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
});
</script>