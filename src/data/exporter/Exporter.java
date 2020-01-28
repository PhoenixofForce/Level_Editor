package data.exporter;

import java.io.File;

import javax.swing.filechooser.FileNameExtensionFilter;

import data.GameMap;
import data.layer.AreaLayer;
import data.layer.FreeLayer;
import data.layer.TileLayer;
import data.layer.layerobjects.Area;
import data.layer.layerobjects.GO;
import data.layer.layerobjects.Tag;

public interface Exporter {

	boolean exportToFile(GameMap map, File file);

	String export(GameMap map);

	String export(TileLayer tileLayer, Object... o2);
	String export(AreaLayer areaLayer, Object... o2);
	String export(FreeLayer freeLayer, Object... o2);

	String export(Tag tag, Object... o2);
	String export(Area area, Object... o2);
	String export(GO go, Object... o2);

	FileNameExtensionFilter getFileFilter();

	interface Exportable {
		String accept(Exporter exporter, Object... o2);
	}
}
