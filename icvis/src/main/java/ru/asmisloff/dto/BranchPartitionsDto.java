package ru.asmisloff.dto;

import java.util.List;

public record BranchPartitionsDto(List<NodeDto> nodes, List<PartitionDto> partitions) { }
