package com.decoupledx.common.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.context.i18n.LocaleContextHolder;

/**
 * The ISO 3166-1 alpha-2 country list for the searchable nationality
 * dropdown (native datalist — approved decision 4). Flags are EMOJI built
 * from the code's regional-indicator symbols (approved decision 5:
 * dependency-free; Windows Chromium renders the letters instead of glyphs —
 * accepted). Names are localized with the request locale. Public so Thymeleaf
 * SpEL can invoke it; the webui package is module-internal either way.
 */
public class Countries {

    /** All countries in localized-name order (stable, searchable). */
    public List<Country> all() {
        Locale locale = LocaleContextHolder.getLocale();
        List<Country> countries = new ArrayList<>();
        for (String code : Locale.getISOCountries(Locale.IsoCountryCode.PART1_ALPHA2)) {
            String name = new Locale("", code).getDisplayCountry(locale);
            countries.add(new Country(code, name, flagOf(code)));
        }
        countries.sort(java.util.Comparator.comparing(country -> country.name()));
        return countries;
    }

    /** Localized name for one code (unknown codes fall back to the code). */
    public String name(String code) {
        String name = new Locale("", code).getDisplayCountry(LocaleContextHolder.getLocale());
        return name.isBlank() ? code : name;
    }

    public static String flagOf(String code) {
        if (code == null || code.length() != 2) {
            return "";
        }
        int first = 0x1F1E6 + (Character.toUpperCase(code.charAt(0)) - 'A');
        int second = 0x1F1E6 + (Character.toUpperCase(code.charAt(1)) - 'A');
        return new String(Character.toChars(first)) + new String(Character.toChars(second));
    }
}
