package tool.clients.diagrams;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Vector;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import xos.Value;

public class Palette {

//  ToolBar     groupContainer;
//  
//  Vector<Group> groups = new Vector<Group>();
	
	private TreeView<String> tree;
//	private HashMap<String,TreeItem<String>> gRoups = new HashMap<>();
	private HashMap<String,Group> groups = new HashMap<>();
	private HashMap<String,TreeItem<String>> buttons = new HashMap<>();
	private TreeItem<String> root;

  public Palette(Diagram diagram) {
	  tree = new TreeView<String>();
	  root = new TreeItem<String>("Root");
	  tree.setRoot(root);
	  
	  tree.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TreeItem<String>>() {

        @Override
        public void changed(
        		ObservableValue<? extends TreeItem<String>> observable, 
        		TreeItem<String> oldValue,
        		TreeItem<String> newValue) {

//            TreeItem<String> selectedItem = (TreeItem<String>) newValue;
            System.out.println("Selected Text : " + newValue.getValue());

            // Workaround:
            // if leaf is selected, 
            // then it must be part of a group
            if(newValue.getChildren().isEmpty()) {
            	TreeItem<String> parent = newValue.getParent();
            	if(parent instanceof Group) {
            		Group group = (Group) parent;
            		Tool tool = group.getToolLabelled(newValue.getValue());
            		if(tool != null) tool.widgetSelected(null);
            	}
            }
        }

	  });
  }

  public void init(Diagram diagram) {
      diagram.newGroup("Diagram");
      newTool(diagram, "Diagram", "Select", "Select", false, "Select.gif");
  }

  public Value asValue() {
	  throw new RuntimeException("Not implemented yet");
//    Value[] g = new Value[groups.size()];
//    int i = 0;
//    for (Group group : groups)
//      g[i++] = group.asValue(group.getName());
//    return new Value(g);
  }

  public void deleteGroup(String name) {
	  throw new RuntimeException("Can't delete yet.");
//    Group group = getGroup(name);
//    if (group != null) {
//      groups.remove(group);
//      group.delete();
//    }
  }

  private Group getGroup(String name) {
	  return groups.get(name);
//    for (Group group : groups)
//      if (group.getName().equals(name)) return group;
//    return null;
  }

  public boolean hasGroup(String name) {
    return getGroup(name) != null;
  }

  public void newGroup(String name) {
	  Group group = new Group(name);
	  root.getChildren().add(group);
	  groups.put(name, group);
	  
//    Group group = new Group(this, groupContainer, name);
//    groups.add(group);
  }

  public void newToggle(Diagram diagram, String groupName, String label, String toolId, boolean state, String iconTrue, String iconFalse) {
    Group group = getGroup(groupName);
    if (group != null) {
      group.newToggle(diagram, label, toolId, state, iconTrue, iconFalse);
//      groupContainer.layout();
    } else System.err.println("cannot find group " + groupName);
  }

  public void newAction(Diagram diagram, String groupName, String label, String toolId, String icon) {
    Group group = getGroup(groupName);
    if (group != null) {
      group.newAction(diagram, label, toolId, icon);
//      groupContainer.layout();
    } else System.err.println("cannot find group " + groupName);
  }

	public void newTool(Diagram diagram, String groupName, String label, String toolId, boolean isEdge, String icon) {
		Group group = getGroup(groupName);
		if (group != null) {
			group.newTool(diagram, label, toolId, isEdge, icon);
		} else
			System.err.println("cannot find group " + groupName);
	}
	  


  public void reset() {
	  System.err.println("Palette.reset called.");
//	    throw new RuntimeException("Not implemented yet");
//    for (Group group : groups)
//      group.resetButtons();
//    getGroup("Diagram").getToolLabelled("Select").select();
  }

  public void writeXML(PrintStream out) {
	    throw new RuntimeException("Not implemented yet");
//    out.print("<Palette>");
//    for (Group group : groups) {
//      if (!group.getName().equals("Diagram")) {
//        group.writeXML(out);
//      }
//    }
//    out.print("</Palette>");
  }

  public void deselect() {
//	    throw new RuntimeException("Not implemented yet");
//    for (Group group : groups)
//      group.deselect();
  }
  
  public void removeAny(Diagram diagram, String toolId) {
	    throw new RuntimeException("Not implemented yet");
//	for (Group group : groups) {
//	  Tool toolToBeRemoved = null;
//	  for(Tool tool : group.tools) {
//		if(tool.id.equals(toolId)) {
//	      toolToBeRemoved = tool; break;
//		}
//	  }
//	  group.removeTool(toolToBeRemoved);
//	}
  }

  public void renameAny(Diagram diagram, final String newName, final String oldName) {
	    throw new RuntimeException("Not implemented yet");
//	for (Group group : groups) {
//	  for(Tool tool : group.tools) {
//		if(tool.id.equals(oldName)) {
//			tool.setID(newName);
//		}
//	  }
//	}
  }
  
  public TreeView<String> getToolBar() {
	  return tree;
  }
  
}
