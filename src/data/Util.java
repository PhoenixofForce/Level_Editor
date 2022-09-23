package data;

public class Util {

	public static boolean textureEquals(int autoTile, String texture1, String texture2) {
		if(texture1 != null && texture2 != null && (autoTile == 0 || !(texture1.split("_")[1].equals("block") && texture2.split("_")[1].equals("block")))) {
			return texture1.equals(texture2);
		}

		else if(autoTile > 0) {
			if(texture1 != null && texture2 != null) {
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
			}
		}

		return false;
	}
}
