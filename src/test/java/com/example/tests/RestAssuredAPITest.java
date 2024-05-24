package com.example.tests;

import com.example.config.TestConfiguration;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import com.jayway.jsonpath.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import static com.example.utils.CommonUtils.insertRandomValue;
import static org.hamcrest.Matchers.*;

public class RestAssuredAPITest {

    @BeforeClass(groups = {"smoke", "regression"})
    public void setup(){
        RestAssured.baseURI = TestConfiguration.BASE_URL;
    }

    @Test(groups = "regression")
    public void ValidateInvalidKey(){
        // validating invalid status code if no key provided
        RestAssured.
        given().log().all().
        when().
                get("/claims").
        then().
                log().all().
                assertThat().
                    statusCode(200).
                    body("Error",equalTo("Invalid API Key"));
    }

    @Test(groups = "regression")
    public void ValidateInvalidKeyWithResponse(){
        // validating invalid status code if invalid api key provided
        Response response =
            RestAssured.
                given().
                        header("APIKEY","very_hard_to_crack_1").
                when().
                        get("/claims").
                then().
                        //log().all().
                        extract().response();
        Assert.assertEquals(response.getStatusCode(), 200);
        Assert.assertEquals(response.path("Error"), "Invalid API Key");
        //System.out.println("RESPONSE :: " +response.asString());
    }


    @Test(groups = "smoke")
    public void ValidateResponseWithValidKey(){
        // validating valid key status code and response
        Response response =
                RestAssured.
                    given().
                            header("APIKEY","very_hard_to_crack").
                    when().
                            get("/claims").
                    then().
                            log().all().
                                    extract().response();
        Assert.assertEquals(response.getStatusCode(), 200);
        //List<String> roles = response.jsonPath().getList("."); to grab
    }

    /***
     * Post a claim using data_claim.json
     */
    @Test(groups = "regression")
    public void ValidatePostAClaim(){
        File file = new File("src/test/resources/JSONDATA/post_claim.json");
        System.out.println(file.toString());
        RestAssured.
        given().
                contentType(ContentType.JSON).
                body(file).
                log().all().
        when().
                post("claims_post/").
        then().
                log().all().
                assertThat().
                statusCode(200);
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
        Response response =
                RestAssured.
                    given().
                        contentType(ContentType.JSON).
                        body(requestJsonAsString).
                        log().all().
                    when().
                            post("claims_post/").
                    then().
                            log().all().extract().response();
        Assert.assertEquals(response.statusCode(), 200);
        // Extract body from request and response for comparison
        String requestBody = JsonPath.read(requestJsonAsString, "$.requests[0].body").toString();
        String responseBody = JsonPath.read(response.getBody().asString(), "$.requests[0].body").toString();
        Assert.assertEquals(requestBody, responseBody);

    }


}
