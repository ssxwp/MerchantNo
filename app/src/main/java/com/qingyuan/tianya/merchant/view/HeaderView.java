package com.qingyuan.tianya.merchant.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qingyuan.tianya.merchant.R;


/**
 * @author gaoyanjun
 *
 */
public class HeaderView extends LinearLayout {
	private ViewGroup leftLayout;
	private ViewGroup rightLayout;
//	private ViewGroup middleLayout;
	private ImageView leftImage;
	private TextView leftText;
	private TextView rightText;
	private TextView midText;
	private ImageView rightPic;
	private ViewGroup delegate;
	private LinearLayout ll_left_layout;
	private RelativeLayout head_rel;


	public HeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		delegate = (ViewGroup) LayoutInflater.from(context).inflate(
				R.layout.header_view, null);
		leftLayout = (ViewGroup) delegate.findViewById(R.id.header_left_layout);
		rightLayout = (ViewGroup) delegate.findViewById(R.id.header_right_layout);
		ll_left_layout = (LinearLayout) delegate.findViewById(R.id.ll_left_layout);
//		middleLayout = (ViewGroup) delegate.findViewById(R.id.header_middle_view);
		leftImage = (ImageView) delegate.findViewById(R.id.header_left_arrow);
		leftText=(TextView) delegate.findViewById(R.id.tv_left);
		rightText = (TextView) delegate.findViewById(R.id.titlelog);
		midText = (TextView) delegate.findViewById(R.id.middleText);
		rightPic = (ImageView) delegate.findViewById(R.id.header_right_pic);
		head_rel = (RelativeLayout)delegate.findViewById(R.id.head_rel);
		addView(delegate);
		TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.HeaderView);
		leftImage.setImageResource(typedArray.getResourceId(R.styleable.HeaderView_headerViewleftImage,R.mipmap.titlb));

//		if (typedArray.hasValue(R.styleable.HeaderView_rightText)) {
//			rightText.setText(typedArray.getString(R.styleable.HeaderView_rightText));
//			rightText.setVisibility(View.VISIBLE);
//			rightLayout.setVisibility(View.VISIBLE);
//		}
		
		if (typedArray.hasValue(R.styleable.HeaderView_rightText)) {
			rightText.setText(typedArray.getString(R.styleable.HeaderView_rightText));
			rightText.setVisibility(View.VISIBLE);
			rightLayout.setVisibility(View.VISIBLE);
		}
		
		if (typedArray.hasValue(R.styleable.HeaderView_headerViewleftImage)) {
			leftLayout.setVisibility(View.VISIBLE);
		}

		if (typedArray.hasValue(R.styleable.HeaderView_middleTextSize)) {
			// midText.setTextSize(TypedValue.COMPLEX_UNIT_DIP,
			// typedArray.getDimension(R.styleable.HeaderView_middleTextSize,
			// 20));
		}
		midText.setTextColor(getResources().getColor(typedArray.getResourceId(R.styleable.HeaderView_middleTextColor,
								(R.color.white))));
		midText.setText(typedArray.getString(R.styleable.HeaderView_middleText));
		ll_left_layout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				if (getContext() instanceof Activity) {
					((Activity) getContext()).onBackPressed();
				}
			}
		});
		typedArray.recycle();
	}

	public void setLeftOnClickListener(OnClickListener leftOnClickListener) {
		leftLayout.setVisibility(View.VISIBLE);
//		leftImage.setOnClickListener(leftOnClickListener);
		ll_left_layout.setOnClickListener(leftOnClickListener);
	}
	public RelativeLayout getHeadBackGround(){
		return head_rel;
	}
	public void setRightText(CharSequence content) {
		rightText.setVisibility(View.VISIBLE);
		rightLayout.setVisibility(View.VISIBLE);
		rightText.setText(content);
	}
	
//	public void setRightOnClickListener(OnClickListener rightOnClickListener) {
//		rightLayout.setVisibility(View.VISIBLE);
//		rightText.setOnClickListener(rightOnClickListener);
//	}

	public ViewGroup getLeftLayout() {
		return leftLayout;
	}
	
	
	public TextView getLeftTextView() {
		leftText.setVisibility(View.VISIBLE);
		return leftText;
	}
	
	public ImageView getLeftImageView() {
		return leftImage;
	}

	public TextView getMidTextView() {
		return midText;
	}

	public TextView getRightText() {
		return rightText;
	}

	public ImageView getRightPic() {
		rightPic.setVisibility(View.VISIBLE);
		rightText.setVisibility(View.GONE);
		return rightPic;
	}
	
	public LinearLayout getLeftLay() {
		return ll_left_layout;
	}
	

}
