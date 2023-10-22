package comumer.i200784;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testSuccessfulLoginToast() {
        // Perform actions to fill in email and password fields
        Espresso.onView(ViewMatchers.withId(R.id.email)).perform(ViewActions.typeText("i201806@nu.edu.pk"));
        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(ViewActions.typeText("12345678"));

        // Close soft keyboard
        Espresso.closeSoftKeyboard();

        // Perform click action on the login button
        Espresso.onView(ViewMatchers.withId(R.id.login_btn)).perform(ViewActions.click());

        // Wait for a while to ensure the toast is displayed
        try {
            Thread.sleep(2000); // Adjust this delay as needed
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if the toast message is displayed
        Espresso.onView(ViewMatchers.withText("Name: daniyal\nEmail: i201806@nu.edu.pk"))
                .inRoot(new ToastMatcher())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }
}
