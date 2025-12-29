package Main.StockFish;

import Main.Main;

import java.io.*;
import java.util.ArrayList;

public class Stockfish extends PGN_Reader implements Runnable  {

    private boolean mate = false;


    public ArrayList<Boolean> mateAtPos = new ArrayList<>();
    public ArrayList<Integer> Evals = new ArrayList<>();
    public ArrayList<String> BestMoves = new ArrayList<>();
    public ArrayList<Integer> SubsequentEvals = new ArrayList<>();


    public Stockfish(String PGN) {
        super(PGN);
    }

    @Override
    public void run() {
        File stockfishPath = new File(Main.jarDir, "Engine/stockfish-windows-x86-64-avx2.exe");
        String bestMove = "";
        Integer secondEval = 0;
        int eval = 0;
        try {
            ProcessBuilder pb = new ProcessBuilder(stockfishPath.getPath());
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            writer.write("uci\n");
            writer.flush();

            writer.write("isready\n");
            writer.flush();

            writer.write("setoption name MultiPV value 2\n");
            writer.flush();


            writer.write("ucinewgame\n");
            writer.flush();


            for (String FEN : FENpositions) {
                writer.write("position fen " + FEN + "\n");
                writer.flush();

                writer.write("go depth 15\n"); // search 15 plies deep
                writer.flush();


                String line = reader.readLine();


                while (line != null) {

                    if (line.startsWith("info depth 15")) {

                         if (line.contains("multipv 1")) {
                            bestMove = line.split(" pv ")[1].split(" ")[0];

                            if (line.contains("score mate")) {
                                eval = Integer.parseInt(line.split("score mate ")[1].split(" ")[0]);
                                mate = true;
                            }
                            if(line.contains("score cp")){
                                eval = Integer.parseInt(line.split("score cp ")[1].split(" ")[0]);
                                mate = false;
                            }

                        }

                        if (line.contains("multipv 2")) {
                            if (line.contains("score mate")) {
                                secondEval = null;

                            } else {
                                secondEval = Integer.parseInt(line.split("score cp ")[1].split(" ")[0]);

                            }
                        }


                    }
                    if (line.startsWith("bestmove")){
                        break;
                    }


                    line = reader.readLine();
                }

                if (FEN.contains(" b "))
                    eval = -eval;

                Evals.add(eval);
                SubsequentEvals.add(secondEval);
                mateAtPos.add(mate);
                BestMoves.add(bestMove);

            }


            writer.write("quit\n");
            writer.flush();



        }catch (Exception e){
            System.out.println("Something went wrong in Main/StockFish/Stockfish");
            e.printStackTrace();
        }
    }


}
