package edu.pcube.util;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.Number;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


import edu.pcube.datastore.InMemoryDataStore;
import edu.pcube.example.AggregateExample;
import edu.pcube.factory.InMemoryDataStoreFactory;


public class Data {


	public InMemoryDataStore inMemoryDataStore;

	public static void writeExcel(double a[][],int M) throws IOException, RowsExceededException, WriteException{   
		System.out.println(a[1][1]);

		File file = new File("./test2.xls");
		if(!file.exists()){
			file.createNewFile();
		}
		WritableWorkbook wwb= Workbook.createWorkbook(file);
		WritableSheet ws = wwb.createSheet("Test Sheet 1",0); 
		Number labelN = new Number(1, 1, 2);
		ws.addCell(labelN); 
		int index = 0;
		//写入数据
		System.out.println("M:"+M);
		System.out.println("a[1].length:"+a[1].length);
		for(int i=1;i<M;i++){
			for(int j=0;j<a[i].length;j++){
				Number label = new Number(0, index, a[i][j]);
				ws.addCell(label); 
				index++;
				System.out.println("i:"+i+"j:"+j+"a[i][j]:"+a[i][j]);
			}
		}
		wwb.write();   
		wwb.close();
	} 

	public static void main(String[] args) throws IOException, RowsExceededException, WriteException {
		Data data=new Data();
		data.inMemoryDataStore=InMemoryDataStoreFactory.fromExcel(AggregateExample.class.getResource("Sample.xls"));

		int M=data.inMemoryDataStore.getObjectGroupedTuples().keySet().size()+1;
		double d[][]=new double[M][100];

		for(Object key:data.inMemoryDataStore.getObjectGroupedTuples().keySet()){
			int N=data.inMemoryDataStore.getObjectTuplesbyObjectId(key).getRowValues().size();
			System.out.println(N);
			double r[]=new double[N];
			Random random=new Random(System.currentTimeMillis());
			double sum=0.0;
			for(int i=0;i<N;i++){
				r[i]=random.nextDouble();
				sum+=r[i];
			}
			System.out.println("a");
			for(int i=0;i<N;i++){
				r[i]=r[i]/sum;
			}
			System.out.println("key:"+Integer.valueOf(key.toString()));
			d[Integer.valueOf(key.toString())]=r;

		}

		writeExcel(d, M);
	}
}
