/*
*   A widget for facilitating file uploads that are stored in a session on server.
*   For now, this is pretty specific to the human disease portal.
*
*   @author kstone
*/
function HDPFileUploadWidget(originalFormId,uploadActionUrl)
{
    this.formId = originalFormId;
    this.action = uploadActionUrl;
    
    // CONSTANTS
    this.originalFileInputId = "locationsFileInput";
    this.originalFileNameInputId = "locationsFileName";
    this.iframeId = "hiddenFileForm_IF";
    this.iframeContentId = this.iframeId + "_content";
    this.hiddenFormId = "hiddenFileForm";
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
            hiddenDiv += "<iframe id=\""+_self.iframeId+"\" src=\"about:blank\"></iframe>"
            hiddenDiv += "<div id=\""+_self.iframeContentId+"\">";
                hiddenDiv += "<form id=\""+_self.hiddenFormId+"\" method=\"POST\" enctype=\"multipart/form-data\" "+
                    "action=\""+_self.action+"\">";
                    hiddenDiv += "<input id=\""+_self.hiddenFileInputId+"\" type=\"file\" name=\"file\" value=\"\">";
                    hiddenDiv += "<input id=\""+_self.hiddenFieldInputId+"\" type=\"hidden\" name=\"field\" value=\"\">";
                    hiddenDiv += "<input id=\""+_self.hiddenTypeInputId+"\" type=\"hidden\" name=\"type\" value=\"\">";
                hiddenDiv += "</form>";
            //add the hidden form
            hiddenDiv += "</div>";
        hiddenDiv += "</div>";
        qFormParent.append(hiddenDiv);
        
        // also add the alertBox dialog
        var alertDiv = "<div class=\"hide\" id=\""+_self.alertBoxId+"\" style=\"position:relative;\">";
        	alertDiv += "<div style=\"position:absolute;top: -200px;left: 200px;background-color: #ffeac3; border: 1px solid black; "+
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
    	var files = null;
    	if(this.files) files = this.files;
    	var filename = $("#"+_self.originalFileInputId).val();
    	$("#"+_self.originalFileNameInputId).val(filename);
    	
    	// copy the hidden form contents into the iframe
    	var formHtml = $("#"+_self.iframeContentId).html();
    	$('#'+_self.iframeId).contents().find('html').html(formHtml);
    	
    	// copy the selected file to the hidden file form
    	var iframeJq = $("#"+_self.iframeId);
    	iframeJq.contents().find("#"+_self.hiddenFileInputId)[0].files = files;
    	//$("#hiddenFile").val(filename);
    	iframeJq.contents().find("#"+_self.hiddenFieldInputId).val("locationsFile");
    	
    	iframeJq.contents().find("#"+_self.hiddenFormId).submit();

    	if((!files || files.length<1) && !filename) return;
    	
    	_self.disableForm();
    	_self.popWaiting("Processing file and caching data matches. Please wait.");
    	_self.interval_id = setInterval(function(){
    		var iframeJq = $("#"+_self.iframeId);
    		var success = iframeJq.contents().find("#success").text();
    		var error = iframeJq.contents().find("#error").text();
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
    				_self.popAlert("ERROR: Max file upload limit of 10MB exceeded.");
    			}
    			else
    			{
    				_self.popAlert(iframeJq.contents().find("body").html());
    			}
    			// reset the locations file fields
				_self.resetLocationsFields();
    		}
    	},_self.uploadPollInterval);
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
    	$("#locationsFileInput").val("");
		$("#locationsFileInput")[0].files=null;
		$("#locationsFileName").val("");
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
}

var resetLocationsFileFields;
var HDP_FUW;
$(function(){
	HDP_FUW = new HDPFileUploadWidget("diseasePortalQueryForm",fewiurl+"diseasePortal/uploadFile");
	resetLocationsFileFields=function(){
		HDP_FUW.resetLocationsFields();
		HDP_FUW.setLocationsFileSession();
	}
	
	$("#locationsFileInput").change(HDP_FUW.setLocationsFileSession);
});