package com.farawayship.library.file_type.fragment;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blankj.utilcode.util.FileUtils;
import com.bumptech.glide.Glide;
import com.farawayship.library.R;
import com.farawayship.library.file_type.adapter.WordAdapter;
import com.farawayship.library.file_type.util.ACache;
import com.google.gson.Gson;
import com.yalantis.taurus.PullToRefreshView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class WordFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private List<File> mFiles;
    private WordAdapter mAdapter;
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
                    mRecyclerView.setAdapter(mAdapter = new WordAdapter(getContext(), mFiles));
                    mLoading.setVisibility(View.INVISIBLE);
                    mLoadingText.setVisibility(View.INVISIBLE);
                    mPullToRefreshView.setVisibility(View.VISIBLE);
                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    mAdapter.setOnItemClickLitener(new WordAdapter.OnItemClickLitener() {
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


    public WordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View ret = inflater.inflate(R.layout.fragment_word, container, false);

        TextView title = (TextView) ret.findViewById(R.id.title);
        title.setText(R.string.doc);
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
        mRecyclerView = (RecyclerView) ret.findViewById(R.id.id_recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mPullToRefreshView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mFiles = FileUtils.listFilesInDirWithFilter(Environment.getExternalStorageDirectory(),  pathname -> pathname.getName().endsWith(".txt"), true, null);
                        addCatch();
                        try{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                mAdapter.notifyDataSetChanged();
                                mPullToRefreshView.setRefreshing(false);
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
        //开线程初始化数据
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
            //子线程未销毁可能时执行
        }
        boolean first = mPreferences.getBoolean("firstWord", true);
        int num = mPreferences.getInt("numWord", 0);
        long time = mPreferences.getLong("WordTime", 0);
        long cha = System.currentTimeMillis() - time;
        //判断缓存时间是否过期

        if (!first && time != 0 & cha < 86400000) {
            for (int i = 0; i < num; i++) {
                String s = String.valueOf(i);
                String string = mCatch.getAsString(s + "word");
                if (string!=null) {
                    File file = mGson.fromJson(string, File.class);
                    mFiles.add(file);
                }

            }
        } else {

            mFiles = FileUtils.listFilesInDirWithFilter(Environment.getExternalStorageDirectory(),  pathname -> pathname.getName().endsWith(".txt"), true, null);
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
            mCatch.put(s + "word", strings.get(i), ACache.TIME_DAY);
        }


        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putBoolean("firstWord", false);
        edit.putInt("numWord", strings.size());
        edit.putLong("WordTime", System.currentTimeMillis());
        edit.commit();
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }
}
