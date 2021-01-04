import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;

public class Life{
    private int health = 100;
    private double healthMAX = 100;
    private int x = 100;
    private int y = 100;
    private int width = Map.xSTEP;
    private int height = Map.ySTEP/10;

    private Rectangle rectangle;

    private Paint stroke = Color.BLACK;
    private Paint fill = Color.RED;
    public Life(){
        rectangle = new Rectangle(x,y,width,height);
        rectangle.setStroke(stroke);
        rectangle.setFill(fill);
    }

    public Life(int x,int y,int health){
        this.health=health;
        this.healthMAX=health;
        this.x=x;
        this.y=y;
        rectangle = new Rectangle(x,y,width,height);
        rectangle.setStroke(stroke);
        rectangle.setFill(fill);
    }

    public Life(int x,int y,int width,int height){
        this.x=x;
        this.y=y;
        this.width=width;
        this.height=height;
        rectangle = new Rectangle(x,y,width,height);
        rectangle.setStroke(stroke);
        rectangle.setFill(fill);
    }

    public Node getAppearence() {
        return rectangle;
    }

    public void update(int x,int y,int health)
    {
        if(health==0)
        {
            Platform.runLater(()->Map.pane.getChildren().remove(this.rectangle));
            return;
        }

        this.x=x;
        this.y=y;
        this.health=health;
        Platform.runLater(()->{
            rectangle.setX(x);
            rectangle.setY(y);
            rectangle.setWidth(health/healthMAX*width);
        });
    }
}
