import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.net.MalformedURLException;
import java.net.URL;

public class NavigateTest {
    private WebDriver driver;

    @BeforeClass
    public void setUp() throws MalformedURLException {
        ChromeOptions options = new ChromeOptions();
        // Set Chrome options if needed
        options.addArguments("--headless"); // Example: Run Chrome in headless mode

        URL remoteUrl = new URL("http://34.211.11.203:4444/wd/hub"); // URL of the Selenium Grid Hub

        driver = new RemoteWebDriver(remoteUrl, options);
    }

    @Test
    public void navigateToExamplePage() {
        river.get("http://localhost:8099/");
		Thread.sleep(1000);
		driver.findElement(By.xpath("//a[@title=\"veterinarians\"]")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//a[@class=\"dropdown-toggle\"]")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//a[@ui-sref=\"owners\"]")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//owner-list/table/tbody/tr[1]/td[1]/a")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//dd/a")).click();
		Thread.sleep(1000);
		driver.findElement(By.xpath("//a[@title=\"home page\"]")).click();

    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}