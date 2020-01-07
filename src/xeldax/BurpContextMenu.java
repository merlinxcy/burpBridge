package xeldax;

import burp.*;
import com.sun.deploy.util.ArrayUtil;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BurpContextMenu implements IContextMenuFactory {
    private IExtensionHelpers helpers;
    private  IBurpExtenderCallbacks callbacks;
    private PrintWriter stdout;
    private PrintWriter stderr;

    public BurpContextMenu(IBurpExtenderCallbacks callbacks){
        helpers = callbacks.getHelpers();
        this.callbacks = callbacks;
        this.stdout = new PrintWriter(callbacks.getStdout(),true);
        this.stderr = new PrintWriter(callbacks.getStderr(),true);
    }
    public List<JMenuItem> createMenuItems(final IContextMenuInvocation invocation){
        List<JMenuItem> list = new ArrayList<JMenuItem>();
        JMenuItem item = new JMenuItem("Call Decode Bridge");
        item.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {
                IHttpRequestResponse[] selectedItems = invocation.getSelectedMessages();
                int[] selectedBounds = invocation.getSelectionBounds();
                byte selectedInvocationContext = invocation.getInvocationContext();
                try{
                    byte[] selectedText = null;
                    if(selectedInvocationContext == IContextMenuInvocation.CONTEXT_MESSAGE_EDITOR_REQUEST){
                        selectedText = selectedItems[0].getRequest();
                    }
                    else{
                        selectedText = selectedItems[0].getResponse();
                    }
                    String param = new String(selectedText);
                    stdout.println(param);
                    String result = PythonBridge.run(param);
                    stdout.println(result);
                    selectedItems[0].setRequest(result.getBytes());
                }
                catch (Exception err){
                    stderr.println(err.getStackTrace().toString());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        list.add(item);
        return list;
    }
}
