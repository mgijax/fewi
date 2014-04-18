package org.jax.mgi.fewi.config;

import javax.annotation.PostConstruct;

import org.jax.mgi.fewi.util.file.TextFileReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


/**
 * Used by the ContextLoader to inject web templates (from webshare/mgiconfig) into our JSPs
 */
@Component
public class WebTemplate {

	private @Value("${template.dir}") String templateLoc;

    String headHtml;
    String bodyStartHtml;
    String bodyStartHdpHtml;
    String bodyStopHtml;
    String cssFiles = new String();
    String jsFiles = new String();
    
    ////////////////////
    // Setters/Modifiers
    ////////////////////
    public void addCss (String cssUrl) {
        String cssAnchor = "<link rel='stylesheet' type='text/css' href='" + cssUrl + "'/>";
        cssFiles = cssFiles + cssAnchor;
    }
    public void addJs (String jsUrl) {
        String jsAnchor = "<script type='text/javascript' src='" + jsUrl + "'></script>";
        jsFiles = jsFiles + jsAnchor;
    }


    /////////////
    // Accessors
    /////////////
    public String getTemplateHeadHtml() {
        return this.headHtml + cssFiles + jsFiles;
    }
    public String getTemplateBodyStartHtml() {
        return this.bodyStartHtml;
    }
    public String getTemplateBodyStartHdpHtml() {
        return this.bodyStartHdpHtml;
    }
    public String getTemplateBodyStopHtml() {
        return this.bodyStopHtml;
    }


    ///////////////////
    // Private Methods
    ///////////////////
    private String setTemplate(String templateFile) {

        String templateContents = new String();

        try {
            templateContents = TextFileReader.readFile(templateLoc + templateFile);
        }
        catch (Exception exc) {
            exc.printStackTrace();
        }

        return templateContents;
    }

    @PostConstruct
    public void init (){
    	
        // preload all relevant template files
        try {
            headHtml = setTemplate("templateHead.html");
            bodyStartHtml = setTemplate("templateBodyStart.html");
            bodyStartHdpHtml = setTemplate("templateHdpBodyStart.html");
            bodyStopHtml = setTemplate("templateBodyStop.html");
        }
        catch (Exception exc) {
            exc.printStackTrace();
        }
    }

}
