package com.letsdoitmacedonia.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.bumptech.glide.util.Util;
import com.letsdoitmacedonia.R;
import com.letsdoitmacedonia.adapters.NewsAdapter;
import com.letsdoitmacedonia.interfaces.RetrofitInterface;
import com.letsdoitmacedonia.models.NewsList;
import com.letsdoitmacedonia.utils.Constants;
import com.letsdoitmacedonia.utils.Utils;
import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;


public class NewsInfo extends AppCompatActivity {

  @InjectView(R.id.toolbar_addItem)
  Toolbar toolbar;
  @InjectView((R.id.news_list))
  ListView newsList;

  private String category = "news";
  private NewsAdapter newsAdapter;

  @Override
  protected void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_news);
    ButterKnife.inject(this);

    // use toolbar as action bar
    setSupportActionBar(toolbar);

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    toolbar.setNavigationOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {

        onBackPressed();
      }
    });

    if (Utils.getSingleton().isOnline(this)) {

      if (Utils.getSingleton().checkLocale(this).equals("mk_MK")) {
        requestData(Constants.FULL_NEWS_MK);
      }
      else {
        requestData(Constants.FULL_NEWS_EN);
      }
    }
    else {
      Toast.makeText(this, R.string.no_internet, Toast.LENGTH_LONG).show();
    }

  }

  private void requestData(String uri) {

    OkHttpClient okHttpClient = new OkHttpClient();
    okHttpClient.setReadTimeout(60 * 1000, TimeUnit.MILLISECONDS);

    RestAdapter adapter = new RestAdapter.Builder()
            .setEndpoint(Constants.ENDOPOINT)
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setClient(new OkClient(okHttpClient))
            .build();

    RetrofitInterface api = adapter.create(RetrofitInterface.class);

    if (Utils.getSingleton().checkLocale(NewsInfo.this).equals("mk_MK")) {
      api.newsMk(category, new Callback<NewsList>() {

        @Override
        public void success(NewsList news, Response response) {

          newsAdapter = new NewsAdapter(NewsInfo.this, news.getPosts());
          newsList.setAdapter(newsAdapter);
          newsAdapter.notifyDataSetChanged();

        }

        @Override
        public void failure(RetrofitError error) {

          error.printStackTrace();
        }
      });
    }
    else {

      api.newsEn(category, new Callback<NewsList>() {

        @Override
        public void success(NewsList news, Response response) {

          newsAdapter = new NewsAdapter(NewsInfo.this, news.getPosts());
          newsList.setAdapter(newsAdapter);
          newsAdapter.notifyDataSetChanged();

        }

        @Override
        public void failure(RetrofitError error) {

          error.printStackTrace();
        }
      });
    }
  }
}
