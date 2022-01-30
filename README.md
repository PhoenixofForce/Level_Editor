## Level Editor
It started as a school project but continued to be value for making levels at game jams, so it contintued to get updates. It allows you to import textures and create Maps with different types of layers:
* *TileLayer*, on which you can place along a grid of fixed size. These kind of layer are used to build the fore- and background of the map
* *FreeLayer*, on which textures can be placed freely on every pixel. These layer can be used for decoration or the placing of enemies
* *AreaLayer*, here no textures are used. Instead you can define certain areas, which we use for the camera zones but could also used for collision boxes

### Importing textures
To import and load textures we use a file format which we call text-File(short for texture). Text-files define single textures in a texturemap(or texture atlas). Lets suppose you have an texture map `map.png`, then you need the corresponding text-File `map.text` in the same folder.
The first line in a text-file gives information about how many textures are loaded. And each following line has the format of `name x y width height`.  The name cannot contain white spaces and each value has to be separated by a white space. 
An example text-files could look like this:
```
4
texture_1 0 0 8 8
texture_2 0 8 8 8
texture_3 8 0 8 8
texture_4 8 8 8 8
```
The current version of the filetype does not support comments, blank lines.

### Saving Maps
Maps are saved as `umap`-Files(unsaved Map files), on which i wont go into detail here.  But when opening an old file, make sure that all the used `png` and `text` files are still in the same position, otherwise the map wont load.

### Exporting Maps
When exporting a map it automatically gets trimmed to fit the scene. 

Because of this and other changes we use `map`-files instead of the  just mentioned `umap`-files.
Here the first line represent the tile size. For each used texture follows a line `#i - texture_name` which is used to replace an texture with an index `i`(starting with 1 since 0 means no texture).
After that the layers get exported, first the TileLayer, after that FreeLayer and then AreaLayers. 
TileLayers use 

`[layer; z-Coord; width; height; t(0, 0), ..., t(0, x); t(1, 0), ..., t(1,y); ...; t(x,1), ... t(x, y)]` 
where the z-Coordinate represents the distance from the camera. And `t(m, n)` stands for the the corresponding texture index.
FreeLayer use `[put; z-Coord; texture index; x; y]`.
These layers can also use tags(description pending) which look like `[put; z; texture index; x; y; [tag; tag name; tag value]]`.
Lastly the AreaLayer are saved as `[area; x1, y1; x2; y2]`, they can also use tags.








