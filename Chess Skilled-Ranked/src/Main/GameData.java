package Main;

import Main.StockFish.Stockfish;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class GameData {
    private ArrayList<String> FENs;
    private ArrayList<String> LANs;
    public Map<Integer, String> LANindexMoveType = new LinkedHashMap<>();
    private Map<String, Integer> wMoves = new LinkedHashMap<>();
    private Map<String, Integer> bMoves = new LinkedHashMap<>();
    public double wAccuracy;
    public double bAccuracy;
    public String playerColor;
    public int rank_points;



    public GameData(String PGN, String playerColor){

        Stockfish stockfish = new Stockfish(PGN);
        stockfish.run();

        this.playerColor = playerColor;
        FENs = stockfish.FENpositions;
        LANs = stockfish.LANmoves;

        int numberOfWhiteMoves = (int) Math.ceil((double) stockfish.moveTracker/2);
        int numberOfBlackMoves = (int) Math.floor((double) stockfish.moveTracker/2);

        wMoves.put("numberOfBestMoves", 0);
        wMoves.put("numberOfGoodMoves", 0);
        wMoves.put("numberOfOkayMoves", 0);
        wMoves.put("numberOfInaccuracies", 0);
        wMoves.put("numberOfMistakes", 0);
        wMoves.put("numberOfBlunders", 0);
        bMoves.put("numberOfBestMoves", 0);
        bMoves.put("numberOfGoodMoves", 0);
        bMoves.put("numberOfOkayMoves", 0);
        bMoves.put("numberOfInaccuracies", 0);
        bMoves.put("numberOfMistakes", 0);
        bMoves.put("numberOfBlunders", 0);


        readEval(stockfish);


        wAccuracy = calculateAccuracy(wMoves.get("numberOfBestMoves"), wMoves.get("numberOfGoodMoves"),
                wMoves.get("numberOfOkayMoves"), numberOfWhiteMoves);

        bAccuracy = calculateAccuracy(bMoves.get("numberOfBestMoves"), bMoves.get("numberOfGoodMoves"),
                bMoves.get("numberOfOkayMoves"), numberOfBlackMoves);

        rank_points = Ranking.adjustRank(this, evaluteGameWeighting(stockfish));

        export_GameData(stockfish);

    }

    private void readEval(Stockfish stockfish) {
        for(int i = 0 ; i < stockfish.moveTracker - 1; i++){
            String movedColor = (i%2==0) ? "White" : "Black";
            boolean blunder = false;
            boolean mistake = false;
            boolean inaccurate = false;
            boolean okay = false;
            boolean good = false;
            boolean best = false;

            int evalDiff = stockfish.Evals.get(i + 1) - stockfish.Evals.get(i);
            if(movedColor.equals("Black")) evalDiff = -evalDiff;

            if(stockfish.mateAtPos.get(i)){
                evalDiff = stockfish.Evals.get(i + 1);
                if(!stockfish.mateAtPos.get(i+1)){
                    if(evalDiff < 0){
                        blunder = true;
                    }
                    else{
                        mistake = true;
                    }
                    applyMoveAssignments(movedColor, i, blunder, mistake, inaccurate, okay, good, best);
                    continue;
                }
                else{
                    evalDiff = 0;
                }
            }

            if(evalDiff >= -50){
                if (stockfish.BestMoves.get(i).equals(stockfish.LANmoves.get(i))){
                    best = true;
                }
                else{
                    good = true;
                }
            }
            else{
                if(evalDiff > -60 ){
                    okay = true;
                }
                else if(evalDiff > -100){
                    inaccurate = true;
                }
                else if (evalDiff > -200){
                    mistake = true;
                }
                else{
                    blunder = true;
                }
            }

            applyMoveAssignments(movedColor, i, blunder, mistake, inaccurate, okay, good, best);


        }
    }


    private Float[] evaluteGameWeighting(Stockfish stockfish){

        int complexityTotal = 0;
        int ctotal = 0;
        for(int i = 0 ; i < stockfish.Evals.size() ; i++){
            if(stockfish.SubsequentEvals.get(i) != null){
                complexityTotal += Math.abs(stockfish.Evals.get(i) - stockfish.SubsequentEvals.get(i));
                ctotal ++;
            }
        }

        // complexity is the average difference of the best move eval and the second best eval
        float complexity = (float) complexityTotal / ctotal;

        int volatilityTotal = 0;
        int vtotal = 0;
        for(int i = 0 ; i < stockfish.Evals.size() - 1; i++){
            if(!stockfish.mateAtPos.get(i)){
                volatilityTotal += Math.abs(stockfish.Evals.get(i) - stockfish.Evals.get(i+1));
                vtotal ++;
            }
        }

        //volatility is the average eval swing
        float volatility = (float) volatilityTotal / vtotal;

        Float[] gameWeighting = new Float[2];
        gameWeighting[0] = complexity;
        gameWeighting[1] = volatility;

        return gameWeighting;
    }

    private void applyMoveAssignments(String movedColor, int i, boolean blunder,  boolean mistake, boolean inaccurate, boolean okay, boolean good, boolean best) {
        int replaceValue;
        if(blunder){
            switch (movedColor){
                case "White" -> {
                    replaceValue = wMoves.get("numberOfBlunders") + 1;
                    wMoves.replace("numberOfBlunders", replaceValue);
                }
                case "Black" -> {
                    replaceValue = bMoves.get("numberOfBlunders") + 1;
                    bMoves.replace("numberOfBlunders", replaceValue);
                }
            }
            LANindexMoveType.put(i, "Blunder");
        }
        if(mistake){
            switch (movedColor) {
                case "White" -> {
                    replaceValue = wMoves.get("numberOfMistakes") + 1;
                    wMoves.replace("numberOfMistakes", replaceValue);
                }
                case "Black" -> {
                    replaceValue = bMoves.get("numberOfMistakes") + 1;
                    bMoves.replace("numberOfMistakes", replaceValue);
                }
            }
            LANindexMoveType.put(i, "Mistake");
        }
        if(inaccurate){
            switch (movedColor) {
                case "White" -> {
                    replaceValue = wMoves.get("numberOfInaccuracies") + 1;
                    wMoves.replace("numberOfInaccuracies", replaceValue);
                }
                case "Black" -> {
                    replaceValue = bMoves.get("numberOfInaccuracies") + 1;
                    bMoves.replace("numberOfInaccuracies", replaceValue);
                }
            }
            LANindexMoveType.put(i, "Inaccuracy");
        }
        if(okay){
            switch (movedColor) {
                case "White" -> {
                    replaceValue = wMoves.get("numberOfOkayMoves") + 1;
                    wMoves.replace("numberOfOkayMoves", replaceValue);
                }
                case "Black" -> {
                    replaceValue = bMoves.get("numberOfOkayMoves") + 1;
                    bMoves.replace("numberOfOkayMoves", replaceValue);
                }
            }
            LANindexMoveType.put(i, "Okay");
        }
        if(good){
            switch (movedColor) {
                case "White" -> {
                    replaceValue = wMoves.get("numberOfGoodMoves") + 1;
                    wMoves.replace("numberOfGoodMoves", replaceValue);
                }
                case "Black" -> {
                    replaceValue = bMoves.get("numberOfGoodMoves") + 1;
                    bMoves.replace("numberOfGoodMoves", replaceValue);
                }
            }
            LANindexMoveType.put(i, "Good");
        }
        if(best){
            switch (movedColor) {
                case "White" -> {
                    replaceValue = wMoves.get("numberOfBestMoves") + 1;
                    wMoves.replace("numberOfBestMoves", replaceValue);
                }
                case "Black" -> {
                    replaceValue = bMoves.get("numberOfBestMoves") + 1;
                    bMoves.replace("numberOfBestMoves", replaceValue);
                }
            }
            LANindexMoveType.put(i, "Best");
        }
    }



    public void export_GameData(Stockfish stockfish){
        Random random = new Random();
        String code = String.valueOf(random.nextInt(100000, 999999));
        File codeFilePath = new File(Main.jarDir, "Data/GameCode.txt");
        String fileName = code + "_Game.json";
        File jsonFilePath = new File(Main.jarDir, "Data/JsonFiles/" + fileName);
        File historyFilePath = new File(Main.jarDir, "Data/GameHistory.txt");
        LocalDate date = LocalDate.now();


        try(BufferedWriter jsonWriter = new BufferedWriter(new FileWriter(jsonFilePath))){
            jsonFilePath.createNewFile();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(this, jsonWriter);

        }
        catch(IOException e){
            System.out.println("Json Writer Path Invalid");
        }

        try(BufferedWriter codeWriter = new BufferedWriter(new FileWriter(codeFilePath))){
            codeWriter.write(code);
        }
        catch (IOException e){
            System.out.println("Code Writer Path Invalid");
        }

        try(BufferedWriter historyWriter = new BufferedWriter(new FileWriter(historyFilePath, true))){
            historyWriter.write(code + " " + date + "\n");
        }
        catch (IOException e){
            System.out.println("History Writer Path Invalid");
        }


    }

    private double calculateAccuracy(int bestMoves, int goodMoves, int okayMoves, int totalMoves){
        return ((double) (bestMoves + goodMoves + okayMoves) * 100)/(double) totalMoves;
    }


}