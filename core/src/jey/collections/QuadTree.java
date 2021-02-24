package jey.collections;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class QuadTree<T extends QuadElement> {
	
	public int maxDepth = 8;
	public int elementsPerNode = 8;
	public int joinThreshold = 8;
	public boolean allocateArrays = false;
	
	private QuadNode<T> root;
	private Array<QuadNode<T>> nodeIterator;
	private Array<T> elementIterator;
	
	public QuadTree(Rectangle bounds) {
		root = new QuadNode<T>(bounds);
		nodeIterator = new Array<QuadNode<T>>();
		elementIterator = new Array<T>();
	}
	
	public QuadTree(float x, float y, float width, float height) {
		this(new Rectangle(x, y, width, height));
	}
	
	public QuadNode<T> getRoot() {
		return root;
	}
	
	public int size() {
		return root.size;
	}

	public Array<QuadNode<T>> nodes() {
		return root.all(getNodeIterator());
	}
	
	public Array<T> elements() {
		Array<T> visited = getElementIterator();
		for (QuadNode<T> node : nodes()) {
			visited.addAll(node.elements);
		}
		return visited;
	}
	
	public Array<T> elementsIn(Rectangle bounds) {
		Array<T> visited = getElementIterator();
		for (QuadNode<T> node : root.in(bounds, getNodeIterator())) {
			for (T element : node.elements) {
				if (bounds.contains(element.getX(), element.getY())) {
					visited.add(element);
				}
			}
		}
		return visited;
	}
	
	public Array<T> elementsIn(float x, float y, float width, float height) {
		return (elementsIn(new Rectangle(x, y, width, height)));
	}
	
	public Array<T> elementsNear(float x, float y) {
		if (allocateArrays) {
			return root.at(x, y).elements;
		} else {
			Array<T> visited = getElementIterator();
			for (T element : root.at(x, y).elements) {
				visited.add(element);
			}
			return visited;
		}
	}
	
	public Array<T> elementsNear(Rectangle bounds) {
		Array<T> visited = getElementIterator();
		for (QuadNode<T> node : root.in(bounds, getNodeIterator())) {
			visited.addAll(node.elements);
		}
		return visited;
	}
	
	public T elementNearest(float x, float y) {
		T closest = null;
		for (T element : elementsNear(x, y)) {
			if (closest == null || Vector2.dst2(element.getX(), element.getY(), x, y) < Vector2.dst2(closest.getX(), closest.getY(), x, y)) {
				closest = element;
			}
		}
		return closest;
	}
	
	public T elementNearest(float x, float y, Rectangle bounds) {
		T closest = null;
		for (T element : elementsIn(bounds)) {
			if (closest == null || Vector2.dst2(element.getX(), element.getY(), x, y) < Vector2.dst2(closest.getX(), closest.getY(), x, y)) {
				closest = element;
			}
		}
		return closest;
	}
	
	public void add(T element) {
		QuadNode<T> node = root.at(element.getX(), element.getY());
		node.addElement(element);
		if (node.elements.size > elementsPerNode && node.depth < maxDepth) {
			node.split();
		}
	}
	
	public boolean remove(T element) {
		QuadNode<T> node = root.at(element.getX(), element.getY());
		if (node.removeElement(element)) {
			if (joinThreshold >= 0 && !node.isRoot() && node.parent.size < joinThreshold) {
				node.parent.join();
			}
			return true;
		}
		return false;
	}
	
	public boolean remove(T element, Rectangle bounds) {
		for (QuadNode<T> node : root.in(bounds, getNodeIterator())) {
			if (node.removeElement(element)) {
				if (joinThreshold >= 0 && !node.isRoot() &&  node.parent.size < joinThreshold) {
					node.parent.join();
				}
				return true;
			}
		}
		return false;
	}
	
	public T removeNearest(float x, float y) {
		QuadNode<T> node = root.at(x, y);
		T closest = null;
		for (T element : node.elements) {
			if (closest == null || Vector2.dst2(element.getX(), element.getY(), x, y) < Vector2.dst2(closest.getX(), closest.getY(), x, y)) {
				closest = element;
			}
		}
		if (node.removeElement(closest)) {
			if (joinThreshold >= 0 && !node.isRoot() && node.parent.size < joinThreshold) {
				node.parent.join();
			}
		}
		return closest;
	}
	
	private Array<T> getElementIterator() {
		if (allocateArrays) {
			return new Array<T>();
		} else {
			elementIterator.clear();
			return elementIterator;
		}
	}
	
	private Array<QuadNode<T>> getNodeIterator() {
		if (allocateArrays) {
			return new Array<QuadNode<T>>();
		} else {
			nodeIterator.clear();
			return nodeIterator;
		}
	}
	
	@Override
	public String toString() {
		String s = "";
		for (QuadNode<T> node : nodes()) {
			for (int i = 0; i < node.depth; i++) {
				s += "   ";
			}
			s += node.bounds.toString() + ", depth=" + node.depth + ", elements=" + node.elements.size + "\n";
		}
		return s;
	}
	

}
