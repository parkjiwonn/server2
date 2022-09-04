import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class MainServer {

    public static ArrayList<PrintWriter> m_OutputList;


    public static void main(String[] args)
    {

        System.out.println("**************************************");
        System.out.println("*              채팅 서버                *");
        System.out.println("**************************************");
        System.out.println("클라이언트의 접속을 기다립니다.");
        m_OutputList = new ArrayList<PrintWriter>();

        try{
            ServerSocket s_socket = new ServerSocket(8888);
            while(true){
                Socket c_socket = s_socket.accept();
                ChatSverThread c_thread = new ChatSverThread();
                c_thread.setSocket(c_socket);
                m_OutputList.add(new PrintWriter(c_socket.getOutputStream()));
                System.out.println("클라이언트 접속 확인 완료");
                System.out.println(m_OutputList.size());
                c_thread.start();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }


}
