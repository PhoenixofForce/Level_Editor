package data;

import util.Util;
import window.Window;

public class AutoTiling {

    private static final int[] AUTOTILE_ID_TO_INDEX = new int[] {
            0,  16,   24,   8,
            64, 208,  248, 104,
            66, 214,  255, 107,
            2,  22,   31,  11,
            75,  80,   88,  72,
            106,  82,   90,  74,
            86,  18,   26,  10,
            210, 218,  250, 122,
            219, 222,   -1, 123,
            126,  94,   95,  91,
            120, 216,  127, 223,
            27,  30,  251, 254
    };

    /*
     * The autotiling works by assigning each surrounding tile an id, so that each id is a multiple of 2
     * (bc they have one 1 in binary)
     *
     * ( 1 )( 2 )( 4 )
     * ( 8 )( x )( 16)
     * ( 32)( 64)(128)
     *
     * If a tile is present the output gets xored(the corresponding bit gets activated).
     * In Autotiling mode 1 just the 4 directly adjacent are considered, in mode 2 also the adjacent corners(if the edges next to the corner are present)
     *
     * Also, because the resulting number is not obvious they get mapped to a different labeling scheme, so the tiles can fit in my preferred layout
     *
     */
    public static int calcAutoTileIndex(String[][] tileNames, int x, int y, String name, int width, int height) {
        int autotileMode = Window.INSTANCE.getAutoTile();

        int out = 0;
        if(autotileMode == 1) {
            if (y != 0 && tileNames[y - 1][x] != null && Util.textureEquals(autotileMode, tileNames[y - 1][x], name))
                out ^= 12;
            if (y != height - 1 && tileNames[y + 1][x] != null && Util.textureEquals(autotileMode, tileNames[y + 1][x], name))
                out ^= 4;
            if (x != 0 && tileNames[y][x - 1] != null && Util.textureEquals(autotileMode, tileNames[y][x - 1], name))
                out ^= 3;
            if (x != width - 1 && tileNames[y][x + 1] != null && Util.textureEquals(autotileMode, tileNames[y][x + 1], name))
                out ^= 1;
        } else if(autotileMode == 2) {
            if (y != 0 && tileNames[y - 1][x] != null && Util.textureEquals(autotileMode, tileNames[y - 1][x], name))
                out ^= 2;
            if (y != height - 1 && tileNames[y + 1][x] != null && Util.textureEquals(autotileMode, tileNames[y + 1][x], name))
                out ^= 64;
            if (x != 0 && tileNames[y][x - 1] != null && Util.textureEquals(autotileMode, tileNames[y][x - 1], name))
                out ^= 8;
            if (x != width - 1 && tileNames[y][x + 1] != null && Util.textureEquals(autotileMode, tileNames[y][x + 1], name))
                out ^= 16;

            boolean w = (out&8)!=0,
                    n = (out&2)!=0,
                    e = (out&16)!=0,
                    s = (out&64)!=0;

			/* Check corners only if adjacent edges are present
				1#.   Corner 1 gets checked
				##.
				2..   Corner 2 does not
			 */
            if (x!= 0 && y != 0 && tileNames[y - 1][x-1] != null && Util.textureEquals(autotileMode, tileNames[y - 1][x - 1], name))
                out ^= w && n? 1: 0;
            if (x!= width-1 && y != 0 && tileNames[y - 1][x+1] != null && Util.textureEquals(autotileMode, tileNames[y - 1][x + 1], name))
                out ^= e && n? 4: 0;

            if (x!= 0 && y != height-1 && tileNames[y + 1][x-1] != null && Util.textureEquals(autotileMode, tileNames[y + 1][x - 1], name))
                out ^= w && s? 32: 0;
            if (x!= width-1 && y != height-1 && tileNames[y + 1][x+1] != null && Util.textureEquals(autotileMode, tileNames[y + 1][x + 1], name))
                out ^= e && s? 128: 0;

            out = Util.arrayIndexOf(AUTOTILE_ID_TO_INDEX, out);
        }

        return out;
    }

}
