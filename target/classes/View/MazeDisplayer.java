package View;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;

public class MazeDisplayer extends Canvas {

    private int [][] mazeData;
    private int row_player;
    private int col_player;
    private boolean ifSolve;
    private boolean isNewWall;
    private ArrayList<Integer> intDataArrList;
    private GraphicsContext graphicsContext;
    private Image wallImage;
    private Image playerImage;
    StringProperty imageFileNameStart = new SimpleStringProperty();
    StringProperty imageFileNamePlayer = new SimpleStringProperty();
    StringProperty imageFileNameGoal = new SimpleStringProperty();
    StringProperty imageFileNameSolution = new SimpleStringProperty();


    IntegerProperty playerRow;
    IntegerProperty playerCol;

    public MazeDisplayer()
    {
        row_player = 0;
        col_player = 0;
        mazeData = null;
        ifSolve = false;
        isNewWall = true;
        graphicsContext = getGraphicsContext2D();
        this.intDataArrList = new ArrayList<Integer>();
        playerRow = new SimpleIntegerProperty();
        playerCol = new SimpleIntegerProperty();
        wallImage = null;
    }

    public IntegerProperty playerColProperty() {
        return playerCol;
    }

    public IntegerProperty playerRowProperty() {
        return playerRow;
    }

    public void setPlayerRow(int playerRow) {
        this.playerRow.set(playerRow);
    }

    public void setPlayerPosition(int row, int col)
    {
        this.row_player = row;
        this.playerRow.setValue(row);
        this.col_player = col;
        this.playerCol.setValue(col);

        draw();
    }

    public void drawMaze(int [][] maze)
    {
        mazeData = maze;
        for(int i=0; i < mazeData.length; i++)
        {
            for(int j=0; j < mazeData[0].length; j++)
            {
                if(mazeData[i][j] == 83)
                {
                    row_player = i;
                    col_player = j;
                }
            }
        }
        draw();
    }

    public void draw()
    {
        if( mazeData !=null)
        {
            double canvasHeight = getHeight();
            double canvasWidth = getWidth();
            int row = mazeData.length;
            int col = mazeData[0].length;
            double cellHeight = canvasHeight/row;
            double cellWidth = canvasWidth/col;
            graphicsContext.clearRect(0,0,canvasWidth,canvasHeight);
            double w,h;
            int entryRow = 0, entryCol = 0, exitRow = 0, exitCol = 0;

            //Draw Maze
            try {
                if(isNewWall)
                {
                    isNewWall = false;
                    wallImage = new Image(new FileInputStream("resources\\" + RandomWall() + ".png"));
                }
            } catch (FileNotFoundException e) {
                System.out.println("There is no file....");
            }
            for(int i=0;i<row;i++)
            {
                for(int j=0;j<col;j++)
                {
                    h = i * cellHeight;
                    w = j * cellWidth;
                    if(mazeData[i][j] == 1) // Wall
                    {
                        if (wallImage == null && isNewWall)
                        {
                            graphicsContext.setFill(Color.BLUEVIOLET);
                            graphicsContext.fillRect(w,h,cellWidth,cellHeight);
                        }
                        else
                        {
                            graphicsContext.drawImage(wallImage,w,h,cellWidth,cellHeight);
                        }
                    }

                    else if(mazeData[i][j] == 83)
                    {
                        Image start = null;
                        try {
                            start = new Image(new FileInputStream(getImageFileNameStart()));
                        }catch(FileNotFoundException e){
                            e.printStackTrace();
                        }
                        entryRow = i;
                        entryCol = j;
                        graphicsContext.drawImage(start, w,h,cellWidth,cellHeight);
                        //graphicsContext.setFill(Color.GREEN);
                        //graphicsContext.fillRect(w,h,cellWidth,cellHeight);
                    }

                    else if(mazeData[i][j] == 69)
                    {
                        Image basket = null;
                        try {
                            basket = new Image(new FileInputStream(getImageFileNameGoal()));
                        }catch(FileNotFoundException e){
                            e.printStackTrace();
                        }
                        exitRow = i;
                        exitCol = j;
                        //graphicsContext.setFill(Color.RED);
                        graphicsContext.drawImage(basket, w,h,cellWidth,cellHeight);
                    }
                }
            }

            if(ifSolve)
            {
                int j;
                Image solutionPic = null;
                for (int i = 0; i < intDataArrList.size(); i += 2)
                {
                    if(i > 1 && i < intDataArrList.size() - 2 )
                    {
                        j = i + 1;
                        h = intDataArrList.get(i) * cellHeight;
                        w = intDataArrList.get(j) * cellWidth;


                        try {
                            solutionPic = new Image(new FileInputStream(getImageFileNameSolution()));
                        }catch(FileNotFoundException e){
                            e.printStackTrace();
                        }
                        graphicsContext.drawImage(solutionPic,w, h, cellWidth, cellHeight);
                        /*graphicsContext.setFill(Color.AQUAMARINE);
                        graphicsContext.fillRect(w, h, cellWidth, cellHeight);*/
                    }
                }

                //Paint entry again
                Image start = null;
                try {
                    start = new Image(new FileInputStream(getImageFileNameStart()));
                }catch(FileNotFoundException e){
                    e.printStackTrace();
                }
                //graphicsContext.setFill(Color.GREEN);
                graphicsContext.drawImage(start,entryCol*cellWidth,entryRow*cellHeight,cellWidth,cellHeight);

                //Paint exit again
                Image basket = null;
                try {
                    basket = new Image(new FileInputStream(getImageFileNameGoal()));
                }catch(FileNotFoundException e){
                    e.printStackTrace();
                }
                //graphicsContext.setFill(Color.RED);
                graphicsContext.drawImage(basket,exitCol*cellWidth,exitRow*cellHeight,cellWidth,cellHeight);
            }

            double h_player = row_player * cellHeight;
            double w_player =  col_player* cellWidth;
            Image playerImage = SelectPlayer();
            graphicsContext.drawImage(playerImage,w_player,h_player,cellWidth,cellHeight);
        }
    }

    private Image SelectPlayer()
    {
        String path = "";
        if(getImageFileNamePlayer().equals("Michael Jordan"))
        {
            path = "resources\\player2.png";
        }
        else if(getImageFileNamePlayer().equals("Donald Duck"))
        {
            path = "resources\\player1.png";
        }
        else if(getImageFileNamePlayer().equals("Lola Bunny"))
        {
            path = "resources\\player3.png";
        }

        try
        {
            playerImage = new Image(new FileInputStream(path));
        } catch (FileNotFoundException e)
        {
            System.out.println("There is no Image player....");
        }

        return playerImage;
    }


    public String RandomWall()
    {
        Random rand = new Random();
        String[] list = {"Bad1","Bad2","Bad3"};
        int index = rand.nextInt(3);
        return list[index];
    }



    public boolean makeACrossMove(int row, int col,String cross)
    {
        if((row < 0 || row > mazeData.length-1) || (col < 0 || col > mazeData[0].length-1))
        {
            return false;
        }

        if(cross.equals("3"))
        {
            if(MakeAMove(row + 2, col + 2))
            {
                if((MakeAMove(row+1,col) && MakeAMove(row + 2, col ) && MakeAMove(row + 2, col + 1 )) || (MakeAMove(row,col+1) && MakeAMove(row, col+2 ) && MakeAMove(row + 1, col + 2 )) )
                {
                    return true;
                }
            }
        }
        else if(cross.equals("7"))
        {
            if(MakeAMove(row - 2, col - 2))
            {
                if((MakeAMove(row,col-1) && MakeAMove(row, col-2 ) && MakeAMove(row - 1, col - 2 )) || (MakeAMove(row - 1,col) && MakeAMove(row-2, col ) && MakeAMove(row-2, col -1 )) )
                {
                    return true;
                }
            }

        }
        else if(cross.equals("1"))
        {
            if(MakeAMove(row +2, col - 2))
            {
                if((MakeAMove(row,col-1) && MakeAMove(row, col-2 ) && MakeAMove(row +1, col - 2 )) || (MakeAMove(row + 1,col) && MakeAMove(row+2, col ) && MakeAMove(row+2, col -1 )) )
                {
                    return true;
                }
            }
        }
        else if(cross.equals("9"))
        {
            if(MakeAMove(row -2, col + 2))
            {
                if((MakeAMove(row - 1,col) && MakeAMove(row-2, col ) && MakeAMove(row -2, col +1 )) || (MakeAMove(row,col+1) && MakeAMove(row,col+2 ) && MakeAMove(row-1, col +2 )) )
                {
                    return true;
                }
            }
        }
        return false;
    }


    public boolean MakeAMove(int row, int col)
    {
        if((row < 0 || row > mazeData.length-1) || (col < 0 || col > mazeData[0].length-1))
        {
            return false;
        }
        else if(mazeData[row][col] == 1)
        {
            return false;
        }

        return true;
    }


    public boolean isNewWall() {
        return isNewWall;
    }

    public void setNewWall(boolean newWall) {
        isNewWall = newWall;
    }

    public StringProperty imageFileNamePlayerProperty() {
        return imageFileNamePlayer;
    }

    public String getImageFileNameSolution() {
        return imageFileNameSolution.get();
    }

    public StringProperty imageFileNameSolutionProperty() {
        return imageFileNameSolution;
    }

    public void setImageFileNameSolution(String imageFileNameSolution) {
        this.imageFileNameSolution.set(imageFileNameSolution);
    }

    /**
     * GETTERS AND SETTERS
     */



    public ArrayList<Integer> getIntDataArrList() {
        return intDataArrList;
    }

    public void setIntDataArrList(ArrayList<Integer> intDataArrList) {
        this.intDataArrList = intDataArrList;
    }

    public boolean isIfSolve() {
        return ifSolve;
    }

    public void setIfSolve(boolean ifSolve) {
        this.ifSolve = ifSolve;
    }

    public int[][] getMazeData() {
        return mazeData;
    }

    public void setMazeData(int[][] mazeData) {
        this.mazeData = mazeData;
    }

    public int getRow_player() {
        return row_player;
    }

    public void setRow_player(int row_player) {
        this.row_player = row_player;
    }

    public int getCol_player() {
        return col_player;
    }

    public void setCol_player(int col_player) {
        this.col_player = col_player;
    }

    public String getImageFileNameStart() {
        return imageFileNameStart.get();
    }

    public void setImageFileNameStart(String imageFileNameWall) {
        this.imageFileNameStart.set(imageFileNameWall);
    }

    public String getImageFileNamePlayer() {
        return imageFileNamePlayer.get();
    }

    public void setImageFileNamePlayer(String imageFileNamePlayer) {
        this.imageFileNamePlayer.set(imageFileNamePlayer);
    }

    public String getImageFileNameGoal() {
        return imageFileNameGoal.get();
    }

    public StringProperty imageFileNameGoalProperty() {
        return imageFileNameGoal;
    }

    public void setImageFileNameGoal(String imageFileNameGoal) {
        this.imageFileNameGoal.set(imageFileNameGoal);
    }

    public GraphicsContext getGraphicsContext()
    {
        return  this.graphicsContext;
    }
}
