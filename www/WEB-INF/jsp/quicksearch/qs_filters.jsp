<div id="filterNote" class="example" style="padding-top: 6px;">
  Note: Filters can be applied to data within each tab.
</div>
<!-- for filter popup (re-used by all filter buttons) -->
<div class="facetFilter">
	<div id="facetDialog">
		<div class="hd">Filter</div>
		<div class="bd">
			<!-- Form action is intended to be just a no-op here. -->
			<form:form method="GET"
				action="${configBean.WEBSHARE_URL}images/filter.png">
				<img src="/fewi/mgi/assets/images/loading.gif">
			</form:form>
		</div>
	</div>
</div>
	