package org.baeldung.home;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.logging.Level;

import org.baeldung.config.MainConfig;
import org.baeldung.site.guide.SpringMicroservicesGuidePage;
import org.baeldung.site.home.HomePageDriver;
import org.baeldung.site.home.NewsLettersubscriptionPage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.jayway.restassured.RestAssured;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { MainConfig.class })
public final class HomePageUITest {

    @Autowired
    HomePageDriver homePageDriver;

    @Autowired
    NewsLettersubscriptionPage newsLettersubscriptionPage;

    @Autowired
    SpringMicroservicesGuidePage springMicroservicesGuidePage;

    @Test
    public final void whenJavaWebWeeklySubscribePopup_thenEmailAndSubscribeElementsExist() {

        try {
            homePageDriver.openNewWindowAndLoadPage();

            homePageDriver.clickNewsletterButton();
            Thread.sleep(1000);
            newsLettersubscriptionPage.clickGetAccessToTheLatestIssuesButton();
            System.out.print("is displayed-->"+newsLettersubscriptionPage.findEmailFieldInSubscriptionPopup().isDisplayed());
            assertTrue(newsLettersubscriptionPage.findEmailFieldInSubscriptionPopup().isDisplayed());
            assertTrue(newsLettersubscriptionPage.findSubscripbeButtonInSubscriptionPopup().isDisplayed());

        } catch (Exception e) {            
            Assert.fail(e.getMessage());
        } finally {
            newsLettersubscriptionPage.quiet();
            homePageDriver.quiet();
        }

    }

    @Test
    public final void javaWeeklyLinksMatchWithLinkText() {
        try {
            homePageDriver.openNewWindowAndLoadPage();

            List<WebElement> javaWeeklyElements = this.homePageDriver.getAllJavaWeeklyIssueLinkElements();
            String expectedLink;
            String issueNumber;
            for (WebElement webElement : javaWeeklyElements) {
                issueNumber = webElement.getText().replaceAll("\\D+", "");
                if (issueNumber.length() > 0) {
                    expectedLink = (this.homePageDriver.getPageURL() + "/java-weekly-") + issueNumber;

                    System.out.println("expectedLink-->" + expectedLink);
                    System.out.println("actual  Link-->" + webElement.getAttribute("href"));

                    assertTrue(expectedLink.equals(webElement.getAttribute("href").toString()));
                }
            }
        } catch (Exception e) {            
            Assert.fail(e.getMessage());
        } finally {
            homePageDriver.quiet();
        }

    }

    @Test
    public final void givenOnTheMicroservicesGuidePage_whenOptinPopupIsLoaded_thenItContainsImages() {
        try {
            this.springMicroservicesGuidePage.openNewWindowAndLoadPage();
            springMicroservicesGuidePage.clickAccessTheGuideButton();
            assertEquals(200, RestAssured.given().get(springMicroservicesGuidePage.findFirstImagePath()).getStatusCode());
            assertEquals(200, RestAssured.given().get(springMicroservicesGuidePage.find2ndImagePath()).getStatusCode());
        } catch (Exception e) {            
            Assert.fail(e.getMessage());
        } finally {
            springMicroservicesGuidePage.quiet();
        }
    }

    @Test 
    public final void whenHomePageLoaded_thenZeroSevereMessagesInBrowserLog() {
        try {
            homePageDriver.openNewWindowAndLoadPage();
            
            LogEntries browserLogentries = homePageDriver.getWebDriver().manage().logs().get(LogType.BROWSER);
            int items = 0;
            for (LogEntry logEntry : browserLogentries) {                       
                if (logEntry.getLevel().equals(Level.SEVERE)) {
                    //System.out.println("Custom-->"+logEntry.getMessage());
                    items++;
                }
            }
            assertEquals(0, items);
        } catch (Exception e) {            
            Assert.fail(e.getMessage());
        } finally {
            homePageDriver.quiet();
        }
    }

}
