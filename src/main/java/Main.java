import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        int serverCnt = 0;
        int clientCnt = 0;
        while(true)
        {
            System.out.println("输入1启动服务器，输入2启动客户端。");
            Scanner scanner = new Scanner(System.in);
            int a = scanner.nextInt();
            if(a==1)
            {
                if(serverCnt>=1)
                    System.out.println("您已打开服务器！");
                else
                {
                    serverCnt++;
                    new Thread(()-> {
                        try {
                            Server.main(args);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }).start();
                    System.out.println("服务器已打开！");
                }
            }
            else if(a==2)
            {
                if(clientCnt >= 1)
                {
                    System.out.println("您已打开客户端！");
                }
                else
                {
                    clientCnt++;
                    new Thread(()->ClientMain.main(args)).start();
                }
                break;
            }
            else
                System.out.println("Unknown args");
        }
    }
}
