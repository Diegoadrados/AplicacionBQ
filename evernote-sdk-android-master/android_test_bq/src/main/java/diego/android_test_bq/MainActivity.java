package diego.android_test_bq;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Fragment;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.evernote.client.android.OnClientCallback;
import com.evernote.edam.notestore.NoteCollectionCounts;
import com.evernote.edam.notestore.NoteFilter;
import com.evernote.edam.notestore.NoteMetadata;
import com.evernote.edam.notestore.NotesMetadataList;
import com.evernote.edam.notestore.NotesMetadataResultSpec;
import com.evernote.edam.type.NoteSortOrder;
import com.evernote.thrift.transport.TTransportException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;


public class MainActivity extends Logica_activity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private ArrayList<String> NotasTitulo;
    private ArrayList<String> NotasID;
    private ArrayAdapter<String> mArrayAdapter;
    private ListView mListView;

    private void logout() {
        try {
            mEvernoteSession.logOut();
        } catch (Exception e) {
            Log.e(LOG_TAG, "Tried to call logout with not logged in", e);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }*/
        Toast.makeText(getApplicationContext(), R.string.registro_exito, Toast.LENGTH_SHORT).show();
        NotasTitulo = new ArrayList<String>();
        NotasID = new ArrayList<String>();
        mArrayAdapter = new ArrayAdapter<String>(this, R.layout.list_item_notes, NotasTitulo);
        mListView = (ListView) findViewById(R.id.listView_main);
        mListView.setAdapter(mArrayAdapter);
        //findNotes();

    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            String note = NotasTitulo.get(i);
            String note_ID = NotasID.get(i);
            Toast.makeText(getApplicationContext(), note, Toast.LENGTH_SHORT).show();
            Intent detailActivityIntent = new Intent(getApplicationContext(), NoteDetailsActivity.class);
            //detailActivityIntent.putExtra(Intent.EXTRA_TEXT, note);
            detailActivityIntent.putExtra(Intent.EXTRA_TEXT, note_ID);
            startActivity(detailActivityIntent);
        }
    });
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.Tipos_de_orden, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                findNotes(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
}

    public void onError(Exception exception, String logstr, int id) {
        Log.e(LOG_TAG, logstr + exception);
        Toast.makeText(getApplicationContext(), id, Toast.LENGTH_LONG).show();
    }

    @SuppressWarnings("deprecation")
    private void findNotes(int i) {
        final int offset = 0;

        final NoteFilter filter = new NoteFilter();
        if(i==0) { //selecionado orden por actualizacion en el desplegable
            filter.setOrder(NoteSortOrder.UPDATED.getValue());
            filter.setAscending(false);
        }
        else { //selecionado orden alfabético de los titulos en el desplegable
            filter.setOrder(NoteSortOrder.TITLE.getValue());
            filter.setAscending(true);
        }
        final NotesMetadataResultSpec spec = new NotesMetadataResultSpec();
        spec.setIncludeTitle(true);

        mArrayAdapter.clear();


        try {

            final OnClientCallback<NoteCollectionCounts> notesCountCallback = new OnClientCallback<NoteCollectionCounts>() {
                @Override
                public void onSuccess(NoteCollectionCounts data) {
                    Map<String, Integer> map = data.getNotebookCounts();
                    Iterator it = map.entrySet().iterator();
                    int size = 0;
                    while (it.hasNext()) {
                        Map.Entry pairs = (Map.Entry) it.next();
                        size = (Integer) pairs.getValue() + size;
                    }
                    try {

                        final OnClientCallback<NotesMetadataList> callback = new OnClientCallback<NotesMetadataList>() {
                            @Override
                            public void onSuccess(NotesMetadataList data) {
                                NotasTitulo.clear();
                                NotasID.clear();
                                Toast.makeText(getApplicationContext(), "Se actualizaron tus notas", Toast.LENGTH_LONG).show();


                                for (NoteMetadata note : data.getNotes()) {
                                    NotasTitulo.add(note.getTitle());
                                    NotasID.add(note.getGuid());
                                }

                                mArrayAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onException(Exception exception) {
                                onError(exception, "Error listing notes. ", R.string.error_listing_notes);
                            }
                        };
                        mEvernoteSession.getClientFactory().createNoteStoreClient().findNotesMetadata(filter, offset, size, spec, callback);

                    } catch (TTransportException exception) {
                        onError(exception, "Error creating notestore. ", R.string.error_creating_notestore);
                    }

                }

                @Override
                public void onException(Exception exception) {

                }
            };

            //mEvernoteSession.getClientFactory().createNoteStoreClient()
            //.findNotesMetadata(filter, offset, pageSize, spec, callback);

            mEvernoteSession.getClientFactory().createNoteStoreClient().findNoteCounts(filter, false, notesCountCallback);
        } catch (TTransportException exception) {
            onError(exception, "Error creating notestore. ", R.string.error_creating_notestore);
        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    /*public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }*/
}