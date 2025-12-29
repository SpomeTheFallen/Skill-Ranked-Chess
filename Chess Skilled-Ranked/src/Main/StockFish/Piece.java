package Main.StockFish;

public enum Piece {
    WP1("Pawn", "White", "a2"), WP2("Pawn", "White", "b2"),
    WP3("Pawn", "White", "c2"), WP4("Pawn", "White", "d2"),
    WP5("Pawn", "White", "e2"), WP6("Pawn", "White", "f2"),
    WP7("Pawn", "White", "g2"), WP8("Pawn", "White", "h2"),
    WN1("Knight", "White","b1"), WN2("Knight", "White", "g1"),
    WR1("Rook", "White", "a1"), WR2("Rook", "White", "h1"),
    WB1("Bishop", "White", "c1"), WB2("Bishop", "White", "f1"),
    WQ("Queen", "White", "d1"), WK("King", "White", "e1"),
    BP1("Pawn", "Black", "a7"), BP2("Pawn", "Black", "b7"),
    BP3("Pawn", "Black", "c7"), BP4("Pawn", "Black", "d7"),
    BP5("Pawn", "Black", "e7"), BP6("Pawn", "Black", "f7"),
    BP7("Pawn", "Black", "g7"), BP8("Pawn", "Black", "h7"),
    BN1("Knight", "Black","b8"), BN2("Knight", "Black", "g8"),
    BR1("Rook", "Black", "a8"), BR2("Rook", "Black", "h8"),
    BB1("Bishop", "Black", "c8"), BB2("Bishop", "Black", "f8"),
    BQ("Queen", "Black", "d8"), BK("King", "Black", "e8");



    private final String color;
    private String type;
    private String position;
    public final String originalPos;


    private Piece(String type, String color, String position){
        this.type = type;
        this.color = color;
        this.position = position;
        this.originalPos = position;
    }

    public void setType(String type){
        this.type = type;
    }

    public void remove(){
        this.position = "removed";
    }

    public String getPosition(){
        return this.position;
    }

    public void setPosition(String position){
        this.position = position;
    }

    public String getType(){
        return this.type;
    }

    public String getColor(){
        return this.color;
    }

    public static void resetPosistions(){
        for(Piece piece: Piece.values()){
            piece.setPosition(piece.originalPos);
        }
    }
}
