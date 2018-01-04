package tool.xmodeler;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

//import com.ceteva.oleBridge.OleBridgeClient;
//import com.ceteva.undo.UndoClient;

import engine.Machine;
import tool.clients.browser.ModelBrowserClient;
import tool.clients.diagrams.DiagramClient;
import tool.clients.dialogs.DialogsClient;
import tool.clients.dialogs.notifier.NotificationType;
import tool.clients.dialogs.notifier.NotifierDialog;
import tool.clients.editors.EditorClient;
import tool.clients.forms.FormsClient;
import tool.clients.menus.MenuClient;
import tool.clients.oleBridge.OleBridgeClient;
import tool.clients.screenGeneration.ScreenGenerationClient;
import tool.clients.undo.UndoClient;
import tool.clients.workbench.WorkbenchClient;
import tool.console.Console;
import tool.console.ConsoleClient;
import xos.OperatingSystem;

public class XModelerBeforeEdtorSplitter {

  // XModeler is a tool that controls and is controlled by the XMF VM that runs
  // on the XMF operating system.

  private static Integer DEVICE_ZOOM_PERCENT = null;

  static final String    NAME                = "XModeler";
  static String          busyMessage         = "";
  static int             TOOL_X              = 100;
  static int             TOOL_Y              = 100;
  static int             TOOL_WIDTH          = 1200;
  static int             TOOL_HEIGHT         = 900;
  static OperatingSystem xos                 = new OperatingSystem();
  static Shell           XModeler            = new Shell(SWT.BORDER | SWT.SHELL_TRIM);
  static SashForm        outerSash           = null;
  static SashForm        rightSash           = null;
  static CTabFolder      editorTabFolder     = null;
  static CTabFolder      propertyTabFolder   = null;
  static CTabFolder      browserTabFolder    = null;
  static Display         display             = null;
  static Menu            menuBar             = null;
  static String          projDir             = null;
  static String          loadedImagePath     = null;
  static String          version             = null;
  static String[]        copyOfArgs          = null;
  static boolean         showLoad            = false;
  public static String   textEditorClass     = "tool.clients.editors.TextEditor";

  // private static boolean overwrite(final String file) {
  // final boolean[] result = new boolean[] { false };
  // XModeler.getDisplay().syncExec(new Runnable() {
  // public void run() {
  // try {
  // result[0] = MessageDialog.openConfirm(XModeler, "Overwite", "Overwrite " + file);
  // } catch (Throwable t) {
  // t.printStackTrace();
  // }
  // }
  // });
  // return result[0];
  // }

  public static String attributeValue(Node node, String name) {
    NamedNodeMap attrs = node.getAttributes();
    for (int i = 0; i < attrs.getLength(); i++) {
      Attr attribute = (Attr) attrs.item(i);
      if (attribute.getName().equals(name)) return attribute.getValue();
    }
    return null;
  }

  public static String attributeValue(Node node, String name, String defaultValue) {
    String value = attributeValue(node, name);
    if (value == null)
      return defaultValue;
    else return value;
  }

  private static Listener closeListener() {
    return new Listener() {
      public void handleEvent(Event event) {
        if (loadedImagePath == null) {
          WorkbenchClient.theClient().shutdownEvent();
        } else {
          WorkbenchClient.theClient().shutdownAndSaveEvent(loadedImagePath, inflationPath());
        }
        event.doit = false;
      }
    };
  }

  public static String encodeXmlAttribute(String str) {
    if (str == null) return null;

    int len = str.length();
    if (len == 0) return str;

    StringBuffer encoded = new StringBuffer();
    for (int i = 0; i < len; i++) {
      char c = str.charAt(i);
      char cc = (i + 1) < (len - 1) ? str.charAt(i + 1) : 0;
      if (c == '<')
        encoded.append("&lt;");
      else if (c == '\n') {
        encoded.append("&#13;");
        if (cc == '\r') i++;
      } else if (c == '\r') {
        encoded.append("&#10;");
        if (cc == '\n') i++;
      } else if (c == '\"')
        encoded.append("&quot;");
      else if (c == '>')
        encoded.append("&gt;");
      else if (c == '\'')
        encoded.append("&apos;");
      else if (c == '&')
        encoded.append("&amp;");
      else encoded.append(c);
    }

    return encoded.toString();
  }

  public static int getDeviceZoomPercent() {
    if (DEVICE_ZOOM_PERCENT == null) DEVICE_ZOOM_PERCENT = 100;
    return DEVICE_ZOOM_PERCENT;
    // return(display.getDPI().x*100/96);
  }

  private static String getImage(String[] args) {
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-image")) return args[i + 1];
    }
    return null;
  }

  private static boolean getImageDialog(String[] args) {
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-imagedialog")) {
        if (args[i + 1].equalsIgnoreCase("true")) {
          return true;
        } else {
          return false;
        }
      }
    }
    return true;
  }

  public static Menu getMenuBar() {
    return menuBar;
  }

  private static String getVersion(String[] args) {
    for (int i = 0; i < args.length; i++) {
      if (args[i].startsWith("version:")) { return args[i].replace("version:", ""); }
    }
    return "";
  }

  public static Shell getXModeler() {
    return XModeler;
  }

  private static String img2xml(String imgString) {
    File img = new File(imgString);
    File path = img.getParentFile();
    String imgFile = img.getName();
    String xmlFile = imgFile.substring(0, imgFile.lastIndexOf(".")) + ".xml";
    File xml = new File(path, xmlFile);
    return xml.getAbsolutePath();
  }

  // private static boolean checkHiRes(String[] args) {
  // for (int i = 0; i < args.length; i++) {
  // if (args[i].equals("-hi-res"))
  // return "true".equals(args[i + 1]);
  // }
  // return false;
  // }

  public static void inflate(String inflationPath) {
    inflationPath = img2xml(loadedImagePath);
    // System.err.println("loadedImagePath: " + loadedImagePath);
    // System.err.println("inflationPath: " + inflationPath);
    // new RuntimeException("XML").printStackTrace();
    try {
      File fXmlFile = new File(inflationPath);
      if (fXmlFile.exists()) {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(fXmlFile);
        doc.getDocumentElement().normalize();
        String root = doc.getDocumentElement().getNodeName();
        Node node = doc.getDocumentElement();
        if (root.equals("XModeler")) {
          final int x = Integer.parseInt(attributeValue(node, "x"));
          final int y = Integer.parseInt(attributeValue(node, "y"));
          final int width = Integer.parseInt(attributeValue(node, "width"));
          final int height = Integer.parseInt(attributeValue(node, "height"));
          XModeler.getDisplay().syncExec(new Runnable() {
            public void run() {
              XModeler.setLocation(x, y);
              XModeler.setSize(width, height);
            }
          });
          ModelBrowserClient.theClient().inflateXML(doc);
          DiagramClient.theClient().inflateXML(doc);
          MenuClient.theClient().inflateXML(doc);
          EditorClient.theClient().inflateXML(doc);
          ConsoleClient.theConsole().inflateXML(doc);
          FormsClient.theClient().inflateXML(doc);
        }
      }
    } catch (IOException e) {
      e.printStackTrace(System.err);
    } catch (ParserConfigurationException e) {
      e.printStackTrace(System.err);
    } catch (SAXException e) {
      e.printStackTrace(System.err);
    } catch (Throwable t) {
      t.printStackTrace(System.err);
    }
  }

  private static String inflationPath() {
    if (loadedImagePath != null) {
      String name = loadedImagePath.substring(0, loadedImagePath.lastIndexOf('.'));
      return name + ".xml";
    }
    return null;
  }

  private static String lookupArg(String string, String[] args) {
    for (int i = 0; i < args.length; i++) {
      if (args[i].equals("-arg") && args[i + 1].startsWith("projects:")) { return args[i + 1].substring(9); }
    }
    return null;
  }

  public static void main(String[] args) {
    copyOfArgs = Arrays.copyOf(args, args.length);
    textEditorClass = args.length > 1 ? args[1] : "tool.clients.editors.TextEditor";
    startXOS(args[0]);
    startXModeler();
    startClients();
    startDispatching();
  }

  public static void removeBusyInformation() {
    busyMessage = "";
    setToolLabel();
  }

  public static void saveInflator(final String inflationPath) {
    XModeler.getDisplay().syncExec(new Runnable() {
      public void run() {
        try {
          if (inflationPath != null) {
            loadedImagePath = inflationPath.substring(0, inflationPath.lastIndexOf('.')) + ".img";
            setToolLabel();
            File file = new File(inflationPath);
            // FileOutputStream fout = new FileOutputStream(file);
            int x = XModeler.getLocation().x;
            int y = XModeler.getLocation().y;
            int width = XModeler.getSize().x;
            int height = XModeler.getSize().y;
            // PrintStream out = new PrintStream(fout);
            PrintStream out = new PrintStream(file, "UTF-8");
            out.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?><XModeler x='" + x + "' y='" + y + "' width='" + width + "' height = '" + height + "'>");
            ModelBrowserClient.theClient().writeXML(out);
            DiagramClient.theClient().writeXML(out);
            MenuClient.theClient().writeXML(out);
            EditorClient.theClient().writeXML(out);
            ConsoleClient.theConsole().writeXML(out);
            FormsClient.theClient().writeXML(out);
            out.print("</XModeler>");
            out.close();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });
  }

  private static void setImage(String[] args) {
    String defaultImage = getImage(args);
    if (defaultImage == null) throw new Error("you have not supplied an image in the initialisation args:\n" + Arrays.toString(args));
    if (!new File(defaultImage).exists()) throw new Error("the default image file must exist: " + defaultImage);
    boolean imageDialog = getImageDialog(args);
    String selectedImage = null;
    if (imageDialog || showLoad) {
      FileDialog dialog = new FileDialog(XModeler, SWT.OPEN);
      dialog.setText("Select the image file");
      dialog.setFilterExtensions(new String[] { "*.img" });
      dialog.setFileName(defaultImage);
      dialog.setFilterPath(projDir);
      selectedImage = dialog.open();
    }
    if (selectedImage != null && !selectedImage.equals(defaultImage)) {
      loadedImagePath = selectedImage;
      setToolLabel();
      for (int i = 0; i < args.length; i++) {
        if (args[i].equals("-image")) args[i + 1] = loadedImagePath;
      }
    }
  }

  private static void setProjectDirectory(String[] args) {
    projDir = lookupArg("projects", args);
    if (projDir == null) throw new Error("you have not set the project directory in the initialisation arguments:\n" + Arrays.toString(args));
  }

  public static void setToolLabel() {
    String path = loadedImagePath == null ? "NO_IMAGE_SET" : loadedImagePath;
    XModeler.setText(NAME + " " + version + " [" + path + "]" + busyMessage);
  }

  public static void showBusyInformation(String info) {
    busyMessage = info;
    setToolLabel();
  }

  public static void showMessage(String title, String message) {
    NotifierDialog.notify(title, message, NotificationType.values()[2]);
  }

  public static void shutdown() {
    XModeler.getDisplay().syncExec(new Runnable() {
      public void run() {
        try {
          XModeler.dispose();
        } catch (Throwable t) {
          t.printStackTrace();
        }
      }
    });
  }

  public static void startClients() {
    xos.newMessageClient("com.ceteva.text", new EditorClient());
    xos.newMessageClient("com.ceteva.mosaic", new WorkbenchClient());
    xos.newMessageClient("com.ceteva.menus", new MenuClient());
    xos.newMessageClient("com.ceteva.modelBrowser", new ModelBrowserClient());
    xos.newMessageClient("com.ceteva.diagram", new DiagramClient());
    xos.newMessageClient("com.ceteva.dialogs", new DialogsClient());
    xos.newMessageClient("com.ceteva.forms", new FormsClient());
    xos.newMessageClient("com.ceteva.undo", new UndoClient());
    xos.newMessageClient("com.ceteva.oleBridge", new OleBridgeClient());
    xos.newMessageClient("screenGeneration", new ScreenGenerationClient()); // BB
  }

  public static void startDispatching() {
    while (!XModeler.isDisposed()) {
      if (!display.readAndDispatch()) display.sleep();
    }
    display.dispose();
    System.exit(0);
  }

  public static void startXModeler() {
    display = Display.getDefault();

    DEVICE_ZOOM_PERCENT = display.getDPI().x * 100 / 75;
    System.err.println("The zoom for this device was detected as " + DEVICE_ZOOM_PERCENT + "%.");

    setToolLabel();
    Image windowIcon = new Image(XModeler.getDisplay(), "icons/shell/mosaic32.gif");
    XModeler.setImage(windowIcon);
    XModeler.setLayout(new FillLayout());
    XModeler.setLocation(TOOL_X, TOOL_Y);
    XModeler.setSize(new org.eclipse.swt.graphics.Point(TOOL_WIDTH, TOOL_HEIGHT));
    XModeler.addListener(SWT.Close, closeListener());
    menuBar = new Menu(XModeler, SWT.BAR);
    outerSash = new SashForm(XModeler, SWT.HORIZONTAL);
    browserTabFolder = new CTabFolder(outerSash, SWT.BORDER);
    rightSash = new SashForm(outerSash, SWT.VERTICAL);
    editorTabFolder = new CTabFolder(rightSash, SWT.BORDER);
    propertyTabFolder = new CTabFolder(rightSash, SWT.BORDER);
    ToolBar propertyToolbar = new ToolBar(propertyTabFolder, SWT.HORIZONTAL | SWT.FLAT);
    editorTabFolder.setMaximizeVisible(true);
    editorTabFolder.setMinimizeVisible(true);
    propertyTabFolder.setMaximizeVisible(true);
    propertyTabFolder.setMinimizeVisible(true);
    browserTabFolder.setMaximizeVisible(true);
    browserTabFolder.setMinimizeVisible(true);
    propertyTabFolder.setTopRight(propertyToolbar);
//    ModelBrowserClient.start(browserTabFolder, SWT.LEFT);
    outerSash.setWeights(new int[] { 1, 5 });
//    EditorClient.start(editorTabFolder, SWT.BORDER);
//    editorTabFolder.addCTabFolder2Listener(new EditorResizeListener());
//    propertyTabFolder.addCTabFolder2Listener(new PropertyResizeListener());
//    browserTabFolder.addCTabFolder2Listener(new BrowserResizeListener());
//    DiagramClient.start(editorTabFolder);
//    FormsClient.start(propertyTabFolder, propertyToolbar, SWT.BORDER);
    ScreenGenerationClient.start(propertyTabFolder); // BB
//    Console.start(propertyTabFolder);
    rightSash.setWeights(new int[] { 2, 1 });
    XModeler.open();

    XModeler.getDisplay().timerExec(3000, new Runnable() {

      public void run() {
        Menu exitMenu = new Menu(menuBar);
        MenuItem exit = new MenuItem(menuBar, SWT.CASCADE);
        exit.setText("Debug");
        exit.setMenu(exitMenu);
        MenuItem panic = new MenuItem(exitMenu, SWT.NONE);
        panic.setText("VM Panic");
        panic.addListener(SWT.Selection, new Listener() {
          public void handleEvent(Event e) {
            Machine.interrupt = true;
          }
        });
        MenuItem loadImage = new MenuItem(menuBar.getItem(0).getMenu(), SWT.NONE);
        loadImage.setText("Load Image");
        loadImage.addListener(SWT.Selection, new Listener() {
          public void handleEvent(Event e) {
            XModeler.dispose();
            busyMessage = "";
            TOOL_X = 100;
            TOOL_Y = 100;
            TOOL_WIDTH = 1200;
            TOOL_HEIGHT = 900;
            xos = new OperatingSystem();
            XModeler = new Shell(SWT.BORDER | SWT.SHELL_TRIM);
            display = null;
            menuBar = null;
            projDir = null;
            loadedImagePath = null;
            showLoad = true;
            main(copyOfArgs);
          }
        });

      }
    });
  }

  static void startXOS(String initFile) {
    final String[] args = xos.getInitArgs(initFile);
    // /*QUICKFIX FOR HI_RES*/FormsClient.HIGH_RESOLUTION = checkHiRes(args);
    setProjectDirectory(args);
    version = getVersion(args);
    setImage(args);
    Thread t = new Thread() {
      public void run() {
        try {
          xos.init(args);
        } catch (Throwable t) {
          System.out.println(t);
          t.printStackTrace();
        }
      }
    };
    t.start();
  }

  public static void maximiseEditors() {
    outerSash.setMaximizedControl(rightSash);
    rightSash.setMaximizedControl(editorTabFolder);
  }

  public static void minimiseEditors() {
    rightSash.setMaximizedControl(null);
    outerSash.setMaximizedControl(null);
  }

  public static void maximiseProperties() {
    outerSash.setMaximizedControl(rightSash);
    rightSash.setMaximizedControl(propertyTabFolder);
  }

  public static void minimiseProperties() {
    rightSash.setMaximizedControl(null);
    outerSash.setMaximizedControl(null);
  }

  public static void maximiseBrowser() {
    outerSash.setMaximizedControl(browserTabFolder);
  }

  public static void minimiseBrowser() {
    outerSash.setMaximizedControl(null);
  }

}
