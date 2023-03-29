package data.maps;

import data.Location;
import jdk.jfr.Experimental;

import java.awt.*;
import java.awt.image.BufferedImage;

@Experimental
public class IsoGameMap extends GameMap {

    private int tileWidth, tileHeight;

    public IsoGameMap(int width, int height, int tileWidth, int tileHeight) {
        super(width, height, Math.max(tileWidth, tileHeight));
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    @Override
    public Location screenSpaceToMapSpace(Location screenLocation) {
        double imageHeight = (Math.max(getWidth(), getHeight()) * tileHeight);
        double xPos = 1.0 * (screenLocation.x + (getHeight() - screenLocation.y)) * tileWidth / 2.0;
        double yPos = (imageHeight + tileHeight / 2.0) - (screenLocation.x + screenLocation.y) * tileHeight / 2.0 - tileHeight / 2;
        return new Location((float) xPos, (float) yPos);
    }

    @Override
    public Location mapSpaceToScreenSpace(Location mapLocation) {
        return null;
    }

    @Override
    public BufferedImage generateStaticTileGrid() {
        int isoHeight = tileHeight;
        int isoWidth = tileWidth;

        int mapWidth = getWidth();
        int mapHeight = getHeight();

        int tileLength = Math.max(mapWidth, mapHeight);

        BufferedImage out = new BufferedImage( tileLength * isoWidth + 1, tileLength * isoHeight + 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) out.getGraphics();
        g2.setColor(Color.LIGHT_GRAY.brighter());
        g2.fillRect(0, 0, out.getWidth(), out.getHeight());


        Location corner1 = screenSpaceToMapSpace(new Location(0, 0));
        Location corner2 = screenSpaceToMapSpace(new Location(0, getHeight()));
        Location corner3 = screenSpaceToMapSpace(new Location(getWidth(), getHeight()));
        Location corner4 = screenSpaceToMapSpace(new Location(getHeight(), 0));
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillPolygon(
                new int[]{ (int) corner1.x, (int) corner2.x, (int) corner3.x, (int) corner4.x },
                new int[]{ (int) corner1.y, (int) corner2.y, (int) corner3.y, (int) corner4.y },
                4
        );

        g2.setColor(Color.GRAY);
        for(int xi = 0; xi <= mapWidth; xi++) {
            Location from = screenSpaceToMapSpace(new Location(xi, 0));
            Location to = screenSpaceToMapSpace(new Location(xi, mapHeight));

            g2.drawLine((int) from.x, (int) from.y, (int) to.x, (int) to.y);
        }

        for(int yi = 0; yi <= mapHeight; yi++) {
            Location from = screenSpaceToMapSpace(new Location(0, yi));
            Location to = screenSpaceToMapSpace(new Location(mapWidth, yi));

            g2.drawLine((int) from.x, (int) from.y, (int) to.x, (int) to.y);
        }

        return out;
    }

    @Override
    public GameMap clone() {
        GameMap out = new IsoGameMap(width, height, tileWidth, tileHeight);
        super.cloneLayersTo(out);
        return out;
    }
}
