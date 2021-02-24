package jey.collections;

public class DefaultQuadElement implements QuadElement {
	
	public final float x;
	public final float y;
	public final Object data;
	
	public DefaultQuadElement(Object data, float x, float y) {
		this.x = x;
		this.y = y;
		this.data = data;
	}
	
	public DefaultQuadElement(float x, float y, Object data) {
		this(data, x, y);
	}

	@Override
	public float getX() {
		return x;
	}

	@Override
	public float getY() {
		return y;
	}
	
	public Object getData() {
		return data;
	}

}
