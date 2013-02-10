package com.aok.oscl.pix;

import processing.core.PApplet;

public class MainProgram extends PApplet {

	private static final long serialVersionUID = 1L;

	/*
	 * instance variables
	 */

	public PixelController PC = null;

	/**
	 * 初期化
	 */
	public void setup() {
		size(Global.cols, Global.rows);
		colorMode(RGB, 255, 255, 255);
		background(0);
		smooth();

		Utils.init(this);
		PC = PixelController.getInstance();
		PC.initialize();
	}

	/**
	 * 描画ループ
	 */
	public void draw() {
		PC.update(); // 次のピクセル位置を計算
	}

	public void keyPressed() {
		if (key == ' ') {
			save("pixel_melt_result.png");
		}
		switch (key) {
		case ' ':
			save("pixel_melt_result.png");
			break;
		case 's':
			Global.blankFlag = !Global.blankFlag;
			break;
		}
	}

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "OscillatingPixels" });
	}
}
