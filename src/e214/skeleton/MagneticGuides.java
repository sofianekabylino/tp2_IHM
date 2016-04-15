package e214.skeleton;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.List;

import javax.swing.JFrame;

import fr.lri.swingstates.canvas.CExtensionalTag;
import fr.lri.swingstates.canvas.CRectangle;
import fr.lri.swingstates.canvas.CShape;
import fr.lri.swingstates.canvas.CStateMachine;
import fr.lri.swingstates.canvas.Canvas;
import fr.lri.swingstates.canvas.transitions.ClickOnTag;
import fr.lri.swingstates.canvas.transitions.EnterOnTag;
import fr.lri.swingstates.canvas.transitions.LeaveOnTag;
import fr.lri.swingstates.canvas.transitions.PressOnTag;
import fr.lri.swingstates.sm.State;
import fr.lri.swingstates.sm.Transition;
import fr.lri.swingstates.sm.transitions.Click;
import fr.lri.swingstates.sm.transitions.Drag;
import fr.lri.swingstates.sm.transitions.Press;
import fr.lri.swingstates.sm.transitions.Release;

/**
 * Classe qui contient les actions sur les lignes et les objets
 * 
 * @author Sofiane YOUSFI Kabylino
 * 
 */
public class MagneticGuides extends JFrame {

	private static final long serialVersionUID = -4856966984470974084L;
	private Canvas canvas;
	private CExtensionalTag oTag;
	private CExtensionalTag lineTagHor, lineTagVert, line;
	final float dash1[] = { 5.0f };
	final BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, dash1, 0.0f);

	final float dash2[] = { 5.0f };
	final BasicStroke boldDashed = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 5.0f, dash2,
			1.0f);

	/**
	 * Constructeur
	 * 
	 * @param title
	 * @param width
	 * @param height
	 */
	public MagneticGuides(final String title, final int width, final int height) {
		super(title);
		canvas = new Canvas(width, height);
		canvas.setAntialiased(true);
		getContentPane().add(canvas);

		oTag = new CExtensionalTag(canvas) {
		};
		line = new CExtensionalTag(canvas) {
		};
		lineTagHor = new CExtensionalTag(canvas) {
		};
		lineTagVert = new CExtensionalTag(canvas) {
		};

		// Effet lors du survol d'une ligne
		final CExtensionalTag hover = new CExtensionalTag(canvas) {
			@Override
			public void added(CShape s) {
				s.setStroke(boldDashed);
			}

			@Override
			public void removed(CShape s) {
				s.setStroke(dashed);
			}
		};

		CStateMachine sm = new CStateMachine() {

			private Point2D p;
			private CShape draggedShape, parentShape;

			// Etat Start
			public State start = new State() {

				// clique sur un carré
				Transition pressOnTag = new PressOnTag(oTag, BUTTON1, ">> oDrag") {
					public void action() {
						p = getPoint();
						draggedShape = getShape();
					}
				};

				// Creation d'une ligne verticale avec un clique droit
				Transition pressRight = new Press(BUTTON3, ">> releasePress") {
					@Override
					public void action() {
						p = getPoint();
						new MagneticGuide(p, canvas, false, lineTagVert, line);
					}
				};

				// Creation d'une ligne horizontale avec un clique gauche
				Transition pressLeft = new Press(BUTTON1, ">> releasePress") {
					@Override
					public void action() {
						p = getPoint();
						new MagneticGuide(p, canvas, true, lineTagHor, line);
					}
				};

				// Un clique long sur une ligne Horizontale
				Transition pressOnLineHor = new PressOnTag(lineTagHor, BUTTON1, ">> DragLineHor") {
					public void action() {
						p = getPoint();
						draggedShape = getShape();
						List<CShape> listShape = line.getFilledShapes();
						for (CShape s : listShape) {
							// On prend uniquement les carrés.
							if ((s.hasTag(oTag)) && (s.contains(s.getMinX(), draggedShape.getCenterY()) != null)
									&& (s.getParent() != draggedShape)) {
								double x = s.getCenterX(), y = s.getCenterY();
								draggedShape.addChild(s);
								s.translateTo(x, y);
							}
						}
					}
				};
				/*
				 * Un clique long sur une ligne Verticale
				 */
				Transition pressOnLineVert = new PressOnTag(lineTagVert, BUTTON1, ">> DragLineVert") {
					public void action() {
						p = getPoint();
						draggedShape = getShape();
						List<CShape> listShape = line.getFilledShapes();
						for (CShape s : listShape) {
							// On prend uniquement les carrés.
							if ((s.hasTag(oTag)) && (s.contains(draggedShape.getCenterX(), s.getMinY()) != null)
									&& (s.getParent() != draggedShape)) {
								double x = s.getCenterX(), y = s.getCenterY();
								draggedShape.addChild(s);
								s.translateTo(x, y);
							}
						}
					}
				};

				// Survol d'une ligne
				Transition enterLine = new EnterOnTag(line) {
					public void action() {
						if (!getShape().hasTag(oTag)) {
							parentShape = getShape();
							parentShape.addTag(hover);
						}
					}
				};

				// Quitter le survol d'une ligne
				Transition leaveLine = new LeaveOnTag(line) {
					public void action() {
						if (!getShape().hasTag(oTag)) {
							parentShape.removeTag(hover);
							parentShape = null;
						}
					}
				};
				// Supprimer une ligne horizontale avec le maintien de la touche
				// CONTROL + un clique gauche
				Transition deleteLineHor = new ClickOnTag(lineTagHor, BUTTON1, CONTROL, ">> start") {
					public void action() {
						getShape().removeTag(lineTagHor);
						getShape().removeTag(line);
						getShape().remove();
					}
				};
				// Supprimer une ligne verticale avec le maintien de la touche
				// CONTROL + un clic gauche
				Transition deleteLineVert = new ClickOnTag(lineTagVert, BUTTON1, CONTROL, ">> start") {
					public void action() {
						getShape().removeTag(lineTagVert);
						getShape().removeTag(line);
						getShape().remove();
					}
				};
				// Cacher les lignes avec le maintien de la touche CONTROL + un
				// clique gauche dans le vide
				Transition hideAllLines = new Click(BUTTON1, CONTROL, ">> start") {
					public void action() {
						List<CShape> l = lineTagHor.getFilledShapes();
						for (CShape dash : l) {
							if (dash.isDrawable())
								dash.setDrawable(false);
							else
								dash.setDrawable(true);
						}
						l = lineTagVert.getFilledShapes();
						for (CShape dash : l) {
							if (dash.isDrawable())
								dash.setDrawable(false);
							else
								dash.setDrawable(true);
						}
					}
				};
			};

			/**
			 * Etat lors d'un déplacement d'un carré
			 */
			public State oDrag = new State() {
				Transition drag = new Drag(BUTTON1) {
					public void action() {
						Point2D q = getPoint();
						draggedShape.translateBy(q.getX() - p.getX(), q.getY() - p.getY());
						p = q;
					}
				};

				// relachement du clic apres un drag
				Transition release = new Release(BUTTON1, ">> start") {
					public void action() {
						List<CShape> listShape = line.getFilledShapes();
						CShape shp = null;
						for (CShape s : listShape) {
							if (!s.hasTag(oTag)) {
								if (draggedShape.contains(draggedShape.getMinX(), s.getCenterY()) != null) {
									shp = s;
									draggedShape.addTag(line);
									s.addChild(draggedShape);
									draggedShape.translateTo(p.getX(), s.getCenterY());
								} else if (draggedShape.contains(s.getCenterX(), draggedShape.getMinY()) != null) {
									shp = s;
									draggedShape.addTag(line);
									s.addChild(draggedShape);
									draggedShape.translateTo(s.getCenterX(), p.getY());
								}
							}
						}
						if (shp == null && draggedShape.hasTag(line)) {
							draggedShape.removeTag(line);
							draggedShape.translateTo(p.getX(), p.getY());
						}
						if (shp == null && draggedShape.getParent() != null) {
							draggedShape.getParent().removeChild(draggedShape);

							draggedShape.translateTo(p.getX(), p.getY());
						}
						p = null;
						shp = null;
					}
				};

				// Survol d'une ligne avec un carré
				Transition enterLine = new EnterOnTag(line) {
					public void action() {
						if (!getShape().hasTag(oTag)) {
							parentShape = getShape();
							parentShape.addTag(hover);
						}
					};
				};
				// Quitter le survol d'une ligne avec un carré
				Transition leaveLine = new LeaveOnTag(line) {
					public void action() {
						if (!getShape().hasTag(oTag)) {
							parentShape.removeTag(hover);
							parentShape = null;

						}
					}
				};

			};

			/**
			 * Relacher le clique pour la creation d'une ligne
			 */
			public State releasePress = new State() {
				Transition release = new Release(">> start") {
					@Override
					public void action() {
						// TODO Méthode qui déclanche l'action
					}
				};
			};

			/**
			 * Deplacement d'une ligne horizontale
			 */
			public State DragLineHor = new State() {
				// Deplacer une ligne
				Transition dragLine = new Drag(BUTTON1, ">> DragLineHor") {
					public void action() {
						Point2D q = getPoint();
						draggedShape.translateBy(0, q.getY() - p.getY());
						p = q;
					}
				};
				// relacher apres un deplacement de la ligne
				Transition release = new Release(BUTTON1, ">> start") {
					public void action() {
						p = null;
					}
				};
			};

			/**
			 * Deplacement d'une ligne verticale
			 */
			public State DragLineVert = new State() {
				// Deplacer une ligne
				Transition dragLine = new Drag(BUTTON1, ">> DragLineVert") {
					public void action() {
						Point2D q = getPoint();
						draggedShape.translateBy(q.getX() - p.getX(), 0);
						p = q;
					}
				};

				// relacher apres un deplacement de la ligne
				Transition release = new Release(BUTTON1, ">> start") {
					public void action() {
						p = null;
					}
				};
			};
		};
		sm.attachTo(canvas);

		pack();
		setVisible(true);
		canvas.requestFocusInWindow();

		// JFrame viz = new JFrame();
		// viz.getContentPane().add(new StateMachineVisualization(sm));
		// viz.pack();
		// viz.setVisible(true);
	}

	public void populate() {
		int width = canvas.getWidth();
		int height = canvas.getHeight();

		double s = (Math.random() / 2.0 + 0.5) * 30.0;
		double x = s + Math.random() * (width - 2 * s);
		double y = s + Math.random() * (height - 2 * s);

		int red = (int) ((0.8 + Math.random() * 0.2) * 255);
		int green = (int) ((0.8 + Math.random() * 0.2) * 255);
		int blue = (int) ((0.8 + Math.random() * 0.2) * 255);

		CRectangle r = canvas.newRectangle(x, y, s, s);
		r.setFillPaint(new Color(red, green, blue));
		r.addTag(oTag);
	}
}