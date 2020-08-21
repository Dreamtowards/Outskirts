package outskirts.util.tools;

import outskirts.storage.dat.DATObject;
import outskirts.storage.dat.DST;
import outskirts.storage.dat.DSTUtils;
import outskirts.util.logging.Log;
import outskirts.util.nbt.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.util.List;
import java.util.Map;

public class DATBrowser extends JFrame {

    private DefaultMutableTreeNode topNode = new DefaultMutableTreeNode(new NodeData(new DATObject(), "Just drop file here"));
    private JTree treeView = new JTree(topNode);
    private DefaultMutableTreeNode selectedNode;

    private File currentFile;
    private Object currentDAT;

    public DATBrowser() {

//        add(new JPanel());
        setContentPane(new JPanel());
        getContentPane().setLayout(new BorderLayout());
        {
            JScrollPane scrollPane = new JScrollPane();
            getContentPane().add(scrollPane);
            scrollPane.add(treeView);
            scrollPane.setViewportView(treeView);
        }

        treeView.addMouseListener(new MouseListener() {
            private long prevClickTime = 0;
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = treeView.getClosestRowForLocation(e.getX(), e.getY());
                treeView.setSelectionRow(row);
                selectedNode = (DefaultMutableTreeNode)treeView.getLastSelectedPathComponent();

                if (SwingUtilities.isLeftMouseButton(e) && System.currentTimeMillis() < prevClickTime + 500) {

                    editNode(selectedNode);
                }
                prevClickTime = System.currentTimeMillis();
            }
            @Override public void mousePressed(MouseEvent e) { }
            @Override public void mouseReleased(MouseEvent e) { }
            @Override public void mouseEntered(MouseEvent e) { }
            @Override public void mouseExited(MouseEvent e) { }
        });

        this.initWindow();
    }

    public void editNode(DefaultMutableTreeNode node) {

        NodeData nodeData = (NodeData) node.getUserObject();

        byte dataType = DST.type(nodeData.dat);
        if (dataType <= NBT.STRING) {
            String result = JOptionPane.showInputDialog(DATBrowser.this, "Edit Value", DST.toString(nodeData.dat));

            if (result != null) {
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    DataOutput dataOutput = new DataOutputStream(outputStream);
                    if (dataType == NBT.BYTE) {
                        new NBTTagByte(Byte.parseByte(result)).write(dataOutput);
                    } else if (dataType == NBT.SHORT) {
                        new NBTTagShort(Short.parseShort(result)).write(dataOutput);
                    } else if (dataType == NBT.INT) {
                        new NBTTagInt(Integer.parseInt(result)).write(dataOutput);
                    } else if (dataType == NBT.LONG) {
                        new NBTTagLong(Long.parseLong(result)).write(dataOutput);
                    } else if (dataType == NBT.FLOAT) {
                        new NBTTagFloat(Float.parseFloat(result)).write(dataOutput);
                    } else if (dataType == NBT.DOUBLE) {
                        new NBTTagDouble(Double.parseDouble(result)).write(dataOutput);
                    } else if (dataType == NBT.BYTE_ARRAY) {
                        String[] arrstr = result.substring(2, result.length() - 1).split(",+");
                        byte[] bytes = new byte[arrstr.length];
                        for (int i = 0;i < arrstr.length;i++)
                            bytes[i] = Byte.parseByte(arrstr[i].trim());
                        new NBTTagByteArray(bytes).write(dataOutput);
                    } else if (dataType == NBT.STRING) {
                        new NBTTagString(result).write(dataOutput);
                    }
                    if (true)
                        throw new RuntimeException("Cant Edit");

                    nodeData.dat = DST.read(new ByteArrayInputStream(outputStream.toByteArray()), dataType);
                }catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, ex.toString());
                }
            }
        } else {
            showMessage("Unsupported type.");
        }
    }



    private DefaultMutableTreeNode readDataToTree(Object dat, String displayname) {
        int type = DST.type(dat);
        if (type <= DST.STRING) {
            return new DefaultMutableTreeNode(new NodeData(dat, String.format("%s (%s)", displayname, DST.TYPES_NAME[type])));
        } else if (type == DST.LIST) {
            List ls = (List)dat;
            DefaultMutableTreeNode lsNode = new DefaultMutableTreeNode(new NodeData(dat, String.format("%s [LIST] %slength", displayname, ls.size())));
            for (int i = 0;i < ls.size();i++) {
                lsNode.add(readDataToTree(ls.get(i), ""+i));
            }
            return lsNode;
        } else if (type == DST.MAP) {
            Map<String, Object> mp = (Map) dat;
            DefaultMutableTreeNode mpNode = new DefaultMutableTreeNode(new NodeData(mp, String.format("%s [MAP] %ssize", displayname, mp.size())));
            for (String key : mp.keySet()) {
                mpNode.add(readDataToTree(mp.get(key), key + ": "));
            }
            return mpNode;
        }
        throw new RuntimeException();
    }


    private void openFile(File file) {
        try {
            if (currentDAT != null) {
                int opCode = showConfirmMessage("Save current file?");
                if (opCode == JOptionPane.YES_OPTION) saveCurrent();
                else if (opCode == JOptionPane.CANCEL_OPTION) return;
            }

            currentDAT = DSTUtils.read(new FileInputStream(file));

            topNode = readDataToTree(currentDAT, file.getPath());

            treeView.setModel(new DefaultTreeModel(topNode, false));
            treeView.revalidate();

            setTitle(file.toString());
            currentFile = file;
        } catch (Exception ex) {
            ex.printStackTrace();
            clearWork();
            showMessage(String.format("Failed to open '%s' (%s)", file, ex));
        }
    }

    private void clearWork() {
        currentFile = null;
        currentDAT = null;
        setTitle("DATBrowser");

        treeView.setModel(null);
        treeView.revalidate();
    }

    private void saveCurrent() {
        if (currentDAT == null)
            return;
        try {
            DSTUtils.write((Map)currentDAT, new FileOutputStream(currentFile));
            Log.LOGGER.info("Saved.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }










    private void initWindow() {

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveCurrent();
                System.exit(0);
            }
        });

        setTitle("DATBrowser");
        setResizable(true);
        setVisible(true);
        setSize(500, 300);

        new FileDrop(getContentPane(), files -> {
            if (files.length == 1) {
                openFile(files[0]);
            } else {
                showMessage("just can open one file");
            }
        });

    }






    public static void main(String[] args) {
        new DATBrowser();
    }

    private static void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    private static int showConfirmMessage(String message) {
        return JOptionPane.showInternalConfirmDialog(null, message);
    }

    private static class NodeData {

        private Object dat;
        private String prefix;

        public NodeData(Object dat, String prefix){
            this.dat = dat;
            this.prefix = prefix;
        }

        @Override
        public String toString() {
            return prefix + " " + ((DST.type(dat) == DST.LIST || DST.type(dat) == DST.MAP) ? "" : DST.toString(dat));
        }
    }
}
