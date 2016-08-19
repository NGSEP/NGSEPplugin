/*******************************************************************************
 * NGSEP - Next Generation Sequencing Experience Platform
 * Copyright 2016 Jorge Duitama
 *
 * This file is part of NGSEP.
 *
 *     NGSEP is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     NGSEP is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with NGSEP.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package net.sf.ngsep.utilities;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Daniel Cruz, Juan Camilo Quintero
 *
 */
public class MouseListenerNgsep implements MouseListener {
	public static final Color COLOR_INICIAL = new Color(Display.getDefault(),
			255, 255, 255);
	public static final Color COLOR_EXCEPCION = new Color(Display.getDefault(),
			233, 248, 90);

	@Override
	public void mouseDoubleClick(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseDown(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseUp(MouseEvent e) {
		Object o = e.getSource();
		if (o instanceof Text)
			mouseUpClick(e, (Text) o, COLOR_INICIAL);
	}
	public void mouseUpClick(MouseEvent e, Text txtBox, Color oc) {
		txtBox.setBackground(oc);
		txtBox.redraw();
		txtBox.update();
	}

}
