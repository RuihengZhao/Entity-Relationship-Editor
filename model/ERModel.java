package model;

import java.awt.*;
import java.util.ArrayList;

public class ERModel extends Object implements IView{
	/** A list of the model's views. */
	private ArrayList<IView> views = new ArrayList<IView>();
	/** A list of the Entities. */
	public ArrayList<Entity> entities = new ArrayList<Entity>();
	/** A list of the Arrows. */
	public ArrayList<Arrow> arrows = new ArrayList<Arrow>();

	private double entity_height = 80;
	private double entity_width = 150;

	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

	int screen_w = (int) screenSize.getWidth();
	int screen_h = (int) screenSize.getHeight();

	int board_w = screen_w - 300;
	int board_h = screen_h - 90;

	public ERModel() {}

	/** Add a new Entity. */
	public void addEntity(String name, double posX, double posY) {
		// Check if it's out of boundary.
		double x = posX;
		double y = posY;

		if (x < entity_width/2) {
			x = entity_width/2;
		} else if (x > board_w - entity_width/2) {
			x = board_w - entity_width/2;
		}

		if (y < entity_height/2) {
			y = entity_height/2;
		} else if (y > board_h - entity_height/2) {
			y = board_h - entity_height/2;
		}

		Entity e = new Entity(name, x, y);
		this.entities.add(e);

		this.updateAllViews();
	}

	/** Remove an Entity from this entities. --Enhancement */
	public void removeEntity(String name) {

        // Remove Entity.
        for (int i = 0; i < entities.size(); i++) {
            Entity temp = entities.get(i);

            if (temp.getName().equals(name)) {
                this.entities.remove(i);
            }
        }

        // Remove connected Arrows.
        for (int i = 0; i < arrows.size(); i++) {
            Arrow temp = arrows.get(i);

            if (temp.getFName().equals(name)) {
                this.arrows.remove(i);
            }

            if (temp.getTName().equals(name)) {
                this.arrows.remove(i);
            }
        }

		this.updateAllViews();
	}

	/** Add a new Arrow.
	 *  f_name is the name of the Entity that the Arrow is from
	 *  t_name is the name of the Entity that the Arrow is going to point
	 * */
	public void addArrow(String f_name, String t_name) {
		Entity f = new Entity("Not Found", -1, -1);
		Entity t = new Entity("Not Found", -1, -1);

		for (int i = 0; i < entities.size(); i++) {
			Entity temp = entities.get(i);

			if (temp.getName().equals(f_name)) {
				f = temp;
			}

			if (temp.getName().equals(t_name)) {
				t = temp;
			}
		}

		Arrow a = new Arrow(f, t);
		this.arrows.add(a);

		this.updateAllViews();
	}

    /** Remove an Arrow from this arrows. --Enhancement*/
    public void removeArrow(String name) {
        String arrow_name;
        for (int i = 0; i < arrows.size(); i++) {
            arrow_name = arrows.get(i).getFName() + "-" + arrows.get(i).getTName();

            if (arrow_name.equals(name)) {
                this.arrows.remove(i);
            }
        }

        this.updateAllViews();
    }

	/** Determine if there are two Entities pointing to each other. */
	public boolean bothway_arrow(Arrow a) {
		String FN = a.getFName();
		String TN = a.getTName();

		for (int i = 0; i < arrows.size(); i++) {
			String fn = arrows.get(i).getFName();
			String tn = arrows.get(i).getTName();

			if (FN.equals(tn) && TN.equals(fn)) return true;
		}

		return false;
	}

	/** Rename an Entity. --Enhancement
	 *  Use the position of mouse click and entity_index
	 *  to determine which entity the user want to rename.
	 */
	public void setName(int index, String new_name) {
		entities.get(index).setName(new_name);

		this.updateAllViews();
	}

	/** Set Entity's new position.
	 *  Use the position of mouse click and entity_index
	 *  to determine which entity the user want to move.
	 */
	public void setPosition(int index, double newX, double newY) {
		entities.get(index).setX(newX);
		entities.get(index).setY(newY);
		entities.get(index).setOriginal_x(newX);
        entities.get(index).setOriginal_y(newY);

		this.updateAllViews();
	}

	/** According to the position of the mouse clicked,
	 * determine which Entity does the user clicked.
	 * Return the index of the entity, or -1 if user didn't clicked on an Entity
	 */
	public int entity_index(double posX, double posY) {
		for (int i = 0; i < entities.size(); i++) {
			if ((posX >= entities.get(i).getX() - entity_width/2) &&
					(posX <= entities.get(i).getX() + entity_width/2) &&
					(posY >= entities.get(i).getY() - entity_height/2) &&
					(posY <= entities.get(i).getY() + entity_height/2)){
				entities.get(i).selected = true;
				this.updateAllViews();
				return i;
			}
		}

		this.updateAllViews();
		return -1;
	}

	/** If the Entity is selected, set to true.
	 *  Also update the lists.
	 */
	public void setSelected(int index) {
		entities.get(index).selected = true;

		this.updateBoardView();
	}

	public void setSelected(String name) {
		for (int i = 0; i < entities.size(); i++) {
			if (entities.get(i).getName().equals(name)) {
				entities.get(i).selected = true;
			}
		}

		this.updateBoardView();
	}

	/** If the Entity is unselected, set to false.
	 *  Also update the lists.
	 */
	public void setUnselected(int index) {
		entities.get(index).selected = false;

        this.updateBoardView();
	}

	public void setUnselected(String name) {
		for (int i = 0; i < entities.size(); i++) {
			if (entities.get(i).getName().equals(name)) {
				entities.get(i).selected = false;
			}
		}

		this.updateBoardView();
	}

	/** Want to create a new Entity at (posX, posY),
	 *  Determine if it overlap with other existing Entities.
	 * */
	public boolean overlap(double x, double y) {
		for (int i = 0; i < entities.size(); i++) {
			if ((x <= entities.get(i).getX() + entity_width) &&
					(x >= entities.get(i).getX() - entity_width) &&
					(y <= entities.get(i).getY() + entity_height) &&
					(y >= entities.get(i).getY() - entity_height)){
				return true;
			}
		}

		return false;
	}

	/** Return an Array of names of all entities. */
	public String[] allEntityNames() {
		String[] names = new String[entities.size()];

		for (int i = 0; i < entities.size(); i++) {
			names[i] = entities.get(i).getName();
		}

		return names;
	}

	/** Return an Array of names of all arrows. */
	public String[] allArrowNames() {
		String[] names = new String[arrows.size()];

		for (int i = 0; i < arrows.size(); i++) {
			names[i] = arrows.get(i).getFName() + "-" + arrows.get(i).getTName();
		}

		return names;
	}

	/** Add a new view of this EREdit. */
	public void addView(IView view) {
		this.views.add(view);
		view.updateView();
	}

	/** Remove a view from this EREdit. */
	public void removeView(IView view) {
		this.views.remove(view);
	}

	/** Update all the views that are viewing this EREdit. */
	private void updateAllViews() {
		for (IView view : this.views) {
			view.updateView();
		}
	}

	private void updateBoardView() {
		views.get(0).updateView();
	}

	@Override
	public void updateView() {}
}
