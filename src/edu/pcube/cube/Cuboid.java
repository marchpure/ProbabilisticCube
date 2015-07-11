package edu.pcube.cube;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;

import edu.pcube.convolution.Complex;
import edu.pcube.convolution.FFT;
import edu.pcube.datastore.InMemoryDataStore;
import edu.pcube.datastore.ObjectGroupedTuples;

public class Cuboid<V> {
	
	protected final Cube cube;
	
	public HashMap<V[], HashMap<Object, Complex[]>> cells;
	private HashMap<V[], Complex[]> aggregationResult;
	private ArrayList<ObjectGroupedTuples<V>> quarantineZone;
	//private HashMap<String, Integer> columnHeaderRowIndex;
	
	public Cuboid(Cube cube, Dimensions dimensions) {
		this.cube=cube;
		this.cells=new HashMap<V[], HashMap<Object,Complex[]>>();
		this.quarantineZone=new ArrayList<ObjectGroupedTuples<V>>();
		this.aggregationResult=new HashMap<V[], Complex[]>();
	}
	
	public void build(InMemoryDataStore<V> inMemoryDataStore) {
		//build cells
		buildCells(inMemoryDataStore.getObjectGroupedTuples());
		//convolution in per cell
		long time1=System.currentTimeMillis();
		for(V[] cell:cells.keySet()){

			Complex result[] = new Complex[2];
			result[0]=new Complex(1.0, 0.0);	result[1]=new Complex(0.0, 0.0);

			HashMap<Object,Complex[]> objectPmfs=cells.get(cell);

			for(Object objectId:objectPmfs.keySet()){
			
				Complex[] zeroPaddedPmfs=objectPmfs.get(objectId);
				if(inMemoryDataStore.getObjectGroupedTuples().get(objectId).isTruncated()){
					zeroPaddedPmfs=zeroPadded(objectPmfs.get(objectId));
					quarantineZone.add(inMemoryDataStore.getObjectGroupedTuples().get(objectId));
				}
				result=convolve(result, zeroPaddedPmfs);
			}
			aggregationResult.put(cell, result);

		}

		printAggregation();
		long time2=System.currentTimeMillis();
		System.out.println("time2-time1:"+(time2-time1));
	}
	
	
	public void buildCells(HashMap<Object,ObjectGroupedTuples<V>> objectGroupedTuples){
		for(Iterator<ObjectGroupedTuples<V>> objectGroupedTuplesIter=objectGroupedTuples.values().iterator();objectGroupedTuplesIter.hasNext();){
			ObjectGroupedTuples<V> tuplesForSingleObject=objectGroupedTuplesIter.next();
			Object objectId=tuplesForSingleObject.getObjectid();
			HashMap<V[], Complex[]> pmfValues=tuplesForSingleObject.getPmfValues();
			
			for(V[] dimensionData:pmfValues.keySet()){
				V[] key=duplicateKeyOfPmf(dimensionData);

				if(key==null){
					key=dimensionData;
					cells.put(key, new HashMap<Object, Complex[]>());
					cells.get(key).put(objectId, pmfValues.get(key));
				}
				cells.get(key).put(objectId, pmfValues.get(dimensionData));
			}
		}
	}
	
	public Complex[] convolve(Complex[] a, Complex[] b){
		List<Complex[]> completeResults=complexesComplete(a, b);
		Complex[] x=completeResults.get(0);
		Complex[] y=completeResults.get(1);
		x=FFT.convolve(x, y);
		
		int x_length=a.length+b.length-1;
		
		if(x.length>x_length){
			Complex xClone[]=new Complex[x_length];
			System.arraycopy(x, 0, xClone, 0, x_length);
			x=new Complex[x_length];
			x=xClone;
		}
		
		return x;
	}
	
	public void printAggregation(){
		int index=0;
		for(V[] key:aggregationResult.keySet()){
			System.out.print("Cell "+index+":");
			System.out.print('\t');
			System.out.print("D1:"+key[0]);
			System.out.print('\t');
			System.out.print("D2:"+key[1]);
			System.out.print('\t');
			System.out.println();
			Complex resultComplex[]=aggregationResult.get(key);
			for(int i=0;i<resultComplex.length;i++){
				if(resultComplex[i].re()>0.00001){
					System.out.println(i+" : "+resultComplex[i].re());
				}
			}
		}
	}
	
	private static boolean is2Pow(int n) {
		// TODO Auto-generated method stub
		int result = ((n&(n-1))==0) ? (1) : (0);
		if(result==1){
			return true;
		}else {
			return false;
		}
	}
	
	private static ArrayList<Complex[]> complexesComplete(Complex[] x,Complex[] y){
		
		Complex[] m = null;
		Complex[] n = null;
		Complex ZERO = new Complex(0, 0);
		
		if(!is2Pow(x.length)||!is2Pow(y.length)||x.length!=y.length){
			int i=1;
			for(i=1;i<x.length||i<y.length;i=2*i){
			}
			if(x.length!=i){
				m=new Complex[i];
				Arrays.fill(m, ZERO);
				System.arraycopy(x, 0, m, 0, x.length);
			}else {
				m=x;
			}
			if(y.length!=i){
				n=new Complex[i];
				Arrays.fill(n, ZERO);
				System.arraycopy(y, 0, n, 0, y.length);
			}else {
				n=y;
			}
		}
		
		ArrayList<Complex[]> tmp=new ArrayList<Complex[]>();
		tmp.add(m);
		tmp.add(n);

		return tmp;
	}

	private Complex[] zeroPadded(Complex[] complexs) {
		double zeroPadAmout=1.0;
		for(int i=0;i<complexs.length;i++){
			zeroPadAmout-=complexs[i].re();
		}
		
		Complex calComplex[]=complexs.clone();
		calComplex[0]=new Complex(zeroPadAmout, 0.0);
		return calComplex;
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
		for(V[] key:cells.keySet()){
			if(isEqualArray(uniqueTuple,key)){
				return key;
			}
		}
		return null;
	}
}
