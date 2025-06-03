package ru.asmisloff;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import ru.asmisloff.dto.BranchPartitionsDto;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.SortedMap;

import static ru.asmisloff.Const.fromTestRoot;

class PartitionViewTest {

    private final ObjectMapper m = new ObjectMapper();

    @Test
    void test() {
        try {
            String path = fromTestRoot("data/buildPartitions_1.json");
            var typeRef = new TypeReference<SortedMap<Integer, BranchPartitionsDto>>() { };
            var partitions = m.readValue(new File(path), typeRef);
            PartitionView pv = new PartitionView(partitions.get(0));
            SwingUtilities.invokeLater(pv);
            while (!pv.isDisposed()) {
                //noinspection BusyWait
                Thread.sleep(50);
            }
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}