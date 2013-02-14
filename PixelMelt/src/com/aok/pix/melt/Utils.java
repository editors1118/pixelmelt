package com.aok.pix.melt;

import processing.core.PApplet;

public class Utils {
	/**
	 * staticなmethod達
	 */
	/**
	 * アプリ全体の初期化
	 */

	public static void init(MainProgram _main) {
		Global.main = _main;
		initBlankColor(); // 色の初期化
		Global.img = _main.loadImage("test.jpg"); // 画像の読み込み
	}

	/**
	 * ピクセルが空である事を示す色を設定。 別に黒じゃなくても良い。
	 */
	private static void initBlankColor() {
		Global.blankColor = Global.main.color(0, 0, 255);
	}

	/**
	 * ２つのマップに適当な値を入れる
	 */
	public static void initMaps() {
		Global.dMap = new Direction[Global.cols][Global.rows];
		Global.sMap = new State[Global.cols][Global.rows];
		Global.rMap = new int[Global.cols * Global.rows];
		int loc;
		for (int i = 0; i < Global.cols; i++) {
			for (int j = 0; j < Global.rows; j++) {
				loc = i + j * Global.cols;
				// initialize reservation map
				Global.rMap[loc] = Global.blankColor;
				// initialize state map
				Global.sMap[i][j] = State.IDLE;
				// initialize direction map
				if (Global.main.pixels[loc] == Global.blankColor) {
					Global.dMap[i][j] = Direction.NONE;
				} else {
					Global.dMap[i][j] = getRandomDirection();
				}
			}
		}
	}

	/**
	 * ランダムな方向を生成
	 * 
	 * @return
	 */

	public static Direction getRandomDirection() {
		int xi = PApplet.ceil((float) (Math.random() * 4));

		Direction d = Direction.NONE;
		switch (xi) {
		case 1:
			d = Direction.UP;
			break;
		case 2:
			d = Direction.DOWN;
			break;
		case 3:
			d = Direction.LEFT;
			break;
		case 4:
			d = Direction.RIGHT;
			break;
		}
//		System.out.println("d = " + d);
		return d;
	}

	public static float customNoise(float range) {
		return Global.main.noise(range);
	}
}
