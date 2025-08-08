package dev.cakestudio.cakenear.manager;

import dev.cakestudio.cakenear.service.SettingsManager;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class NearManager {

    public static String getDirection(@NonNull Player source, @NonNull Player target, @NonNull SettingsManager settings) {
        Location sourceLocation = source.getEyeLocation();
        Vector sourceDirection = sourceLocation.getDirection();
        Vector toTarget = target.getEyeLocation().toVector().subtract(sourceLocation.toVector());

        double relativeAngle = getRelativeAngle(sourceDirection, toTarget);

        if (relativeAngle > -22.5 && relativeAngle <= 22.5) {
            return settings.getArrow("N");
        } else if (relativeAngle > 22.5 && relativeAngle <= 67.5) {
            return settings.getArrow("NE");
        } else if (relativeAngle > 67.5 && relativeAngle <= 112.5) {
            return settings.getArrow("E");
        } else if (relativeAngle > 112.5 && relativeAngle <= 157.5) {
            return settings.getArrow("SE");
        } else if (relativeAngle > -67.5 && relativeAngle <= -22.5) {
            return settings.getArrow("NW");
        } else if (relativeAngle > -112.5 && relativeAngle <= -67.5) {
            return settings.getArrow("W");
        } else if (relativeAngle > -157.5 && relativeAngle <= -112.5) {
            return settings.getArrow("SW");
        } else {
            return settings.getArrow("S");
        }
    }

    private static double getRelativeAngle(@NonNull Vector sourceDirection, @NonNull Vector toTarget) {
        double sourceAngle = Math.atan2(sourceDirection.getZ(), sourceDirection.getX());
        double targetAngle = Math.atan2(toTarget.getZ(), toTarget.getX());
        double relativeAngle = Math.toDegrees(targetAngle - sourceAngle);

        if (relativeAngle > 180) relativeAngle -= 360;
        if (relativeAngle <= -180) relativeAngle += 360;

        return relativeAngle;
    }
}