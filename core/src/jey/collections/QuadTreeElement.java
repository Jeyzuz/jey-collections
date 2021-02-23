package jey.collections;

public class QuadTreeElement<T> {
	
	public final float x;
	public final float y;
	public final T data;
	
	protected QuadTreeElement(float x, float y, T data) {
		this.x = x;
		this.y = y;
		this.data = data;
	}
	
	protected QuadTreeElement(T data, float x, float y) {
		this(x, y, data);
	}

}
