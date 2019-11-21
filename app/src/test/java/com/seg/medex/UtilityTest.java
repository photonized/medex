package com.seg.medex;

import org.junit.Test;

import static org.junit.Assert.*;

public class UtilityTest {

    @Test
    public void validPasswordLength() {
        String input;
        boolean output;
        boolean expected;

        input = "abcdef";
        expected = false;
        output = Utility.validPassword(input);

        assertEquals(expected,output);
    }
    
    @Test
    public void validPasswordLength2(){
      String input;
      boolean output;
      boolean expected;

      input = "a";
      expected = false;
      output = Utility.validPassword(input);
      assertEquals(expected,output);

    }

    @Test
    public void validPasswordLength3(){
      String input;
      boolean output;
      boolean expected;

      input = "ab";
      expected = false;
      output = Utility.validPassword(input);
      assertEquals(expected,output);

    }

    @Test
    public void validPasswordLength4(){
      String input;
      boolean output;
      boolean expected;

      input = "abcdeghijkl";
      expected = true;
      output = Utility.validPassword(input);
      assertEquals(expected,output);

    }

    @Test
    public void validNameLength() {
        String input;
        boolean output;
        boolean expected;

        input = "omer";
        expected = true;
        output = Utility.validName(input);
        assertEquals(expected,output);

    }

    @Test
    public void validNameLength2() {
        String input;
        boolean output;
        boolean expected;

        input = "vladnaderaliaman";
        expected = true;
        output = Utility.validName(input);
        assertEquals(expected,output);

    }

    @Test
    public void validNameNoLength() {
        String input;
        boolean output;
        boolean expected;

        input = "";
        expected = false;
        output = Utility.validName(input);
        assertEquals(expected,output);

    }

    @Test
    public void validNameWithSpacesCheck() {
        String input;
        boolean output;
        boolean expected;

        input = "Jonah   ";
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

    }

    @Test
    public void passwordsMatch2() {
        String input;
        String input2;
        boolean output;
        boolean expected;

        input = "@@@@ASDASD09900";
        input2 = "@@@@ASDASD09900";
        expected = true;
        output = Utility.passwordsMatch(input,input2);
        assertEquals(expected,output);

    }

    @Test
    public void noPasswordsMatchCheck() {
        String input;
        String input2;
        boolean output;
        boolean expected;

        input = "medexisthegreatestprojectalive";
        input2 = "medexisthegreatestproject";
        expected = false;
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

    }

    @Test
    public void notValidUsername() {
        String input;
        boolean output;
        boolean expected;

        input = "omerasdajkshkjsadhfjkashdfjkashdkfjhsadkjfhasf";
        expected = false;
        output = Utility.validUsername(input);
        assertEquals(expected,output);

    }

    @Test
    public void validUsername2() {
        String input;
        boolean output;
        boolean expected;

        input = "Omer123123123";
        expected = true;
        output = Utility.validUsername(input);
        assertEquals(expected,output);

    }

    @Test
    public void emptyUsernameCheck() {
        String input;
        boolean output;
        boolean expected;

        input = "";
        expected = false;
        output = Utility.validUsername(input);
        assertEquals(expected,output);

    }


    @Test
    public void validEmailCheck() {
        String input;
        boolean output;
        boolean expected;

        input = "omer@omer.ca";
        expected = true;
        output = Utility.validEmail(input);
        assertEquals(expected,output);

      }

      @Test
      public void notValidEmailCheck() {
          String input;
          boolean output;
          boolean expected;

          input = "omer";
          expected = false;
          output = Utility.validEmail(input);
          assertEquals(expected,output);

        }

        @Test
        public void emptyEmailCheck() {
            String input;
            boolean output;
            boolean expected;

            input = "";
            expected = false;
            output = Utility.validEmail(input);
            assertEquals(expected,output);

        }

        @Test
        public void notValidEmailCheck2() {
            String input;
            boolean output;
            boolean expected;

            input = "omer.ca";
            expected = false;
            output = Utility.validEmail(input);
            assertEquals(expected,output);

          }

          @Test
          public void notValidEmailCheck3() {
              String input;
              boolean output;
              boolean expected;

              input = "omer@.ca";
              expected = false;
              output = Utility.validEmail(input);
              assertEquals(expected,output);

            }
            @Test
            public void validEmailCheck3() {
                String input;
                boolean output;
                boolean expected;

                input = "HALA@MADRID.ES";
                expected = true;
                output = Utility.validEmail(input);
                assertEquals(expected,output);

              }

              //deliverable 3 tests for profile
            @Test
            public void validClinicNameLength(){
                String input;
                boolean output;
                boolean expected;

                input = "MedAttention";
                expected = true;
                output = Utility.validName(input);
                assertEquals(expected, output);

            }

            @Test
            public void validClinicNameLengthExtended(){
                String input;
                boolean output;
                boolean expected;

                input = "MedicalAttentionUrgentApplication";
                expected = true;
                output = Utility.validName(input);
                assertEquals(expected, output);

            }

            @Test
            public void validClinicNameNoLength() {
                String input;
                boolean output;
                boolean expected;

                input = "";
                expected = false;
                output = Utility.validName(input);
                assertEquals(expected,output);

            }

            @Test
            public void validClinicNameWithSpacesCheck() {
                String input;
                boolean output;
                boolean expected;

                input = "MEDICAL   ";
                expected = false;
                output = Utility.validName(input);
                assertEquals(expected,output);

            }

            @Test
            public void validStreetNumberCheck(){
                int input;
                boolean output;
                boolean expected;

                input = 213;
                expected = true;
                output = Utility.isNumerical(input);
                assertEquals(expected, output);

            }

            @Test
            public void validStreetNumberCheckExtended(){
                int input;
                boolean output;
                boolean expected;

                input = 121343123;
                expected = false;
                output = Utility.isNumerical(input);
                assertEquals(expected, output);

            }
            @Test
            public void validStreetNameCheck(){
                String input;
                boolean output;
                boolean expected;

                input = "matatestreet";
                expected = true;
                output = Utility.isAlpha(input);
                assertEquals(expected, output);

            }

            @Test
            public void validStreetNameCheckExtended(){
                String input;
                boolean output;
                boolean expected;

                input = "123matatestreet";
                expected = false;
                output = Utility.isAlpha(input);
                assertEquals(expected, output);

            }

            @Test
            public void validStreetNameCheckSpaces(){
                String input;
                boolean output;
                boolean expected;

                input = "123    matatestreet";
                expected = false;
                output = Utility.isAlpha(input);
                assertEquals(expected, output);

            }

            @Test
            public void validPostalCodeCheck(){
                String input;
                boolean output;
                boolean expected;

                input = "k1v9s2";
                expected = true;
                output = Utility.isAlphanumeric(input);
                assertEquals(expected, output);

            }

            @Test
            public void validPostalCheckExtended(){
                String input;
                boolean output;
                boolean expected;

                input = "kk1v9ss2"; //length must be less than 6
                expected = false;
                output = Utility.isAlphanumericPostalCode(input);
                assertEquals(expected, output);

            }
}
