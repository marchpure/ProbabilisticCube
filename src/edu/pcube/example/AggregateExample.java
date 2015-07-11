package edu.pcube.example;

import java.io.IOException;

import edu.pcube.cube.Cube;
import edu.pcube.datastore.InMemoryDataStore;
import edu.pcube.factory.InMemoryDataStoreFactory;


public class AggregateExample {
	public static void main(String[] args) {
		
		// import the rdbms data to memory
		try {
			InMemoryDataStore<Object> inMemoryDataStore = InMemoryDataStoreFactory.fromExcel(AggregateExample.class.getResource("Sample.xls"));

			Cube cube=new Cube(inMemoryDataStore);
			cube.getBaseCuboid().build(inMemoryDataStore);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
