package Main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static File baseDir;

    public static void main(String[] args) throws URISyntaxException {

        String path = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();

        if(path.endsWith(".jar")) {
            baseDir = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
        }
        else{
            baseDir = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getParentFile().getParentFile().getParentFile();
        }

        File pgnImportPath = new File(baseDir, "Data/pgn.txt");

        try(BufferedReader pgnImportReader = new BufferedReader(new FileReader(pgnImportPath))){
            String line = "";
            String color = "";
            StringBuilder pgn = new StringBuilder();
            while (line != null){
                if (line.contains("Color: ")){
                    color = line.split("Color: ")[1];
                }
                pgn.append(line);
                pgn.append(" ");
                line = pgnImportReader.readLine();
            }
            GameData gameData = new GameData(pgn.toString(), color);

        }
        catch(IOException e){
            System.out.println("Error");
        }


    }
}