package Main;

import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;

public class Ranking {
    public static ArrayList<GameData> games = new ArrayList<>();


    private Ranking() {
    }


    public static int adjustRank(GameData game, Float[] gameWeighting) {
        int points = initRanking();
        double accuracy = CalcAverageAccuracy();
        double improveMetric = 0;
        // p = I/V + C

        if(game.playerColor.equals("White")){
            improveMetric = ((game.wAccuracy - accuracy) / accuracy) * 100;
        }
        else{
            improveMetric = ((game.bAccuracy - accuracy) / accuracy) * 100;
        }

        points += Math.round(improveMetric / gameWeighting[1] + gameWeighting[0]);

        if(points < 0){
            points = 0;
        }

        return points;

    }

    private static int initRanking() {
        File gameHistoryPath = new File(Main.jarDir, "Data/GameHistory.txt");
        int points = 1000;
        try (BufferedReader gameHistoryReader = new BufferedReader(new FileReader(gameHistoryPath))) {
            String line = "";
            ArrayList<String> gameCodes = new ArrayList<>();
            while (line != null) {
                if(!line.isBlank()) {
                    gameCodes.add(line.split(" ")[0]);
                }
                line = gameHistoryReader.readLine();

            }

            for (String code : gameCodes) {
                String fileName = code + "_Game.json";
                File jsonFilePath = new File(Main.jarDir, "Data/JsonFiles/" + fileName);
                try (BufferedReader prevJsonReader = new BufferedReader(new FileReader(jsonFilePath))) {
                    Gson gson = new Gson();
                    GameData game = gson.fromJson(prevJsonReader, GameData.class);
                    games.add(game);


                } catch (IOException e) {
                    System.out.println("Json File path invalid");
                }

            }
            if(games.size() > 0){
                points = games.getLast().rank_points;
            }

        } catch (IOException e) {
            System.out.println("Unable to find game history path");
        }

        return points;
    }


    private static double CalcAverageAccuracy() {
        double totalAccuracy = 0;
        int totalGames = 0;
        for (GameData game : games) {
            if(game.playerColor.equals("White")){
                totalAccuracy += game.wAccuracy;
            }
            else{
                totalAccuracy += game.bAccuracy;
            }

             totalGames++;

        }
        return totalAccuracy / totalGames;
    }

}