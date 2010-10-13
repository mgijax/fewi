package org.jax.mgi.fewi.config;

import java.util.*;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConfigWrapper {

    // property objects; will be loaded at post-construction
    Properties fewiProps = new Properties();   // fewi.properties
    Properties globalProps = new Properties(); // GlobalConfig.properties
    Properties externalUrlProps = new Properties(); // externalUrl.properties


    @PostConstruct
    public void init() {

        // input streams used to read the properties files
        InputStream gcPropertiesIn =
          ConfigWrapper.class.getClassLoader().getResourceAsStream("../properties/GlobalConfig.properties");
        InputStream fwPropertiesIn =
          ConfigWrapper.class.getClassLoader().getResourceAsStream("../properties/fewi.properties");


        try {globalProps.load(gcPropertiesIn);}
        catch (Exception e) {
          System.out.println("--> ConfigWrapper - Error loading GlobalConfig.properties");
        }

        try {fewiProps.load(fwPropertiesIn);}
        catch (Exception e) {
          System.out.println("--> ConfigWrapper - Error loading fewi.properties");
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    //  accessors mapping file properties to bean-based access for view
    ///////////////////////////////////////////////////////////////////////////

    // fewi url - favor local config value in fewi.properties
    public String getFewiUrl() {
		if (fewiProps.getProperty("fewi.url") != null) {
          return fewiProps.getProperty("fewi.url");
	    }
        return globalProps.getProperty("FEWI_URL");
    }

    // seqfetch
	public String getSeqFetchUrl() {
		return globalProps.getProperty("SEQFETCH_URL");
	}


    // Rat Map
	public String getRatMapUrl() {
		return externalUrlProps.getProperty("ratmap");
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

