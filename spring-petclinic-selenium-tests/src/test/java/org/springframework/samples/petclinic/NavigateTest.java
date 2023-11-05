import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.net.URL;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NavigateTest {

    private WebDriver driver;
    private String baseUrl;

    @Before
    public void setUp() throws Exception {
        // Set ChromeOptions with desired arguments
        ChromeOptions options = new ChromeOptions();
        // Add any additional Chrome arguments or preferences if needed
        // options.addArguments("--headless");

        // URL of the Selenium Grid Hub
        URL gridUrl = new URL("http://localhost:4444/wd/hub");

        // Create a RemoteWebDriver instance with ChromeOptions and grid URL
        driver = new RemoteWebDriver(gridUrl, options);

        // base URL
        baseUrl = System.getProperty("baseUrl", "http://localhost:8099");
    }

    @Test
    public void testSample() {
        // Test logic
        driver.get(baseUrl);
    }

    @After
    public void tearDown() {
        // Close the WebDriver session
        if (driver != null) {
            driver.quit();
        }
    }
}
