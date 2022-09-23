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

public interface Exporter<T> {

	boolean exportToFile(GameMap map, File file);

	T export(GameMap map);

	T export(TileLayer tileLayer, Object... o2);
	T export(AreaLayer areaLayer, Object... o2);
	T export(FreeLayer freeLayer, Object... o2);

	T export(Tag tag, Object... o2);
	T export(Area area, Object... o2);
	T export(GO go, Object... o2);

	T append(T o1, T o2);

	FileNameExtensionFilter getFileFilter();

	void setOptions(boolean tileWithName, boolean freeWithName, boolean areaWithName);

	interface Exportable {
		Object accept(Exporter exporter, Object... o2);
	}
}
