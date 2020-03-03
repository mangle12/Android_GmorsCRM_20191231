package tw.com.masterhand.gmorscrm.model;

import android.text.TextUtils;

import com.google.gson.Gson;

import tw.com.masterhand.gmorscrm.room.setting.ConfigCity;
import tw.com.masterhand.gmorscrm.room.setting.ConfigCountry;

public class Address {
    Country country;// 國家
    City city;// 城市
    String address;// 地址

    public Country getCountry() {
        return country;
    }

    public void setCountry(ConfigCountry configCountry) {
        if (configCountry != null) {
            country = new Country();
            country.setId(configCountry.getId());
            country.setName(configCountry.getName());
        } else {
            country = null;
        }
    }

    public City getCity() {
        return city;
    }

    public void setCity(ConfigCity configCity) {
        if (configCity != null) {
            city = new City();
            city.setId(configCity.getId());
            city.setCountryId(configCity.getConfig_country_id());
            city.setName(configCity.getName());
        } else {
            city = null;
        }
    }

    public String getAddress() {
        if (!TextUtils.isEmpty(address)) {
            String show = "";
            try {
                Address temp = new Gson().fromJson(address, Address.class);
                show += temp.getAddress();
            } catch (Exception e) {
                show += address;
            }
            return show;
        } else {
            return "";
        }
    }

    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * 取得顯示地址
     */
    public String getShowAddress() {
        StringBuilder show = new StringBuilder();
        if (country != null) {
            show.append(country.getName());
        }
        if (city != null) {
            show.append(" ").append(city.getName());
        }
        if (!TextUtils.isEmpty(show.toString()))
            show.append(" ");
        show.append(getAddress());
        return show.toString();
    }

    /**
     * 取得區域(國家+城市)
     */
    public String getArea() {
        String show = "";
        if (country != null) {
            show += country.getName();
        }
//        if (city != null) {
//            show += city.getName();
//        }
        return show;
    }

    public static class Country {
        String id;
        String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class City {
        String id;
        String config_country_id;
        String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getCountryId() {
            return config_country_id;
        }

        public void setCountryId(String countryId) {
            this.config_country_id = countryId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
