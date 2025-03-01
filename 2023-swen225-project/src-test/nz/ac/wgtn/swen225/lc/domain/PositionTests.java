package nz.ac.wgtn.swen225.lc.domain;

import nz.ac.wgtn.swen225.lc.domain.engine.Position;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

public class PositionTests {
    @ParameterizedTest
    @CsvSource({"1_1_15_2_16_3", "1500_1501_1333_1333_2833_2834"})
    public void testAddition(String input) {
        var parts = Arrays.stream(input.split("_")).map(Integer::parseInt).toList();
        Position pos = new Position(parts.get(0), parts.get(1));
        Position v2 = new Position(parts.get(2), parts.get(3));
        Assertions.assertEquals(new Position(parts.get(4), parts.get(5)), pos.add(v2));
    }

    @ParameterizedTest
    @CsvSource({"1_1_15_2_-14_-1", "1500_1501_1333_1333_167_168"})
    public void testSubtraction(String input) {
        var parts = Arrays.stream(input.split("_")).map(Integer::parseInt).toList();
        Position pos = new Position(parts.get(0), parts.get(1));
        Position v2 = new Position(parts.get(2), parts.get(3));
        Assertions.assertEquals(new Position(parts.get(4), parts.get(5)), pos.subtract(v2));
    }

    @ParameterizedTest
    @CsvSource({"1_3","5_2","6_1","0_0"})
    public void testToString(String input) {
        var parts = Arrays.stream(input.split("_")).map(Integer::parseInt).toList();
        Position pos = new Position(parts.get(0), parts.get(1));
        Assertions.assertEquals(String.format("(%s,%s)", parts.get(0), parts.get(1)), pos.toString());
    }

    record DistanceTestRecord(Position first, Position second, double distance) {}
    DistanceTestRecord dtr(int x0, int y0, int x1, int y1, double distance) {
        return new DistanceTestRecord(new Position(x0, y0), new Position(x1, y1), distance);
    }
    @TestFactory
    public Collection<DynamicTest> testDistanceFunction() {
        return Stream.of(
                dtr(1, 2, 3, 4, 2.8284271247461903),
                dtr(15,16,0,1, 21.213203435596427),
                dtr(0,0,0,0,0),
                dtr(19592,352,5189,5328,15238.339312405404)
        ).map(dt -> DynamicTest.dynamicTest(String.format("%s to %s", dt.first, dt.second), () -> {
            Assertions.assertEquals(dt.distance, dt.first.distance(dt.second));
        })).toList();
    }

    @Test
    public void testGreaterThan() {
        Assertions.assertTrue(new Position(5,5).gt(new Position(4,4)));
        Assertions.assertTrue(new Position(5,5).gtOrEq(new Position(5,5)));
    }

    @Test
    public void testLesserThan() {
        Assertions.assertTrue(new Position(4,4).lt(new Position(5,5)));
        Assertions.assertTrue(new Position(5,5).ltOrEq(new Position(5,5)));
    }
}
