package diego.android_test_bq;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.evernote.client.android.EvernoteSession;

public class Logica_activity extends Activity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String CONSUMER_KEY = "diegoadrados-3382";
    private static final String CONSUMER_SECRET = "ee81ba3d1a9c4f61";

    // Initial development is done on Evernote's testing service, the sandbox.
    // Change to HOST_PRODUCTION to use the Evernote production service
    // once your code is complete, or HOST_CHINA to use the Yinxiang Biji
    // (Evernote China) production service.

    private static final EvernoteSession.EvernoteService EVERNOTE_SERVICE = EvernoteSession.EvernoteService.SANDBOX;
    protected EvernoteSession mEvernoteSession;
    protected final int DIALOG_PROGRESS = 101;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set up the Evernote Singleton Session
        mEvernoteSession = EvernoteSession.getInstance(this, CONSUMER_KEY, CONSUMER_SECRET, EVERNOTE_SERVICE, false);
    }


    // using createDialog, could use Fragments instead
    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_PROGRESS:
                return new ProgressDialog(this);
        }
        return super.onCreateDialog(id);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onPrepareDialog(int id, Dialog dialog) {
        switch (id) {
            case DIALOG_PROGRESS:
                ((ProgressDialog) dialog).setIndeterminate(true);
                dialog.setCancelable(false);
                ((ProgressDialog) dialog).setMessage(getString(R.string.Loading));
        }
    }}
