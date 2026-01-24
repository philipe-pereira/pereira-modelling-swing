package br.com.pereiraeng.modelling.swing.system;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.ImageIcon;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import br.com.pereiraeng.core.ColorUtils;
import br.com.pereiraeng.icons.Icons;
import br.com.pereiraeng.xml.XMLadapter;

public class XMLsysReader extends XMLadapter {

	private Collection<SysCp> sys;

	public static Collection<SysCp> read(File file) {
		XMLsysReader xmn = new XMLsysReader();
		xmn.parse(file);
		return xmn.getUMLs();
	}

	private Collection<SysCp> getUMLs() {
		return sys;
	}

	@Override
	public void startDocument() throws SAXException {
		sys = new LinkedList<SysCp>();
	}

	private transient SysComp comp;

	private transient SubSys sub;

	@Override
	public void startElement(String qName, Attributes atts) {
		switch (qName) {
		case "comp":
			String iconPath = atts.getValue("icon");
			ImageIcon ii = null;
			if (iconPath != null) {
				ii = Icons.loadIcon(iconPath);
				if (ii == null) {
					File file = new File(iconPath);
					if (file.exists())
						ii = new ImageIcon(file.getAbsolutePath());
				}
			}
			comp = new SysComp(atts.getValue("name"), ii);
			sys.add(comp);
			break;
		case "conn":
			SysConn sc = new SysConn(SysComp.getObj(atts.getValue("comp1"), sys),
					SysComp.getObj(atts.getValue("comp2"), sys));
			String x = atts.getValue("x1");
			String y = atts.getValue("y1");
			if (x != null && y != null)
				sc.setP(Integer.parseInt(x), Integer.parseInt(y), true);
			x = atts.getValue("x2");
			y = atts.getValue("y2");
			if (x != null && y != null)
				sc.setP(Integer.parseInt(x), Integer.parseInt(y), false);

			String color = atts.getValue("stroke");
			if (color != null) // rgb(XXX,YYY,ZZZ)
				sc.setColor(ColorUtils.html2color(color));
			break;
		case "sys":
			sub = new SubSys(atts.getValue("name"));
			sys.add(sub);
			break;
		}
	}

	@Override
	public void characters(String s) {
		if (sub != null) {
			String[] ss = s.split(";");
			for (int i = 0; i < ss.length; i++) {
				SysComp sc = SysComp.getObj(ss[i], sys);
				if (sc != null)
					sub.add(sc);
			}
		}
	}

	@Override
	public void endElement(String qName) {
		switch (qName) {
		case "comp":
			comp = null;
			break;
		case "sys":
			sub = null;
			break;
		}
	}

	@Override
	public void endDocument() throws SAXException {
	}
}
