package ViewModel;


import Model.MyModel;
import View.MazeDisplayer;
import javafx.beans.property.*;
import javafx.scene.input.KeyEvent;

import java.io.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Properties;

public class MyViewModel extends Observable implements Observer {

    private MyModel model;
    private MazeDisplayer mazeDisplayer;
    private ArrayList<Integer> intDataArrList;
    private ArrayList<String> MazeNames;
    private String tempDirectoryPath = System.getProperty("java.io.tmpdir");
    private boolean isFirstGenerate;
    private boolean isFirstSolve;

    private int mazeExitRow;
    private int mazeExitCol;

    IntegerProperty playerRow;
    IntegerProperty playerCol;
    StringProperty mazeGenProp;
    StringProperty solveAlgoProp;
    StringProperty mazeNameToSave;
    StringProperty mazeToload;
    StringProperty PlayerPath;


    public MyViewModel(MazeDisplayer mazeDisplayer)
    {
        this.mazeDisplayer = mazeDisplayer;
        this.intDataArrList = new ArrayList<>();
        this.MazeNames = new ArrayList<String>();
        this.PlayerPath = new SimpleStringProperty();
        this.mazeNameToSave = new SimpleStringProperty();
        this.mazeToload = new SimpleStringProperty();
        this.mazeGenProp = new SimpleStringProperty();
        this.solveAlgoProp = new SimpleStringProperty();
        playerRow = new SimpleIntegerProperty();
        playerCol = new SimpleIntegerProperty();
        isFirstGenerate = true;
        isFirstSolve = true;
        playerRow.bind(mazeDisplayer.playerRowProperty());
        playerCol.bind(mazeDisplayer.playerColProperty());
        this.model = new MyModel();
        this.model.assignObserver(this);
        //reloadMazes();
    }

    @Override
    public void update(Observable o, Object arg)
    {

    }

    public void checkPlayerPosition()
    {
        if(playerRow.getValue() == mazeExitRow*2 && playerCol.get() == mazeExitCol*2)
        {
            setChanged();
            notifyObservers("finish line");
        }
    }

    public void assignObserver(Observer o)
    {
        this.addObserver(o);
    }

    public void createMaze(int row, int col)
    {
        model.mazeGenPropProperty().bind(mazeGenProp);
        if(isFirstGenerate)
        {
            isFirstGenerate = false;
            model.getMazeGeneratingServer().start();
        }
        model.operateGeneratorClient(row, col);
        mazeExitRow = model.getMaze().getExit().getRowIndex();
        mazeExitCol = model.getMaze().getExit().getColumnIndex();
        mazeDisplayer.setNewWall(true);
        mazeDisplayer.setIfSolve(false);
        mazeDisplayer.imageFileNamePlayerProperty().bindBidirectional(PlayerPath);
        mazeDisplayer.drawMaze(model.getMaze().getData());
        writeToPropertyfile(mazeGenProp);
        setChanged();
        notifyObservers("maze created");
    }

    public void solveMaze()
    {
        model.solveAlgoPropProperty().bind(solveAlgoProp);
        if(isFirstSolve)
        {
            isFirstSolve = false;
            model.getSolveSearchProblemServer().start();
        }
        model.CommunicateWithServer_SolveSearchProblem();
        convertPosToIntArray();
        mazeDisplayer.setIfSolve(true);
        mazeDisplayer.drawMaze(model.getMaze().getData());
        writeToPropertyfile(solveAlgoProp);
    }

    private void writeToPropertyfile(StringProperty stringProperty)
    {
        try (OutputStream output = new FileOutputStream("resources\\config.properties")) {

            Properties prop = new Properties();

            prop.setProperty("ThreadPoolSize", "3");
            prop.setProperty("AMazeGenerator", mazeGenProp.getValue());
            prop.setProperty("ASearchingAlgorithm", solveAlgoProp.getValue());

            // save properties to project root folder
            prop.store(output, null);
        }
        catch(FileNotFoundException e)
        {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void stopServers()
    {
        if(!isFirstGenerate)
            this.model.getMazeGeneratingServer().stop();
        if(!isFirstSolve)
            model.getSolveSearchProblemServer().stop();
    }

    public void convertPosToIntArray()
    {
        intDataArrList.clear();
        ArrayList<Integer> sol = this.model.getMazetosolve();

         for(int i = 0; i < sol.size()-2; i = i + 2)
         {
            int currentRow =  sol.get(i);
            int currentCol = sol.get(i+1);
            int nextRow = sol.get(i+2);
            int nextCol = sol.get(i+3);

             intDataArrList.add(sol.get(i)*2);
             intDataArrList.add(sol.get((i+1))*2);

            if(currentRow != nextRow && currentCol != nextCol)//Diagonal
            {
                if((currentRow + 1 == nextRow) && (currentCol + 1 == nextCol))//RIGHT DOWN
                {
                    intDataArrList.add(currentRow*2 + 1);
                    intDataArrList.add(currentCol*2 + 1);
                }

                else if((currentRow + 1 == nextRow) && (currentCol - 1 == nextCol))//RIGHT UP
                {
                    intDataArrList.add(currentRow*2 + 1);
                    intDataArrList.add(currentCol*2 - 1);
                }

                else if((currentRow - 1 == nextRow) && (currentCol + 1 == nextCol))//LEFT UP
                {
                    intDataArrList.add(currentRow*2 - 1);
                    intDataArrList.add(currentCol*2 + 1);
                }

                else if((currentRow - 1 == nextRow) && (currentCol - 1 == nextCol))//LEFT DOWN
                {
                    intDataArrList.add(currentRow*2 - 1);
                    intDataArrList.add(currentCol*2 - 1);
                }
            }

            else if(currentRow != nextRow)
            {
                if(currentRow - 1 == nextRow)//UP
                {
                    intDataArrList.add(currentRow * 2 - 1);
                    intDataArrList.add(currentCol * 2);
                }

                else if(currentRow + 1 == nextRow)//DOWN
                {
                    intDataArrList.add(currentRow * 2 + 1);
                    intDataArrList.add(currentCol * 2);
                }
            }

            else if(currentCol != nextCol)
            {

                if(currentCol - 1 == nextCol)//LEFT
                {
                    intDataArrList.add(currentRow * 2);
                    intDataArrList.add(currentCol * 2 - 1);
                }

                else if(currentCol + 1 == nextCol)//RIGHT
                {
                    intDataArrList.add(currentRow * 2);
                    intDataArrList.add(currentCol * 2 + 1);
                }
            }
         }
         this.intDataArrList.add((sol.get(sol.size() - 2))*2);
         this.intDataArrList.add((sol.get(sol.size() - 1))*2);

         mazeDisplayer.setIntDataArrList(this.intDataArrList);
    }

    /*public void reloadMazes()
    {
        File folder = new File(tempDirectoryPath);
        File[] listofFiles = folder.listFiles();
            for(int i=0; i < listofFiles.length;i++)
            {
                if(listofFiles[i].getName().length() >= 8)
                {
                    System.out.println(listofFiles[i].getName().substring(listofFiles[i].getName().length() - 8));
                    if ((listofFiles[i].getName().substring(listofFiles[i].getName().length() - 8).equals(".90sMaze"))) {
                        MazeNames.add(listofFiles[i].getName().substring(0,listofFiles[i].getName().length() - 8 ));
                    }
                }
            }
    }*/

    public void saveMaze()
    {
        File MazeFile = new File(tempDirectoryPath + "\\" + getMazeNameToSave() + ".90sMaze");
        try
        {
            FileOutputStream fileOutForMaze = new FileOutputStream(MazeFile);
            ObjectOutputStream objectMazeOut = new ObjectOutputStream(fileOutForMaze);
            if(model.getMaze() != null)
            {
                if(!MazeNames.contains(getMazeNameToSave()))
                {
                    this.MazeNames.add(getMazeNameToSave());
                    objectMazeOut.writeObject(model.getMaze().toByteArray());
                    objectMazeOut.close();
                    setChanged();
                    notifyObservers("save completed");
                }
                else
                {
                    setChanged();
                    notifyObservers("Name already exists");
                }
            }

            //File folder = new File(tempDirectoryPath);
            //File[] listofFiles = folder.listFiles();
            /*for(int i=0; i < listofFiles.length;i++)
            {
                System.out.println(listofFiles[i].getName());
            }*/
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void uploadMaze()
    {
        FileInputStream SolutionInputFile = null;
        try {
            SolutionInputFile = new FileInputStream(tempDirectoryPath + "\\"  + mazeToload.getValue() + ".90sMaze");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ObjectInputStream Mazeobject = null;
        try {
            Mazeobject = new ObjectInputStream(SolutionInputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            byte [] mazeBytes = (byte[]) Mazeobject.readObject();

            this.model.setByteMaze(mazeBytes);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        mazeDisplayer.setIfSolve(false);
        mazeExitRow = model.getMaze().getExit().getRowIndex();
        mazeExitCol = model.getMaze().getExit().getColumnIndex();
        mazeDisplayer.drawMaze(model.getMaze().getData());
        //mazeDisplayer.draw();
    }


    public void MoveThePlayer(KeyEvent keyEvent)
    {
        int player_row_position = mazeDisplayer.getRow_player();
        int player_col_position = mazeDisplayer.getCol_player();

        switch (keyEvent.getText()) {
            case "8":
                if(mazeDisplayer.MakeAMove(player_row_position - 1, player_col_position))
                    mazeDisplayer.setPlayerPosition(player_row_position - 1, player_col_position);
                break;
            case "2":
                if(mazeDisplayer.MakeAMove(player_row_position + 1, player_col_position))
                    mazeDisplayer.setPlayerPosition(player_row_position + 1, player_col_position);
                break;
            case "6":
                if(mazeDisplayer.MakeAMove(player_row_position, player_col_position + 1))
                    mazeDisplayer.setPlayerPosition(player_row_position, player_col_position + 1);
                break;
            case "4":
                if(mazeDisplayer.MakeAMove(player_row_position , player_col_position - 1))
                    mazeDisplayer.setPlayerPosition(player_row_position, player_col_position - 1);
                break;

            case "3":
                if(mazeDisplayer.makeACrossMove(player_row_position , player_col_position,"3"))
                    mazeDisplayer.setPlayerPosition(player_row_position + 2, player_col_position + 2);
                break;

            case "7":
                if(mazeDisplayer.makeACrossMove(player_row_position , player_col_position,"7"))
                    mazeDisplayer.setPlayerPosition(player_row_position - 2, player_col_position -2);
                break;

            case "1":
                if(mazeDisplayer.makeACrossMove(player_row_position , player_col_position,"1"))
                    mazeDisplayer.setPlayerPosition(player_row_position + 2, player_col_position -2);
                break;

            case "9":
                if(mazeDisplayer.makeACrossMove(player_row_position , player_col_position,"9"))
                    mazeDisplayer.setPlayerPosition(player_row_position - 2, player_col_position + 2);
                break;

            default:
                mazeDisplayer.setPlayerPosition(player_row_position, player_col_position);

        }

        keyEvent.consume();
        checkPlayerPosition();
    }


    /**
     * GETTERS OR SETTERS
     */

    public String getPlayerPath() {
        return PlayerPath.get();
    }

    public StringProperty playerPathProperty() {
        return PlayerPath;
    }

    public void setPlayerPath(String playerPath) {
        this.PlayerPath.set(playerPath);
    }

    public MyModel getModel() {
        return model;
    }
    public void setModel(MyModel model) {
        this.model = model;
    }
    public ArrayList<Integer> getIntDataArrList() {
        return intDataArrList;
    }

    public String getMazeNameToSave() {
        return mazeNameToSave.get();
    }

    public StringProperty mazeNameToSaveProperty() {
        return mazeNameToSave;
    }

    public void setMazeNameToSave(String mazeNameToSave) {
        this.mazeNameToSave.set(mazeNameToSave);
    }

    public ArrayList<String> getMazeNames() {
        return MazeNames;
    }

    public String getMazeToloadProperty() {
        return mazeToload.get();
    }

    public StringProperty mazeToloadProperty() {
        return mazeToload;
    }

    public void setMazeToloadProperty(String mazeToloadProperty) {
        this.mazeToload.set(mazeToloadProperty);
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

