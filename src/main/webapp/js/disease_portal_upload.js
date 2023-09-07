/*
*   A widget for facilitating file uploads that are stored in a session on server.
*   For now, this is pretty specific to the human disease portal.
*
*   @author kstone
*/
function HDPFileUploadWidget(config)
{
    this.formId = config.formId;
    
    // CONSTANTS
    this.originalFileInputId = config.fileInputId || "locationsFileInput";
    this.originalFileNameInputId = config.fileNameInputId || "locationsFileName";
    this.originalFileNotifyId = config.fileNotifyId || "locationsFileNotify";
    this.iframeId = "hiddenfileform_if";
    this.iframeContentId = this.iframeId + "_content";
    this.hiddenFormId = config.hiddenFormId || "hiddenFileForm";
    this.hiddenFileInputId = "hiddenFile";
    this.hiddenFieldInputId = "hiddenField";
    this.hiddenTypeInputId = "hiddenType";
    this.alertBoxId = "formAlertBox";
    this.alertBoxContentId = this.alertBoxId+"_content";
    this.alertBoxCloseId = this.alertBoxId+"_close";
    this.uploadPollInterval = 200; //time to poll when upload has seen success/failure
    
    // define the closure for this object
    var _self = this;
    
    _self.init = function()
    {
        var qFormParent = $("#"+_self.formId).parent();
        // set up any hidden elements we need for form submitals
        var hiddenDiv = "<div class=\"hide\">";
            // add the iframe we will use for form submital
            hiddenDiv += "<iframe id=\""+_self.iframeId+"\" name=\""+_self.iframeId+"\" src=\""+fewiurl+"/fewi/blank.html\"></iframe>";
        hiddenDiv += "</div>";
        qFormParent.append(hiddenDiv);
        
        // also add the alertBox dialog
        var alertDiv = "<div class=\"hide\" id=\""+_self.alertBoxId+"\" style=\"position:relative; z-index: 1;\">";
        	alertDiv += "<div id=\"alertBoxPosition\" style=\"position:absolute;background-color: #ffeac3; border: 1px solid black; "+
        			"border-radius:4px; text-align: center;padding: 20px;max-height:400px;max-width:800px;overflow:auto;\">";
	            alertDiv += "<div id=\""+_self.alertBoxContentId+"\"></div>";
	            alertDiv += "<br><button id=\""+_self.alertBoxCloseId+"\" style=\"padding:4px;cursor:pointer;\">OK</button>";
            alertDiv += "</div>";
        alertDiv += "</div>";
        
        qFormParent.append(alertDiv);
        
        $("#"+_self.alertBoxCloseId).click(function(e){
        	$("#"+_self.alertBoxId).hide();
        	_self.enableForm();
        });
    };
    _self.init();
    
    _self.setLocationsFileSession = function(e)
    {
		$("#"+_self.originalFileNotifyId).hide();
    	var files = null;
    	if(this.files) files = this.files;

    	// reset the iframe content
    	$("#"+_self.iframeId).contents().find("html").html("");

    	var originalFileInputJq = $("#"+_self.originalFileInputId);
    	// get the filename
    	var filename = originalFileInputJq.val();
    	filename = _self.resolveFilename(filename);
    	// set file name in hidden input
    	$("#"+_self.originalFileNameInputId).val(filename);
    	
    	$("#"+_self.hiddenFormId).submit();
    	//console.log("files="+files+", filename="+filename);

    	if((!files || files.length<1) && !filename) return;
    	
    	_self.disableForm();
    	_self.popWaiting("Processing file and caching data matches. Please wait.");
    	$("#"+_self.iframeId).one("load",function(e){
    		var iframeJq = $(this);
    		var success = iframeJq.contents().find("#success").html();
    		var error = iframeJq.contents().find("#error").html();
    		//var iframe = document.getElementById('frameId');
    		//var html = iframe.contentDocument.getElementsByTagName('body')[0].innerHTML;

    		//console.log("success="+success+", error="+error);
    		if(success || error)
    		{
    			//_self.enableForm();
    			clearInterval(_self.interval_id);
    			if(success)
    			{
    				_self.popAlert(success);
    			}
    			else
    			{
    				// handle spefic errors from uploadFile service
    				_self.popAlert("ERROR:"+error);
    				// reset the locations file fields
    				_self.resetLocationsFields();
    			}
    		}
    		// handle more generic sever exceptions
    		var bodyText = iframeJq.contents().find("body").text();
    		if(bodyText.indexOf("Exception")>0)
    		{
    			clearInterval(_self.interval_id);
    			
    			if(bodyText.indexOf("MaxUploadSizeExceededException")>0)
    			{
    				_self.popAlert("ERROR: Max file upload limit of 25MB exceeded.");
    			}
    			else
    			{
    				_self.popAlert(iframeJq.contents().find("body").html());
    			}
    			// reset the locations file fields
				_self.resetLocationsFields();
    		}
    	});
    }
    
    _self.disableForm = function()
    {
    	$("#"+_self.formId+" input[type=submit]").prop("disabled",true);
    	$("#"+_self.formId+" input[type=reset]").prop("disabled",true);
    }
    _self.enableForm = function()
    {
    	$("#"+_self.formId+" input[type=submit]").prop("disabled",false);
    	$("#"+_self.formId+" input[type=reset]").prop("disabled",false);
    }
    
    _self.resetLocationsFields = function()
    {
    	var fileInput = $("#"+_self.originalFileInputId);
		try{
			fileInput.val("");
		} catch (err){
			// do nothing. This only happens on some browsers.
			// And when this error does happen, we don't need the above line anyway.
		}
		fileInput.replaceWith( fileInput = fileInput.clone( true ) );
		$("#"+_self.originalFileNameInputId).val("");
    }
    
    _self.popAlert = function(msg,hideButton)
    {
    	if(hideButton) $("#"+_self.alertBoxCloseId).hide();
    	else $("#"+_self.alertBoxCloseId).show();
    	$("#"+_self.alertBoxContentId).html(msg);
    	$("#"+_self.alertBoxId).show();
    }
    _self.popWaiting = function(msg)
    {
    	_self.popAlert("<img src=\"http://www.informatics.jax.org/assets/images/loading.gif\" height=\"24\" width=\"24\"> "+msg,true);
    }
    _self.resolveFilename = function(filename)
    {
    	// strips any directory info off the filename
    	if(filename)
    	{
    		var slashIdx = filename.lastIndexOf("/");
    		if(slashIdx>=0) filename = filename.substr(slashIdx+1);
    		var backSlashIdx = filename.lastIndexOf("\\");
    		if(backSlashIdx>=0) filename = filename.substr(backSlashIdx+1);
    	}
    	return filename;
    }
}

var resetLocationsFileFields,resetGeneFileFields;
var repositionUploadWidgets = function()
{
	//$("#geneFileDiv").position({of:$("#geneFilePlaceholder"),my:"left"});
	//$("#locationsFileDiv").position({of:$("#locationsFileHome"),my:"right"});
}
var HDP_FUW;
var HDP_FUW_GENES;
$(function(){
	// position the upload inside the proper div
	repositionUploadWidgets();
	
	HDP_FUW = new HDPFileUploadWidget({formId:"diseasePortalQueryForm"});
	HDP_FUW_GENES = new HDPFileUploadWidget({formId:"diseasePortalQueryForm",
		fileInputId: "geneFileInput",
		fileNameInputId: "geneFileName",
		fileNotifyId: "geneFileNotify",
		hiddenFormId: "hiddenFileForm2"});
	resetLocationsFileFields=function(){
		HDP_FUW.resetLocationsFields();
		/*
		 *  Setting a timeout here shouldn't make a damn difference, 
		 *  	but there is a weird race condition with YUI's history manager
		 *  	when called inside the reset button's event handler.
		 *  I'm just glad that this at least solves it.
		 */
		setTimeout(HDP_FUW.setLocationsFileSession,300); 
	}
	
	resetGeneFileFields=function(){
		HDP_FUW_GENES.resetLocationsFields();
		/*
		 *  Setting a timeout here shouldn't make a damn difference, 
		 *  	but there is a weird race condition with YUI's history manager
		 *  	when called inside the reset button's event handler.
		 *  I'm just glad that this at least solves it.
		 */
		setTimeout(HDP_FUW_GENES.setLocationsFileSession,300); 
	}
	
	$("#locationsFileInput").change(HDP_FUW.setLocationsFileSession);
	$("#geneFileInput").change(HDP_FUW_GENES.setLocationsFileSession);
	$("#enableVcfFilter").change(function(){
		var input = this;
		input.disabled = true;
		setTimeout(function(){
			input.disabled = false;
			HDP_FUW.setLocationsFileSession();
		},400); 
	});
});
