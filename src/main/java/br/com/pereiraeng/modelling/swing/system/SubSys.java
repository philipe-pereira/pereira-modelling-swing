package br.com.pereiraeng.modelling.swing.system;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import br.com.pereiraeng.swing.LeafOM;
import br.com.pereiraeng.swing.interfaces.WL;

/**
 * Classe dos objetos que representam sub-sistemas (i.e., conjunto de
 * {@link SysComp componentes})
 * 
 * @author Philipe PEREIRA
 *
 */
public class SubSys extends ArrayList<SysComp> implements SysCp {
	private static final long serialVersionUID = 1L;

	private WL wl;

	private String nome;

	public SubSys(String nome) {
		this.nome = nome;
	}

	@Override
	public void setDrawable(boolean drawable) {
	}

	@Override
	public boolean isDrawable() {
		return true;
	}

	@Override
	public void drawObject(Graphics2D g) {
		Point2D.Float min = getMin();
		Point2D.Float max = getMax();

		Point pM = LeafOM.getTranformedPoint(max.x, max.y, wl);
		Point pm = LeafOM.getTranformedPoint(min.x, min.y, wl);

		pM.x -= 30;
		pm.x += 30;
		pM.y += 25;
		pm.y -= 40;

		g.drawString(nome, pM.x, pm.y);
		BasicStroke bs = (BasicStroke) g.getStroke();
		g.setStroke(new BasicStroke(2f));
		g.drawRect(pM.x, pm.y, Math.abs(pM.x - pm.x), Math.abs(pM.y - pm.y));
		g.setStroke(bs);

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
	public Point2D.Float getMin() {
		if (this.size() == 0)
			return new Point2D.Float(0f, 0f);

		Point2D.Float out = new Point2D.Float(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
		for (SysComp n : this) {
			if (n.getMin().x > out.x)
				out.x = n.getMin().x;
			if (n.getMin().y > out.y)
				out.y = n.getMin().y;
		}
		return out;
	}

	@Override
	public Point2D.Float getMax() {
		if (this.size() == 0)
			return new Point2D.Float(0f, 0f);

		Point2D.Float out = new Point2D.Float(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
		for (SysComp n : this) {
			if (n.getMax().x < out.x)
				out.x = n.getMax().x;
			if (n.getMax().y < out.y)
				out.y = n.getMax().y;
		}
		return out;
	}

}
