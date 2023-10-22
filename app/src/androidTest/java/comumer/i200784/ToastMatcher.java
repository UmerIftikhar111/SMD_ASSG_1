package comumer.i200784;

import android.view.WindowManager;
import androidx.test.espresso.Root;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class ToastMatcher extends TypeSafeMatcher<Root> {

    @Override
    public void describeTo(Description description) {
        description.appendText("is toast");
    }

    @Override
    protected boolean matchesSafely(Root root) {
        int type = root.getWindowLayoutParams().get().type;
        if ((type == WindowManager.LayoutParams.TYPE_TOAST)) {
            android.view.WindowManager.LayoutParams windowParams = (android.view.WindowManager.LayoutParams) root.getWindowLayoutParams().get();
            return windowParams.height == WindowManager.LayoutParams.WRAP_CONTENT
                    && windowParams.width == WindowManager.LayoutParams.WRAP_CONTENT;
        }
        return false;
    }

    public static ToastMatcher isToast() {
        return new ToastMatcher();
    }
}
