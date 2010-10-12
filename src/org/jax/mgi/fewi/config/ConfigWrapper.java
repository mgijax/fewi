package org.jax.mgi.fewi.config;

import java.util.*;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigWrapper {


    // property objects; will be loaded at construction
    Properties fewiProperties = new Properties();   // fewi.properties
    Properties globalProperties = new Properties(); // GlobalConfig.properties


    @PostConstruct
    public void init() {

        // input streams used to read the properties files
        InputStream gcPropertiesIn =
          ConfigWrapper.class.getClassLoader().getResourceAsStream("../properties/GlobalConfig.properties");
        InputStream fwPropertiesIn =
          ConfigWrapper.class.getClassLoader().getResourceAsStream("../properties/fewi.properties");


        try {globalProperties.load(gcPropertiesIn);}
        catch (Exception e) {
          System.out.println("--> ConfigWrapper - Error loading GlobalConfig.properties");
        }

        try {fewiProperties.load(fwPropertiesIn);}
        catch (Exception e) {
          System.out.println("--> ConfigWrapper - Error loading fewi.properties");
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    //  accessors mapping file properties to bean-based access for view
    ///////////////////////////////////////////////////////////////////////////

    public String getFewiUrl() {
		if (fewiProperties.getProperty("fewi.url") != null) {
          return fewiProperties.getProperty("fewi.url");
	    }
        return globalProperties.getProperty("FEWI_URL");
    }
	public String getSeqFetchUrl() {
		return globalProperties.getProperty("SEQFETCH_URL");
	}

}



///////////////////////////////////////////////////////////////////////////
//  old code to be safed on the off chance we need switch implementations
///////////////////////////////////////////////////////////////////////////
//    private String fewiUrlFromGlobalConfig;
//    @Value("#{GlobalConfig.FEWI_URL}")
//    public void setFewiUrlGlobalConfig(String url) {
//        this.fewiUrlFromGlobalConfig = url;
//    }
//    private @Value("${fewi.url}") String fewiUrlFromLocalConfig;

