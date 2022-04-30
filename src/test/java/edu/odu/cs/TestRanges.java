package edu.odu.cs;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.math.NumberUtils.*;
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
    void testRemoveWhenIntervalWidthIsZeroThenDoNothing() {
        final var emptyInterval = new Interval(DOUBLE_ZERO, DOUBLE_ZERO);
        final var ranges = new Ranges(DOUBLE_ZERO, DOUBLE_ZERO);

        ranges.remove(emptyInterval);

        final var remaining = extractRemainingAsList(ranges);
        assertThat(remaining.get(INTEGER_ZERO).width(), equalTo(DOUBLE_ZERO));
    }

    @Test
    void testRemoveWhenThereIsNotRemainingThenDoNothing() {
        final var low = 1.0D;
        final var high = 10.D;
        final var ranges = new Ranges(low, high);

        final var intervalToSubtract = new Interval(low, high);

        ranges.remove(intervalToSubtract);

        final var remaining = extractRemainingAsList(ranges);
        assertThat(remaining.isEmpty(), equalTo(true));
    }

    @Test
    void testRemoveWhenCurrentMinIsGreaterThanIntervalMinThenDoNothing() {
        final var rangeLow = 10.D;
        final var rangeHigh = 20.D;
        final var ranges = new Ranges(rangeLow, rangeHigh);

        final var intervalMin = 1.0D;
        final var intervalMax = 9.0D;
        final var intervalToSubtract = new Interval(intervalMin, intervalMax);

        ranges.remove(intervalToSubtract);

        final var remaining = extractRemainingAsList(ranges);
        final var result = remaining.get(INTEGER_ZERO);
        assertThat(result.width(), equalTo(10.0D));
        assertThat(result.getMin(), equalTo(10.0D));
        assertThat(result.getMax(), equalTo(20.0D));
    }

    @Test
    void testRemoveWhenCurrentMinIsLessThanOrEqualToIntervalMinAndRangeDoesNotOverlapThenDoNothing() {
        final var rangeLow = 10.D;
        final var rangeHigh = 20.D;
        final var ranges = new Ranges(rangeLow, rangeHigh);

        final var intervalMin = 25.0D;
        final var intervalMax = 30.0D;
        final var intervalToSubtract = new Interval(intervalMin, intervalMax);

        ranges.remove(intervalToSubtract);

        final var remaining = extractRemainingAsList(ranges);
        final var result = remaining.get(INTEGER_ZERO);
        assertThat(result.width(), equalTo(10.0D));
        assertThat(result.getMin(), equalTo(10.0D));
        assertThat(result.getMax(), equalTo(20.0D));
    }

    @Test
    void testRemoveWhenIntervalOverlapsRangeAndLowerPartWidthIsGreaterThanZeroThenRemoveIntervalAndAddLowerPart() {
        final var rangeLow = 10.0D;
        final var rangeHigh = 20.0D;
        final var ranges = new Ranges(rangeLow, rangeHigh);

        final var intervalMin = 15.0D;
        final var intervalMax = 25.0D;
        final var intervalToSubtract = new Interval(intervalMin, intervalMax);

        ranges.remove(intervalToSubtract);

        final var remaining = extractRemainingAsList(ranges);
        final var result = remaining.get(INTEGER_ZERO);
        assertThat(result.width(), equalTo(5.0D));
        assertThat(result.getMin(), equalTo(10.0D));
        assertThat(result.getMax(), equalTo(15.0D));
    }

    @Test
    void testRemoveWhenIntervalOverlapsRangeAndUpperPartWidthIsGreaterThanZeroThenRemoveIntervalAndAddUpperPart() {
        final var rangeLow = 10.D;
        final var rangeHigh = 20.D;
        final var ranges = new Ranges(rangeLow, rangeHigh);

        final var intervalMin = 1.0D;
        final var intervalMax = 15.0D;
        final var intervalToSubtract = new Interval(intervalMin, intervalMax);

        ranges.remove(intervalToSubtract);

        final var remaining = extractRemainingAsList(ranges);
        final var result = remaining.get(INTEGER_ZERO);
        assertThat(result.width(), equalTo(5.0D));
        assertThat(result.getMin(), equalTo(15.0D));
        assertThat(result.getMax(), equalTo(20.0D));
    }

    @Test
    void testRemoveWhenIntervalOverlapsRangeAndPartsWidthIsGreaterThanZeroThenRemoveIntervalAndAddParts() {
        final var rangeLow = 10.D;
        final var rangeHigh = 20.D;
        final var ranges = new Ranges(rangeLow, rangeHigh);

        final var intervalMin = 13.0D;
        final var intervalMax = 17.0D;
        final var intervalToSubtract = new Interval(intervalMin, intervalMax);

        ranges.remove(intervalToSubtract);

        final var remaining = extractRemainingAsList(ranges);
        assertThat(remaining.size(), equalTo(2));

        final var result1 = remaining.get(INTEGER_ZERO);
        assertThat(result1.width(), equalTo(3.0D));
        assertThat(result1.getMin(), equalTo(10.0D));
        assertThat(result1.getMax(), equalTo(13.0D));

        final var result2 = remaining.get(INTEGER_ONE);
        assertThat(result2.width(), equalTo(3.0D));
        assertThat(result2.getMin(), equalTo(17.0D));
        assertThat(result2.getMax(), equalTo(20.0D));
    }

    @Test
    void testToString() {
        final var rangeLow = 10.D;
        final var rangeHigh = 20.D;
        final var ranges = new Ranges(rangeLow, rangeHigh);

        final var result = ranges.toString();

        assertThat(result, equalTo("[(10.0,20.0)]"));
    }

    @Test
    void testSumWhenIntervalWasRemovedFromRangeThenResultIsLessThanOriginalWidth() {
        final var rangeLow = 10.D;
        final var rangeHigh = 20.D;
        final var ranges = new Ranges(rangeLow, rangeHigh);

        final var intervalMin = 1.0D;
        final var intervalMax = 15.0D;
        final var intervalToSubtract = new Interval(intervalMin, intervalMax);
        ranges.remove(intervalToSubtract);

        final var result = ranges.sum();

        assertThat(result, equalTo(5.0D));
    }

    private List<Interval> extractRemainingAsList(final Ranges ranges) {
        final var list = new ArrayList<Interval>();
        ranges.iterator().forEachRemaining(list::add);
        return list;
    }
}