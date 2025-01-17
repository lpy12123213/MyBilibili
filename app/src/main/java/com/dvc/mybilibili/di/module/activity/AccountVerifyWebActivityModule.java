package com.dvc.mybilibili.di.module.activity;

import android.arch.lifecycle.Lifecycle;
import android.support.v7.app.AppCompatActivity;

import com.dvc.base.di.PerFragment;
import com.dvc.mybilibili.mvp.ui.activity.web.AccountVerifyWebActivity;
import com.dvc.mybilibili.mvp.ui.fragment.web.GuideFragment;
import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle;
import com.trello.rxlifecycle2.LifecycleProvider;

import dagger.Module;
import dagger.Provides;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class AccountVerifyWebActivityModule {

    @PerFragment
    @ContributesAndroidInjector
    abstract GuideFragment guideFragment();

    @Provides
    static AppCompatActivity provideAppComatActivity(AccountVerifyWebActivity activity) {
        return activity;
    }

    @Provides
    static LifecycleProvider<Lifecycle.Event> provideLifecycleProvider(AppCompatActivity mAppCompatActivity) {
        return AndroidLifecycle.createLifecycleProvider(mAppCompatActivity);
    }
}
