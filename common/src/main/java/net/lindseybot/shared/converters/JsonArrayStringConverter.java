package net.lindseybot.shared.converters;

import org.json.JSONArray;

import javax.persistence.AttributeConverter;

public class JsonArrayStringConverter implements AttributeConverter<JSONArray, String> {

    @Override
    public String convertToDatabaseColumn(JSONArray attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.toString();
    }

    @Override
    public JSONArray convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return new JSONArray(dbData);
    }

}
