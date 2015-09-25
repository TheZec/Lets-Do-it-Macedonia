package com.letsdoitmacedonia.adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.letsdoitmacedonia.R;
import com.letsdoitmacedonia.models.WastePoint;
import com.letsdoitmacedonia.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class MapAdapter implements GoogleMap.InfoWindowAdapter {


  Marker marketToRefresh;
  private View view;
  private List<WastePoint> wastePointList = new ArrayList<>();
  private Context context;

  public MapAdapter(Context context, List<WastePoint> wastePointList) {

    this.context = context;
    this.wastePointList = wastePointList;
    this.view = View.inflate(context, R.layout.custom_info_window, null);
  }

  @Override
  public View getInfoWindow(Marker marker) {

    marketToRefresh = marker;

    WastePoint wastePoint = null;
    for (WastePoint wastePoints : wastePointList) {

      try {
        if (marker.getPosition().latitude == Double.parseDouble(wastePoints.getLat())
                && marker.getPosition().longitude == Double.parseDouble(wastePoints.getLng())) {

          wastePoint = wastePoints;
          break;
        }
      }
      catch (NumberFormatException e) {
        e.printStackTrace();
      }

    }

    if (wastePoint == null) {
      return null;
    }

    // Getting reference to the TextView to set volume
    TextView volume = (TextView) view.findViewById(R.id.volume);

    // Getting reference to the TextView to set content
    TextView content = (TextView) view.findViewById(R.id.content);

    // Getting reference to the TextView to set description
    TextView description = (TextView) view.findViewById(R.id.description);

    // Getting reference to the TextView to set addedBy
    TextView addedBy = (TextView) view.findViewById(R.id.addedBy);

    // set text
    volume.setText(context.getString(R.string.volume) + " " + wastePoint.getVol() + "m3");
    volume.setTypeface(Utils.getSingleton().setCustomFont(context));

    content.setText(context.getString(R.string.content) + " " + wastePoint.getComp_name());
    content.setTypeface(Utils.getSingleton().setCustomFont(context));

    description.setText(context.getString(R.string.description) + " " + wastePoint.getDes());
    description.setTypeface(Utils.getSingleton().setCustomFont(context));

    addedBy.setText(context.getString(R.string.added_by) + " " + wastePoint.getName());
    addedBy.setTypeface(Utils.getSingleton().setCustomFont(context));

    Glide.with(context)
            .load("http://ajdemakedonija.mk/" + wastePoint.getImg())
            .asBitmap()
            .listener(new RequestListener<String, Bitmap>() {

              @Override
              public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {

                return false;
              }

              @Override
              public boolean onResourceReady(final Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {

                ((ImageView) view.findViewById(R.id.imageMarker)).setImageBitmap(resource);
                if (marketToRefresh != null && marketToRefresh.isInfoWindowShown()) {
                  marketToRefresh.hideInfoWindow();
                  marketToRefresh.showInfoWindow();
                }
                return true;
              }
            })
            .into((ImageView) view.findViewById(R.id.imageMarker));

    return view;

  }

  @Override
  public View getInfoContents(Marker marker) {

    return null;
  }
}

