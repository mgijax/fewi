<style>
.hl
{
	font-size:80%;
}
.yui-skin-sam .yui-dt-liner
{
	padding: 4px 4px 4px 4px;
}
td.yui-dt-col-coordinate
{
	white-space: nowrap;
}
.diseaseList li
{
	list-style-type: circle;
	padding: 0px;
	margin-left: 16px; 
}
/* #griddata { overflow-x: auto; } */

.yui-skin-sam .yui-dt th{
  background:url(${configBean.WEBSHARE_URL}images/cre/SpriteYuiOverRide.png)
  repeat-x 0 -1300px;
}
.yui-skin-sam th.yui-dt-asc,.yui-skin-sam th.yui-dt-desc{
  background:url(${configBean.WEBSHARE_URL}images/cre/SpriteYuiOverRide.png)
  repeat-x 0 -1400px;
}

.yui-skin-sam th.yui-dt-sortable .yui-dt-liner{
  background:url(${configBean.WEBSHARE_URL}images/cre/creSortableArrow.png)
  no-repeat right;
}
.yui-skin-sam th.yui-dt-asc .yui-dt-liner{
  background:url(${configBean.WEBSHARE_URL}images/cre/creDownArrow.png)
  no-repeat right;
}
.yui-skin-sam th.yui-dt-desc .yui-dt-liner{
  background:url(${configBean.WEBSHARE_URL}images/cre/creUpArrow.png)
  no-repeat right;
}
#resultSummary
{
	display:table;
}
#filterReset 
{ 
	cursor: pointer; 
}
#gridFilterIndicator
{
	margin: 8px;
}

.summary-helptext
{
	color: purple;
	font-size:100%;
	font-style:italic;
}
.hide {display: none;}


</style>
<div id="summary">
    <div id="paginationTop" style="float:right;">&nbsp;</div>
    <div id="querySummary" style="float:none;margin-left:0;">
        <div class="innertube" style="width:500px">
            <div id="searchSummary"> <!-- filled via js --> </div>
            <div id="breadbox"><div id="gridFilterIndicator" class="hide">
            	<span id="filterReset" class="filterItem">Remove Filters</span>
            </div></div>
        </div>
    </div>
    <br clear="all" />
</div>
<div id="resultSummary" class="yui-navset">
	<ul class="yui-nav">
		<li><a id="gridtab" href="#grid"><em>Traits &amp; Models</em></a></li>
        <li><a id="genestab" href="#genes"><em>Genes (<span id="totalGenesCount"></span>)</em></a></li>
        <li><a id="diseasestab" href="#diseases"><em>Diseases (<span id="totalDiseasesCount"></span>)</em></a></li>
    </ul>
    <div class="yui-content">
    	<div>
    		<div class="">
    			<div id="legendArea" style="background-color:#F7F7F7; border:2px solid #AAA; width:700px; margin-bottom: 4px;">
			    	<div style="padding:5px 2px;">
			            	<div style="font-weight:bold; display: inline-block;">Legend:</div>
			                <div style="width:26px; height:20px; background-color: #49648B; display: inline-block;">
			                	<div style="width:100%;height:100%; background: url('${configBean.FEWI_URL}assets/images/hdp/human_cell_sprite.gif') 0px 0px;"></div></div>
			            	<div style="display:inline-block;"> - Terms are annotated to genes in <strong style="background-color:#ffdab3; color:#000; font-size:12px;">human</strong>/<strong style="background-color: #49648B; color:#FFF; font-size:12px;">mouse</strong>. Darker colors indicate more annotations.</div>
			        </div>
			        <div style="margin-left:65px;"><span style=" border:1px solid; border-left-color:#FF0000; border-right-color:#FF0000; border-bottom-color:#CCC; border-top-color:#CCC;">&nbsp;&nbsp;&nbsp;</span> - Affected system contains the queried term.</div>
			        <div style="display:block; margin-left:65px;"><strong>N</strong> - No abnormal phenotype observed.</div>
		    		<div class="summary-helptext shared-helptext" style="color:#000; padding:4px 2px; font-style:normal;">
		    			<span style="font-weight:bold;">NOTE:</span> Genes may have additional phenotypes. Query by gene or location for complete characterization.
		    		</div>
			        </div>
    		</div>
	        <div id="griddata"></div>
	        <div id="griddata_loading"><img src="/fewi/mgi/assets/images/loading.gif" height="24" width="24"> Searching...</div>
        </div>
        <div>
        	<div id="genes-pheno-helptext" class="summary-helptext pheno-helptext hide">
	        	Mouse and human homologs with phenotype or disease annotations matching your search query. 
        	</div>
    		<div id="genes-gene-helptext" class="summary-helptext genes-helptext hide">
	    		Mouse and human homologs matching your search query with disease and phenotype associations. 
    		</div>
    		<div class="summary-helptext shared-helptext">
    			Results are limited to single locus mouse mutations but may contain background-dependent associations annotated in MGI. 
	    		<br/>Human disease data obtained from NBCI and OMIM.
    		</div>
			<div style="padding-bottom:6px; padding-top:4px;">
				<span class="label">Export:</span>
				<a id="markersTextDownload" class="filterButton">
				<img src="${configBean.WEBSHARE_URL}images/text.png" width="10" height="10" /> 
				Text File</a> 
			</div>

	       <div id="genesdata"></div>
			<!-- <hr/>
			<i>DEBUG OUTPUT:</i> <b>Terms matching your query</b> (sorted by score, then alpha):
			<div id="termsDebug"></div> -->
			<div id="debug"></div>
        </div>
        <div>
        	<div id="diseases-pheno-helptext" class="summary-helptext pheno-helptext hide">
	        	Diseases matching your search query and/or diseases with a mouse model displaying a matching phenotype. 
	        	<br/>Mouse genes displayed are limited to single locus mouse mutations but may contain background-dependent associations annotated in MGI. 
        	</div>
    		<div id="diseases-gene-helptext" class="summary-helptext genes-helptext hide">
	    		Diseases associated with the mouse and human homologs matching your search query. 
	    		<br/>Results are limited to single locus mouse mutations but may contain background-dependent associations annotated in MGI. 
    		</div>
    		<div class="summary-helptext shared-helptext">
    			All human data are obtained from NCBI and OMIM.
    		</div>
			<div style="padding-bottom:6px; padding-top:4px;">
				<span class="label">Export:</span>
				<a id="diseaseTextDownload" class="filterButton">
				<img src="${configBean.WEBSHARE_URL}images/text.png" width="10" height="10" /> 
				Text File</a> 
			</div>
	        <div id="diseasesdata"></div>
        </div>
</div>
	<div id="paginationBottom" style="float:right;">&nbsp;</div>
