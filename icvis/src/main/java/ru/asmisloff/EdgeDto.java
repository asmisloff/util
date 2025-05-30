package ru.asmisloff;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record EdgeDto(int src, int tgt) { }
