package com.aok.pix.melt;

public class PixelController {

	/*
	 * instance variables
	 */
	private MainProgram main = null;
	private int cols;
	private int rows;

	/*
	 * Singleton Pattern
	 */

	private static PixelController instance = null;

	private PixelController() {
		main = Global.main;
		cols = main.width;
		rows = main.height;
	}

	public static PixelController getInstance() {
		if (instance == null) {
			instance = new PixelController();
		}
		return instance;
	}

	/*
	 * method
	 */

	/**
	 * ピクセルを動かすための準備
	 */
	public void initialize() {
		main.image(Global.img, 0, 0); // 画像の表示
		main.loadPixels();
		removePixels(); // 空ピクセルを作る
		Utils.initMaps(); // ピクセルの状態の初期化
		main.updatePixels();
	}

	/**
	 * ピクセルを動かすために 閾値以下の明るさのピクセルを ピクセルが空である事を示す色に 置き換える。
	 */
	private void removePixels() {
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				makeBlank(i, j);
			}
		}
	}

	/**
	 * @param _color
	 *            ピクセルの色
	 * @return　 画像のピクセルを空のピクセルと置き換えてよいならtrue
	 */
	private boolean canRemove(int _color) {
		// memo: brightness is 0 to 255
		int brightness = (int) main.brightness(_color);

		if (brightness < 5) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 指定されたピクセルを空ピクセルに置き換える
	 * 
	 * @param x
	 *            ピクセルのx座標
	 * @param y
	 *            ピクセルのy座標
	 */
	private void setBlank(int loc) {
		if (!Global.blankFlag) {
			return;
		}
		main.pixels[loc] = Global.blankColor;
	}

	// 初期化用
	private void makeBlank(int x, int y) {
		// if (canRemove(main.pixels[x + y * cols])) {
		// main.pixels[x + y * cols] = Global.blankColor;
		// }
		float seed = (float) Math.random();
		if (seed < 0.1f) {
			main.pixels[x + y * cols] = Global.blankColor;
		}
	}

	/**
	 * ピクセルを動かす
	 */
	public void update() {
		// System.out.println("****** update ******");
		Global.blankCount = 0;
		Global.NoneCount = 0;
		main.loadPixels();

		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				bookNextLocation(i, j);
			}
		}

		// System.out.println("bookNextLocation Done!");

		int to = -1;
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				// Global.rMap[to] = fromという対応関係
				to = i + j * cols;

				main.pixels[to] = Global.rMap[to];
			}
		}
		main.updatePixels();
		System.out.println("Global.NoneCount = " + Global.NoneCount);
		System.out.println("Global.blankCount =" + Global.blankCount);

	}

	/**
	 * 次の場所を予約
	 * 
	 * @param x1
	 *            移動前のピクセルx座標
	 * @param y1
	 *            移動前のピクセルy座標
	 * @param x2
	 *            移動先のピクセルx座標
	 * @param y2
	 *            移動先のピクセルy座標
	 */
	private void reservePixel(int x1, int y1, int x2, int y2) {

		int from = x1 + y1 * cols;
		int to = x2 + y2 * cols;
		// System.out.println("******** reservePixel *********");
		// System.out.println("(from, to) = (" + from + "," + to + ")");
		Global.rMap[to] = main.pixels[from];
		Global.rMap[from] = Global.blankColor;
		setBlank(from);
	}

	private void reservePixel(int x, int y) {
		int loc = x + y * cols;
		Global.rMap[loc] = main.pixels[loc];
		setBlank(loc);
	}

	/**
	 * ピクセルを動かす
	 * 
	 * @param x
	 *            ピクセルのx座標
	 * @param y
	 *            ピクセルのy座標
	 */
	private void bookNextLocation(int x, int y) {
		int loc = x + y * cols;
		int p = main.pixels[loc];
		if (p == Global.blankColor) {
			// blank pixel
			Global.blankCount++;
			return;
		} else {
			State st = updateState(x, y);

			switch (st) {
			case IDLE:
				Global.sMap[x][y] = st;
				reservePixel(x, y);
				break;
			case UP:
				Global.sMap[x][y - 1] = st;
				reservePixel(x, y, x, y - 1);
				break;
			case DOWN:
				Global.sMap[x][y + 1] = st;
				reservePixel(x, y, x, y + 1);
				break;
			case LEFT:
				Global.sMap[x - 1][y] = st;
				reservePixel(x, y, x - 1, y);
				break;
			case RIGHT:
				Global.sMap[x + 1][y] = st;
				reservePixel(x, y, x + 1, y);
				break;
			}
		}
	}

	/**
	 * ピクセルの次の状態を生成
	 * 
	 * @param x
	 *            ピクセルのx座標
	 * @param y
	 *            ピクセルのy座標
	 * @return 次の状態
	 */
	private State updateState(int x, int y) {
		/*
		 * 
		 * direction(int) means direction from the current pixel
		 * 
		 * UP: 1 RIGHT: 2 DOWN: 3 LEFT: 4
		 * 
		 * direction++: clockwise move direction--: counterclockwise move
		 */
		int sign; // 周りにからのピクセルがあるかどうかをチェックする回転方向
		int loc = x + y * cols; // ピクセルの場所

		// ピクセルの色の値から回転方向を決定
		if (isClockwise(main.pixels[loc])) {
			sign = 1;
		} else {
			sign = -1;
		}

		// ピクセルの状態を決定
		int stateNum = 0;
		Direction d = Global.dMap[x][y];
		// System.out.println("Direction d =" + d);
		stateNum = getNewState(d.getIntValue(), sign, x, y, 1);
		// System.out.println("Global.dMap[x][y] = " + Global.dMap[x][y]);

		// 状態マップを更新
		State st = State.valueOf(stateNum);
		// System.out.println("State st = " + st);
		// System.out.println("******** updateState *********");
		// System.out.println("(x, y) = (" + x + "," + y + ")");
		// System.out.println("st = " + st);

		// 次の状態を返す
		return st;
	}

	/**
	 * 次の状態を生成
	 * 
	 * @param _directionNum
	 *            今の方向番号
	 * @param _sign
	 *            回転方向
	 * @param _nx
	 *            ピクセルのx座標
	 * @param _ny
	 *            ピクセルのy座標
	 * @param _count
	 *            今までに方向を生成した回数（4で終了）
	 * @return 次の状態
	 */
	private int getNewState(int _directionNum, int _sign, int x, int y,
			int _count) {

		if (_directionNum == Direction.NONE.getIntValue()) {
			Global.NoneCount++;
		}

		int count = _count;
		if (count > 4) {
			Global.dMap[x][y] = Utils.getRandomDirection();
			return 0;
		}

		int nextX = x;
		int nextY = y;
		switch (_directionNum) {
		case 0:
			break;
		case 1:
			// UP
			nextX = x;
			nextY = y - 1;
			break;
		case 2:
			// RIGHT
			nextX = x + 1;
			nextY = y;
			break;
		case 3:
			// DOWN
			nextX = x;
			nextY = y + 1;
			break;
		case 4:
			// LEFT
			nextX = x - 1;
			nextY = y;
			break;
		}

		if (isSafe(nextX, nextY)) {
			Global.dMap[nextX][nextY] = Direction.valueOf(_directionNum);
			return _directionNum;
		} else {
			return getNewState(newDirectionNum(_directionNum + 1 * _sign),
					_sign, x, y, count + 1);
		}

	}

	/**
	 * 0~5の方向番号を1~4に訂正
	 * 
	 * @param 0~5の方向番号
	 * @return 1~4の方向番号
	 */
	private int newDirectionNum(int n) {
		if (n < 0) {
			n += 4;
		} else if (n > 4) {
			n -= 4;
		}

		return n;
	}

	/**
	 * ピクセルの彩度で回転方向を決定
	 * 
	 * @param p
	 *            ピクセル
	 * @return 回転方向が時計回りならtrue
	 */
	private boolean isClockwise(int p) {
		if (main.saturation(p) < 200) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * ピクセルを移動出来るかどうか
	 * 
	 * @param dx
	 *            行き先のピクセルx座標
	 * @param dy
	 *            行き先のピクセルy座標
	 * @return ピクセルを移動出来るならtrue
	 */
	private boolean isSafe(int dx, int dy) {
		// System.out.println("******** isSafe *********");
		if (isInBounds(dx, dy)) {
			// System.out.println("isInBounds!");
			if (isBlank(dx, dy)) {
				// System.out.println("isBlank!");
				// System.out.println("(x, y) = (" + dx + "," + dy + ")");
				// System.out.println("isSafe!");
				return true;
			}
			// System.out.println("isBlank!");
		}
		// System.out.println("isOutOfBounds!");
		return false;
	}

	/**
	 * ピクセルが画像内かどうか
	 * 
	 * @param dx
	 *            行き先のピクセルx座標
	 * @param dy
	 *            　行き先のピクセルy座標
	 * @return ピクセルが画像の範囲内ならtrue
	 */
	private boolean isInBounds(int dx, int dy) {
		if (dx >= 0 && dx < cols) {
			if (dy >= 0 && dy < rows) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 行き先のピクセルが予約されていないか
	 * 
	 * @param dx
	 *            行き先のピクセルのx座標
	 * @param dy
	 *            行き先のピクセルのy座標
	 * @return
	 */
	private boolean isBlank(int dx, int dy) {
		int loc = dx + dy * cols;
		if (Global.rMap[loc] == Global.blankColor) {
			return true;
		}
		return false;
	}
}
