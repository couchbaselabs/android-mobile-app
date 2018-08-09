package com.couchbase.couchbase;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LayoutHelper{
    private Context context;

    public LayoutHelper(Context context){
        this.context = context;
    }

    public void setTextView(TextView textView, LinearLayout layout, String text, int textSize, int width, int height, int paddingLeft, int paddingTop, int paddingRight, int paddingBot){
        textView.setText(text);
        textView.setTextSize(textSize);
        textView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBot);
        textView.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        layout.addView(textView);
    }

    public void setWeightedTextView(TextView textView, LinearLayout layout, String text, int textSize, int width, int height, int paddingLeft, int paddingTop, int paddingRight, int paddingBot, float weight){
        textView.setText(text);
        textView.setTextSize(textSize);
        textView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBot);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        layoutParams.weight = weight;
        textView.setLayoutParams(layoutParams);
        layout.addView(textView);
    }

    public void setDividerView(View divider, LinearLayout layout){
        divider.setLayoutParams(new LinearLayout.LayoutParams(-1, 1));
        divider.setBackgroundColor(context.getResources().getColor(R.color.colorDivider));
        layout.addView(divider);
    }

    public void setLayout(LinearLayout layout, LinearLayout parent, int orientation, int width, int height, int gravity, int weightsum){
        layout.setOrientation(orientation);
        layout.setGravity(gravity);
        layout.setWeightSum(weightsum);
        layout.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        parent.addView(layout);
    }

    public void setWeightedLayout(LinearLayout layout, LinearLayout parent, int orientation, int width, int height, int gravity, float weight){
        layout.setOrientation(orientation);
        layout.setGravity(gravity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        layoutParams.weight = weight;
        layout.setLayoutParams(layoutParams);
        parent.addView(layout);
    }

    public void setCardLayout(LinearLayout layout, CardView parent, int orientation, int width, int height, int gravity, int weightsum){
        layout.setOrientation(orientation);
        layout.setGravity(gravity);
        layout.setWeightSum(weightsum);
        layout.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        parent.addView(layout);
    }

    public void setWeightedCardLayout(LinearLayout layout, CardView parent, int orientation, int width, int height, int gravity, float weight){
        layout.setOrientation(orientation);
        layout.setGravity(gravity);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        layoutParams.weight = weight;
        layout.setLayoutParams(layoutParams);
        parent.addView(layout);
    }

    public void setCard(CardView layout, LinearLayout parent, int width, int height){
        layout.setRadius(5);
        layout.setCardElevation(2);
        layout.setLayoutParams(new LinearLayout.LayoutParams(width, height));
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) layout.getLayoutParams();
        layoutParams.setMargins(40, 20, 40, 20);
        layout.requestLayout();
        parent.addView(layout);
    }

    public void setImageView(LinearLayout layout, ImageView imageView, Context context, int source, int width, int height, int paddingLeft, int paddingTop, int paddingRight, int paddingBot){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
        imageView.setLayoutParams(layoutParams);
        imageView.setPadding(paddingLeft, paddingTop, paddingRight, paddingBot);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageView.setImageDrawable(context.getResources().getDrawable(source, context.getTheme()));
        } else {
            imageView.setImageDrawable(context.getResources().getDrawable(source));
        }
        layout.addView(imageView);
    }
}
