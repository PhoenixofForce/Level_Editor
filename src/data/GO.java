package data;

public class GO {
	public float x, y, width, height;
	public String name;

	public GO(String name, float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.name = name;
	}

	public void move(float dx, float dy) {
		x += dx;
		y += dy;
	}
}
