package com.letsdoitmacedonia.adapters;


import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.letsdoitmacedonia.R;
import com.letsdoitmacedonia.models.News;
import com.letsdoitmacedonia.utils.Utils;

import java.util.List;


public class NewsAdapter extends BaseAdapter {

  Context context;
  private List<News> list;
  private Typeface typeFaceCustomFont;

  public  NewsAdapter(Context context, List<News> objects) {

    this.context = context;
    this.list = objects;
  }

  @Override
  public int getCount() {

    return list.size();
  }

  @Override
  public Object getItem(int position) {

    return list.get(position);
  }

  @Override
  public long getItemId(int position) {

    return position;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {

    ViewHolder holder;
    if (convertView == null) {
      holder = new ViewHolder();
      convertView = View.inflate(context, R.layout.items_news_list, null);
      holder.title = (TextView) convertView.findViewById(R.id.news_title);
      holder.newsInfo = (TextView) convertView.findViewById(R.id.news_info);
      holder.imageNews = (ImageView) convertView.findViewById(R.id.image_news);
      convertView.setTag(holder);
    }
    else {
      holder = (ViewHolder) convertView.getTag();
    }

    //set planer name
    News news = (News) getItem(position);

    holder.title.setText(news.getTitle());
    holder.title.setTypeface(Utils.getSingleton().setCustomFont(context));

    holder.newsInfo.setText(news.getContent());

    Glide.with(context)
            .load("http://ajdemakedonija.mk/" + news.getImage())
            .asBitmap()
            .into(holder.imageNews);

    return convertView;
  }

  public class ViewHolder {

    public TextView title;
    public TextView newsInfo;
    public ImageView imageNews;
  }
}
