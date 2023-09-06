package org.jax.mgi.fewi.searchUtil;

import java.util.HashSet;
import java.util.Set;

public class MetaData {
    
    String score;
    Set <String> highlightWords = new HashSet<String> ();
    Boolean generated = Boolean.FALSE;;
    
    public String getScore() {
        return score;
    }
    public void setScore(String score) {
        this.score = score;
    }

    public void setGenerated() {
    	this.generated = Boolean.TRUE;
    }
    
    public Boolean isGenerated() {
    	return generated;
    }
    
}
