package com.aok.pix.melt;

/**
 * ピクセルが画面端もしくは 他の空でないピクセルに突き当たった時の 曲がる方向。時計周りか反時計周り。
 * 
 * @author aokiissei
 * 
 */

public enum Rotation {
	CLOCKWISE(0), COUNTERCLOCKWISE(1);

	Rotation(final int _intValue) {
		intValue = _intValue;
	}

	public int getIntValue() {
		return intValue;
	}

	public static Rotation valueOf(final int _intValue) {
		for (Rotation r : values()) {
			if (r.getIntValue() == _intValue) {
				return r;
			}
		}
		return null;
	}

	private int intValue;
}
