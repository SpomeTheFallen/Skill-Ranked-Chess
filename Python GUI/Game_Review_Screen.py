
from PyQt5.QtWidgets import (QLabel, QWidget, QVBoxLayout,
                             QHBoxLayout, QGridLayout, QPushButton, QStackedLayout,
                             QSizePolicy)

from PyQt5.QtGui import QFont, QPixmap
from PyQt5.QtCore import Qt, pyqtSignal


class ReviewScreen(QWidget):
    exitSignal = pyqtSignal()

    def __init__(self, FENs, LANs, indexMoveType, wMoves, wAccuracy, bMoves, bAccuracy):
        super().__init__()

        self.boardLayout = QStackedLayout()

        self.exit = False

        self.initLayout(FENs, LANs, indexMoveType, wMoves, wAccuracy, bMoves, bAccuracy)

    def initBoard(self, fen, lan, MoveType):
        fen = fen.split()[0]
        rowTracker = 0
        colTracker = 0

        board = QGridLayout()
        board.setContentsMargins(0, 0, 0, 0)
        board.setSpacing(0)
        white = True
        for i in range(8):
            for j in range(8):
                square = QLabel()
                if white:
                    square.setStyleSheet("background-color: #f2bd83")
                if not white:
                    square.setStyleSheet("background-color: #753e01")
                board.addWidget(square, i, j)

                white = not white
            white = not white

        letters = ["a", "b", "c", "d", "e", "f", "g", "h"]
        if lan is not None:
            targetCol = letters.index(lan[2])
            targetRow = 8 - int(lan[3])
        else:
            targetRow = None
            targetCol = None

        for row in fen.split("/"):
            for col in row:

                match col:
                    case "R":
                        L = QLabel()
                        L.setPixmap(QPixmap("Images/R.png"))
                        piece = True
                    case "N":
                        L = QLabel()
                        L.setPixmap(QPixmap("Images/N.png"))
                        piece = True
                    case "B":
                        L = QLabel()
                        L.setPixmap(QPixmap("Images/B.png"))
                        piece = True
                    case "K":
                        L = QLabel()
                        L.setPixmap(QPixmap("Images/K.png"))
                        piece = True
                    case "Q":
                        L = QLabel()
                        L.setPixmap(QPixmap("Images/Q.png"))
                        piece = True
                    case "P":
                        L = QLabel()
                        L.setPixmap(QPixmap("Images/P.png"))
                        piece = True
                    case "r":
                        L = QLabel()
                        L.setPixmap(QPixmap("Images/rB.png"))
                        piece = True
                    case "n":
                        L = QLabel()
                        L.setPixmap(QPixmap("Images/nB.png"))
                        piece = True
                    case "b":
                        L = QLabel()
                        L.setPixmap(QPixmap("Images/bB.png"))
                        piece = True
                    case "q":
                        L = QLabel()
                        L.setPixmap(QPixmap("Images/qB.png"))
                        piece = True
                    case "k":
                        L = QLabel()
                        L.setPixmap(QPixmap("Images/kB.png"))
                        piece = True
                    case "p":
                        L = QLabel()
                        L.setPixmap(QPixmap("Images/pB.png"))
                        piece = True

                    case _:
                        if col.isdigit():
                            colTracker += int(col) - 1
                        piece = False
                if piece:
                    L.setScaledContents(True)
                    L.setSizePolicy(QSizePolicy.Ignored, QSizePolicy.Ignored)
                    board.addWidget(L, rowTracker, colTracker)

                if targetCol == colTracker and targetRow == rowTracker:
                    square = QWidget()
                    squareLayout = QGridLayout()
                    squareLayout.setSpacing(0)

                    filler1 = QLabel()
                    filler2 = QLabel()
                    filler3 = QLabel()
                    squareLayout.addWidget(filler1, 0, 0)
                    squareLayout.addWidget(filler2, 1, 1)
                    squareLayout.addWidget(filler3, 1, 0)
                    A = QLabel()
                    A.setScaledContents(True)
                    A.setSizePolicy(QSizePolicy.Ignored, QSizePolicy.Ignored)
                    squareLayout.addWidget(A, 0, 1)
                    square.setLayout(squareLayout)
                    board.addWidget(square, rowTracker, colTracker)
                    match MoveType:
                        case "Best":
                            A.setPixmap(QPixmap("Images/Best.png"))
                        case "Good":
                            A.setPixmap(QPixmap("Images/Good.png"))
                        case "Okay":
                            A.setPixmap(QPixmap("Images/Okay.png"))
                        case "Inaccuracy":
                            A.setPixmap(QPixmap("Images/Inaccuracy.png"))
                        case "Mistake":
                            A.setPixmap(QPixmap("Images/Mistake.png"))
                        case "Blunder":
                            A.setPixmap(QPixmap("Images/Blunder.png"))


                colTracker += 1

            colTracker = 0
            rowTracker +=1



        return board

    def initStats(self, move, xMoves):
        match move:
            case "numberOfBestMoves":
                title = "Best Moves: "
                image = "Best.png"
                color = "#25E6B9"
            case "numberOfGoodMoves":
                title = "Good Moves: "
                image = "Good.png"
                color = "#B5E61D"
            case "numberOfOkayMoves":
                title = "Okay Moves: "
                image = "Okay.png"
                color = "#5B7F6C"
            case "numberOfInaccuracies":
                title = "Inaccuracies: "
                image = "Inaccuracy.png"
                color = "#FFF200"
            case "numberOfMistakes":
                title = "Mistakes: "
                image = "Mistake.png"
                color = "#FF7F27"
            case "numberOfBlunders":
                title = "Blunders: "
                image = "Blunder.png"
                color = "#ED1C24"

        #move Formatting
        moveLayout = QHBoxLayout()

        moveMarginL = QHBoxLayout()
        moveMargin = QLabel()
        moveMargin.setStyleSheet("background-color: #424242")
        moveLabel = QLabel(title)
        moveNumber = QLabel(str(xMoves[move]))
        moveFont = QFont("Arial", 12)
        moveLabel.setStyleSheet("background-color: #424242 ; color: white")
        moveNumber.setStyleSheet("background-color: #424242 ; color: " + color)
        moveLabel.setFont(moveFont)
        moveNumber.setFont(moveFont)
        moveLabel.setAlignment(Qt.AlignLeft | Qt.AlignVCenter)
        moveNumber.setAlignment(Qt.AlignCenter)
        moveMarginL.addWidget(moveMargin)
        moveMarginL.addWidget(moveLabel)

        #logo Formatting
        logo = QLabel()
        logo.setStyleSheet("background-color: #424242")
        logo.setPixmap(QPixmap("Images/" + image))
        logo.setScaledContents(True)
        logo.setSizePolicy(QSizePolicy.Ignored, QSizePolicy.Ignored)

        marginL = QHBoxLayout()
        margin = QLabel()
        margin.setStyleSheet("background-color: #424242")
        marginL.addWidget(logo, 1)
        marginL.addWidget(margin, 3)

        #finalizing
        moveLayout.addLayout(moveMarginL, 1)
        moveLayout.addWidget(moveNumber, 1)
        moveLayout.addLayout(marginL, 1)

        return moveLayout

    def initLayout(self, FENs, LANs, indexMoveType, wMoves, wAccuracy, bMoves, bAccuracy):
        mainLayout = QHBoxLayout()
        sidePanel = QVBoxLayout()


        #-----Board------
        for i in range(len(FENs)):
            board = QWidget()
            if i < 1:
                board.setLayout(self.initBoard(FENs[i], None, None))
            else:
                board.setLayout(self.initBoard(FENs[i], LANs[i - 1], indexMoveType[str(i - 1)]))

            self.boardLayout.addWidget(board)


        #----Side Panel-----

        # ExitButton
        buttonFont = QFont("Arial", 12)
        exitButton = QPushButton("X")
        exitButton.setStyleSheet("background-color: #424242 ; color: #b00000")
        exitButton.setFont(buttonFont)
        exitButton.clicked.connect(self.exitScreen)
        exitMargin = QLabel()
        exitMargin.setStyleSheet("background-color: #424242")
        ExitButtonLayout = QHBoxLayout()
        ExitButtonLayout.addWidget(exitMargin, 3)
        ExitButtonLayout.addWidget(exitButton, 1)

        sidePanel.addLayout(ExitButtonLayout)

        #Stats
        statsLayout = QVBoxLayout()
        statsLayout.setSpacing(0)

        labelFont = QFont("Arial", 14)

        whiteLabel = QLabel("White:")
        whiteLabel.setAlignment(Qt.AlignCenter)
        whiteLabel.setStyleSheet("background-color: #424242 ; color: white")
        whiteLabel.setFont(labelFont)
        statsLayout.addWidget(whiteLabel)
        for move in wMoves.keys():
            moveLayout = self.initStats(move, wMoves)

            statsLayout.addLayout(moveLayout)

        accuracyFont = QFont("Arial", 12)

        whiteAccuracy = QLabel(f"Accuracy: {wAccuracy:.2f}%")
        whiteAccuracy.setStyleSheet("background-color: #424242 ; color: white")
        whiteAccuracy.setAlignment(Qt.AlignCenter)
        whiteAccuracy.setFont(accuracyFont)
        statsLayout.addWidget(whiteAccuracy)

        blackLabel = QLabel("Black:")
        blackLabel.setAlignment(Qt.AlignCenter)
        blackLabel.setStyleSheet("background-color: #424242 ; color: white")
        blackLabel.setFont(labelFont)
        statsLayout.addWidget(blackLabel)
        for move in bMoves.keys():
            moveLayout = self.initStats(move, bMoves)

            statsLayout.addLayout(moveLayout)

        blackAccuracy = QLabel(f"Accuracy: {bAccuracy:.2f}%")
        blackAccuracy.setStyleSheet("background-color: #424242 ; color: white")
        blackAccuracy.setAlignment(Qt.AlignCenter)
        blackAccuracy.setFont(accuracyFont)
        statsLayout.addWidget(blackAccuracy)

        sidePanel.addLayout(statsLayout)



        #Buttons
        nextButton = QPushButton(">")
        nextButton.setStyleSheet("background-color: #424242 ; color: white")
        nextButton.setFont(buttonFont)
        nextButton.clicked.connect(self.next_page)
        prevButton = QPushButton("<")
        prevButton.setStyleSheet("background-color: #424242 ; color: white")
        prevButton.setFont(buttonFont)
        prevButton.clicked.connect(self.prev_page)
        ButtonLayout = QHBoxLayout()
        ButtonLayout.addWidget(prevButton)
        ButtonLayout.addWidget(nextButton)

        sidePanel.addLayout(ButtonLayout)


        #-----Main Layout-----
        self.boardLayout.setContentsMargins(0, 0, 0, 0)
        sidePanel.setContentsMargins(0, 0, 0, 0)
        mainLayout.setContentsMargins(0, 0, 0, 0)

        mainLayout.addLayout(self.boardLayout, 3)
        mainLayout.addLayout(sidePanel, 2)
        mainLayout.setSpacing(0)

        self.setLayout(mainLayout)

    def next_page(self):
        index = self.boardLayout.currentIndex() + 1
        self.boardLayout.setCurrentIndex(index)

    def prev_page(self):
        index = self.boardLayout.currentIndex() - 1
        self.boardLayout.setCurrentIndex(index)

    def exitScreen(self):
        self.exitSignal.emit()


