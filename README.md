# Entity-Relationship-Editor
An Entity-Relationship Editor implemented in Java.

EREdit (Entity Relationship Editor) shows entities as boxes and relationships as arrows between the boxes.

The user interface also has a list containing each model's name. Similarly, the UI has a list containing each of the arrows, represented by the names of the two entities it connects.

The UI supports the following interactions:

1. Create a new entity at a specified location in the diagram.
2. Give a new entity a name.
3. Connect two entities with an arrow.
4. Move an entity to a new position in the diagram. Any arrows should remain connected. For example, Figure 2 is the same E-R diagram as in Figure 1, but after moving the "Controller" entity.
5. Select an entity with the mouse. It should change appearance in some way. At the same time, it should be highlighted in the list of entities and any arrows connected to this entity should be highlighted in the list of arrows.
6. Selecting an entity in the list of entities should highlight it in the diagram and highlight any connected arrows in the list of arrows.
7. Selecting one of the arrows in the list of arrows should highlight both of the entities it connects in the diagram as well as the list of entities.
8. Arrows between entities should remain orthogonal. That is, they should join entities at right angles.
9. The mouse's scrollwheel and Ctrl+] or Ctrl+[ should zoom the view of the entire diagram in 10% increments (in or out).
10. Also can delete Entities and Arrows.
11. Can create a new Entity by using Ctrl+e.
12. Can create a new Arrow by using Ctrl+a.
13. Can save you diagram by using Ctrl + s, it will create a screenshot of the board and save it.
