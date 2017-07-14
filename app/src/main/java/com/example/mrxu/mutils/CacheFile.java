package com.example.mrxu.mutils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;

public class CacheFile {
	public static final String AUTH1 = "photo_front.jpg";
	public static final String AUTH2 = "photo_reverse.jpg";
	public static final String AUTH3 = "photo_sence.jpg";
	public static final String SIGN = "sign.png";

	public static ArrayList<File> getFileList(Context context) {
		ArrayList<File> files = new ArrayList<File>();
		files.add(new File(context
				.getExternalFilesDir(Environment.DIRECTORY_PICTURES), AUTH1));
		files.add(new File(context
				.getExternalFilesDir(Environment.DIRECTORY_PICTURES), AUTH2));
		files.add(new File(context
				.getExternalFilesDir(Environment.DIRECTORY_PICTURES), AUTH3));
		files.add(new File(context.getFilesDir(), CacheFile.SIGN));
		return files;
	}
}
