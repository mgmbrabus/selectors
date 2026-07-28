package ru.netology;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.jupiter.api.Assertions.*;

public class CardOrderTest {

    private WebDriver driver;

    @BeforeEach
    void setUp() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");

        driver = new ChromeDriver(options);
        driver.get("http://localhost:9999");
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }

    private void fillForm(String name, String phone, boolean agreement) {
        driver.findElement(By.cssSelector("[data-test-id='name'] input"))
                .sendKeys(name);

        driver.findElement(By.cssSelector("[data-test-id='phone'] input"))
                .sendKeys(phone);

        if (agreement) {
            driver.findElement(By.cssSelector(".checkbox__box")).click();
        }

        driver.findElement(By.cssSelector("button")).click();
    }

    @Test
    void shouldSubmitValidForm() {
        fillForm("Иван Иванов", "+79001234567", true);

        WebElement notification =
                driver.findElement(By.cssSelector("[data-test-id='order-success']"));

        assertTrue(notification.isDisplayed());

        assertEquals(
                "Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.",
                notification.getText()
        );

    }

    @Test
    void shouldRejectLatinName() {
        fillForm("Ivan Petrov", "+79001234567", true);

        WebElement error = driver.findElement(
                By.cssSelector("[data-test-id='name'].input_invalid .input__sub"));

        assertEquals(
                "Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.",
                error.getText()
        );
    }

    @Test
    void shouldRejectEmptyName() {
        fillForm("", "+79001234567", true);

        WebElement error = driver.findElement(
                By.cssSelector("[data-test-id='name'].input_invalid .input__sub"));

        assertEquals(
                "Поле обязательно для заполнения",
                error.getText()
        );
    }

    @Test
    void shouldRejectWrongPhone() {
        fillForm("Иван Иванов", "9001234567", true);

        WebElement error = driver.findElement(
                By.cssSelector("[data-test-id='phone'].input_invalid .input__sub"));

        assertEquals(
                "Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.",
                error.getText()
        );
    }

    @Test
    void shouldRejectEmptyPhone() {
        fillForm("Иван Иванов", "", true);

        WebElement error = driver.findElement(
                By.cssSelector("[data-test-id='phone'].input_invalid .input__sub"));

        assertEquals(
                "Поле обязательно для заполнения",
                error.getText()
        );
    }

    @Test
    void shouldRejectWithoutAgreement() {
        fillForm("Иван Иванов", "+79001234567", false);

        WebElement checkbox = driver.findElement(
                By.cssSelector("[data-test-id='agreement'].input_invalid"));

        assertTrue(checkbox.isDisplayed());
    }
}