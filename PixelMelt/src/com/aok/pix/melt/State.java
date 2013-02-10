package com.aok.pix.melt;

/**
 * ピクセルの今の状態。 止まっているか、上下左右に動いているか。
 * 
 * @author aokiissei
 * 
 */
public enum State {
	IDLE(0), UP(1), RIGHT(2), DOWN(3), LEFT(4);

	State(final int _intValue) {
		intValue = _intValue;
	}

	public int getIntValue() {
		return intValue;
	}

	public static State valueOf(final int _intValue) {
		for (State s : values()) {
			if (s.getIntValue() == _intValue) {
				return s;
			}
		}
		return null;
	}

	private int intValue;
}
