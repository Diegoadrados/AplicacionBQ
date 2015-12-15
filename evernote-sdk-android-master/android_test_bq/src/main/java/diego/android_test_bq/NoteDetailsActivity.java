package diego.android_test_bq;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.evernote.client.android.OnClientCallback;
import com.evernote.edam.type.Note;
import com.evernote.thrift.transport.TTransportException;

/**
 * Created by Mediamarktmaj on 14/12/2015.
 */
public class NoteDetailsActivity extends Logica_activity {

    private static final String LOG_TAG = NoteDetailsActivity.class.getSimpleName();

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_note_view);
        Intent intent = this.getIntent();
        String noteid = intent.getStringExtra(Intent.EXTRA_TEXT);
        try {
            mEvernoteSession.getClientFactory().createNoteStoreClient().getNote(noteid, true, false, false, false, getNoteCallback);
        } catch (TTransportException exception) {
            onError(exception, "Error creating notestore. ", R.string.error_creating_notestore);
        }
        showDialog(DIALOG_PROGRESS);

    }

    private void onError(Exception exception, String logstr, int id) {
        Log.e(LOG_TAG, logstr + exception);
        Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings("deprecation")
    private final OnClientCallback<Note> getNoteCallback = new OnClientCallback<Note>() {

        @Override
        public void onSuccess(Note data) {
            TextView noteTitle = (TextView) findViewById(R.id.nota_titulo);
            noteTitle.setText(data.getTitle());
            TextView noteContent = (TextView) findViewById(R.id.nota_contenido);
            noteContent.setText(Html.fromHtml(data.getContent()));
            removeDialog(DIALOG_PROGRESS);
        }

        @Override
        public void onException(Exception exception) {
            onError(exception, "Error retrieving note details. ", R.string.error_listing_notes);
            removeDialog(DIALOG_PROGRESS);
        }
    };


}
