package jey.demo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import jey.collections.QuadTree;
import jey.collections.QuadTreeElement;
import jey.collections.QuadTreeNode;

public class QuadTreeDemo implements Demo {
	
	public static final float SIZE = 2000;
	public static final int MAX_DEPTH = 10;
	public static final int ELEMENTS_PER_NODE = 4;
	public static final int ELEMENTS_TO_INSERT = 2000;
	
	private QuadTree<String> tree;
	
	public QuadTreeDemo() {
		performanceTest();
		tree = new QuadTree<String>(0, 0, SIZE, SIZE);
		tree.maxDepth = MAX_DEPTH;
		tree.elementsPerNode = ELEMENTS_PER_NODE;
		for (int i = 0; i < ELEMENTS_TO_INSERT; i++) {
			float x = MathUtils.random(SIZE);
			float y = MathUtils.random(SIZE);
			tree.add(x, y, x + ", " + y);
		}
	}

	@Override
	public void draw(ShapeRenderer renderer, Vector3 mouseInWorld) {
		for (QuadTreeNode<String> node : tree.getRoot().all(new Array<QuadTreeNode<String>>())) {
			renderer.setColor(Color.WHITE);
			renderer.rect(node.bounds.x, node.bounds.y, node.bounds.width, node.bounds.height);
			for (QuadTreeElement<String> element : node.elements) {
				renderer.setColor(Color.WHITE);
				renderer.rect(element.x, element.y, 1, 1);
			}
		}
		if (tree.getRoot().bounds.contains(mouseInWorld.x, mouseInWorld.y)) {
			QuadTreeElement<String> cheapLookup = tree.get(mouseInWorld.x, mouseInWorld.y, false);
			QuadTreeElement<String> expensiveLookup = tree.get(mouseInWorld.x, mouseInWorld.y, true);
			if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
				float x = Float.max(Float.min(mouseInWorld.x + MathUtils.random(SIZE / 10), SIZE), 0);
				float y = Float.max(Float.min(mouseInWorld.y + MathUtils.random(SIZE / 10), SIZE), 0);
				tree.add(x, y, x + ", " + y);
			}
			if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
				if (expensiveLookup != null) {
					tree.remove(expensiveLookup.data, expensiveLookup.x, expensiveLookup.y, false);
				}
			}
			renderer.setColor(Color.RED);
			Rectangle paddedBounds = new Rectangle(tree.getRoot().at(mouseInWorld.x, mouseInWorld.y).bounds);
			float separationX = tree.getRoot().bounds.width / (1 << tree.maxDepth);
			float separationY = tree.getRoot().bounds.height / (1 << tree.maxDepth);
			paddedBounds.set(paddedBounds.x - separationX, paddedBounds.y - separationY, 
					paddedBounds.width + 2 * separationX, paddedBounds.height + 2 * separationY);
			for (QuadTreeNode<String> node : tree.getRoot().in(paddedBounds, new Array<QuadTreeNode<String>>())) {
				renderer.rect(node.bounds.x, node.bounds.y, node.bounds.width, node.bounds.height);
				renderer.rect(paddedBounds.x, paddedBounds.y, paddedBounds.width, paddedBounds.height);
			}
			if (expensiveLookup != null)
				renderer.rect(expensiveLookup.x - 1, expensiveLookup.y - 1, 2, 2);
			renderer.setColor(Color.GREEN);
			Rectangle in = tree.getRoot().at(mouseInWorld.x, mouseInWorld.y).bounds;
			renderer.rect(in.x, in.y, in.width, in.height);
			if (cheapLookup != null)
				renderer.rect(cheapLookup.x - 1, cheapLookup.y - 1, 2, 2);
		}
		if (Gdx.input.isKeyPressed(Keys.SPACE)) {
			float x = MathUtils.random(SIZE);
			float y = MathUtils.random(SIZE);
			tree.add(x, y, x + ", " + y);
		}
	}
	
	public void performanceTest() {
		System.out.println("--- QuadTree ---");
		tree = new QuadTree<String>(0, 0, SIZE, SIZE);
		tree.maxDepth = 8;
		tree.elementsPerNode = 16;
		long ms = System.currentTimeMillis();
		for (int i = 0; i < 10000000; i++) {
			float x = MathUtils.random(SIZE);
			float y = MathUtils.random(SIZE);
			tree.add(x, y, x + ", " + y);
		}
		System.out.println("Added 10000000 in " + (System.currentTimeMillis() - ms) + " ms");
		for (int i = 0; i < 5; i++) {
			float x = MathUtils.random(SIZE);
			float y = MathUtils.random(SIZE);
			ms = System.currentTimeMillis();
			for (int j = 0; j < 100; j++) {
				tree.get(x, y, false);
			}
			System.out.println("100 lookups no neighbors in " + (System.currentTimeMillis() - ms) + " ms");
			ms = System.currentTimeMillis();
			for (int j = 0; j < 100; j++) {
				tree.get(x, y, true);
			}
			System.out.println("100 lookups with neighbors in " + (System.currentTimeMillis() - ms) + " ms");
		}
		ms = System.currentTimeMillis();
		for (QuadTreeNode<String> node : tree.getRoot().all(new Array<QuadTreeNode<String>>())) {
			for (QuadTreeElement<String> element : node.elements) {
				element.data.isEmpty();
			}
		}
		System.out.println("Iterated all in " + (System.currentTimeMillis() - ms) + " ms");
		System.out.println("\n--- Array ---");
		int[] lookupIndexes = new int[3];
		for (int i = 0; i < 3; i++) {
			lookupIndexes[i] = MathUtils.random(10000000);
		}
		ms = System.currentTimeMillis();
		Array<String> array = new Array<String>();
		for (int i = 0; i < 10000000; i++) {
			float x = MathUtils.random(SIZE);
			float y = MathUtils.random(SIZE);
			array.add(x + ", " + y);
		}
		System.out.println("Added 10000000 in " + (System.currentTimeMillis() - ms) + " ms");
		for (int i = 0; i < 3; i++) {
			ms = System.currentTimeMillis();
			for (int j = 0; j < 10; j++) {
				array.get(lookupIndexes[i]);
			}
			System.out.println("10 lookups with index in " + (System.currentTimeMillis() - ms) + " ms");
			String s = array.get(lookupIndexes[i]);
			ms = System.currentTimeMillis();
			for (int j = 0; j < 10; j++) {
				for (String s2 : array) {
					if (s2.equals(s)) {
						break;
					}
				}
			}
			System.out.println("10 lookups without index in " + (System.currentTimeMillis() - ms) + " ms");
			ms = System.currentTimeMillis();
			for (String s2 : array) {
				s2.isEmpty();
			}
			System.out.println("Iterated all in " + (System.currentTimeMillis() - ms) + " ms");
		}
	}

}
