package com.wzs.bottomtab.demo;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
/**
 * Fragment管理类
 * @author wu_zhongshan@163.com
 *
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter{
    ArrayList<Fragment> list;
    public MyFragmentPagerAdapter(FragmentManager fm,ArrayList<Fragment> list) {
        super(fm);
        this.list = list;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Fragment getItem(int arg0) {
        return list.get(arg0);
    }


}