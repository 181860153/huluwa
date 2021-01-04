import javafx.application.Platform;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;

public class Map {
    public static final int xSTEP = 50;
    public static final int ySTEP = 50;
    public static final int xMAX = 10;
    public static final int yMAX = 10;
    public static final int width = xSTEP * xMAX;
    public static final int height = ySTEP * yMAX;

    static int seed = 10;
    static boolean OK = false;
    static Pane pane;
    static Creature[][] field;
    static ArrayList<Creature> creatures;
    static ArrayList<Rectangle> rectangles;
    static Creature myHulu;
    static Creature mySnake;
    static boolean running = false;
    static int numTeam1 = 0;
    static int numTeam2 = 0;
    static boolean reloading = false;

    static {
        field = new Creature[yMAX][xMAX];
        pane = new Pane();
        creatures = new ArrayList<>();
        rectangles = new ArrayList<>();
        for (int i = 0; i < yMAX; i++) {
            for (int j = 0; j < xMAX; j++) {
                Rectangle rectangle = new Rectangle(j * xSTEP, i * ySTEP, xSTEP, ySTEP);
                if ((i + j) % 2 == 0)
                    rectangle.setFill(Color.GRAY);
                else
                    rectangle.setFill(Color.WHITE);
                rectangles.add(rectangle);
            }
        }
    }


    public static void initPane() {
        pane.getChildren().addAll(rectangles);
        for (Creature h : creatures)
            pane.getChildren().add(h.getAppearance());
        for (Creature h : creatures)
            pane.getChildren().add(h.getLife().getAppearence());
    }

    public static void init() throws IOException, InterruptedException {
        Hulu h1 = new Hulu("1", 1, 0, 0);
        numTeam1++;
        h1.setAuto(false);
        myHulu = h1;
        creatures.add(h1);
        Hulu h2 = new Hulu("2", 2, 3, 3);
        numTeam1++;
        creatures.add(h2);
        Snake h3 = new Snake("3", 3, 5, 0);
        numTeam2++;
        creatures.add(h3);
        mySnake = h3;
        h3.setAuto(false);
        Snake h4 = new Snake("4", 4, 7, 3);
        numTeam2++;
        creatures.add(h4);
        Grandpa h5 = new Grandpa("5", 5, 5, 3);
        numTeam1++;
        creatures.add(h5);

        Map.initPane();
        Map.setKey();
        Map.start();

        /*new Thread(()-> {
            try {
                Map.reload();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();*/


    }

    public static void reload() throws IOException, InterruptedException {
        reloading = true;
        OK = true;
        running = true;


        Random random = new Random(seed);
        for (Creature h : creatures)
            h.setRandom(new Random(random.nextInt()));
        String path = "log.txt";
        try{
            FileInputStream fileInputStream = new FileInputStream(path);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line = null;
            int t = 0;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("Random")) {
                    seed = Integer.parseInt(line.substring(6));
                }
                else if(reloading)
                {
                    //System.out.println(line);
                    String[] s = line.split(" ");

                    int nowT = Integer.parseInt(s[0]);
                    if (nowT > t)
                        Thread.sleep(nowT - t);
                    t = nowT;
                    if (s.length == 5) {
                        int number = Integer.parseInt(s[0]);
                        int y = Integer.parseInt(s[1]);
                        int x = Integer.parseInt(s[2]);
                        String dir = s[3];
                        if (!dir.equals("hurt"))
                            continue;
                        int hazard = Integer.parseInt(s[4]);
                        Creature h = Map.field[y][x];
                        //System.out.println("y" + y + "x" + x);
                        if (h == null) continue;
                        h.hurt(hazard);
                    } else if (s.length == 4) {
                        int number = Integer.parseInt(s[0]);
                        int y = Integer.parseInt(s[1]);
                        int x = Integer.parseInt(s[2]);
                        String dir = s[3];
                        Creature h = Map.field[y][x];
                        //System.out.println("y" + y + "x" + x);
                        if (h == null) continue;
                        //else System.out.println("here");
                        //if(h.getTeam()!=number) return;
                        switch (dir) {
                            case "down":
                                h.moveDown();
                                break;
                            case "up":
                                h.moveUp();
                                break;
                            case "left":
                                h.moveLeft();
                                break;
                            case "right":
                                h.moveRight();
                                break;
                        /*case UP:
                            h2.moveUp();
                            break;
                        case DOWN:
                            h2.moveDown();
                            break;
                        case LEFT:
                            h2.moveLeft();
                            break;
                        case RIGHT:
                            h2.moveRight();
                            break;*/

                            case "J":
                                h.shoot();
                                break;
                        /*case K:
                            h2.shoot();
                            break;*/
                            default:
                                //System.out.println(dir);
                        }
                    }
                    else
                        break;
                }

            }

            fileInputStream.close();
        }
        catch (Exception e)
        {
            path = null;
        }
    }

    public static void response(String str) {
        //System.out.println(str);
        String[] s = str.split(" ");
        try {
            if (s[s.length - 1].equals("OK")) {
                OK = true;
               //System.out.println("ok = true");
            } else if (s[s.length - 1].equals("Space")) {
                running = true;
                //System.out.println("running = true");
            } else if (str.startsWith("Random")) {
                seed = Integer.parseInt(str.substring(6));
            } else {
                if (s.length == 5) {
                    int number = Integer.parseInt(s[0]);
                    int y = Integer.parseInt(s[1]);
                    int x = Integer.parseInt(s[2]);
                    String dir = s[3];
                    if (!dir.equals("hurt"))
                        return;
                    int hazard = Integer.parseInt(s[4]);
                    Creature h = Map.field[y][x];
                    //System.out.println("y" + y + "x" + x);
                    if (h == null) return;
                    h.hurt(hazard);
                } else if (s.length == 4) {
                    int number = Integer.parseInt(s[0]);
                    int y = Integer.parseInt(s[1]);
                    int x = Integer.parseInt(s[2]);
                    String dir = s[3];
                    Creature h = Map.field[y][x];
                    //System.out.println("y" + y + "x" + x);
                    if (h == null) return;
                    //else System.out.println("here");
                    if (h.getTeam() != number) return;
                    switch (dir) {
                        case "down":
                            h.moveDown();
                            break;
                        case "up":
                            h.moveUp();
                            break;
                        case "left":
                            h.moveLeft();
                            break;
                        case "right":
                            h.moveRight();
                            break;
                        /*case UP:
                            h2.moveUp();
                            break;
                        case DOWN:
                            h2.moveDown();
                            break;
                        case LEFT:
                            h2.moveLeft();
                            break;
                        case RIGHT:
                            h2.moveRight();
                            break;*/

                        case "J":
                            h.shoot();
                            break;
                        /*case K:
                            h2.shoot();
                            break;*/
                        default:
                            //System.out.println(dir);
                    }
                }
            }
        } catch (Throwable e) {
            //e.printStackTrace();
        }

    }

    public static void setKey() {
        Creature h = myHulu;
        h.setAuto(false);
        Creature h2 = mySnake;
        pane.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case L:
                    new Thread(() -> {

                        Platform.runLater(()->{
                            FileChooser fileChooser = new FileChooser();
                            File file = fileChooser.showOpenDialog(ClientMain.thisstage);
                            if (file == null || !(file.getName().equals("log.txt"))) {
                                System.out.println("cannot open this file");
                                System.exit(1);
                            }
                            try {
                                Map.reload();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        });



                    }).start();
                    break;
                case S:
                case DOWN:
                    h.trymoveDown();
                    h2.trymoveDown();
                    break;
                case W:
                case UP:
                    h.trymoveUp();
                    h2.trymoveUp();
                    break;
                case A:
                case LEFT:
                    h.trymoveLeft();
                    h2.trymoveLeft();
                    break;
                case D:
                case RIGHT:
                    h.trymoveRight();
                    h2.trymoveRight();
                    break;
             /*case UP:
                    h2.moveUp();
                    break;
                case DOWN:
                    h2.moveDown();
                    break;
                case LEFT:
                    h2.moveLeft();
                    break;
                case RIGHT:
                    h2.moveRight();
                    break;

             case J:
                    h.shoot();
                    break;
             case K:
                    h2.shoot();
                    break;*/

            }
            ClientMain.client.send(event.getCode().getName());
        });
    }


    public static void start() {
        for (Creature h : creatures)
            new Thread(h).start();

        new Thread(() -> {
            try {
                ClientMain.client.listen();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            while (true) {
                if (numTeam1 <= 0 || numTeam2 <= 0) {
                    running = false;
                    reloading = false;
                }
                Thread.yield();
            }
        }).start();

        new Thread(() -> {
            while (true) {
                if (myHulu.health <= 0) {
                    for (Creature creature : creatures) {
                        if ((creature instanceof Hulu || creature instanceof Grandpa) && creature.health > 0) {
                            myHulu = creature;
                        }
                    }
                    if (myHulu != null)
                        setKey();
                }
                Thread.yield();
            }
        }).start();

        new Thread(() -> {
            while (true) {
                if (mySnake.health <= 0) {
                    for (Creature creature : creatures) {
                        if ((creature instanceof Snake) && creature.health > 0) {
                            mySnake =  creature;
                        }
                    }
                    if (myHulu != null)
                        setKey();
                }
                Thread.yield();
            }
        }).start();
    }

    public static ArrayList<Rectangle> getRectangles() {
        return rectangles;
    }
}
