package edu.odu.cs;

import org.junit.jupiter.api.Test;

import static org.apache.commons.lang3.math.NumberUtils.DOUBLE_ZERO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Test of the Ranges class
 */
class TestRanges {

    double precision = 0.001;

    @Test
    void testConstructor() {
        Ranges ranges = new Ranges(1.0, 100.0);
        assertThat(ranges.sum(), closeTo(99.0, precision));

        Interval[] expected = {new Interval(1.0, 100.0)};
        assertThat(ranges, contains(expected));
    }

    @Test
    void testSubtractWhenIntervalWidthIsZeroThenDoNothing() {
        final var emptyInterval = new Interval(DOUBLE_ZERO, DOUBLE_ZERO);
        final var ranges = new Ranges(DOUBLE_ZERO, DOUBLE_ZERO);

        ranges.subtract(emptyInterval);
        assertThat(ranges.remaining.getFirst().width(), equalTo(DOUBLE_ZERO));
    }

    @Test
    void testSubtractWhenThereIsNotRemainingThenDoNothing() {
        final var low = 1.0D;
        final var high = 10.D;
        final var ranges = new Ranges(low, high);
        ranges.remaining.removeIf(interval -> true);

        final var intervalToSubtract = new Interval(low, high);

        ranges.subtract(intervalToSubtract);

        assertThat(ranges.remaining.isEmpty(), equalTo(true));
    }

    @Test
    void testSubtractWhenCurrentMinIsGreaterThanIntervalMinThenDoNothing() {
        final var rangeLow = 10.D;
        final var rangeHigh = 20.D;
        final var ranges = new Ranges(rangeLow, rangeHigh);

        final var intervalMin = 1.0D;
        final var intervalMax = 9.0D;
        final var intervalToSubtract = new Interval(intervalMin, intervalMax);

        ranges.subtract(intervalToSubtract);

        final var result = ranges.remaining.getFirst();
        assertThat(result.width(), equalTo(10.0D));
        assertThat(result.getMin(), equalTo(10.0D));
        assertThat(result.getMax(), equalTo(20.0D));
    }

    @Test
    void testSubtractWhenCurrentMinIsLessThanOrEqualToIntervalMinAndRangeDoesNotOverlapThenDoNothing() {
        final var rangeLow = 10.D;
        final var rangeHigh = 20.D;
        final var ranges = new Ranges(rangeLow, rangeHigh);

        final var intervalMin = 25.0D;
        final var intervalMax = 30.0D;
        final var intervalToSubtract = new Interval(intervalMin, intervalMax);

        ranges.subtract(intervalToSubtract);

        final var result = ranges.remaining.getFirst();
        assertThat(result.width(), equalTo(10.0D));
        assertThat(result.getMin(), equalTo(10.0D));
        assertThat(result.getMax(), equalTo(20.0D));
    }

    @Test
    void testSubtractWhenIntervalOverlapsRangeAndLowerPartWidthIsGreaterThanZeroThenRemoveIntervalAndAddLowerPart() {
        final var rangeLow = 10.0D;
        final var rangeHigh = 20.0D;
        final var ranges = new Ranges(rangeLow, rangeHigh);

        final var intervalMin = 15.0D;
        final var intervalMax = 25.0D;
        final var intervalToSubtract = new Interval(intervalMin, intervalMax);

        ranges.subtract(intervalToSubtract);

        final var result = ranges.remaining.getFirst();
        assertThat(result.width(), equalTo(5.0D));
        assertThat(result.getMin(), equalTo(10.0D));
        assertThat(result.getMax(), equalTo(15.0D));
    }

    @Test
    void testSubtractWhenIntervalOverlapsRangeAndUpperPartWidthIsGreaterThanZeroThenRemoveIntervalAndAddUpperPart() {
        final var rangeLow = 10.D;
        final var rangeHigh = 20.D;
        final var ranges = new Ranges(rangeLow, rangeHigh);

        final var intervalMin = 1.0D;
        final var intervalMax = 15.0D;
        final var intervalToSubtract = new Interval(intervalMin, intervalMax);

        ranges.subtract(intervalToSubtract);

        final var result = ranges.remaining.getFirst();
        assertThat(result.width(), equalTo(5.0D));
        assertThat(result.getMin(), equalTo(15.0D));
        assertThat(result.getMax(), equalTo(20.0D));
    }

    @Test
    void testToString() {
        final var rangeLow = 10.D;
        final var rangeHigh = 20.D;
        final var ranges = new Ranges(rangeLow, rangeHigh);

        final var result = ranges.toString();

        assertThat(result, equalTo("[(10.0,20.0)]"));
    }
}