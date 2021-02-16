package View;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public interface IView
{
    void menuAction(ActionEvent menuKind);
    void mouseEvents(MouseEvent mouseEvent);
    void addMouseScrolling(Node node);
    void closeProgram();
    void keyPressed(KeyEvent keyEvent);
    void keyRelease(KeyEvent keyEvent);
    void generateAndSolveListeners(ActionEvent event);
    void playMusic();

}
