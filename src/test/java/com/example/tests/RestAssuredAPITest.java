package com.example.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.example.config.TestConfiguration;
import com.example.utils.CommonUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import com.jayway.jsonpath.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import static com.example.utils.CommonUtils.insertRandomValue;
import static org.hamcrest.Matchers.*;

public class RestAssuredAPITest extends CommonMethods{
    private static ExtentReports extent;
    private ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    private String baseUrl;

    @BeforeClass(groups = {"regression", "smoke"})
    public void setup(ITestContext context) {
        baseUrl = System.getProperty("baseUrl", TestConfiguration.DEFAULT_BASE_URL);
        RestAssured.baseURI = baseUrl;
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("test-output/ExtentReport_" + CommonUtils.getCurrentDateTime() + ".html");
        sparkReporter.config().setDocumentTitle("Automation Test Results");
        sparkReporter.config().setReportName("API Test Report");
        sparkReporter.config().setTheme(Theme.STANDARD);
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Base URL", RestAssured.baseURI);
        if (context != null)
            extent.setSystemInfo("Suite Name", context.getSuite().getName());
    }

    @BeforeMethod(groups = {"regression", "smoke","sanity"})
    public void startTest(java.lang.reflect.Method method, ITestContext context) {
        if (extent == null) {
            System.out.println("ExtentReports is not initialized properly.");
            return;}
        ExtentTest extentTest = extent.createTest(method.getName());
        test.set(extentTest);
        extentTest.info("Base URL: " + RestAssured.baseURI);
        if (context != null)
            extentTest.info("Suite Name: " + context.getSuite().getName());
    }

    @Test(groups = "regression")
    public void ValidateInvalidKey() {
        // validating invalid status code if no key provided
        test.get().info("Endpoint tested :: " + RestAssured.baseURI+"/claims");
        Response response = RestAssured.
                        given().log().all().
                when().
                        get("/claims").
                then().
                        //log().all().
                        extract().response();
        validateAPIStatusCode(test, response.getStatusCode());
        validateAPIErrorMessage(test, response.path("Error"),"Invalid API Key"); //Actual and expected value
    }

    @Test(groups = "regression")
    public void ValidateInvalidKeyWithResponse() {
        // validating invalid status code if invalid api key provided
        test.get().info("Endpoint tested :: " + RestAssured.baseURI+"/claims");
        Response response =
                RestAssured.
                        given().
                        header("APIKEY", "very_hard_to_crack_1").
                when().
                        get("/claims").
                then().
                        //log().all().
                        extract().response();
        validateAPIStatusCode(test, response.getStatusCode());
        validateAPIErrorMessage(test, response.path("Error"),"Invalid API Key"); //Actual and expected value

    }


    @Test(groups = "smoke")
    public void ValidateResponseWithValidKey() {
        // validating valid key status code and response
        test.get().info("Endpoint tested :: " + RestAssured.baseURI+"/claims");
        Response response =
                RestAssured.
                        given().
                        header("APIKEY", "very_hard_to_crack").
                when().
                        get("/claims").
                then().
                        log().all().
                        extract().response();
        validateAPIStatusCode(test, response.getStatusCode());
    }

    /***
     * Post a claim using data_claim.json
     */
    @Test(groups = "regression")
    public void ValidatePostAClaim() {
        File file = new File("src/test/resources/JSONDATA/post_claim.json");
        System.out.println(file.toString());
        test.get().info("Endpoint tested :: " +RestAssured.baseURI+"/claims_post");
        Response response = RestAssured.
                given().
                contentType(ContentType.JSON).
                body(file).
                //log().all().
        when().
                post("/claims_post/").
        then().
                //log().all().
                        extract().response();
        validateAPIStatusCode(test, response.getStatusCode());
    }

    /***
     * Create claim - notification post method
     */
    @Test(groups = {"smoke", "regression"})
    public void ValidatePostAClaimNotification() throws IOException {
        // Specify the file path
        File file = new File("src/test/resources/JSONDATA/post_claim.json");
        // Read JSON from file
        String requestJsonAsString = new String(Files.readAllBytes(file.toPath()));
        // Insert random value
        String jsonPathToEdit = "$.requests[0].headers[1].value";
        requestJsonAsString = insertRandomValue(requestJsonAsString, jsonPathToEdit, 1, 1000);
        test.get().info("Endpoint tested :: " +RestAssured.baseURI+"/claims_post");
        Response response =
                RestAssured.
                given().
                        contentType(ContentType.JSON).
                        body(requestJsonAsString).
                        log().all().
                when().
                        post("/claims_post/").
                then().
                        log().all().extract().response();
        // Extract body from request and response for comparison
        String requestBody = JsonPath.read(requestJsonAsString, "$.requests[0].body").toString();
        String responseBody = JsonPath.read(response.getBody().asString(), "$.requests[0].body").toString();
        validateAPIStatusCode(test, response.getStatusCode());
        validateAPIErrorMessage(test, responseBody,requestBody);
    }

    @AfterMethod(groups = {"regression", "smoke"})
    public void tearDown() {
        extent.flush();
    }
}
