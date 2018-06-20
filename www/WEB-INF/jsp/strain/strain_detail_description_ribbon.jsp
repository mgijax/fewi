	<div class="row">
		<div class="header <%=leftTdStyles.getNext() %>" id="descriptionRibbonLabel">
			Description
		</div>
		<div class="detail <%=rightTdStyles.getNext() %> summaryRibbon">
			<section class="summarySec1 ">
				<!-- We don't want to superscript this, as there are HTML links embedded. -->
				<div id="description">${strain.description}</div>
			</section>
		</div>
	</div>
	<style>
	#description { max-height: 125px; overflow-y: auto; overflow-x: hidden; margin-left: 15px; }
	</style>