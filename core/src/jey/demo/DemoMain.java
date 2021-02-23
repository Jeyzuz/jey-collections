package jey.demo;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class DemoMain extends ApplicationAdapter {

	private OrthographicCamera camera;
	private ShapeRenderer renderer;
	private Vector3 mouse;
	private Demo demo;
	
	@Override
	public void create () {
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.position.set(camera.viewportWidth / 2, camera.viewportHeight / 2, 0);
		renderer = new ShapeRenderer();
		renderer.setAutoShapeType(true);
		mouse = new Vector3();
		demo = new QuadTreeDemo();
	}

	@Override
	public void render () {
		update();
		updateCamera();
		draw();
	}
	
	
	@Override
	public void resize(int width, int height) {
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.position.set(width / 2, height / 2, 0);
		camera.update();
	}

	@Override
	public void dispose () {
		renderer.dispose();
	}
	
	private void updateCamera() {
		if (Gdx.input.isKeyPressed(Keys.NUM_1) || Gdx.input.isKeyPressed(Keys.NUMPAD_1)) {
			demo = new QuadTreeDemo();
		}
		Vector2 pan = new Vector2();
		if (Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP)) {
			pan.add(0, 1);
		}
		if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT)) {
			pan.add(-1, 0);
		}
		if (Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN)) {
			pan.add(0, -1);
		}
		if (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT)) {
			pan.add(1, 0);
		}
		pan.nor().scl(camera.zoom).scl(10);
		camera.translate(pan);
		if (Gdx.input.isKeyPressed(Keys.Q) || Gdx.input.isKeyPressed(Keys.PAGE_UP)) {
			camera.zoom += .03f;
		}
		if (Gdx.input.isKeyPressed(Keys.E) || Gdx.input.isKeyPressed(Keys.PAGE_DOWN)) {
			camera.zoom -= .03f;
		}
		camera.update();
	}
	
	private void update() {
		mouse.set(Gdx.input.getX(), Gdx.input.getY(), 0);
		camera.unproject(mouse);
	}
	
	private void draw() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		renderer.setProjectionMatrix(camera.combined);
		renderer.updateMatrices();
		renderer.begin();
		demo.draw(renderer, mouse);
		renderer.end();
	}
	
}
