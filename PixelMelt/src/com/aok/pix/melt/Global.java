package com.aok.pix.melt;

import processing.core.PImage;

public class Global {

	/*
	 * shared variables
	 */

	public static MainProgram main; // メイン
	public static Direction dMap[][]; // pixel direction map
	public static State sMap[][]; // pixel state map
	public static int rMap[]; // pixel reservation map
								// 移動先の場所を予約するマップ。移動先を予約する事で互いにぶつからないようにしている。
								// (x1,y1)→(x2,y2)の場合は
								// rMap[x2][y2] = x1 + y1 * cols となる。
	public static int blankColor; // ピクセルが無い(そこに隣のピクセルが移動出来る)事を示す色
	public static PImage img; // 表示する元画像
	public static boolean firstflag; // setup()の中では出来ない初期化処理をdraw()で行うためのフラグ
	// screen width
	public static final int cols = 500; // 横の画素数
	// screen height
	public static final int rows = 367; // 縦の画素数

	public static boolean blankFlag = true;
}
