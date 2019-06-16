package Model;

import Client.*;
import IO.MyDecompressorInputStream;
import Server.*;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.mazeGenerators.Position;
import algorithms.search.*;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class MyModel extends Observable implements IModel {

    private ExecutorService threadPool = Executors.newCachedThreadPool();/////??????
    Server mazeGeneratingServer;
    Server solveSearchProblemServer;
    Maze maze;
    ArrayList<AState> mazeSolutionSteps;
    private int direction;
    private boolean isCorrectMove;


    public MyModel() {
        //Raise the servers
        mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());
        solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
    }

    public void startServers() {
        mazeGeneratingServer.start();
        solveSearchProblemServer.start();
    }

    public void stopServers() {
        mazeGeneratingServer.stop();
        solveSearchProblemServer.stop();
    }

    private int characterPositionRow=1;
    private int characterPositionColumn=1;

    public void setCharacterPositionRow(int characterPositionRow) {

        this.characterPositionRow = characterPositionRow;
        setChanged();
        notifyObservers();
    }

    public void setCharacterPositionColumn(int characterPositionColumn) {
        this.characterPositionColumn = characterPositionColumn;
        setChanged();
        notifyObservers();
    }

    @Override
    public void generateMaze(int row, int col) {
        //Generate maze
        CommunicateWithServer_MazeGenerating(row, col);

        threadPool.execute(() -> {
            //generateRandomMaze(width,height);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        });
        setChanged();
        notifyObservers();
    }

    @Override
    public ArrayList<AState> solveMaze() {
        CommunicateWithServer_SolveSearchProblem();
        return mazeSolutionSteps;
    }

    @Override
    public Maze getMaze() {
        return maze;
    }

    @Override
    public void setMaze(Maze maze) {

        this.maze = maze;
    }

    public int getDirection() {
        return direction;
    }

    @Override
    public void moveCharacter(KeyCode movement) {
        isCorrectMove = false;
        Position currPos = new Position(characterPositionRow, characterPositionColumn);
        AState state = new MazeState(1, null, currPos);
        SearchableMaze searchableMaze = new SearchableMaze(this.maze);
        ArrayList<AState> possibleS = searchableMaze.getAllPossibleStates(state);
        switch (movement) {
            case NUMPAD8:
            case DIGIT8:
            case UP:
                if (possibleS.contains(new MazeState(1, null, new Position(characterPositionRow - 1, characterPositionColumn)))) {
                    characterPositionRow--;
                    direction = 0;
                    isCorrectMove=true;
                }
                break;
            case NUMPAD2:
            case DIGIT2:
            case DOWN:
                if (possibleS.contains(new MazeState(1, null, new Position(characterPositionRow + 1, characterPositionColumn)))) {
                    characterPositionRow++;
                    direction = 180;
                    isCorrectMove=true;
                }
                break;
            case NUMPAD6:
            case DIGIT6:
            case RIGHT:
                if (possibleS.contains(new MazeState(1, null, new Position(characterPositionRow, characterPositionColumn + 1)))) {
                    characterPositionColumn++;
                    direction = 90;
                    isCorrectMove=true;
                }
                break;
            case NUMPAD4:
            case DIGIT4:
            case LEFT:
                if (possibleS.contains(new MazeState(1, null, new Position(characterPositionRow, characterPositionColumn - 1)))) {
                    characterPositionColumn--;
                    direction = -90;
                    isCorrectMove=true;
                }
                break;
            case NUMPAD7://8&4
            case DIGIT7:
                if (possibleS.contains(new MazeState(1, null, new Position(characterPositionRow - 1, characterPositionColumn - 1)))) {
                    characterPositionRow--;
                    characterPositionColumn--;
                    direction = -45;
                    isCorrectMove=true;
                }
                break;
            case NUMPAD1://4&2
            case DIGIT1:
                if (possibleS.contains(new MazeState(1, null, new Position(characterPositionRow + 1, characterPositionColumn - 1)))) {
                    characterPositionRow++;
                    characterPositionColumn--;
                    direction = -135;
                    isCorrectMove=true;

                }
                break;
            case NUMPAD3://2&6
            case DIGIT3:
                if (possibleS.contains(new MazeState(1, null, new Position(characterPositionRow + 1, characterPositionColumn + 1)))) {
                    characterPositionRow++;
                    characterPositionColumn++;
                    direction = 135;
                    isCorrectMove=true;

                }
                break;
            case NUMPAD9://6&8
            case DIGIT9:
                if (possibleS.contains(new MazeState(1, null, new Position(characterPositionRow - 1, characterPositionColumn + 1)))) {
                    characterPositionRow--;
                    characterPositionColumn++;
                    direction = 45;
                    isCorrectMove=true;

                }
                break;

        }
        //maze.updateMazeNum(characterPositionRow,characterPositionColumn,-1);

        ///not need to changed if not move!!
        setChanged();
        notifyObservers();

    }

    public boolean isCorrectMove() {
        return isCorrectMove;
    }

    public void setCorrectMove(boolean correctMove) {
        isCorrectMove = correctMove;
    }

    @Override
    public int getCharacterPositionRow() {
        return characterPositionRow;
    }

    @Override
    public int getCharacterPositionColumn() {
        return characterPositionColumn;
    }


    private void CommunicateWithServer_MazeGenerating(int row, int col) {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{row, col};
                        toServer.writeObject(mazeDimensions); //send maze dimensions to server
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[1000000000 /*CHANGE SIZE ACCORDING TO YOU MAZE SIZE*/]; //allocating byte[] for the decompressed maze -
                        is.read(decompressedMaze); //Fill decompressedMaze with bytes
                        Maze m = new Maze(decompressedMaze);
                        characterPositionColumn = m.getStartPosition().getColumnIndex();//
                        characterPositionRow = m.getStartPosition().getRowIndex();//
                        maze = m;


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void CommunicateWithServer_SolveSearchProblem() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        toServer.writeObject(maze); //send maze to server
                        toServer.flush();
                        Solution mazeSolution = (Solution) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        //Print Maze Solution retrieved from the server
                        mazeSolutionSteps = mazeSolution.getSolutionPath();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
}
