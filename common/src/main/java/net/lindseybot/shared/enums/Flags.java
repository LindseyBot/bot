package net.lindseybot.shared.enums;

import lombok.Getter;

public enum Flags {

    Australia("Australia"),
    Belgium("Belgium"),
    Brazil("Brasil"),
    Brunei("Brunei"),
    Canada("Canada"),
    China("China"),
    Denmark("Denmark"),
    France("France"),
    Germany("Germany"),
    Honduras("Honduras"),
    India("India"),
    Ireland("Ireland"),
    Japan("Japan"),
    Korea("Korea"),
    Mexico("Mexico"),
    Netherlands("Netherlands"),
    NewZealand("New Zealand"),
    Norway("Norway"),
    Portugal("Portugal"),
    Russia("Russia"),
    SaudiArabia("Saudi Arabia"),
    Sweden("Sweden"),
    Switzerland("Switzerland"),
    Taiwan("Taiwan"),
    UK("United Kingdom"),
    Unknown("Not specified"),
    USA("United States of America");

    @Getter
    private final String name;

    Flags(String name) {
        this.name = name;
    }

    public static Flags fromString(String value) {
        for (Flags country : Flags.values()) {
            if (country.name().equalsIgnoreCase(value)) {
                return country;
            }
        }
        return Flags.Unknown;
    }

}
