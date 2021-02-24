package jey.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import jey.collections.QuadTree;
import jey.collections.DefaultQuadElement;
import jey.collections.QuadNode;

public class QuadTreeDemo extends InputAdapter implements Demo {
	
	private static final int SIZE = 500;
	private static final int POPULATION = 1000;
	private QuadTree<DefaultQuadElement> tree;
	private Rectangle searchBounds = new Rectangle(0, 0, 10, 10);
	
	public QuadTreeDemo() {
		tree = new QuadTree<DefaultQuadElement>(0, 0, SIZE, SIZE);
		tree.elementsPerNode = 4;
		tree.joinThreshold = 4;
		tree.maxDepth = 12;
		for (int i = 0; i < POPULATION; i++) {
			float x = MathUtils.random(SIZE);
			float y = MathUtils.random(SIZE);
			tree.add(new DefaultQuadElement(x, y, x + ", " + y));
		}
		Gdx.input.setInputProcessor(this);
	}

	@Override
	public void draw(ShapeRenderer renderer, Vector3 mouseInWorld) {
		renderer.setColor(Color.WHITE);
		for (QuadNode<DefaultQuadElement> node : tree.nodes()) {	
			renderer.rect(node.bounds.x, node.bounds.y, node.bounds.width, node.bounds.height);
			for (DefaultQuadElement element : node.elements) {
				renderer.rect(element.x, element.y, 1, 1);
			}
		}
		renderer.setColor(Color.RED);
		searchBounds.setCenter(mouseInWorld.x, mouseInWorld.y);
		renderer.rect(searchBounds.x, searchBounds.y, searchBounds.width, searchBounds.height);
		DefaultQuadElement nearestSlow = tree.elementNearest(mouseInWorld.x, mouseInWorld.y, searchBounds);
		Array<QuadNode<DefaultQuadElement>> nodesSlow = tree.getRoot().in(searchBounds, new Array<QuadNode<DefaultQuadElement>>());
		if (nearestSlow != null)
			renderer.rect(nearestSlow.getX(), nearestSlow.getY(), 1.1f, 1.1f);
		for (QuadNode<DefaultQuadElement> node : nodesSlow) {
			renderer.rect(node.bounds.x, node.bounds.y, node.bounds.width, node.bounds.height);
		}
		if (tree.getRoot().bounds.contains(mouseInWorld.x, mouseInWorld.y)) {
			renderer.setColor(Color.GREEN);
			DefaultQuadElement nearestFast = tree.elementNearest(mouseInWorld.x, mouseInWorld.y);
			QuadNode<DefaultQuadElement> nodeFast = tree.getRoot().at(mouseInWorld.x, mouseInWorld.y);
			if (nearestFast != null)
				renderer.rect(nearestFast.getX(), nearestFast.getY(), 1.1f, 1.1f);
			renderer.rect(nodeFast.bounds.x, nodeFast.bounds.y, nodeFast.bounds.width, nodeFast.bounds.height);
			if (Gdx.input.isButtonJustPressed(Buttons.LEFT)) {
				tree.add(new DefaultQuadElement(mouseInWorld.x + ", " + mouseInWorld.y, mouseInWorld.x, mouseInWorld.y));
			}
			if (Gdx.input.isButtonJustPressed(Buttons.RIGHT)) {
				tree.removeNearest(mouseInWorld.x, mouseInWorld.y);
			}
		}
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		searchBounds.width -= amountY * 5;
		searchBounds.height -= amountY * 5;
		return false;
	}

}
