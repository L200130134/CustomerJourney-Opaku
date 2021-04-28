package com.rikyahmadfathoni.test.opaku.model;

import androidx.fragment.app.Fragment;

public class FragmentModel {

    private final long id;
    private final Fragment fragment;

    public FragmentModel(long id, Fragment fragment) {
        this.id = id;
        this.fragment = fragment;
    }

    public long getId() {
        return id;
    }

    public Fragment getFragment() {
        return fragment;
    }
}
