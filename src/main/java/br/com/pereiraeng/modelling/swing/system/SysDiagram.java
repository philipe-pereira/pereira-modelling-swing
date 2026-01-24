package br.com.pereiraeng.modelling.swing.system;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;
import java.util.Collection;
import java.util.Locale;

import br.com.pereiraeng.io.IOutils;
import br.com.pereiraeng.swing.LeafOM;
import br.com.pereiraeng.swing.interfaces.Click;
import br.com.pereiraeng.swing.interfaces.WL;

public class SysDiagram extends LeafOM<SysCp> {
	private static final long serialVersionUID = 1L;

	private Collection<SysCp> objs;

	public SysDiagram() {
		super(Color.WHITE, 500, 500, true, true, true, true);
	}

	public void setObjs(Collection<SysCp> objs) {
		this.objs = objs;
		calculateView(null);
	}

	public SysConn getConn(String comp1, String comp2) {
		return SysComp.getObj(comp1, objs).getConn(comp2);
	}

	// ------------------------------ DRAWER ------------------------------

	@Override
	public Collection<SysCp> getList() {
		return objs;
	}

	private transient boolean dragable = true;

	public void setDragable(boolean dragable) {
		this.dragable = dragable;
	}

	@Override
	protected boolean isDragable() {
		return dragable;
	}

	@Override
	protected void setDragPostion(Click c, int x, int y) {
		Point2D.Float pf = getInversePoint(x, y, this);
		((SysComp) c).setLoc(pf.x, pf.y);
	}

	@Override
	protected float getMinDx() {
		return Float.MIN_VALUE;
	}

	@Override
	protected float getMaxDx() {
		return Float.MAX_VALUE;
	}

	@Override
	protected void drawBackground(Graphics2D g, WL wl) {
	}

	@Override
	protected void drawForeground(Graphics2D g, WL wl) {
	}

	// ---------- SALVAR E CARREGAR DISPOSIÇÃO GRÁFICAS DOS ELEMENTOS ----------

	private static final char CONN = '+';

	public static void save(Collection<SysCp> scs, File f) {
		String out = "";
		for (SysCp s : scs) {
			if (s instanceof SysComp) {
				// posição
				SysComp sc = (SysComp) s;
				out += String.format(Locale.US, "%s\t%g\t%g\n", sc.getNome(), s.getMin().x, s.getMin().y);

				// conector
				for (SysConn scn : sc) {
					Point p = scn.getP(scn.is1(sc));
					if (p != null)
						out += String.format(Locale.US, "%c%s\t%d\t%d\n", CONN, scn.getOtherComp(sc).getNome(), p.x,
								p.y);
				}
			}
		}

		if (f.exists())
			f.delete();
		IOutils.writeFile(f, out);
	}

	public static void load(Collection<SysCp> objs, File f) {
		String[] ss = IOutils.readFile2(f).split("\n");

		SysComp scp = null;
		for (int i = 0; i < ss.length; i++) {
			String[] s = ss[i].split("\t");

			if (s[0].charAt(0) == CONN) {
				if (scp != null) { // conector
					SysConn scn = scp.getConn(s[0].substring(1));
					if (scn != null)
						scn.setP(Integer.parseInt(s[1]), Integer.parseInt(s[2]), scn.is1(scp));
				}
			} else {
				// posição
				scp = SysComp.getObj(s[0], objs);
				if (scp != null)
					scp.setLoc(Float.parseFloat(s[1]), Float.parseFloat(s[2]));
			}
		}
	}
}
