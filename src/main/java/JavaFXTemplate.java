import javafx.animation.PauseTransition;
import javafx.application.Application;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class JavaFXTemplate extends Application {

	int numDraw, numSelections, numMatched, winnings, totWinnings;
	boolean selectionStarted, gameStarted, selectionChose, drawChose;
	String matchedNums;
	Label title,matchedNumsLab,numsMatchedLab,winningsLab,totWinningsLab;
	Button play, exit, s1, s4, s8, s10, d1, d2, d3, d4, startDraw, randSelect, cont;
	Popup gameEnd, rules, odds;
	MenuBar mb1, mb2;
	Menu m1, m2;
	MenuItem rulesItem, rulesItem2, oddsItem, oddsItem2, themeItem, quitItem;
	ArrayList<StackPane> pieceList, selectedPieces, drawnPieces;
	HashMap<String, Scene> sceneMap;
	EventHandler<ActionEvent> numSelectButtons, drawingButtons, drawRandom, startDrawings, continueGame;
	PauseTransition pause = new PauseTransition(Duration.seconds(2));

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Keno");

		matchedNums = "";
		numDraw = numSelections = numMatched = winnings = totWinnings = 0;
		selectionStarted = selectionChose = drawChose = gameStarted = false;
		mb1 = new MenuBar();
		mb2 = new MenuBar();
		m1 = new Menu("Menu");
		m2 = new Menu("Menu");
		quitItem = new MenuItem("Quit");
		quitItem.setOnAction(e -> Platform.exit());
		mb1.getMenus().add(m1);
		mb2.getMenus().add(m2);

		sceneMap = new HashMap<>();
		sceneMap.put("start", createTitleScreen(primaryStage));
		sceneMap.put("play", createPlayScreen(primaryStage));

		play.setOnAction(e -> primaryStage.setScene(sceneMap.get("play")));

		Label endOfDrawLab = new Label("End of draw!");
		endOfDrawLab.setStyle("-fx-background-color: white;" + "-fx-border-color: black;" +
				 		      "-fx-font-size: 80");
		Button exitEOD = new Button("New Game");
		exitEOD.setOnAction(e -> {
			gameEnd.hide();
			resetGame();
		});
		VBox eodBox = new VBox(endOfDrawLab, exitEOD);
		eodBox.setStyle("-fx-font-family: Arial;");
		gameEnd = new Popup();
		gameEnd.setX(530);
		gameEnd.setY(400);
		gameEnd.getContent().setAll(eodBox);

		Label rulesLabel = new Label("1. Select how many spots to select, 1, 4, 8, or 10.\n" +
							       "2. Select how many consecutive draws to play.\n" +
								   "3. Select as many numbers as you did spots.\n" +
								   "Pick from the numbers 1-80 or use random select.\n");
		rulesLabel.setStyle("-fx-background-color: white;" + "-fx-border-color: black;" +
				    	  "-fx-font-size: 20");
		Button closeRulesButton = new Button("Close");
		closeRulesButton.setOnAction(e -> rules.hide());
		VBox rulesVBox = new VBox(5,rulesLabel,closeRulesButton);
		rulesVBox.setStyle("-fx-font-family: Arial;");
		rules = new Popup();
		rules.setX(300);
		rules.setY(170);
		rules.getContent().setAll(rulesVBox);

		Label oddsLabel = new Label("1 Spot Game:\nMatch 1, win 2\n" +
									"\n4 Spot Game:\nMatch 2, win 1\nMatch 3, win 5\nMatch 4, win 75\n" +
									"\n8 Spot Game:\nMatch 4, win 2\nMatch 5, win 12\nMatch 6, win 50" +
									"\nMatch 7, win 750\nMatch 8, win 10000\n" +
									"\n10 Spot Game:\nMatch 0, win 5\nMatch 5, win 2\nMatch 6, win 15" +
									"\nMatch 7, win 40\nMatch 8, win 450\nMatch 9, win 4250\nMatch 10, win 100000");
		oddsLabel.setStyle("-fx-background-color: white;" + "-fx-border-color: black;" +
						   "-fx-font-size: 20");
		Button closeOddsButton = new Button("Close");
		closeOddsButton.setOnAction(e -> odds.hide());
		VBox oddsVBox = new VBox(5, oddsLabel, closeOddsButton);
		oddsVBox.setStyle("-fx-font-family: Arial;");
		odds = new Popup();
		odds.setX(300);
		odds.setY(160);
		odds.getContent().setAll(oddsVBox);

		primaryStage.setScene(sceneMap.get("start"));
		primaryStage.show();
	}

	// method to create the title screen
	public Scene createTitleScreen(Stage primaryStage) {
		BorderPane root = new BorderPane();
		root.setStyle("-fx-font-family: Arial;");

		// game title
		title = new Label("Keno");
		title.setStyle("-fx-font-size: 80;"+"-fx-text-fill: black");
		title.setPrefWidth(900);
		title.setAlignment(Pos.CENTER);

		// play button
		play = new Button("Play");
		play.setPrefWidth(100);

		// align elements with a vbox
		VBox paneCenter = new VBox(20, title, play);
		paneCenter.setAlignment(Pos.CENTER);
		paneCenter.setStyle("-fx-font-family: Arial;");

		// menu bar
		rulesItem = new MenuItem("Rules");
		rulesItem.setOnAction(e -> rules.show(primaryStage));
		oddsItem = new MenuItem("Odds");
		oddsItem.setOnAction(e -> odds.show(primaryStage));
		m1.getItems().addAll(rulesItem, oddsItem, quitItem);
		m1.setStyle("-fx-font-family: Arial;");

		// create scene
		root.setTop(mb1);
		root.setCenter(paneCenter);
		root.setStyle("-fx-background-color: white");
		return new Scene(root, 900, 700);
	}

	// method to create the play screen
	public Scene createPlayScreen(Stage primaryStage) {
		// initialize members
		BorderPane root = new BorderPane();
		root.setStyle("-fx-font-family: Arial;");
		selectedPieces = new ArrayList<>();
		drawnPieces = new ArrayList<>();
		rulesItem2 = new MenuItem("Rules");
		rulesItem2.setOnAction(e -> rules.show(primaryStage));
		oddsItem2 = new MenuItem("Odds");
		oddsItem2.setOnAction(e -> odds.show(primaryStage));
		themeItem = new MenuItem("Change Theme");

		m2.getItems().addAll(rulesItem2, oddsItem2, themeItem, quitItem);
		exit = new Button("Exit");

		//this handler is used by num drawing selection buttons
		numSelectButtons = actionEvent -> {
			if (!gameStarted) {
				// if a new selection is chosen, reset all selected pieces
				if (selectionStarted) {
					selectedPieces.forEach(e -> e.getChildren().get(0).setStyle("-fx-fill: grey"));
					selectedPieces.clear();
					selectionStarted = false;
				}

				// re-enable each button
				s1.setDisable(false);
				s4.setDisable(false);
				s8.setDisable(false);
				s10.setDisable(false);

				// disable the clicked button and get its represented value
				Button b = (Button) actionEvent.getSource();
				b.setDisable(true);
				numSelections = Integer.parseInt(b.getText());
				selectionChose = true;
				randSelect.setDisable(false);  // enable the random select button now
			}
		};

		// handler used by num drawing buttons
		drawingButtons = actionEvent -> {
			if (!gameStarted) {
				// re-enable each button
				d1.setDisable(false);
				d2.setDisable(false);
				d3.setDisable(false);
				d4.setDisable(false);

				// disable the clicked button and get its represented value
				Button b = (Button) actionEvent.getSource();
				b.setDisable(true);
				numDraw = Integer.parseInt(b.getText());
				drawChose = true;
			}
			if (selectionChose && drawChose && selectedPieces.size() == numSelections) {
				startDraw.setDisable(false);
			}
		};

		// handler used to select random places
		drawRandom = actionEvent -> {
			// only allow random if selection has not been started
			if (!selectionStarted && numSelections > 0) {
				selectionStarted = true;

				// pick a random piece from the board
				Random rand = new Random();
				StackPane piece;

				// check if piece was not chosen, and add to selected
				while (selectedPieces.size() < numSelections) {
					piece = pieceList.get(rand.nextInt(pieceList.size()));
					if (!selectedPieces.contains(piece)) {
						piece.getChildren().get(0).setStyle("-fx-fill: gold");
						selectedPieces.add(piece);
						pause.play();
					}
				}

				randSelect.setDisable(true);
			}
			if (selectionChose && drawChose) {
				startDraw.setDisable(false);
			}
		};

		// handler to start the game draw
		startDrawings = actionEvent -> {
			// disable left side buttons
			s1.setDisable(true);
			s4.setDisable(true);
			s8.setDisable(true);
			s10.setDisable(true);
			d1.setDisable(true);
			d2.setDisable(true);
			d3.setDisable(true);
			d4.setDisable(true);

			// make sure pieces were selected and a number of draws was selected
			startDraw.setDisable(true);
			cont.setDisable(false);

			// call draw
			draw();
		};

		// handler for continue button
		continueGame = actionEvent -> {
			if (numDraw > 0) {
				startDraw.setDisable(true);
				cont.setDisable(false);
				drawnPieces.clear();
				resetBoard();
				draw();

			} else {
				cont.setDisable(true);
				gameEnd.show(primaryStage);
			}
		};

		// left side buttons
		Label selectLab = new Label("Number of selections:");
		s1 = new Button("1");
		s1.setOnAction(numSelectButtons);
		s4 = new Button("4");
		s4.setOnAction(numSelectButtons);
		s8 = new Button("8");
		s8.setOnAction(numSelectButtons);
		s10 = new Button("10");
		s10.setOnAction(numSelectButtons);
		HBox selectionButtons = new HBox(5, s1, s4, s8, s10);
		Label drawLab = new Label("Number of drawings:");
		d1 = new Button("1");
		d1.setOnAction(drawingButtons);
		d2 = new Button("2");
		d2.setOnAction(drawingButtons);
		d3 = new Button("3");
		d3.setOnAction(drawingButtons);
		d4 = new Button("4");
		d4.setOnAction(drawingButtons);
		HBox drawingButtons = new HBox(5, d1, d2, d3, d4);
		VBox leftSideButtons = new VBox(30, selectLab, selectionButtons, drawLab, drawingButtons);

		// right side buttons
		startDraw = new Button("Start Drawings");
		startDraw.setOnAction(startDrawings);
		startDraw.setDisable(true);
		randSelect = new Button("Select Random");
		randSelect.setOnAction(drawRandom);
		randSelect.setDisable(true);
		cont = new Button("Continue");
		cont.setOnAction(continueGame);
		cont.setDisable(true);
		matchedNumsLab = new Label("Matched Numbers:\n");
		numsMatchedLab = new Label("Numbers matched:\n");
		winningsLab = new Label("Winnings:\n");
		totWinningsLab = new Label("Total Winnings:\n");
		VBox rightSideButtons = new VBox(50, randSelect, startDraw, cont, matchedNumsLab,
										 numsMatchedLab, winningsLab, totWinningsLab);

		// create board
		pieceList = new ArrayList<>();
		GridPane gameBoard = createBoard();
		gameBoard.setAlignment(Pos.CENTER);

		// set up the scene
		root.setTop(mb2);
		root.setLeft(leftSideButtons);
		root.setRight(rightSideButtons);
		root.setCenter(gameBoard);
		return new Scene(root, 900, 700);
	}

	public GridPane createBoard() {
		// create a board of 80 pieces, add each piece to an ArrayList
		GridPane gameBoard = new GridPane();
		gameBoard.setPrefSize(8,10);
		gameBoard.setHgap(5);
		gameBoard.setVgap(5);

		int count = 1;
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 8; j++) {
				// stackpane contains the rectangle and the text
				StackPane piece = new StackPane();
				piece.setOnMouseClicked(e -> {  // event handler for piece selection
					if (selectionChose) {
						randSelect.setDisable(true);  // selection started, disable random option
						selectionStarted = true;
						if (selectedPieces.size() < numSelections) {  // check if more selections can be made
							if (!selectedPieces.contains(piece)) {	  // check if piece was already selected
								piece.getChildren().get(0).setStyle("-fx-fill: gold");
								selectedPieces.add(piece);
							}
						}
					}
					if (selectionChose && drawChose && selectedPieces.size() == numSelections) {
						startDraw.setDisable(false);
					}
				});

				Text rText = new Text(Integer.toString(count));
				Rectangle r = new Rectangle(50, 50);
				r.setStyle("-fx-fill: grey");
				piece.getChildren().addAll(r, rText);  // create stackpane for piece
				gameBoard.add(piece, j, i);  // add piece to board
				pieceList.add(piece);  // add piece to list of pieces
				count++;
			}
		}
		return gameBoard;
	}

	public void draw() {
		numMatched = winnings = 0;
		matchedNumsLab.setText("Matched numbers:\n");
		winningsLab.setText("Winnings:\n");
		Random rand = new Random();
		StackPane piece;
		while (drawnPieces.size() < 20) {	// run for 20 draws
			piece = pieceList.get(rand.nextInt(pieceList.size()));
			if (!drawnPieces.contains(piece)) {
				drawnPieces.add(piece);
				if (selectedPieces.contains(piece)) {  // piece was selected by player
					piece.getChildren().get(0).setStyle("-fx-fill: green");
					String matchedNumsLabelText = matchedNumsLab.getText();

					Text pieceNum = (Text) piece.getChildren().get(1);
					matchedNumsLabelText += pieceNum.getText() + " ";
					numMatched++;

					matchedNumsLab.setText(matchedNumsLabelText);
					numsMatchedLab.setText("Numbers matched:\n" + numMatched);

				} else {  // not correct selection
					piece.getChildren().get(0).setStyle("-fx-fill: #d30303");
				}
			}
		}
		numDraw--;
		calculateWinnings();
		totWinnings += winnings;
		winningsLab.setText("Winnings:\n" + winnings);
		totWinningsLab.setText("Total Winnings:\n" + totWinnings);

	}

	public void resetBoard() {
		pieceList.forEach(piece -> {
			if (!selectedPieces.contains(piece)) {
				piece.getChildren().get(0).setStyle("-fx-fill: grey");
			} else {
				piece.getChildren().get(0).setStyle("-fx-fill: gold");
			}
		});
	}

	public void resetGame() {
		// clear selected pieces
		selectedPieces.clear();

		// clear board selections
		resetBoard();
		drawnPieces.clear();
		numDraw = numSelections = 0;
		selectionStarted = gameStarted = selectionChose = drawChose = false;

		// re-enable buttons
		s1.setDisable(false);
		s4.setDisable(false);
		s8.setDisable(false);
		s10.setDisable(false);
		d1.setDisable(false);
		d2.setDisable(false);
		d3.setDisable(false);
		d4.setDisable(false);

		matchedNumsLab.setText("Matched numbers:\n");
		numsMatchedLab.setText("Numbers matched:\n");
		winningsLab.setText("Winnings:\n");
	}

	public void calculateWinnings() {
		if (numSelections == 1) {
			if (numMatched == 1) {
				winnings += 2;
			}
		} else if (numSelections == 4) {
			if (numMatched == 2) {
				winnings += 1;
			} else if (numMatched == 3) {
				winnings += 5;
			} else if (numMatched == 4) {
				winnings += 75;
			}
		} else if (numSelections == 8) {
			if (numMatched == 4) {
				winnings += 2;
			} else if (numMatched == 5) {
				winnings += 12;
			} else if (numMatched == 6) {
				winnings += 50;
			} else if (numMatched == 7) {
				winnings += 750;
			} else if (numMatched == 8) {
				winnings += 10000;
			}
		} else if (numSelections == 10) {
			if (numMatched == 0) {
				winnings += 5;
			} else if (numMatched == 5) {
				winnings += 2;
			} else if (numMatched == 6) {
				winnings += 15;
			} else if (numMatched == 7) {
				winnings += 40;
			} else if (numMatched == 8) {
				winnings += 450;
			} else if (numMatched == 9) {
				winnings += 4250;
			} else if (numMatched == 10) {
				winnings += 100000;
			}
		}
	}
}
