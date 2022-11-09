package com.mygdx.jar.graphicsObjects;

import static com.mygdx.jar.gameObjects.BoardObjects.PositionCheck.TitlesList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.Screen;
import com.mygdx.jar.ChessGame;
import com.mygdx.jar.CameraLauncher;
import com.mygdx.jar.gameObjects.BoardObjects.Board;
import com.mygdx.jar.gameObjects.BoardObjects.DetectionThread;
import com.mygdx.jar.gameObjects.BoardObjects.Point;
import com.mygdx.jar.gameObjects.BoardObjects.Position;
import com.mygdx.jar.imageHandlersObjects.ScreenshotFactory;

import java.util.Stack;

import sun.java2d.Surface;
import sun.java2d.SurfaceData;

class GameScreen implements Screen {
    private static final String ABC = "אבגדהוזחטיכלמנסעפצקרשתםןךףץ";
    private String[] CurrentTitlesList;

    // screen
    private final Camera camera;
    private final Viewport viewport;

    // graphics
    private final SpriteBatch batch;
    private final Texture[] ScrollingBackgrounds;
    private final Texture[] BackgroundsChessBoardCells;
    private final Texture[] SpinningOptionImages;
    private final Texture ReGameOption;
    public final PiecesImages piecesImages;

    private final Texture ReverseMoveOption;
    private final Texture ReplayReversedMoveOption;

    private final Texture GrayBackgroundForPawnWinning;
    private final Texture BlackBackgroundForPawnWinning;

    private final Texture TrashCan;
    private final Texture CameraImg;
    private final Texture SocialMedia;
    private final Texture ViewStateImg;
    private final Texture GalleryImg;
    private final Texture ButtonImg;
    private final Texture WhiteTurn;
    private final Texture BlackTurn;

    private Texture CameraStream;

    // timing
    private final float backgroundMaxScrollingSpeed;
    private final float[] backgroundOffsets = {0, 0, 0, 0};

    // world parameters
    private final float WORLD_WIDTH;
    private final float WORLD_HEIGHT;
    private final float SquaredSizeOfBoard;

    private final float BoardLeftLimit;
    private final float BoardRightLimit;
    private final float BoardDownLimit;
    private final float BoardUpLimit;

    private final float BoardViewLeftLimit;
    private final float BoardViewRightLimit;
    private final float BoardViewDownLimit;
    private final float BoardViewUpLimit;
    private int BoardNum;
    private boolean HasScrolled;

    private final int reGameWidth;
    private final int reGameHeight;
    private final int reverseMoveWidth;
    private final int reverseMoveHeight;

    // game stuff
    private final int BoardSize;
    private ChessGame chessGame;

    // touch stuff
    private Point TouchPos;
    private boolean TouchingNow;
    private boolean TouchedAlready;
    private Point ClickedPiece;
    private float xTouchPixel;
    private float yTouchPixel;
    private boolean IsActivePiece;
    private float currentTime = 0;
    private boolean IsSecondTouch;
    private float ViewOffset;

    private int direction;
    private boolean touchedColor;
    private boolean IsSpinning;
    private boolean wasPawnWin = false;
    private boolean hasBeenUntouched;
    private boolean ChoosingTitle;

    // game types
    private boolean IsFisherChess;
    private final LettersImages Letters_Images;

    private final Stack<Board> previousBoards;
    private final Stack<Board> previousBoardsForView;
    private final Stack<DetectionThread> detectionThreads;

    private String State;

    private final CameraLauncher cameraLauncher;
    private boolean IsCameraEnabled;
    private boolean wasGameMode;
    private boolean IsPermissionGranted;

    GameScreen(int widthScreen, int heightScreen, CameraLauncher launcher) {
        cameraLauncher = launcher;
        IsPermissionGranted = (cameraLauncher != null ? cameraLauncher.isPermissionGranted() : false);
        IsCameraEnabled = false;
        State = "View"; // "Game"
        wasGameMode = false;
        ChoosingTitle = false;
        IsFisherChess = false;
        BoardSize = 8;
        ViewOffset = 0;

        StartNewGame(IsFisherChess);
        Board board = new Board(Position.Chess_Board);
        previousBoards = new Stack<Board>();
        previousBoardsForView = new Stack<Board>();
        detectionThreads = new Stack<DetectionThread>();

        previousBoards.push(board);
        previousBoardsForView.push(new Board(board));
        detectionThreads.push(new DetectionThread("?", previousBoardsForView.peek()));

        direction = 1;
        IsSpinning = false;
        WORLD_WIDTH = widthScreen;
        WORLD_HEIGHT = heightScreen;
        if (WORLD_WIDTH < WORLD_HEIGHT) {
            SquaredSizeOfBoard = (float) (WORLD_WIDTH * 0.9);
        } else {
            SquaredSizeOfBoard = (float) (WORLD_HEIGHT * 0.9);
        }
        reGameWidth = (int) (WORLD_WIDTH / 8.0 * 3);
        reGameHeight = (int) (reGameWidth / 543.0 * 231);

        BoardLeftLimit = (float) ((WORLD_WIDTH - SquaredSizeOfBoard) / 2);
        BoardRightLimit = (float) ((WORLD_WIDTH - SquaredSizeOfBoard) / 2 + SquaredSizeOfBoard);
        BoardDownLimit = (float) ((WORLD_HEIGHT - SquaredSizeOfBoard) / 2);
        BoardUpLimit = (float) ((WORLD_HEIGHT - SquaredSizeOfBoard) / 2 + SquaredSizeOfBoard);

        BoardViewLeftLimit = (float) ((WORLD_WIDTH - SquaredSizeOfBoard) / 3);
        BoardViewRightLimit = (float) ((WORLD_WIDTH - SquaredSizeOfBoard) / 6 + WORLD_WIDTH / 2);
        BoardViewDownLimit = (float) (WORLD_HEIGHT - (SquaredSizeOfBoard / 2)) - (WORLD_WIDTH - SquaredSizeOfBoard) / 3;
        BoardViewUpLimit = (float) WORLD_HEIGHT;//((WORLD_HEIGHT + (SquaredSizeOfBoard / 2)) / 2);

        camera = new OrthographicCamera();
        viewport = new StretchViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);

        ScrollingBackgrounds = new Texture[4];
        ScrollingBackgrounds[0] = new Texture("core/images/ScrollingScreenImages/StarScape00.png");
        ScrollingBackgrounds[1] = new Texture("core/images/ScrollingScreenImages/StarScape01.png");
        ScrollingBackgrounds[2] = new Texture("core/images/ScrollingScreenImages/StarScape02.png");
        ScrollingBackgrounds[3] = new Texture("core/images/ScrollingScreenImages/StarScape03.png");

        BackgroundsChessBoardCells = new Texture[14];
        BackgroundsChessBoardCells[0] = new Texture("core/images/BoardCellsTypes/Type1/BoardWhiteCell.png");
        BackgroundsChessBoardCells[1] = new Texture("core/images/BoardCellsTypes/Type1/BoardBlackCell.png");
        BackgroundsChessBoardCells[2] = new Texture("core/images/BoardClueCells/BoardGreenCell.png");
        BackgroundsChessBoardCells[3] = new Texture("core/images/BoardClueCells/BoardDarkGreenCell.png");
        BackgroundsChessBoardCells[4] = new Texture("core/images/BoardClueCells/BoardRedCell.png");
        BackgroundsChessBoardCells[5] = new Texture("core/images/BoardClueCells/BoardBrownCell.png");
        BackgroundsChessBoardCells[6] = new Texture("core/images/BoardClueCells/BoardDarkBrownCell.png");
        BackgroundsChessBoardCells[7] = new Texture("core/images/BoardClueCells/BoardPurpleCell.png");
        BackgroundsChessBoardCells[8] = new Texture("core/images/BoardClueCells/BoardDarkPurpleCell.png");
        BackgroundsChessBoardCells[9] = new Texture("core/images/BoardClueCells/BoardPinkCell.png");
        BackgroundsChessBoardCells[10] = new Texture("core/images/BoardClueCells/BoardYellowCell.png");
        BackgroundsChessBoardCells[11] = new Texture("core/images/BoardClueCells/BoardGrayCell.png");
        BackgroundsChessBoardCells[12] = new Texture("core/images/BoardClueCells/BoardDarkGrayCell.png");
        BackgroundsChessBoardCells[13] = new Texture("core/images/BoardClueCells/BoardBlueCell.png");

        GrayBackgroundForPawnWinning = new Texture("core/images/PawnWinningImages/Gray.png");
        BlackBackgroundForPawnWinning = new Texture("core/images/PawnWinningImages/Black.png");
        TrashCan = new Texture("core/images/userStuff/trashCan.png");
        CameraImg = new Texture("core/images/userStuff/camera.png");
        SocialMedia = new Texture("core/images/userStuff/socialMedia.png");
        ViewStateImg = new Texture("core/images/userStuff/viewState.png");
        GalleryImg = new Texture("core/images/userStuff/gallery.png");
        ButtonImg = new Texture("core/images/userStuff/buttonImg.png");
        WhiteTurn = new Texture("core/images/userStuff/whiteTurn.png");
        BlackTurn = new Texture("core/images/userStuff/blackTurn.png");

        SpinningOptionImages = new Texture[2];
        SpinningOptionImages[0] = new Texture("core/images/userStuff/SpinBoardOff.png");
        SpinningOptionImages[1] = new Texture("core/images/userStuff/SpinBoardOn.png");

        ReGameOption = new Texture("core/images/userStuff/ReGame.png");

        ReverseMoveOption = new Texture("core/images/userStuff/ReverseMoveOption.png");
        reverseMoveWidth = (int) (WORLD_WIDTH / 10.0);
        reverseMoveHeight = (int) ((reverseMoveWidth / 551.0) * 659.0);
        ReplayReversedMoveOption = new Texture("core/images/userStuff/ReplayReversedMoveOption.png");

        piecesImages = new PiecesImages((int) (SquaredSizeOfBoard / 8));

        backgroundMaxScrollingSpeed = (float) WORLD_HEIGHT / 4;

        batch = new SpriteBatch();

        // Touch things
        TouchingNow = false;
        TouchedAlready = false;
        TouchPos = new Point();
        IsActivePiece = false;

        Letters_Images = new LettersImages();
    }

    @Override
    public void render(float deltaTime) {
        batch.begin();

        // scrolling background
        renderScrollingBackground(deltaTime);
        switch (State) {
            case "Game":
                renderGameState(deltaTime);
                wasGameMode = true;
                break;

            case "View":
                renderViewState(deltaTime);
                wasGameMode = false;
                break;

            case "Camera":
                renderCameraState(deltaTime);
                wasGameMode = false;
                break;

            default:
                renderGameOptions();
                wasGameMode = false;
                break;
        }
        batch.end();
    }

    private void renderCameraState(float deltaTime) {
        String imgPath = "CameraStream/Picture1.png";
        if (CameraStream == null){
            System.out.println("Something went wrong");
            CameraStream = new Texture(imgPath);
        }
        batch.draw(CameraStream, WORLD_WIDTH / 8, WORLD_HEIGHT / 8 * 2, WORLD_WIDTH / 4 * 3, WORLD_HEIGHT / 8 * 5);
        batch.draw(BackgroundsChessBoardCells[3], 0, 0, WORLD_WIDTH, WORLD_HEIGHT / 8);
        batch.draw(ButtonImg, (WORLD_WIDTH - WORLD_HEIGHT / 10) / 2, WORLD_HEIGHT / 100, WORLD_HEIGHT / 10, WORLD_HEIGHT / 10);
        batch.draw(GalleryImg, WORLD_WIDTH / 5 - WORLD_HEIGHT / 20, WORLD_HEIGHT / 100, WORLD_HEIGHT / 10, WORLD_HEIGHT / 10);
        batch.draw(ViewStateImg, WORLD_WIDTH / 16 * 12, WORLD_HEIGHT / 100, WORLD_HEIGHT / 10, WORLD_HEIGHT / 10);

        TouchedAlready = TouchingNow;
        TouchingNow = Gdx.input.isTouched();
        xTouchPixel = Gdx.input.getX();
        yTouchPixel = Gdx.input.getY();

        if (TouchedAlready && !TouchingNow) {
            xTouchPixel = Gdx.input.getX();
            yTouchPixel = Gdx.input.getY();
            if (yTouchPixel > WORLD_HEIGHT / 8 * 7) {
                if (xTouchPixel > (float) (WORLD_WIDTH / 16 * 12) && xTouchPixel < (float) (WORLD_WIDTH / 16 * 12) + (float) (WORLD_HEIGHT / 10)) {
                    State = "View";
                    if (IsCameraEnabled && (cameraLauncher != null)){
                        cameraLauncher.closeCamera();
                    }
                } else if (xTouchPixel > (WORLD_WIDTH - WORLD_HEIGHT / 10) / 2 && xTouchPixel < (WORLD_WIDTH + WORLD_HEIGHT / 10) / 2) {
                    StartNewGame(true);
                    Board newBoard = new Board(Position.Chess_Board);
                    previousBoards.push(newBoard);
                    previousBoardsForView.push(new Board(newBoard));
                    detectionThreads.push(new DetectionThread("?", previousBoardsForView.peek()));
                    BoardNum = 0;
                    State = "Game";
                    ChoosingTitle = false;
                    if (IsCameraEnabled && (cameraLauncher != null)){
                        cameraLauncher.closeCamera();
                    }
                }
            }
        }

        if (cameraLauncher != null){
            boolean wasCameraEnabled = IsCameraEnabled;
            IsCameraEnabled = TouchingNow &&
                    (xTouchPixel > WORLD_WIDTH / 8 && xTouchPixel < WORLD_WIDTH / 8 * 7) &&
                    (yTouchPixel > WORLD_HEIGHT / 8 * 1 && yTouchPixel < WORLD_HEIGHT / 8 * 6);

            boolean isGalleryEnabled = TouchingNow &&
                                        yTouchPixel > WORLD_HEIGHT / 8 * 7 &&
                                        xTouchPixel > WORLD_WIDTH / 5 - WORLD_HEIGHT / 20 &&
                                        xTouchPixel < WORLD_WIDTH / 5 + WORLD_HEIGHT / 20;
            if (IsCameraEnabled){
                if (!wasCameraEnabled){
                    cameraLauncher.openCamera();
                }
                if (CameraStream == null){
                    System.out.println("Something went wrong!!!!!!!!!!!!!");
                }
                cameraLauncher.captureImage();
            }
            else{
                if (wasCameraEnabled){
                    cameraLauncher.closeCamera();
                }
            }
            if (isGalleryEnabled) {
                cameraLauncher.openGallery();
            }
            CameraStream = cameraLauncher.getCapturedImage();
            CameraStream = cameraLauncher.getCapturedImage();
        }
    }

    private void renderViewState(float deltaTime) {
        float maxHeight = renderViewStateScreen();
        // get the screen position of the touch

        float touchX = Gdx.input.getX();
        float touchY = Gdx.input.getY();
        TouchedAlready = TouchingNow;
        TouchingNow = Gdx.input.isTouched();

        detectInputViewState(deltaTime, maxHeight, touchX, touchY);
    }

    private void detectInputViewState(float deltaTime, float maxHeight, float touchX, float touchY) {
        if (TouchingNow) {
            if (TouchedAlready) {
                float velocity = (Math.abs(yTouchPixel - touchY) / WORLD_HEIGHT) / deltaTime;
                if (velocity > (WORLD_WIDTH / 350) * 0.3) {
                    HasScrolled = true;
                }
                if (touchY < WORLD_HEIGHT / 8 * 7) {
                    ViewOffset += yTouchPixel - touchY;
                    ViewOffset = Math.max(Math.min(ViewOffset, maxHeight - WORLD_HEIGHT), 0);
                }
            }
            xTouchPixel = (int) touchX;
            yTouchPixel = (int) touchY;

            int boardX = (int) (xTouchPixel / (WORLD_WIDTH / 2));
            int boardY = (int) ((yTouchPixel + ViewOffset) / ((SquaredSizeOfBoard / 2) + (WORLD_WIDTH - SquaredSizeOfBoard) / 3));

            if ((xTouchPixel > (WORLD_WIDTH - SquaredSizeOfBoard) / 3 + (SquaredSizeOfBoard / 2 + (WORLD_WIDTH - SquaredSizeOfBoard) / 3) * boardX &&
                    xTouchPixel < (float) (WORLD_WIDTH / 2) - (WORLD_WIDTH - SquaredSizeOfBoard) / 6 + (SquaredSizeOfBoard / 2 + (WORLD_WIDTH - SquaredSizeOfBoard) / 3) * boardX)
                    && (yTouchPixel + ViewOffset > (WORLD_WIDTH - SquaredSizeOfBoard) / 3 + (SquaredSizeOfBoard / 2 + (WORLD_WIDTH - SquaredSizeOfBoard) / 3) * boardY &&
                    yTouchPixel + ViewOffset < (SquaredSizeOfBoard / 2 + (WORLD_WIDTH - SquaredSizeOfBoard) / 3) * (boardY + 1) &&
                    yTouchPixel < WORLD_HEIGHT - (float) (WORLD_HEIGHT / 8))) {
                int boardNum = 2 * boardY + boardX;
                if (BoardNum != boardNum && !HasScrolled) {
                    BoardNum = boardNum;
                    currentTime = 0;
                }
                currentTime += deltaTime;
            } else {
                if (yTouchPixel < WORLD_HEIGHT - (float) (WORLD_HEIGHT / 8)) {
                    BoardNum = -1;
                }
            }
        } else {
            ViewOffset = Math.max(Math.min(ViewOffset, maxHeight - WORLD_HEIGHT), 0);
            xTouchPixel = (int) touchX;
            yTouchPixel = (int) touchY;
            if (TouchedAlready) {
                if (yTouchPixel > WORLD_HEIGHT - (float) (WORLD_HEIGHT / 8)) {
                    if (xTouchPixel > WORLD_WIDTH / 4 * 3) {
                        if (BoardNum != -1) {
                            Stack<Board> temp = new Stack<Board>();
                            Stack<Board> tempView = new Stack<Board>();
                            Stack<DetectionThread> tempThreads = new Stack<DetectionThread>();
                            int boardNum = 0;
                            while (!previousBoards.empty() && BoardNum != boardNum) {
                                temp.push(previousBoards.pop());
                                tempView.push(previousBoardsForView.pop());
                                tempThreads.push(detectionThreads.pop());
                                boardNum++;
                            }
                            if (!previousBoards.empty()) {
                                previousBoards.pop();
                                previousBoardsForView.pop();
                                detectionThreads.pop();
                            }
                            while (!temp.empty()) {
                                previousBoards.push(temp.pop());
                                previousBoardsForView.push(tempView.pop());
                                detectionThreads.push(tempThreads.pop());
                            }
                            BoardNum = -1;
                        }
                    } else if (xTouchPixel > WORLD_WIDTH / 20 && xTouchPixel < WORLD_WIDTH / 4 + WORLD_WIDTH / 20) {
                        if (!IsPermissionGranted && cameraLauncher != null){
                            cameraLauncher.askAllPermissions();
                        }
                        if (IsPermissionGranted){
                            State = "Camera";
                        }
                        else {
                            if (cameraLauncher != null){
                                IsPermissionGranted = cameraLauncher.isPermissionGranted();
                            }
                        }
                    }
                } else if (!HasScrolled && BoardNum != -1 && currentTime > 0.1) {
                    Stack<Board> tempBoards = new Stack<Board>();
                    Stack<Board> tempBoardsView = new Stack<Board>();
                    Stack<DetectionThread> tempThreads = new Stack<DetectionThread>();
                    int boardNum = 0;
                    while (!previousBoards.empty() && BoardNum != boardNum) {
                        tempBoards.push(previousBoards.pop());
                        tempBoardsView.push(previousBoardsForView.pop());
                        tempThreads.push(detectionThreads.pop());
                        boardNum++;
                    }
                    EnterGame(previousBoards.peek());
                    State = "Game";
                    ChoosingTitle = false;
                    while (!tempBoards.empty()) {
                        previousBoards.push(tempBoards.pop());
                        previousBoardsForView.push(tempBoardsView.pop());
                        detectionThreads.push(tempThreads.pop());
                    }
                }
            }
            currentTime = 0;
            HasScrolled = false;
        }
    }

    private void renderGameState(float deltaTime) {
        // render board map
        renderChessBoard(BoardLeftLimit, BoardDownLimit, SquaredSizeOfBoard);
        renderReverseMoveOption();
        renderReplayReversedMoveOption();
        renderGameTransferScreens();
        renderOpposeStartingColor();
        renderChoosingPawnNewType();
        renderTitle(WORLD_WIDTH / 2, WORLD_HEIGHT / 16 * 15, WORLD_HEIGHT / 40);
        if (!wasGameMode && (cameraLauncher != null)) {
            ScreenshotFactory.saveScreenshot(cameraLauncher.getImagesDir(),
                    (int) BoardLeftLimit,
                    (int) BoardDownLimit,
                    (int) SquaredSizeOfBoard, (int) SquaredSizeOfBoard);
        }

        TouchedAlready = TouchingNow;
        TouchingNow = Gdx.input.isTouched();
        xTouchPixel = Gdx.input.getX();
        yTouchPixel = Gdx.input.getY();

        boolean wasChoosingTitle = ChoosingTitle;
        detectInputChoosingTitle(WORLD_WIDTH / 2, WORLD_HEIGHT / 16 * 15, WORLD_HEIGHT / 40);

        if (!(ChoosingTitle || wasChoosingTitle)) {
            if (!chessGame.GameOver) {
                // render the option of spinning screen
                renderSpinningScreenOption();
                detectInputSpinningScreenOption();

                if (!chessGame.WinPawn) {
                    hasBeenUntouched = false;
                    // detect input
                    detectInput(deltaTime);

                    // Check if Some king being thread
                    if (chessGame.SomePieceMoved) {
                        chessGame.SomePieceMoved = false;
                        if (!chessGame.WinPawn) {
                            if (IsSpinning && (Position.Is_white_turn ^ direction == 1)) {
                                direction *= -1;
                            }
                        }
                    }
                } else {
                    // get new Pawn type input
                    String newPawnType = detectInputNewPawnType();
                    if (!newPawnType.equals("")) {
                        chessGame.PawnChangedInto(newPawnType);
                        if (IsSpinning && (Position.Is_white_turn ^ direction == 1)) {
                            direction *= -1;
                        }
                    } else {
                        if (TouchedAlready && !TouchingNow && hasBeenUntouched) {
                            chessGame.ReversePawnWinning();
                            if (ClickedPiece != null) {
                                if (ChessGame.CountClicksOnSameCell_AfterCell(ClickedPiece) % 2 == 1) {
                                    chessGame.ClickDuringGame(ClickedPiece);
                                }
                            }
                        }
                    }
                    if (!TouchingNow) {
                        hasBeenUntouched = true;
                    }
                }

                if (chessGame.GameOver) {
                    // game over
                    System.out.println("Game Over");
                    if (Position.KingAtDanger != null) {
                        if (!Position.Is_white_turn) {
                            System.out.println("White won");
                        } else {
                            System.out.println("Black won");
                        }
                    } else {
                        System.out.println("draw");
                    }
                }
            } else {
                // game ended
                renderStartAnotherGame();
                if (detectInputReGame()) {
                    chessGame.ReverseAllMoves();
                    chessGame.ClearHistory();
                }
            }

            detectInputReverseReplayMoves();
            detectInputOpposeStartingColor();
        } else {
            hasBeenUntouched = false;
        }

        detectInputGameTransferScreens();

        wasPawnWin = chessGame.WinPawn;
    }

    private void renderOpposeStartingColor() {
        if (!Position.reverseMoveAvailable()) {
            batch.draw((Position.Is_white_turn ? WhiteTurn : BlackTurn), WORLD_WIDTH / 16, WORLD_HEIGHT / 32 * 25, WORLD_WIDTH / 4, WORLD_WIDTH / 8);
        }
    }

    private void renderGameTransferScreens() {
        batch.draw(BackgroundsChessBoardCells[3], 0, 0, WORLD_WIDTH, (float) (WORLD_HEIGHT / 8));
        batch.draw(SocialMedia, (float) (WORLD_WIDTH / 64 * 28), (float) (WORLD_HEIGHT / 200), (float) (WORLD_HEIGHT / 9), (float) (WORLD_HEIGHT / 9));
        batch.draw(ViewStateImg, (float) (WORLD_WIDTH / 16 * 12), (float) (WORLD_HEIGHT / 100), (float) (WORLD_HEIGHT / 10), (float) (WORLD_HEIGHT / 10));
        batch.draw(CameraImg, (float) (WORLD_WIDTH / 20), 0, (float) (WORLD_WIDTH / 4), (float) (WORLD_HEIGHT / 8));
    }

    private void detectInputOpposeStartingColor() {
        if (TouchedAlready && !TouchingNow) {
            if (yTouchPixel < WORLD_HEIGHT / 32 * 7 && yTouchPixel > WORLD_HEIGHT / 32 * 5) {
                if (xTouchPixel > WORLD_WIDTH / 16 && xTouchPixel < WORLD_WIDTH / 16 + WORLD_WIDTH / 4) {
                    if (!Position.reverseMoveAvailable()) {
                        Position.Is_white_turn = !Position.Is_white_turn;
                        chessGame.ClearHistory();

                        if (BoardNum != -1) {
                            Stack<Board> temp = new Stack<Board>();
                            Stack<Board> tempView = new Stack<Board>();

                            int boardNum = 0;
                            while (!previousBoards.empty() && BoardNum != boardNum) {
                                temp.push(previousBoards.pop());
                                tempView.push(previousBoardsForView.pop());
                                boardNum++;
                            }
                            if (!previousBoards.empty()) {
                                previousBoards.peek().IsWhiteTurn = !previousBoards.peek().IsWhiteTurn;
                                previousBoardsForView.peek().IsWhiteTurn = previousBoards.peek().IsWhiteTurn;
                            }
                            while (!temp.empty()) {
                                previousBoards.push(temp.pop());
                                previousBoardsForView.push(tempView.pop());
                            }
                        }
                        if (IsSpinning && Position.Is_white_turn ^ direction == 1) {
                            direction *= -1;
                        }
                    }
                }
            }
        }
    }

    private void detectInputGameTransferScreens() {
        if (TouchedAlready && !TouchingNow) {
            if (yTouchPixel > WORLD_HEIGHT / 8 * 7) {
                if (xTouchPixel > WORLD_WIDTH / 20 && xTouchPixel < WORLD_WIDTH / 4 + WORLD_WIDTH / 20) {
                    if (!IsPermissionGranted && cameraLauncher != null){
                        cameraLauncher.askAllPermissions();
                        IsPermissionGranted = cameraLauncher.isPermissionGranted();
                    }
                    if (IsPermissionGranted){
                        State = "Camera";
                    }
                } else if (xTouchPixel > (float) (WORLD_WIDTH / 16 * 12) && xTouchPixel < (float) (WORLD_WIDTH / 16 * 12) + (float) (WORLD_HEIGHT / 10)) {
                    State = "View";
                }
                else if (xTouchPixel > WORLD_WIDTH / 16 * 7 && xTouchPixel < WORLD_WIDTH / 16 * 7 + WORLD_HEIGHT / 9){
                    if (cameraLauncher != null){
                        String title = Position.Chess_Board.Title;
                        cameraLauncher.share((title.equals(Board.NonTitle) || title.equals(Board.TitleNotFit)) ? "" : title);
                    }
                }
            }
        }
    }

    private void detectInputReverseReplayMoves() {
        if (!TouchingNow && TouchedAlready) {
            // Reversing moves and replaying reversed moves:
            if (yTouchPixel < (float) (WORLD_HEIGHT / 64.0 * 50) + reverseMoveHeight && yTouchPixel > (float) (WORLD_HEIGHT / 64.0 * 50)) {
                if (xTouchPixel > (float) (WORLD_WIDTH / 2 - reverseMoveWidth - WORLD_WIDTH / 30.0) && xTouchPixel < (float) (WORLD_WIDTH / 2 - WORLD_WIDTH / 30.0)) {
                    if (!wasPawnWin) {
                        chessGame.ReverseMove();
                        if (IsSpinning && (Position.Is_white_turn ^ direction == 1)) {
                            direction *= -1;
                        }
                    }
                } else if (xTouchPixel > (float) (WORLD_WIDTH / 2 + WORLD_WIDTH / 30.0) && xTouchPixel < (float) (WORLD_WIDTH / 2 + reverseMoveWidth + WORLD_WIDTH / 30.0)) {
                    if ((!wasPawnWin) || TouchPos.equals(Position.getNextEndMoveCell())) {
                        chessGame.Replay_reversed_move();
                        if (IsSpinning && (Position.Is_white_turn ^ direction == 1)) {
                            direction *= -1;
                        }
                    }
                } else if (xTouchPixel > (float) (WORLD_WIDTH / 2 - 2 * (reverseMoveWidth + WORLD_WIDTH / 30.0) - reverseMoveWidth / 8.0) && xTouchPixel < (float) (WORLD_WIDTH / 2 - reverseMoveWidth - 2 * WORLD_WIDTH / 30.0)) {
                    chessGame.ReverseAllMoves();
                    if (IsSpinning && (Position.Is_white_turn ^ direction == 1)) {
                        direction *= -1;
                    }
                } else if (xTouchPixel > (float) (WORLD_WIDTH / 2 + 2 * WORLD_WIDTH / 30.0 + reverseMoveWidth) && xTouchPixel < (float) (WORLD_WIDTH / 2 + 2 * (WORLD_WIDTH / 30.0 + reverseMoveWidth) + reverseMoveWidth / 8.0)) {
                    if ((!wasPawnWin) || TouchPos.equals(Position.getNextEndMoveCell())) {
                        chessGame.ReplayAllReversedMoves();
                        if (IsSpinning && (Position.Is_white_turn ^ direction == 1)) {
                            direction *= -1;
                        }
                    }
                }
            }
        }
    }

    private void renderGameOptions() {
        // render option: game or riddle

        // if game:
        // render game partners: self-game, online-enemy/ies or computer-enemy/ies.
        // render game type1: Normal Chess, Fisher Chess, of Losing Chess.
        // render game type2: Swidden or not.
        // render wanted color: black or white.

        // render option of start the game.
        // else:
        // generate a method of riddle.
    }

    private void renderStartAnotherGame() {
        batch.draw(ReGameOption, (float) ((WORLD_WIDTH - reGameWidth) / 2), WORLD_HEIGHT - (float) ((WORLD_HEIGHT + reGameHeight) / 2 - (SquaredSizeOfBoard / 4.0 * 3)), reGameWidth, reGameHeight);
    }

    private void renderReverseMoveOption() {
        if (!Position.reverseMoveAvailable()) {
            return;
        }
        batch.draw(ReverseMoveOption, (float) (WORLD_WIDTH / 2 - reverseMoveWidth - WORLD_WIDTH / 30.0), WORLD_HEIGHT - (float) (WORLD_HEIGHT / 64.0 * 50) - reverseMoveHeight, (float) reverseMoveWidth, reverseMoveHeight);
        batch.draw(ReverseMoveOption, (float) (WORLD_WIDTH / 2 - 2 * (reverseMoveWidth + WORLD_WIDTH / 30.0) + reverseMoveWidth / 4.0), WORLD_HEIGHT - (float) (WORLD_HEIGHT / 64.0 * 50) - reverseMoveHeight, (float) (reverseMoveWidth / 4.0 * 3), reverseMoveHeight);
        batch.draw(ReverseMoveOption, (float) (WORLD_WIDTH / 2 - 2 * (reverseMoveWidth + WORLD_WIDTH / 30.0) - reverseMoveWidth / 8.0), WORLD_HEIGHT - (float) (WORLD_HEIGHT / 64.0 * 50) - reverseMoveHeight, (float) (reverseMoveWidth / 4.0 * 3), reverseMoveHeight);
    }

    private void renderReplayReversedMoveOption() {
        if (!Position.replayReversedMoveAvailable()) {
            return;
        }
        if (chessGame.WinPawn && !TouchPos.equals(Position.getNextEndMoveCell())) {
            return;
        }
        batch.draw(ReplayReversedMoveOption, (float) (WORLD_WIDTH / 2 + WORLD_WIDTH / 30.0), WORLD_HEIGHT - (float) (WORLD_HEIGHT / 64.0 * 50) - reverseMoveHeight, (float) reverseMoveWidth, reverseMoveHeight);
        batch.draw(ReplayReversedMoveOption, (float) (WORLD_WIDTH / 2 + 2 * (WORLD_WIDTH / 30.0) + reverseMoveWidth), WORLD_HEIGHT - (float) (WORLD_HEIGHT / 64.0 * 50) - reverseMoveHeight, (float) (reverseMoveWidth / 4.0 * 3), reverseMoveHeight);
        batch.draw(ReplayReversedMoveOption, (float) (WORLD_WIDTH / 2 + 2 * (WORLD_WIDTH / 30.0) + reverseMoveWidth + reverseMoveWidth / 8.0 * 3), WORLD_HEIGHT - (float) (WORLD_HEIGHT / 64.0 * 50) - reverseMoveHeight, (float) (reverseMoveWidth / 4.0 * 3), reverseMoveHeight);
    }

    private void renderTitle(float x, float y, float height) {
        Stack<DetectionThread> tempThreads = new Stack<DetectionThread>();
        Stack<Board> tempBoards = new Stack<Board>();
        Stack<Board> tempBoardsView = new Stack<Board>();

        String title = "";
        int boardNum = 0;
        while (!detectionThreads.empty() && BoardNum != boardNum) {
            tempThreads.push(detectionThreads.pop());
            tempBoards.push(previousBoards.pop());
            tempBoardsView.push(previousBoardsForView.pop());
            boardNum++;
        }
        if (!previousBoards.empty()){
            previousBoards.peek().Title = previousBoardsForView.peek().Title;
            Position.Chess_Board.Title = previousBoards.peek().Title;
            title = detectionThreads.peek().getTitle();
        }
        while (!tempThreads.empty()) {
            detectionThreads.push(tempThreads.pop());
            previousBoards.push(tempBoards.pop());
            previousBoardsForView.push(tempBoardsView.pop());
        }
        CurrentTitlesList = new String[ChoosingTitle ? (TitlesList.length + (Position.Chess_Board.Title.equals(Board.NonTitle) ? 1 : 0)) : (Position.Chess_Board.Title.equals(Board.TitleCheck) ? 2 : 1)];
        CurrentTitlesList[0] = Position.Chess_Board.Title;
        if (Position.Chess_Board.Title.equals(Board.TitleCheck)){
            CurrentTitlesList[1] = title;
        }
        else{
            boolean hasSeenTitle = false;
            for (int i = 1; i < CurrentTitlesList.length; i++){
                if ((CurrentTitlesList[0]).equals(TitlesList[i - 1])){
                    hasSeenTitle = true;
                }
                CurrentTitlesList[i] = TitlesList[i - (hasSeenTitle ? 0 : 1)];
            }
        }

        float[] totalWidth = new float[CurrentTitlesList.length];
        float wordsSpace = 20;
        float lettersSpace = 6;
        float maxWidth = 0;
        for (int i = 0; i < CurrentTitlesList.length; i++){
            totalWidth[i] = getTotalTextWidth(CurrentTitlesList[i], lettersSpace, wordsSpace, height);
            if (totalWidth[i] > maxWidth){
                maxWidth = totalWidth[i];
            }
        }

        for (int i = 0; i < CurrentTitlesList.length; i++){
            batch.draw(BackgroundsChessBoardCells[i > 0 ? (i + 2) % 2 + 11 : (Position.Chess_Board.Title.equals(Board.TitleNotFit) ? 4 : 13)],
                    x - (maxWidth / 2) - height / 4,
                    y - (height * (1 + 2*i)),
                    maxWidth + height / 2, height * 2);
            float currentX = x - (totalWidth[i] / 2);
            String txt = CurrentTitlesList[i];
            for (int index = txt.length() - 1; index >= 0; index--){
                char chr = txt.charAt(index);
                int charIndex = ABC.indexOf(chr);
                if (charIndex != -1){
                    float letterWidth = ((float) Letters_Images.Sizes[charIndex].X) / 48 * height;
                    float letterHeight = ((float) Letters_Images.Sizes[charIndex].Y) / 48 * height;

                    batch.draw(Letters_Images.BlackLetters[charIndex],
                            currentX, y - (height * (1 + 4*i)) / 2 - (charIndex != 11 ? letterHeight - height : 0), letterWidth, letterHeight);
                    currentX += letterWidth + (lettersSpace / 48 * height);
                }
                else {
                    currentX += wordsSpace / 48 * height;
                }
            }
        }
    }

    private float getTotalTextWidth(String txt, float lettersSpace, float wordsSpace, float height){
        float totalWidth = 0;
        for (int i = 0; i < txt.length(); i++){
            char chr = txt.charAt(i);
            int charIndex = ABC.indexOf(chr);
            if (charIndex != -1){
                totalWidth += ((float) Letters_Images.Sizes[charIndex].X + (i < txt.length() - 1 ? lettersSpace : 0)) / 48 * height;
            }
            else {
                totalWidth += wordsSpace / 48 * height;
            }
        }
        return totalWidth;
    }

    private void detectInputChoosingTitle(float x, float y, float height) {
        if (TouchedAlready && !TouchingNow) {
            float wordsSpace = 20;
            float lettersSpace = 6;
            float totalWidth = getTotalTextWidth(Position.Chess_Board.Title, lettersSpace, wordsSpace, height);
            if (xTouchPixel > x - (totalWidth / 2) - height / 4 &&
                    xTouchPixel < x + (totalWidth / 2) + height / 4 &&
                    yTouchPixel > (WORLD_HEIGHT - y) - height &&
                    yTouchPixel < (WORLD_HEIGHT - y) + height * (1 + 2*(CurrentTitlesList.length - 1))) {
                ChoosingTitle = !ChoosingTitle;

                int titleIndex = (int) ((yTouchPixel - (WORLD_HEIGHT - y) + height) / height / 2);
                String title = CurrentTitlesList[titleIndex];

                Stack<DetectionThread> tempThreads = new Stack<DetectionThread>();
                Stack<Board> tempBoards = new Stack<Board>();
                Stack<Board> tempBoardsView = new Stack<Board>();

                int boardNum = 0;
                while (!detectionThreads.empty() && BoardNum != boardNum) {
                    tempThreads.push(detectionThreads.pop());
                    tempBoards.push(previousBoards.pop());
                    tempBoardsView.push(previousBoardsForView.pop());
                    boardNum++;
                }
                if (!detectionThreads.empty()){
                    DetectionThread thread = detectionThreads.peek();
                    if (thread.isAlive()){
                        ChoosingTitle = !ChoosingTitle;
                    }
                    else if (titleIndex > 0){
                        System.out.println("Size = " + previousBoards.size());
                        thread = new DetectionThread(title, previousBoardsForView.peek());
                        detectionThreads.pop();
                        detectionThreads.push(thread);
                    }
                }
                while (!tempThreads.empty()) {
                    detectionThreads.push(tempThreads.pop());
                    previousBoards.push(tempBoards.pop());
                    previousBoardsForView.push(tempBoardsView.pop());
                }
            }
            else if (ChoosingTitle){
                ChoosingTitle = false;
            }
        }
    }

    private boolean detectInputReGame() {
        if (!TouchingNow && TouchedAlready) {
            return xTouchPixel >= (float) ((WORLD_WIDTH - reGameWidth) / 2) &&
                    xTouchPixel < (float) ((WORLD_WIDTH - reGameWidth) / 2) + reGameWidth &&
                    yTouchPixel < (float) ((WORLD_HEIGHT + reGameHeight) / 2 - (SquaredSizeOfBoard / 4.0 * 3)) &&
                    yTouchPixel > (float) ((WORLD_HEIGHT + reGameHeight) / 2 - (SquaredSizeOfBoard / 4.0 * 3)) - reGameHeight;
        }
        return false;
    }

    private String detectInputNewPawnType() {
        float cellSize = SquaredSizeOfBoard * 5 / 3 / 8;
        float left_x = WORLD_WIDTH / 2 - cellSize * 2;
        float down_y = WORLD_HEIGHT / 2;
        if (!TouchingNow && TouchedAlready) {
            if (xTouchPixel > left_x && xTouchPixel < left_x + cellSize * 4 &&
                    yTouchPixel < down_y && yTouchPixel > down_y - cellSize) {

                if (xTouchPixel < left_x + cellSize) {
                    return "Queen";
                } else {
                    if (xTouchPixel < left_x + cellSize * 2) {
                        return "Knight";
                    } else {
                        if (xTouchPixel < left_x + cellSize * 3) {
                            return "Bishop";
                        } else {
                            return "Rook";
                        }
                    }
                }
            } else {
                // Spinning Board Option:
                if (xTouchPixel > (float) (WORLD_WIDTH / 2 - (float) (WORLD_WIDTH / 4 / 2)) && xTouchPixel < (float) (WORLD_WIDTH / 2 - (float) (WORLD_WIDTH / 4 / 2)) + (float) (WORLD_WIDTH / 4) && yTouchPixel < WORLD_HEIGHT - (float) (WORLD_HEIGHT / 16 * 13) && yTouchPixel > WORLD_HEIGHT - ((float) (WORLD_HEIGHT / 16 * 13) + (float) (WORLD_WIDTH / 4) / 384 * 230)) {
                    IsSpinning = !IsSpinning;
                    if (IsSpinning && (Position.Is_white_turn ^ direction == -1)) {
                        direction *= -1;
                    }
                }
            }
        }


        return "";
    }

    private void renderChoosingPawnNewType() {
        float cellSize = SquaredSizeOfBoard * 5 / 3 / 8;
        float frameSize = WORLD_WIDTH / 60;
        float left_x = WORLD_WIDTH / 2 - cellSize * 2;
        float down_y = WORLD_HEIGHT / 2;
        if (chessGame.WinPawn) {
            batch.draw(BlackBackgroundForPawnWinning, left_x - frameSize, down_y - frameSize, 4 * cellSize + 2 * frameSize, cellSize + 2 * frameSize);
            batch.draw(GrayBackgroundForPawnWinning, left_x, down_y, 4 * cellSize, cellSize);

            String color = !Position.Is_white_turn ? "white" : "black";
            renderPiecesIntoBoardAtScreen("Queen", color, left_x, down_y, cellSize);
            renderPiecesIntoBoardAtScreen("Knight", color, left_x + cellSize * 1, down_y, cellSize);
            renderPiecesIntoBoardAtScreen("Bishop", color, left_x + cellSize * 2, down_y, cellSize);
            renderPiecesIntoBoardAtScreen("Rook", color, left_x + cellSize * 3, down_y, cellSize);
        }
    }

    private void renderPiecesIntoBoardAtScreen(String type, String color, float x, float y, float cellSize) {
        Point pieceSize;
        Texture whiteImg;
        Texture blackImg;
        switch (type) {
            case "King":
                pieceSize = new Point(piecesImages.KingSize);
                whiteImg = piecesImages.WhiteKing;
                blackImg = piecesImages.BlackKing;
                break;

            case "Queen":
                pieceSize = new Point(piecesImages.QueenSize);
                whiteImg = piecesImages.WhiteQueen;
                blackImg = piecesImages.BlackQueen;
                break;

            case "Rook":
                pieceSize = new Point(piecesImages.RookSize);
                whiteImg = piecesImages.WhiteRook;
                blackImg = piecesImages.BlackRook;
                break;

            case "Bishop":
                pieceSize = new Point(piecesImages.BishopSize);
                whiteImg = piecesImages.WhiteBishop;
                blackImg = piecesImages.BlackBishop;
                break;

            case "Knight":
                pieceSize = new Point(piecesImages.KnightSize);
                whiteImg = piecesImages.WhiteKnight;
                blackImg = piecesImages.BlackKnight;
                break;

            case "Pawn":
                pieceSize = new Point(piecesImages.PawnSize);
                whiteImg = piecesImages.WhitePawn;
                blackImg = piecesImages.BlackPawn;
                break;

            default:
                pieceSize = null;
                whiteImg = null;
                blackImg = null;
                break;
        }
        if (pieceSize != null) {
            pieceSize.X *= cellSize / ((0.9 * WORLD_WIDTH) / 8.0);
            pieceSize.Y *= cellSize / ((0.9 * WORLD_WIDTH) / 8.0);
            int x_batch = (int) (x + ((cellSize - pieceSize.X) / 2));
            int y_batch = (int) (y + ((cellSize - pieceSize.Y) / 2));
            Texture img = color.equals("white") ? whiteImg : blackImg;
            batch.draw(img, x_batch, y_batch, pieceSize.X, pieceSize.Y);
        }
    }

    private float renderViewStateScreen() {
        // render boards maps
        int boardNum = 0;
        boolean isBoardNumMatch = false;
        Stack<Board> temp = new Stack<Board>();
        while (!previousBoardsForView.empty()) {
            temp.push(previousBoardsForView.pop());
            float bottomX = (boardNum % 2 == 0 ? BoardViewLeftLimit : BoardViewRightLimit);
            float bottomY = ViewOffset + BoardViewDownLimit - (int) (boardNum / 2) * (SquaredSizeOfBoard / 2 + (WORLD_WIDTH - SquaredSizeOfBoard) / 3);
            if (BoardNum == boardNum) {
                batch.draw(BackgroundsChessBoardCells[4], bottomX - (WORLD_WIDTH - SquaredSizeOfBoard) / 6, bottomY - (WORLD_WIDTH - SquaredSizeOfBoard) / 6, SquaredSizeOfBoard / 2 + (WORLD_WIDTH - SquaredSizeOfBoard) / 3, SquaredSizeOfBoard / 2 + (WORLD_WIDTH - SquaredSizeOfBoard) / 3);
                isBoardNumMatch = true;
            }
            renderChessBoardsView(temp.peek(), bottomX, bottomY, SquaredSizeOfBoard / 2);
            boardNum++;
        }
        while (!temp.empty()) {
            previousBoardsForView.push(temp.pop());
        }
        batch.draw(BackgroundsChessBoardCells[3], 0, 0, WORLD_WIDTH, (float) (WORLD_HEIGHT / 8));
        if (isBoardNumMatch) {
            batch.draw(TrashCan, (float) (WORLD_WIDTH / 4 * 3), 0, (float) (WORLD_WIDTH / 4), (float) (WORLD_HEIGHT / 8));
        }
        batch.draw(CameraImg, (float) (WORLD_WIDTH / 20), 0, (float) (WORLD_WIDTH / 4), (float) (WORLD_HEIGHT / 8));
        return (float) ((boardNum + boardNum % 2) / 2 * (SquaredSizeOfBoard / 2 + (WORLD_WIDTH - SquaredSizeOfBoard) / 3) + (WORLD_WIDTH - SquaredSizeOfBoard) / 3) + (float) (WORLD_HEIGHT / 8);
    }

    private void renderPiecesIntoScreen(String type, String color, int x, int y) {
        if (color.equals("white")) {
            if (type.equals("King")) {
                batch.draw(piecesImages.WhiteKing, x - (int) (piecesImages.KingSize.X / 2), WORLD_HEIGHT - y - (float) (piecesImages.KingSize.Y / 2), piecesImages.KingSize.X, piecesImages.KingSize.Y);
            } else {
                if (type.equals("Queen")) {
                    batch.draw(piecesImages.WhiteQueen, x - (int) (piecesImages.QueenSize.X / 2), WORLD_HEIGHT - y - (float) (piecesImages.QueenSize.Y / 2), piecesImages.QueenSize.X, piecesImages.QueenSize.Y);
                } else {
                    if (type.equals("Rook")) {
                        batch.draw(piecesImages.WhiteRook, x - (int) (piecesImages.RookSize.X / 2), WORLD_HEIGHT - y - (float) (piecesImages.RookSize.Y / 2), piecesImages.RookSize.X, piecesImages.RookSize.Y);
                    } else {
                        if (type.equals("Bishop")) {
                            batch.draw(piecesImages.WhiteBishop, x - (int) (piecesImages.BishopSize.X / 2), WORLD_HEIGHT - y - (float) (piecesImages.BishopSize.Y / 2), piecesImages.BishopSize.X, piecesImages.BishopSize.Y);
                        } else {
                            if (type.equals("Knight")) {
                                batch.draw(piecesImages.WhiteKnight, x - (int) (piecesImages.KnightSize.X / 2), WORLD_HEIGHT - y - (float) (piecesImages.KnightSize.Y / 2), piecesImages.KnightSize.X, piecesImages.KnightSize.Y);
                            } else {
                                if (type.equals("Pawn")) {
                                    batch.draw(piecesImages.WhitePawn, x - (int) (piecesImages.PawnSize.X / 2), WORLD_HEIGHT - y - (float) (piecesImages.PawnSize.Y / 2), piecesImages.PawnSize.X, piecesImages.PawnSize.Y);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (type == "King") {
                batch.draw(piecesImages.BlackKing, x - (int) (piecesImages.KingSize.X / 2), WORLD_HEIGHT - y - (float) (piecesImages.KingSize.Y / 2), piecesImages.KingSize.X, piecesImages.KingSize.Y);
            } else {
                if (type == "Queen") {
                    batch.draw(piecesImages.BlackQueen, x - (int) (piecesImages.QueenSize.X / 2), WORLD_HEIGHT - y - (float) (piecesImages.QueenSize.Y / 2), piecesImages.QueenSize.X, piecesImages.QueenSize.Y);
                } else {
                    if (type == "Rook") {
                        batch.draw(piecesImages.BlackRook, x - (int) (piecesImages.RookSize.X / 2), WORLD_HEIGHT - y - (float) (piecesImages.RookSize.Y / 2), piecesImages.RookSize.X, piecesImages.RookSize.Y);
                    } else {
                        if (type == "Bishop") {
                            batch.draw(piecesImages.BlackBishop, x - (int) (piecesImages.BishopSize.X / 2), WORLD_HEIGHT - y - (float) (piecesImages.BishopSize.Y / 2), piecesImages.BishopSize.X, piecesImages.BishopSize.Y);
                        } else {
                            if (type == "Knight") {
                                batch.draw(piecesImages.BlackKnight, x - (int) (piecesImages.KnightSize.X / 2), WORLD_HEIGHT - y - (float) (piecesImages.KnightSize.Y / 2), piecesImages.KnightSize.X, piecesImages.KnightSize.Y);
                            } else {
                                if (type == "Pawn") {
                                    batch.draw(piecesImages.BlackPawn, x - (int) (piecesImages.PawnSize.X / 2), WORLD_HEIGHT - y - (float) (piecesImages.PawnSize.Y / 2), piecesImages.PawnSize.X, piecesImages.PawnSize.Y);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void renderSpinningScreenOption() {
        if (IsSpinning) {
            batch.draw(SpinningOptionImages[1], WORLD_WIDTH / 8 * 3, WORLD_HEIGHT / 32 * 25, (float) (WORLD_WIDTH / 4), WORLD_WIDTH / 8);
        } else {
            batch.draw(SpinningOptionImages[0], WORLD_WIDTH / 8 * 3, WORLD_HEIGHT / 32 * 25, (float) (WORLD_WIDTH / 4), WORLD_WIDTH / 8);
        }
    }

    private void detectInputSpinningScreenOption() {
        if (TouchingNow && !TouchedAlready) {
            // Spinning Board Option:
            if (xTouchPixel > (float) (WORLD_WIDTH / 2 - (float) (WORLD_WIDTH / 4 / 2)) &&
                    xTouchPixel < (float) (WORLD_WIDTH / 2 - (float) (WORLD_WIDTH / 4 / 2)) + (float) (WORLD_WIDTH / 4) &&
                    yTouchPixel < WORLD_HEIGHT - (WORLD_HEIGHT / 32 * 25) &&
                    yTouchPixel > WORLD_HEIGHT - (WORLD_HEIGHT / 32 * 25 + WORLD_WIDTH / 8)) {
                IsSpinning = !IsSpinning;
                if (IsSpinning && (Position.Is_white_turn ^ direction == 1)) {
                    direction *= -1;
                }
            }
        }
    }

    private void renderScrollingBackground(float deltaTime) {
        backgroundOffsets[0] += deltaTime * backgroundMaxScrollingSpeed / 1;
        backgroundOffsets[1] += deltaTime * backgroundMaxScrollingSpeed / 2;
        backgroundOffsets[2] += deltaTime * backgroundMaxScrollingSpeed / 4;
        backgroundOffsets[3] += deltaTime * backgroundMaxScrollingSpeed / 8;

        for (int layer = 0; layer < backgroundOffsets.length; layer++) {
            backgroundOffsets[layer] %= WORLD_HEIGHT;
            batch.draw(ScrollingBackgrounds[layer], 0, 0 - backgroundOffsets[layer], WORLD_WIDTH, WORLD_HEIGHT);
            batch.draw(ScrollingBackgrounds[layer], 0, 0 - backgroundOffsets[layer] + WORLD_HEIGHT, WORLD_WIDTH, WORLD_HEIGHT);
        }
    }

    private void StartNewGame(boolean isFisher) {
        chessGame = new ChessGame(BoardSize, isFisher);
        ClickedPiece = null;
    }

    private void EnterGame(Board board) {
        Board newBoard = new Board(board);
        chessGame = new ChessGame(newBoard);
        ClickedPiece = null;
    }

    private void detectInput(float deltaTime) {
        // get the screen position of the touch
        if (TouchingNow) {
            if (!TouchedAlready) {
                touchedColor = Position.Is_white_turn;
                currentTime = 0;
                IsActivePiece = false;

                // convert world to position
                if (xTouchPixel > BoardLeftLimit && xTouchPixel < BoardRightLimit && yTouchPixel > BoardDownLimit && yTouchPixel < BoardUpLimit) {
                    TouchPos = new Point(((int) ((1 - direction) / 2) * 7 + direction * ((int) Math.floor((xTouchPixel - BoardLeftLimit) / SquaredSizeOfBoard * BoardSize))), ((int) ((1 - direction) / 2) * 7 + direction * ((BoardSize - 1) - (int) Math.floor(((yTouchPixel) - BoardDownLimit) / SquaredSizeOfBoard * BoardSize))));

                    if (ChessGame.CountClicksOnSameCell_AfterCell(TouchPos) % 2 == 1) {
                        chessGame.ClickDuringGame(TouchPos);
                        IsSecondTouch = false;
                    } else {
                        IsSecondTouch = true;
                    }

                    ChessGame.BoardCellsGrid[TouchPos.X][TouchPos.Y].ActivePiece = true;
                    ClickedPiece = new Point(TouchPos.X, TouchPos.Y);
                } else {
                    if (ClickedPiece != null && ChessGame.Click_counted_on_same_cell % 2 == 1) {
                        chessGame.ClickDuringGame(ClickedPiece);
                    }
                    ClickedPiece = null;
                }
            } else {
                currentTime += deltaTime;
                if (currentTime > 0.1 && ClickedPiece != null) {
                    IsActivePiece = true;
                    if (ChessGame.Click_counted_on_same_cell % 2 == 0) {
                        chessGame.ClickDuringGame(ClickedPiece);
                    }
                }
            }
        } else {
            if (currentTime < 0.1 && ClickedPiece != null && TouchedAlready) {
                if (IsSecondTouch) {
                    chessGame.ClickDuringGame(TouchPos);
                }
            }
            if (TouchedAlready && currentTime > 0.1 && ClickedPiece != null && touchedColor == Position.Is_white_turn) {
                if (xTouchPixel > BoardLeftLimit && xTouchPixel < BoardRightLimit && yTouchPixel > BoardDownLimit && yTouchPixel < BoardUpLimit) {
                    TouchPos = new Point(((int) ((1 - direction) / 2) * 7 + direction * ((int) Math.floor((xTouchPixel - BoardLeftLimit) / SquaredSizeOfBoard * BoardSize))), ((int) ((1 - direction) / 2) * 7 + direction * ((BoardSize - 1) - (int) Math.floor(((yTouchPixel) - BoardDownLimit) / SquaredSizeOfBoard * BoardSize))));
                    if (ChessGame.BoardCellsGrid[TouchPos.X][TouchPos.Y].BackgroundColor.equals("green")) {
                        chessGame.ClickDuringGame(TouchPos);
                    } else {
                        chessGame.ClickDuringGame(ClickedPiece);
                    }
                } else {
                    chessGame.ClickDuringGame(ClickedPiece);
                }
            }
            if (ClickedPiece != null) {
                ChessGame.BoardCellsGrid[ClickedPiece.X][ClickedPiece.Y].ActivePiece = false;
            }
            IsActivePiece = false;
        }
    }

    private void renderChessBoard(float leftLimit, float downLimit, float BoardPixelsSize) {
        chessGame.KingInDangerToRedBackground();
        float xPiece;
        float yPiece;

        for (int i = 0; i < BoardSize; i++) {
            for (int j = 0; j < BoardSize; j++) {
                int imgNum;
                switch (ChessGame.BoardCellsGrid[i][j].BackgroundColor) {
                    case "green":
                        // paint the cell background to green
                        if (i % 2 == 0 ^ j % 2 == 0) {
                            imgNum = 2;
                        } else {
                            imgNum = 3;
                        }
                        break;

                    case "red":
                        // paint the cell background to red
                        imgNum = 4;
                        break;

                    case "brown":
                        // paint the cell background to brown
                        imgNum = 10;
                        break;

                    case "purple":
                        // paint the cell background to brown
                        if (i % 2 == 0 ^ j % 2 == 0) {
                            imgNum = 7;
                        } else {
                            imgNum = 8;
                        }
                        break;

                    default:
                        if (i % 2 == 0 ^ j % 2 == 0) {
                            imgNum = 0;
                        } else {
                            imgNum = 1;
                        }
                        break;
                }
                batch.draw(BackgroundsChessBoardCells[imgNum], leftLimit + ((float) ((1 - direction) / 2) * 7 + direction * i) * BoardPixelsSize / BoardSize, downLimit + ((float) ((1 - direction) / 2) * 7 + direction * j) * BoardPixelsSize / BoardSize, (float) (BoardPixelsSize / BoardSize), (float) (BoardPixelsSize / BoardSize));

                if (!ChessGame.BoardCellsGrid[i][j].PieceType.equals("")) {
                    if (!ChessGame.BoardCellsGrid[i][j].ActivePiece) {
                        float pieceSize = BoardPixelsSize / BoardSize;
                        xPiece = ((((float) ((1 - direction) / 2) * 7 + direction * i) * pieceSize) + leftLimit);
                        yPiece = ((((float) ((1 - direction) / 2) * 7 + direction * j) * pieceSize) + downLimit);
                        renderPiecesIntoBoardAtScreen(ChessGame.BoardCellsGrid[i][j].PieceType, ChessGame.BoardCellsGrid[i][j].PieceColor, xPiece, yPiece, pieceSize);
                    }
                }
            }
        }
        if (ClickedPiece != null) {
            if (IsActivePiece && (ChessGame.BoardCellsGrid[ClickedPiece.X][ClickedPiece.Y].PieceColor.equals("black") ^ Position.Is_white_turn)) {
                xPiece = (int) xTouchPixel; //// x touch
                yPiece = (int) yTouchPixel; //// y touch
                renderPiecesIntoScreen(ChessGame.BoardCellsGrid[ClickedPiece.X][ClickedPiece.Y].PieceType, ChessGame.BoardCellsGrid[ClickedPiece.X][ClickedPiece.Y].PieceColor, (int) xPiece, (int) yPiece);
            } else {
                xPiece = ((((float) ((1 - direction) / 2) * 7 + direction * ClickedPiece.X) * BoardPixelsSize / BoardSize) + leftLimit);
                yPiece = ((((float) ((1 - direction) / 2) * 7 + direction * ClickedPiece.Y) * BoardPixelsSize / BoardSize) + downLimit);
                renderPiecesIntoBoardAtScreen(ChessGame.BoardCellsGrid[ClickedPiece.X][ClickedPiece.Y].PieceType, ChessGame.BoardCellsGrid[ClickedPiece.X][ClickedPiece.Y].PieceColor, xPiece, yPiece, (BoardPixelsSize / BoardSize));
            }
        }
    }

    private void renderChessBoardsView(Board board, float leftLimit, float downLimit, float BoardPixelsSize) {
        float xPiece;
        float yPiece;

        for (int i = 0; i < BoardSize; i++) {
            for (int j = 0; j < BoardSize; j++) {
                batch.draw(BackgroundsChessBoardCells[(i % 2 == 0 ^ j % 2 == 0) ? 0 : 1], leftLimit + ((float) ((1 - direction) / 2) * 7 + direction * i) * BoardPixelsSize / BoardSize, downLimit + ((float) ((1 - direction) / 2) * 7 + direction * j) * BoardPixelsSize / BoardSize, (float) (BoardPixelsSize / BoardSize), (float) (BoardPixelsSize / BoardSize));

                if (!board.The_Grid[i][j].Type.equals("")) {
                    xPiece = ((((float) ((1 - direction) / 2) * 7 + direction * i) * BoardPixelsSize / BoardSize) + leftLimit);
                    yPiece = ((((float) ((1 - direction) / 2) * 7 + direction * j) * BoardPixelsSize / BoardSize) + downLimit);
                    renderPiecesIntoBoardAtScreen(board.The_Grid[i][j].Type, board.The_Grid[i][j].Color_piece, xPiece, yPiece, (BoardPixelsSize / BoardSize));
                }
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void show() {

    }

    @Override
    public void dispose() {

    }
}
