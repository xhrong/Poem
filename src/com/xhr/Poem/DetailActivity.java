package com.xhr.Poem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import com.xhr.Poem.model.PoemItem;
import com.xhr.Poem.view.ContentFragment;

import java.util.List;

/**
 * Created by xhrong on 2015/5/11.
 */
public class DetailActivity extends FragmentActivity {

    private ViewPager mPager;
    private int position;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);
        Intent intent = getIntent();
        if (intent == null) finish();
        position = intent.getIntExtra("position", -1);
        initViewPager();
    }


    /*
* 初始化ViewPager
*/
    private void initViewPager() {
        mPager = (ViewPager) findViewById(R.id.viewpager);

        //给ViewPager设置适配器
        mPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), AppState.getCurrentPoems()));
        mPager.setCurrentItem(position);//设置当前显示标签页为第一页
        mPager.setOffscreenPageLimit(2);
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });//页面变化时的监听器
    }


    public class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

        List<PoemItem> list;

        public MyFragmentPagerAdapter(FragmentManager fm, List<PoemItem> list) {
            super(fm);
            this.list = list;

        }

        @Override
        public void destroyItem(android.view.ViewGroup container, int position, java.lang.Object object) {

            super.destroyItem(container, position, object);

            Log.i("INFO", "Destroy Item...");
        }

        @Override
        public int getCount() {
            return list.size();
        }



        @Override
        public Fragment getItem(int position) {
            return ContentFragment.newInstance(list.get(position));
        }
    }
}
