package data.io.exporter;

import data.GameMap;
import data.layer.AreaLayer;
import data.layer.FreeLayer;
import data.layer.TileLayer;
import data.layer.layerobjects.Area;
import data.layer.layerobjects.GameObject;
import data.layer.layerobjects.Tag;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PngExporter implements Exporter<String> {

	private static final PngExporter INSTANCE = new PngExporter();

	private FileNameExtensionFilter fileFilter;
	private BufferedImage out;
	private PngExporter() {
		this.fileFilter = new FileNameExtensionFilter(".png", "png");
	}
	
	@Override
	public boolean exportToFile(GameMap map, File file) {
		try {
			int[] bounds = map.getBounds();
			out = new BufferedImage(map.getWidth()*map.getTileSize(), map.getHeight()*map.getTileSize(), BufferedImage.TYPE_INT_ARGB);
			export(map);
			
			ImageIO.write(out.getSubimage(bounds[0]*map.getTileSize(), bounds[1]*map.getTileSize(), (1+bounds[2]-bounds[0])*map.getTileSize(), (1+bounds[3]-bounds[1])*map.getTileSize()), "PNG", file);
		} catch (IOException e1) {}
		
		return false;
	}

	@Override
	public String export(GameMap map) {
		map.getLayers().values().stream()
		.sorted((o1, o2) -> Float.compare(o2.depth(), o1.depth()))
		.forEach(l -> l.draw(out.getGraphics(), null, null));
		
		return "";
	}

	@Override
	public String export(TileLayer tileLayer, Object... o2) {
		return "";
	}

	@Override
	public String export(AreaLayer areaLayer, Object... o2) {
		return "";
	}

	@Override
	public String export(FreeLayer freeLayer, Object... o2) {
		return "";
	}

	@Override
	public String export(Tag tag, Object... o2) {
		return "";
	}

	@Override
	public String export(Area area, Object... o2) {
		return "";
	}

	@Override
	public String export(GameObject gameObject, Object... o2) {
		return "";
	}

	@Override
	public String append(String o1, String o2) {
		return o1 + o2;
	}

	@Override
	public FileNameExtensionFilter getFileFilter() {
		return fileFilter;
	}

	@Override
	public void setOptions(boolean tileWithName, boolean freeWithName, boolean areaWithName) { }

	public static PngExporter getInstance() {
		return INSTANCE;
	}
}
