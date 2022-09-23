package window.elements;

import data.exporter.Exporter;
import data.exporter.MapExporter;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ExportWindow extends JDialog {

    private File file = null;
    private window.Window window;

    private ExportWindow dialogueInstance;

    private JButton selectedFile, export;
    private JLabel errorMessages;

    private JCheckBox exportTileNames, exportFreeNames, exportAreaNames;

    public ExportWindow(window.Window w) {
        dialogueInstance = this;
        this.window = w;

        this.setTitle("Export Map");
        this.setModal(true);
        this.setLayout(null);
        this.setPreferredSize(new Dimension(216, 239));
        this.pack();
        this.setResizable(false);

        if(MenuBar.lastExport != null) file = MenuBar.lastExport;
        else if(MenuBar.lastSave != null) file = new File(MenuBar.lastSave.getAbsolutePath().substring(0, MenuBar.lastSave.getAbsolutePath().length()-4) + "map");

        selectedFile = new JButton("Select File...");
        this.getContentPane().add(selectedFile);
        selectedFile.setBounds(10, 10, 180, 20);

        if(file != null){
            String path = file.getAbsolutePath();
            if(path.length() > 20) path = "..." + path.substring(path.length() - 17);

            selectedFile.setText(path);
        }

        selectedFile.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(){
                public void approveSelection() {
                    File f = getSelectedFile();

                    boolean endsWithFileType = false;
                    for(Exporter exp: MenuBar.exporter) {
                        if(exp.getFileFilter().accept(f)) {
                            endsWithFileType = true;
                            break;
                        }
                    }

                    if(!endsWithFileType) setSelectedFile( new File(f.getAbsolutePath() + getFileFilter().getDescription()));
                    f = getSelectedFile();

                    if(f.exists()) {
                        int n = JOptionPane.showOptionDialog(this, "The file already exists, should it be replaced?", "File exists", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null, new String[]{"Yes", "No"}, "No");
                        if(n == 0) super.approveSelection();
                    } else super.approveSelection();
                }
            };
           if(file != null) chooser.setSelectedFile(file);

            chooser.setOpaque(true);

            chooser.setAcceptAllFileFilterUsed(false);
            for(Exporter exp: MenuBar.exporter) chooser.addChoosableFileFilter(exp.getFileFilter());

            int returnVal = chooser.showDialog(new JButton(""), "Export File");
            if(returnVal == JFileChooser.APPROVE_OPTION){
                file = chooser.getSelectedFile();

                String path = file.getAbsolutePath();
                if(path.length() > 20) path = "..." + path.substring(path.length() - 17);

                selectedFile.setText(path);
            }
        });

        errorMessages = new JLabel("");
        errorMessages.setForeground(Color.RED.darker());
        this.getContentPane().add(errorMessages);
        errorMessages.setBounds(10, 165,85,25);

        export = new JButton("Export");
        this.getContentPane().add(export);
        export.setBounds(105, 165,85,25);

        export.addActionListener(e -> {
            if(file == null) {
                errorMessages.setText("Select a file");
                return;
            }

            MenuBar.lastExport = file;

            Exporter exporter = MapExporter.getInstance();
            for(Exporter exp: MenuBar.exporter) {
                if(exp.getFileFilter().accept(file)) {
                    exporter = exp;
                    break;
                }
            }

            exporter.setOptions(exportTileNames.isSelected(), exportFreeNames.isSelected(), exportAreaNames.isSelected());
            exporter.exportToFile(window.getMap(), file);
            dialogueInstance.setVisible(false);
        });

        exportTileNames = new JCheckBox("TileLayer with Name");
        this.getContentPane().add(exportTileNames);
        exportTileNames.setBounds(10, 40, 180, 20);

        exportFreeNames = new JCheckBox("FreeLayer with Name");
        this.getContentPane().add(exportFreeNames);
        exportFreeNames.setBounds(10, 70, 180, 20);

        exportAreaNames = new JCheckBox("AreaLayer with Name");
        this.getContentPane().add(exportAreaNames);
        exportAreaNames.setBounds(10, 100, 180, 20);
    }
}
