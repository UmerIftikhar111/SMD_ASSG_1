package comumer.i200784;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.*;
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
        Espresso.onView(ViewMatchers.withId(R.id.password)).perform(ViewActions.typeText("12345678"));
        Espresso.onView(ViewMatchers.withId(R.id.email)).perform(ViewActions.typeText("i201806@nu.edu.pk"));

        // Close soft keyboard
        Espresso.closeSoftKeyboard();

        // Perform click action on the login button
        Espresso.onView(ViewMatchers.withId(R.id.login_btn)).perform(ViewActions.click());

        // Wait for a while to ensure the toast is displayed
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if the R.id.current_username label has 'daniyal' on it
        Espresso.onView(ViewMatchers.withId(R.id.current_username))
                .check(ViewAssertions.matches(ViewMatchers.withText("daniyal")));

    }
}
