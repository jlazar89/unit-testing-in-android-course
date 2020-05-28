package com.techyourchance.unittestingfundamentals.exercise2;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class StringDuplicatorTest {

    StringDuplicator SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new StringDuplicator();
    }

    @Test
    public void duplicator_emptyString_emptyStringReturned() {
        String result = SUT.duplicate("");
        assertThat(result,is(""));
    }

    @Test
    public void dupliactor_singleCharacter_singleCharacterReturned() {
        String result = SUT.duplicate("J");
        assertThat(result,is("JJ"));
    }

    @Test
    public void duplicator_longString_duplicatorStringReturned(){
        String result = SUT.duplicate("Jeffy Lazar");
        assertThat(result,is("Jeffy LazarJeffy Lazar"));
    }
}