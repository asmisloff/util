package ru.asmisloff;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

class PartitionViewTest {

    private final ObjectMapper m = new ObjectMapper();

    @Test
    void test() {
        try {
            String path = "./src/test/java/ru/asmisloff/data/nodes-and-edges.json";
            PartitionsDto partitions = m.readValue(new File(path), PartitionsDto.class);
            PartitionView pv = PartitionView.fromJson(m.writeValueAsString(partitions));
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