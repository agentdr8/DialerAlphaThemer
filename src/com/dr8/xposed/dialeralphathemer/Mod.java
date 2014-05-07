package com.dr8.xposed.dialeralphathemer;

import com.dr8.xposed.dialeralphathemer.R;

import android.content.res.XModuleResources;
import android.content.res.XResources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam;

public class Mod implements IXposedHookZygoteInit, IXposedHookInitPackageResources {
	private static String MODULE_PATH = null;
	private static String targetpkg = "com.htc.htcdialer";
	private static XSharedPreferences pref;

	private void retrieveDrawable(String pkg, final String replacement, Integer draw, XModuleResources modRes, InitPackageResourcesParam resparam, final XSharedPreferences prefs) {
		final Bitmap bm = BitmapFactory.decodeResource(modRes, draw);
		bm.setDensity(Bitmap.DENSITY_NONE);
		resparam.res.setReplacement(pkg, "drawable", replacement, new XResources.DrawableLoader() {
			@Override
			public Drawable newDrawable(XResources res, int id) throws Throwable {
				XposedBridge.log("colorizing image " + replacement);
				BitmapDrawable wd = new BitmapDrawable(null, bm);
				wd.setColorFilter(prefs.getInt("color", 0xffffffff), PorterDuff.Mode.MULTIPLY);
				return wd;
			}
		});
	}

	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		MODULE_PATH = startupParam.modulePath;
		pref = new XSharedPreferences("com.dr8.xposed.dialeralphathemer", "com.dr8.xposed.dialeralphathemer_preferences");
		pref.reload();
		XResources.setSystemWideReplacement("com.htc", "color", "tabfont_color", pref.getInt("color", 0xFFFFFFFF));
	}

	@Override
	public void handleInitPackageResources(InitPackageResourcesParam resparam) throws Throwable {
		XModuleResources modRes = XModuleResources.createInstance(MODULE_PATH, resparam.res);
		pref = new XSharedPreferences("com.dr8.xposed.dialeralphathemer", "com.dr8.xposed.dialeralphathemer_preferences");
		pref.reload();

		if (resparam.packageName.equals(targetpkg)) {
			retrieveDrawable(targetpkg, "dialer_icon_plus", R.drawable.dialer_icon_plus, modRes, resparam, pref);
			retrieveDrawable(targetpkg, "automotive_dialer_icon_plus", R.drawable.automotive_dialer_icon_plus, modRes, resparam, pref);
			retrieveDrawable(targetpkg, "dialer_icon_plus_l", R.drawable.dialer_icon_plus_l, modRes, resparam, pref);
			retrieveDrawable(targetpkg, "dialer_icon_record", R.drawable.dialer_icon_record, modRes, resparam, pref);
			retrieveDrawable(targetpkg, "automotive_dialer_icon_record", R.drawable.automotive_dialer_icon_record, modRes, resparam, pref);
			retrieveDrawable(targetpkg, "dialer_icon_record_l", R.drawable.dialer_icon_record_l, modRes, resparam, pref);
		}

		if (resparam.packageName.equals("com.android.phone")) {
			retrieveDrawable("com.android.phone", "dialer_icon_plus", R.drawable.dialer_icon_plus, modRes, resparam, pref);
			retrieveDrawable("com.android.phone", "dialer_icon_record", R.drawable.dialer_icon_record, modRes, resparam, pref);
		}

		if (!resparam.packageName.equals(targetpkg)) {
			return;
		}

	}
}


