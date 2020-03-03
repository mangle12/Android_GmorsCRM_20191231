package tw.com.masterhand.gmorscrm.tools;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class GsonGMTDateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

    public GsonGMTDateAdapter() {
    }

    @Override
    public synchronized JsonElement serialize(Date date, Type type, JsonSerializationContext
            jsonSerializationContext) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(TimeFormater.DATABASE_DATE_TIME_STRING, Locale
                .getDefault());
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return new JsonPrimitive(dateFormat.format(date));
    }

    @Override
    public synchronized Date deserialize(JsonElement jsonElement, Type type,
                                         JsonDeserializationContext jsonDeserializationContext) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(TimeFormater.DATABASE_DATE_TIME_STRING, Locale
                    .getDefault());
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            return dateFormat.parse(jsonElement.getAsString());
        } catch (ParseException e) {
            throw new JsonParseException(e);
        }
    }
}