package com.example.tests;

import com.aventstack.extentreports.ExtentTest;
import org.testng.Assert;

public class CommonMethods {


    public void validateAPIStatusCode(ThreadLocal<ExtentTest> test, int resStatusCode) {
        test.get().info("Response Status Code: " + resStatusCode);
        try{
            Assert.assertEquals(resStatusCode, 200, "Expected result does not match actual result");
            test.get().pass("Test passed");
        } catch (AssertionError e) {
            test.get().fail("TEST FAIL -> EXPECTED VALUE :: "+200+ " ACTUAL VALUE :: " +resStatusCode);
            throw e; // Rethrow the AssertionError to ensure TestNG marks the test as failed
        }
    }


    public void validateAPIErrorMessage(ThreadLocal<ExtentTest> test, String actual, String expected){
        test.get().info("Response Error Message: " +actual);
        try{
            Assert.assertEquals(actual, expected, "Expected result Matches actual results");
            test.get().pass("TEST PASS -> EXPECTED VALUE :: "+expected+ " ACTUAL VALUE :: " +actual);
        } catch (AssertionError e) {
            test.get().fail("TEST FAIL -> EXPECTED VALUE :: "+expected+ " ACTUAL VALUE :: " +actual);
            throw e; // Rethrow the AssertionError to ensure TestNG marks the test as failed
        }
    }
}
