package br.com.pereiraeng.modelling.swing.system;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Float;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.Icon;

import br.com.pereiraeng.swing.LeafOM;
import br.com.pereiraeng.swing.interfaces.Click;
import br.com.pereiraeng.swing.interfaces.WL;

/**
 * Classe dos objetos que representam componentes do sistema
 * 
 * @author Philipe PEREIRA
 *
 */
public class SysComp extends ArrayList<SysConn> implements SysCp, Click {
	private static final long serialVersionUID = 1L;

	private String nome;

	private Icon icon;

	public SysComp(String nome, Icon icon) {
		this.nome = nome;
		if (icon != null) {
			this.icon = icon;
			this.dim = new Dimension(icon.getIconWidth(), icon.getIconHeight());
		} else
			this.dim = new Dimension(48, 48);
	}

	public String getNome() {
		return nome;
	}

	public void addConn(SysConn relation) {
		this.add(relation);
	}

	public SysConn getConn(String comp) {
		SysConn out = null;
		for (SysConn c : this) {
			if (comp.equals(c.getOtherComp(this).getNome())) {
				out = c;
				break;
			}
		}
		return out;
	}

	public SysConn removeConn(String comp) {
		SysComp other = null;
		SysConn out = null;
		for (SysConn c : this) {
			SysComp o = c.getOtherComp(this);
			if (comp.equals(o.getNome())) {
				other = o;
				out = c;
				break;
			}
		}
		this.remove(out);
		other.remove(out);
		return out;
	}

	// ------------------------ DRAWER ------------------------

	private boolean drawable = true;

	private WL wl;

	private Dimension dim;

	private Point2D.Float loc = new Point2D.Float((float) Math.random(), (float) Math.random());

	public void setLoc(float x, float y) {
		loc.setLocation(x, y);
	}

	@Override
	public void setDrawable(boolean drawable) {
		this.drawable = drawable;
	}

	@Override
	public boolean isDrawable() {
		return drawable;
	}

	@Override
	public void drawObject(Graphics2D g) {
		Point p = LeafOM.getTranformedPoint(loc.x, loc.y, wl);
		p.x -= dim.width / 2;
		p.y -= dim.height / 2;

		// conector
		for (SysConn r : this)
			draw(g, wl, this, r);

		// ícone
		if (icon != null)
			icon.paintIcon(null, g, p.x, p.y);
		else {
			g.drawRect(p.x, p.y, 48, 48);
//			g.drawString("?", p.x, p.y);
		}

		// nome
		g.drawString(nome, p.x, p.y);
	}

	private static void draw(Graphics2D g, WL wl, SysComp comp1, SysConn conn) {
		SysComp comp2 = conn.getOtherComp(comp1);

		if (!comp1.isDrawable() || !comp2.isDrawable())
			return;

		Point2D.Float l = comp1.getMin();
		Point p1 = LeafOM.getTranformedPoint(l.x, l.y, wl);

		l = comp2.getMin();
		Point p2 = LeafOM.getTranformedPoint(l.x, l.y, wl);

		// deslocamento do conector
		Point p = conn.getP(true);
		if (p != null) {
			if (conn.is1(comp1)) {
				p1.x += p.x;
				p1.y += p.y;
			} else {
				p2.x += p.x;
				p2.y += p.y;
			}
		}
		p = conn.getP(false);
		if (p != null) {
			if (conn.is1(comp1)) {
				p2.x += p.x;
				p2.y += p.y;
			} else {
				p1.x += p.x;
				p1.y += p.y;
			}
		}

		// só desenha metada do caminho: a outra metada que desenha é o outro campo (que
		// também tem uma referência, apontanda para esta)

		Color c = conn.getColor();
		if (c != null)
			g.setColor(c);

		boolean d = conn.isDashed();

		if (d)
			g.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[] { 5f }, 0f));

		float dx = p2.x - p1.x;
		boolean h;
		if (dx == 0)
			h = false;
		else
			h = Math.abs((p2.y - p1.y) / dx) < 1;

		if (h) {
			g.drawLine(p1.x, p1.y, (p2.x + p1.x) / 2, p1.y);
			g.drawLine((p2.x + p1.x) / 2, p1.y, (p2.x + p1.x) / 2, (p2.y + p1.y) / 2);
		} else {
			g.drawLine(p1.x, p1.y, p1.x, (p1.y + p2.y) / 2);
			g.drawLine(p2.x, (p1.y + p2.y) / 2, (p2.x + p1.x) / 2, (p2.y + p1.y) / 2);
		}

		if (d)
			g.setStroke(new BasicStroke());

		g.setColor(Color.BLACK);
	}

	@Override
	public void setWL(WL wl) {
		this.wl = wl;
	}

	@Override
	public boolean wasDrawn() {
		return true;
	}

	@Override
	public Float getMin() {
		return loc;
	}

	@Override
	public Float getMax() {
		return loc;
	}

	@Override
	public boolean isOn(int x, int y) {
		return getClickableArea().contains(x, y);
	}

	@Override
	public Area getClickableArea() {
		Point p = LeafOM.getTranformedPoint(loc.x, loc.y, wl);
		Rectangle2D.Float rect = new Rectangle2D.Float(p.x - dim.width / 2, p.y - dim.height / 2, dim.width,
				dim.height);
		return new Area(rect);
	}

	// ------------------------ AUXILIAR ------------------------

	public static SysComp getObj(String name, Collection<SysCp> scs) {
		for (SysCp sc : scs)
			if (sc instanceof SysComp)
				if (((SysComp) sc).getNome().equals(name))
					return (SysComp) sc;
		return null;
	}
}