package org.jax.mgi.fewi.util;

import java.util.ArrayList;
import java.util.List;

import org.jax.mgi.fe.datamodel.DiseaseModel;

import java.util.HashSet;

/* Is: a filter to ensure that we only display a given disease model once
 * 	on the 'all models' page for a disease
 * Has: a set of models displayed so far
 * Does: takes a list of models we'd like to display and filters out all those
 * 	which have already been displayed
 */
public class DiseaseModelFilter {
    // set of database keys for disease models we've already seen
    private HashSet<Integer> modelKeys = new HashSet<Integer>();

    // rely on default constructor

    // go through a list of models and return a new list with only those we
    // haven't already seen before
    public List<DiseaseModel> filter (List<DiseaseModel> models) {
	ArrayList<DiseaseModel> subset = new ArrayList<DiseaseModel>();

	for (DiseaseModel dm : models) {
	    int dmKey = dm.getDiseaseModelKey();
	    if (!modelKeys.contains(dmKey)) {
		modelKeys.add(dmKey);
		subset.add(dm);
	    }
	}
	return subset;
    }
}
