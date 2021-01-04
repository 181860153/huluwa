import javafx.application.Platform;

import java.util.Random;

public class Grandpa extends Creature implements Runnable{
    private Random random = new Random();

    public Grandpa(String name, int id, int x, int y) {
        super(name, id, x, y, "pa.png", 1, 300, 20, 15, 0);

    }

    public Grandpa(String name, int id, int x, int y, int health, int damage, int speed, int shield) {
        super(name, id, x, y, "pa.png", 1, health, damage, speed, shield);

    }

    @Override
    public void run() {
        while (health > 0) {
            if(Map.reloading)
            {
                int d = random.nextInt(10);
                //System.out.println("here");
                switch (d) {
                    case 0:
                        moveRight();
                        break;
                    case 1:
                        moveLeft();
                        break;
                    case 2:
                        moveDown();
                        break;
                    case 3:
                        moveUp();
                        break;
                }
                shoot();
                //System.out.println("shoot");
                try {
                    Thread.sleep(random.nextInt(500));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else if(!Map.OK || !Map.running)
                Thread.yield();
            else if(isAuto)
            {
                int d=0;
                try {
                    d = random.nextInt(10);
                }
                catch (Exception e)
                {
                    random = new Random(seed);
                }
                switch (d) {
                    case 0:
                        trymove("right");
                        break;
                    case 1:
                        trymove("left");
                        break;
                    case 2:
                        trymove("down");
                        break;
                    case 3:
                        trymove("up");
                        break;
                }
                shoot();
                try {
                    Thread.sleep(random.nextInt(500));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                shoot();
                try {
                    Thread.sleep(random.nextInt(1000));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public synchronized void shoot() {
        Bullet bullet = new Bullet("bullet.png");
        bullet.setDamage(damage);
        bullet.setDx(speed);
        bullet.setDy(0);
        bullet.setShooter(id);
        bullet.setShooterTeam(team);

        bullet.setX((x + 0.5) * xSTEP);
        bullet.setY((y + 0.5) * ySTEP);

        Platform.runLater(() -> Map.pane.getChildren().add(bullet.getImageView()));
        new Thread(bullet).start();
    }
}
