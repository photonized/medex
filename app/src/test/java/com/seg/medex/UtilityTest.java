package com.seg.medex;

import org.junit.Test;

import static org.junit.Assert.*;

public class UtilityTest {

    @Test
    public void validPassword() {
        String input;
        boolean output;
        boolean expected;

        input = "abcdef";
        expected = false;
        output = Utility.validPassword(input);
        assertEquals(expected,true);

        input = "a";
        expected = false;
        output = Utility.validPassword(input);
        assertEquals(expected,output);

        input = "ab";
        expected = false;
        output = Utility.validPassword(input);
        assertEquals(expected,output);

        input = "abcd";
        expected = false;
        output = Utility.validPassword(input);
        assertEquals(expected,output);

        input = "abcdeghijkl";
        expected = true;
        output = Utility.validPassword(input);
        assertEquals(expected,output);


    }

    @Test
    public void validName() {
        String input;
        boolean output;
        boolean expected;

        input = "omer";
        expected = true;
        output = Utility.validName(input);
        assertEquals(expected,output);

        input = "vladnaderaliaman";
        expected = true;
        output = Utility.validName(input);
        assertEquals(expected,output);

        input = "";
        expected = false;
        output = Utility.validName(input);
        assertEquals(expected,output);

        input = "123";
        expected = false;
        output = Utility.validName(input);
        assertEquals(expected,output);

        input = "JOhnathon      ";
        expected = false;
        output = Utility.validName(input);
        assertEquals(expected,output);


    }

    @Test
    public void passwordsMatch() {
        String input;
        String input2;
        boolean output;
        boolean expected;

        input = "123";
        input2 = "123";
        expected = true;
        output = Utility.passwordsMatch(input,input2);
        assertEquals(expected,output);

        input = "@@@@ASDASD09900";
        input2 = "@@@@ASDASD09900";
        expected = true;
        output = Utility.passwordsMatch(input,input2);
        assertEquals(expected,output);

        input = "@@@@ASDASD09900";
        input2 = "@ASDASD09900";
        expected = false;
        output = Utility.passwordsMatch(input,input2);
        assertEquals(expected,output);

        input = "medexisthegreatestprojectalive";
        input2 = "medexisthegreatestproject";
        expected = false;
        output = Utility.passwordsMatch(input,input2);
        assertEquals(expected,output);

        input = "123whoisinthestore";
        input2 = "123whoisinthestore";
        expected = true;
        output = Utility.passwordsMatch(input,input2);
        assertEquals(expected,output);

    }

    @Test
    public void validUsername() {
        String input;
        boolean output;
        boolean expected;

        input = "omer";
        expected = true;
        output = Utility.validUsername(input);
        assertEquals(expected,output);

        input = "omerasdajkshkjsadhfjkashdfjkashdkfjhsadkjfhasf";
        expected = false;
        output = Utility.validUsername(input);
        assertEquals(expected,output);

        input = "Omer123123123";
        expected = true;
        output = Utility.validUsername(input);
        assertEquals(expected,output);

        input = "123123123123";
        expected = true;
        output = Utility.validUsername(input);
        assertEquals(expected,output);

        input = "";
        expected = false;
        output = Utility.validUsername(input);
        assertEquals(expected,output);

    }

    @Test
    public void validEmail() {
        String input;
        boolean output;
        boolean expected;

        input = "omer@omer.ca";
        expected = true;
        output = Utility.validEmail(input);
        assertEquals(expected,output);

        input = "omer";
        expected = false;
        output = Utility.validEmail(input);
        assertEquals(expected,output);

        input = "";
        expected = false;
        output = Utility.validEmail(input);
        assertEquals(expected,output);

        input = "omer.ca";
        expected = false;
        output = Utility.validEmail(input);
        assertEquals(expected,output);

        input = "omer@.ca";
        expected = false;
        output = Utility.validEmail(input);
        assertEquals(expected,output);

        input = "HALA@MADRID.ES";
        expected = true;
        output = Utility.validEmail(input);
        assertEquals(expected,output);


    }
}