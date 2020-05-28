package com.techyourchance.unittestingfundamentals.exercise3;

import com.techyourchance.unittestingfundamentals.example3.Interval;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class IntervalsAdjacencyDetectorTest {
    IntervalsAdjacencyDetector SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new IntervalsAdjacencyDetector();
    }


    // interval1 is before interval2
    @Test
    public void isAdjacent_interval1BeforeInterval2_falseReturned() {
        Interval interval1 = new Interval(-3,5);
        Interval interval2 = new Interval(1,20);
        boolean result = SUT.isAdjacent(interval1,interval2);
        assertThat(result,is(false));
    }

    // interval1 overlaps interval2 on start
    @Test
    public void isAdjacent_interval1OverlapsInterval2OnStart_trueReturned() {
        Interval interval1 = new Interval(-3,5);
        Interval interval2 = new Interval(3,20);
        boolean result = SUT.isAdjacent(interval1,interval2);
        assertThat(result,is(false));
    }

    // interval1 is contained within interval2
    @Test
    public void isAdjacent_interval1ContainedWithinInterval2_trueReturned() {
        Interval interval1 = new Interval(-1,5);
        Interval interval2 = new Interval(-3,20);
        boolean result = SUT.isAdjacent(interval1,interval2);
        assertThat(result,is(false));
    }

    // interval1 contains interval2
    @Test
    public void isAdjacent_interval1ContainsInterval2_trueReturned() {
        Interval interval1 = new Interval(-1,5);
        Interval interval2 = new Interval(1,3);
        boolean result = SUT.isAdjacent(interval1,interval2);
        assertThat(result,is(false));
    }

    // interval1 overlaps interval2 on end
    @Test
    public void isAdjacent_interval1OverlapsInterval2OnEnd_trueReturned() {
        Interval interval1 = new Interval(-1,5);
        Interval interval2 = new Interval(-4,3);
        boolean result = SUT.isAdjacent(interval1,interval2);
        assertThat(result,is(false));
    }

    // interval1 is after interval2
    @Test
    public void isAdjacent_interval1AfterInterval2OnEnd_trueReturned() {
        Interval interval1 = new Interval(-1, 5);
        Interval interval2 = new Interval(-10, -3);
        boolean result = SUT.isAdjacent(interval1,interval2);
        assertThat(result,is(false));
    }


    @Test
    public void isAdjacent_interval1BeforeAdjacentInterval2_trueReturned() throws Exception {
        Interval interval1 = new Interval(-1, 5);
        Interval interval2 = new Interval(5, 8);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(true));
    }

    @Test
    public void isAdjacent_interval1AfterAdjacentInterval2_trueReturned() throws Exception {
        Interval interval1 = new Interval(-1, 5);
        Interval interval2 = new Interval(-3, -1);
        boolean result = SUT.isAdjacent(interval1, interval2);
        assertThat(result, is(true));
    }
}