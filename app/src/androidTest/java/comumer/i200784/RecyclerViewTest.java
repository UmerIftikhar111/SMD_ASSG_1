package comumer.i200784;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.rule.ActivityTestRule;
import org.junit.Rule;
import org.junit.Test;

public class RecyclerViewTest {

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

        // Wait for a while
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Check if the RecyclerView with id recyclerViewAds is displayed
        Espresso.onView(ViewMatchers.withId(R.id.recyclerViewAds))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

    }

}

