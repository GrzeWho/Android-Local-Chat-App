package com.kot.mova.utils;

import android.text.format.DateUtils;

import com.kot.mova.model.Coordinates;
import com.kot.mova.model.Message;
import com.kot.mova.model.ViewMessage;

public class UtilMethods {

    public static ViewMessage getViewMessage(Message message, Coordinates currentLocation) {
        ViewMessage viewMessage = new ViewMessage();
        viewMessage.setTime(DateUtils.getRelativeTimeSpanString(message.getTimestamp()).toString());
        viewMessage.setCoordinates(message.getCoordinates());
        viewMessage.setDistance(getDistanceString(distance(currentLocation.getX(), message.getCoordinates().getX(), currentLocation.getY(), message.getCoordinates().getY())));
        viewMessage.setId(message.getMessageId());
        viewMessage.setName("Grzegorz");
        viewMessage.setMessage(message.getMessage());
        return viewMessage;
    }

    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    public static String getDistanceString(double distance) {
        if (distance > 1) {
            String.format("%.2f", distance);
            return String.format("%.2f", distance) + "km away";
        } else if (distance < 0.002) {
            return "within arm's reach";
        } else {
            return (int)(distance*1000) + "m away";
        }
    }
}
