import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {
    public static final Company NULL_COMPANY = new Company();

    public static void main(String[] args) throws InterruptedException, IOException {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Laura\\Downloads\\chromedriver_win32\\chromedriver.exe");
        long start = System.nanoTime();

        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.get("https://www.neti.ee/cgi-bin/teema/ARI/Tekstiil_ja_Jalatsid/");

        List<WebElement> links;
        List<Company> companies = new ArrayList<>();
        By linksSelector = By.cssSelector("li.result-item.company");
        for (int i = 0; i < (links = driver.findElements(linksSelector)).size(); i++) {

            Company newCompany = getCompany(driver, links.get(i));
            if (newCompany != NULL_COMPANY) {
                companies.add(newCompany);
            }

            if (driver instanceof JavascriptExecutor) {
                Point endPoint = links.get(i).getLocation();
                ((JavascriptExecutor) driver)
                        .executeScript("window.scrollTo(0, " + endPoint.y + ");");
            }

            System.gc();
        }
        long diff = System.nanoTime() - start;

        System.out.println("Time it took in hours: " + diff/1000/60/60);
        createFile(companies);

        Thread.sleep(1000);  // Let the user actually see something!
        driver.quit();
    }
    static final By btnSelector = By.cssSelector("div.expand > a");
    static final By headerSelector = By.cssSelector("h3");
    static final By emailSelector = By.cssSelector("tr.fc-bi-contacts-field  td.fc-bi-contact-value  a");
    private static Company getCompany(WebDriver driver, WebElement listElement) {
        Company company = new Company();
        long start = System.nanoTime();
        try {

            WebElement contactButton = listElement.findElement(btnSelector);
            contactButton.click();
            WebElement nameForCompanyElem = listElement.findElement(headerSelector);
            String nameForCompany = nameForCompanyElem.getText();
            if(nameForCompany.length() == 0) {
                Thread.sleep(100);
                nameForCompany = nameForCompanyElem.getText();
            }
            company.setName(nameForCompany);
            List<WebElement> emailsForCompany = listElement.findElements(emailSelector);
            company.setEmails(emailsForCompany.stream()
                    .filter(WebElement::isDisplayed)
                    .map(WebElement::getText)
                    .collect(Collectors.toList()));
            return company;

        } catch (Exception e) {
            System.out.println(e);
        } finally {
            System.out.println(System.nanoTime() - start);
            closeButton(driver);
        }
        return NULL_COMPANY;
    }

    private static void createFile(List<Company> companies) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter("Tekstiil.csv", true));
        out.write("Ettevõte");
        out.write(";");
        out.write("E-mail");
        out.newLine();
        for (Company company : companies) {
            out.write(company.getName());
            out.write(";");
            out.write(company.getEmails().toString());
            out.newLine();
        }
        out.close();
    }

    private static void closeButton(WebDriver driver) {
        try {
            WebElement closeButton = driver.findElement(By.cssSelector("li.result-item.company.fc-bi-ok.result-item-active > div.expand-info.business-popup > div.business-popup-scroll > div > div > div > div.expand > a"));
            closeButton.click();
        } catch (Exception e) {
        }
    }
}