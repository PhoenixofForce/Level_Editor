package data;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TextureHandler {
	private TextureHandler() {}

	public static Map<String, BufferedImage> textures_png;
	public static Map<String, Rectangle> textures_sprite_sheet;
	public static Map<String, String> textures_sprite_sheet_texture;

	static {
		textures_png = new HashMap<>();
		textures_sprite_sheet = new HashMap<>();
		textures_sprite_sheet_texture = new HashMap<>();
	}

	/**Loads an image
	 *
	 * @param textureName name with which the image can be called
	 * @param fileName name of the file in which the image is located in
	 */
	public static void loadImagePng(String textureName, String fileName) {
		try {
			textures_png.put(textureName, ImageIO.read(new File(fileName)));
		} catch (IOException e) {
			System.err.println("Error loading texture: " + textureName);
			System.exit(-1);
		}
	}

	/**Loads an spritesheet from a .text-file
	 * A .png with the same name has to be in the same folder
	 *
	 * @param spriteSheetName
	 * @param fileName file that links to the .text file
	 */
	public static void loadImagePngSpriteSheet(String spriteSheetName, String fileName) {
		//if(textures_png.containsKey(spriteSheetName)) return;
		try {
			loadImagePng(spriteSheetName, fileName.substring(0, fileName.length()-4)+"png");
			Scanner s = new Scanner(new File(fileName), "UTF8");

			int amount = Integer.valueOf(s.nextLine());

			for (int i = 0; i < amount; i++) {
				String[] line = s.nextLine().split(" ");

				String texture = line[0];
				int x = Integer.valueOf(line[1]);
				int y = Integer.valueOf(line[2]);
				int width = Integer.valueOf(line[3]);
				int height = Integer.valueOf(line[4]);

				textures_sprite_sheet.put(spriteSheetName + "_" + texture, new Rectangle(x, y, width, height));
				textures_sprite_sheet_texture.put(spriteSheetName + "_" + texture, spriteSheetName);
			}
		} catch (Exception e) {
			System.err.println("Error loading spritesheet: " + spriteSheetName);
			e.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * @param name
	 * @return
	 */
	public static int getBlockCount(String name) {
		Pattern pattern = Pattern.compile(name+"_[0-9]*");
		int c = 0;
		for(String s: textures_png.keySet()) {
			Matcher matcher = pattern.matcher(s);
			if(matcher.matches()) c++;
		}

		for(String s: textures_sprite_sheet.keySet()) {
			Matcher matcher = pattern.matcher(s);
			if(matcher.matches()) c++;
		}

		return c;
	}

	public static String getSpriteSheetImage(String textureName){
		return textures_sprite_sheet_texture.get(textureName);
	}

	public static void createError(int ts) {
		BufferedImage im = new BufferedImage(ts, ts, BufferedImage.TYPE_INT_ARGB);
		Graphics g = im.getGraphics();
		g.setColor(new Color(255, 0, 200));
		g.fillRect(0, 0, ts, ts);
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, ts/2, ts/2);
		g.fillRect(ts/2,ts/2,ts/2,ts/2);
		textures_png.put("error_" + ts, im);
	}

	public static BufferedImage getImagePng(String textureName) {
		if (textures_png.containsKey(textureName))
			return textures_png.get(textureName);
		else if(textures_sprite_sheet.containsKey(textureName)) {
			Rectangle rec = textures_sprite_sheet.get(textureName);
			return textures_png.get(textures_sprite_sheet_texture.get(textureName)).getSubimage(rec.x, rec.y, rec.width, rec.height);
		}
		throw new RuntimeException("No such image: " + textureName);
	}

	public static boolean existsImagePng(String textureName) {
		if (textures_png.containsKey(textureName))
			return true;
		else if(textures_sprite_sheet.containsKey(textureName)) {
			return true;
		}
		return false;
	}

	/** Gets all images in form of ImageIcons, scales all image up to min 32x32
	 * @return Map of all image names, with their bound image as an image icon
	 */
	public static Map<String, ImageIcon> getAllImages() {
		Map<String, ImageIcon> out = new HashMap<>();
		for(String s: textures_sprite_sheet.keySet()) {
			BufferedImage img = getImagePng(s);
			while(img.getWidth() < 32 && img.getHeight() < 32) img = scale(img);
			out.put(s, new ImageIcon(img));
		}
		return out;
	}

	/** Doubles the image size
	*   @param in the image that gets scaled
	*   @return the scaled image
	**/
	private static BufferedImage scale(BufferedImage in) {
		BufferedImage out = new BufferedImage(in.getWidth() * 2, in.getHeight() * 2, BufferedImage.TYPE_INT_ARGB);
		out.getGraphics().drawImage(in, 0, 0, out.getWidth(), out.getHeight(), null);
		return out;
	}
}
