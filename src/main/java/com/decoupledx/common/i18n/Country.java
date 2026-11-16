package com.decoupledx.common.i18n;

/**
 * One entry of the searchable nationality dropdown (ADR-0003: records in
 * their own files). Public for Thymeleaf SpEL; the webui package is
 * module-internal either way.
 */
public record Country(String code, String name, String flag) {
}
