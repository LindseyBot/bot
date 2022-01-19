package net.lindseybot.shared.converters;

import org.json.JSONObject;

import javax.persistence.AttributeConverter;

public class JsonObjectStringConverter implements AttributeConverter<JSONObject, String> {

    @Override
    public String convertToDatabaseColumn(JSONObject attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.toString();
    }

    @Override
    public JSONObject convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return new JSONObject(dbData);
    }

}
