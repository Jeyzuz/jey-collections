package jey.collections;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class QuadTreeNode<T> {
	
	public final Rectangle bounds;
	public final int depth;
	public final Array<QuadTreeElement<T>> elements;
	
	private QuadTreeNode<T> parent;
	private QuadTreeNode<T> bottomLeft;
	private QuadTreeNode<T> bottomRight;
	private QuadTreeNode<T> topLeft;
	private QuadTreeNode<T> topRight;
	
	public QuadTreeNode(Rectangle bounds, QuadTreeNode<T> parent) {
		this.bounds = bounds;
		this.elements = new Array<QuadTreeElement<T>>();
		this.parent = parent;
		if (parent == null) {
			depth = 1;
		} else {
			depth = parent.depth + 1;
		}
	}
	
	public boolean isLeaf() {
		return bottomLeft == null || bottomRight == null || topLeft == null || topRight == null;
	}
	
	public boolean isRoot() {
		return parent == null;
	}
	
	public Array<QuadTreeNode<T>> in(Rectangle bounds, Array<QuadTreeNode<T>> containing) {
		if (isLeaf() && this.bounds.overlaps(bounds)) {
			containing.add(this);
		} else {
			if (bottomLeft.bounds.overlaps(bounds)) {
				bottomLeft.in(bounds, containing);
			}
			if (bottomRight.bounds.overlaps(bounds)) {
				bottomRight.in(bounds, containing);
			}
			if (topLeft.bounds.overlaps(bounds)) {
				topLeft.in(bounds, containing);
			}
			if (topRight.bounds.overlaps(bounds)) {
				topRight.in(bounds, containing);
			}
		}
		return containing;
	}
	
	public Array<QuadTreeNode<T>> all(Array<QuadTreeNode<T>> containing) {
		if (isLeaf()) {
			containing.add(this);
		} else {
			bottomLeft.all(containing);
			bottomRight.all(containing);
			topLeft.all(containing);
			topRight.all(containing);
		}
		return containing;
	}
	
	public QuadTreeNode<T> at(float x, float y) {
		if (isLeaf() && this.bounds.contains(x, y)) {
			return this;
		} 
		if (bottomLeft.bounds.contains(x, y)) {
			return bottomLeft.at(x, y);
		} else if (bottomRight.bounds.contains(x, y)) {
			return bottomRight.at(x, y);
		} else if (topLeft.bounds.contains(x, y)) {
			return topLeft.at(x, y);
		} else if (topRight.bounds.contains(x, y)) {
			return topRight.at(x, y);
		} else {
			return null;
		}
	}
	
	public Array<QuadTreeElement<T>> elementsNear(float x, float y, Array<QuadTreeElement<T>> containing) {
		containing.addAll(at(x, y).elements);
		return containing;
	}
	
	public Array<QuadTreeElement<T>> elementsNear(Rectangle bounds, Array<QuadTreeElement<T>> containing) {
		for (QuadTreeNode<T> node : in(bounds, new Array<QuadTreeNode<T>>())) {
			for (QuadTreeElement<T> element : node.elements) {
				containing.add(element);
			}
		}
		return containing;
	}
	
	public Array<QuadTreeElement<T>> elementsIn(Rectangle bounds, Array<QuadTreeElement<T>> containing) {
		for (QuadTreeNode<T> node : in(bounds, new Array<QuadTreeNode<T>>())) {
			for (QuadTreeElement<T> element : node.elements) {
				if (bounds.contains(element.x, element.y)) {
					containing.add(element);
				}
			}
		}
		return containing;
	}
	
	public void addElement(float x, float y, T data, int maxDepth, int elementsPerNode) {
		QuadTreeNode<T> node = at(x, y);
		node.elements.add(new QuadTreeElement<T>(x, y, data));
		if (node.elements.size > elementsPerNode && node.depth < maxDepth) {
			Vector2 center = node.bounds.getCenter(new Vector2());
			node.bottomLeft = new QuadTreeNode<T>(new Rectangle(node.bounds.x, node.bounds.y, node.bounds.width / 2, node.bounds.height / 2), node);
			node.bottomRight = new QuadTreeNode<T>(new Rectangle(center.x, node.bounds.y, node.bounds.width / 2, node.bounds.height / 2), node);
			node.topLeft = new QuadTreeNode<T>(new Rectangle(node.bounds.x, center.y, node.bounds.width / 2, node.bounds.height / 2), node);
			node.topRight = new QuadTreeNode<T>(new Rectangle(center.x, center.y, node.bounds.width / 2, node.bounds.height / 2), node);
			while (!node.elements.isEmpty()) {
				QuadTreeElement<T> element = node.elements.pop();
				node.at(element.x, element.y).elements.add(element);
			}
		}
	}
	
	public boolean removeElement(float x, float y, T data) {
		QuadTreeNode<T> node = at(x, y);
		for (int i = 0; i < node.elements.size; i++) {
			if (node.elements.get(i).data.equals(data)) {
				node.elements.removeIndex(i);
				if (node.parent.bottomLeft.elements.size + node.parent.bottomRight.elements.size + 
						node.parent.topLeft.elements.size + node.parent.topRight.elements.size == 0) {
					node.parent.bottomLeft = null;
					node.parent.bottomRight = null;
					node.parent.topLeft = null;
					node.parent.topRight = null;
				}
				return true;
			}
		}
		return false;
	}

}
