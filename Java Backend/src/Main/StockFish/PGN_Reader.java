package Main.StockFish;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PGN_Reader {
    String PGN;
    public ArrayList<String> PGNmoves = new ArrayList<>();
    public ArrayList<String> PGNwhiteMoves = new ArrayList<>();
    public ArrayList<String> PGNblackMoves = new ArrayList<>();
    public String PGNmovesString;

    public ArrayList<String> LANmoves = new ArrayList<>();
    public ArrayList<String> FENpositions = new ArrayList<>();

    public int moveTracker = 1;
    private String moveColor;

    private static final Pattern universalSAN = Pattern.compile("([NBRKQ]?)([a-h]?)(x?)([a-h])([1-8])(=?)([QRBN]?)([+#]?)");
    private static final Pattern pawnSAN = Pattern.compile("([a-h]?)(x?)([a-h])([1-8])(=?)([QRBN]?)([+#]?)");
    private static final Pattern knightSAN = Pattern.compile("N([a-h]?)([1-8]?)(x?)([a-h])([1-8])([+#]?)");
    private static final Pattern bishopSAN = Pattern.compile("B([a-h]?)([1-8]?)(x?)([a-h])([1-8])([+#]?)");
    private static final Pattern rookSAN = Pattern.compile("R([a-h]?)([1-8]?)(x?)([a-h])([1-8])([+#]?)");
    private static final Pattern queenSAN = Pattern.compile("Q([a-h]?)([1-8]?)(x?)([a-h])([1-8])([+#]?)");
    private static final Pattern kingSAN = Pattern.compile("K(x?)([a-h])([1-8])");
    private static final Pattern castleSAN = Pattern.compile("O-O([-O]?)");

    public PGN_Reader(String PGN){
        this.PGN = PGN.replaceAll("\\s+", " ");
        PGNmoves.addAll(Arrays.asList(this.PGN.split(" ")));
        for (int i = PGNmoves.size()-1; i >= 0 ; i--){
            Matcher pgnMatch = universalSAN.matcher(PGNmoves.get(i));
            Matcher castleMatch = castleSAN.matcher(PGNmoves.get(i));
            if(!pgnMatch.find() && !castleMatch.find()){
                PGNmoves.remove(i);
            }
        }

        for(int i = 0 ; i < PGNmoves.size() ; i+=2){
            PGNwhiteMoves.add(PGNmoves.get(i));
        }

        for(int i = 1 ; i < PGNmoves.size() ; i+=2){
            PGNblackMoves.add(PGNmoves.get(i));
        }

        this.PGN = String.join(" ", PGNmoves);

        FENpositions.add(generateFEN());

        for(String move : PGNmoves){
            readSAN(move);
        }

        Piece.resetPosistions();
    }

    public void PrintPGNMoves(){
        System.out.println("Moves: ");
        for(String move : PGNmoves) {
            System.out.print(move + " ");
        }

        System.out.println("\nWhite Moves: ");
        for(String move : PGNwhiteMoves){
            System.out.print(move + " ");
        }

        System.out.println("\nBlack Moves: ");
        for (String move : PGNblackMoves){
            System.out.print(move + " ");
        }
        System.out.println("\nLAN Moves: ");
        for(String move : LANmoves){
            System.out.print(move + " ");
        }
        System.out.println("\nFEN Positions: ");
        for(String pos : FENpositions){
            System.out.print(pos + " | ");
        }
    }



    public void readSAN(String SAN) {
        moveColor = (moveTracker % 2 == 1) ? "White" : "Black";

        if (Character.isLowerCase(SAN.charAt(0))) {
            checkPawnSANCases(SAN);
        }
        else{
            switch (SAN.charAt(0)){
                case 'N' -> checkKnightSANCases(SAN);
                case 'B' -> checkBishopSANCases(SAN);
                case 'R' -> checkRookSANCases(SAN);
                case 'Q' -> checkQueenSANCases(SAN);
                case 'K', 'O' -> checkKingSANCases(SAN);
                default -> System.out.println(SAN + " did not match any case");
            }
        }
    }

    private void checkPawnSANCases(String SAN){
        Matcher findPawnSAN = pawnSAN.matcher(SAN);

        if(!findPawnSAN.find()){
            System.out.println(SAN + " did not match with pawn");
            return;
        }

        resetHalfMoves = true;
        String initialCol = findPawnSAN.group(1);
        String destinationCol = findPawnSAN.group(3);
        String destinationRow = findPawnSAN.group(4);
        boolean promotion = findPawnSAN.group(5).equals("=");
        String promotionTo = findPawnSAN.group(6);
        String check = findPawnSAN.group(7);
        boolean capture = findPawnSAN.group(2).equals("x");
        boolean enPessant = true;
        boolean singlePush = false;


        if(capture){
            for(Piece piece : Piece.values()){
                if(piece.getPosition().equals(destinationCol + destinationRow)){
                    piece.remove();
                    enPessant = false;
                    break;
                }
            }
        }
        else{
            enPessant = false;
        }
        if(enPessant){
            for(Piece piece : Piece.values()){
                if(piece.getPosition().charAt(0) == destinationCol.charAt(0) && Math.abs(piece.getPosition().charAt(1) - destinationRow.charAt(0)) == 1){
                    piece.remove();
                    break;
                }
            }
        }

        for(Piece piece : Piece.values()){
            if(piece.getType().equals("Pawn") && piece.getColor().equals(moveColor) && !piece.getPosition().equals("removed")){
                if(initialCol.isBlank() && piece.getPosition().charAt(0) == destinationCol.charAt(0)){
                    singlePush = Math.abs(piece.getPosition().charAt(1) - destinationRow.charAt(0)) == 1;
                    if(singlePush){
                        if(promotion){
                            applyPromotionMoveUpdates(piece, destinationCol, destinationRow, promotionTo);
                            break;
                        }
                        applyMoveUpdates(piece, destinationCol, destinationRow);
                        break;
                    }

                }
                if(!initialCol.isBlank() && piece.getPosition().charAt(0) == initialCol.charAt(0)){
                    singlePush = Math.abs(piece.getPosition().charAt(1) - destinationRow.charAt(0)) == 1;
                    if(singlePush){
                        if(promotion){
                            applyPromotionMoveUpdates(piece, destinationCol, destinationRow, promotionTo);
                            break;
                        }
                        applyMoveUpdates(piece, destinationCol, destinationRow);
                        break;
                    }
                }
            }
        }

        if(!singlePush){
            for(Piece piece : Piece.values()){
                if(piece.getType().equals("Pawn") && piece.getColor().equals(moveColor) && piece.getPosition().charAt(0) == destinationCol.charAt(0) && !piece.getPosition().equals("removed")){
                    if(Math.abs(piece.getPosition().charAt(1) - destinationRow.charAt(0)) == 2){
                       applyMoveUpdates(piece, destinationCol, destinationRow);
                       if(moveColor.equals("White")) enPessantTarget = destinationCol + 3;
                       if(moveColor.equals("Black")) enPessantTarget = destinationCol + 6;
                       break;
                   }
                }
            }
        }

    }

    private void applyMoveUpdates(Piece piece, String destinationCol, String destinationRow) {
        LANmoves.add(piece.getPosition() + destinationCol + destinationRow);
        piece.setPosition(destinationCol + destinationRow);
        moveTracker++;
        halfMoveTracker++;
        if(resetHalfMoves) halfMoveTracker = 0;
        FENpositions.add(generateFEN());
    }

    private void applyPromotionMoveUpdates(Piece piece, String destinationCol, String destinationRow, String promotionTo) {
        LANmoves.add(piece.getPosition() + destinationCol + destinationRow + promotionTo);
        piece.setPosition(destinationCol + destinationRow);
        switch (promotionTo){
            case "Q" -> promotionTo = "Queen";
            case "B" -> promotionTo = "Bishop";
            case "R" -> promotionTo = "Rook";
            case "N" -> promotionTo = "Knight";
        }
        piece.setType(promotionTo);
        moveTracker++;
        halfMoveTracker++;
        if(resetHalfMoves) halfMoveTracker = 0;
        FENpositions.add(generateFEN());

    }

    private void checkKnightSANCases(String SAN){
        Matcher findKnightSAN = knightSAN.matcher(SAN);

        if(!findKnightSAN.find()) {
            System.out.println(SAN + " did not match with knight");
            return;
        }

        String initialCol = findKnightSAN.group(1);
        String initialRow = findKnightSAN.group(2);
        String destinationCol = findKnightSAN.group(4);
        String destinationRow = findKnightSAN.group(5);
        String check = findKnightSAN.group(6);
        boolean capture = findKnightSAN.group(3).equals("x");

        if(capture){
            for(Piece piece : Piece.values()){
                if(piece.getPosition().equals(destinationCol + destinationRow)){
                    piece.remove();
                    resetHalfMoves = true;
                    break;
                }
            }
        }

        for(Piece piece : Piece.values()){
            if(piece.getType().equals("Knight") && piece.getColor().equals(moveColor) && !piece.getPosition().equals("removed")){
                boolean knightHorMove = Math.abs(destinationCol.charAt(0) - piece.getPosition().charAt(0)) == 2 && Math.abs(destinationRow.charAt(0) - piece.getPosition().charAt(1)) == 1;
                boolean knightVerMove = Math.abs(destinationCol.charAt(0) - piece.getPosition().charAt(0)) == 1 && Math.abs(destinationRow.charAt(0) - piece.getPosition().charAt(1)) == 2;
                if(initialCol.isBlank() && initialRow.isBlank()){
                    if(knightHorMove || knightVerMove){
                        applyMoveUpdates(piece, destinationCol, destinationRow);
                        break;
                    }
                }

                if(!initialRow.isBlank() && initialCol.isBlank()){
                    if((knightHorMove || knightVerMove) && initialRow.charAt(0) == piece.getPosition().charAt(1)){
                        applyMoveUpdates(piece, destinationCol, destinationRow);
                        break;
                    }
                }
                if(!initialCol.isBlank() && initialRow.isBlank()){
                    if((knightHorMove || knightVerMove) && initialCol.charAt(0) == piece.getPosition().charAt(0)){
                        applyMoveUpdates(piece, destinationCol, destinationRow);
                        break;
                    }
                }
                if(!initialRow.isBlank() && !initialCol.isBlank()){
                    if(piece.getPosition().charAt(0) == initialCol.charAt(0) && piece.getPosition().charAt(1) == initialRow.charAt(0)){
                        applyMoveUpdates(piece, destinationCol, destinationRow);
                        break;
                    }
                }
            }

        }
    }

    private void checkBishopSANCases(String SAN) {
        Matcher findBishopSAN = bishopSAN.matcher(SAN);

        if (!findBishopSAN.find()) {
            System.out.println(SAN + " did not match with bishop");
            return;
        }

        String initialCol = findBishopSAN.group(1);
        String initialRow = findBishopSAN.group(2);
        String destinationCol = findBishopSAN.group(4);
        String destinationRow = findBishopSAN.group(5);
        String check = findBishopSAN.group(6);
        boolean capture = findBishopSAN.group(3).equals("x");

        if (capture) {
            for (Piece piece : Piece.values()) {
                if (piece.getPosition().equals(destinationCol + destinationRow)) {
                    piece.remove();
                    resetHalfMoves = true;
                    break;
                }
            }
        }

        for (Piece piece : Piece.values()) {
            if (piece.getType().equals("Bishop") && piece.getColor().equals(moveColor) && !piece.getPosition().equals("removed")) {
                boolean bishopMove = Math.abs(destinationCol.charAt(0) - piece.getPosition().charAt(0)) == Math.abs(destinationRow.charAt(0) - piece.getPosition().charAt(1)) && isDiagonalPathClear(piece.getPosition(), destinationCol + destinationRow);

                if (initialCol.isBlank() && initialRow.isBlank()) {
                    if (bishopMove) {
                        applyMoveUpdates(piece, destinationCol, destinationRow);
                        break;
                    }
                }
                if (!initialRow.isBlank() && initialCol.isBlank()) {
                    if (bishopMove && initialRow.charAt(0) == piece.getPosition().charAt(1)) {
                        applyMoveUpdates(piece, destinationCol, destinationRow);
                        break;
                    }
                }
                if (!initialCol.isBlank() && initialRow.isBlank()) {
                    if (bishopMove && initialCol.charAt(0) == piece.getPosition().charAt(0)) {
                        applyMoveUpdates(piece, destinationCol, destinationRow);
                        break;
                    }
                }
                if (!initialRow.isBlank() && !initialCol.isBlank()) {
                    if (piece.getPosition().charAt(0) == initialCol.charAt(0) && piece.getPosition().charAt(1) == initialRow.charAt(0)) {
                        applyMoveUpdates(piece, destinationCol, destinationRow);
                        break;
                    }
                }
            }


        }

    }

    private boolean isDiagonalPathClear(String from, String to){
        char startingCol = from.charAt(0);
        char startingRow = from.charAt(1);
        char destinationCol = to.charAt(0);
        char destinationRow = to.charAt(1);

        int rowDiff = destinationRow - startingRow;
        int colDiff = destinationCol - startingCol;

        boolean upRight = false;
        boolean downRight = false;
        boolean upLeft = false;
        boolean downLeft = false;

        if(rowDiff < 0 && colDiff > 0) downRight = true;
        if(rowDiff < 0 && colDiff < 0) downLeft = true;
        if(rowDiff > 0 && colDiff > 0) upRight = true;
        if(rowDiff > 0 && colDiff < 0) upLeft = true;
        while (startingCol != destinationCol || startingRow != destinationRow) {
            if(downRight){
                startingCol++;
                startingRow--;
            }
            if(downLeft){
                startingCol--;
                startingRow--;
            }
            if (upRight) {
                startingCol++;
                startingRow++;
            }
            if (upLeft) {
                startingCol--;
                startingRow++;
            }
            for (Piece piece : Piece.values()) {
                if (piece.getPosition().charAt(0) == startingCol && piece.getPosition().charAt(1) == startingRow) {
                    return false;
                }

            }
        }

        return true;

    }

    private void checkRookSANCases(String SAN) {
        Matcher findRookSAN = rookSAN.matcher(SAN);

        if (!findRookSAN.find()) {
            System.out.println(SAN + " did not match with rook");
            return;
        }

        String initialCol = findRookSAN.group(1);
        String initialRow = findRookSAN.group(2);
        String destinationCol = findRookSAN.group(4);
        String destinationRow = findRookSAN.group(5);
        String check = findRookSAN.group(6);
        boolean capture = findRookSAN.group(3).equals("x");

        if (capture) {
            for (Piece piece : Piece.values()) {
                if (piece.getPosition().equals(destinationCol + destinationRow)) {
                    piece.remove();
                    resetHalfMoves = true;
                    break;
                }
            }
        }

        for (Piece piece : Piece.values()) {
            if (piece.getType().equals("Rook") && piece.getColor().equals(moveColor) && !piece.getPosition().equals("removed")) {
                boolean rookHorMove = Math.abs(destinationRow.charAt(0) - piece.getPosition().charAt(1)) == 0 && isHorPathClear(piece.getPosition(), destinationCol + destinationRow);
                boolean rookVerMove = Math.abs(destinationCol.charAt(0) - piece.getPosition().charAt(0)) == 0 && isVerPathClear(piece.getPosition(), destinationCol + destinationRow);
                if (initialCol.isBlank() && initialRow.isBlank()) {
                    if (rookVerMove || rookHorMove) {
                        applyMoveUpdates(piece, destinationCol, destinationRow);
                        removeCastleRight(piece);
                        break;
                    }
                }
                if (!initialRow.isBlank() && initialCol.isBlank()) {
                    if ((rookVerMove || rookHorMove) && initialRow.charAt(0) == piece.getPosition().charAt(1)) {
                        applyMoveUpdates(piece, destinationCol, destinationRow);
                        removeCastleRight(piece);
                        break;
                    }
                }
                if (!initialCol.isBlank() && initialRow.isBlank()) {
                    if ((rookVerMove || rookHorMove) && initialCol.charAt(0) == piece.getPosition().charAt(0)) {
                        applyMoveUpdates(piece, destinationCol, destinationRow);
                        removeCastleRight(piece);
                        break;
                    }
                }
                if (!initialRow.isBlank() && !initialCol.isBlank()) {
                    if (piece.getPosition().charAt(0) == initialCol.charAt(0) && piece.getPosition().charAt(1) == initialRow.charAt(0)) {
                        applyMoveUpdates(piece, destinationCol, destinationRow);
                        removeCastleRight(piece);
                        break;
                    }
                }
            }


        }
    }

    private boolean isHorPathClear(String from, String to){
        char startingCol = from.charAt(0);
        char destinationCol = to.charAt(0);
        char destinationRow = to.charAt(1);

        boolean left = false;
        boolean right = false;

        if(destinationCol < startingCol) left = true;
        if(destinationCol > startingCol) right = true;
        while (startingCol != destinationCol) {
            if(left) startingCol--;
            if(right) startingCol++;

            for(Piece piece : Piece.values()){
                if (piece.getPosition().charAt(0) == startingCol && piece.getPosition().charAt(1) == destinationRow) {
                    return false;
                }
            }

        }
        return true;
    }

    private boolean isVerPathClear(String from, String to){
        char startingRow = from.charAt(1);
        char destinationCol = to.charAt(0);
        char destinationRow = to.charAt(1);

        boolean up = false;
        boolean down = false;

        if(destinationRow < startingRow) down = true;
        if(destinationRow > startingRow) up = true;

        while (startingRow != destinationRow) {
            if(down) startingRow--;
            if(up) startingRow++;

            for(Piece piece : Piece.values()){
                if (piece.getPosition().charAt(1) == startingRow && piece.getPosition().charAt(0) == destinationCol) {
                    System.out.println(piece.name() + " is blocking the path, pos: " +piece.getPosition());
                    return false;
                }
            }

        }
        return true;
    }

    private void checkQueenSANCases(String SAN) {
        Matcher findQueenSAN = queenSAN.matcher(SAN);

        if (!findQueenSAN.find()) {
            System.out.println(SAN + " did not match with queen");
            return;
        }

        String initialCol = findQueenSAN.group(1);
        String initialRow = findQueenSAN.group(2);
        String destinationCol = findQueenSAN.group(4);
        String destinationRow = findQueenSAN.group(5);
        String check = findQueenSAN.group(6);
        boolean capture = findQueenSAN.group(3).equals("x");

        if (capture) {
            for (Piece piece : Piece.values()) {
                if (piece.getPosition().equals(destinationCol + destinationRow)) {
                    piece.remove();
                    resetHalfMoves = true;
                    break;
                }
            }
        }

        for (Piece piece : Piece.values()) {
            if (piece.getType().equals("Queen") && piece.getColor().equals(moveColor) && !piece.getPosition().equals("removed")) {
                boolean rookHorMove = Math.abs(destinationRow.charAt(0) - piece.getPosition().charAt(1)) == 0 && isHorPathClear(piece.getPosition(), destinationCol + destinationRow);
                boolean rookVerMove = Math.abs(destinationCol.charAt(0) - piece.getPosition().charAt(0)) == 0 && isVerPathClear(piece.getPosition(), destinationCol + destinationRow);
                boolean bishopMove = Math.abs(destinationCol.charAt(0) - piece.getPosition().charAt(0)) == Math.abs(destinationRow.charAt(0) - piece.getPosition().charAt(1)) && isDiagonalPathClear(piece.getPosition(), destinationCol + destinationRow);
                if (initialCol.isBlank() && initialRow.isBlank()) {
                    if (rookVerMove || rookHorMove || bishopMove) {
                        applyMoveUpdates(piece, destinationCol, destinationRow);
                        break;
                    }
                }
                if (!initialRow.isBlank() && initialCol.isBlank()) {
                    if ((rookVerMove || rookHorMove || bishopMove) && initialRow.charAt(0) == piece.getPosition().charAt(1)) {
                        applyMoveUpdates(piece, destinationCol, destinationRow);
                        break;
                    }
                }
                if (!initialCol.isBlank() && initialRow.isBlank()) {
                    if ((rookVerMove || rookHorMove || bishopMove) && initialCol.charAt(0) == piece.getPosition().charAt(0)) {
                        applyMoveUpdates(piece, destinationCol, destinationRow);
                        break;
                    }
                }
                if (!initialRow.isBlank() && !initialCol.isBlank()) {
                    if (piece.getPosition().charAt(0) == initialCol.charAt(0) && piece.getPosition().charAt(1) == initialRow.charAt(0)) {
                        applyMoveUpdates(piece, destinationCol, destinationRow);
                        break;
                    }
                }
            }
        }
    }

    private void checkKingSANCases(String SAN){

        Matcher findKingSAN = kingSAN.matcher(SAN);
        Matcher findCastle = castleSAN.matcher(SAN);
        boolean kingMove = findKingSAN.find();
        boolean castle = findCastle.find();

        if (!kingMove && !castle) {
            System.out.println(SAN + " did not match with king");
            return;
        }
        if(kingMove) {
            String destinationCol = findKingSAN.group(2);
            String destinationRow = findKingSAN.group(3);
            boolean capture = findKingSAN.group(1).equals("x");
            if (capture) {
                for (Piece piece : Piece.values()) {
                    if (piece.getPosition().equals(destinationCol + destinationRow)) {
                        piece.remove();
                        resetHalfMoves = true;
                        break;
                    }
                }
            }

            for (Piece piece : Piece.values()) {
                if (piece.getType().equals("King") && piece.getColor().equals(moveColor) && !piece.getPosition().equals("removed")) {
                    applyMoveUpdates(piece, destinationCol, destinationRow);
                    if(piece.getColor().equals("White")){
                        WshortCastle = false;
                        WlongCastle = false;
                    }
                    else{
                        BshortCastle = false;
                        BlongCastle = false;
                    }
                    break;
                }
            }
        }
        if(castle) {
            boolean longCastle = findCastle.group(1).equals("-O");
            applyCastleMoveUpdate(longCastle, moveColor);
        }

    }

    private void applyCastleMoveUpdate(boolean longCastles, String moveColor){
        String orgPos = "error";
        String finalPos = "error";

        if(!longCastles){
            if(moveColor.equals("White")){
                orgPos = Piece.WK.getPosition();
                Piece.WK.setPosition("g1");
                finalPos = "g1";
                Piece.WR2.setPosition("f1");
                WlongCastle = false;
                WshortCastle = false;
            }
            if(moveColor.equals("Black")){
                orgPos = Piece.BK.getPosition();
                Piece.BK.setPosition("g8");
                finalPos = "g8";
                Piece.BR2.setPosition("f8");
                BlongCastle = false;
                BshortCastle = false;
            }
        }
        if(longCastles){
            if(moveColor.equals("White")){
                orgPos = Piece.WK.getPosition();
                Piece.WK.setPosition("c1");
                finalPos = "c1";
                Piece.WR1.setPosition("d1");
                WlongCastle = false;
                WshortCastle = false;
            }
            if(moveColor.equals("Black")){
                orgPos = Piece.BK.getPosition();
                Piece.BK.setPosition("c8");
                finalPos = "c8";
                Piece.BR1.setPosition("d8");
                BlongCastle = false;
                BshortCastle = false;
            }
        }
        LANmoves.add(orgPos + finalPos);
        moveTracker++;
        halfMoveTracker++;
        FENpositions.add(generateFEN());
    }

    private final String[] columns = {"a", "b", "c", "d", "e", "f", "g", "h"};
    private String enPessantTarget = "-";
    private boolean resetHalfMoves;
    private int halfMoveTracker = 0;
    private boolean WshortCastle = true;
    private boolean WlongCastle = true;
    private boolean BlongCastle = true;
    private boolean BshortCastle = true;

    private String generateFEN(){
        StringBuilder FEN = new StringBuilder();
        int rank;
        int emptySquares;
        String type = "";
        boolean empty = false;

        for(int i = 0; i < 8; i++){
            emptySquares = 0;
            switch (i){
                case 0 -> rank = 8;
                case 1 -> rank = 7;
                case 2 -> rank = 6;
                case 3 -> rank = 5;
                case 4 -> rank = 4;
                case 5 -> rank = 3;
                case 6 -> rank = 2;
                case 7 -> rank = 1;
                default -> rank = 0;
            }
            for (String column : columns) {
                empty = true;
                for (Piece piece : Piece.values()) {
                    if (piece.getPosition().equals(column + rank)) {
                        if (piece.getColor().equals("White")) {
                            switch (piece.getType()) {
                                case "Pawn" -> type = "P";
                                case "Knight" -> type = "N";
                                case "Bishop" -> type = "B";
                                case "Rook" -> type = "R";
                                case "Queen" -> type = "Q";
                                case "King" -> type = "K";
                            }
                        }
                        if (piece.getColor().equals("Black")) {
                            switch (piece.getType()) {
                                case "Pawn" -> type = "p";
                                case "Knight" -> type = "n";
                                case "Bishop" -> type = "b";
                                case "Rook" -> type = "r";
                                case "Queen" -> type = "q";
                                case "King" -> type = "k";
                            }
                        }

                        empty = false;
                        if (emptySquares > 0) {
                            FEN.append(emptySquares);
                            emptySquares = 0;
                        }
                        FEN.append(type);
                    }
                }
                if (empty) {
                    emptySquares++;
                }
            }
            if(emptySquares > 0){
                FEN.append(emptySquares);
            }
            FEN.append("/");
        }

        FEN.append(" ");

        if(moveTracker%2 == 1) FEN.append("w");
        else FEN.append("b");

        FEN.append(" ");

        if(WshortCastle) FEN.append("K");
        if(WlongCastle) FEN.append("Q");
        if(BshortCastle) FEN.append("k");
        if(BlongCastle) FEN.append("q");
        if(!WlongCastle && !WshortCastle && !BshortCastle && !BlongCastle) FEN.append("-");

        FEN.append(" ");

        FEN.append(enPessantTarget);
        enPessantTarget = "-";

        FEN.append(" ");

        FEN.append(halfMoveTracker);
        resetHalfMoves = false;

        FEN.append(" ");

        FEN.append((int) Math.ceil((double) moveTracker/2));

        return FEN.toString();
    }

    private void removeCastleRight(Piece piece) {
        if(piece.name().equals("WR1")) WlongCastle = false;
        if(piece.name().equals("WR2")) WshortCastle = false;
        if(piece.name().equals("BR1")) BlongCastle = false;
        if(piece.name().equals("BR2")) BshortCastle = false;
    }

}
