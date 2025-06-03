package ru.asmisloff.dto;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.SortedMap;

import static ru.asmisloff.Const.fromTestRoot;
import static ru.asmisloff.Const.om;

class BranchPartitionsDtoTest {

    @Test
    void deserialize() {
        String path = fromTestRoot("data/buildPartitions_1.json");
        TypeReference<SortedMap<Integer, BranchPartitionsDto>> typeRef = new TypeReference<>() { };
        Assertions.assertDoesNotThrow(() -> om.readValue(new File(path), typeRef));
    }
}