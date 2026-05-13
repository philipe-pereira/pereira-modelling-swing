package br.com.pereiraeng.modelling.swing;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Float;
import java.awt.geom.Rectangle2D;
import java.util.List;

import br.com.pereiraeng.modelling.modelutils.uml.UMLfield;
import br.com.pereiraeng.modelling.modelutils.uml.UMLobject;
import br.com.pereiraeng.modelling.modelutils.uml.UMLrelation;
import br.com.pereiraeng.swing.LeafOM;
import br.com.pereiraeng.swing.MonoSpacedFont;
import br.com.pereiraeng.swing.interfaces.Click;
import br.com.pereiraeng.swing.interfaces.DesM;
import br.com.pereiraeng.swing.interfaces.WL;

public class UMLobjectD extends UMLobject implements DesM, Click {
	private static final long serialVersionUID = 1195586577636965802L;

	private static final MonoSpacedFont FONT = MonoSpacedFont.CN12;

	public UMLobjectD(String nome, String descricao) {
		super(nome, descricao);
	}

	private Color highlight;

	private WL wl;

	private Point2D.Float loc = new Point2D.Float((float) Math.random(), (float) Math.random());

	public int getW() {
		return w;
	}

	public void setLoc(float x, float y) {
		loc.setLocation(x, y);
	}

	public void setHighlight(Color highlight) {
		this.highlight = highlight;
	}

	@Override
	public void setDrawable(boolean drawable) {
		super.drawable = drawable;
	}

	@Override
	public boolean isDrawable() {
		return super.drawable;
	}

	@Override
	public void drawObject(Graphics2D g) {
		g.setFont(FONT.getFont());
		Point p = LeafOM.getTranformedPoint(loc.x, loc.y, wl);

		// nome
		g.drawString(nome, p.x, p.y);

		// caixa
		if (highlight != null) {
			g.setColor(highlight);
			g.fillRect(p.x, p.y, w * FONT.getWidth(), FONT.getHeight() * this.size());
			g.setColor(Color.BLACK);
		}
		g.drawRect(p.x, p.y, w * FONT.getWidth(), FONT.getHeight() * this.size());

		// campos
		for (int i = 0; i < this.size(); i++) {
			UMLfield o = this.get(i);
			g.drawString(o.getCampo(), p.x, p.y + FONT.getHeight() + i * FONT.getHeight());

			List<UMLrelation> rs = o.getRelations();
			if (rs != null)
				for (UMLrelation r : rs)
					draw(g, wl, o, r, highlight);
		}
	}

	private static void draw(Graphics2D g, WL wl, UMLfield f1, UMLrelation r, Color highlight) {
		UMLfield f2 = r.getOtherField(f1);

		UMLobjectD o1;
		if (f1.getObject() instanceof UMLobjectD) {
			o1 = (UMLobjectD) f1.getObject();
		} else
			return;

		UMLobjectD o2;
		if (f2.getObject() instanceof UMLobjectD) {
			o2 = (UMLobjectD) f2.getObject();
		} else
			return;

		if (!o1.isDrawable() || !o2.isDrawable())
			return;

		Point2D.Float l = o1.getMin();
		Point p1 = LeafOM.getTranformedPoint(l.x, l.y, wl);
		int i1 = o1.indexOf(f1);
		p1.y = p1.y + FONT.getHeight() / 2 + i1 * FONT.getHeight();

		l = o2.getMin();
		Point p2 = LeafOM.getTranformedPoint(l.x, l.y, wl);
		int i2 = o2.indexOf(f2);
		p2.y = p2.y + FONT.getHeight() / 2 + i2 * FONT.getHeight();

		if (p1.x > p2.x)
			p2.x += o2.getW() * FONT.getWidth();
		else
			p1.x += o1.getW() * FONT.getWidth();

		// só desenha metada do caminho: a outra metada que desenha é o outro campo (que
		// também tem uma referência, apontanda para esta)
		if (highlight != null)
			g.setColor(highlight);

		g.drawLine(p1.x, p1.y, (p2.x + p1.x) / 2, p1.y);
		g.drawLine((p2.x + p1.x) / 2, p1.y, (p2.x + p1.x) / 2, (p2.y + p1.y) / 2);

		if (highlight != null)
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
		Rectangle2D.Float rect = new Rectangle2D.Float(p.x, p.y, w * FONT.getWidth(), this.size() * FONT.getHeight());
		return new Area(rect);
	}
}
