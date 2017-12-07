package data;

public class GO {

	public Loc loc;
	public String name;

	/**
	 * Used to avoid the use of hashmaps
	 * @param loc position of the Sprite
	 * @param name name of the sprite
	 */
	public GO(Loc loc, String name) {
		this.loc = loc;
		this.name = name;
	}

}
