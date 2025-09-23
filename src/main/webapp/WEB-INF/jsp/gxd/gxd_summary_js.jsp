<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/external/d3.min.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/widgets/SuperGrid.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/fewi_utils.js"></script>
<script type="text/javascript" src="${configBean.FEWI_URL}assets/js/gxd_summary_matrix.js"></script>
<c:if test="${configBean.gxdDebugMode == 'true'}">
<script>
	window.debugMode = true;
</script>
</c:if>

<script>

document.addEventListener('DOMContentLoaded', function() {

    const theilerStageFilterButton = document.getElementById('theilerStageFilter');
    if (theilerStageFilterButton) {
        theilerStageFilterButton.addEventListener('click', function() {
            console.log('GA4 custom event sent: ----filterOpened');
            gtag('event', 'gxdSumFilterClick', {
                'filterOpened': 'theilerStageFilterButton'
            });
        });
    }

    const systemFilterButton = document.getElementById('systemFilter');
    if (systemFilterButton) {
        systemFilterButton.addEventListener('click', function() {
            console.log('GA4 custom event sent: ----filterOpened');
            gtag('event', 'gxdSumFilterClick', {
                'filterOpened': 'systemFilterButton'
            });
        });
    }

    const coFilterButton = document.getElementById('coFilter');
    if (coFilterButton) {
        coFilterButton.addEventListener('click', function() {
            console.log('GA4 custom event sent: ----filterOpened');
            gtag('event', 'gxdSumFilterClick', {
                'filterOpened': 'coFilterButton'
            });
        });
    }

    const assayTypeFilterButton = document.getElementById('assayTypeFilter');
    if (assayTypeFilterButton) {
        assayTypeFilterButton.addEventListener('click', function() {
            console.log('GA4 custom event sent: ----filterOpened');
            gtag('event', 'gxdSumFilterClick', {
                'filterOpened': 'assayTypeFilterButton'
            });
        });
    }

    const detectedFilterButton = document.getElementById('detectedFilter');
    if (detectedFilterButton) {
        detectedFilterButton.addEventListener('click', function() {
            console.log('GA4 custom event sent: ----filterOpened');
            gtag('event', 'gxdSumFilterClick', {
                'filterOpened': 'detectedFilterButton'
            });
        });
    }

    const tmpLevelFilterButton = document.getElementById('tmpLevelFilter');
    if (tmpLevelFilterButton) {
        tmpLevelFilterButton.addEventListener('click', function() {
            console.log('GA4 custom event sent: ----filterOpened');
            gtag('event', 'gxdSumFilterClick', {
                'filterOpened': 'tmpLevelFilterButton'
            });
        });
    }

    const wildtypeFilterButton = document.getElementById('wildtypeFilter');
    if (wildtypeFilterButton) {
        wildtypeFilterButton.addEventListener('click', function() {
            console.log('GA4 custom event sent: ----filterOpened');
            gtag('event', 'gxdSumFilterClick', {
                'filterOpened': 'wildtypeFilterButton'
            });
        });
    }

    const mrkTypeFilterButton = document.getElementById('markerTypeFilter');
    if (mrkTypeFilterButton) {
        mrkTypeFilterButton.addEventListener('click', function() {
            console.log('GA4 custom event sent: ----filterOpened');
            gtag('event', 'gxdSumFilterClick', {
                'filterOpened': 'mrkTypeFilterButton'
            });
        });
    }

    const goMfFilterButton = document.getElementById('goMfFilter');
    if (goMfFilterButton) {
        goMfFilterButton.addEventListener('click', function() {
            console.log('GA4 custom event sent: ----filterOpened');
            gtag('event', 'gxdSumFilterClick', {
                'filterOpened': 'goMfFilterButton'
            });
        });
    }

    const goBpFilterButton = document.getElementById('goBpFilter');
    if (goBpFilterButton) {
        goBpFilterButton.addEventListener('click', function() {
            console.log('GA4 custom event sent: ----filterOpened');
            gtag('event', 'gxdSumFilterClick', {
                'filterOpened': 'goBpFilterButton'
            });
        });
    }

    const goCcFilterButton = document.getElementById('goCcFilter');
    if (goCcFilterButton) {
        goCcFilterButton.addEventListener('click', function() {
            console.log('GA4 custom event sent: ----filterOpened');
            gtag('event', 'gxdSumFilterClick', {
                'filterOpened': 'goCcFilterButton'
            });
        });
    }

    const mpFilterButton = document.getElementById('mpFilter');
    if (mpFilterButton) {
        mpFilterButton.addEventListener('click', function() {
            console.log('GA4 custom event sent: ----filterOpened');
            gtag('event', 'gxdSumFilterClick', {
                'filterOpened': 'mpFilterButton'
            });
        });
    }

    const doFilterButton = document.getElementById('doFilter');
    if (doFilterButton) {
        doFilterButton.addEventListener('click', function() {
            console.log('GA4 custom event sent: ----filterOpened');
            gtag('event', 'gxdSumFilterClick', {
                'filterOpened': 'doFilterButton'
            });
        });
    }

    const heatmaptabButton = document.getElementById('heatmaptabButton');
    if (heatmaptabButton) {
        heatmaptabButton.addEventListener('click', function() {
            console.log('GA4 custom event sent: ----heatmap launch');
            gtag('event', 'gxdSumHeatMapClick', {});
        });
    }

    const heatmaptabImage = document.getElementById('heatmaptabImage');
    if (heatmaptabImage) {
        heatmaptabImage.addEventListener('click', function() {
            console.log('GA4 custom event sent: ----heatmap launch');
            gtag('event', 'gxdSumHeatMapClick', {});
        });
    }


    
});
</script>
