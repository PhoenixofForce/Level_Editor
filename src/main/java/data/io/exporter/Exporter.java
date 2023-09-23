package data.io.exporter;

import java.io.File;

import javax.swing.filechooser.FileNameExtensionFilter;

import data.GameMap;
import data.layer.AreaLayer;
import data.layer.FreeLayer;
import data.layer.TileLayer;
import data.layer.layerobjects.Area;
import data.layer.layerobjects.GameObject;
import data.layer.layerobjects.Tag;

public interface Exporter<T> {

	boolean exportToFile(GameMap map, File file);

	T export(GameMap map);

	T export(TileLayer tileLayer, ExporterData data);
	T export(AreaLayer areaLayer, ExporterData data);
	T export(FreeLayer freeLayer, ExporterData data);

	T export(Tag tag, ExporterData data);
	T export(Area area, ExporterData data);
	T export(GameObject gameObject, ExporterData data, float depth);

	T append(T o1, T o2);

	FileNameExtensionFilter getFileFilter();

	void setOptions(boolean tileWithName, boolean freeWithName, boolean areaWithName);

	interface Exportable {
		Object accept(Exporter exporter, ExporterData data);
	}
}
