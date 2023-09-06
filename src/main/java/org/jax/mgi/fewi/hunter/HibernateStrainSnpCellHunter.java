package org.jax.mgi.fewi.hunter;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.jax.mgi.fe.datamodel.StrainSnpCell;
import org.jax.mgi.fewi.searchUtil.SearchParams;
import org.jax.mgi.fewi.searchUtil.SearchResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


@Repository
public class HibernateStrainSnpCellHunter
{
    //--------------------------//
    //--- instance variables ---//
    //--------------------------//
    
    @Autowired
    private SessionFactory sessionFactory;
	
    //----------------------//
    //--- public methods ---//
    //----------------------//

    // This method is specifically to get the highest number of SNPs in a StrainSnpCell,
    // so we can use that count for scaling color values on the strain detail page.
    @SuppressWarnings("unchecked")
	public int getMaxCount() 
    {
		if (sessionFactory == null) {
			return 0;
		}

    	String hql = "select max(allCount) as maxCount from StrainSnpCell";
		List list = sessionFactory.getCurrentSession().createQuery(hql).list(); 
		return ( (Integer)list.get(0) ).intValue();
    }
}
