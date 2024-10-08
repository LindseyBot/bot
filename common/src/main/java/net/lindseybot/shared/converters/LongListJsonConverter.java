package net.lindseybot.shared.converters;

import jakarta.persistence.AttributeConverter;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class LongListJsonConverter implements AttributeConverter<List<Long>, String> {

    @Override
    public String convertToDatabaseColumn(List<Long> attribute) {
        if (attribute == null) {
            return null;
        }
        JSONArray array = new JSONArray();
        attribute.forEach(array::put);
        return array.toString();
    }

    @Override
    public List<Long> convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return new ArrayList<>();
        }
        List<Long> items = new ArrayList<>();
        JSONArray array = new JSONArray(dbData);
        for (int i = 0; i < array.length(); i++) {
            items.add(array.getLong(i));
        }
        return items;
    }

}
