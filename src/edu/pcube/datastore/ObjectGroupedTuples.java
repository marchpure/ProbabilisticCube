package edu.pcube.datastore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import edu.pcube.convolution.Complex;

public class ObjectGroupedTuples<V> {
	private final Object objectid;
	private String[] dimensions;
	
	private HashMap<V[], Complex[]> pmfValues;
	private ArrayList<V[]> rowValues;
	private HashMap<String, Integer> columnIndex;
	
	public ObjectGroupedTuples(Object objectid) {
		this.objectid=objectid;
		this.columnIndex=new HashMap<String, Integer>();
		this.rowValues=new ArrayList<V[]>();
		this.pmfValues=new HashMap<V[], Complex[]>();
	}	
	
	public Object getObjectid() {
		return objectid;
	}

	public HashMap<V[], Complex[]> getPmfValues() {
		return pmfValues;
	}

	public ArrayList<V[]> getRowValues() {
		return rowValues;
	}

	public HashMap<String, Integer> getColumnIndex() {
		return columnIndex;
	}
	
	public void setColumnIndex(HashMap<String, Integer> columnIndex) {
		this.columnIndex = columnIndex;
	}

	public String[] getDimensions() {
		return dimensions;
	}

	public void setDimensions(String[] dimensions) {
		this.dimensions = dimensions;
	}

	public int getMeasuresMax(){
		int max=0;
		for(Iterator<V[]> iter=this.rowValues.iterator();iter.hasNext();){
			V[] row=iter.next();
			int measure=(Integer) row[this.getColumnIndex().get("X")];
			if(max<measure){
				max=measure;
			}
		}
		return max;
	}
	
	
	public boolean isEqualArray(V[] a,V[] b){
		if(a.length!=b.length){
			return false;
		}else {
			for(int i=0;i<a.length;i++){
				if(!a[i].equals(b[i])){
					return false;
				}
			}
		}
		return true;
	}
	
	public V[] duplicateKeyOfPmf(V[] uniqueTuple){
		for(V[] key:pmfValues.keySet()){
			if(isEqualArray(uniqueTuple,key)){
				return key;
			}
		}
		return null;
	}
	
	public V[] getDimensionData(V[] tuple){
		int N=tuple.length;
		V[] dimensionData=(V[]) new Object[N-2];
		
		int index=0;
		for(int i=0;i<this.getDimensions().length;i++){
			String columnHeader=this.getDimensions()[i];
			dimensionData[index]=tuple[this.columnIndex.get(columnHeader)];
			index++;
		}
		return dimensionData;
	}
	
	public V getMeasureData(V[] tuple){
		return tuple[columnIndex.get("X")];
	}
	
	public Complex getPData(V[] tuple){
		return new Complex((double) tuple[columnIndex.get("P")], 0.0);
	}
	
	public boolean isTruncated(){
		if(this.getPmfValues().size()>1){
			return true;
		}else {
			return false;
		}
	}

	public void generatePMF(){
		int N=this.getMeasuresMax()+1;
		
		for(Iterator<V[]> iter=this.rowValues.iterator();iter.hasNext();){
			V[] tuple=iter.next();
			V[] dimension=this.getDimensionData(tuple);
			V measure=this.getMeasureData(tuple);
			
			Complex p=this.getPData(tuple);
			
			V[] key=duplicateKeyOfPmf(dimension);
						
			if(key==null){
				pmfValues.put(dimension, new Complex[N]);
				for(int k=0;k<N;k++){
					pmfValues.get(dimension)[k]=new Complex(0.0, 0.0);
				}
				pmfValues.get(dimension)[(Integer)measure]=pmfValues.get(dimension)[(Integer)measure].plus(p);
			}else {
				pmfValues.get(key)[(Integer)measure]=pmfValues.get(key)[(Integer)measure].plus(p);
			}
		}
		//printPmf();
	}
	
	public void printPmf(){
		for(V[] key:this.pmfValues.keySet()){
			System.out.println("pmfValues.get(key).length:"+pmfValues.get(key).length);
			for(int i=0;i<pmfValues.get(key).length;i++){
				System.out.println(pmfValues.get(key)[i].re());
			}
		}
	}
}
