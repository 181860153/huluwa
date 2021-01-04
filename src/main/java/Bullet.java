import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Bullet implements Runnable {
    private final int width = 10;
    private final int height = 10;
    private int damage;
    private double x;
    private double y;
    private int dx;
    private int dy;

    private Image image;
    private ImageView imageView;

    private int shooter;
    private int shooterTeam;

    public Bullet(String pic) {
        image = new Image(pic);
        imageView = new ImageView(image);
        imageView.setFitHeight(height);
        imageView.setFitWidth(width);
    }

    @Override
    public void run() {

        while (x < Map.width && y < Map.height) {
            while(!Map.OK || !Map.running)
                Thread.yield();
            x += dx;
            y += dy;
            Platform.runLater(() -> {
                imageView.setX(x);
                imageView.setY(y);
            });
            if(Map.reloading)
            {
                if(realShot())
                    break;
            }
            else if (checkShot())
                break;

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        Platform.runLater(() -> Map.pane.getChildren().remove(imageView));

    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setShooter(int i) {
        this.shooter = i;
    }

    public void setShooterTeam(int i) {
        this.shooterTeam = i;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public synchronized boolean checkShot() {
        int px = (int) (x / Map.xSTEP);
        int py = (int) (y / Map.ySTEP);

        try {
            if (px < 0 || py < 0 || px >= Map.xMAX || py >= Map.yMAX) {
                return false;
            } else if (Map.field[py][px] == null || shooterTeam == Map.field[py][px].getTeam()) {
                return false;
            } else {
                Map.field[py][px].tryhurt(damage);
                return true;
            }
        } catch (Exception e) {
            //System.out.println(px + " " + py);
            e.printStackTrace();
        }
        return true;
    }

    public synchronized boolean realShot() {
        int px = (int) (x / Map.xSTEP);
        int py = (int) (y / Map.ySTEP);

        try {
            if (px < 0 || py < 0 || px >= Map.xMAX || py >= Map.yMAX) {
                return false;
            } else if (Map.field[py][px] == null || shooterTeam == Map.field[py][px].getTeam()) {
                return false;
            } else {
                Map.field[py][px].hurt(damage);
                return true;
            }
        } catch (Exception e) {
            //System.out.println(px + " " + py);
            e.printStackTrace();
        }
        return true;
    }
}
