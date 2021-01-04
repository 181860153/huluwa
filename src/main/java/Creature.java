import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Random;

public abstract class Creature implements Runnable {
    protected int seed = Map.seed;
    protected Random random = new Random(47);
    protected static final int xSTEP = Map.xSTEP;
    protected static final int ySTEP = Map.ySTEP;
    protected static final int xMAX = Map.xMAX;
    protected static final int yMAX = Map.yMAX;
    protected String name;
    protected int id;
    protected int team = 1;
    protected int health = 300;
    protected int speed = 15;
    protected int damage = 20;
    protected int shield = 0;
    protected int x = 0;
    protected int y = 0;
    protected int width = Map.xSTEP;
    protected int height = Map.ySTEP;
    protected Image image;
    protected ImageView imageView;
    protected Life life;
    protected boolean isAuto = true;

    private final boolean testMode = false;

    public Creature(String name, int id, int x, int y, String pic, int team) {
        this.name = name;
        this.id = id;
        this.x = x;
        this.y = y;
        this.team = team;
        image = new Image(pic);
        imageView = new ImageView(image);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        updateImage();
        Map.field[y][x] = this;
        random = new Random(Map.seed);
        life = new Life(x * xSTEP, y * ySTEP, health);
    }

    public Creature(String name, int id, int x, int y, String pic, int team, int health, int damage, int speed, int shield) {
        this.name = name;
        this.id = id;
        this.x = x;
        this.y = y;
        this.team = team;
        this.health=health;
        this.damage=damage;
        this.speed=speed;
        this.shield=shield;
        image = new Image(pic);
        imageView = new ImageView(image);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        updateImage();
        Map.field[y][x] = this;

        life = new Life(x * xSTEP, y * ySTEP, health);
    }

    public void updateImage() {
        Platform.runLater(() -> {
            imageView.setX(x * xSTEP);
            imageView.setY(y * ySTEP);
            life.update(x * xSTEP, y * ySTEP, health);
        });
    }

    public synchronized void moveLeft() {
        if (x > 0 && Map.field[y][x - 1] == null) {
            Map.field[y][x] = null;
            x--;
            Map.field[y][x] = this;
            updateImage();
        }

    }

    public synchronized void moveRight() {
        if (x < xMAX - 1 && Map.field[y][x + 1] == null) {
            Map.field[y][x] = null;
            x++;
            Map.field[y][x] = this;
            updateImage();
        }

    }

    public synchronized void moveUp() {
        if (y > 0 && Map.field[y - 1][x] == null) {
            Map.field[y][x] = null;
            y--;
            Map.field[y][x] = this;
            updateImage();
        }
    }

    public synchronized void moveDown() {
        if (y < yMAX - 1 && Map.field[y + 1][x] == null) {
            Map.field[y][x] = null;
            y++;
            Map.field[y][x] = this;
            updateImage();
        }
    }

    public void tryhurt(int hazard){
        ClientMain.client.send(y+" "+x+" "+"hurt"+" "+hazard);
    }

    public void hurt(int hazard) {
        if(testMode) return;
        health -= hazard;
        life.update(x * xSTEP, y * ySTEP, health);
        if (health <= 0) {
            if(team==1)
                Map.numTeam1--;
            else if(team==2)
                Map.numTeam2--;
            Platform.runLater(() -> Map.pane.getChildren().remove(this.imageView));
            Map.field[y][x] = null;
        }
    }

    public void trymove(String str)
    {
        switch (str){
            case "left":
                trymoveLeft();
                break;
            case "right":
                trymoveRight();
                break;
            case "up":
                trymoveUp();
                break;
            case "down":
                trymoveDown();
                break;
        }
    }

    public void trymoveLeft() {
        if (x > 0 && Map.field[y][x - 1] == null) {
            ClientMain.client.send(y+" "+x+" "+"left");
        }

    }

    public void trymoveRight() {
        if (x < xMAX - 1 && Map.field[y][x + 1] == null) {
            ClientMain.client.send(y+" "+x+" "+"right");
        }

    }

    public void trymoveUp() {
        if (y > 0 && Map.field[y - 1][x] == null) {
            ClientMain.client.send(y+" "+x+" "+"up");
        }
    }

    public void trymoveDown() {
        if (y < yMAX - 1 && Map.field[y + 1][x] == null) {
            ClientMain.client.send(y+" "+x+" "+"down");
        }
    }


    public int getId() {
        return id;
    }

    public Node getAppearance() {
        return imageView;
    }

    public int getTeam() {
        return team;
    }

    public Life getLife() {
        return life;
    }

    public void setAuto(boolean auto) {
        isAuto = auto;
    }

    abstract public void shoot();

    public int getSeed()
    {
        return seed;
    }

    public void setRandom(Random random) {
        this.random = random;
    }
}
