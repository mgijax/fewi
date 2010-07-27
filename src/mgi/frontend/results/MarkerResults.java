package mgi.frontend.results;


import java.util.ArrayList;
import java.util.List;

public class MarkerResults {

    private List <Integer> markerIDs = new ArrayList <Integer> ();
    private List <String> metaData = new ArrayList <String> ();;
    private Integer maxCount;
    private String queryString;

    public List<Integer> getMarkerIDs() {

        List <Integer> temp = new ArrayList();
        return markerIDs;
    }

    public void setMarkerIDs(List<Integer> markerIDs) {
        this.markerIDs = markerIDs;
    }
    public void addMarkerIDs(Integer a) {
        this.markerIDs.add(a);
    }
    public List<String> getMetaData() {
        List <String> metaTemp = new ArrayList();
        return metaTemp;
        //return metaData;
    }
    public void setMetaData(List<String> metaData) {
        this.metaData = metaData;
    }
    public Integer getMaxCount() {
        return maxCount;
    }
    public void setMaxCount(Integer maxCount) {
        this.maxCount = maxCount;
    }
    public String getQueryString() {
        return "This is the query String";
        //return queryString;
    }
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }




}
