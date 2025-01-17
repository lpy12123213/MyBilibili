package com.dvc.mybilibili.mvp.presenter.fragment;

import android.arch.lifecycle.Lifecycle;
import android.content.Context;

import com.dvc.base.di.ApplicationContext;
import com.dvc.base.utils.RxSchedulersHelper;
import com.dvc.mybilibili.app.retrofit2.callback.ObserverCallback;
import com.dvc.mybilibili.mvp.model.DataManager;
import com.dvc.mybilibili.mvp.model.api.exception.BiliApiException;
import com.dvc.mybilibili.mvp.model.api.service.bililive.beans.BiliLiveHomePage;
import com.dvc.mybilibili.mvp.presenter.MyMvpBasePresenter;
import com.dvc.mybilibili.mvp.ui.fragment.home.LiveFragView;
import com.trello.rxlifecycle2.LifecycleProvider;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.functions.Consumer;

public class LiveFragPresenter extends MyMvpBasePresenter<LiveFragView> {

    @Inject
    public LiveFragPresenter(@ApplicationContext Context context, DataManager dataManager, LifecycleProvider<Lifecycle.Event> provider) {
        super(context, dataManager, provider);
    }

    public void loadData(int relation_page, int rec_page) {
        this.apiHelper.getLiveHomePageData(relation_page, rec_page, 0)
                .compose(RxSchedulersHelper.ioAndMainThread())
                .compose(provider.bindUntilEvent(Lifecycle.Event.ON_PAUSE))
                .subscribe(new ObserverCallback<BiliLiveHomePage>() {
                    @Override
                    public void onSuccess(BiliLiveHomePage biliLiveHomePage) {
                        ifViewAttached(view -> view.loadDataCompleted(biliLiveHomePage));
                    }

                    @Override
                    public void onError(BiliApiException apiException, int code) {
                        ifViewAttached(view -> view.loadDataFailed(apiException));
                    }
                });
    }

    public void refreshModuleData(int module_id, String attention_room_id, int page, Consumer<BiliLiveHomePage.ModuleRooms> consumer) {
        this.apiHelper.getLiveHomeModuleData(module_id, attention_room_id, page, 0)
                .compose(RxSchedulersHelper.ioAndMainThread())
                .compose(provider.bindUntilEvent(Lifecycle.Event.ON_PAUSE))
                .subscribe(new ObserverCallback<BiliLiveHomePage.ModuleRooms>() {
                    @Override
                    public void onSuccess(BiliLiveHomePage.ModuleRooms moduleRooms) throws IOException {
                        try {
                            consumer.accept(moduleRooms);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(BiliApiException apiException, int code) {
                        try {
                            consumer.accept(null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
