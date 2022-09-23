package util;

public class Util {

	public static boolean textureEquals(int autoTile, String texture1, String texture2) {
		if(texture1 == null && texture2 == null) return true;
		if(texture1 == null || texture2 == null) return false;

		if((autoTile == 0 || !(texture1.split("_")[1].equals("block") && texture2.split("_")[1].equals("block")))) {
			return texture1.equals(texture2);
		}

		String[] parts1 = texture2.split("_");
		String[] parts2 = texture2.split("_");

		if(parts1[1].equals("block") && parts2[1].equals("block") && parts1.length == parts2.length) {
			for(int i = 2; i < parts1.length-1; i++) {
				if (!parts1[i].equals(parts2[i])) {
					return false;
				}
			}

			return true;
		}

		return false;
	}

	public static int arrayIndexOf(int[] a, int f) {
		for(int i = 0; i < a.length; i++) {
			if(a[i] == f) return i;
		}
		return -1;
	}

}
