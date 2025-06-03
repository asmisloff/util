package ru.asmisloff.dto;

import java.util.List;

public record CellDto(int[] leftSection, int[] rightSection, List<EdgeDto> edges) { }
