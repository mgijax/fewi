<style>
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
</style>

<div id="diseasePortalSearch">
	
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
      <div id="geneFilePlaceholder" style="height:30px; width: 1px;"></div>
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
        <label><input id="organismHuman1" name="organism" class="organism" type="radio" value="human"/>Human(GRCh38)</label>
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

    <input id="locationsFileName" type="hidden" name="locationsFileName" value="${locationsFileName}">
    <input id="geneFileName" type="hidden" name="geneFileName" value="${geneFileName}">
    <input type="hidden" name="fGene" id="fGene" />
	<input type="hidden" name="fHeader" id="fHeader" />
	<input type="hidden" name="numDCol" id=numDCol />
    <input id="submit1" style="position:absolute; top:225px; left:700px; width:60px; font-size:20px;"
      name="submit" class="formButtons" value="GO" type="submit"><br/>
    <input id="reset1" class="formButtons"
      style="position:absolute; top:265px; left:700px; width:60px; font-size:14px;" type="reset" >
    </form:form>

    <div style="position:absolute; top:220px; left:20px; width:600px;">
		<br/>
      <div style='font-size:150%;'>
        ------------------------------------------------------------------------
      </div>
      <form action="${configBean.FEWI_URL}diseasePortal/uploadFile" method="post" enctype="multipart/form-data"
		id="hiddenFileForm" name="hiddenFileForm" target="hiddenfileform_if">
       <div style='margin-top:6px; margin-left:20px;'>
        <span style="font-size:150%;">Upload a VCF File: <input id="locationsFileInput" type="file" name="file"></span>
        <img id="locationsFileHelpImg" src="${configBean.WEBSHARE_URL}images/help_large_transp.gif" />
		<div id="locationsFileHelp"> 
			<div class="hd">VCF File Uploading Tips</div> 
			<div class="bd">
				<p>The file upload functionality will be expanded in coming releases. For now, the following restrictions are in place:</p>
				<ul>
					<li>Files are assumed to be in VCf v.4.0 or later format:
						<ul>
							<li>Column 1 = chromosome</li>
							<li>Column 2 = coordinate</li>
							<li>Column 3 = SNP ID (if any)</li>
							<li>Column 7 = Filter value</li>
						</ul>
					</li>
					<li>Files over 25MB cannot be processed</li>
					<li>Only the first 100,000 lines of a file can be processed</li>
					<li>Lines containing a SNP ID (RS ID) in column 3 are rejected
						<br/>(emphasizing only unknown variants)
					</li>
					<li>Lines containing values other than "pass", ".", or no value (null) in column 7 are rejected</li>
				</ul>
				<p>If your file contains SNP IDs in column 3 and you wish to include those rows in the search, 
					please edit the file to remove the IDs (leaving column 3 empty).
					Save as a tab-delimited file in a format other than Unicode.
				</p>
				<p>If your file is larger than 25MB or has more than 100,000 lines, please edit the file to break it into smaller files.
					Save as a tab-delimited file in a format other than Unicode.</p>
			</div> 
		</div> 
      </div>
      <div style='margin-left:220px; padding-top:5px;'>
	<!-- These are here to make the user feel better, but should not be submitted as extra organism values -->
        <label><input id="organismHuman2" name="organismIgnore" class="organism" type="radio" value="human"/>Human(GRCh38)</label>
        <label><input id="organismMouse2" name="organismIgnore" class="organism" type="radio" value="mouse" checked="checked"/>Mouse(GRCm38)</label>
      	<div id="locationsFileNotify"><c:if test="${not empty locationsFileName}"><span >(Using cached file [${locationsFileName}])</span></c:if></div>
      </div>
	    <input type="hidden" name="field" value="locationsFile">
	    <input type="hidden" name="type" value="vcf">
      </form>
    </div>
	<div id="geneFileDiv" style="position:absolute; top:140px;">
		<form action="${configBean.FEWI_URL}diseasePortal/uploadFile" method="post" enctype="multipart/form-data"
			id="hiddenFileForm2" name="hiddenFileForm" target="hiddenfileform_if">
			<span style="">Upload a File: <input id="geneFileInput" type="file" name="file"></span>
			<div id="geneFileNotify"><c:if test="${not empty geneFileName}"><span >(Using cached file [${geneFileName}])</span></c:if></div>
			<input type="hidden" name="field" value="geneFile">
		    <input type="hidden" name="type" value="singleCol">
		</form>
	</div>

    </div>
  </div> <!-- hdpQueryFormWrapper -->
</div> <!-- hdpPageWrapper -->
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
