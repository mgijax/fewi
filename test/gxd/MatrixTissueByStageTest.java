package gxd;

import java.util.List;

import org.jax.mgi.fewi.controller.GXDController;
import org.jax.mgi.fewi.handler.GxdMatrixHandler;
import org.jax.mgi.fewi.matrix.GxdDummyMatrixCell;
import org.jax.mgi.fewi.matrix.GxdMatrixCell;
import org.jax.mgi.fewi.matrix.GxdMatrixRow;
import org.jax.mgi.fewi.matrix.GxdStageGridJsonResponse;
import org.jax.mgi.fewi.matrix.OpenCloseState;
import org.jax.mgi.fewi.test.base.BaseConcordionTest;
import org.jax.mgi.fewi.test.mock.MockGxdControllerQuery;
import org.springframework.beans.factory.annotation.Autowired;


public class MatrixTissueByStageTest extends BaseConcordionTest {

	@Autowired
	GXDController controller;
	
	@Autowired
	GxdMatrixHandler matrixHandler;
	
	public GxdStageGridJsonResponse getStageGridByNomenQuery(String nomen) throws Exception
	{
		MockGxdControllerQuery mq = this.getMockQuery().gxdController(controller);
		mq.setNomenclature(nomen);
		return mq.getStageGrid();
	}
	
	public GxdStageGridJsonResponse getStageGridByNomenAndStageQuery(String nomen,String stage) throws Exception
	{
		MockGxdControllerQuery mq = this.getMockQuery().gxdController(controller);
		mq.setNomenclature(nomen);
		mq.setTheilerStage(Integer.parseInt(stage));
		return mq.getStageGrid();
	}
	
	public GxdStageGridJsonResponse getStageGridChildrenByNomenQuery(String nomen,String parentRowId) throws Exception
	{
		MockGxdControllerQuery mq = this.getMockQuery().gxdController(controller);
		mq.setNomenclature(nomen);
		
		return mq.getStageGrid(parentRowId);
	}
	
	public GxdStageGridJsonResponse getExpandedStageGridByNomenAndStageQuery(String nomen,String stage) throws Exception
	{
		MockGxdControllerQuery mq = this.getMockQuery().gxdController(controller);
		mq.setNomenclature(nomen);
		mq.setTheilerStage(Integer.parseInt(stage));
		
		return this.getExpandedStageGrid(mq);
	}

	public String getCellByTerm(GxdStageGridJsonResponse<GxdMatrixCell> response, String term,String colId) throws Exception
	{
		// map the term to a rowId for comparison
		String rowId = getTermId(term,response);
		
		for(GxdMatrixCell cell : response.getData())
		{
			if(cell.getRid().equals(rowId) && cell.getCid().equalsIgnoreCase(colId))
			{
				if(cell instanceof GxdDummyMatrixCell)
				{
					return "Valid symbol";
				}
				return "colored";
			}
		}
		return "blank";
	}
	
	public String getRowState(GxdStageGridJsonResponse response,String term)
	{
		List<GxdMatrixRow> rows = matrixHandler.getFlatTermList(response.getRows(),null);
		String OPEN_DEFAULT = "Open by default";
		String CAN_OPEN = "Can be opened";
		String CANNOT_OPEN = "CANNOT be opened";
		String NOT_FOUND = "not found";
		String state = NOT_FOUND;
		for(GxdMatrixRow row: rows)
		{
			if(row.getTerm().equalsIgnoreCase(term))
			{
				// is row open?
				if(row.getOc().equals(OpenCloseState.OPEN.getState()))
				{
					state = OPEN_DEFAULT;
				}
				// is this row openable?
				else if(row.getEx())
				{
					// only set this state if it's not a default open row
					if(!OPEN_DEFAULT.equals(state))
					{
						state = CAN_OPEN;
					}
				}
				else
				{
					state = CANNOT_OPEN;
				}
			}
		}
		return state;
	}
	
	public Integer getAssayResultCountByStructure(String structureId) throws Exception
	{
		MockGxdControllerQuery mq = this.getMockQuery().gxdController(controller);
		mq.setStructure(structureId);
		mq.pageSize=0;
		return mq.getAssayResults().getTotalCount();
	}
	
	public Integer getGridCellTotalCountByStructure(String structureId) throws Exception
	{
		MockGxdControllerQuery mq = this.getMockQuery().gxdController(controller);
		mq.setStructure(structureId);
		GxdStageGridJsonResponse<GxdMatrixCell> response = mq.getStageGrid();
		// add up all the cells
		Integer count = 0;
		for(GxdMatrixCell cell : response.getData())
		{
			if(cell instanceof GxdDummyMatrixCell) continue;
			count += Integer.parseInt(cell.getVal());
		}
		return count;
	}
	
	
	/*
	 * helper methods
	 */
	private String getTermId(String term,GxdStageGridJsonResponse response)
	{
		List<GxdMatrixRow> rows = matrixHandler.getFlatTermList(response.getRows());
		for(GxdMatrixRow row : rows)
		{
			if(row.getTerm().equalsIgnoreCase(term))
			{
				return row.getRid();
			}
		}
		return "";
	}
	
	private GxdStageGridJsonResponse<GxdMatrixRow> getExpandedStageGrid(MockGxdControllerQuery mq) throws Exception
	{
		GxdStageGridJsonResponse<GxdMatrixRow> response = mq.getStageGrid();
		for(GxdMatrixRow row : response.getRows())
		{
			getExpandedStageGrid(mq,row, response);
		}
		return response;
	}
	private void getExpandedStageGrid(MockGxdControllerQuery mq, GxdMatrixRow row, GxdStageGridJsonResponse prevResponse) throws Exception
	{
		if(row.getEx())
		{
			// expand row and add the new rows and data from it
			GxdStageGridJsonResponse<GxdMatrixRow> expandResponse = mq.getStageGrid(row.getRid());
			for(GxdMatrixRow child : expandResponse.getRows())
			{
				row.addChild(child);
			}
			prevResponse.getData().addAll(expandResponse.getData());
			
		}
		// try to iterate each matrix row and expand it
		for(GxdMatrixRow child : row.getChildren())
		{
			getExpandedStageGrid(mq,child, prevResponse);
		}
	}
}
	
