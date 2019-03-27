package com.zck.ydre;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.zck.ydre.adapter.SideBarRecyclerAdapter;
import com.zck.ydre.adapter.YdRecyclerAdapter;
import com.zck.ydre.util.ImageInfo;
import com.zck.ydre.util.SideBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main extends AppCompatActivity {

    private final String TAG = "Main";

    private RecyclerView sidebarRecyclerView, recyclerView;
    private ProgressBar progressBarLoading;
    private LinearLayoutManager linearLayoutManager;
    private StaggeredGridLayoutManager staggeredGridLayoutManager;
    private RequestQueue requestQueue;
    private String urls[] = {"https://yande.re", "https://yande.re/post"};
    private SideBarRecyclerAdapter sideBarRecyclerAdapter;
    private YdRecyclerAdapter ydRecyclerAdapter;
    private Handler handler;
    private AsyncTask task;

    private ArrayList<SideBar> sideBars;
    private ArrayList<ImageInfo> imageInfos;

    private int ydPage = 2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBarLoading = findViewById(R.id.main_progress_loading);
        sideBars = new ArrayList<>();
        imageInfos = new ArrayList<>();
        initRequestQueue();
        initRecyclerView();
    }

    private void initRequestQueue() {
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB
        Network network = new BasicNetwork(new HurlStack());
        requestQueue = new RequestQueue(cache, network);
        requestQueue.start();
    }

    private void initRecyclerView() {
        sidebarRecyclerView = findViewById(R.id.main_sidebar_recycler);
        recyclerView = findViewById(R.id.main_recycler);

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        sidebarRecyclerView.setLayoutManager(linearLayoutManager);

        staggeredGridLayoutManager = new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        startRecyclerAdapter();
    }

    private void startRecyclerAdapter() {

        initSidebarRecyclerAdapter();
        initYdRecyclerViewAdapter();

        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 20403) {
                    Bundle bundle = msg.getData();
                    sideBars = bundle.getParcelableArrayList("siderbar");
                    imageInfos = bundle.getParcelableArrayList("imageinfo");

                    Log.d(TAG, "handleMessage: 更新adapter");
                    sideBarRecyclerAdapter.setNewData(sideBars);
                    ydRecyclerAdapter.setNewData(imageInfos);

                    imageLoadDone();
                }
            }
        };
        PageFirstRequest(urls[1]);
    }

    private void initYdRecyclerViewAdapter() {
        Log.d(TAG, "initYdRecyclerViewAdapter: 被调用");
        ydRecyclerAdapter = new YdRecyclerAdapter();
        ydRecyclerAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (task == null) {
                    task = new PageInfoLoad().execute(String.format("%s?page=%d", urls[1], ydPage++));
                }
            }
        }, recyclerView);
        ydRecyclerAdapter.disableLoadMoreIfNotFullPage();
        recyclerView.setAdapter(ydRecyclerAdapter);
    }

    /**
     * 重置yd相关对象数据
     */
    private void resetYdData() {
        ydPage = 2;
    }

    private void initSidebarRecyclerAdapter() {
        Log.d(TAG, "initSidebarRecyclerAdapter: 被调用");
        sideBarRecyclerAdapter = new SideBarRecyclerAdapter();
        sideBarRecyclerAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                PageFirstRequest(sideBars.get(position).getLink());
            }
        });
        sidebarRecyclerView.setAdapter(sideBarRecyclerAdapter);
    }

    /**
     * 资源加载中
     * ProgressBar 可见
     * RecyclerView 不可见
     */
    private void imageOnLoad() {
        progressBarLoading.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    /**
     * 资源加载完毕
     * ProgressBar 不可见
     * RecyclerView 可见
     */
    private void imageLoadDone() {
        progressBarLoading.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }


    /**
     * 第一次打开某个页面时调用，以便于获取sidebar内容
     * @param url 页面链接
     */
    private void PageFirstRequest(String url) {

        // 在这个方法里重置yd的数据是为了能够在PageInfoLoad中调用adapter的notifyDataSetChanged方法
//        resetYdData();

        Log.d(TAG, "PageFirstRequest: 加载siderbar数据中...");
        imageOnLoad();

        final ArrayList<SideBar> items = new ArrayList<>();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // 解析sidebar数据
                Document doc = Jsoup.parse(response);
                Elements sidebarLis = doc.select("#content #post-list .sidebar #tag-sidebar").select("li");
                for (Element sidebarLi : sidebarLis) {
                    Element a = sidebarLi.select("a").get(1); // 标签a
                    String sidebarJumpLink = urls[0] + a.attr("href"); // 标签a href内容 
                    items.add(new SideBar(a.text(), sidebarJumpLink));
                }
                Log.d(TAG, "onResponse: item数量：" + items.size());
                ArrayList<ImageInfo> imageInfos = pageDataParse(response);
                // 利用 bundle 传递数据
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("siderbar", items);
                bundle.putParcelableArrayList("imageinfo", imageInfos);
                Message msg = new Message();
                msg.setData(bundle);
                msg.what = 20403;
                handler.sendMessage(msg);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Main.this, "异常：" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("User-Agent", MyOKhttp.USERAGENT);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }

    /**
     * 解析页面信息
     * @param response 网页html
     * @return 包含页面所有preview，title，file的url的list集合
     */
    private ArrayList<ImageInfo> pageDataParse(String response) {
        ArrayList<ImageInfo> imageInfos = new ArrayList<>();
        Document doc = Jsoup.parse(response);
        Elements lis = doc.getElementById("post-list-posts").select("li");
        for (Element li: lis) {
            String imageSourceUrl = li.select("a[class=directlink largeimg]").attr("href");
            String previewUrl = li.select(".inner .thumb").select("img").attr("src");
            String title = li.select(".inner .thumb").select("img").attr("title");
            imageInfos.add(new ImageInfo(title, previewUrl, imageSourceUrl));
        }
        return imageInfos;
    }


    class PageInfoLoad extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground: 加载页面链接：" + strings[0]);
            String body = MyOKhttp.httpRequest(strings[0]);
            return body;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null && !s.equals("")) {
                ydRecyclerAdapter.loadMoreComplete();
                ydRecyclerAdapter.addData(pageDataParse(s));
            } else {
                ydRecyclerAdapter.loadMoreFail();
            }
            task = null;
        }
    }

}
