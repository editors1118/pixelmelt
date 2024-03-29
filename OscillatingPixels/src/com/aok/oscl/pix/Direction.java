package com.aok.oscl.pix;

/**
 * ピクセルの進行方向
 * 
 * @author aokiissei
 * 
 */

public enum Direction {
	NONE(0), UP(1), RIGHT(2), DOWN(3), LEFT(4);

	Direction(final int _intValue) {
		intValue = _intValue;
	}

	public int getIntValue() {
		return intValue;
	}

	public static Direction valueOf(final int _intValue) {
		for (Direction d : values()) {
			if (d.getIntValue() == _intValue) {
				return d;
			}
		}
		return null;
	}

	private int intValue;
}
