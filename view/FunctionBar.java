package view;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import model.IView;
import model.ERModel;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class FunctionBar extends JPanel implements ActionListener{
    ERModel model;

	private JList Entity_list;
	private JList Arrow_list;

	private DefaultListModel Entity_model;
	private DefaultListModel Arrow_model;

    private String file_name;
	private String Entity_Name;
    private String Arrow_Name;
	private double posX;
	private double posY;
	private String fromName;
	private String toName;

	private int previous_selected_Entity_index = -1;
	private int previous_selected_Arrow_index = -1;
	private String previous_selected_Entity_name1;
	private String previous_selected_Entity_name2;

	private boolean select_arrow_onList = false;
	private boolean select_entity_onList = true;

	private int Entity_counter = 0;
	private int Arrow_counter = 0;
    
    Board board;

    boolean pressed_ctrl = false;

	public FunctionBar(ERModel m) {
        model = m;

		this.setBackground(Color.LIGHT_GRAY);

        this.setFocusable(true);
        this.requestFocusInWindow();

		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		Entity_model = new DefaultListModel();
		Arrow_model = new DefaultListModel();

		Entity_list = new JList(Entity_model);
		Arrow_list = new JList(Arrow_model);

		JScrollPane Entity_panel = new JScrollPane(Entity_list);
		JScrollPane Arrow_panel = new JScrollPane(Arrow_list);

		JButton addEntity = new JButton("New  Entity");
		JButton addArrow = new JButton("New Arrow");
		JButton removeEntity = new JButton("Remove  Entity");
        JButton removeArrow = new JButton("Remove Arrow");

        JLabel Entity_List = new JLabel("Entity List:");
        JLabel Arrow_List = new JLabel("Arrow List:");

		board = new Board(m);

        this.addKeyListener(new KController());
        board.addKeyListener(new KController());
        addEntity.addKeyListener(new KController());
        addArrow.addKeyListener(new KController());
        removeEntity.addKeyListener(new KController());
        removeArrow.addKeyListener(new KController());
        Entity_panel.addKeyListener(new KController());
        Arrow_panel.addKeyListener(new KController());

		addEntity.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createEntityDialog();
				m.addEntity(Entity_Name, posX, posY);
			}
		});

        removeEntity.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeEntityDialog();
                m.removeEntity(Entity_Name);
            }
        });

		addArrow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createArrowDialog();
				m.addArrow(fromName, toName);
			}
		});

        removeArrow.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeArrowDialog();
                m.removeArrow(Arrow_Name);
            }
        });

		Entity_list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int index = Entity_list.getSelectedIndex();

				if (select_entity_onList) {
					if (index != -1) {
						Entity_counter += 1;
						if (Entity_counter == 3) {
							m.setUnselected(previous_selected_Entity_index);
							Entity_counter = 1;
						}

						// Change Entity Appearance
						m.setSelected(index);
						previous_selected_Entity_index = index;

						// Change Arrow List Appearance
						String Selected_Entity_Name = m.entities.get(index).getName();

						select_arrow_onList = false;

						Arrow_list.clearSelection();
						for (int i = 0; i < m.arrows.size(); i++) {
							String from_name = m.arrows.get(i).getFName();
							String to_name = m.arrows.get(i).getTName();

							if ((Selected_Entity_Name.equals(from_name)) || (Selected_Entity_Name.equals(to_name))) {
								Arrow_list.addSelectionInterval(i, i);
							}
						}

						select_arrow_onList = true;
					} else {  // Change Entity Appearance back
						Entity_counter = 0;
						m.setUnselected(previous_selected_Entity_index);
					}
				}

			}
		});

		Arrow_list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				int index = Arrow_list.getSelectedIndex();

				if (select_arrow_onList) {
					if (index != -1) {
						Arrow_counter += 1;
						if (Arrow_counter == 3) {
							m.setUnselected(previous_selected_Entity_name1);
							m.setUnselected(previous_selected_Entity_name2);
							Arrow_counter = 1;
						}

						// Change Entity Appearance
						String first = m.arrows.get(index).getFName();
						String second = m.arrows.get(index).getTName();

						m.setSelected(first);
						m.setSelected(second);

						previous_selected_Arrow_index = index;
						previous_selected_Entity_name1 = first;
						previous_selected_Entity_name2 = second;

						// Change Entity List Appearance
						select_entity_onList = false;

						Entity_list.clearSelection();
						for (int i = 0; i < m.entities.size(); i++) {
							String name = m.entities.get(i).getName();

							if ((first.equals(name)) || (second.equals(name))) {
								Entity_list.addSelectionInterval(i, i);
							}
						}

						select_entity_onList = true;

					} else {  // Change Entity Appearance back
						Arrow_counter = 0;
						m.setUnselected(previous_selected_Entity_name1);
						m.setUnselected(previous_selected_Entity_name2);
					}
				}
			}
		});

		add(addEntity);
        add(removeEntity);
		add(addArrow);
        add(removeArrow);
        add(Entity_List);
		add(Entity_panel);
        add(Arrow_List);
		add(Arrow_panel);
		add(board);

		layout.putConstraint(SpringLayout.WEST, addEntity, 30, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, addEntity, 15, SpringLayout.NORTH, this);

        layout.putConstraint(SpringLayout.WEST, removeEntity, 15, SpringLayout.EAST, addEntity);
        layout.putConstraint(SpringLayout.NORTH, removeEntity, 15, SpringLayout.NORTH, this);

		layout.putConstraint(SpringLayout.WEST, addArrow, 30, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, addArrow, 10, SpringLayout.SOUTH, addEntity);

        layout.putConstraint(SpringLayout.WEST, removeArrow, 15, SpringLayout.EAST, addArrow);
        layout.putConstraint(SpringLayout.NORTH, removeArrow, 10, SpringLayout.SOUTH, removeEntity);

        layout.putConstraint(SpringLayout.WEST, Entity_List, 15, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, Entity_List, 15, SpringLayout.SOUTH, addArrow);

		layout.putConstraint(SpringLayout.WEST, Entity_panel, 10, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, Entity_panel, 15, SpringLayout.SOUTH, Entity_List);

        layout.putConstraint(SpringLayout.WEST, Arrow_List, 15, SpringLayout.WEST, this);
        layout.putConstraint(SpringLayout.NORTH, Arrow_List, 10, SpringLayout.SOUTH, Entity_panel);

		layout.putConstraint(SpringLayout.WEST, Arrow_panel, 10, SpringLayout.WEST, this);
		layout.putConstraint(SpringLayout.NORTH, Arrow_panel, 10, SpringLayout.SOUTH, Arrow_List);

		layout.putConstraint(SpringLayout.WEST, board, 15, SpringLayout.EAST, Entity_panel);
		layout.putConstraint(SpringLayout.NORTH, board, 15, SpringLayout.NORTH, this);

		m.addView(new IView() {
			public void updateView() {  // When Entity is selected, show on two lists.
				Entity_list.clearSelection();
				Arrow_list.clearSelection();

				//select_entity_onList = false;

				for (int i = 0; i < m.entities.size(); i++) {
					if (m.entities.get(i).selected) {
						Entity_list.setSelectedIndex(i);

						String Selected_Entity_Name = m.entities.get(i).getName();

						select_arrow_onList = false;

						for (int j = 0; j < m.arrows.size(); j++) {
							String from_name = m.arrows.get(j).getFName();
							String to_name = m.arrows.get(j).getTName();

							if ((Selected_Entity_Name.equals(from_name)) || (Selected_Entity_Name.equals(to_name))) {
								Arrow_list.addSelectionInterval(j, j);
							}
						}

						select_arrow_onList = true;
					}
				}

				//select_entity_onList = true;

			}
		});
	}

	/** Show the dialog that get data from user input.
	 *  Get data: Entity Name, PositionX, PositionY
     *  Used when user want to create an Entity with 'New Entity' button.
	 */
	public void createEntityDialog() {
		JTextField nameField = new JTextField(10);
		JTextField xField = new JTextField(5);
		JTextField yField = new JTextField(5);

		JPanel newEntity_Panel = new JPanel();
		newEntity_Panel.add(new JLabel("Entity Name:"));
		newEntity_Panel.add(nameField);
		newEntity_Panel.add(Box.createHorizontalStrut(10));
		newEntity_Panel.add(new JLabel("Position x:"));
		newEntity_Panel.add(xField);
		newEntity_Panel.add(Box.createHorizontalStrut(10)); // a spacer
		newEntity_Panel.add(new JLabel("Position y:"));
		newEntity_Panel.add(yField);

		/** Get Entity name and position from user input. */
		int result = JOptionPane.showConfirmDialog(null, newEntity_Panel, "New Entity", JOptionPane.OK_CANCEL_OPTION);

		Entity_Name = nameField.getText();
		posX = Double.valueOf(xField.getText());
		posY = Double.valueOf(yField.getText());

		/** Add new Entity to Entity List. */
		Entity_model.addElement(Entity_Name);
	}

	public void removeEntityDialog() {
        JTextField nameField = new JTextField(10);

        JPanel removeEntity_Panel = new JPanel();
        removeEntity_Panel.add(new JLabel("Name of the Entity that you want to remove:"));
        removeEntity_Panel.add(nameField);

        /** Get Entity name from user input. */
        int result = JOptionPane.showConfirmDialog(null, removeEntity_Panel, "Remove Entity", JOptionPane.OK_CANCEL_OPTION);

        Entity_Name = nameField.getText();

        /** Remove Entity from Entity List. */
        Entity_model.removeElement(Entity_Name);

        /** Remove Arrows from Entity List. */

        for (int i = 0; i < Arrow_model.getSize(); i++) {
            String full_name = (String) Arrow_model.getElementAt(i);

            int dash_index = -1;
            String dash = "-";
            for (int j = 0; j < full_name.length(); j++) {
                if (full_name.substring(j, j+1).equals(dash)) {
                    dash_index = j;
                    break;
                }
            }

            String from_name = full_name.substring(0, dash_index);
            String to_name = full_name.substring(dash_index + 1);

            if (Entity_Name.equals(from_name) || Entity_Name.equals(to_name)) {
                Arrow_model.remove(i);
            }
        }
    }

    public void removeArrowDialog() {
        JTextField nameField = new JTextField(10);

        JPanel removeEntity_Panel = new JPanel();
        removeEntity_Panel.add(new JLabel("Name of the Arrow that you want to remove:"));
        removeEntity_Panel.add(nameField);

        /** Get Arrow name from user input. */
        int result = JOptionPane.showConfirmDialog(null, removeEntity_Panel, "Remove Arrow", JOptionPane.OK_CANCEL_OPTION);

        Arrow_Name = nameField.getText();

        /** Remove Arrow from Arrow List. */
        Arrow_model.removeElement(Arrow_Name);
    }

	/** Show the dialog that get data from user input.
	 *  Get data: From Entity Name, To Entity Name.
	 */
	public void createArrowDialog() {
		JTextField from_Entity = new JTextField(10);
		JTextField to_Entity = new JTextField(10);

		JPanel newArrow_Panel = new JPanel();
		newArrow_Panel.add(new JLabel("From:"));
		newArrow_Panel.add(from_Entity);
		newArrow_Panel.add(Box.createHorizontalStrut(10));
		newArrow_Panel.add(new JLabel("To:"));
		newArrow_Panel.add(to_Entity);

		/** Get Arrow from and to from user input. */
		int result = JOptionPane.showConfirmDialog(null, newArrow_Panel, "New Arrow",
				JOptionPane.OK_CANCEL_OPTION);

		fromName = from_Entity.getText();
		toName = to_Entity.getText();

		/** Add new Arrow to Arrow List. */
		Arrow_model.addElement(fromName + "-" + toName);
	}

	public void saveDialog() {
        JTextField name = new JTextField(10);

        JPanel save_Panel = new JPanel();
        save_Panel.add(new JLabel("File Name:"));
        save_Panel.add(name);

        /** Get Arrow from and to from user input. */
        int result = JOptionPane.showConfirmDialog(null, save_Panel, "Save",
                JOptionPane.OK_CANCEL_OPTION);

        file_name = name.getText();
    }

    @Override
    public void actionPerformed(ActionEvent e) {}

    public class KController extends KeyAdapter {
       @Override
       public void keyPressed(KeyEvent e) {
           int keycode = e.getKeyCode();
           //System.out.println(String.valueOf(keycode));

           if (keycode == KeyEvent.VK_CONTROL) {
               pressed_ctrl = true;
           }

           // Zoom In
           if ((keycode == 91) && pressed_ctrl) {
               if (board.zoom_counter > -15) {
                   board.zoom_counter--;
                   board.zoomIn();
               }
           }

           // Zoom Out
           if ((keycode == 93) && pressed_ctrl) {
               if (board.zoom_counter < 5) {
                   board.zoom_counter++;
                   board.zoomOut();
               }
           }

           // New Entity
           if ((keycode == 69) && pressed_ctrl) {
               createEntityDialog();
               model.addEntity(Entity_Name, posX, posY);
           }

           // New Arrow
           if ((keycode == 65) && pressed_ctrl) {
               createArrowDialog();
               model.addArrow(fromName, toName);
           }

           // Save
           if ((keycode == 83) && pressed_ctrl) {
               saveDialog();
               saveComponentAsJPEG(board, file_name);
           }
       }

       @Override
       public void keyReleased(KeyEvent e) {
           int keycode = e.getKeyCode();

           if (keycode == KeyEvent.VK_CONTROL) pressed_ctrl = false;
       }

        public void saveComponentAsJPEG(Component myComponent, String filename) {
            Dimension size = myComponent.getSize();
            BufferedImage myImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = myImage.createGraphics();
            myComponent.paint(g2);
            try {
                OutputStream out = new FileOutputStream(filename);
                JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
                encoder.encode(myImage);
                out.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }
}
