package org.springframework.samples.petclinic;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.By;
import java.net.URL;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.samples.petclinic.ParasoftWatcher;

@ExtendWith(ParasoftWatcher.class)
public class NavigateTest {

    private static WebDriver driver;
    private static String baseUrl;

    @BeforeAll
    static void setUp() throws Exception {
        // Set ChromeOptions with desired arguments
        ChromeOptions options = new ChromeOptions();
        // Add any additional Chrome arguments or preferences if needed
        options.addArguments("--headless");

        // URL of the Selenium Grid Hub
        URL gridUrl = new URL("http://34.211.11.203:4444/wd/hub");

        // Create a RemoteWebDriver instance with ChromeOptions and grid URL
        driver = new RemoteWebDriver(gridUrl, options, false);

        // base URL
        baseUrl = System.getProperty("baseUrl", "http://localhost:8099");
    }

    @Test
    public void testSample() {
        // Test logic
        
        // Navigate to the desired web page
        driver.get(baseUrl);
        // Debug
        System.out.println("FOO");

        // Get the text of the heading element using its CSS selector
        String headingText = driver.findElement(By.cssSelector("h1")).getText();

        // Print the heading text to the console
        System.out.println("Heading of the web page: " + headingText);

    }

    @AfterAll
    static void tearDown() {
        // Close the WebDriver session
        if (driver != null) {
            driver.quit();
        }
    }
}
