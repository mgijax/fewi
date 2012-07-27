package org.jax.mgi.fewi.test.concordion;

import java.util.HashMap;
import java.util.Map;

import org.concordion.api.CommandCall;
import org.concordion.api.CommandCallList;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;
import org.concordion.internal.Row;
import org.concordion.internal.SimpleEvaluator;
import org.concordion.internal.Table;
import org.concordion.internal.command.EchoCommand;

public class TableSupport {
    private final CommandCall tableCommandCall;
    private final Map<Integer, CommandCall> commandCallByColumn = new HashMap<Integer, CommandCall>();
    private Table table;
    private Evaluator evaluator = null;
    private ResultRecorder resultRecorder = null;
    
    public TableSupport(CommandCall tableCommandCall) {
        assert tableCommandCall.getElement().isNamed("table");
        this.tableCommandCall = tableCommandCall;
        this.table = new Table(tableCommandCall.getElement());
        populateCommandCallByColumnMap();
    }
    public TableSupport(CommandCall tableCommandCall,Evaluator evaluator,ResultRecorder resultRecorder) {
    	this.evaluator = evaluator;
    	this.resultRecorder = resultRecorder;
        assert tableCommandCall.getElement().isNamed("table");
        this.tableCommandCall = tableCommandCall;
        this.table = new Table(tableCommandCall.getElement());
        populateCommandCallByColumnMap();
    }

    public int getColumnCount() {
        return getLastHeaderRow().getCells().length;
    }

    public Row[] getDetailRows() {
        return table.getDetailRows();
    }

    public void copyCommandCallsTo(Row detailRow) {
        int columnIndex = 0;
        for (Element cell : detailRow.getCells()) {
            CommandCall cellCall = commandCallByColumn.get(new Integer(columnIndex));
            if (cellCall != null) {
                cellCall.setElement(cell);
            }
            columnIndex++;
        }
    }

    private void populateCommandCallByColumnMap() {
        Row headerRow = getLastHeaderRow();
        CommandCallList children = tableCommandCall.getChildren();
        for (int i = 0; i < children.size(); i++) {
            CommandCall childCall = children.get(i);
            int columnIndex = headerRow.getIndexOfCell(childCall.getElement());
            if (columnIndex == -1) {
            	//try to execute the command and see what happens!
            	childCall.verify(evaluator, resultRecorder);
            	columnIndex = -1;
            	//throw new RuntimeException("Non-Echo Commands must be placed on <th> elements when using 'execute' or 'verifyRows' commands on a <table>.");
            }
            else
            {
            	commandCallByColumn.put(new Integer(columnIndex), childCall);
            }
        }
    }

    public Row getLastHeaderRow() {
        return table.getLastHeaderRow();
    }

    public Row addDetailRow() {
        return table.addDetailRow();
    }
}
