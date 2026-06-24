import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By; //  импорт для сокращения кода
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SauceDemoTest {
    private WebDriver driver;

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    // ================= Тесты =================

    @Test
    public void smokeTest1TitleCheck() { // Тест-1
        driver.get("https://saucedemo.com");
        String expectedTitle = "Swag Labs";
        String actualTitle = driver.getTitle();
        assertEquals(expectedTitle, actualTitle, "Заголовок страницы не совпадает!");
    }

    @Test
    public void smokeTest2SuccessfulLogin() { // Тест-2
        driver.get("https://saucedemo.com");

        // Находим поля и вводим логин и пароль
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        // Проверяем, что URL изменился (успешный вход на сайт)
        String expectedUrl = "https://www.saucedemo.com/inventory.html";
        String actualUrl = driver.getCurrentUrl();

        assertEquals(expectedUrl, actualUrl, "Не удалось авторизоваться, URL не совпадает!");
    }



    @Test
    public void negativeTestWrongPassword() { // Тест-3
        driver.get("https://saucedemo.com");

        // Вводим правильный логин и неверный пароль
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("wrong_password");
        driver.findElement(By.id("login-button")).click();

        // Находим элемент ошибки по css-селектору "сообщения-плашки"
        String expectedError = "Epic sadface: Username and password do not match any user in this service";
        String actualError = driver.findElement(By.cssSelector("[data-test='error']")).getText();

        assertEquals(expectedError, actualError, "Текст ошибки при неверном пароле не совпадает!");
    }


    @Test
    public void negativeTestEmptyUsername() { // Тест-4
        driver.get("https://saucedemo.com");

        // Вводим только пароль
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        // Проверяем текст ошибки
        String expectedError = "Epic sadface: Username is required";
        String actualError = driver.findElement(By.cssSelector("[data-test='error']")).getText();

        assertEquals(expectedError, actualError, "Текст ошибки при пустом логине не совпадает!");
    }

    @Test
    public void bugTestProblemUserSorting() { // Баг тест-5; Данный тест "намеренно" падает, так как он локализует баг сортировки у problem_user
        driver.get("https://saucedemo.com");
        driver.findElement(By.id("user-name")).sendKeys("problem_user"); // Ломаный юзер
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        // Кликаем на контейнер сортировки и выбираем опцию "сотрировки" (Name: Z to A)
        org.openqa.selenium.WebElement sortSelect = driver.findElement(By.className("product_sort_container"));
        sortSelect.click();
        driver.findElement(By.cssSelector("option[value='za']")).click();

        // Проверяем имя первого товара. Ожидаем "Test.allTheThings() T-Shirt (Red)" (последний по алфавиту)
        String expectedFirstItem = "Test.allTheThings() T-Shirt (Red)";
        String actualFirstItem = driver.findElement(By.className("inventory_item_name")).getText();

        // Этот ассерт "упадет", так как сортировка у problem_user не сработала. Это докажет баг!
        assertEquals(expectedFirstItem, actualFirstItem, "БАГ: Сортировка по алфавиту Z-A не сработала для problem_user!");
    }

    @Test
    public void positiveTestAddToCart() { // Тест-6
        driver.get("https://saucedemo.com");
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        // Добавляем первый товар (рюкзак)
        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();

        // Проверяем, что на иконке корзины появилась цифра "1"
        String cartBadge = driver.findElement(By.className("shopping_cart_badge")).getText();
        assertEquals("1", cartBadge, "Количество товаров в корзине не совпадает!");
    }

    @Test
    public void positiveTestRemoveFromCart() { // Тест-7
        driver.get("https://saucedemo.com");
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        // Нажимаем появившуюся кнопку Remove
        driver.findElement(By.id("remove-sauce-labs-backpack")).click();

        // Проверяем, что счетчик корзины исчез (стал пустым списком элементов)
        int badgeCount = driver.findElements(By.className("shopping_cart_badge")).size();
        assertEquals(0, badgeCount, "Счетчик корзины должен был исчезнуть!");
    }

    @Test
    public void positiveTestOpenProductPage() { // Тест-8
        driver.get("https://saucedemo.com");
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        // Кликаем по названию рюкзака
        driver.findElement(By.id("item_4_title_link")).click();

        // Проверяем, что на странице товара есть кнопка "Back to products"
        boolean isBackBtnDisplayed = driver.findElement(By.id("back-to-products")).isDisplayed();
        org.junit.jupiter.api.Assertions.assertTrue(isBackBtnDisplayed, "Кнопка возврата не отображается!");
    }

    @Test
    public void positiveTestSidebarMenu() throws InterruptedException { // Тест-9
        driver.get("https://saucedemo.com");
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        // Открываем бургер-меню
        driver.findElement(By.id("react-burger-menu-btn")).click();
        Thread.sleep(1000); // Даем меню полсекунды на анимацию открытия (можете изменить парамметр для удобства)

        // Проверяем, что ссылка "Logout" отображается (мы ее видим)
        boolean isLogoutVisible = driver.findElement(By.id("logout_sidebar_link")).isDisplayed();
        org.junit.jupiter.api.Assertions.assertTrue(isLogoutVisible, "Ссылка Logout не появилась в меню!");
    }

    @Test
    public void positiveTestFooterCopyright() { // Тест-10
        driver.get("https://saucedemo.com");
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        // Проверяем текст копирайта в самом низу страницы
        String footerText = driver.findElement(By.className("footer_copy")).getText();
        org.junit.jupiter.api.Assertions.assertTrue(footerText.contains("Sauce Labs. All Rights Reserved."), "Неверный текст в футере!");
    }

    // ====================================================================

    @AfterEach
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
