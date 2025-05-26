package ru.asmisloff;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

class PartitionViewTest {

    private final List<Node> nodes;
    private final ObjectMapper m = new ObjectMapper();

    PartitionViewTest() {
        nodes = new ArrayList<>(30);
        int cnt = 1;
        for (int x = 0; x < 110; x += 10) {
            for (int li = 0; li < 3; li++) {
                nodes.add(new Node(cnt++, li, x, false));
            }
        }
    }

    @Test
    void test() {
        try {
            PartitionView pv = new PartitionView(m.writeValueAsString(nodes));
            SwingUtilities.invokeLater(pv);
            while (!pv.isDisposed()) {
                //noinspection BusyWait
                Thread.sleep(50);
            }
        } catch (JsonProcessingException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}