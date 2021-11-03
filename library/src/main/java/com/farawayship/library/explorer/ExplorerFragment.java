package com.farawayship.library.explorer;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.ListFragment;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;

import pl.itto.file_manager.R;
import com.farawayship.library.event_bus.FileSelectEvent;

/**
 * Display the contents of a directory
 */
public class ExplorerFragment extends ListFragment
        implements ActionMode.Callback, DirectoryAdapter.Listener {

    private static final String TAG = "ExplorerFragment";
    static final String DIRECTORY = "directory";
    static final String SHOW_HIDDEN = "show_hidden";

    interface Listener {
        void onBrowseDirectory(String directory);

        void onSendUris(ArrayList<Uri> uris);
    }

    private Listener mListener;
    private DirectoryAdapter mDirectoryAdapter;
    private ActionMode mActionMode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListener = (Listener) getParentFragment();

        // Watch for long presses on directories
//        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//                mDirectoryAdapter.activateCheckboxes(position);
//                //noinspection ConstantConditions
//                mActionMode = getActivity().startActionMode(ExplorerFragment.this);
//                return true;
//            }
//        });

        //noinspection ConstantConditions

        // Use the directory argument if provided or default to the first one found
        Bundle arguments = getArguments();
        String directory = null;
        if (arguments != null) {
            directory = arguments.getString(DIRECTORY);
        }
        if (directory == null) {
            directory = Environment.getExternalStorageDirectory().getPath();
        }

        // Create the adapter for the directory
        mDirectoryAdapter = new DirectoryAdapter(
                directory,
                arguments != null && arguments.getBoolean(SHOW_HIDDEN),
                getActivity(),
                this
        );
        setListAdapter(mDirectoryAdapter);
    }

    @Override
    public void onListItemClick(ListView listView, View view, int position, long id) {
        if (mActionMode == null) {
            File file = mDirectoryAdapter.getItem(position);
            if (file == null && !mDirectoryAdapter.isIsInRootFolder()) {
                // Parent folder item, need to go up
                File dir = new File(mDirectoryAdapter.getDirectory());
                File parentDir = dir.getParentFile();
                mListener.onBrowseDirectory(parentDir.getPath());
                return;
            }
            //noinspection ConstantConditions
            if (file.isDirectory()) {
                mListener.onBrowseDirectory(file.getPath());
            } else {
                Log.d(TAG, "on file selected: "+file.getAbsolutePath());
                EventBus.getDefault().post(new FileSelectEvent(file));
//                ArrayList<Uri> uris = new ArrayList<>();
//                uris.add(Uri.fromFile(file));
//                mListener.onSendUris(uris);
            }

        } else {
            mDirectoryAdapter.toggleItem(position);
        }
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        MenuInflater inflator = mode.getMenuInflater();
        inflator.inflate(R.menu.menu_explorer_actions, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        if (item.getItemId() == R.id.action_send) {
            mListener.onSendUris(mDirectoryAdapter.getUris());
            mActionMode.finish();
            return true;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mDirectoryAdapter.deactivateCheckboxes();
        mActionMode = null;
    }

    @Override
    public void onAllItemsDeselected() {
        mActionMode.finish();
    }

    @Override
    public void onError(String message) {
        setEmptyText(message);
    }

    /**
     * Indicate whether hidden items should be shown
     *
     * @param showHidden true to show hidden items
     */
    void showHidden(boolean showHidden) {
        mDirectoryAdapter.toggleHidden(showHidden);
    }
}
