package data.io.importer;

import java.io.File;
import java.util.List;

import javax.swing.filechooser.FileNameExtensionFilter;

import data.GameMap;
import data.layer.AreaLayer;
import data.layer.FreeLayer;
import data.layer.TileLayer;
import data.layer.layerobjects.Tag;
import window.Window;

public interface Importer {

	GameMap importMap(Window w, File input, boolean isNewMap);
	
	TileLayer importTileLayer(String input, Object... o2);
	AreaLayer importAreaLayer(String input, Object... o2);
	FreeLayer importFreeLayer(String input, Object... o2);
	
	void handleImports(String input, Object... o2);
	List<Tag> handleTags(String line);
	
	FileNameExtensionFilter getFileFilter();
}
