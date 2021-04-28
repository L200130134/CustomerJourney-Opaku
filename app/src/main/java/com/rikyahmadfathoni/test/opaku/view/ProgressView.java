package com.rikyahmadfathoni.test.opaku.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.rikyahmadfathoni.test.opaku.databinding.ContentProgressBinding;

public class ProgressView {

    public ContentProgressBinding binding;

    public ProgressView(Context context) {
        this.binding = ContentProgressBinding.inflate(LayoutInflater.from(context));
    }

    public void show(@Nullable ViewGroup viewGroup) {
        if (viewGroup != null) {
            if (binding != null) {
                final FrameLayout content = binding.getRoot();
                final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                content.setLayoutParams(lp);
                content.requestLayout();
                viewGroup.addView(content);
            }
        }
    }

    public void dismiss() {
        if (binding != null) {
            final ViewParent parent = binding.getRoot().getParent();
            if (parent instanceof ViewGroup) {
                ((ViewGroup)parent).removeView(binding.getRoot());
            }
        }
    }

    public void removeInstance() {
        this.binding = null;
    }
}
