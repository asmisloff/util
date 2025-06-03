package ru.asmisloff.dto;

import java.util.List;

public record PartitionDto(int xLeft, int xRight, List<CellDto> cells) { }
