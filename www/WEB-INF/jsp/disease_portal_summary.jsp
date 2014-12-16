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

<div id="summary">
	<div id="querySummary" style="float:left;margin-left:0">
		<div class="innertube" style="width:500px;">
			<div id="searchSummary"> <!-- filled via js --> </div>
		</div>
	</div>

	<div id="paginationTop" style="float:right; margin-top: 0px;">&nbsp;</div>

	<div id="filterSection" style="width:600px">
		<div id="contentcolumn" style="width:480px; margin-left: 510px;">
			<span id="filterLabel" class="label">Filter results by:</span>
			<a id="featureTypeButton" class="filterButton">Genome&nbsp;Feature&nbsp;Type&nbsp;<img src="${configBean.WEBSHARE_URL}images/filter.png" width="8" height="8"/></a>
			<br/>
			<div id="breadbox">
				<div id="filterSummary" class="filters" style="display:none">
					<span class="label">Filtered by:</span>
					&nbsp;<span id="defaultText" style="display:none">No filters selected.</span>
					<span id="filterList">
						<span id="filterReset" class="filterItem">Remove row/column filters</span>
					</span><br/>
				</div>
			</div>
		</div>
	</div>

	<div class="facetFilter" style="">
		<div id="facetDialog">
			<div class="hd">Filter</div>
			<div class="bd">
				<form:form method="GET" action="${configBean.FEWI_URL}diseasePortal/summary">
					<img src="/fewi/mgi/assets/images/loading.gif">
				</form:form>
			</div>
		</div>
	</div>

	<br clear="all" />
</div>


<div id="resultSummary" class="yui-navset">
	<ul class="yui-nav">
		<li><a id="gridtab" href="#grid"><em>Gene Homologs x Phenotypes/Diseases</em></a></li>
		<li><a id="genestab" href="#genes"><em>Genes (<span id="totalGenesCount"></span>)</em></a></li>
		<li><a id="diseasestab" href="#diseases"><em>Diseases (<span id="totalDiseasesCount"></span>)</em></a></li>
	</ul>
	<div class="yui-content">
		<div>
			<div id="legendArea" style="background-color:#F7F7F7; border:2px solid #AAA; width:860px; margin-bottom: 4px;">
				<div style="padding:5px 2px;">
					<table>
						<tr>
							<td><div style="font-weight:bold; padding-right: 5px;">Legend:</div></td>
							<td>The matrix includes all phenotypes/diseases associated with mouse models and human genes returned.</td>
						</tr><tr>
							<td></td>
							<td><span class="highlight">Highlighted Columns</span> contain at least one phenotype or disease result matching your search term(s)</td>
						</tr><tr>
							<td></td>
							<td style="padding-left: 20px; padding-top: 5px;">
								<table>
									<tr>
										<td>
											<div style="width:20px; height:20px; background-color: #49648B; display: inline-block;">
												<div style="width:100%;height:100%; background: url('${configBean.FEWI_URL}assets/images/hdp/human_cell_sprite.gif') -6px 0px;"></div>
											</div>
										</td>
										<td style="padding-left: 5px;">
											<div style="display:inline-block;"> - Terms are annotated to genes in <strong style="background-color:#ffdab3; color:#000; font-size:12px;">human</strong>/<strong style="background-color: #49648B; color:#FFF; font-size:12px;">mouse</strong>. 
												Darker colors indicate <a onclick="javascript:openUserhelpWindow('disease_connection_help.shtml#hdp_results'); return false;" href="${configBean.USERHELP_URL}disease_connection_help.shtml#hdp_results">more annotations</a>.
											</div>
										</td>
									</tr>
									<tr>
										<td><strong>N</strong></td>
										<td style="padding-left: 5px;">- No abnormal phenotype observed.</td>
									</tr>
								</table>
							</td>
						</tr><tr>
							<td style="vertical-align: top;"><span style="font-weight:bold;">NOTE:</span></td>
							<td>In searches with phenotype/disease terms, only the phenotypes/diseases of the matching models/genes are displayed. In searches using gene or location parameters only, the complete phenotype profiles of the matching gene mutations are displayed. <a onclick="javascript:openUserhelpWindow('disease_connection_help.shtml'); return false;" href="${configBean.USERHELP_URL}disease_connection_help.shtml">More...</a></td>
						</tr>
					</table>
				</div>
			</div>

			<div id="griddata"></div>
			<div id="griddata_loading"><img src="/fewi/mgi/assets/images/loading.gif" height="24" width="24"> Preparing Data and Building Grid. Please Wait...</div>
		</div>
		<div>
			<div style="padding-bottom:6px; padding-top:4px;">
				<span class="label">Export:</span><a id="markersTextDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/text.png" width="10" height="10" />Text File</a> 
			</div>
			<div id="genesdata"></div>
			<div id="debug"></div>
		</div>
		<div>
			<div style="padding-bottom:6px; padding-top:4px;">
				<span class="label">Export:</span><a id="diseaseTextDownload" class="filterButton"><img src="${configBean.WEBSHARE_URL}images/text.png" width="10" height="10" />Text File</a> 
			</div>
			<div id="diseasesdata"></div>
		</div>
	</div>
	<div id="paginationBottom" style="float:right;">&nbsp;</div>

	<div id="showMgiHumanGenesHelpDivDialog">
		<div class="hd">Source of Gene-Disease Associations</div>
		<div class="bd">
			<p>Disease associations for human genes are from the NCBI mim2gene_medgen file and include annotations from OMIM, NCBI curation, Gene Reviews, and Gene Tests. Mouse genes are associated with human diseases through mouse genotypes described in publications.</p>
		</div>
	</div>
	<div id="showMgiHumanDiseaseHelpDivDialog">
		<div class="hd">Source of Gene-Disease Associations</div>
		<div class="bd">
			<p>Disease associations for human genes are from the NCBI mim2gene_medgen file and include annotations from OMIM, NCBI curation, Gene Reviews, and Gene Tests.</p>
		</div>
	</div>
</div>
