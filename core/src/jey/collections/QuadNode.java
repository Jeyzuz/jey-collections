package jey.collections;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class QuadNode<T extends QuadElement> {
	
	public final Rectangle bounds;
	public final int depth;
	public final Array<T> elements;
	public int size;
	
	protected QuadNode<T> parent;
	protected QuadNode<T> nw;
	protected QuadNode<T> ne;
	protected QuadNode<T> sw;
	protected QuadNode<T> se;
	
	
	public QuadNode(Rectangle bounds, QuadNode<T> parent) {
		this.bounds = bounds;
		this.depth = parent.depth + 1;
		this.elements = new Array<T>();
		this.parent = parent;
	}
	
	public QuadNode(Rectangle bounds) {
		this.bounds = bounds;
		this.depth = 1;
		this.elements = new Array<T>();
	}
	
	public boolean isLeaf() {
		return nw == null || ne == null || sw == null || se == null;
	}
	
	public boolean isRoot() {
		return parent == null;
	}
	
	public boolean isEmpty() {
		return size > 0;
	}
	
	public Array<QuadNode<T>> all(Array<QuadNode<T>> visited) {
		if (isLeaf()) {
			visited.add(this);
		} else {
			nw.all(visited);
			ne.all(visited);
			sw.all(visited);
			se.all(visited);
		}
		return visited;
	}
	
	public Array<QuadNode<T>> in(Rectangle bounds, Array<QuadNode<T>> visited) {
		if (isLeaf() && this.bounds.overlaps(bounds)) {
			visited.add(this);
			return visited;
		} else {
			if (nw.bounds.overlaps(bounds)) {
				nw.in(bounds, visited);
			}
			if (ne.bounds.overlaps(bounds)) {
				ne.in(bounds, visited);
			}
			if (sw.bounds.overlaps(bounds)) {
				sw.in(bounds, visited);
			}
			if (se.bounds.overlaps(bounds)) {
				se.in(bounds, visited);
			}
		}
		return visited;
	}
	
	public QuadNode<T> at(float x, float y) {
		if (bounds.contains(x, y)) {
			if (isLeaf()) {
				return this;
			} else if (nw.bounds.contains(x, y)) {
				return nw.at(x, y);
			} else if (ne.bounds.contains(x, y)) {
				return ne.at(x, y);
			} else if (sw.bounds.contains(x, y)) {
				return sw.at(x, y);
			} else if (se.bounds.contains(x, y)) {
				return se.at(x, y);
			} else {
				throw new IllegalArgumentException(x + ", " + y + " is out of bounds for all children in " + bounds);
			}
		} else {
			throw new IllegalArgumentException(x + ", " + y + " is out of bounds in node " + bounds);
		}
	}
	
	public void split() {
		Vector2 center = bounds.getCenter(new Vector2());
		nw = new QuadNode<T>(new Rectangle(bounds.x, center.y, bounds.width / 2, bounds.height / 2), this);
		ne = new QuadNode<T>(new Rectangle(center.x, center.y, bounds.width / 2, bounds.height / 2), this);
		sw = new QuadNode<T>(new Rectangle(bounds.x, bounds.y, bounds.width / 2, bounds.height / 2), this);
		se = new QuadNode<T>(new Rectangle(center.x, bounds.y, bounds.width / 2, bounds.height / 2), this);
		while (!elements.isEmpty()) {
			T element = elements.pop();
			at(element.getX(), element.getY()).elements.add(element);
		}
	}
	
	public void join() {
		elements.addAll(nw.elements);
		elements.addAll(ne.elements);
		elements.addAll(sw.elements);
		elements.addAll(se.elements);
		nw = null;
		ne = null;
		sw = null;
		se = null;
	}
	
	public void addElement(T element) {
		elements.add(element);
		QuadNode<T> current = this;
		size++;
		while ((current = current.parent) != null) {
			current.size++;
		}
	}
	
	public boolean removeElement(T element) {
		if (elements.removeValue(element, false)) {
			QuadNode<T> current = this;
			size--;
			while ((current = current.parent) != null) {
				current.size--;
			}
			return true;
		}
		return false;
	}
	
	public Array<T> getElements() {
		return elements;
	}

}
