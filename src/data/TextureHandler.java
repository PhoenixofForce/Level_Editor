package data;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
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

	public static void loadImagePng(String textureName, String fileName) {
		try {
			textures_png.put(textureName, ImageIO.read(new File(fileName)));
		} catch (IOException e) {
			System.err.println("Error loading texture: " + textureName);
			System.exit(-1);
		}
	}

	public static void loadImagePngSpriteSheet(String spriteSheetName, String fileName) {
		if(textures_png.containsKey(spriteSheetName)) return;
		try {
			loadImagePng(spriteSheetName, fileName.substring(0, fileName.length()-4)+"png");
			Scanner s = new Scanner(new File(fileName));

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

	public static int getCount(String name) {
		int c = 0;
		for(String s: textures_png.keySet()) {
			if(s.toLowerCase().contains(name.toLowerCase() + "_")) c++;
		}

		for(String s: textures_sprite_sheet.keySet()) {
			if(s.toLowerCase().contains(name.toLowerCase() + "_")) c++;
		}

		return c;
	}

	public static Rectangle getSpriteSheetBounds(String textureName) {
		return textures_sprite_sheet.get(textureName);
	}

	public static String getSpriteSheetImage(String textureName){
		return textures_sprite_sheet_texture.get(textureName);
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

	public static String getTextureName(BufferedImage img) {
		for(String s: textures_sprite_sheet.keySet()) {
			if(compareImages(getImagePng(s), img)) {
				return s;
			}
		}
		return null;
	}


	public static String getTextureName(ImageIcon img) {
		for(String s: textures_sprite_sheet.keySet()) {
			if(compareImages(convertIcon(new ImageIcon(getImagePng(s))), convertIcon(img))) {
				return s;
			}
		}
		return null;
	}

	public static List<String> getImagesOnSpriteSheet(String spriteSheetName) {
		return textures_sprite_sheet_texture.keySet().stream().filter(s -> getSpriteSheetImage(s).equals(spriteSheetName)).collect(Collectors.toList());
	}

	public static List<BufferedImage> getAllImages() {
		List<BufferedImage> out = new ArrayList<>();
		for(String s: textures_sprite_sheet.keySet()) {
			out.add(getImagePng(s));
		}
		return out;
	}

	private static boolean compareImages(BufferedImage imgA, BufferedImage imgB) {
		// The images must be the same size.
		if (imgA.getWidth() == imgB.getWidth() && imgA.getHeight() == imgB.getHeight()) {
			int width = imgA.getWidth();
			int height = imgA.getHeight();

			// Loop over every pixel.
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					// Compare the pixels for equality.
					if (imgA.getRGB(x, y) != imgB.getRGB(x, y)) {
						return false;
					}
				}
			}
		} else {
			return false;
		}

		return true;
	}

	private static BufferedImage convertIcon(ImageIcon icon) {
		BufferedImage bi = new BufferedImage(
				icon.getIconWidth(),
				icon.getIconHeight(),
				BufferedImage.TYPE_INT_RGB);
		Graphics g = bi.createGraphics();
		icon.paintIcon(null, g, 0,0);
		g.dispose();
		return bi;
	}
}