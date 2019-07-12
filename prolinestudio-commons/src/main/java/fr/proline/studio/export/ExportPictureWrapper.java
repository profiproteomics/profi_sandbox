/* 
 * Copyright (C) 2019 VD225637
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the CeCILL FREE SOFTWARE LICENSE AGREEMENT
 * ; either version 2.1 
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * CeCILL License V2.1 for more details.
 *
 * You should have received a copy of the CeCILL License 
 * along with this program; If not, see <http://www.cecill.info/licences/Licence_CeCILL_V2.1-en.html>.
 */
package fr.proline.studio.export;


import java.io.File;


public class ExportPictureWrapper {

	File m_graphicFile = null; // jfreesvg
	File m_graphicFile2 = null; // "spectrum_tmp_3000x2000.png");
	File m_graphicFile3 = null; // batik svg

	
	public void setFile(File svgFile) {
		m_graphicFile = svgFile;
	}
	public void setFile2(File svgFile) {
		m_graphicFile2 = svgFile;
	}
	
	public void setFile3(File svgFile) {
		m_graphicFile3 = svgFile;
	}
	
	

	
	
}
