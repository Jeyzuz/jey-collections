package jey.collections;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class QuadTree<T> {
	
	public int maxDepth = 8;
	public int elementsPerNode = 8;
	public boolean createArrays = false;
	
	private QuadTreeNode<T> root;
	private Array<QuadTreeElement<T>> results;
	
	public QuadTree(Rectangle bounds) {
		root = new QuadTreeNode<T>(bounds, null);
		results = new Array<QuadTreeElement<T>>();
	}
	
	public QuadTree(float x, float y, float width, float height) {
		this(new Rectangle(x, y, width, height));
	}
	
	public Array<QuadTreeElement<T>> get(Rectangle bounds) {
		if (!root.bounds.overlaps(bounds)) {
			throw new IllegalArgumentException(bounds + " is out of bounds of " + root.bounds);
		}
		return root.elementsIn(bounds, results());
	}
	
	public QuadTreeElement<T> get(float x, float y, boolean checkNeighbors) {
		if (!root.bounds.contains(x, y)) {
			throw new IllegalArgumentException(x + ", " + y + " is out of bounds of " + root.bounds);
		}		
		if (checkNeighbors) {
			Rectangle paddedBounds = new Rectangle(root.at(x, y).bounds);
			float separationX = root.bounds.width / (1 << maxDepth);
			float separationY = root.bounds.height / (1 << maxDepth);
			paddedBounds.set(paddedBounds.x - separationX, paddedBounds.y - separationY, 
					paddedBounds.width + 2 * separationX, paddedBounds.height + 2 * separationY);
			return closest(x, y, root.elementsNear(paddedBounds, results()));
		} else {
			return closest(x, y, root.elementsNear(x, y, results()));
		}
	}
	
	public void add(float x, float y, T data) {
		if (!root.bounds.contains(x, y)) {
			throw new IllegalArgumentException(x + ", " + y + " is out of bounds of " + root.bounds);
		}
		root.addElement(x, y, data, maxDepth, elementsPerNode);
	}
	
	public boolean remove(T data, float x, float y, boolean checkNeighbors) {
		if (!root.bounds.contains(x, y)) {
			throw new IllegalArgumentException(x + ", " + y + " is out of bounds of " + root.bounds);
		}
		if (checkNeighbors) {
			Rectangle paddedBounds = new Rectangle(root.at(x, y).bounds);
			float separationX = root.bounds.width / (1 << maxDepth);
			float separationY = root.bounds.height / (1 << maxDepth);
			paddedBounds.set(paddedBounds.x - separationX, paddedBounds.y - separationY, 
					paddedBounds.width + 2 * separationX, paddedBounds.height + 2 * separationY);
			boolean removed = false;
			for (QuadTreeNode<T> node : root.in(paddedBounds, new Array<QuadTreeNode<T>>())) {
				if (node.removeElement(x, y, data) ) {
					removed = true;
				}
			}
			return removed;
		} else {
			return root.removeElement(x, y, data);
		}
	}
	
	public QuadTreeElement<T> closest(float x, float y, Array<QuadTreeElement<T>> elements) {
		QuadTreeElement<T> closest = null;
		for (QuadTreeElement<T> element : elements) {
			if (closest == null || Vector2.dst2(element.x, element.y, x, y) < Vector2.dst2(closest.x, closest.y, x, y)) {
				closest = element;
			}
		}
		return closest;
	}
	
	private Array<QuadTreeElement<T>> results() {
		if (createArrays) {
			return new Array<QuadTreeElement<T>>();
		}
		results.clear();
		return results;
	}

	public QuadTreeNode<T> getRoot() {
		return root;
	}

}
