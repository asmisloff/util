package ru.asmisloff;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record NodeDto(int i, int li, int x, boolean br) { }
