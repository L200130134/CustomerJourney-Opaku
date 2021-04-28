package com.rikyahmadfathoni.test.opaku.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class FragmentAdapter extends FragmentStateAdapter {

    private final ArrayList<Fragment> fragments = new ArrayList<>();

    public FragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public FragmentAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public FragmentAdapter addFragment(Fragment fragment) {
        return addFragment(fragment, false);
    }

    public FragmentAdapter addFragment(Fragment fragment, boolean notify) {
        fragments.add(fragment);
        if (notify) {
            notifyItemInserted(fragments.size() -1);
        }
        return this;
    }

    public FragmentAdapter updateFragment(Fragment fragment, int position) {
        fragments.set(position, fragment);
        notifyItemChanged(position);
        return this;
    }

    public FragmentAdapter removeFragment(Fragment fragment) {
        return removeFragment(fragment, false);
    }

    public FragmentAdapter removeFragment(Fragment fragment, boolean notify) {
        final int index = fragments.indexOf(fragment);
        fragments.remove(fragment);
        if (notify && index >= 0) {
            notifyItemRemoved(index);
        }
        return this;
    }

    public Fragment getFragment(int position) {
        return fragments.get(position);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return getFragment(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
