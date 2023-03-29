package data.io.importer;

import java.io.File;

import javax.swing.filechooser.FileNameExtensionFilter;

import data.maps.GameMap;
import window.Window;

public interface Importer {

	GameMap importMap(Window w, File input, boolean isNewMap);

	/*
	TileLayer importTileLayer(String input, Object... o2);
	AreaLayer importAreaLayer(String input, Object... o2);
	FreeLayer importFreeLayer(String input, Object... o2);
	
	void handleImports(String input, Object... o2);
	List<Tag> handleTags(String line);
	*/
	
	FileNameExtensionFilter getFileFilter();
}
