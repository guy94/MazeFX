package Model;

import Client.Client;
import Client.IClientStrategy;
import IO.MyCompressorOutputStream;
import IO.MyDecompressorInputStream;
import Server.Server;
import Server.ServerStrategyGenerateMaze;
import Server.ServerStrategySolveSearchProblem;
import algorithms.mazeGenerators.AMazeGenerator;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.MyMazeGenerator;
import algorithms.mazeGenerators.Position;
import algorithms.search.ASearchingAlgorithm;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogBuilder;


import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.FileHandler;
import java.util.logging.Handler;

public class MyModel extends Observable implements IModel
{
    private Server mazeGeneratingServer;
    private Server solveSearchProblemServer;
    private Client client;


    private AMazeGenerator mazeGenerator;
    private ASearchingAlgorithm searchingAlgorithm;

    private static final Logger logger = LogManager.getLogger(MyModel.class);

    private Solution mazeSolution;
    private Maze maze;
    private ArrayList<Integer> mazeToSolve;
    private MyCompressorOutputStream myCompressorOutputStream;
    private MyDecompressorInputStream myDecompressorInputStream;
    StringProperty mazeGenProp;
    StringProperty solveAlgoProp;


    public MyModel(){

        this.mazeToSolve = new ArrayList<Integer>();
        this.mazeGenProp = new SimpleStringProperty();
        this.solveAlgoProp = new SimpleStringProperty();

        this.mazeGeneratingServer = new Server(5400, 1000, new ServerStrategyGenerateMaze());;
        this.solveSearchProblemServer = new Server(5401, 1000, new ServerStrategySolveSearchProblem());
        this.maze = null;
        this.mazeSolution = null;
        logger.info("Player started a game.");
    }

    public void assignObserver(Observer o)
    {
        this.addObserver(o);
    }

    public void operateGeneratorClient(int row, int col)
    {
        try {
            this.client = new Client(InetAddress.getLocalHost(), 5400, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {

                        logger.info("Player generated a maze");
                        logger.info("Maze size: " + row + ", " + col);

                        mazeGeneratingServer.setProprties("MazeGenerator", mazeGenProp.getValue());
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{row, col};
                        toServer.writeObject(mazeDimensions); //send maze dimensions to server
                        toServer.flush();
                        byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[(row*2-1)*(col*2-1)+25 /*CHANGE SIZE ACCORDING TO YOU MAZE SIZE*/]; //allocating byte[] for the decompressed maze -
                        is.read(decompressedMaze); //Fill decompressedMaze with bytes
                        maze = new Maze(decompressedMaze);
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

    public void CommunicateWithServer_SolveSearchProblem() {
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, new IClientStrategy() {
                @Override
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        logger.info("Player asked server to solve the maze");
                        solveSearchProblemServer.setProprties("SearchingAlgorithm", solveAlgoProp.getValue());
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        MyMazeGenerator mg = new MyMazeGenerator();
                        //maze.print();
                        toServer.writeObject(maze); //send maze to server
                        toServer.flush();
                        mazeSolution = (Solution) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                        logger.info("Maze Is Solved");
                        changeSolutionToIntArr();

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


    public void changeSolutionToIntArr()
    {
        mazeToSolve.clear();
        ArrayList<AState> sol = mazeSolution.getSolutionPath();

        for(int i=0; i < sol.size(); i++)
        {
            MazeState current = ((MazeState)sol.get(i));
            Position currPos = current.getSelf();
            mazeToSolve.add(currPos.getRowIndex());
            mazeToSolve.add(currPos.getColumnIndex());
        }
    }


    /**
     * GETTERS AND SETTERS
     */


    public Solution getMazeSolution() {
        return mazeSolution;
    }

    public ArrayList<Integer> getMazetosolve() {
        return mazeToSolve;
    }

    public Server getSolveSearchProblemServer() {
        return solveSearchProblemServer;
    }

    public void setSolveSearchProblemServer(Server solveSearchProblemServer) {
        this.solveSearchProblemServer = solveSearchProblemServer;
    }

    public Server getMazeGeneratingServer() {
        return mazeGeneratingServer;
    }

    public void setMazeGeneratingServer(Server mazeGeneratingServer) {
        this.mazeGeneratingServer = mazeGeneratingServer;
    }

    public AMazeGenerator getMazeGenerator() {
        return mazeGenerator;
    }

    public void setMazeGenerator(AMazeGenerator mazeGenerator) {
        this.mazeGenerator = mazeGenerator;
    }

    public ASearchingAlgorithm getSearchingAlgorithm() {
        return searchingAlgorithm;
    }

    public void setSearchingAlgorithm(ASearchingAlgorithm searchingAlgorithm) {
        this.searchingAlgorithm = searchingAlgorithm;
    }

    public Maze getMaze() {
        return maze;
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
    }

    public Solution getSolution() {
        return mazeSolution;
    }

    public void setSolution(Solution solution) {
        this.mazeSolution = solution;
    }

    public MyCompressorOutputStream getMyCompressorOutputStream() {
        return myCompressorOutputStream;
    }

    public void setMyCompressorOutputStream(MyCompressorOutputStream myCompressorOutputStream) {
        this.myCompressorOutputStream = myCompressorOutputStream;
    }

    public MyDecompressorInputStream getMyDecompressorInputStream() {
        return myDecompressorInputStream;
    }

    public void setMyDecompressorInputStream(MyDecompressorInputStream myDecompressorInputStream) {
        this.myDecompressorInputStream = myDecompressorInputStream;
    }

    public void setByteMaze(byte [] byteArr)
    {
        Maze maze = new Maze(byteArr);
        this.maze = maze;
    }

    public String getMazeGenProp() {
        return mazeGenProp.get();
    }

    public StringProperty mazeGenPropProperty() {
        return mazeGenProp;
    }

    public void setMazeGenProp(String mazeGenProp) {
        this.mazeGenProp.set(mazeGenProp);

    }

    public String getSolveAlgoProp() {
        return solveAlgoProp.get();
    }

    public StringProperty solveAlgoPropProperty() {
        return solveAlgoProp;
    }

    public void setSolveAlgoProp(String solveAlgoProp) {
        this.solveAlgoProp.set(solveAlgoProp);
    }
}
