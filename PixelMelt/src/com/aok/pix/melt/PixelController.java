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
		if (canRemove(main.pixels[x + y * cols])) {
			main.pixels[x + y * cols] = Global.blankColor;
		}
	}

	/**
	 * ピクセルを動かす
	 */
	public void update() {

		main.loadPixels();

		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				bookNextLocation(i, j);
			}
		}

		int to = -1;
		for (int i = 0; i < cols; i++) {
			for (int j = 0; j < rows; j++) {
				// Global.rMap[to] = fromという対応関係
				to = i + j * cols;
				// 予約マップに予約が無かったり、前と同じ場所を予約してある場合はピクセルを移動させる必要がないので移動処理を行わない
				if (isBlank(i, j) || Global.rMap[to] == to) {
					Global.rMap[to] = -1; // 使い終わった予約マップの初期化
					return;
				}
				movePixel(Global.rMap[to], to);
				Global.rMap[to] = -1; // 使い終わった予約マップの初期化
			}
		}

		main.updatePixels();

	}

	/**
	 * ピクセルの色をコピー
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
	private void copyPixel(int from, int to) {

		main.pixels[to] = main.pixels[from];
	}

	/**
	 * ピクセルを移動
	 * 
	 * @param from
	 *            移動前のピクセルの配列番号
	 * @param to
	 *            移動後のピクセルの配列番号
	 */
	private void movePixel(int from, int to) {
		// ピクセルの値をコピー
		copyPixel(from, to);
		// ピクセルの移動前の場所を空ピクセルに
		setBlank(from);
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
		// 方向マップを移動後の状態に更新
		updateDMap(x1, y1, x2, y2);
		// 状態マップを移動後の状態に更新
		updateSMap(x1, y1, x2, y2);
		int from = x1 + y1 * cols;
		int to = x2 + y2 * cols;
		Global.rMap[to] = from;
	}

	/**
	 * ピクセルの状態マップをピクセルの状態に合わせて更新
	 * 
	 * @param x1
	 *            移動前のピクセルx座標
	 * @param y1
	 *            移動前のピクセルy座標
	 * @param x2
	 *            移動後のピクセルx座標
	 * @param y2
	 *            移動後のピクセルy座標
	 */
	private void updateDMap(int x1, int y1, int x2, int y2) {
		Global.dMap[x2][y2] = Global.dMap[x1][y1];
		Global.dMap[x1][y1] = Direction.NONE;
	}

	/**
	 * ピクセルの状態マップをピクセルの状態に合わせて更新
	 * 
	 * @param x1
	 *            移動前のピクセルx座標
	 * @param y1
	 *            移動前のピクセルy座標
	 * @param x2
	 *            移動後のピクセルx座標
	 * @param y2
	 *            移動後のピクセルy座標
	 */
	private void updateSMap(int x1, int y1, int x2, int y2) {
		Global.sMap[x2][y2] = Global.sMap[x1][y1];
		Global.sMap[x1][y1] = State.IDLE;
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
			return;
		} else {
			State st = updateState(x, y);

			switch (st) {
			case IDLE:
				reservePixel(x, y, x, y);
				break;
			case UP:
				reservePixel(x, y, x, y - 1);
				break;
			case DOWN:
				reservePixel(x, y, x, y + 1);
				break;
			case LEFT:
				reservePixel(x, y, x - 1, y);
				break;
			case RIGHT:
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
		stateNum = getNewState(d.getIntValue(), sign, x, y, 1);

		// 状態マップを更新
		State st = State.valueOf(stateNum);
		Global.sMap[x][y] = st;

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
		int count = _count;
		if (count > 4) {
			Global.dMap[x][y] = Utils.getRandomDirection();
			return 0;
		}
		count++;
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
			Global.dMap[x][y] = Direction.valueOf(_directionNum);
			return _directionNum;
		} else {
			return getNewState(newDirectionNum(_directionNum + 1 * _sign),
					_sign, x, y, count);
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
			n = 4;
		} else if (n > 4) {
			n = 1;
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
		if (isInBounds(dx, dy)) {
			if (isBlank(dx, dy)) {
				return true;
			}
		}
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
		if (Global.rMap[loc] == -1) {
			return true;
		}
		return false;
	}
}
