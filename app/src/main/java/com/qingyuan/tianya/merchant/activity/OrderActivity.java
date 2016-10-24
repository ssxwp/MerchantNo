package com.qingyuan.tianya.merchant.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.qingyuan.tianya.merchant.R;
import com.qingyuan.tianya.merchant.fragment.OrderFragment;
import com.qingyuan.tianya.merchant.fragment.ValidateFragment;

public class OrderActivity extends BaseActivity {
    private Fragment fragment_order;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        addActivity(this);
        initView();
        initData();
        initClick();
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
        if (fragment_order == null) {
            fragment_order = new OrderFragment();
        }
        switchContent(fragment_order);
    }

    private void switchContent(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        hideFragments(ft);
        if (!fragment.isAdded()) {
            ft.add(R.id.ac_order_fram, fragment);
        }
        ft.show(fragment);
        ft.commit();
    }

    private void hideFragments(FragmentTransaction ft) {
        if (fragment_order != null) {
            ft.hide(fragment_order);
        }
    }

    @Override
    public void initClick() {
    }

}
