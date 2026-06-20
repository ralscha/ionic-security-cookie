package ch.rasc.security;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.MountableFile;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.AriaRole;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
    properties = { "server.port=8080", "spring.docker.compose.enabled=false" })
class ApplicationIT {

  private static final Path CLIENT_DIST = Path.of("..", "client", "dist", "app",
      "browser").toAbsolutePath().normalize();

  private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>(
      "postgres:18-alpine").withDatabaseName("demo").withUsername("demo")
      .withPassword("demo");

  private static final GenericContainer<?> CADDY = new GenericContainer<>("caddy:2.11.4")
      .withCopyFileToContainer(MountableFile.forHostPath(CLIENT_DIST), "/srv")
      .withCommand("caddy", "file-server", "--root", "/srv", "--listen", ":80")
      .withExposedPorts(80).waitingFor(Wait.forHttp("/"));

  static {
    if (!Files.exists(CLIENT_DIST.resolve("index.html"))) {
      throw new IllegalStateException(
          "Client build output not found: " + CLIENT_DIST
              + ". Run `npm run build` in the client directory first.");
    }
    POSTGRES.start();
    CADDY.start();
  }

  @DynamicPropertySource
  static void registerProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
    registry.add("app.allow-origin", () -> clientOrigin());
  }

  @AfterAll
  static void stopContainers() {
    CADDY.stop();
    POSTGRES.stop();
  }

  @Test
  void userCanLoginNavigateAndLogoutThroughTheClient() {
    try (Playwright playwright = Playwright.create();
        Browser browser = playwright.chromium()
            .launch(new BrowserType.LaunchOptions().setHeadless(true))) {
      Page page = browser.newPage();

      page.navigate(clientOrigin());
      assertThat(pageTitle(page)).containsText("Login");

      fillIonInput(page, "Username", "admin");
      fillIonInput(page, "Password", "admin");
      Response loginResponse = page.waitForResponse(
          response -> response.url().endsWith("/login"),
          () -> page.getByRole(AriaRole.BUTTON,
              new Page.GetByRoleOptions().setName("Login")).click());
      if (loginResponse.status() != 200) {
        throw new AssertionError("Login failed with HTTP " + loginResponse.status());
      }

      assertThat(page.getByRole(AriaRole.MAIN)).containsText("A secret message");
      assertThat(page.locator("ion-menu")).containsText("Users");
      assertThat(page.locator("ion-menu")).containsText("Profile");
      assertThat(page.locator("ion-menu")).containsText("Remember Me Sessions");

      clickMenuItem(page, "Profile");
      assertThat(pageTitle(page)).containsText("Profile");
      assertThat(ionInput(page, "Username")).hasValue("admin");
      assertThat(ionInput(page, "Email")).hasValue("admin@test.com");

      clickMenuItem(page, "Users");
      assertThat(pageTitle(page)).containsText("Users");
      assertThat(page.getByRole(AriaRole.MAIN)).containsText("admin@test.com");
      assertThat(page.getByRole(AriaRole.MAIN)).containsText("ADMIN");

      clickMenuItem(page, "Remember Me Sessions");
      assertThat(pageTitle(page)).containsText("Remember Me Sessions");
      assertThat(page.getByRole(AriaRole.MAIN)).containsText("No Remember Me Sessions");

      clickMenuItem(page, "Log off");
      assertThat(pageTitle(page)).containsText("Logged Off");
      assertThat(page.getByRole(AriaRole.MAIN))
          .containsText("You have been successfully logged off.");
    }
  }

  private static String clientOrigin() {
    return "http://localhost:" + CADDY.getMappedPort(80);
  }

  private static void fillIonInput(Page page, String label, String value) {
    Locator input = ionInput(page, label);
    input.fill(value);
  }

  private static Locator ionInput(Page page, String label) {
    return page.locator("#main-content ion-input[label='" + label + "'] input").last();
  }

  private static Locator pageTitle(Page page) {
    return page.locator("#main-content ion-title").last();
  }

  private static void clickMenuItem(Page page, String text) {
    page.locator("ion-menu ion-item")
        .filter(new Locator.FilterOptions().setHasText(text)).click();
  }
}
