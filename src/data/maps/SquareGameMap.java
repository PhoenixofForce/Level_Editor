package data.maps;

import data.Location;
import data.layer.AreaLayer;
import data.layer.FreeLayer;
import data.layer.Layer;
import data.layer.TileLayer;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SquareGameMap extends GameMap {

    public SquareGameMap(int width, int height, int tileSize) {
        super(width, height, tileSize);
    }

    @Override
    public Location screenSpaceToMapSpace(Location screenLocation) {
        return screenLocation;
    }

    @Override
    public Location mapSpaceToScreenSpace(Location mapLocation) {
        return mapLocation;
    }

    @Override
    public BufferedImage generateStaticTileGrid() {
        BufferedImage out = new BufferedImage( getWidth() * getTileSize(), getHeight() * getTileSize(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) out.getGraphics();

        g2.setColor(Color.LIGHT_GRAY);
        g2.fillRect(0, 0, getWidth() * getTileSize(), getHeight() * getTileSize());

        //prepares graphics object to draw tile separators
        g2.setColor(Color.LIGHT_GRAY.darker());
        g2.setStroke(new BasicStroke(1));

        //draw tile separators
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                g2.drawLine(x * getTileSize(), y * getTileSize(), getWidth() * getTileSize(), y * getTileSize());
                g2.drawLine(x * getTileSize(), y * getTileSize(), x * getTileSize(), getHeight() * getTileSize());
            }
        }

        return out;
    }

    @Override
    public GameMap clone() {
        GameMap out = new SquareGameMap(width, height, tileSize);
        super.cloneLayersTo(out);
        return out;
    }
}
