package ru.netology;


import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class CardOrderTest {

    private WebDriver driver;

    @BeforeEach
    void setup() {
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
        driver.findElement(By.cssSelector("input[name='name']")).sendKeys(name);
        driver.findElement(By.cssSelector("input[name='phone']")).sendKeys(phone);

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

        assertTrue(notification.getText().contains("успешно"));
    }
    @Test
    void shouldRejectLatinName() {

        fillForm("Ivan Petrov", "+79001234567", true);

        WebElement error =
                driver.findElement(By.cssSelector("[data-test-id='name'] .input__sub"));
        System.out.println(error.getText());
        assertTrue(error.getText().contains("Допустимы только русские буквы"));
    }
    @Test
    void shouldRejectEmptyName() {

        fillForm("", "+79001234567", true);

        WebElement error =
                driver.findElement(By.cssSelector("[data-test-id='name'] .input__sub"));

        assertTrue(error.getText().contains("Поле обязательно"));
    }
    @Test
    void shouldRejectWrongPhone() {

        fillForm("Иван Иванов", "12345", true);

        WebElement error =
                driver.findElement(By.cssSelector("[data-test-id='phone'] .input__sub"));

        assertTrue(error.getText().contains("Телефон указан неверно"));
    }
    @Test
    void shouldRejectEmptyPhone() {

        fillForm("Иван Иванов", "", true);

        WebElement error =
                driver.findElement(By.cssSelector("[data-test-id='phone'] .input__sub"));

        assertTrue(error.getText().contains("Поле обязательно"));
    }
    @Test
    void shouldRequireAgreement() {

        fillForm("Иван Иванов", "+79001234567", false);

        WebElement checkbox =
                driver.findElement(By.cssSelector("[data-test-id='agreement']"));

        assertTrue(checkbox.getAttribute("class").contains("input_invalid"));
    }
}