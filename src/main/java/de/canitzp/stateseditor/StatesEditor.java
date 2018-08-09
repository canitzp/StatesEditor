package de.canitzp.stateseditor;

import com.sun.deploy.panel.PropertyTreeModel;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StatesEditor {

    public static JFrame frame = new JFrame("States Editor");

    public static void main(String[] args){
        JPanel main = new JPanel(new BorderLayout());
        JTree chunkTree = new JTree(new DefaultMutableTreeNode("States"));
        chunkTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2){
                    DefaultMutableTreeNode node = ((DefaultMutableTreeNode) chunkTree.getLastSelectedPathComponent());
                    System.out.println(node);
                }
            }
        });
        main.add(new JScrollPane(chunkTree), BorderLayout.CENTER);
        JMenuBar bar = new JMenuBar();
        JMenu menuFile = new JMenu("File");
        JMenuItem itemOpenWorld = new JMenuItem("Open World Folder");
        itemOpenWorld.setMnemonic('W');
        itemOpenWorld.addActionListener(e -> {
            JFileChooser worldChooser = new JFileChooser();
            worldChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if(worldChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION){
                File statesFolder = worldChooser.getSelectedFile();
                if(statesFolder.getName().equals("states") && new File(statesFolder, "forced_chunks.json").exists()){
                    System.out.println("Loaded states dir at " + statesFolder.toString());
                    List<Json.Chunk> chunks = loadChunks(new File(statesFolder, "chunks"));
                    List<Json.District> districts = loadDistricts(new File(statesFolder, "districts"));
                    List<Json.Municipality> municipalities = loadMunicipalities(new File(statesFolder, "municipalitites"));
                    List<Json.State> states = loadStates(new File(statesFolder, "states"));

                    System.out.println(chunks);
                    System.out.println(districts);
                    System.out.println(municipalities);
                    System.out.println(states);

                    DefaultMutableTreeNode chunkNode = new DefaultMutableTreeNode("Chunks");
                    for(Json.Chunk chunk : chunks){
                        DefaultMutableTreeNode node = new DefaultMutableTreeNode(String.valueOf(chunk.x) + "_" + String.valueOf(chunk.z));
                        node.add(create("x: ", chunk.x));
                        node.add(create("z: ", chunk.z));
                        node.add(new DefaultMutableTreeNode("price: " + chunk.price));
                        node.add(new DefaultMutableTreeNode("district: " + chunk.district));
                        node.add(new DefaultMutableTreeNode("created: " + chunk.created + " " + new Date(chunk.created).toInstant().atZone(ZoneId.of("UTC"))));
                        node.add(new DefaultMutableTreeNode("creator: " + chunk.creator));
                        node.add(new DefaultMutableTreeNode("changed: " + chunk.changed + " " + new Date(chunk.changed).toInstant().atZone(ZoneId.of("UTC"))));
                        node.add(new DefaultMutableTreeNode("type: " + chunk.type));
                        node.add(new DefaultMutableTreeNode("owner: " + chunk.owner));
                        node.add(new DefaultMutableTreeNode("last_tax_collection: " + chunk.last_tax_collection));
                        node.add(new DefaultMutableTreeNode("last_save: " + chunk.last_save + " " + new Date(chunk.last_save).toInstant().atZone(ZoneId.of("UTC"))));
                        chunkNode.add(node);
                    }

                    DefaultMutableTreeNode root = (DefaultMutableTreeNode) chunkTree.getModel().getRoot();
                    root.add(chunkNode);
                    ((DefaultTreeModel) chunkTree.getModel()).reload(root);
                }
            }
        });
        menuFile.add(itemOpenWorld);
        bar.add(menuFile);

        frame.setLayout(new BorderLayout());
        frame.add(bar, BorderLayout.NORTH);
        frame.add(main, BorderLayout.CENTER);

        frame.setMinimumSize(new Dimension(Math.round(Toolkit.getDefaultToolkit().getScreenSize().width / 2F), Math.round(Toolkit.getDefaultToolkit().getScreenSize().height / 2F)));
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private static List<Json.Chunk> loadChunks(File chunkFolder){
        List<Json.Chunk> chunks = new ArrayList<>();
        for(File dir : FileUtils.listFilesAndDirs(chunkFolder, DirectoryFileFilter.DIRECTORY, TrueFileFilter.INSTANCE)){
            for(File json : FileUtils.listFiles(dir, new String[]{"json"}, true)){
                chunks.add(Json.read(json, Json.Chunk.class));
            }
        }
        return chunks;
    }

    private static List<Json.District> loadDistricts(File districtFolder){
        List<Json.District> districts = new ArrayList<>();
        for(File json : FileUtils.listFiles(districtFolder, new String[]{"json"}, true)){
            districts.add(Json.read(json, Json.District.class));
        }
        return districts;
    }

    private static List<Json.Municipality> loadMunicipalities(File municipalityFolder){
        List<Json.Municipality> municipalities = new ArrayList<>();
        for(File json : FileUtils.listFiles(municipalityFolder, new String[]{"json"}, true)){
            municipalities.add(Json.read(json, Json.Municipality.class));
        }
        return municipalities;
    }

    private static List<Json.State> loadStates(File statesFolder){
        List<Json.State> states = new ArrayList<>();
        for(File json : FileUtils.listFiles(statesFolder, new String[]{"json"}, true)){
            states.add(Json.read(json, Json.State.class));
        }
        return states;
    }

    private static MutableTreeNode create(String key, Object value){
        DefaultMutableTreeNode nd = new DefaultMutableTreeNode(key);
        nd.add(new DefaultMutableTreeNode(value));
        return nd;
    }

}
