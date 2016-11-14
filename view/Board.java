package view;

import model.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import javax.swing.*;
import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

public class Board extends JComponent implements ActionListener{
	private ERModel model;

	private int previous_selected_Entity_index = -1;
	private int selected_Entity_index = -1;

	/** Handel resize. */
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	int screen_w = (int) screenSize.getWidth();
	int screen_h = (int) screenSize.getHeight();

    int board_w = screen_w - 300;
    int board_h = screen_h - 90;

    int fontSize = 20;
    int entity_lengrh = 150;
    int entity_height = 80;

    int tenth_fontSize = 2;
	int tenth_length = 15;
    int tenth_height = 8;

    int zoom_counter = 0;

    private boolean canDrage = false;

	public Board(ERModel m) {
		super();

        this.setFocusable(true);
        this.requestFocusInWindow();

		this.model = m;

		// Layout
		this.setPreferredSize(new Dimension((screen_w - 300), (screen_h - 90)));
		this.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		this.registerControllers();

		this.model.addView(new IView() {
			public void updateView() {
				repaint();
			}
		});
	}

	/** Register event Controllers for mouse clicks and motion. */
	private void registerControllers() {
		MouseInputListener mil = new MController();
		this.addMouseListener(mil);
		this.addMouseMotionListener(mil);

        MouseWheelListener mwl = new MWController();
        this.addMouseWheelListener(mwl);
	}

    @Override
    public void actionPerformed(ActionEvent e) {}

    private class MController extends MouseInputAdapter {

		/** Determine where does the user clicked the mouse.
		 *  If user clicked on an Entity, change color and show on two lists.
		 *  If user clicked on an Arrow, change color and show on two lists.  -- Enhancement
		 *  If user clicked on an white space, create an new Entity.
		 */
		public void mousePressed(MouseEvent e) {
			// Determine where does the user clicked.
			selected_Entity_index = model.entity_index((double) e.getX(), (double) e.getY());

			if (selected_Entity_index != -1) {
                model.setSelected(selected_Entity_index);
            }

			if ((previous_selected_Entity_index != selected_Entity_index) && (previous_selected_Entity_index != -1)) {
				model.setUnselected(previous_selected_Entity_index);
			}

			previous_selected_Entity_index = selected_Entity_index;

			// User want to create new Entity
			if (selected_Entity_index == -1) {

			}

			repaint();
		}

		public void mouseReleased(MouseEvent e) {
			canDrage = false;
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}

		public void mouseMoved(MouseEvent e) {
			int temp = model.entity_index((double) e.getX(), (double) e.getY());

			if ((selected_Entity_index != -1) && (temp == selected_Entity_index)) {
				canDrage = true;
				setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			} else {
				canDrage = false;
				if (temp != -1) {
					model.setUnselected(temp);
				}
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		}

		/** The user is dragging the mouse. Move Entity. */
		public void mouseDragged(MouseEvent e) {
			if (canDrage) {
				model.setPosition(selected_Entity_index, e.getX(), e.getY());
			}
		}
	}

	private class MWController implements MouseWheelListener {

        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {

            int notches = e.getWheelRotation();

            if (notches == 1) {  // Zoom Out
                if (zoom_counter < 5) {
                    zoom_counter++;
                    zoomOut();
                }
            } else {  // Zoom In
                if (zoom_counter > -15) {
                    zoom_counter--;
                    zoomIn();
                }
            }
        }
    }

	/** Paint the Board. */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Paint Background
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, (screen_w - 300), (screen_h - 90));

		// Paint coordinates
		g.setColor(Color.BLACK);
		drawArrow(g, 0, 0, (screen_w - 300), 0);
		drawArrow(g, 0, 0, 0, (screen_h - 90));

		// Change font size and font style.
		Font f = new Font("Calibri", Font.BOLD, fontSize);
		g.setFont(f);

		int number_of_Entities = model.entities.size();
		int number_of_Arrows = model.arrows.size();

		int half_entity_length = entity_lengrh/2;
		int half_entity_height = entity_height/2;
        int one_and_half_entity_lenghth = entity_lengrh + entity_lengrh + entity_lengrh/2;
        int one_and_half_entity_height = entity_height + entity_height + entity_height/2;

		if (number_of_Entities != 0) {  // Draw Entities
			for (int i = 0; i < number_of_Entities; i++) {
				if (model.entities.get(i).selected) {
					g.setColor(Color.BLUE);
				}

				int x = (int) model.entities.get(i).getX();
				int y = (int) model.entities.get(i).getY();

                if (canDrage) {
                    if (x < half_entity_length) {
                        x = half_entity_length;
                        model.setPosition(i, x, y);
                    } else if (x > board_w - half_entity_length) {
                        x = board_w - half_entity_length;
                        model.setPosition(i, x, y);
                    }

                    if (y < entity_height/2) {
                        y = half_entity_height;
                        model.setPosition(i, x, y);
                    } else if (y > board_h - half_entity_height) {
                        y = board_h - half_entity_height;
                        model.setPosition(i, x, y);
                    }
                } else {
                    //  Entity out of boundary
                    //  if out_of_boundary -> repositioning by moving all Entities to the center a little bit
                    if (x < half_entity_length) {
                        //System.out.println("Out left board.");
                        for (int j = 0; j < number_of_Entities; j++) {
                            model.setPosition(j, model.entities.get(j).getX() + entity_lengrh, model.entities.get(j).getY());
                        }
                    } else if (x > board_w - half_entity_length) {
                        //System.out.println("Out right board.");
                        for (int j = 0; j < number_of_Entities; j++) {
                            model.setPosition(j, model.entities.get(j).getX() - entity_lengrh, model.entities.get(j).getY());
                        }
                    }

                    if (y < half_entity_height) {
                        //System.out.println("Out top board.");
                        for (int j = 0; j < number_of_Entities; j++) {
                            model.setPosition(j, model.entities.get(j).getX(), model.entities.get(j).getY() + entity_height);
                        }
                    } else if (y > board_h - half_entity_height) {
                        //System.out.println("Out bottom board.");
                        for (int j = 0; j < number_of_Entities; j++) {
                            model.setPosition(j, model.entities.get(j).getX(), model.entities.get(j).getY() - entity_height);
                        }
                    }
                }

				// Draw Boxes
				g.drawLine((x - half_entity_length), (y - half_entity_height), (x + half_entity_length), (y - half_entity_height)); // top border
				g.drawLine((x - half_entity_length), (y - half_entity_height), (x - half_entity_length), (y + half_entity_height));  // left border
				g.drawLine((x + half_entity_length), (y - half_entity_height), (x + half_entity_length), (y + half_entity_height));  // right border
				g.drawLine((x - half_entity_length), (y + half_entity_height), (x + half_entity_length), (y + half_entity_height));  // bottom border

				// Draw Names
				g.drawString(model.entities.get(i).getName(), (x - entity_lengrh/5), (y + entity_height/10));

				g.setColor(Color.BLACK);
			}
		}

		if (number_of_Arrows != 0) {  // Draw arrows
			boolean bothway = false;

			for (int i = 0; i < number_of_Arrows; i++) {

				int fx = (int) model.arrows.get(i).getFPosX();  // Arrow from point (fx, fy)
				int fy = (int) model.arrows.get(i).getFPosY();
				int tx = (int) model.arrows.get(i).getTPosX();  // to (tx, ty)
				int ty = (int) model.arrows.get(i).getTPosY();

				if (model.bothway_arrow(model.arrows.get(i))) {  // if two entities are pointing to each other
                    if (!bothway) {  // draw the first arrow of bothway arrow
                        if ((fx == tx) && (fy > ty + entity_height)) { // Top
                            drawArrow(g, fx, (fy - half_entity_height), tx, (ty + half_entity_height));
                        } else if ((fx == tx) && (fy < ty - entity_height)) { // Bottom
                            drawArrow(g, fx, (fy + half_entity_height), tx, (ty - half_entity_height));
                        } else if ((fy == ty) && (fx > tx + entity_lengrh)) { // Left
                            drawArrow(g, (fx - half_entity_length), fy, (tx + half_entity_length), ty);
                        } else if ((fy == ty) && (fx < tx - entity_lengrh)) { // Right
                            drawArrow(g, (fx + half_entity_length), fy, (tx - half_entity_length), ty);
                        } else if ((fy - half_entity_height <= ty) && (ty <= fy + half_entity_height)) {
                            // (fy - half_entity_height) <= ty <= (fy + half_entity_height)
                            g.drawLine(fx, (fy - half_entity_height), fx, (fy - one_and_half_entity_height));
                            g.drawLine(fx, (fy - one_and_half_entity_height), tx, (fy - one_and_half_entity_height));
                            drawArrow(g, tx, (fy - one_and_half_entity_height), tx, (ty - half_entity_height));
                        } else if (((fx <= tx) && ((fx + half_entity_length) > tx))
                                || ((fx >= tx) && ((fx - half_entity_length) < tx)) ) {
                            if (fx > tx) {  // Start from left border
                                g.drawLine((fx - half_entity_length), fy, (fx - one_and_half_entity_lenghth), fy);
                                g.drawLine((fx - one_and_half_entity_lenghth), fy, (fx - one_and_half_entity_lenghth), ty);
                                drawArrow(g, (fx - one_and_half_entity_lenghth), ty, (tx - half_entity_length), ty);
                            } else {  // Start from right border
                                g.drawLine((fx + half_entity_length), fy, (fx + one_and_half_entity_lenghth), fy);
                                g.drawLine((fx + one_and_half_entity_lenghth), fy, (fx + one_and_half_entity_lenghth), ty);
                                drawArrow(g, (fx + one_and_half_entity_lenghth), ty, (tx + half_entity_length), ty);
                            }
                        } else if ((fx == tx) && (((fy - entity_height == ty)) || ((fy + entity_height == ty)))) {
                            // Top/Bottom Adjoining
                            g.drawLine((fx + half_entity_length), fy, (fx + entity_lengrh), fy);
                            g.drawLine((fx + entity_lengrh), fy, (fx + entity_lengrh), ty);
                            drawArrow(g, (fx + entity_lengrh), ty, (tx + half_entity_length), ty);
                        } else if ((fx < tx) && (fy > ty)) { // Top-Right Area
                            g.drawLine(fx, (fy - half_entity_height), fx, ty);
                            drawArrow(g, fx, ty, (tx - half_entity_length), ty);
                        } else if ((fx < tx) && (fy < ty)) { // Bottom-Right Area
                            g.drawLine(fx, (fy + half_entity_height), fx, ty);
                            drawArrow(g, fx, ty, (tx - half_entity_length), ty);
                        } else if ((fx > tx) && (fy > ty)) { // Top-Left Area
                            g.drawLine(fx, (fy - half_entity_height), fx, ty);
                            drawArrow(g, fx, ty, (tx + half_entity_length), ty);
                        } else if ((fx > tx) && (fy < ty)) { // Bottom-Right Area
                            g.drawLine(fx, (fy + half_entity_height), fx, ty);
                            drawArrow(g, fx, ty, (tx + half_entity_length), ty);
                        }

                        bothway = true;
                    } else {  // draw the second arrow of bothway arrow
                        if ((fx == tx) && (fy > ty + entity_height)) { // Top
                            drawArrow(g, fx, (fy - half_entity_height), tx, (ty + half_entity_height));
                        } else if ((fx == tx) && (fy < ty - entity_height)) { // Bottom
                            drawArrow(g, fx, (fy + half_entity_height), tx, (ty - half_entity_height));
                        } else if ((fy == ty) && (fx > tx + entity_lengrh)) { // Left
                            drawArrow(g, (fx - half_entity_length), fy, (tx + half_entity_length), ty);
                        } else if ((fy == ty) && (fx < tx - entity_lengrh)) { // Right
                            drawArrow(g, (fx + half_entity_length), fy, (tx - half_entity_length), ty);
                        } else if ((fy - half_entity_height <= ty) && (ty <= fy + half_entity_height)) {
                            // (fy - half_entity_height) <= ty <= (fy + half_entity_height)
                            g.drawLine(fx, (fy - half_entity_height), fx, (fy - one_and_half_entity_height));
                            g.drawLine(fx, (fy - one_and_half_entity_height), tx, (fy - one_and_half_entity_height));
                            drawArrow(g, tx, (fy - one_and_half_entity_height), tx, (ty - half_entity_height));
                        } else if (((fx <= tx) && ((fx + half_entity_length) > tx))
                                || ((fx >= tx) && ((fx - half_entity_length) < tx)) ) {
                            if (fx > tx) {  // Start from left border
                                g.drawLine((fx - half_entity_length), fy, (fx - one_and_half_entity_lenghth), fy);
                                g.drawLine((fx - one_and_half_entity_lenghth), fy, (fx - one_and_half_entity_lenghth), ty);
                                drawArrow(g, (fx - one_and_half_entity_lenghth), ty, (tx - half_entity_length), ty);
                            } else {  // Start from right border
                                g.drawLine((fx + half_entity_length), fy, (fx + one_and_half_entity_lenghth), fy);
                                g.drawLine((fx + one_and_half_entity_lenghth), fy, (fx + one_and_half_entity_lenghth), ty);
                                drawArrow(g, (fx + one_and_half_entity_lenghth), ty, (tx + half_entity_length), ty);
                            }
                        } else if ((fx == tx) && (((fy - entity_height == ty)) || ((fy + entity_height == ty)))) {
                            // Top/Bottom Adjoining
                            g.drawLine((fx + half_entity_length), fy, (fx + entity_lengrh), fy);
                            g.drawLine((fx + entity_lengrh), fy, (fx + entity_lengrh), ty);
                            drawArrow(g, (fx + entity_lengrh), ty, (tx + half_entity_length), ty);
                        } else if ((fx < tx) && (fy > ty)) { // Top-Right Area
                            g.drawLine(fx, (fy - half_entity_height), fx, ty);
                            drawArrow(g, fx, ty, (tx - half_entity_length), ty);
                        } else if ((fx < tx) && (fy < ty)) { // Bottom-Right Area
                            g.drawLine(fx, (fy + half_entity_height), fx, ty);
                            drawArrow(g, fx, ty, (tx - half_entity_length), ty);
                        } else if ((fx > tx) && (fy > ty)) { // Top-Left Area
                            g.drawLine(fx, (fy - half_entity_height), fx, ty);
                            drawArrow(g, fx, ty, (tx + half_entity_length), ty);
                        } else if ((fx > tx) && (fy < ty)) { // Bottom-Right Area
                            g.drawLine(fx, (fy + half_entity_height), fx, ty);
                            drawArrow(g, fx, ty, (tx + half_entity_length), ty);
                        }

                        bothway = false;
                    }
				}else {
                    //  Calculate the position of the start point and end point
                    //  ( ToEntity relvent to FromEntity. )
                    //  For Top/Bottom/Left/Right cases, just drow a straight arrow.
                    //  For other cases, have to draw orthogonal arrows.

                    if ((fx == tx) && (fy > ty + entity_height)) { // Top
                        drawArrow(g, fx, (fy - half_entity_height), tx, (ty + half_entity_height));
                    } else if ((fx == tx) && (fy < ty - entity_height)) { // Bottom
                        drawArrow(g, fx, (fy + half_entity_height), tx, (ty - half_entity_height));
                    } else if ((fy == ty) && (fx > tx + entity_lengrh)) { // Left
                        drawArrow(g, (fx - half_entity_length), fy, (tx + half_entity_length), ty);
                    } else if ((fy == ty) && (fx < tx - entity_lengrh)) { // Right
                        drawArrow(g, (fx + half_entity_length), fy, (tx - half_entity_length), ty);
                    } else if ((fy - half_entity_height <= ty) && (ty <= fy + half_entity_height)) {
                        // (fy - half_entity_height) <= ty <= (fy + half_entity_height)
                        g.drawLine(fx, (fy - half_entity_height), fx, (fy - one_and_half_entity_height));
                        g.drawLine(fx, (fy - one_and_half_entity_height), tx, (fy - one_and_half_entity_height));
                        drawArrow(g, tx, (fy - one_and_half_entity_height), tx, (ty - half_entity_height));
                    } else if (((fx <= tx) && ((fx + half_entity_length) > tx))
                            || ((fx >= tx) && ((fx - half_entity_length) < tx)) ) {
                        if (fx > tx) {  // Start from left border
                            g.drawLine((fx - half_entity_length), fy, (fx - one_and_half_entity_lenghth), fy);
                            g.drawLine((fx - one_and_half_entity_lenghth), fy, (fx - one_and_half_entity_lenghth), ty);
                            drawArrow(g, (fx - one_and_half_entity_lenghth), ty, (tx - half_entity_length), ty);
                        } else {  // Start from right border
                            g.drawLine((fx + half_entity_length), fy, (fx + one_and_half_entity_lenghth), fy);
                            g.drawLine((fx + one_and_half_entity_lenghth), fy, (fx + one_and_half_entity_lenghth), ty);
                            drawArrow(g, (fx + one_and_half_entity_lenghth), ty, (tx + half_entity_length), ty);
                        }
                    } else if ((fx == tx) && (((fy - entity_height == ty)) || ((fy + entity_height == ty)))) {
                        // Top/Bottom Adjoining
                        g.drawLine((fx + half_entity_length), fy, (fx + entity_lengrh), fy);
                        g.drawLine((fx + entity_lengrh), fy, (fx + entity_lengrh), ty);
                        drawArrow(g, (fx + entity_lengrh), ty, (tx + half_entity_length), ty);
                    } else if ((fx < tx) && (fy > ty)) { // Top-Right Area
                        g.drawLine(fx, (fy - half_entity_height), fx, ty);
                        drawArrow(g, fx, ty, (tx - half_entity_length), ty);
                    } else if ((fx < tx) && (fy < ty)) { // Bottom-Right Area
                        g.drawLine(fx, (fy + half_entity_height), fx, ty);
                        drawArrow(g, fx, ty, (tx - half_entity_length), ty);
                    } else if ((fx > tx) && (fy > ty)) { // Top-Left Area
                        g.drawLine(fx, (fy - half_entity_height), fx, ty);
                        drawArrow(g, fx, ty, (tx + half_entity_length), ty);
                    } else if ((fx > tx) && (fy < ty)) { // Bottom-Right Area
                        g.drawLine(fx, (fy + half_entity_height), fx, ty);
                        drawArrow(g, fx, ty, (tx + half_entity_length), ty);
                    }
				}
			}
		}

	}

	/** Helper Function. Used for drawing arrows. */
	void drawArrow(Graphics g1, int x1, int y1, int x2, int y2) {
		final int ARR_SIZE = 5;
		Graphics2D g = (Graphics2D) g1.create();

		double dx = x2 - x1, dy = y2 - y1;
		double angle = Math.atan2(dy, dx);
		int len = (int) Math.sqrt(dx*dx + dy*dy);
		AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
		at.concatenate(AffineTransform.getRotateInstance(angle));
		g.transform(at);

		// Draw horizontal arrow starting in (0, 0)
		g.drawLine(0, 0, len, 0);
		g.fillPolygon(new int[] {len, len-ARR_SIZE, len-ARR_SIZE, len},
				new int[] {0, -ARR_SIZE, ARR_SIZE, 0}, 4);
	}

	/** Helper Function. Used for zoom in. */
	void zoomIn() {
        fontSize += tenth_fontSize;
        entity_lengrh += tenth_length;
        entity_height += tenth_height;

        int num_of_entities = model.entities.size();

        for (int i = 0; i < num_of_entities; i++) {
            int x = (int) model.entities.get(i).getX();
            int y = (int) model.entities.get(i).getY();

            int o_x = model.entities.get(i).get_originalX();
            int o_y = model.entities.get(i).get_originalY();

            double x_increment = Math.abs(o_x - board_w/2)/10;
            double y_increment = Math.abs(o_y - board_h/2)/10;

            if (o_x > board_w/2) {
                model.entities.get(i).setX(x + x_increment);
            } else if (o_x < board_w/2) {
                model.entities.get(i).setX(x - x_increment);
            } else if (o_x == board_w/2) {
                // Will not change.
            }

            if (o_y > board_h/2) {
                model.entities.get(i).setY(y + y_increment);
            } else if (o_y < board_h/2) {
                model.entities.get(i).setY(y - y_increment);
            } else if (o_y == board_h/2) {
                // Will not change.
            }
        }

        repaint();
    }

    /** Helper Function. Used for zoom out. */
    void zoomOut() {
        fontSize -= tenth_fontSize;
        entity_lengrh -= tenth_length;
        entity_height -= tenth_height;

        int num_of_entities = model.entities.size();

        for (int i = 0; i < num_of_entities; i++) {
            int x = (int) model.entities.get(i).getX();
            int y = (int) model.entities.get(i).getY();

            int o_x = model.entities.get(i).get_originalX();
            int o_y = model.entities.get(i).get_originalY();

            double x_increment = Math.abs(o_x - board_w/2)/10;
            double y_increment = Math.abs(o_y - board_h/2)/10;
            
            if (o_x > board_w/2) {
                if (x - x_increment < board_w/2) {
                    model.entities.get(i).setX(board_w/2);
                } else {
                    model.entities.get(i).setX(x - x_increment);
                }
            } else if (o_x < board_w/2) {
                if (x + x_increment > board_w/2) {
                    model.entities.get(i).setX(board_w/2);
                } else {
                    model.entities.get(i).setX(x + x_increment);
                }
            } else if (o_x == board_w/2) {
                // Will not change.
            }

            if (o_y > board_h/2) {
                if (y - y_increment < board_h/2) {
                    model.entities.get(i).setY(board_h/2);
                } else {
                    model.entities.get(i).setY(y - y_increment);
                }
            } else if (o_y < board_h/2) {
                if (y + y_increment > board_h/2) {
                    model.entities.get(i).setY(board_h/2);
                } else {
                    model.entities.get(i).setY(y + y_increment);
                }
            } else if (o_y == board_h/2) {
                // Will not change.
            }
        }

        repaint();
    }

    void saveDiagram(){

    }
}
