package edu.pcube.datastore;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.table.TableModel;

public class InMemoryDataStore<V> {
	private HashMap<Object,ObjectGroupedTuples<V>> objectGroupedTuples;
	private ArrayList<String> columnHeaders;

	public InMemoryDataStore(TableModel tableModel) {
		this.columnHeaders=new ArrayList<String>();
		this.objectGroupedTuples=new HashMap<Object, ObjectGroupedTuples<V>>();
		
		for(int r = 0; r<tableModel.getRowCount();r++){
			V[] values = (V[]) new Object[tableModel.getColumnCount()-1];
			final Object objectId=tableModel.getValueAt(r, 0);			
			for(int c = 1; c < tableModel.getColumnCount(); c++) {
				final V valueAt = (V)tableModel.getValueAt(r, c);
				values[c-1]=valueAt;
			}
			
			if(!this.objectGroupedTuples.containsKey(objectId)){
				this.objectGroupedTuples.put(objectId, new ObjectGroupedTuples<V>(objectId));
			}
			objectGroupedTuples.get(objectId).getRowValues().add(values);
		}
		
		HashMap<String, Integer> columnIndex=new HashMap<String, Integer>();
		
		for(int c = 0; c < tableModel.getColumnCount(); c++) {
			this.columnHeaders.add(tableModel.getColumnName(c));
			if(c!=0){
				columnIndex.put(tableModel.getColumnName(c), c-1);
			}
		}
		
		for(Object objectId:this.getObjectGroupedTuples().keySet()){
			this.getObjectTuplesbyObjectId(objectId).setColumnIndex(columnIndex);
			this.getObjectTuplesbyObjectId(objectId).setDimensions(this.getBaseDimensions());
			this.getObjectTuplesbyObjectId(objectId).generatePMF();;
		}
	}

	public HashMap<Object, ObjectGroupedTuples<V>> getObjectGroupedTuples() {
		return objectGroupedTuples;
	}
	
	public ObjectGroupedTuples<V> getObjectTuplesbyObjectId(Object objectId) {
		return objectGroupedTuples.get(objectId);
	}
	
	public ArrayList<String> getColumnHeaders() {
		return columnHeaders;
	}

	public String[] getBaseDimensions(){
		//the number of dimensions
		int N=columnHeaders.size()-3;
		String[] dimensions=new String[N];

		int dimensionIndex=0;
		for(int c=0;c<columnHeaders.size();c++){
			if(!this.getColumnHeaders().get(c).equals("P")&&!this.getColumnHeaders().get(c).equals("X")
					&&!this.getColumnHeaders().get(c).equals("object")){
				dimensions[dimensionIndex]=this.getColumnHeaders().get(c);
				dimensionIndex++;
			}
		}
		return dimensions;
	}
}
