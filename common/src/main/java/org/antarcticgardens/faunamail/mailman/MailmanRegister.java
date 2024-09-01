package org.antarcticgardens.faunamail.mailman;

import net.minecraft.resources.ResourceLocation;
import org.antarcticgardens.faunamail.mailman.displays.ItemDisplayDisplay;
import org.antarcticgardens.faunamail.mailman.displays.MailItemDisplay;
import org.antarcticgardens.faunamail.mailman.movement.FlyUpDownMovement;
import org.antarcticgardens.faunamail.mailman.movement.Movement;

import java.util.HashMap;
import java.util.Map;

public class MailmanRegister {

    public static Map<ResourceLocation, Movement.movementCreator> movements = new HashMap<>() {
        {
            put(ResourceLocation.parse("fauna_mail:fly_up_down"), FlyUpDownMovement::creator);
        }
    };

    public static Map<ResourceLocation, MailItemDisplay.displayCreator> displays = new HashMap<>() {
        {
            put(ResourceLocation.parse("fauna_mail:item_display"), ItemDisplayDisplay::creator);
        }
    };

    public static Map<ResourceLocation, Mailman> mailmanMap = new HashMap<>();

    public static void registerMailman(ResourceLocation id, Mailman mailman) {
        mailmanMap.put(id, mailman);
    }

    public static void parse(Map<String, Object> json) {
        if (!json.containsKey("entity_id")) {
            throw new IllegalArgumentException("Missing required property 'entity_id'");
        }
        ResourceLocation location = ResourceLocation.tryParse((String) json.get("entity_id"));
        if (location == null) {
            throw new IllegalArgumentException("Invalid property 'entity_id'");
        }

        float speed_bpt = getFloat(json, "speed_bpt", 0.4f);

        float max_distance = getFloat(json, "max_distance", 1000f);

        Returns returns = Returns.YES;
        if (json.get("returns") != null) {
            switch ((String) json.get("returns")) {
                case "NO" -> returns = Returns.NO;
                case "YES" -> {}
                case "IF_TAMED" -> returns = Returns.IF_TAMED;
                default -> throw new IllegalArgumentException("Invalid property 'returns'");
            }
        }

        float return_journey_min_speed_multiplier = getFloat(json, "return_journey_min_speed_multiplier", 0.8f);

        float return_journey_max_speed_multiplier = getFloat(json, "return_journey_max_speed_multiplier", 1.2f);

        int dimensional_travel_cost = getInteger(json, "dimensional_travel_cost", -1);
        boolean can_locate_player = getBoolean(json, "can_locate_player", false);
        boolean can_locate_mailbox = getBoolean(json, "can_locate_mailbox", true);

        Movement movement;
        if (json.get("movement") instanceof Map) {
            Map<String, Object> mov = (Map<String, Object>) json.get("movement");
            String name = mov.get("name").toString();
            Movement.movementCreator creator = movements.get(ResourceLocation.tryParse(name));
            if (creator == null) {
                throw new IllegalArgumentException("Invalid property 'movement'");
            }
            movement = creator.create(mov);
        } else {
            throw new IllegalArgumentException("Invalid property 'movement'");
        }

        MailItemDisplay display;
        if (json.get("display") instanceof Map) {
            Map<String, Object> displayMap = (Map<String, Object>) json.get("display");
            String name = displayMap.get("name").toString();
            MailItemDisplay.displayCreator creator = displays.get(ResourceLocation.tryParse(name));
            if (creator == null) {
                throw new IllegalArgumentException("Invalid property 'display'");
            }
            display = creator.create(displayMap);
        } else {
            throw new IllegalArgumentException("Invalid property 'display'");
        }

        registerMailman(
                location,
                new Mailman(location, speed_bpt, max_distance, returns, return_journey_min_speed_multiplier, return_journey_max_speed_multiplier, dimensional_travel_cost, can_locate_player, can_locate_mailbox, movement, display)
        );

    }

    public static int getInteger(Map<String, Object> json, String key, int defaultValue) {
        if (json.containsKey(key)) {
            if (json.get(key) instanceof Double) {
                return (int)(double)(Double) json.get(key);
            } else {
                throw new IllegalArgumentException("Invalid property '"+key+"' is not an integer");
            }
        }
        return defaultValue;
    }

    public static float getFloat(Map<String, Object> json, String key, float defaultValue) {
        if (json.containsKey(key)) {
            if (json.get(key) instanceof Double) {
                return (float)(double)(Double) json.get(key);
            } else {
                throw new IllegalArgumentException("Invalid property '"+key+"' is not a float");
            }
        }
        return defaultValue;
    }

    public static boolean getBoolean(Map<String, Object> json, String key, boolean defaultValue) {
        if (json.containsKey(key)) {
            if (json.get(key) instanceof Boolean) {
                return (Boolean) json.get(key);
            } else {
                throw new IllegalArgumentException("Invalid property '"+key+"' is not a boolean");
            }
        }
        return defaultValue;
    }

}
