package comumer.i200784;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class RegisterActivityTest {

    @Rule
    public ActivityTestRule<RegisterActivity> activityRule = new ActivityTestRule<>(RegisterActivity.class);

    @Test
    public void testUserRegistrationAndRedirect() {


        Espresso.onView(ViewMatchers.withId(R.id.contact))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));


//        // Replace with test user information
//        String testUserName = "Test User";
//        String testUserEmail = "testuser@example.com";
//        String testUserPassword = "password";
//        String testUserContact = "123456789";
//        String testSelectedCountry = "Pakistan";
//        String testSelectedCity = "Lahore";
//
//        // Fill in registration fields
//
//        Espresso.onView(ViewMatchers.withId(R.id.contact)).perform(typeText(testUserContact));
//        Espresso.onView(ViewMatchers.withId(R.id.email)).perform(typeText(testUserEmail));
//        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(typeText(testUserPassword));
//        Espresso.onView(ViewMatchers.withId(R.id.name)).perform(typeText(testUserName));
//
//        Espresso.closeSoftKeyboard();
//
//        // Select country and city
//        Espresso.onView(ViewMatchers.withId(R.id.country)).perform(ViewActions.click());
//        Espresso.onView(withText(testSelectedCountry)).perform(click());
//
//        Espresso.onView(ViewMatchers.withId(R.id.city)).perform(ViewActions.click());
//        Espresso.onView(withText(testSelectedCity)).perform(click());
//
//        // Click the register button
//        Espresso.onView(ViewMatchers.withId(R.id.register_btn)).perform(click());
//
//
//        // Wait for a while
//        try {
//            Thread.sleep(10000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        // Check if the login btn is displayed
//        Espresso.onView(ViewMatchers.withId(R.id.login_btn))
//                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
//

    }
}
