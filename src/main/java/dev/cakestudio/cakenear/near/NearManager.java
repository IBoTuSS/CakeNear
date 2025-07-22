package dev.cakestudio.cakenear.near;

import dev.cakestudio.cakenear.config.ConfigManager;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class NearManager {

    public static String getDirection(@NonNull Player source, @NonNull Player target) {
        FileConfiguration config = ConfigManager.getConfig();

        Location sourceLocation = source.getEyeLocation();
        Vector sourceDirection = sourceLocation.getDirection();
        Vector toTarget = target.getEyeLocation().toVector().subtract(sourceLocation.toVector());

        double relativeAngle = getRelativeAngle(sourceDirection, toTarget);

        if (relativeAngle > -22.5 && relativeAngle <= 22.5) {
            return config.getString("arrows.N");
        } else if (relativeAngle > 22.5 && relativeAngle <= 67.5) {
            return config.getString("arrows.NE");
        } else if (relativeAngle > 67.5 && relativeAngle <= 112.5) {
            return config.getString("arrows.E");
        } else if (relativeAngle > 112.5 && relativeAngle <= 157.5) {
            return config.getString("arrows.SE");
        } else if (relativeAngle > -67.5 && relativeAngle <= -22.5) {
            return config.getString("arrows.NW");
        } else if (relativeAngle > -112.5 && relativeAngle <= -67.5) {
            return config.getString("arrows.W");
        } else if (relativeAngle > -157.5 && relativeAngle <= -112.5) {
            return config.getString("arrows.SW");
        } else {
            return config.getString("arrows.S");
        }
    }

    private static double getRelativeAngle(@NonNull Vector sourceDirection, @NonNull Vector toTarget) {
        double sourceAngle = Math.atan2(sourceDirection.getZ(), sourceDirection.getX());
        double targetAngle = Math.atan2(toTarget.getZ(), toTarget.getX());

        double relativeAngle = Math.toDegrees(targetAngle - sourceAngle);

        if (relativeAngle > 180) {
            relativeAngle -= 360;
        }
        if (relativeAngle <= -180) {
            relativeAngle += 360;
        }
        return relativeAngle;
    }
}
