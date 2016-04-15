package e214.skeleton;

import java.awt.BasicStroke;
import java.awt.geom.Point2D;

import fr.lri.swingstates.canvas.CExtensionalTag;
import fr.lri.swingstates.canvas.CSegment;
import fr.lri.swingstates.canvas.Canvas;

/**
 * Classe qui crée les lignes horisontales et verticales
 * @author Sofiane YOUSFI Kabylino
 *
 */
class MagneticGuide extends CExtensionalTag{
	
	private CSegment seg;
	final float dash1[] = {5.0f};
    final BasicStroke dashed =
            new BasicStroke(1.0f,
                            BasicStroke.CAP_BUTT,
                            BasicStroke.JOIN_MITER,
                            5.0f, dash1, 0.0f);
	
    /**
     * Constructeur
     * 
     * @param point2D
     * @param canvas
     * @param clickLeft
     * @param dashTag
     * @param parent
     */
	public MagneticGuide(final Point2D point2D, final Canvas canvas, final boolean clickLeft, final CExtensionalTag dashTag, final CExtensionalTag parent){
		System.out.println(point2D.getY());
		// Ligne Horizontale
		if(clickLeft) 
			seg = (CSegment) canvas.newSegment(new Point2D.Double(-1000, point2D.getY()), new Point2D.Double(10000, point2D.getY())).setStroke(dashed);
		else{
			// Ligne verticale
			seg = (CSegment) canvas.newSegment(new Point2D.Double(point2D.getX(), -1000), new Point2D.Double(point2D.getX(), 10000)).setStroke(dashed);
		}
		seg.setOutlinePaint(null);
		seg.addTag(dashTag);
		seg.addTag(parent);
		seg.belowAll();
	}
}