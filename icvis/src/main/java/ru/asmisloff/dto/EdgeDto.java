package ru.asmisloff.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record EdgeDto(int src, int tgt) {

    @Override
    public String toString() {
        return String.format("(%d - %d)", src, tgt);
    }
}
