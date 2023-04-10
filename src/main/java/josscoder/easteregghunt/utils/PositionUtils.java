package josscoder.easteregghunt.utils;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;

public class PositionUtils {

    public static String positionToString(Position location) {
        return String.format("%s:%s:%s:%s", location.getX(), location.getY(), location.getZ(), location.getLevel().getFolderName());
    }

    public static Position stringToPosition(String string) {
        String[] split = string.split(":");

        if (split.length < 4) {
            throw new RuntimeException("Could not load position because need 4 params (x:y:z:world)");
        }

        double x = Double.parseDouble(split[0]);
        double y = Double.parseDouble(split[1]);
        double z = Double.parseDouble(split[2]);

        Level level = Server.getInstance().getLevelByName(split[3]);

        return new Position(x, y, z, level);
    }
}
