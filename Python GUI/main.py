
import sys
from PyQt5.QtWidgets import (QApplication, QMainWindow, QWidget, QStackedLayout)

from PyQt5.QtGui import QIcon
import json
import subprocess
from Game_Review_Screen import ReviewScreen
from Home_Screen import HomeScreen

from pathlib import Path

def get_base_dir():
    if getattr(sys, 'frozen', False):
        # Running as .exe
        return Path(sys.executable).parent
    else:
        # Running as script
        return Path(__file__).parent.parent


BaseDir = get_base_dir()

DataDir = BaseDir / "Data"

class MainWindow(QMainWindow):
    def __init__(self):
        super().__init__()
        self.setWindowTitle("Chess: Skill Ranked")
        self.setGeometry(300, 300, 1000, 750)
        self.show()
        self.setWindowIcon(QIcon("Images/Icon.png"))

        self.historyCode = 0


        gameHistory = self.getGameHistory()

        self.homeScreen = HomeScreen(gameHistory, self, BaseDir)
        self.centralLayout = QStackedLayout()
        self.initUI()

    def getGameHistory(self):
        gameHistoryPath = DataDir / 'GameHistory.txt'
        try:
            with open(gameHistoryPath, 'r') as f:
                gameHistory = f.readlines()

        except FileNotFoundError:
            print("Error: Game Code File Not Found")

        return gameHistory

    def initUI(self):
        self.centralLayout.addWidget(self.homeScreen)
        centralWidget = QWidget()

        centralWidget.setLayout(self.centralLayout)
        self.setCentralWidget(centralWidget)

        self.homeScreen.openReview.connect(self.startGameReview)




        self.showMaximized()


    def updateHome(self):

        gameHistory = self.getGameHistory()
        self.centralLayout.takeAt(0)


        self.homeScreen = HomeScreen(gameHistory, self, BaseDir)


        self.centralLayout.insertWidget(0, self.homeScreen)


        self.homeScreen.openReview.connect(self.startGameReview)




    def startGameReview(self):
        jarCodePath = BaseDir / 'analyzer.jar'

        try:
            subprocess.run(["java", "-jar", jarCodePath])
        except:
            print("Java Error")

        gameCodePath = DataDir / 'GameCode.txt'
        try:
            with open(gameCodePath, 'r') as f:
                gameCode = f.read()

        except FileNotFoundError:
            print("Error: Game Code File Not Found")


        jsonFile = DataDir / 'JsonFiles' / (gameCode + '_Game.json')

        try:
            with open(jsonFile, 'r') as f:
                data = json.load(f)
                FENs = data['FENs']
                LANs = data['LANs']
                indexMoveType = data['LANindexMoveType']
                wMoves = data['wMoves']
                bMoves = data['bMoves']
                wAccuracy = data['wAccuracy']
                bAccuracy = data['bAccuracy']

        except FileNotFoundError:
            print(f"Error: {gameCode}_Game.json was not found.\nMissing or corrupted Game Data for this Game.")

        gameReview = ReviewScreen(FENs, LANs, indexMoveType, wMoves, wAccuracy, bMoves, bAccuracy)
        gameReview.exitSignal.connect(self.closeReview)

        self.centralLayout.addWidget(gameReview)
        self.centralLayout.setCurrentIndex(1)

        self.updateHome()




    def startPrevReview(self, gameCode):
        jsonFile = DataDir / 'JsonFiles' / (gameCode + '_Game.json')

        try:
            with open(jsonFile, 'r') as f:
                data = json.load(f)
                FENs = data['FENs']
                LANs = data['LANs']
                indexMoveType = data['LANindexMoveType']
                wMoves = data['wMoves']
                bMoves = data['bMoves']
                wAccuracy = data['wAccuracy']
                bAccuracy = data['bAccuracy']

        except FileNotFoundError:
            print(f"Error: {gameCode}_Game.json was not found.")

        gameReview = ReviewScreen(FENs, LANs, indexMoveType, wMoves, wAccuracy, bMoves, bAccuracy)
        gameReview.exitSignal.connect(self.closeReview)
        self.centralLayout.addWidget(gameReview)
        self.centralLayout.setCurrentIndex(1)


    def closeReview(self):

        self.centralLayout.setCurrentIndex(0)
        self.centralLayout.takeAt(1)


def main():
    app = QApplication(sys.argv)
    window = MainWindow()

    sys.exit(app.exec_())

if __name__ == '__main__':
    main()