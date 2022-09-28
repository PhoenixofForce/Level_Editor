import java.io.*;
import java.util.*;

/**
 * A class that you can copy into your projects to load .map files into custom data types
 * @version MapLoader.java: 1.0, map-Files: 1.0 - 1.1
 * @Author PhoenixofForce
 * @Author https://github.com/PhoenixofForce/Level_Editor
 */
public class MapLoader {

    public record Tag(String header, String body) { }

    public interface MapSettings {
        void setMapSettings(int tileSize, List<Tag> tags);
    }

    public interface TileLayer {
        void addTileLayer(Map<Integer, String> textureIds, Optional<String> name, float depth, int[][] map);
    }

    public interface FreeLayer {
        void addFreeLayer(Map<Integer, String> textureIds, Optional<String> name, float depth, int texture, float x, float y, List<Tag> tags);
    }

    public interface AreaLayer {
        void addAreaLayer(Optional<String> name, float x1, float y1, float x2, float y2, List<Tag> tags);
    }

    /**
     * Loads a .map file. Ensures flexibility through {MapSettings, TileLayer, FreeLayer, AreaLayer}-Interfaces
     */
    public static void loadMap(File f, MapSettings mapSettings, TileLayer tileLayer, FreeLayer freeLayer, AreaLayer areaLayer) {
        try {
            BufferedReader fileReader = new BufferedReader(new FileReader(f));
            String line = fileReader.readLine();

            int tileSize = 0;
            List<Tag> mapTags = new ArrayList<>();

            List<String> lineContents = splitLineAtSemicolon(line);
            tileSize = Integer.parseInt(lineContents.get(0));
            for(int i = 1; i < lineContents.size(); i++) {
                mapTags.add(stringToTag(lineContents.get(i)));
            }
            mapSettings.setMapSettings(tileSize, mapTags);

            line = fileReader.readLine();

            Map<Integer, String> textureIds = new HashMap<>();
            while(line.startsWith("#")) {
                int id = Integer.parseInt(line.split("-")[0].substring(1).trim());
                String textureName = line.substring(("#" + id + " - ").length());
                textureIds.put(id, textureName);

                line = fileReader.readLine();
            }

            while(line.startsWith("[layer")) {
                loadTileLayer(textureIds, line, tileLayer);
                line = fileReader.readLine();
            }

            while(line.startsWith("[put")) {
                loadFreeLayer(textureIds, line, freeLayer);
                line = fileReader.readLine();
            }

            while(line != null && line.startsWith("[area")) {
                loadAreaLayer(line, areaLayer);
                line = fileReader.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Extracts data from a string representing a tile layer, that has the form
     * [layer; (name); depth; width; height; ((N_0, ..., N_height)_0; ...; (N_0, ..., N_height)_width);]
     */
    private static void loadTileLayer(Map<Integer, String> textureIds, String line, TileLayer tileLayer) {
        List<String> lineContents = splitLineAtSemicolon(line.substring(1, line.length() - 1));

        boolean hasName = true;
        int nameOffset = 0;

        if(isFloat(lineContents.get(1))) {
            hasName = false;
            nameOffset = -1;
        }

        Optional<String> name = hasName? Optional.of(lineContents.get(1)): Optional.empty();
        float depth = Float.parseFloat(lineContents.get(2 + nameOffset));
        int width = Integer.parseInt(lineContents.get(3 + nameOffset));
        int height = Integer.parseInt(lineContents.get(4 + nameOffset));

        int[][] map = new int[width][height];
        for (int x = 0; x < width; x++) {
            String[] valuesLine = lineContents.get(x + 5 + nameOffset).split(",");
            for (int y = 0; y < height; y++) {
                int tile = Integer.parseInt(valuesLine[y].trim());
                map[x][y] = tile;
            }
        }

        tileLayer.addTileLayer(textureIds, name, depth, map);
    }

    /**
     * Extracts data from a string representing a free layer, that has the form
     * [put; (name); depth; textureId; x; y; (tags)]
     * where tags are
     * tag_1; tag_2; ...; tag_n
     * and a single tag
     * [tag; header; body]
     */
    private static void loadFreeLayer(Map<Integer, String> textureIds, String line, FreeLayer freeLayer) {
        List<String> lineContents = splitLineAtSemicolon(line.substring(1, line.length() - 1));

        boolean hasName = true;
        int nameOffset = 0;

        if(isFloat(lineContents.get(1))) {
            hasName = false;
            nameOffset = -1;
        }

        Optional<String> name = hasName? Optional.of(lineContents.get(1)): Optional.empty();
        float depth = Float.parseFloat(lineContents.get(2 + nameOffset));
        int textureId = Integer.parseInt(lineContents.get(3 + nameOffset));
        float x = Float.parseFloat(lineContents.get(4 + nameOffset));
        float y = Float.parseFloat(lineContents.get(5 + nameOffset));

        List<Tag> tags = new ArrayList<>();
        for(int i = 6 + nameOffset; i < lineContents.size(); i++) {
            tags.add(stringToTag(lineContents.get(i)));
        }

        freeLayer.addFreeLayer(textureIds, name, depth, textureId, x, y, tags);
    }

    /**
     * Extracts data from a string representing a area layer, that has the form
     * [area; (name); x1; y1; x2; y2; (tags)]
     * where tags are
     * tag_1; tag_2; ...; tag_n
     * and a single tag
     * [tag; header; body]
     */
    private static void loadAreaLayer(String line, AreaLayer areaLayer) {
        List<String> lineContents = splitLineAtSemicolon(line.substring(1, line.length() - 1));

        boolean hasName = true;
        int nameOffset = 0;

        if(isFloat(lineContents.get(1))) {
            hasName = false;
            nameOffset = -1;
        }

        Optional<String> name = hasName? Optional.of(lineContents.get(1)): Optional.empty();
        float x1 = Float.parseFloat(lineContents.get(2 + nameOffset));
        float y1 = Float.parseFloat(lineContents.get(3 + nameOffset));
        float x2 = Float.parseFloat(lineContents.get(4 + nameOffset));
        float y2 = Float.parseFloat(lineContents.get(5 + nameOffset));

        List<Tag> tags = new ArrayList<>();
        for(int i = 6 + nameOffset; i < lineContents.size(); i++) {
            tags.add(stringToTag(lineContents.get(i)));
        }

        areaLayer.addAreaLayer(name, x1, y1, x2, y2, tags);
    }

    /**
     * Splits a String at semikolons that are not enclosed by brackets(`[]`)
     *
     * Arg1;Arg2;[tag; tagHeader; tagBody]
     * =>
     * [Arg1, Arg1, [tag; tagHeader; tagBody]]
     *
     * NOTE: uneven amounts of brackets[] (ex "[[test]; foo", "]]]]]; bar;") will lead to unexpected behavior
     */
    private static List<String> splitLineAtSemicolon(String line) {
        List<String> out = new ArrayList<>();

        StringBuilder currentString = new StringBuilder();
        int escapeLevel = 0;
        for(char c: line.toCharArray()) {
            if(c == '[') {
                escapeLevel++;
            } else if(c == ']') {
                escapeLevel--;
            } else if (c == ';' && escapeLevel == 0) {
                out.add(currentString.toString().trim());
                currentString = new StringBuilder();
                continue;
            }

            currentString.append(c);
        }
        if(currentString.length() > 0) out.add(currentString.toString().trim());
        return out;
    }

    /**
     * Splits a tag-String "[tag; header; body]" into "header" and "body"
     * and returns the resulting tag
     *
     * NOTE: Tags cannot contain ';'
     */
    private static Tag stringToTag(String line) {
        String[] lineContents = line.split(";");
        String tagHeader = lineContents[1].trim();
        String tagBody = lineContents[2].trim();
        return new Tag(tagHeader, tagBody);
    }

    private static boolean isFloat(String s) {
        try {
            Float.parseFloat(s);
            return true;
        } catch(Exception ignored) { }
        return false;
    }
}
