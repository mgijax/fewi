package org.jax.mgi.fewi.template;

import java.util.*;
import java.io.*;

public class WebTemplate {

    String templateLoc;

    String headHtml;
    String bodyStartHtml;
    String bodyStopHtml;
    String cssFiles = new String();
    String jsFiles = new String();

    //////////////
    // Constructor
    //////////////
    public WebTemplate(String directory) {

        templateLoc = directory;

        // preload all relevant template files
        try {
            headHtml = setTemplate("templateHead.html");
            bodyStartHtml = setTemplate("templateBodyStart.html");
            bodyStopHtml = setTemplate("templateBodyStop.html");
        }
        catch (Exception exc) {
            exc.printStackTrace();
        }
    }


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
    public String getTemplateBodyStopHtml() {
        return this.bodyStopHtml;
    }


    ///////////////////
    // Private Methods
    ///////////////////
    private String setTemplate(String templateFile) {

        String templateContents = new String();

        try {
            templateContents = readFile(templateLoc + templateFile);
        }
        catch (Exception exc) {
            exc.printStackTrace();
        }

        return templateContents;
    }

    /**
     * Returns the contents of a file as a string.
     *
     * @param filePath  Path of the file to be read
     * @return  String of the contents of the file
     * @throws IOException
     */
    private String readFile(String filePath) throws IOException {

        if (filePath != null) {

            // setup StringBuffer to hold contents of file
            StringBuffer sb = new StringBuffer();

            // setup file BufferedReader to read file
            FileInputStream fis = new FileInputStream(filePath);
            InputStreamReader isr = new InputStreamReader(fis);
            Reader in = new BufferedReader(isr);
            int ch; // holder for each character

            // read characters into StrinBuffer
            while ((ch = in.read()) > -1) {
                sb.append((char)ch);
            }

            // close file & return
            in.close();
            return sb.toString();
        }
        else {
            throw new IOException("Null file path");
        }
    }




}
