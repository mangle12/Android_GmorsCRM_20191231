package tw.com.masterhand.gmorscrm.room.converter;

import android.arch.persistence.room.TypeConverter;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import tw.com.masterhand.gmorscrm.tools.Logger;
import tw.com.masterhand.gmorscrm.tools.TimeFormater;

public class DateConverter {
    @TypeConverter
    public static Date fromString(String value) {
//        Logger.e("wayne","DateConverter fromString:"+value);
        try {
            SimpleDateFormat DATABASE_DATE_TIME_FORMATER = new SimpleDateFormat(
                    TimeFormater.DATABASE_DATE_TIME_STRING, Locale.getDefault());
            Date date=TextUtils.isEmpty(value) ? null : DATABASE_DATE_TIME_FORMATER.parse(value);
            return date;
        } catch (Exception e) {
            Logger.e(DateConverter.class.getSimpleName(), "fromString Exception source:" + value);
            Logger.e(DateConverter.class.getSimpleName(), "fromString Exception:" + e.getMessage());
            return null;
        }
    }

    @TypeConverter
    public static String dateToString(Date date) {
        try {
            if (date == null)
                return null;
            SimpleDateFormat DATABASE_DATE_TIME_FORMATER = new SimpleDateFormat(
                    TimeFormater.DATABASE_DATE_TIME_STRING, Locale.getDefault());
            return DATABASE_DATE_TIME_FORMATER.format(date);
        } catch (Exception e) {
            Logger.e(DateConverter.class.getSimpleName(), "dateToString Exception:" + e
                    .getMessage());
            return null;
        }
    }
}