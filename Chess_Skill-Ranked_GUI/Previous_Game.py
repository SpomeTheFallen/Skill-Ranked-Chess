from PyQt5.QtWidgets import (QLabel, QWidget,
                             QHBoxLayout, QPushButton,
                             QSizePolicy, )

from PyQt5.QtGui import QFont
from PyQt5.QtCore import Qt, pyqtSignal


class PreviousGame(QWidget):
    openHistory = pyqtSignal()

    def __init__(self, prevGame):
        super().__init__()

        prev = prevGame.split(" ")
        if len(prev) < 2:
            print("Error: Previous Game must contain 2 items" + prevGame)
            return

        self.gameCode, self.date = prev[0], prev[1]

        self.initLayout()

    def initLayout(self):
        layout = QHBoxLayout()
        layout.setSpacing(0)

        margin = QLabel()
        margin.setStyleSheet("background-color: #424242")

        font = QFont("Arial", 12)
        analyze = QPushButton()
        analyze.setText("Analyze")
        analyze.setStyleSheet("background-color: #424242 ; color: white")
        analyze.setFont(font)
        analyze.setSizePolicy(QSizePolicy.Expanding, QSizePolicy.Expanding)
        analyze.clicked.connect(self.openHistory.emit)

        dateLabel = QLabel(self.date)
        dateLabel.setAlignment(Qt.AlignCenter)
        dateLabel.setStyleSheet("background-color: #424242 ; color: white")
        dateLabel.setFont(font)

        layout.addWidget(margin, 5)
        layout.addWidget(analyze, 2)
        layout.addWidget(dateLabel, 3)
        layout.setContentsMargins(0, 0, 0, 0)

        self.setLayout(layout)
