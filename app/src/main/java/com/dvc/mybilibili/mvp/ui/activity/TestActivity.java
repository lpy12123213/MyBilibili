package com.dvc.mybilibili.mvp.ui.activity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.airbnb.lottie.LottieAnimationView;
import com.dvc.mybilibili.R;
import com.dvc.mybilibili.player.BiliVideoPlayer;
import com.shuyu.gsyvideoplayer.GSYVideoManager;
import com.shuyu.gsyvideoplayer.utils.Debuger;
import com.shuyu.gsyvideoplayer.utils.OrientationUtils;
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TestActivity extends AppCompatActivity {

    @BindView(R.id.animation_view)
    LottieAnimationView lottieAnimationView;
    @BindView(R.id.bilivideoplayer)
    StandardGSYVideoPlayer biliVideoPlayer;
//    @BindView(R.id.animation_view2)
//    SVGAImageView svgaImageView;

    OrientationUtils orientationUtils;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bili_app_activity_test);
        ButterKnife.bind(this);
        lottieAnimationView.playAnimation();
//        svgaImageView.startAnimation();
        String source1 = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4";
        biliVideoPlayer.setUp(source1, true, "测试视频");
//        biliVideoPlayer.setFristStart(v ->
//                biliVideoPlayer.startPlayLogic());
        biliVideoPlayer.startPlayLogic();
        Debuger.enable();

        //外部辅助的旋转，帮助全屏
        orientationUtils = new OrientationUtils(this, biliVideoPlayer);
        //初始化不打开外部的旋转
        orientationUtils.setEnable(false);
        biliVideoPlayer.getFullscreenButton().setOnClickListener(v -> {
            //直接横屏
            orientationUtils.resolveByClick();
            //第一个true是否需要隐藏actionbar，第二个true是否需要隐藏statusbar
            biliVideoPlayer.startWindowFullscreen(TestActivity.this, true, true);
        });
    }

//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        //如果旋转了就全屏
//        if (biliVideoPlayer.isInPlayingState()) {
//            biliVideoPlayer.onConfigurationChanged(this, newConfig, orientationUtils, true, true);
//        }
//    }

    @Override
    public void onBackPressed() {
        if (GSYVideoManager.backFromWindowFull(this)) {
            return;
        }
        super.onBackPressed();
    }
}