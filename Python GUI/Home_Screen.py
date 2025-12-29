from functools import partial

from PyQt5.QtWidgets import (QLabel, QWidget, QVBoxLayout,
                             QHBoxLayout,  QPushButton, QStackedLayout,
                             QSizePolicy, QPlainTextEdit)

from PyQt5.QtGui import QFont, QPixmap
import json
from PyQt5.QtCore import Qt, pyqtSignal
from Previous_Game import PreviousGame





class HomeScreen(QWidget):
    openReview = pyqtSignal()
    def __init__(self, gameHistory, parent, BaseDir):
        super().__init__()
        self.importLayout = QStackedLayout()

        self.pgnTextBox = QPlainTextEdit()

        self.historyLayout = QVBoxLayout()

        self.BaseDir = BaseDir

        self.initLayout(gameHistory, parent)


    def initLayout(self, gameHistory, parent):
        mainLayout = QVBoxLayout()

        #-----import PGN------

        importFont = QFont("Arial", 12)
        importLabel = QPushButton("Import PGN")
        importLabel.setStyleSheet("background-color: #424242 ; color: white")
        importLabel.setFont(importFont)
        importLabel.clicked.connect(self.importPGN)

        #PGN Text box
        PGNBoxLayout = QHBoxLayout()
        self.pgnTextBox.setPlaceholderText("Paste PGN")
        self.pgnTextBox.setFont(importFont)
        submitButton = QPushButton("Submit")
        submitButton.setFont(importFont)
        submitButton.clicked.connect(self.submitPGN)
        PGNBoxLayout.addWidget(self.pgnTextBox)
        PGNBoxLayout.addWidget(submitButton)
        pgnBox = QWidget()
        pgnBox.setLayout(PGNBoxLayout)

        self.importLayout.addWidget(importLabel)
        self.importLayout.addWidget(pgnBox)

        #color picker

        colPickerLayout = QHBoxLayout()
        colWhite = QPushButton("White")
        colWhite.setFont(importFont)
        colWhite.setStyleSheet("background-color: #424242 ; color: white")
        colWhite.setSizePolicy(QSizePolicy.Expanding, QSizePolicy.Expanding)
        colBlack = QPushButton("Black")
        colBlack.setFont(importFont)
        colBlack.setStyleSheet("background-color: #424242 ; color: white")
        colBlack.setSizePolicy(QSizePolicy.Expanding, QSizePolicy.Expanding)

        colWhite.clicked.connect(self.colorWhite)
        colBlack.clicked.connect(self.colorBlack)

        colPickerLayout.addWidget(colWhite)
        colPickerLayout.addWidget(colBlack)

        colPicker = QWidget()
        colPicker.setLayout(colPickerLayout)

        self.importLayout.addWidget(colPicker)




        #-----Game History-----
        historyTitle = QLabel("Game History")
        historyTitle.setFont(importFont)
        historyTitle.setStyleSheet("background-color: #424242 ; color: white")
        historyTitle.setAlignment(Qt.AlignCenter)
        historyTitle.setSizePolicy(QSizePolicy.Expanding, QSizePolicy.Expanding)
        self.historyLayout.addWidget(historyTitle)

        if len(gameHistory) > 0:
            for i in range(len(gameHistory) - 1, -1, -1):
                game = gameHistory[i]
                prevGame = PreviousGame(game)
                self.historyLayout.addWidget(prevGame)
                prevGame.openHistory.connect(partial(parent.startPrevReview, prevGame.gameCode))

        self.historyLayout.setSpacing(0)


        #-----Ranks-----
        rankLayout = QHBoxLayout()

        points = self.getRank(gameHistory)
        pointsLabel = QLabel("Points: " + str(points))
        pointsFont = QFont("Arial", 30)
        pointsLabel.setFont(pointsFont)
        pointsLabel.setStyleSheet("background-color: #424242 ; color: white")
        pointsLabel.setAlignment(Qt.AlignCenter)

        rank = QLabel()

        if points <= 1000 :
            rank.setPixmap(QPixmap("Images/P.png"))
            rank.setStyleSheet("background-color: #424242")
            rank.setScaledContents(True)
            rank.setSizePolicy(QSizePolicy.Ignored, QSizePolicy.Ignored)
        elif points <= 2000 :
            rank.setPixmap(QPixmap("Images/B.png"))
            rank.setStyleSheet("background-color: #424242")
            rank.setScaledContents(True)
            rank.setSizePolicy(QSizePolicy.Ignored, QSizePolicy.Ignored)
        elif points <= 3000 :
            rank.setPixmap(QPixmap("Images/N.png"))
            rank.setStyleSheet("background-color: #424242")
            rank.setScaledContents(True)
            rank.setSizePolicy(QSizePolicy.Ignored, QSizePolicy.Ignored)
        elif points <= 4000 :
            rank.setPixmap(QPixmap("Images/R.png"))
            rank.setStyleSheet("background-color: #424242")
            rank.setScaledContents(True)
            rank.setSizePolicy(QSizePolicy.Ignored, QSizePolicy.Ignored)
        elif points <= 5000 :
            rank.setPixmap(QPixmap("Images/Q.png"))
            rank.setStyleSheet("background-color: #424242")
            rank.setScaledContents(True)
            rank.setSizePolicy(QSizePolicy.Ignored, QSizePolicy.Ignored)
        elif points <= 6000 :
            rank.setPixmap(QPixmap("Images/K.png"))
            rank.setStyleSheet("background-color: #424242")
            rank.setScaledContents(True)
            rank.setSizePolicy(QSizePolicy.Ignored, QSizePolicy.Ignored)



        rankLayout.addWidget(rank, 4)
        rankLayout.addWidget(pointsLabel, 2)

        #----finalizing-----
        mainLayout.setContentsMargins(0, 0, 0, 0)
        self.importLayout.setContentsMargins(0, 0, 0, 0)
        self.historyLayout.setContentsMargins(0, 0, 0, 0)
        rankLayout.setContentsMargins(0, 0, 0, 0)

        mainLayout.setSpacing(0)
        mainLayout.addLayout(rankLayout, 2)
        mainLayout.addLayout(self.importLayout, 1)
        mainLayout.addLayout(self.historyLayout, 2)


        self.setLayout(mainLayout)

    def updateHistory(self, gameHistory):
        prevGame = PreviousGame(gameHistory[-1])
        self.historyLayout.insertWidget(0, PreviousGame(prevGame))
        return prevGame

    def getRank(self, gameHistory):
        if len(gameHistory) > 0:
            gameCode = gameHistory[-1].split(" ")[0]

            jsonFile = self.BaseDir / "Data" / 'JsonFiles' / (gameCode + '_Game.json')

            try:
                with open(jsonFile, 'r') as f:
                    data = json.load(f)
                    points = data['rank_points']

            except FileNotFoundError:
                print(f"Error: {gameCode}_Game.json was not found.")
        else:
            points = 1000
        return points

    def submitPGN(self):
        pgn = self.pgnTextBox.toPlainText()
        gameCodePath = self.BaseDir / 'Data' / 'pgn.txt'

        with open(gameCodePath, 'w') as f:
            f.write(pgn)


        self.importLayout.setCurrentIndex(2)

    def importPGN(self):
        self.importLayout.setCurrentIndex(1)

    def colorWhite(self):

        gameCodePath = self.BaseDir / 'Data' / 'pgn.txt'
        try:
            with open(gameCodePath, 'a') as f:
                f.write("\nColor: White")

        except FileNotFoundError:
            print("Error: PGN File Not Found")


        self.importLayout.setCurrentIndex(0)
        self.openReview.emit()

    def colorBlack(self):

        gameCodePath = self.BaseDir / 'Data' / 'pgn.txt'
        try:
            with open(gameCodePath, 'a') as f:
                f.write("\n Color: Black")

        except FileNotFoundError:
            print("Error: PGN File Not Found")


        self.importLayout.setCurrentIndex(0)
        self.openReview.emit()
