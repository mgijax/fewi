<%@ include file="/WEB-INF/jsp/templates/templateHead.html" %>

<%@ include file="/WEB-INF/jsp/includes.jsp" %>

<script type="text/javascript">
        var fewiurl = "${configBean.FEWI_URL}";
        var mrkID = "${mrkID}";
</script>

<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/external/d3.min.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/widgets/SuperGrid.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/fewi_utils.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/gxd_summary_matrix.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/gxd_pheno_matrix.js"></script>

<link rel="stylesheet" type="text/css" href="${configBean.FEWI_URL}assets/css/gxd/gxd_summary.css" />
<style>
</style>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStart.html" %>

<div id="phenoGridWrapper">
  <div id="ggTarget" class=""></div>
</div>

<%@ include file="/WEB-INF/jsp/templates/templateBodyStop.html" %>
