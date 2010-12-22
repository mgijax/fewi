package org.jax.mgi.fewi.searchUtil;

import java.util.HashSet;
import java.util.Set;

public class MetaData {
    
    String score;
    Set <String> highlightWords = new HashSet<String> ();
    
    public String getScore() {
        return score;
    }
    public void setScore(String score) {
        this.score = score;
    }
    public Set<String> getHighlightWords() {
        return highlightWords;
    }
    public void setHighlightWords(Set<String> highlightWords) {
        this.highlightWords = highlightWords;
    }
    public void addHighlightWords(String word) {
        this.highlightWords.add(word);
    }

}
