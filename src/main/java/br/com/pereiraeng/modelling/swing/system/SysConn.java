package br.com.pereiraeng.modelling.swing.system;

import java.awt.Color;
import java.awt.Point;

public class SysConn {

	private final SysComp comp1, comp2;

	public SysConn(SysComp u1, SysComp u2) {
		this.comp1 = u1;
		this.comp2 = u2;
		u1.addConn(this);
		u2.addConn(this);
	}

	public SysComp getOtherComp(SysComp comp) {
		return comp1 == comp ? comp2 : comp1;
	}

	public boolean is1(SysComp comp) {
		return comp1 == comp;
	}

	@Override
	public String toString() {
		return String.format("%s<->%s", comp1.getNome(), comp2.getNome());
	}

	// ----------------------- DRAWER ----------------------------

	private transient Color color;

	private transient boolean dashed;

	private transient Point p1, p2;

	public void setP(int x, int y, boolean p1) {
		if (p1)
			this.p1 = new Point(x, y);
		else
			this.p2 = new Point(x, y);
	}

	public Point getP(boolean p1) {
		if (p1)
			return this.p1;
		else
			return this.p2;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public void setDashed(boolean dashed) {
		this.dashed = dashed;
	}

	public boolean isDashed() {
		return dashed;
	}
}