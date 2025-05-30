package ru.asmisloff;

import java.util.List;

public record PartitionsDto(List<NodeDto> nodes, List<EdgeDto> edges) { }
