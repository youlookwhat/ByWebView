package me.jingbin.web;

/**
 * Created by jingbin on 2020/6/30.
 */
public abstract class OnTitleProgressCallback {

    /**
     * @param title 返回的标题
     */
    public void onReceivedTitle(String title) {

    }

    /**
     * @param newProgress 返回的进度
     */
    public void onProgressChanged(int newProgress) {

    }

    /**
     * 全屏显示时处理横竖屏。
     * 默认返回false，全屏时为横屏，全屏还原后为竖屏
     * 如果要手动处理，需要返回true！
     *
     * @param isShow 是否显示了全屏视频 true点击了全屏显示，false全屏视频还原
     */
    public boolean onHandleScreenOrientation(boolean isShow) {
        return false;
    }
}
