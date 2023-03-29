package data.maps;

import data.Location;

import java.awt.*;
import java.awt.image.BufferedImage;

public class SquareGameMap extends GameMap {

    public SquareGameMap(int width, int height, int tileSize) {
        super(width, height, tileSize);
    }

    @Override
    public Location worldToMapSpace(Location worldLocation) {
        return new Location(worldLocation.x / getTileWidth(), worldLocation.y / getTileWidth()); // divide by tileSize
    }

    @Override
    public Location mapToWorldSpace(Location mapLocation) {
        return new Location(mapLocation.x * getTileWidth(), mapLocation.y * getTileWidth());  // multiply by tilSize
    }

    @Override
    public BufferedImage generateStaticTileGrid() {
        BufferedImage out = new BufferedImage( getWidth() * getTileWidth(), getHeight() * getTileWidth(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) out.getGraphics();

        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(0, 0, getWidth() * getTileWidth(), getHeight() * getTileWidth());

        //prepares graphics object to draw tile separators
        g2.setColor(Color.LIGHT_GRAY.darker());
        g2.setStroke(new BasicStroke(1));

        //draw tile separators
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                g2.drawLine(x * getTileWidth(), y * getTileWidth(), getWidth() * getTileWidth(), y * getTileWidth());
                g2.drawLine(x * getTileWidth(), y * getTileWidth(), x * getTileWidth(), getHeight() * getTileWidth());
            }
        }

        return out;
    }

    @Override
    public GameMap clone() {
        GameMap out = new SquareGameMap(width, height, tileWidth);
        super.cloneLayersTo(out);
        return out;
    }
}
