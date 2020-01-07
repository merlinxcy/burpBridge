package xeldax;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Base64;


public class PythonBridge {
    public static String run(String param)throws Exception {
        Runtime runtime = Runtime.getRuntime();
        byte[] encodedBytes = Base64.getEncoder().encode(param.getBytes());
        String par_str = new String(encodedBytes);
        Process exec = runtime.exec(Config.pythonBinaryPath +" "+ Config.pythonScriptPath + " "+par_str);
        InputStream stdin = exec.getInputStream();
        InputStreamReader isr = new InputStreamReader(stdin);
        BufferedReader br = new BufferedReader(isr);
        String info;
        String result = "";
        while ((info = br.readLine()) != null) {
            result += info+"\n";
        }
        System.out.println(result);
        return result;
    }
}
