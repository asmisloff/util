package ru.asmisloff;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ViewportTest {

    private final Viewport vp = new Viewport(100, 100);

    @ParameterizedTest
    @MethodSource("setCenterTestData")
    void setCenter(SetCenterTestData td) {
        vp.setScale(td.scaleX, td.scaleY);
        vp.setCenter(td.xc, td.yc);
        assertAll("Левый верхний угол отображается в ожидаемую точку модели",
            () -> assertEquals(td.expectedOriginX, vp.getOriginX(), 1e-6f),
            () -> assertEquals(td.expectedOriginY, vp.getOriginY(), 1e-6f)
        );
    }

    private static Stream<SetCenterTestData> setCenterTestData() {
        return Stream.of(
            new SetCenterTestData(1f, 1f, 10f, 10f, -40f, -40f),
            new SetCenterTestData(1f, -1f, 10f, 10f, -40f, 60f)
        );
    }

    protected record SetCenterTestData(float scaleX,
                                       float scaleY,
                                       float xc,
                                       float yc,
                                       float expectedOriginX,
                                       float expectedOriginY) {}
}