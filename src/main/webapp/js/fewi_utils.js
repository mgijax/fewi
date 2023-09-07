/*
*	This is intended to be a helpful place to create shared classes and functions for
*	fewi content. 
*
*	@author kstone - 2013/07/26
*
*
*	Naming conventions:
*		-classes begin "MGI"
*		-functions begin "mgi"
*		-the rest is camelcase
*		This is to be able to easily identify methods that come from shared libraries such as this.
*/



/*
 * A function to deserialise request strings
 * 	E.g. field1=text&field2=pax&field2=kit
 * 		becomes {"field1":"text","field2":["pax","kit"]}
 */
function mgiParseRequest(request)
{
	var reply = {};
	var kvPairs = request.split('&');
	for (pair in kvPairs) {
		var kv = kvPairs[pair].split('=');
		if(kv.length>1)
		{
			var key = kv[0];
			var value = kv[1];
			if(!reply[key])
			{
				reply[key] = value;
			}
			else
			{
				// if we have more than one value for a key, turn it into an array
				if(!$.isArray(reply[key]))
				{
					reply[key] = [reply[key]];
				}
				reply[key].push(value);
			}
		}
	}
	return reply;
};

var MGIRequestParser = function()
{
	// functions to be configured and set if needed
	// getExtraParams is a hook you can overwrite to add extra params to a request (like the current tab)
	this.getExtraParams = function(){return undefined;};
	
	// a function to convert request string into an array
	this.parseRequest=mgiParseRequest;
	
	// define closure
	var _self = this;
	
	/*
	 * Returns a request string for consumption by the DataSource
	 * 	always includes results,startIndex,sort, dir, and querystring
	 *  additional parameters can be supplied via the getExtraParams function
	 */
	this.generateRequest = function(sortedBy, startIndex, results, facets) 
	{
		var sort = defaultSort;
		var dir = 'asc';

		if (!YAHOO.lang.isUndefined(sortedBy) && !YAHOO.lang.isNull(sortedBy)){
			sort = sortedBy['key'];
			dir = sortedBy['dir'];
		}
		// Converts from DataTable format "yui-dt-[dir]" to server value "[dir]"
		if (dir.length > 7){
			dir = dir.substring(7);
		}
		results   = results || RESULTS_PAGE_SIZE;
		
		var stateParams = "results="+results+"&startIndex="+startIndex+"&sort="+sort+"&dir="+dir;
		var facetParams = [];
		for (key in facets){
			list = facets[key];
			for(var i=0; i < list.length; i++){
				facetParams.push( key + '=' + list[i].replace('*', ',') );
			}
		}
		var facets = facetParams.join("&");
		var query = [];
		if(querystring) query.push(querystring);
		if(stateParams) query.push(stateParams);
		if(facets) query.push(facets);
		
		var extraParams = _self.getExtraParams();
		if(extraParams) query.push(extraParams);
		
		return query.join("&");
	};

}

// Add commas to integer i (which is actually a string).
// Taken from stackoverflow.
function commaDelimit(i) {
    if ((i == null) || (i == undefined) || (i.length == 0)) { return; }
    var parts = i.toString().split(".");
    if (parts[0].indexOf(',') < 0) {
    	parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    }
    return parts.join(".");
}

/*
 * This is intended to manage most of the behavior of having a multi-tabbed summary with queryform.
 * 	Examples are www.informatics.jax.org/gxd and www.informatics.jax.org/diseasePortal
 * 
 * 	Mandatory config parameters:
 * 		-tabViewId
 * 		-tabs
 * 	Optional config parameters:
 * 		-pageSizes (default page sizes defined in same order as "tabIds")
 * 		-historyId (history manager namespace, e.g. "gxd" or "hdp")
 * 	Optional parameters:
 * 		- isDisabled : returns true if tabs should be disabled, false
 * 			if not
 *  // assumes existence of the following methods:
 *  	-handleNavigation
 */
var MGITabSummary = function(config, isDisabled)
{
	// init parameters
	this.tabViewId = config["tabViewId"];
	this.tabIds = config["tabIds"];
	this.defaultTab = "defaultTab" in config ? config["defaultTab"] : this.tabIds[0];
	if("pageSizes" in config) this.pageSizes = config["pageSizes"];
	
	this.isDisabled = null;
	if (isDisabled != null) { this.isDisabled = isDisabled; }
	
	this.doHistory = "historyId" in config;
	this.historyMgrId = config["historyId"];
	
	// define the closure for event handling
	var _self=this;
	
	//return the current tab from the history manager
	this.getCurrentTab = function() 
	{
		if ((this.isDisabled != null) && (this.isDisabled())) {
			// if controls are disabled, bail out
			return;
		}

		var activeIndex = _self.summaryTabs.get("activeIndex");
		return _self.tabs[activeIndex];
	};
	
	// return default page size of current page
	this.getCurrentDefaultPageSize = function()
	{
		if(_self.defaultPageSizes) return _self.defaultPageSizes[_self.getCurrentTab()]
		return 100; // default value if none set.
	}
	
	this.init = function()
	{
		// MGI table tab control
		YAHOO.widget.Tab.prototype.ACTIVE_TITLE = '';
		this.summaryTabs = new YAHOO.widget.TabView(this.tabViewId);
	
		//init lookup of summary tab mappings of index and history manager parameter
		this.tabs = {};
		for(var i=0; i<this.tabIds.length; i++)
		{
			var tabId = this.tabIds[i];
			this.tabs[tabId] = i;
			this.tabs[i] = tabId;
		}
		
		// init lookup of default page sizes
		this.defaultPageSizes = {};
		for(var i=0; i<this.pageSizes.length; i++)
		{
			var pageSize = this.pageSizes[i];
			this.defaultPageSizes[i] = pageSize;
			// also add the lookup by tab id
			this.defaultPageSizes[this.tabs[i]] = pageSize;
		}
		
		// Add tab params to the generated requests
		this._rp = new MGIRequestParser();
		this._rp.getExtraParams = function()
		{
			var currentTab = _self.getCurrentTab();
			if(currentTab) var tabParams = "tab="+currentTab;
			
			return tabParams;
		}
	}
	this.init();
	
	if(this.doHistory)
	{
		// a class variable to help the tab change handler know when to fire off a new query
		this.newQueryState = false;
	
		//tab change handler
		//$("#debug").html("0");
		this.handleTabViewActiveTabChange = function(e)
		{
			if ((this.isDisabled != null) && (this.isDisabled())) {
				// if controls are disabled, bail out
				return;
			}

			//var cnt = parseInt($("#debug").html());
			//cnt = cnt+1;
			//$("#debug").html(cnt);
			var currentTabState = _self.getCurrentTab();
	
			var pageSize = _self.getCurrentDefaultPageSize();
			
			var currentRequest = History.getCurrentState(_self.historyMgrId);
			
			// resolve the tab out of request
			var currentValues = _self._rp.parseRequest(currentRequest);
			var requestedTab = currentValues["tab"];
			if($.isArray(requestedTab)) requestedTab = requestedTab[0];
			
			if(requestedTab && $.inArray(requestedTab,_self.tabIds)>=0)
			{	
				if(requestedTab == currentTabState)
				{
					// no tab change is happening
					// do we have a new query?
					if(_self.newQueryState)
					{
						// _self is the only time we don't want to remember the pagination.
						newRequest = _self._rp.generateRequest(null, 0, pageSize);
					}
					else
					{
						// no new query, save the pagination.
						var sortBy = {key:currentValues["sort"] || defaultSort,dir:currentValues["dir"] || "asc"};
						newRequest = _self._rp.generateRequest(sortBy,currentValues["startIndex"],currentValues["results"]);
					}
				}
				else
				{
					// tab change
					newRequest = _self._rp.generateRequest(null, 0, pageSize);
				}
			}
			else
			{
				// no previous tabs
				newRequest = _self._rp.generateRequest(null, 0, pageSize);
			}
			_self.newQueryState = false;
			if(newRequest == currentRequest) {
				handleNavigation(newRequest, true);
			} else {
				History.navigate(_self.historyMgrId, newRequest);
			}
		}
		this.summaryTabs.addListener("activeTabChange", this.handleTabViewActiveTabChange);
	}
	
	
	// to enable tab to be specified in querystring, we need  a way to parse it out
	this.resolveTabParam=function()
	{
		var reply = {};
		var kvPairs = querystring.split('&');
		var newKVs = [];
		var tabParam;
		for (pair in kvPairs) {
			arg = kvPairs[pair];
			if(arg && arg.indexOf("tab=")==0 )
			{
				if(arg.length>4) tabParam = arg.substr(4);
			}
			else
			{
				newKVs.push(arg);
			}
		}
		querystring = newKVs.join("&");
		return tabParam;
	}
	
	/*
	 * A convenience wrapper for creating the datatable paginator object
	 * 	since this will almost always be identical between datatables
	 * 		OPTIONAL:
	 * 			totalRecords,
	 * 			recordOffset
	 */
	this.createPaginator = function(rowsPerPageOptions, rowsPerPage, totalRecords, recordOffset)
	{
		// Create the Paginator
		var paginator = new YAHOO.widget.Paginator({
		   template: "{FirstPageLink} {PreviousPageLink}<strong>{PageLinks}</strong> {NextPageLink} {LastPageLink} <span style=align:right;>{RowsPerPageDropdown}</span><br/>{CurrentPageReport}",
		   pageReportTemplate: "Showing result(s) {startRecord} - {endRecord} of {totalRecords}",
		   rowsPerPageOptions: rowsPerPageOptions,
		   containers: ["paginationTop", "paginationBottom"],
		   rowsPerPage: rowsPerPage,
		   pageLinks: 3,
		   recordOffset: recordOffset || 1,
		   totalRecords: totalRecords,
		});
		
		return paginator
	}
	this.initPaginator = function(dataTable,paginator)
	{

		// Define a custom function to route pagination through the Browser History Manager
		var handlePagination = function(state) 
		{
		   // The next state will reflect the new pagination values
		   // while preserving existing sort values
		   var newState = _self._rp.generateRequest(this.get("sortedBy"), state.recordOffset, state.rowsPerPage);
		   //myPaginator.setState(newState);
		   // Pass the state along to the Browser History Manager
		   History.navigate(_self.historyMgrId, newState);
		};
		// First we must unhook the built-in mechanism...
		paginator.unsubscribe("changeRequest", dataTable.onPaginatorChangeRequest);
		// ...then we hook up our custom function
		paginator.subscribe("changeRequest", handlePagination, dataTable, true);
	}
}


/*
 * This class manages animating the queryform
 * 	Requires:
 * 		-formId (id of the query form)
 * 	Optional:
 * 		-qwrapId (the ID of the div wrapping what you want to animate
 * 		-toggleId (the ID of the toggle button)
 */
var MGIQFAnimator = function(formId,qwrapId,toggleId)
{
	this.formId=formId;
	if(qwrapId==undefined) qwrapId="qwrap";
	this.qwrapId=qwrapId;
	if(toggleId==undefined) toggleId="toggleQF";
	this.toggleId=toggleId;
	
	// define the closure
	var _self = this;
	
	this.toggleQF = function(oCallback,noAnimate)
	{
		if(noAnimate==undefined) noAnimate=false;
		
		 var outer = YAHOO.util.Dom.get('outer');
			YAHOO.util.Dom.setStyle(outer, 'overflow', 'hidden');
		 var qf = YAHOO.util.Dom.get(_self.qwrapId);
		 var toggleLink = YAHOO.util.Dom.get('toggleLink');
		 var toggleImg = YAHOO.util.Dom.get('toggleImg');
		 attributes =  { height: { to: 0 }};
		 	
		 if (!YAHOO.lang.isNull(toggleLink) && !YAHOO.lang.isNull(toggleImg)
		 		) {
			    attributes = { height: { to: QFHeight }};
				if (!qDisplay){
					attributes = { height: { to: 0  }};
					setText(toggleLink, "Click to modify search");
			    	YAHOO.util.Dom.removeClass(toggleImg, 'qfCollapse');
			    	YAHOO.util.Dom.addClass(toggleImg, 'qfExpand');    	
			    	qDisplay = true;
				} else {            
			    	YAHOO.util.Dom.setStyle(qf, 'height', '0px');
			    	YAHOO.util.Dom.setStyle(qf, 'display', 'none');
			    	YAHOO.util.Dom.removeClass(toggleImg, 'qfExpand');
			    	YAHOO.util.Dom.addClass(toggleImg, 'qfCollapse');
			    	setText(toggleLink, "Click to hide search");
			    	qDisplay = false;
			    	changeVisibility(_self.qwrapId);
				}
			}
		 var animComplete;
		 // define what to do after the animation finishes
		 if(qDisplay)
		 {
		 	animComplete = function(){
		 		changeVisibility(_self.qwrapId);
		 		$("#"+_self.qwrapId).css("height","auto");
		 	};
		 }
		 else
		 {
		 	animComplete = function(){
		 		YAHOO.util.Dom.setStyle(outer, 'overflow', 'visible');
					
					if (/MSIE (\d+.\d+);/.test(navigator.userAgent)) {
						// Reduce the ie/yui quirky behavior
				    	YAHOO.util.Dom.setStyle(qf, 'display', 'none');
				    	YAHOO.util.Dom.setStyle(qf, 'display', 'block');
				    	YAHOO.util.Dom.setStyle(YAHOO.util.Dom.get(_self.formId), 'display', 'none');
				    	YAHOO.util.Dom.setStyle(YAHOO.util.Dom.get(_self.formId), 'display', 'block');
				    	YAHOO.util.Dom.setStyle(YAHOO.util.Dom.get(_self.toggleId), 'display', 'none');
				    	YAHOO.util.Dom.setStyle(YAHOO.util.Dom.get(_self.toggleId), 'display', 'block');				
					}
		 		$("#"+_self.qwrapId).css("height","auto");
		 	};
		 }
		 if(!noAnimate)
		 {
				var anim = new YAHOO.util.Anim(_self.qwrapId, attributes);
				
				anim.onComplete.subscribe(animComplete);
			
				if (!YAHOO.lang.isNull(oCallback)){	
					anim.onComplete.subscribe(oCallback);
				}
				
				anim.duration = 0.50;
				anim.animate();
		 }
		 else
		 {
		 	YAHOO.util.Dom.setStyle(_self.qwrapId, 'height', attributes["height"]["to"]+'px');
		 	animComplete();
		 }
		 if(typeof repositionUploadWidgets == 'function') repositionUploadWidgets();
	}

	//Attache the animation handler to the toggleQF div
	this.toggleLink = YAHOO.util.Dom.get(this.toggleId);
	if (!YAHOO.lang.isUndefined(this.toggleLink)){
		YAHOO.util.Event.addListener(this.toggleId, "click", this.toggleQF);
	}
};

window.FewiUtil = new function()
{
	this.SortSmartAlpha = function(a,b)
	{
		a = a.toLowerCase();
		b = b.toLowerCase();
		function chunkify(t) {
		    var tz = [], x = 0, y = -1, n = 0, i, j;

		    while (i = (j = t.charAt(x++)).charCodeAt(0)) {
		      var m = (i == 46 || (i >=48 && i <= 57));
		      if (m !== n) {
		        tz[++y] = "";
		        n = m;
		      }
		      tz[y] += j;
		    }
		    return tz;
		  }

		  var aa = chunkify(a);
		  var bb = chunkify(b);

		  for (x = 0; aa[x] && bb[x]; x++) {
		    if (aa[x] !== bb[x]) {
		      var c = Number(aa[x]), d = Number(bb[x]);
		      if (c == aa[x] && d == bb[x]) {
		        return c - d;
		      } else return (aa[x] > bb[x]) ? 1 : -1;
		    }
		  }
		  return aa.length - bb.length;
	};
	
	/*
	 * get unique values from array
	 */
	this.uniqueValues = function(arr)
	{
	   var u = {}, a = [];
	   for(var i = 0, l = arr.length; i < l; ++i){
	      if(u.hasOwnProperty(arr[i])) {
	         continue;
	      }
	      a.push(arr[i]);
	      u[arr[i]] = 1;
	   }
	   return a;
	};
}

// pulled from stackoverflow -- add commas to an integer
function numberWithCommas(x) {
    return x.toString().replace(/\B(?=(?:\d{3})+(?!\d))/g, ",");
}

// return a string of pseudo-random letters and numbers that is 'size' characters long
function mgiRandomString(size) {
	var s = '';
	while (s.length < size) {
		s = s + Math.random().toString(36).substring(2);
	}
	return s.substring(0,size);
}

// remove any <script> or </script> tags from string 's'
function noScript(s) {
	if (s == null) { return s; }
	return s.replace(/<script>/g, '').replace(/<\/script>/g, '');
}

// remove any alert(...) commands from string 's'
function noAlert(s) {
	if (s == null) { return s; }
	return s.replace(/alert[(][^)]*[)]/g, '');
}

// return a progress meter as two nested DIVs, including a rollover tip for actual numbers
function progressMeter(numberDone, totalNumber) {
	if (totalNumber == 0) return "-";
	
	var pct = Math.round((100.0 * numberDone) / totalNumber);
	var sb = "";
	sb = sb + "<div id='progressMeter' style='border: 1px solid black; width:200px; height:20px; margin-top: 5px;' title='" + Number(numberDone).toLocaleString() + " of " + Number(totalNumber).toLocaleString() + "'>";
	sb = sb + "<div id='shadedPart' style='height: 100%; background-color: darkblue; float:left; color: white; width: " + pct + "%'>";
	if (pct > 15) {
		sb = sb + pct + "%";
		sb = sb + "</div><!-- shadedPart -->";
	} else {
		sb = sb + "</div><!-- shadedPart -->";
		sb = sb + pct + "%";
	}
	sb = sb + "</div><!-- progressMeter -->";
	return sb;
}
