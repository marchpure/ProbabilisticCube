package edu.pcube.cube;

import java.util.HashMap;
import java.util.Map;

import edu.pcube.datastore.InMemoryDataStore;


/**
 * Models a multidimensional dataset, where that data are aggregated across an arbitrary number of dimensions.
 * It can also be seen as a lattice of cuboids.
 */
public class Cube {
	
	private final InMemoryDataStore inMemoryDataStore;
	private Dimensions baseCellDimension;
	private final Cuboid baseCuboid;
	
	private Map<Cuboid, Cuboid> child = new HashMap<Cuboid, Cuboid>();

	public Cube(InMemoryDataStore inMemoryDataStore) {
		this.inMemoryDataStore = inMemoryDataStore;
		this.baseCellDimension=Dimensions.create(this.inMemoryDataStore.getBaseDimensions());
		this.baseCuboid = new Cuboid(this, baseCellDimension);
	}

	public InMemoryDataStore getDataFrame() {
		return inMemoryDataStore;
	}

	public Dimensions getBaseCellDimension() {
		return baseCellDimension;
	}

	public Cuboid getBaseCuboid() {
		return baseCuboid;
	}

}
