package com.wzs.bottomtab.demo;

import android.content.Context;
import android.graphics.Typeface;

/**
 * fontawesome帮助类，用于导入fontawesome字体库
 * @author wu_zhongshan@163.com
 *
 */
public class FontManager {
    public static final String ROOT = "fonts/";
    public static final String FONTAWESOME=ROOT +"fontawesome-webfont.ttf";
    public static Typeface getTypeface(Context context, String font) {
        return Typeface.createFromAsset(context.getAssets(), font);
    }
}
