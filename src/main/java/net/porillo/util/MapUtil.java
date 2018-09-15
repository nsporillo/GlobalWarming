package net.porillo.util;

import java.util.Map;
import java.util.TreeMap;

public class MapUtil {

    public static <Key extends Number, Value extends Number> Value searchTreeMap(TreeMap<Key, Value> map, Key target) {
        Map.Entry<Key, Value> ceil = map.ceilingEntry(target);
        Map.Entry<Key, Value> floor = map.floorEntry(target);
        Value result = null;
        if (ceil != null && floor != null) {
            result = Math.abs(target.doubleValue() - floor.getKey().doubleValue()) < Math.abs(target.doubleValue() - ceil.getKey().doubleValue()) ? floor.getValue() : ceil.getValue();
        } else if (ceil != null || floor != null){
            result = floor != null ? floor.getValue() : ceil.getValue();
        }

        return result;
    }


}
