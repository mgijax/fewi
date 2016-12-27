package org.jax.mgi.fewi.forms;

import java.util.LinkedHashMap;
import java.util.Map;


/**
 * A place for share form logic and values
 */
public class FormWidgetValues {
	/*
	 * Pheno System widget values
	 */
	public static Map<String,String> getPhenoSystemWidgetValues1()
	{
		// we store the mp ids as form submit values
		Map<String,String> widgetValues = new LinkedHashMap<String,String>();
		widgetValues.put("MP:0005375","adipose tissue");
		widgetValues.put("MP:0005386","behavior/neurological");
		widgetValues.put("MP:0005385","cardiovascular system");
		widgetValues.put("MP:0005384","cellular");
		widgetValues.put("MP:0005382","craniofacial");
		widgetValues.put("MP:0005381","digestive/alimentary system");
		widgetValues.put("MP:0005380","embryo");
		widgetValues.put("MP:0005379","endocrine/exocrine glands");
		widgetValues.put("MP:0005378","growth/size");
		widgetValues.put("MP:0005377","hearing/vestibular/ear");
		widgetValues.put("MP:0005397","hematopoietic system");
		widgetValues.put("MP:0005376","homeostasis/metabolism");
		widgetValues.put("MP:0005387","immune system");
		widgetValues.put("MP:0010771","integument");
		widgetValues.put("MP:0005371","limbs/digits/tail");
	    
		return widgetValues;
	}
	public static Map<String,String> getPhenoSystemWidgetValues2()
	{
		// we store the mp ids as form submit values
		Map<String,String> widgetValues = new LinkedHashMap<String,String>();
		
		widgetValues.put("MP:0005370","liver/biliary system");
		widgetValues.put("MP:0010768","mortality/aging");
		widgetValues.put("MP:0005369","muscle");
		widgetValues.put("MP:0002006","neoplasm");
		widgetValues.put("MP:0003631","nervous system");
		widgetValues.put("MP:0003012","phenotype not analyzed");
		widgetValues.put("MP:0002873","normal phenotype");
		widgetValues.put("MP:0001186","pigmentation");
		widgetValues.put("MP:0005367","renal/urinary system");
		widgetValues.put("MP:0005389","reproductive system");
		widgetValues.put("MP:0005388","respiratory system");
		widgetValues.put("MP:0005390","skeleton");
		widgetValues.put("MP:0005394","taste/olfaction");
		widgetValues.put("MP:0005391","vision/eye");
	    
		return widgetValues;
	}
	public static Map<String,String> getPhenoSystemWidgetValuesAll()
	{
		Map<String,String> widgetValues = getPhenoSystemWidgetValues1();
		
		// append extra values
		widgetValues.putAll(getPhenoSystemWidgetValues2());
		
		return widgetValues;
	}
}
