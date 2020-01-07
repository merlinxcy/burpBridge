package burp;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Console;
import java.io.File;
import java.io.PrintWriter;

import com.sun.deploy.panel.JavaPanel;
import com.sun.scenario.effect.impl.sw.java.JSWBlend_COLOR_BURNPeer;
import xeldax.BurpContextMenu;
import xeldax.Config;
import xeldax.SmokeTest;

import javax.swing.*;

public class BurpExtender implements  IBurpExtender, ITab, ActionListener {
    private IBurpExtenderCallbacks callbacks;
    private PrintWriter stderr;
    private PrintWriter stdout;
    // UI 相关配置
    private JPanel mainPanel;
    private JTextField pythonPath;
    private JTextField pythonBinaryPath;
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks){
        this.callbacks = callbacks;
        this.stderr = new PrintWriter(callbacks.getStderr(),true);
        this.stdout = new PrintWriter(callbacks.getStdout(), true);
        stdout.println("[*]Welcome to python Bride Burpsuite");
        SmokeTest.test();
        //Start
        callbacks.setExtensionName("BridgeToPython");
        callbacks.registerContextMenuFactory(new BurpContextMenu(callbacks));
        //创建UI
        buildUI();

    }
    @Override
    public String getTabCaption(){
        return  "burpBridge";
    }
    @Override
    public Component getUiComponent(){
        return mainPanel;
    }
    @Override
    public void actionPerformed(ActionEvent e){
        String cmd = e.getActionCommand();
        if(cmd.equals("pythonScriptPathSelect")){
            JFrame parentFrame = new JFrame();
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Python Script Path");

            int userSelection = fileChooser.showOpenDialog(parentFrame);

            if(userSelection == JFileChooser.APPROVE_OPTION) {

                final File pythonPathFile = fileChooser.getSelectedFile();

                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        pythonPath.setText(pythonPathFile.getAbsolutePath());
                    }
                });

            }
        }
        else if(cmd.equals("pythonBinaryPathSelect")){
            JFrame parentFrame = new JFrame();
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Python Binary Path");

            int userSelection = fileChooser.showOpenDialog(parentFrame);

            if(userSelection == JFileChooser.APPROVE_OPTION) {

                final File pythonPathFile = fileChooser.getSelectedFile();

                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        pythonBinaryPath.setText(pythonPathFile.getAbsolutePath());
                    }
                });

            }
        }
        else if(cmd.equals("checkAll")){
            Config.pythonScriptPath = pythonPath.getText();
            Config.pythonBinaryPath = pythonBinaryPath.getText();
            stdout.println(Config.pythonScriptPath);
        }

    }
    private void buildUI(){
        try{
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    //主面板
                    mainPanel = new JPanel();
                    mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
                    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
                    //创建配置面板
                    JPanel configurationConfPanel = new JPanel();
                    configurationConfPanel.setLayout(new BoxLayout(configurationConfPanel,BoxLayout.Y_AXIS));

                    JPanel pythonPathPanel = new JPanel();
                    pythonPathPanel.setLayout(new BoxLayout(pythonPathPanel,BoxLayout.X_AXIS));
                    pythonPathPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    JLabel labelPython = new JLabel("Python Binary Path:");
                    pythonBinaryPath = new JTextField(200);
                    pythonBinaryPath.setMaximumSize(pythonBinaryPath.getPreferredSize());
                    JButton pythonButton = new JButton("Select File");
                    pythonButton.setActionCommand("pythonBinaryPathSelect");
                    pythonButton.addActionListener(BurpExtender.this);
                    pythonPathPanel.add(labelPython);
                    pythonPathPanel.add(pythonBinaryPath);
                    pythonPathPanel.add(pythonButton);

                    JPanel pythonScriptPathPanel = new JPanel();
                    pythonScriptPathPanel.setLayout(new BoxLayout(pythonScriptPathPanel,BoxLayout.X_AXIS));
                    pythonScriptPathPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    JLabel labelPythonScript = new JLabel("Python Script Path:");
                    pythonPath = new JTextField(200);
                    pythonPath.setMaximumSize(pythonPath.getPreferredSize());
                    JButton pythonPathButton = new JButton("Select File");
                    pythonPathButton.setActionCommand("pythonScriptPathSelect");
                    pythonPathButton.addActionListener(BurpExtender.this);
                    pythonScriptPathPanel.add(labelPythonScript);
                    pythonScriptPathPanel.add(pythonPath);
                    pythonScriptPathPanel.add(pythonPathButton);

                    JPanel configButtonPannel = new JPanel();
                    configButtonPannel.setLayout(new BoxLayout(configButtonPannel,BoxLayout.X_AXIS));
                    configButtonPannel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    JButton  checkButton =  new JButton("Change Config");
                    checkButton.setActionCommand("checkAll");
                    checkButton.addActionListener(BurpExtender.this);
                    configButtonPannel.add(checkButton);

                    configurationConfPanel.add(pythonPathPanel);
                    configurationConfPanel.add(pythonScriptPathPanel);
                    configurationConfPanel.add(configButtonPannel);
                    splitPane.add(configurationConfPanel);
                    splitPane.setResizeWeight(.7d);
                    mainPanel.add(splitPane);
                    callbacks.customizeUiComponent(mainPanel);
                    callbacks.addSuiteTab(BurpExtender.this);
                }
            });
        }
        catch (Exception e){
            this.stderr.println(e.toString());
        }
    }
}
