package net.lindseybot.shared.enums;

import lombok.Getter;

public enum Language {

    cs_CZ("Czech"),
    de_DE("German"),
    en_US("English (US)"),
    fr_FR("French"),
    nh_JP("Japanese"),
    nl_NL("Dutch (Netherlands)"),
    pt_BR("Portuguese (Brazil)"),
    ro_RO("Romanian"),
    ru_RU("Russian"),
    sv_SE("Swedish"),
    tlh_KL("Klingon"),
    vi_VN("Vietnamese"),
    zh_CN("Chinese Simplified"),
    zh_TW("Chinese Traditional");

    @Getter
    private final String name;

    Language(String name) {
        this.name = name;
    }

}
