	var radius = 5;
	var maxlinks = 500;
	var enforcemaxlinks = true;
	var jsonlimit = 20000;
	var width = 600;
    var height = 550;
	//var width = 2500;
	//var height = 1400;
    var scaleFactor = 2;
    var initialScale = 0.8;
    var scaleMax = 16;
    var scaleMin = 0.25;

	var links = [];
	var nodes = [];
	
	var nodeindex = {};
	var linkindex = {};
	var linkcounts = {};
	var clickednode;
	var currentTranslate = [0, 0];
	var currentScale = 1;
	var draging = false;

	var force = d3.layout.force()
		.linkDistance(60)
		.charge(-300)
		//.gravity(.05)
		.size([width, height])
		.on("tick", tick);
	
	var drag = d3.behavior.drag()
		.origin(function(d) { return d; })
		.on("dragstart", dragstarted)
		.on("drag", dragged)
		.on("dragend", dragended);
	
	var svg = d3.select("#graph").append("svg")
		.attr("width", width)
		.attr("height", height);

	var firstG = svg.append("g");
	
	firstG.append("rect")
		.attr("class", "overlay")
		.attr("width", width)
		.attr("height", height);
	
	//var container = firstG.call(d3.behavior.zoom().scaleExtent([0.35, 15]).on("zoom", zoom)).on("dblclick.zoom", null).append("g");
	var zoomListener = d3.behavior.zoom().scaleExtent([scaleMin, scaleMax]).scale(initialScale).on("zoom", zoom);
	
	var container = firstG.call(zoomListener).on("dblclick.zoom", null).append("g").attr("transform", "scale(" + initialScale +")");

	firstG.append("image")
		.attr('x',10)
		.attr('y',30)
		.attr('width', 18)
		.attr('height', 18)
		.attr("xlink:href", plusimage)
		.on("click", zoomin);
	
	firstG.append("image")
		.attr('x',10)
		.attr('y',50)
		.attr('width', 18)
		.attr('height', 18)
		.attr("xlink:href", minusimage)
		.on("click", zoomout);
	
	var text = firstG.append("text")
		.attr("x", 10)
		.attr("y", 20)
		.attr("class", "warningclass")
		.style("display", "block")
		.on("click", function(d) {
			if(d3.event.altKey) {
				enforcemaxlinks = !enforcemaxlinks;
				markerIDs.forEach(function(id) {
					expandNodeByName(id);
				});
				update();
			}
		});
	
	var loading = firstG.append("text")
		.attr("x", 10)
		.attr("y", height - 10)
		.attr("class", "loadingclass")
		.text("Loading...")
		.style("display", "block");
	
	container.append("defs")
		.append("marker")
		.attr("id", "arrow")
		.attr("viewBox", "0 -5 10 10")
		.attr("refX", 17)
		.attr("refY", -0.5)
		.attr("markerWidth", 10)
		.attr("markerHeight", 10)
		.attr("orient", "auto")
		.append("path")
		.attr("d", "M0,-4L10,0L0,4");
	
	container.selectAll("defs")
		.append("marker")
		.attr("id", "selflink")
		.attr("viewBox", "0 -5 10 10")
		.attr("refX", -11)
		.attr("refY", -3)
		.attr("markerWidth", 10)
		.attr("markerHeight", 10)
		.attr("orient", "0")
		.append("path")
		.attr("d", "M0,-4L10,0L0,4");

	var path = container.append("g").selectAll("path")
		.data(force.links());
		
	var svgnodes = container.append("g")
		.selectAll("g").data(force.nodes());

//	var circle = svgnodes.append("circle")
//		.style("fill", nodecolor)
//		//.call(force.drag);
//		.call(drag);
//	
	
	loading = loading.style("display", "block");
	markerIDs.forEach(function(id) {
		expandNodeByName(id);
	});
	
	filters.registerCallback("GraphCallBack", updateGraphBasedOnSorting);
	
function dragstarted(d) {
	d3.event.sourceEvent.stopPropagation();
	d3.select(this).classed("dragging", true);
}

function dragged(d) {
	d3.select(this).attr("cx", d.x = d3.event.x).attr("cy", d.y = d3.event.y);
}

function dragended(d) {
	d3.select(this).classed("dragging", false);
}

function tick() {
	path.attr("d", linkArc);
	svgnodes.attr("transform", transform);
}

function transform(d) {
	return "translate(" + d.x + "," + d.y + ")";
}

function linkArc(d) {
	var sx = d.source.x;
	var sy = d.source.y;
	var tx = d.target.x;
	var ty = d.target.y;
	
	var dx = tx - sx, dy = ty - sy;
	var dry = 0;
	var drx = 0;
	var ang = 0;
	var arc = 0;

	if(dx == 0 && dy == 0) {
		drx = 10 + d.linknum;
		dry = 20 + d.linknum;
		ang = 90;
		arc = 1;
		sx = (sx - dry);
		sy = (sy - 3);
		tx = (tx - (dry + .01));
		ty = (ty - 3);
	} else {
		dry = drx = Math.sqrt(dx * dx + dy * dy);
		ang = -45;
		arc = 0;
		
		var key = makeKey(d);

		if(linkcounts[key] > 1) {
			drx = drx/(1 + (1/linkcounts[key]) * (d.linknum - 1));
			dry = drx;
		}
	}
	
	return "M" + sx + "," + sy + "A" + drx + "," + dry + "," + ang + "," + arc + ",1," + tx + "," + ty;
}

function zoom() {
	currentTranslate = d3.event.translate;
	currentScale = d3.event.scale;
	container.attr("transform", "translate(" + d3.event.translate + ")scale(" + d3.event.scale + ")");
}

function zoomin() {
	
	var newScale = currentScale * scaleFactor;
	if(newScale > scaleMax) {
		newScale = scaleMax;
	}
	var newx = ((width / 2) * (1 - newScale));
	var newy = ((height / 2) * (1 - newScale));
	
	var diffx = currentTranslate[0] - ((width / 2) * (1 - currentScale));
	var diffy = currentTranslate[1] - ((height / 2) * (1 - currentScale));
	diffx = diffx * (newScale / currentScale);
	diffy = diffy * (newScale / currentScale);

	zoomListener.scale(newScale).translate([newx + diffx, newy + diffy]);
	zoomListener.event(svg.transition().duration(500));

}

function zoomout() {
	
	var newScale = currentScale / scaleFactor;
	if(newScale < scaleMin) {
		newScale = scaleMin;
	}
	var newx = ((width / 2) * (1 - newScale));
	var newy = ((height / 2) * (1 - newScale));

	var diffx = currentTranslate[0] - ((width / 2) * (1 - currentScale));
	var diffy = currentTranslate[1] - ((height / 2) * (1 - currentScale));
	diffx = diffx * (newScale / currentScale);
	diffy = diffy * (newScale / currentScale);
	
	zoomListener.scale(newScale).translate([newx + diffx, newy + diffy]);
	zoomListener.event(svg.transition().duration(500));

}

function update() {

	force.nodes(nodes).links(links).start();
	
	// Update the links
	path = path.data(force.links(), function(d) {
		return d.miKey;
	});

	// Exit any old links.
	path.exit().remove();

	// Enter any new links.
	path.enter().append("path", ".link")
		.attr("class", function(d) { return "link " + d.validation; })
		.attr("marker-end", function(d) {
			if(d.selflink) {
				return "url(#selflink)";
			} else {
				return "url(#arrow)";
			}
		});
	
	// Update the nodes
	svgnodes = svgnodes.data(force.nodes(), function(d) { return d.id; }); //.enter().append("g");

	// Exit any old nodes.
	// Doesn't work
	svgnodes.exit().remove(); 

	var nodec = svgnodes.enter().append("g");//.enter().append("circle");
	
	nodec.append("circle")
		//.attr("cx", function(d) { return d.x; })
		//.attr("cy", function(d) { return d.y; })
		.attr("r", function(d) { return radius; })
		.on("click", click)
		.style("fill", nodecolor)
		.call(force.drag);

	nodec.append("text")
		.attr("x", radius * 2)
		.attr("y", ".31em")
		.attr("class", "nodestyle")
		.text(function(d) {
			return d.name;
		});
	
	if(Object.keys(linkindex).length >= maxlinks && enforcemaxlinks == true) {
		text = text.style("display", "block").text("Graph limited to (" + maxlinks + ") interactions");
	} else if(Object.keys(linkindex).length >= maxlinks && enforcemaxlinks == false) {
		text = text.style("display", "block").text("Showing (" + links.length + ") interactions");
	} else {
		text = text.style("display", "none");
	}
	loading = loading.style("display", "none");
}

function nodecolor(d) {
	if(d.expanded == true) { return "#ffcccc"; }
	else { return "#cccccc"; }
}

function turncoloron(d) {
	return "#ffcccc";
}

function turncoloroff(d) {
	return "#cccccc";
}

function click(d) {
	if (d3.event.defaultPrevented) return;

	// This functionality is not officially implemented.
	// Use at your own risk.
	
	if(d3.event.altKey || !enforcemaxlinks) {
		if(d.expanded == true) {
			if(expandedcount() > 1) {
				d3.select(this).style("fill", turncoloroff);
				contractNodeByName(d.id);
				removeMarker(d.id);
			}
		} else {
			if(Object.keys(linkindex).length < maxlinks || !enforcemaxlinks) {
				d3.select(this).style("fill", turncoloron);
				addMarker(d.id);
				expandNodeByName(d.id);
			}
		}
	}
	// Expand All
	if(d3.event.altKey && !enforcemaxlinks) {
		nodes.forEach(function(node) {
			if(!node.expanded) {
				addMarker(node.id);
				//node.style("fill", turncoloron);
				expandNodeByName(node.id);
				node.expanded = true;
			}
		});
	}
	
}

function expandedcount() {
	var ret = 0;
	nodes.forEach(function(node) {
		if(node.expanded == true) {
			ret += 1;
		}
	});
	return ret;
}

function contractNodeByName(nodeName) {
	//var url = fewiurl + "interaction/json?markerIDs=" + nodeName + "&pageSize=" + jsonlimit;
	//clickednode = nodeName;
	//d3.json(url, ContractURL);
	
	var retlnks = [];
	var retlnkidx = {};
	var retnodes = [];
	var retnodeidx = {};
	links.forEach(function(link) {
		var keepLink = true;
		
		if(link.source.id == nodeName || link.target.id == nodeName) {
			keepLink = false;
		}

		if(link.source.id == nodeName) {
			if(link.target.expanded == true) {
				keepLink = true;
			}
		} 
		if(link.target.id == nodeName) {
			if(link.source.expanded == true) {
				keepLink = true;
			}
		}
		
		if(keepLink) {
			retlnks.push(link);
			retlnkidx[link.miKey] = retlnks[retlnks.length - 1];

			if(!retnodeidx[link.source.id]) {
				retnodes.push(nodeindex[link.source.id]);
				retnodeidx[link.source.id] = retnodes[retnodes.length - 1];
			}
			if(!retnodeidx[link.target.id]) {
				retnodes.push(nodeindex[link.target.id]);
				retnodeidx[link.target.id] = retnodes[retnodes.length - 1];
			}
		}

	});

	links = retlnks;
	linkindex = retlnkidx;
	nodes = retnodes;
	nodeindex = retnodeidx;
	if(nodeindex[nodeName]) {
		nodeindex[nodeName].expanded = false;
	}
	update();
}

function expandNodeByName(nodeName) {
	var url = fewiurl + "interaction/json?markerIDs=" + nodeName + filters.getUrlFragment() + "&pageSize=" + getPageSize() + "&sort=" + is_getSort() + "&dir=" + is_getSortDir();
	clickednode = nodeName;
	loading = loading.style("display", "block");
	d3.json(url, ExpandURL);
}

function updateGraphBasedOnSorting() {
	var url = fewiurl + "interaction/json?" + getQuerystring() + "&pageSize=" + getPageSize() + "&sort=" + is_getSort() + "&dir=" + is_getSortDir();
	d3.json(url, ContractToSet);
}

function getPageSize() {
	if(enforcemaxlinks) {
		return maxlinks;
	} else {
		return 20000;
	}
}

function ContractToSet(e, json) {
	var retlnks = [];
	var retlnkidx = {};
	var retnodes = [];
	var retnodeidx = {};

	json.summaryRows.forEach(function(link) {
		
		if(Object.keys(retlnkidx).length < maxlinks || enforcemaxlinks == false) {
			
			if(typeof nodeindex[link.organizerID] == 'undefined') {
				if(typeof retnodeidx[link.organizerID] == 'undefined') {
					retnodes.push({name: link.organizerSymbol, id: link.organizerID, expanded: false});
					retnodeidx[link.organizerID] = retnodes[retnodes.length - 1];
				}
			} else {
				if(typeof retnodeidx[link.organizerID] == 'undefined') {
					retnodes.push(nodeindex[link.organizerID]);
					retnodeidx[link.organizerID] = retnodes[retnodes.length - 1];
				}
			}
			link.source = retnodeidx[link.organizerID];
			
			if(typeof nodeindex[link.participantID] == 'undefined') {
				if(typeof retnodeidx[link.participantID] == 'undefined') {
					retnodes.push({name: link.participantSymbol, id: link.participantID, expanded: false});
					retnodeidx[link.participantID] = retnodes[retnodes.length - 1];
				}
			} else {
				if(typeof retnodeidx[link.participantID] == 'undefined') {
					retnodes.push(nodeindex[link.participantID]);
					retnodeidx[link.participantID] = retnodes[retnodes.length - 1];
				}
			}
			link.target = retnodeidx[link.participantID];
			
			if(typeof linkindex[link.miKey] == 'undefined') {
				if(!link.validation) {
					link.validation = "inferred";
				}

				retlnks.push({source: link.source, target: link.target, linknum: 1, miKey: link.miKey, validation: link.validation, selflink: (link.target.id == link.source.id)});

				retlnkidx[link.miKey] = retlnks[retlnks.length - 1];
//				var key = makeKey(link);
//				
//				if(linkcounts[key]) {
//					linkcounts[key] = linkcounts[key] + 1;
//					linkindex[link.miKey].linknum = linkcounts[key];
//				} else {
//					linkcounts[key] = 1;
//					linkindex[link.miKey].linknum = 1;
//				}
			} else {
				retlnks.push(linkindex[link.miKey]);
				retlnkidx[link.miKey] = retlnks[retlnks.length - 1];
			}
		}
		
	});
	
	links = retlnks;
	linkindex = retlnkidx;
	nodes = retnodes;
	nodeindex = retnodeidx;
	
	update();
}

function ExpandURL(e, json) {

	var linksadded = 0;
	
	json.summaryRows.forEach(function(link) {

		if(Object.keys(linkindex).length < maxlinks || enforcemaxlinks == false) {
		
			if(typeof nodeindex[link.organizerID] == 'undefined') {
				nodes.push({name: link.organizerSymbol, id: link.organizerID, expanded: false});
				nodeindex[link.organizerID] = nodes[nodes.length - 1];
			}
			link.source = nodeindex[link.organizerID];
			
			if(typeof nodeindex[link.participantID] == 'undefined') {
				nodes.push({name: link.participantSymbol, id: link.participantID, expanded: false});
				nodeindex[link.participantID] = nodes[nodes.length - 1];
			}
			link.target = nodeindex[link.participantID];
			
			if(nodeindex[link.organizerID].id == clickednode) {
				nodeindex[link.organizerID].expanded = true;
			}
			if(nodeindex[link.participantID].id == clickednode) {
				nodeindex[link.participantID].expanded = true;
			}
			
			if(typeof linkindex[link.miKey] == 'undefined') {
				if(!link.validation) {
					link.validation = "inferred";
				}

				links.push({source: link.source, target: link.target, linknum: 1, miKey: link.miKey, validation: link.validation, selflink: (link.target.id == link.source.id)});

				linksadded = linksadded + 1;
				linkindex[link.miKey] = links[links.length - 1];
				var key = makeKey(link);
				
				if(linkcounts[key]) {
					linkcounts[key] = linkcounts[key] + 1;
					linkindex[link.miKey].linknum = linkcounts[key];
				} else {
					linkcounts[key] = 1;
					linkindex[link.miKey].linknum = 1;
				}
			}
		}
	});
	
	if(linksadded > 0) {
		if(nodeindex[clickednode]) {
			nodeindex[clickednode].expanded = true;
		}
		update();
	}
}

function addMarker(nodeName) {
	//markerIDs.push(nodeName);
	// remove nodeName from markerIDs list
}

function removeMarker(nodeName) {
	// add nodeName to markerIDs list if doesn't already exist
}

function makeKey(link) {
	return link.source.id + "-" + link.target.id;
}
