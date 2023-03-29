package data.maps;

import data.Location;
import jdk.jfr.Experimental;

import java.awt.*;
import java.awt.image.BufferedImage;

@Experimental
public class IsoGameMap extends GameMap {

    private int tileWidth, tileHeight;

    public IsoGameMap(int width, int height, int tileWidth, int tileHeight) {
        super(width, height, tileWidth, tileHeight);
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    public Location getDrawingOffset() {
        return new Location(-tileWidth / 2.0f, -tileHeight);
    }

    @Override
    public Location worldToMapSpace(Location mapLocation) {
        double imageHeight = (Math.max(getWidth(), getHeight()) * tileHeight);

        //reversed calculation from mapSpaceToWorldSpace
        double worldX = 0.5 * ((2.0 / tileWidth) * mapLocation.x - getHeight() - (2.0 / tileHeight) * (mapLocation.y - imageHeight));
        double worldY = -((2.0 / tileHeight) * (mapLocation.y - imageHeight) + worldX);

        return new Location((float) worldX * 1, (float) worldY * 1);
    }

    @Override
    public Location mapToWorldSpace(Location inWorldLocation) {
        Location worldLocation = new Location(inWorldLocation.x / 1, inWorldLocation.y / 1);

        double imageHeight = (Math.max(getWidth(), getHeight()) * tileHeight);
        double inverseWorldY = (getHeight() - worldLocation.y);

        double mapX = (worldLocation.x + inverseWorldY) * tileWidth / 2.0;
        double mapY = imageHeight - (worldLocation.x + worldLocation.y) * tileHeight / 2.0;

        return new Location((float) mapX, (float) mapY);
    }

    @Override
    public BufferedImage generateStaticTileGrid() {
        int isoHeight = tileHeight;
        int isoWidth = tileWidth;

        int mapWidth = getWidth();
        int mapHeight = getHeight();

        int tileLength = Math.max(mapWidth, mapHeight);

        BufferedImage out = new BufferedImage( tileLength * isoWidth, tileLength * isoHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) out.getGraphics();
        g2.setColor(Color.LIGHT_GRAY.brighter());
        //g2.fillRect(0, 0, out.getWidth(), out.getHeight());

        Location corner1 = mapToWorldSpace(new Location(0, 0));
        Location corner2 = mapToWorldSpace(new Location(0, getHeight()));
        Location corner3 = mapToWorldSpace(new Location(getWidth(), getHeight()));
        Location corner4 = mapToWorldSpace(new Location(getWidth(), 0));
        g2.setColor(Color.LIGHT_GRAY);
        g2.fillPolygon(
                new int[]{ (int) corner1.x, (int) corner2.x, (int) corner3.x, (int) corner4.x },
                new int[]{ (int) corner1.y, (int) corner2.y, (int) corner3.y, (int) corner4.y },
                4
        );

        g2.setColor(Color.BLACK);
        //g2.fillOval((int) corner1.x - 10, (int) corner1.y - 10, 20, 20);

        g2.setColor(Color.GRAY);
        for(int xi = 1; xi < mapWidth; xi++) {
            Location from = mapToWorldSpace(new Location(xi, 0));
            Location to = mapToWorldSpace(new Location(xi, mapHeight));

            g2.drawLine((int) from.x, (int) from.y, (int) to.x, (int) to.y);
        }

        for(int yi = 1; yi < mapHeight; yi++) {
            Location from = mapToWorldSpace(new Location(0, yi));
            Location to = mapToWorldSpace(new Location(mapWidth, yi));

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
