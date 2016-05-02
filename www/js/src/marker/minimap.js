(function(){
/*
 * Creates the minimap image on marker detail page
 *
 */
var Minimap = function() {
		
	/*
	 * Requires id to be the id of a canvas element
	 * 	in which to draw the minimap on
	 * 
	 *  data is list of minimap markers as
	 *   {
	 *     anchorSymbol,
	 *     cmOffset,
	 *     maxCmOffset,
	 *     primary
	 *   }
	 *   
	 *   NOTE: maxCmOffset is only required on 1st element
	 *   
	 *   OPTIONAL configuration
	 *   
	 *   rotate90: rotate the image 90 degree, from vertical to horizontal
	 *   
	 */
	this.draw = function(config) {
		var id = config.id;
		var data = config.data;
		
		if (!data || data.length == 0) {
			console.log("no minimap to draw. data list is empty");
			return;
		}
		
		this.rotate90 = config.rotate90 || false;
		
		var maxCmOffset = data[0].maxCmOffset;
		var canvas = document.getElementById(id);
		
		this._init();
		
		this._calcMiniMapDimensions(data, maxCmOffset);
		this._assignCells(data);
		this._drawImg(canvas, data, maxCmOffset);
	}
	
	
	/* 
	 * helper functions 
	 */
	
	/*
	 * Initialize several calculation values
	 */
	this._init = function() {
		
		// holder for marker symbol placements
		this.cells = []
		// width
		this.maxX = 130;
		// height
		this.maxY = null;
		// pixel to cm ratio
		this.scale = 2
		// x offset
		this.startX = 35;
		// y offset
		this.startY = 15;
		// calculated later
		this.stopY = null;
		// diameter of centromere
		this.centroDiam = 8;
		// width of chromosome bar
		this.chrWidth = 2;
		// x offset where ruler is placed
		this.ruleX = 10;
		// offset from chromosome where symbols begin
		this.textX = 20;
		// space at bottom of image
		this.bottomGap = 10;
		// start for big hash marks on ruler
		this.hashStart = this.startX - this.ruleX + 5;
		// big hash mark end
		this.hashStop = this.startX - this.ruleX - 5;
		// start for small hash marks on ruler
		this.smallStart = this.startX - this.ruleX + 2;
		// small hash mark end
		this.smallStop = this.startX - this.ruleX - 2;
		// width of font
		this.fontX = 6;
		// height of font
		this.fontY = 12;
		// space between characters on y-axis
		this.tcellgap = 1;
		// height of a text cell / marker symbol
		this.tcellheight = this.fontY + 2*this.tcellgap;
		// length of cells
		this.numCells = null;
		// height of chromosome
		this.chrheight = null;
		// where ruler stops
		this.stoprule = null;
	}
	
	this._calcMiniMapDimensions = function(data, maxCmOffset) {
		var maxSymbolLen = 0;
		for (var i=0; i<data.length; i++) {
			
			var marker = data[i];
			maxSymbolLen = Math.max(maxSymbolLen, marker.anchorSymbol.length);
		}
		
		maxX = this.startX + this.textX + (this.fontX * maxSymbolLen) + 2
		
		this.chrheight = Math.round(maxCmOffset * this.scale);
		
		this.maxY = this.startY + this.chrheight;
		
		this.stopY = this.maxY;
		
		var rem = this.maxY % this.tcellheight;
		if (rem > 0){
			var diff = this.tcellheight - rem;
			this.maxY = Math.round(this.maxY + diff);
		}
			
		this.stoprule = maxCmOffset;
		rem = this.stoprule % 10;
		if (rem > 0){
			var diff = 10.0 - rem;
			this.stoprule = Math.round(this.stoprule + diff);
		}
		
		this.stoprule = this.stoprule * this.scale + this.startY;
		
		if (this.stoprule > this.maxY) {
			this.maxY = Math.round(this.maxY + diff);
		}
		
		this.numCells = this.chrheight / this.tcellheight;
		
		var markerCount = data.length;
		if (this.numCells < markerCount) {
			this.numCells = markerCount
			this.maxY = this.maxY + this.tcellheight;
		}
		
		this.maxY = this.maxY + this.bottomGap;
		
		for (var i=0; i<this.numCells; i++) {
			this.cells.push(null);
		}
	}
	
	this._assignCells = function(data) {
		var maxCells = this.cells.length;
		
		for (var i=0; i<data.length; i++) {
			
			var marker = data[i];
			var offPix = marker.cmOffset * this.scale;
			
			var idx = Math.floor(offPix/(this.fontY + 2));
			
			idx = Math.min(idx, maxCells);
			
			if (this.cells[idx] == null) {
				this.cells[idx] = marker;
			}
			else {
				if (this.cells[idx].cmOffset < marker.cmOffset) {
					var j = idx;
					while (j < this.cells.length 
							&& this.cells[j] 
							&& this.cells[j].cmOffset < marker.cmOffset) {
						j += 1
					}
					this.cells.splice(j, 0, marker)
				}
				else {
					var j = idx;
					while (j >=0 
							&& this.cells[j]
							&& this.cells[j].cmOffset > marker.cmOffset) {
						j -= 1
					}
					this.cells.splice(j, 0, marker)
				}
			}
		}
		
		while (this.cells.length > maxCells) {
			var startlen = this.cells.length;
			
			if (this.cells[startlen - 1] == null) {
				this.cells.splice(startlen-1, 1);
			}
			
			if (this.cells[0] == null) {
				this.cells.splice(0,1);
			}
			
			if (startlen == this.cells.length) {
				break;
			}
			
		}
		
		if (this.cells.length < maxCells) {
			this.cells.push(null);
		}
		
		if (this.cells.length > maxCells) {
			var j = this.cells.length - 1;
			
			while(j > 0 && this.cells.length > maxCells) {
				if (this.cells[j] == null) {
					this.cells.splice(j, 1);
				}
				j -= 1;
			}
		}
		
		
		for (var i=0; i < this.cells.length; i++) {
			marker = this.cells[i];
			if (marker != null) {
				marker.cellIdx = i;
			}
		}
	}
	
	this._drawImg = function(canvas, data, maxCmOffset) {
		
		
		// set canvas dimensions
		var ctx = canvas.getContext("2d");
		
		// set canvas size
		canvas.width = this.maxX;
		canvas.height = this.maxY + this.bottomGap;

		if (this.rotate90) {
			// rotate canvas from vertical to horizontal
			// if configured
			canvas.width = this.maxY + this.bottomGap;
			canvas.height = this.maxX;
			ctx.rotate(270 * Math.PI / 180);
			ctx.translate(-this.maxX,0);
		}
		
		// offset canvas to fix blurry line bug
		ctx.translate(0.5, 0.5);
		
		// draw canvas background color
		ctx.fillStyle = "#efefef";
		ctx.fillRect(0,0,this.maxX,this.maxY);
		// monospaced font for easier calculations
		ctx.font = "12px \"Lucida Console\", Monaco, monospace";
		
		// draw chromosome
		// saddlebrown
		var chrColor = "#8B4513"
		ctx.fillStyle = chrColor;
		ctx.strokeStyle = chrColor;
		ctx.beginPath();
		ctx.arc(this.startX + 0.5, this.startY, this.centroDiam / 2,
				0, 2*Math.PI);
		ctx.fill();
		
		ctx.fillRect(this.startX - (this.chrWidth / 2) + 0.5, this.startY,
				this.chrWidth, this.stopY - this.startY);
		
		
		// draw ruler
		ctx.beginPath();
		ctx.moveTo(this.startX - this.ruleX, this.startY);
		ctx.lineTo(this.startX - this.ruleX, this.stoprule);
		ctx.stroke();
		
		var length = Math.round((this.stoprule - this.startY) / this.scale)
		var halftexth = this.tcellheight / 2;
		for (var i=0; i <= length; i+= 5) {
			
			ctx.strokeStyle = chrColor;
			
			var rulelabel = i + "";
			var rulelabelWidth = this.fontX * (rulelabel.length + 2.5);
			
			var ypos = (i * this.scale) + this.startY;
			
			if (i % 10 == 0) {
				// draw hash
				ctx.beginPath();
				ctx.moveTo(this.hashStart, ypos);
				ctx.lineTo(this.hashStop, ypos);
				ctx.stroke();
				
				// draw num
				// darkgreen
				ctx.strokeStyle = "#006400";
				ctx.fillStyle = "#006400";
				ctx.fillText(rulelabel, 
						this.hashStart - rulelabelWidth, 
						(i * this.scale) + this.startY + (this.tcellheight / 4));
			}
			else if (i % 5 == 0) {
				// draw small hash
				ctx.beginPath();
				ctx.moveTo(this.smallStart, ypos);
				ctx.lineTo(this.smallStop, ypos);
				ctx.stroke();
			}
		} 
		
		// fix the offset
		ctx.translate(-0.5, -0.5);
		
		for (var i=0; i< data.length; i++) {
			var marker = data[i];
			
			var cmOffsetPixels = this.startY + marker.cmOffset * this.scale;
			var textOffsetPixels = this.startY + (marker.cellIdx) * this.tcellheight;
			var textStartPixels = this.startX + this.textX;
			
			// darkblue
			var color = "#00008B";
			if (marker.primary) {
				// red
				color = "#FF0000";
			}
			
			// draw text
			ctx.strokeStyle = color;
			ctx.fillStyle = color;
			ctx.fillText(marker.anchorSymbol, 
					textStartPixels, 
					textOffsetPixels + (this.tcellheight / 4));
			
			// draw line
			// black
			ctx.strokeStyle = "#000000";
			ctx.beginPath();
			ctx.moveTo(this.startX + this.chrWidth,cmOffsetPixels);
			ctx.lineTo(textStartPixels - 2, textOffsetPixels);
			ctx.stroke();
			
			// draw Chromosome hatch
			// orange
			ctx.strokeStyle = "#FFA500";
			ctx.beginPath();
			ctx.moveTo(this.startX - this.chrWidth - 1, cmOffsetPixels);
			ctx.lineTo(this.startX + this.chrWidth + 1, cmOffsetPixels);
			ctx.stroke();
			
		}
	}
};



// expose Minimap object as a singleton
window.Minimap = new Minimap();

})();