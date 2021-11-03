package com.farawayship.library.file_type.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.blankj.utilcode.util.FileUtils;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.yalantis.taurus.PullToRefreshView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pl.itto.file_manager.R;
import pl.itto.file_manager.file_type.adapter.ApkAdapter;
import pl.itto.file_manager.file_type.util.ACache;

/**
 * A simple {@link Fragment} subclass.
 */
public class ApkFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private List<File> mFiles;
    private ApkAdapter mAdapter;
    private Gson mGson;
    private ImageView mLoading;
    private TextView mLoadingText;
    private ACache mCatch;
    private SharedPreferences mPreferences;
    private PullToRefreshView mPullToRefreshView;

    private Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    mRecyclerView.setAdapter(mAdapter = new ApkAdapter(getContext(), mFiles));
                    mLoading.setVisibility(View.INVISIBLE);
                    mLoadingText.setVisibility(View.INVISIBLE);
                    mPullToRefreshView.setVisibility(View.VISIBLE);
                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    mAdapter.setOnItemClickLitener(new ApkAdapter.OnItemClickLitener() {
                        @Override
                        public void onItemClick(View view, int position) {
                            File file = mFiles.get(position);
                            Intent intent = new Intent();
                            intent.putExtra("EXTRA_FILE_PATH", file.getAbsolutePath());
                            getActivity().setResult(Activity.RESULT_OK, intent);
                            getActivity().finish();
                        }

                        @Override
                        public void onItemLongClick(View view, int position) {
                        }
                    });
                    break;
            }
            super.handleMessage(msg);
        }
    };


    public ApkFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.fragment_apk, container, false);

        TextView title = (TextView) ret.findViewById(R.id.title);
        title.setText(R.string.apk);
        ImageView reicon = (ImageView)ret.findViewById(R.id.return_index);
        reicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        mLoading = (ImageView) ret.findViewById(R.id.loading_gif);
        mRecyclerView = (RecyclerView) ret.findViewById(R.id.id_recyclerview);
        mLoadingText = (TextView) ret.findViewById(R.id.loading_text);
        mRecyclerView = (RecyclerView) ret.findViewById(R.id.id_recyclerview);
        mPullToRefreshView = (PullToRefreshView) ret.findViewById(R.id.pull_to_refresh);

        Glide.with(getContext()).load(R.drawable.loading)
                .into(mLoading);
        mFiles = new ArrayList<>();
        mGson = new Gson();
        mCatch = ACache.get(getContext());
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));

        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mFiles = FileUtils.listFilesInDirWithFilter(Environment.getExternalStorageDirectory(),  pathname -> pathname.getName().endsWith(".apk"), true, null);
                        addCatch();
                        try{
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mAdapter.notifyDataSetChanged();
                                    mPullToRefreshView.setRefreshing(false);
                                    onCreate(null);
                                    Toast.makeText(getContext(), "Refresh complete", Toast.LENGTH_SHORT).show();
                                }
                            });}
                        catch (Exception e){

                        }
                    }
                }).start();

            }
        });
        initDate();
        return ret;
    }

    private void initDate() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                judge();
                Message message = new Message();
                message.what = 1;
                myHandler.sendMessage(message);
            }
        }).start();
    }

    private void judge() {
        try {
            mPreferences = getContext().getSharedPreferences("table", Context.MODE_PRIVATE);
        } catch (Exception e) {
            //Execute when the child thread is not destroyed
        }

        boolean first = mPreferences.getBoolean("firstApk", true);
        int num = mPreferences.getInt("numApk", 0);

        long time = mPreferences.getLong("ApkTime", 0);
        long cha = System.currentTimeMillis() - time;
        //Determine whether the cache time has expired

        if (!first && time != 0 & cha < 86400000) {
            for (int i = 0; i < num; i++) {
                String s = String.valueOf(i);
                String string = mCatch.getAsString(s + "apk");
                if (string!=null) {
                    Log.d("aaa", "judge: ++++++++==" );
                    File file = mGson.fromJson(string, File.class);
                    mFiles.add(file);
                }

            }
        } else {

            mFiles = FileUtils.listFilesInDirWithFilter(Environment.getExternalStorageDirectory(),  pathname -> pathname.getName().endsWith(".apk"), true, null);
            addCatch();
        }
    }

    private void addCatch() {
        ArrayList<String> strings = new ArrayList<>();
        for (int i = 0; i < mFiles.size(); i++) {
            String s = mGson.toJson(mFiles.get(i));
            strings.add(s);
        }
        for (int i = 0; i < strings.size(); i++) {
            String s = String.valueOf(i);
            mCatch.put(s + "apk", strings.get(i), ACache.TIME_DAY);
        }

        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putBoolean("firstApk", false);
        edit.putInt("numApk", strings.size());
        edit.putLong("ApkTime", System.currentTimeMillis());
        edit.commit();
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }
}
