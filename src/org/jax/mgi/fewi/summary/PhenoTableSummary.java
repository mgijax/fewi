package org.jax.mgi.fewi.summary;


/**
 * This is a special display layer class for representing the allele phenotype by genotype table
 * 
 * @author kstone
 *
 */
public class PhenoTableSummary 
{
//	 private Logger logger
//     = LoggerFactory.getLogger(PhenoTableSummary.class);
//	 
//	private List<MPSystem> significantSystems=null;
//	private List<MPSystem> inSignificantSystems=null;
//	private Map<String,MPSystem> significantSystemMap = new HashMap<String,MPSystem>();
//	private Map<String,MPSystem> inSignificantSystemMap = new HashMap<String,MPSystem>();
//	private List<MPGenotype> genotypes;
//	
//	public PhenoTableSummary(AllelePhenoSummary allele)
//	{
//		// do a first pass to get some metrics on number of columns required
//		List<MPGenotype> mpgList = new ArrayList<MPGenotype>();
//        for(AllelePhenoGenotypeAssociation ga : allele.getGenotypeAssociations())
//        {
//        	Genotype g = ga.getGenotype();
//        	logger.info("looping through genotypes: "+g.getGenotypeKey());
//        	if(g.getMPAnnotations().size()<1) continue;
//        	
//        	// build the display layer Genotype object
//        	MPGenotype mpg = new MPGenotype(g);
//        	// get the list of distinct jnums for this genotype
//        	Set<String> distinctJnums = new HashSet<String>();
//        	// get list of distinct sex types (M,F, NA)
//        	Set<String> distinctSexTypes = new HashSet<String>();
//        	for(MPAnnotation ma : g.getMPAnnotations())
//        	{
////        		logger.info("annotation="+ma.getJnum()+","+ma.getSex()+","+ma.getTerm()+
////        				","+ma.getSystem());
//    			// Set the distinct system and term list
//    			addTerm(ma.getSystem(),ma.getTerm(),ma.getTermID(),ma.isCall());
//        		mpg.addMPAnnotation(ma);
//        		// count distinct jnums
//        		distinctJnums.add(ma.getJnum());
//        		
//        		// count distinct sex types
//        		distinctSexTypes.add(ma.getSex());	
//        	}
//        	mpg.setDistinctSexes(distinctSexTypes);
//        	mpg.setDistinctJnums(distinctJnums);
//        	mpgList.add(mpg);
//        }
//        setGenotypes(mpgList);
//	}
//	public void addTerm(String system,String term, String termID,boolean call)
//	{
//		Map<String,MPSystem> systemMap = this.inSignificantSystemMap;
//		if(call) systemMap = this.significantSystemMap;
//		MPSystem ms;
//		if(systemMap.containsKey(system))
//		{
//			ms = systemMap.get(system);
//			ms.addTerm(term, termID);
//		}
//		else 
//		{
//			ms = new MPSystem(system);
//			ms.addTerm(term, termID);
//			systemMap.put(system, ms);
//		}
//		
//	}
//	public List<MPSystem> getSignificanSystems() {
//		if(significantSystems==null)
//		{
//			significantSystems = new ArrayList(this.significantSystemMap.values());
//			Collections.sort(significantSystems);
//		}
//		return significantSystems;
//	}
//
//	public void setSignificantSystems(List<MPSystem> significantSystems) {
//		this.significantSystems = significantSystems;
//	}
//	public List<MPSystem> getInSignificanSystems() {
//		if(inSignificantSystems==null)
//		{
//			inSignificantSystems = new ArrayList(this.inSignificantSystemMap.values());
//			Collections.sort(inSignificantSystems);
//		}
//		return inSignificantSystems;
//	}
//
//	public void setInSignificantSystems(List<MPSystem> inSignificantSystems) {
//		this.inSignificantSystems = inSignificantSystems;
//	}
//	
//	public List<MPGenotype> getGenotypes() {
//		return genotypes;
//	}
//
//	public void setGenotypes(List<MPGenotype> genotypes) {
//		this.genotypes = genotypes;
//	}
//	
//	/*
//	 * This method ensures a consistent way of making a combined key that defines an annotation
//	 * This gets used by different maps
//	 */
//	public String makeAnnotationKey(String sex,String jnum, String term)
//	{
//		return sex+"|"+jnum+"|"+term;
//	}
//	/**
//	 * inner class for representing the distinct system groups
//	 * @author kstone
//	 *
//	 */
//	public class MPSystem implements Comparable
//	{
//		private String system;
//		private String systemClass;
//		private List<MPTerm> terms = new ArrayList<MPTerm>();
//		private Set<String> termIDSet = new HashSet<String>();
//		
//		public MPSystem(String system)
//		{
//			this.system = system;
//			this.systemClass = system;
//			systemClass = systemClass.replace("/", "_");
//			systemClass = systemClass.replace("-", "_");
//			systemClass = systemClass.replace(" ", "_");
//
//		}
//		public String getSystemClass()
//		{
//			return systemClass;
//		}
//		public List<MPTerm> getTerms() {
//			return terms;
//		}
//
//		public void addTerm(String term, String termID)
//		{
//			if(!termIDSet.contains(termID))
//			{
//				termIDSet.add(termID);
//				this.terms.add(new MPTerm(term,termID));
//			}
//		}
//		public String getSystem()
//		{
//			return system;
//		}
//		/**
//		 * inner class for representing the distinct term groups
//		 * @author kstone
//		 *
//		 */
//		public class MPTerm
//		{
//			private String term;
//			private String termID;
//			
//			public MPTerm(String term,String termID)
//			{
//				this.term = term;
//				this.termID = termID;
//			}
//			public String getTerm() {
//				return term;
//			}
//			public String getTermID() {
//				return termID;
//			}
//		}
//		@Override
//		public int compareTo(Object arg0) {
//			return system.compareTo(((MPSystem) arg0).getSystem());
//		}
//	}
//	/**
//	 * inner class to represent the genotype groupings
//	 * @author kstone
//	 *
//	 */
//	public class MPGenotype
//	{
//		private Genotype genotype;
//		private List<MPSystemAnnotation> mpSystemAnnotations;
//		private List<MPAnnotation> mpAnnotations;
//		private List<String> distinctSexes;
//		private List<String> distinctJnums;
//		private Map<String,MPSystemAnnotation> systemAnnotMap = new HashMap<String,MPSystemAnnotation>();
//		private Map<String,MPAnnotation> annotMap = new HashMap<String,MPAnnotation>();
//
//		public MPGenotype(Genotype g)
//		{
//			this.genotype = g;
//		}
//		public Genotype getGenotype() {
//			return genotype;
//		}
//		public MPSystemAnnotation getSystemAnnotation(String sex,String jnum, String system)
//		{
//			String key = makeAnnotationKey(sex,jnum,system);
//			if(systemAnnotMap.containsKey(key)) return systemAnnotMap.get(key);
//			return null;
//		}
//		public MPAnnotation getAnnotation(String sex,String jnum, String termID)
//		{
//			String key = makeAnnotationKey(sex,jnum,termID);
//			if(annotMap.containsKey(key)) return annotMap.get(key);
//			return null;
//		}
//		public void addMPAnnotation(MPAnnotation mpa)
//		{
//			String annotKey = makeAnnotationKey(mpa.getSex(),mpa.getJnum(),mpa.getTermID());
//			String systemKey = makeAnnotationKey(mpa.getSex(),mpa.getJnum(),mpa.getSystem());
//			annotMap.put(annotKey, mpa);
//			MPSystemAnnotation msa;
//			if(systemAnnotMap.containsKey(systemKey))
//			{
//				msa = systemAnnotMap.get(systemKey);
//			}
//			else
//			{
//				msa = new MPSystemAnnotation();
//				msa.setSex(mpa.getSex());
//				msa.setJnum(mpa.getJnum());
//				msa.setSystem(mpa.getSystem());
//				systemAnnotMap.put(systemKey, msa);
//			}
//			msa.setCall(mpa.isCall());
//		}
//		public List<MPSystemAnnotation> getMpSystemAnnotations() {
//			return mpSystemAnnotations;
//		}
//		public void setMpSystemAnnotations(List<MPSystemAnnotation> mpSystemAnnotations) {
//			this.mpSystemAnnotations = mpSystemAnnotations;
//		}
//		public List<MPAnnotation> getMpAnnotations() {
//			return mpAnnotations;
//		}
//		public void setMpAnnotations(List<MPAnnotation> mpAnnotations) {
//			this.mpAnnotations = mpAnnotations;
//		}
//		public List<String> getDistinctSexes() {
//			return distinctSexes;
//		}
//		public void setDistinctSexes(Collection<String> distinctSexes) {
//			// maintain the order of "M","F","NA"
//			List<String> distinctSexTypes = new ArrayList<String>();
//			if(distinctSexes.contains("M")) distinctSexTypes.add("M");
//			if(distinctSexes.contains("F")) distinctSexTypes.add("F");
//			if(distinctSexes.contains("NA")) distinctSexTypes.add("NA");
//			this.distinctSexes = distinctSexTypes;
//		}
//		public List<String> getDistinctJnums() {
//			return distinctJnums;
//		}
//		public void setDistinctJnums(Collection<String> distinctJnums) {
//			this.distinctJnums = new ArrayList<String>(distinctJnums);
//		}
//		public int getGenoColspan() {
//			return this.distinctJnums.size() * this.distinctSexes.size();
//		}
//
//
//		/**
//		 * inner class to represent the aggregation of annotation calls by system
//		 * @author kstone
//		 *
//		 */
//		public class MPSystemAnnotation
//		{
//			private String sex;
//			private String jnum;
//			private String system;
//			private boolean call;
//			public String getSex() {
//				return sex;
//			}
//			public void setSex(String sex) {
//				this.sex = sex;
//			}
//			public String getJnum() {
//				return jnum;
//			}
//			public void setJnum(String jnum) {
//				this.jnum = jnum;
//			}
//			public String getSystem() {
//				return system;
//			}
//			public void setSystem(String system) {
//				this.system = system;
//			}
//			public boolean isCall() {
//				return call;
//			}
//			public void setCall(boolean call) {
//				this.call = this.call || call;
//			}
//		}
//	}
}

