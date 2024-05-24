// src/test/java/com/example/tests/ExtentReportListener.java
package com.example.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
//import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ExtentReportListener implements ITestListener {
        private ExtentReports extent;
        private ExtentTest test;

        @Override
        public void onStart(ITestContext context) {
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter("extent-report.html");
            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
        }

        @Override
        public void onTestStart(ITestResult result) {
            test = extent.createTest(result.getMethod().getMethodName());
        }

        @Override
        public void onTestSuccess(ITestResult result) {
            test.pass("Test passed");
        }

        @Override
        public void onTestFailure(ITestResult result) {
            test.fail(result.getThrowable());
        }

        @Override
        public void onTestSkipped(ITestResult result) {
            test.skip(result.getThrowable());
        }

        @Override
        public void onFinish(ITestContext context) {
            extent.flush();
        }

}
