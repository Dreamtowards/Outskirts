package outskirts.util.tools;

import outskirts.util.nbt.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class NBTBrowser extends JFrame {

    private JScrollPane scrollPane = new JScrollPane();
    private DefaultMutableTreeNode topNode = new DefaultMutableTreeNode(new NBTNode(new NBTTagCompound(), "Just drop file here"));
    private JTree treeView = new JTree(topNode);
    private DefaultMutableTreeNode selectedNode;

    private File currentFile;
    private NBT currentNBT;

    public NBTBrowser() {


        add(new JPanel());
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(scrollPane);
        scrollPane.add(treeView);
        scrollPane.setViewportView(treeView);

        treeView.addMouseListener(new MouseListener() {
            private long prevClickTime = 0;
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = treeView.getClosestRowForLocation(e.getX(), e.getY());
                treeView.setSelectionRow(row);
                selectedNode = (DefaultMutableTreeNode)treeView.getLastSelectedPathComponent();

                if (SwingUtilities.isLeftMouseButton(e) && System.currentTimeMillis() - prevClickTime < 500) {

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

        NBTNode nbtNode = (NBTNode)node.getUserObject();

        if (nbtNode.nbt.type() <= NBT.STRING) {
            String result = JOptionPane.showInputDialog(NBTBrowser.this, "Edit Value", nbtNode.getNBT().toString());

            if (result != null) {
                try {
                    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                    DataOutput dataOutput = new DataOutputStream(outputStream);
                    byte type = nbtNode.getNBT().type();
                    if (type == NBT.BYTE) {
                        new NBTTagByte(Byte.parseByte(result)).write(dataOutput);
                    } else if (type == NBT.SHORT) {
                        new NBTTagShort(Short.parseShort(result)).write(dataOutput);
                    } else if (type == NBT.INT) {
                        new NBTTagInt(Integer.parseInt(result)).write(dataOutput);
                    } else if (type == NBT.LONG) {
                        new NBTTagLong(Long.parseLong(result)).write(dataOutput);
                    } else if (type == NBT.FLOAT) {
                        new NBTTagFloat(Float.parseFloat(result)).write(dataOutput);
                    } else if (type == NBT.DOUBLE) {
                        new NBTTagDouble(Double.parseDouble(result)).write(dataOutput);
                    } else if (type == NBT.BYTE_ARRAY) {
                        String[] arrstr = result.substring(2, result.length() - 1).split(",+");
                        byte[] bytes = new byte[arrstr.length];
                        for (int i = 0;i < arrstr.length;i++)
                            bytes[i] = Byte.parseByte(arrstr[i].trim());
                        new NBTTagByteArray(bytes).write(dataOutput);
                    } else if (type == NBT.STRING) {
                        new NBTTagString(result).write(dataOutput);
                    }

                    nbtNode.getNBT().read(new DataInputStream(new ByteArrayInputStream(outputStream.toByteArray())));
                }catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, ex.toString());
                }
            }
        } else {
            showMessage("Unsupported type.");
        }
    }


    private void readToTree(DefaultMutableTreeNode parent, NBT nbt, String prefix) {

        if (nbt.type() == NBT.END) {
            parent.add(new DefaultMutableTreeNode(new NBTNode(nbt, "[END] "+prefix)));
        } else if (nbt.type() == NBT.BYTE) {
            parent.add(new DefaultMutableTreeNode(new NBTNode(nbt, "[BYTE] " +prefix)));
        } else if (nbt.type() == NBT.SHORT) {
            parent.add(new DefaultMutableTreeNode(new NBTNode(nbt, "[SHORT] " +prefix)));
        } else if (nbt.type() == NBT.INT) {
            parent.add(new DefaultMutableTreeNode(new NBTNode(nbt, "[INT] " +prefix)));
        } else if (nbt.type() == NBT.LONG) {
            parent.add(new DefaultMutableTreeNode(new NBTNode(nbt, "[LONG] " +prefix)));
        } else if (nbt.type() == NBT.FLOAT) {
            parent.add(new DefaultMutableTreeNode(new NBTNode(nbt, "[FLOAT] " +prefix)));
        } else if (nbt.type() == NBT.DOUBLE) {
            parent.add(new DefaultMutableTreeNode(new NBTNode(nbt, "[DOUBLE] " +prefix)));
        } else if (nbt.type() == NBT.BYTE_ARRAY) {
            parent.add(new DefaultMutableTreeNode(new NBTNode(nbt, "[BYTE_ARRAY] " +prefix)));
        } else if (nbt.type() == NBT.STRING) {
            parent.add(new DefaultMutableTreeNode(new NBTNode(nbt, "[STRING] " +prefix)));
        } else if (nbt.type() == NBT.LIST) {
            NBTTagList list = (NBTTagList) nbt;
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new NBTNode(nbt, "[LIST] " +prefix+ list.size() + " entry"));
            parent.add(childNode);
            for (NBT child : list) {
                readToTree(childNode, child, "");
            }
        } else if (nbt.type() == NBT.COMPOUND) {
            NBTTagCompound tagCompound = (NBTTagCompound) nbt;
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(new NBTNode(tagCompound, "[COMPOUND] " +prefix+ tagCompound.size() + " entry"));
            parent.add(childNode);
            for (String key : tagCompound.keySet()) {
                readToTree(childNode, tagCompound.getTag(key), key + ": ");
            }
        }
    }


    private void openFile(File file) {
        try {
            if (currentNBT != null) {
                int op = showConfirmMessage("Save current file?");
                if (op == JOptionPane.YES_OPTION)
                    saveCurrent();
                else if (op == JOptionPane.CANCEL_OPTION)
                    return;
            }

            currentNBT = NBTUtils.read(new FileInputStream(file));

            topNode = new DefaultMutableTreeNode(new NBTNode(new NBTTagCompound(), file.getPath()));
            readToTree(topNode, currentNBT, "");

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
        currentNBT = null;
        setTitle("NBTBrowser");

        treeView.setModel(null);
        treeView.revalidate();
    }

    private void saveCurrent() {
        if (currentNBT == null) {
            return;
        }

        try {
            NBTUtils.write(currentNBT, new FileOutputStream(currentFile));
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

        setTitle("NBTBrowser");
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
        new NBTBrowser();
    }

    private static void showMessage(String message) {
        JOptionPane.showMessageDialog(null, message);
    }

    private static int showConfirmMessage(String message) {
        return JOptionPane.showInternalConfirmDialog(null, message);
    }

    private static class NBTNode {

        private NBT nbt;
        private String prefix;

        public NBTNode(NBT nbt, String prefix){
            this.nbt = nbt;
            this.prefix = prefix;
        }

        public NBT getNBT() {
            return nbt;
        }

        @Override
        public String toString() {
            return prefix + ((nbt.type() == NBT.LIST || nbt.type() == NBT.COMPOUND) ? "" : nbt.toString());
        }
    }
}
