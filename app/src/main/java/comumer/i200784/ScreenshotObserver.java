package comumer.i200784;

import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

public class ScreenshotObserver extends ContentObserver {

    public ScreenshotObserver(Handler handler) {
        super(handler);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        super.onChange(selfChange, uri);

        // send notification

        ChatDetailsActivity.sendNotification("A screenshot of your chat was taken",ChatDetailsActivity.receiverUid, User.currentUser.getUid(), User.currentUser.getName());

    }


}

