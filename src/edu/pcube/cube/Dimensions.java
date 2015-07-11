package edu.pcube.cube;


public class Dimensions {
	private static Dimensions get(Object... path) {
		return new Dimensions(path);
	}
	
	public final Object[] path;

	public Dimensions(Object... path) {
		this.path = path;
	}

	public static Dimensions create(Object... path) {
		return get(path);
	}
	
	public Object getLast() {
		return path[path.length - 1];
	}
	
	public void print(){
		for(int i=0;i<path.length;i++){
			System.out.print(path[i]);
			System.out.print('\t');
		}
	}
}