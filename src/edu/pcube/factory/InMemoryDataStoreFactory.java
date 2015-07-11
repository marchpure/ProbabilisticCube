package edu.pcube.factory;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

import com.macrofocus.data.source.ExcelDataSource;

import edu.pcube.datastore.InMemoryDataStore;


public class InMemoryDataStoreFactory {
	private static final InMemoryDataStoreFactory ourInstance = new InMemoryDataStoreFactory();

	public static InMemoryDataStoreFactory getInstance() {
		return ourInstance;
	}

	protected InMemoryDataStoreFactory() {
	}

	/**
	 * Creates a data frame by loading an Excel (.xls, .xlsx) file.
	 */
	public static InMemoryDataStore<Object> fromExcel(URL url) throws IOException {
		try {
			return new InMemoryDataStore<Object>(new ExcelDataSource(url).load(null));
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
}
